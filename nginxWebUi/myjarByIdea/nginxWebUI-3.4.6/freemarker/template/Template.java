package freemarker.template;

import freemarker.core.BugException;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.core.FMParser;
import freemarker.core.LibraryLoad;
import freemarker.core.Macro;
import freemarker.core.OutputFormat;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.TemplateElement;
import freemarker.core.TextBlock;
import freemarker.core.TokenMgrError;
import freemarker.core._CoreAPI;
import freemarker.debug.impl.DebuggerService;
import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.tree.TreePath;

public class Template extends Configurable {
   public static final String DEFAULT_NAMESPACE_PREFIX = "D";
   public static final String NO_NS_PREFIX = "N";
   private static final int READER_BUFFER_SIZE = 4096;
   private Map macros;
   private List imports;
   private TemplateElement rootElement;
   private String encoding;
   private String defaultNS;
   private Object customLookupCondition;
   private int interpolationSyntax;
   private int actualTagSyntax;
   private int actualNamingConvention;
   private boolean autoEscaping;
   private OutputFormat outputFormat;
   private final String name;
   private final String sourceName;
   private final ArrayList lines;
   private final ParserConfiguration parserConfiguration;
   private Map prefixToNamespaceURILookup;
   private Map namespaceURIToPrefixLookup;
   private Version templateLanguageVersion;

   private Template(String name, String sourceName, Configuration cfg, ParserConfiguration customParserConfiguration) {
      super((Configurable)toNonNull(cfg));
      this.macros = new HashMap();
      this.imports = new Vector();
      this.lines = new ArrayList();
      this.prefixToNamespaceURILookup = new HashMap();
      this.namespaceURIToPrefixLookup = new HashMap();
      this.name = name;
      this.sourceName = sourceName;
      this.templateLanguageVersion = normalizeTemplateLanguageVersion(toNonNull(cfg).getIncompatibleImprovements());
      this.parserConfiguration = (ParserConfiguration)(customParserConfiguration != null ? customParserConfiguration : this.getConfiguration());
   }

   private static Configuration toNonNull(Configuration cfg) {
      return cfg != null ? cfg : Configuration.getDefaultConfiguration();
   }

   public Template(String name, Reader reader, Configuration cfg) throws IOException {
      this(name, (String)null, (Reader)reader, (Configuration)cfg);
   }

   public Template(String name, String sourceCode, Configuration cfg) throws IOException {
      this(name, (Reader)(new StringReader(sourceCode)), cfg);
   }

   public Template(String name, Reader reader, Configuration cfg, String encoding) throws IOException {
      this(name, (String)null, reader, cfg, encoding);
   }

   public Template(String name, String sourceName, Reader reader, Configuration cfg) throws IOException {
      this(name, sourceName, reader, cfg, (String)null);
   }

   public Template(String name, String sourceName, Reader reader, Configuration cfg, String encoding) throws IOException {
      this(name, sourceName, reader, cfg, (ParserConfiguration)null, encoding);
   }

   public Template(String name, String sourceName, Reader reader, Configuration cfg, ParserConfiguration customParserConfiguration, String encoding) throws IOException {
      this(name, sourceName, cfg, customParserConfiguration);
      this.setEncoding(encoding);

      LineTableBuilder ltbReader;
      try {
         ParserConfiguration actualParserConfiguration = this.getParserConfiguration();
         if (!(reader instanceof BufferedReader) && !(reader instanceof StringReader)) {
            reader = new BufferedReader((Reader)reader, 4096);
         }

         ltbReader = new LineTableBuilder((Reader)reader, actualParserConfiguration);
         reader = ltbReader;

         try {
            FMParser parser = new FMParser(this, (Reader)reader, actualParserConfiguration);
            if (cfg != null) {
               _CoreAPI.setPreventStrippings(parser, cfg.getPreventStrippings());
            }

            try {
               this.rootElement = parser.Root();
            } catch (IndexOutOfBoundsException var16) {
               if (!ltbReader.hasFailure()) {
                  throw var16;
               }

               this.rootElement = null;
            }

            this.actualTagSyntax = parser._getLastTagSyntax();
            this.interpolationSyntax = actualParserConfiguration.getInterpolationSyntax();
            this.actualNamingConvention = parser._getLastNamingConvention();
         } catch (TokenMgrError var17) {
            throw var17.toParseException(this);
         }
      } catch (ParseException var18) {
         var18.setTemplateName(this.getSourceName());
         throw var18;
      } finally {
         ((Reader)reader).close();
      }

      ltbReader.throwFailure();
      DebuggerService.registerTemplate(this);
      this.namespaceURIToPrefixLookup = Collections.unmodifiableMap(this.namespaceURIToPrefixLookup);
      this.prefixToNamespaceURILookup = Collections.unmodifiableMap(this.prefixToNamespaceURILookup);
   }

   /** @deprecated */
   @Deprecated
   public Template(String name, Reader reader) throws IOException {
      this(name, (Reader)reader, (Configuration)null);
   }

   /** @deprecated */
   @Deprecated
   Template(String name, TemplateElement root, Configuration cfg) {
      this(name, (String)null, (Configuration)cfg, (ParserConfiguration)((ParserConfiguration)null));
      this.rootElement = root;
      DebuggerService.registerTemplate(this);
   }

   public static Template getPlainTextTemplate(String name, String content, Configuration config) {
      return getPlainTextTemplate(name, (String)null, content, config);
   }

   public static Template getPlainTextTemplate(String name, String sourceName, String content, Configuration config) {
      Template template;
      try {
         template = new Template(name, sourceName, new StringReader("X"), config);
      } catch (IOException var6) {
         throw new BugException("Plain text template creation failed", var6);
      }

      _CoreAPI.replaceText((TextBlock)template.rootElement, content);
      DebuggerService.registerTemplate(template);
      return template;
   }

   private static Version normalizeTemplateLanguageVersion(Version incompatibleImprovements) {
      _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
      int v = incompatibleImprovements.intValue();
      if (v < _TemplateAPI.VERSION_INT_2_3_19) {
         return Configuration.VERSION_2_3_0;
      } else {
         return v > _TemplateAPI.VERSION_INT_2_3_21 ? Configuration.VERSION_2_3_21 : incompatibleImprovements;
      }
   }

   public void process(Object dataModel, Writer out) throws TemplateException, IOException {
      this.createProcessingEnvironment(dataModel, out, (ObjectWrapper)null).process();
   }

   public void process(Object dataModel, Writer out, ObjectWrapper wrapper, TemplateNodeModel rootNode) throws TemplateException, IOException {
      Environment env = this.createProcessingEnvironment(dataModel, out, wrapper);
      if (rootNode != null) {
         env.setCurrentVisitorNode(rootNode);
      }

      env.process();
   }

   public void process(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
      this.createProcessingEnvironment(dataModel, out, wrapper).process();
   }

   public Environment createProcessingEnvironment(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
      Object dataModelHash;
      if (dataModel instanceof TemplateHashModel) {
         dataModelHash = (TemplateHashModel)dataModel;
      } else {
         if (wrapper == null) {
            wrapper = this.getObjectWrapper();
         }

         if (dataModel == null) {
            dataModelHash = new SimpleHash(wrapper);
         } else {
            TemplateModel wrappedDataModel = wrapper.wrap(dataModel);
            if (!(wrappedDataModel instanceof TemplateHashModel)) {
               if (wrappedDataModel == null) {
                  throw new IllegalArgumentException(wrapper.getClass().getName() + " converted " + dataModel.getClass().getName() + " to null.");
               }

               throw new IllegalArgumentException(wrapper.getClass().getName() + " didn't convert " + dataModel.getClass().getName() + " to a TemplateHashModel. Generally, you want to use a Map<String, Object> or a JavaBean as the root-map (aka. data-model) parameter. The Map key-s or JavaBean property names will be the variable names in the template.");
            }

            dataModelHash = (TemplateHashModel)wrappedDataModel;
         }
      }

      return new Environment(this, (TemplateHashModel)dataModelHash, out);
   }

   public Environment createProcessingEnvironment(Object dataModel, Writer out) throws TemplateException, IOException {
      return this.createProcessingEnvironment(dataModel, out, (ObjectWrapper)null);
   }

   public String toString() {
      StringWriter sw = new StringWriter();

      try {
         this.dump((Writer)sw);
      } catch (IOException var3) {
         throw new RuntimeException(var3.getMessage());
      }

      return sw.toString();
   }

   public String getName() {
      return this.name;
   }

   public String getSourceName() {
      return this.sourceName != null ? this.sourceName : this.getName();
   }

   public Configuration getConfiguration() {
      return (Configuration)this.getParent();
   }

   public ParserConfiguration getParserConfiguration() {
      return this.parserConfiguration;
   }

   Version getTemplateLanguageVersion() {
      return this.templateLanguageVersion;
   }

   /** @deprecated */
   @Deprecated
   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public String getEncoding() {
      return this.encoding;
   }

   public Object getCustomLookupCondition() {
      return this.customLookupCondition;
   }

   public void setCustomLookupCondition(Object customLookupCondition) {
      this.customLookupCondition = customLookupCondition;
   }

   public int getActualTagSyntax() {
      return this.actualTagSyntax;
   }

   public int getInterpolationSyntax() {
      return this.interpolationSyntax;
   }

   public int getActualNamingConvention() {
      return this.actualNamingConvention;
   }

   public OutputFormat getOutputFormat() {
      return this.outputFormat;
   }

   void setOutputFormat(OutputFormat outputFormat) {
      this.outputFormat = outputFormat;
   }

   public boolean getAutoEscaping() {
      return this.autoEscaping;
   }

   void setAutoEscaping(boolean autoEscaping) {
      this.autoEscaping = autoEscaping;
   }

   public void dump(PrintStream ps) {
      ps.print(this.rootElement.getCanonicalForm());
   }

   public void dump(Writer out) throws IOException {
      out.write(this.rootElement.getCanonicalForm());
   }

   /** @deprecated */
   @Deprecated
   public void addMacro(Macro macro) {
      this.macros.put(macro.getName(), macro);
   }

   /** @deprecated */
   @Deprecated
   public void addImport(LibraryLoad ll) {
      this.imports.add(ll);
   }

   public String getSource(int beginColumn, int beginLine, int endColumn, int endLine) {
      if (beginLine >= 1 && endLine >= 1) {
         --beginLine;
         --beginColumn;
         --endColumn;
         --endLine;
         StringBuilder buf = new StringBuilder();

         int i;
         for(i = beginLine; i <= endLine; ++i) {
            if (i < this.lines.size()) {
               buf.append(this.lines.get(i));
            }
         }

         i = this.lines.get(endLine).toString().length();
         int trailingCharsToDelete = i - endColumn - 1;
         buf.delete(0, beginColumn);
         buf.delete(buf.length() - trailingCharsToDelete, buf.length());
         return buf.toString();
      } else {
         return null;
      }
   }

   /** @deprecated */
   @Deprecated
   public TemplateElement getRootTreeNode() {
      return this.rootElement;
   }

   /** @deprecated */
   @Deprecated
   public Map getMacros() {
      return this.macros;
   }

   /** @deprecated */
   @Deprecated
   public List getImports() {
      return this.imports;
   }

   /** @deprecated */
   @Deprecated
   public void addPrefixNSMapping(String prefix, String nsURI) {
      if (nsURI.length() == 0) {
         throw new IllegalArgumentException("Cannot map empty string URI");
      } else if (prefix.length() == 0) {
         throw new IllegalArgumentException("Cannot map empty string prefix");
      } else if (prefix.equals("N")) {
         throw new IllegalArgumentException("The prefix: " + prefix + " cannot be registered, it's reserved for special internal use.");
      } else if (this.prefixToNamespaceURILookup.containsKey(prefix)) {
         throw new IllegalArgumentException("The prefix: '" + prefix + "' was repeated. This is illegal.");
      } else if (this.namespaceURIToPrefixLookup.containsKey(nsURI)) {
         throw new IllegalArgumentException("The namespace URI: " + nsURI + " cannot be mapped to 2 different prefixes.");
      } else {
         if (prefix.equals("D")) {
            this.defaultNS = nsURI;
         } else {
            this.prefixToNamespaceURILookup.put(prefix, nsURI);
            this.namespaceURIToPrefixLookup.put(nsURI, prefix);
         }

      }
   }

   public String getDefaultNS() {
      return this.defaultNS;
   }

   public String getNamespaceForPrefix(String prefix) {
      if (prefix.equals("")) {
         return this.defaultNS == null ? "" : this.defaultNS;
      } else {
         return (String)this.prefixToNamespaceURILookup.get(prefix);
      }
   }

   public String getPrefixForNamespace(String nsURI) {
      if (nsURI == null) {
         return null;
      } else if (nsURI.length() == 0) {
         return this.defaultNS == null ? "" : "N";
      } else {
         return nsURI.equals(this.defaultNS) ? "" : (String)this.namespaceURIToPrefixLookup.get(nsURI);
      }
   }

   public String getPrefixedName(String localName, String nsURI) {
      if (nsURI != null && nsURI.length() != 0) {
         if (nsURI.equals(this.defaultNS)) {
            return localName;
         } else {
            String prefix = this.getPrefixForNamespace(nsURI);
            return prefix == null ? null : prefix + ":" + localName;
         }
      } else {
         return this.defaultNS != null ? "N:" + localName : localName;
      }
   }

   /** @deprecated */
   @Deprecated
   public TreePath containingElements(int column, int line) {
      ArrayList elements = new ArrayList();

      TemplateElement elem;
      for(TemplateElement element = this.rootElement; element.contains(column, line); element = elem) {
         elements.add(element);
         Enumeration enumeration = element.children();

         do {
            if (!enumeration.hasMoreElements()) {
               return elements.isEmpty() ? null : new TreePath(elements.toArray());
            }

            elem = (TemplateElement)enumeration.nextElement();
         } while(!elem.contains(column, line));
      }

      return elements.isEmpty() ? null : new TreePath(elements.toArray());
   }

   public static class WrongEncodingException extends ParseException {
      private static final long serialVersionUID = 1L;
      /** @deprecated */
      @Deprecated
      public String specifiedEncoding;
      private final String constructorSpecifiedEncoding;

      /** @deprecated */
      @Deprecated
      public WrongEncodingException(String templateSpecifiedEncoding) {
         this(templateSpecifiedEncoding, (String)null);
      }

      public WrongEncodingException(String templateSpecifiedEncoding, String constructorSpecifiedEncoding) {
         this.specifiedEncoding = templateSpecifiedEncoding;
         this.constructorSpecifiedEncoding = constructorSpecifiedEncoding;
      }

      public String getMessage() {
         return "Encoding specified inside the template (" + this.specifiedEncoding + ") doesn't match the encoding specified for the Template constructor" + (this.constructorSpecifiedEncoding != null ? " (" + this.constructorSpecifiedEncoding + ")." : ".");
      }

      public String getTemplateSpecifiedEncoding() {
         return this.specifiedEncoding;
      }

      public String getConstructorSpecifiedEncoding() {
         return this.constructorSpecifiedEncoding;
      }
   }

   private class LineTableBuilder extends FilterReader {
      private final int tabSize;
      private final StringBuilder lineBuf = new StringBuilder();
      int lastChar;
      boolean closed;
      private Exception failure;

      LineTableBuilder(Reader r, ParserConfiguration parserConfiguration) {
         super(r);
         this.tabSize = parserConfiguration.getTabSize();
      }

      public boolean hasFailure() {
         return this.failure != null;
      }

      public void throwFailure() throws IOException {
         if (this.failure != null) {
            if (this.failure instanceof IOException) {
               throw (IOException)this.failure;
            } else if (this.failure instanceof RuntimeException) {
               throw (RuntimeException)this.failure;
            } else {
               throw new UndeclaredThrowableException(this.failure);
            }
         }
      }

      public int read() throws IOException {
         try {
            int c = this.in.read();
            this.handleChar(c);
            return c;
         } catch (Exception var2) {
            throw this.rememberException(var2);
         }
      }

      private IOException rememberException(Exception e) throws IOException {
         if (!this.closed) {
            this.failure = e;
         }

         if (e instanceof IOException) {
            return (IOException)e;
         } else if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
         } else {
            throw new UndeclaredThrowableException(e);
         }
      }

      public int read(char[] cbuf, int off, int len) throws IOException {
         try {
            int numchars = this.in.read(cbuf, off, len);

            for(int i = off; i < off + numchars; ++i) {
               char c = cbuf[i];
               this.handleChar(c);
            }

            return numchars;
         } catch (Exception var7) {
            throw this.rememberException(var7);
         }
      }

      public void close() throws IOException {
         if (this.lineBuf.length() > 0) {
            Template.this.lines.add(this.lineBuf.toString());
            this.lineBuf.setLength(0);
         }

         super.close();
         this.closed = true;
      }

      private void handleChar(int c) {
         int numSpaces;
         if (c != 10 && c != 13) {
            if (c == 9 && this.tabSize != 1) {
               numSpaces = this.tabSize - this.lineBuf.length() % this.tabSize;

               for(int i = 0; i < numSpaces; ++i) {
                  this.lineBuf.append(' ');
               }
            } else {
               this.lineBuf.append((char)c);
            }
         } else if (this.lastChar == 13 && c == 10) {
            numSpaces = Template.this.lines.size() - 1;
            String lastLine = (String)Template.this.lines.get(numSpaces);
            Template.this.lines.set(numSpaces, lastLine + '\n');
         } else {
            this.lineBuf.append((char)c);
            Template.this.lines.add(this.lineBuf.toString());
            this.lineBuf.setLength(0);
         }

         this.lastChar = c;
      }
   }
}
