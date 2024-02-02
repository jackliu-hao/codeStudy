/*      */ package freemarker.template;
/*      */ 
/*      */ import freemarker.core.BugException;
/*      */ import freemarker.core.Configurable;
/*      */ import freemarker.core.Environment;
/*      */ import freemarker.core.FMParser;
/*      */ import freemarker.core.LibraryLoad;
/*      */ import freemarker.core.Macro;
/*      */ import freemarker.core.OutputFormat;
/*      */ import freemarker.core.ParseException;
/*      */ import freemarker.core.ParserConfiguration;
/*      */ import freemarker.core.TemplateElement;
/*      */ import freemarker.core.TextBlock;
/*      */ import freemarker.core.TokenMgrError;
/*      */ import freemarker.core._CoreAPI;
/*      */ import freemarker.debug.impl.DebuggerService;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.FilterReader;
/*      */ import java.io.IOException;
/*      */ import java.io.PrintStream;
/*      */ import java.io.Reader;
/*      */ import java.io.StringReader;
/*      */ import java.io.StringWriter;
/*      */ import java.io.Writer;
/*      */ import java.lang.reflect.UndeclaredThrowableException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collections;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Vector;
/*      */ import javax.swing.tree.TreePath;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Template
/*      */   extends Configurable
/*      */ {
/*      */   public static final String DEFAULT_NAMESPACE_PREFIX = "D";
/*      */   public static final String NO_NS_PREFIX = "N";
/*      */   private static final int READER_BUFFER_SIZE = 4096;
/*   86 */   private Map macros = new HashMap<>();
/*   87 */   private List imports = new Vector();
/*      */   
/*      */   private TemplateElement rootElement;
/*      */   
/*      */   private String encoding;
/*      */   
/*      */   private String defaultNS;
/*      */   
/*      */   private Object customLookupCondition;
/*      */   
/*      */   private int interpolationSyntax;
/*   98 */   private final ArrayList lines = new ArrayList(); private int actualTagSyntax; private int actualNamingConvention; private boolean autoEscaping; private OutputFormat outputFormat; private final String name; private final String sourceName;
/*      */   private final ParserConfiguration parserConfiguration;
/*  100 */   private Map prefixToNamespaceURILookup = new HashMap<>();
/*  101 */   private Map namespaceURIToPrefixLookup = new HashMap<>();
/*      */ 
/*      */   
/*      */   private Version templateLanguageVersion;
/*      */ 
/*      */ 
/*      */   
/*      */   private Template(String name, String sourceName, Configuration cfg, ParserConfiguration customParserConfiguration) {
/*  109 */     super(toNonNull(cfg));
/*  110 */     this.name = name;
/*  111 */     this.sourceName = sourceName;
/*  112 */     this.templateLanguageVersion = normalizeTemplateLanguageVersion(toNonNull(cfg).getIncompatibleImprovements());
/*  113 */     this.parserConfiguration = (customParserConfiguration != null) ? customParserConfiguration : getConfiguration();
/*      */   }
/*      */   
/*      */   private static Configuration toNonNull(Configuration cfg) {
/*  117 */     return (cfg != null) ? cfg : Configuration.getDefaultConfiguration();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template(String name, Reader reader, Configuration cfg) throws IOException {
/*  124 */     this(name, (String)null, reader, cfg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template(String name, String sourceCode, Configuration cfg) throws IOException {
/*  134 */     this(name, new StringReader(sourceCode), cfg);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template(String name, Reader reader, Configuration cfg, String encoding) throws IOException {
/*  142 */     this(name, (String)null, reader, cfg, encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template(String name, String sourceName, Reader reader, Configuration cfg) throws IOException {
/*  181 */     this(name, sourceName, reader, cfg, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template(String name, String sourceName, Reader reader, Configuration cfg, String encoding) throws IOException {
/*  202 */     this(name, sourceName, reader, cfg, (ParserConfiguration)null, encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Template(String name, String sourceName, Reader reader, Configuration cfg, ParserConfiguration customParserConfiguration, String encoding) throws IOException {
/*  233 */     this(name, sourceName, cfg, customParserConfiguration);
/*      */     LineTableBuilder ltbReader;
/*  235 */     setEncoding(encoding);
/*      */     
/*      */     try {
/*  238 */       ParserConfiguration actualParserConfiguration = getParserConfiguration();
/*      */       
/*  240 */       if (!(reader instanceof BufferedReader) && !(reader instanceof StringReader)) {
/*  241 */         reader = new BufferedReader(reader, 4096);
/*      */       }
/*  243 */       ltbReader = new LineTableBuilder(reader, actualParserConfiguration);
/*  244 */       reader = ltbReader;
/*      */       
/*      */       try {
/*  247 */         FMParser parser = new FMParser(this, reader, actualParserConfiguration);
/*  248 */         if (cfg != null) {
/*  249 */           _CoreAPI.setPreventStrippings(parser, cfg.getPreventStrippings());
/*      */         }
/*      */         try {
/*  252 */           this.rootElement = parser.Root();
/*  253 */         } catch (IndexOutOfBoundsException exc) {
/*      */ 
/*      */ 
/*      */           
/*  257 */           if (!ltbReader.hasFailure()) {
/*  258 */             throw exc;
/*      */           }
/*  260 */           this.rootElement = null;
/*      */         } 
/*  262 */         this.actualTagSyntax = parser._getLastTagSyntax();
/*  263 */         this.interpolationSyntax = actualParserConfiguration.getInterpolationSyntax();
/*  264 */         this.actualNamingConvention = parser._getLastNamingConvention();
/*  265 */       } catch (TokenMgrError exc) {
/*      */ 
/*      */         
/*  268 */         throw exc.toParseException(this);
/*      */       } 
/*  270 */     } catch (ParseException e) {
/*  271 */       e.setTemplateName(getSourceName());
/*  272 */       throw e;
/*      */     } finally {
/*  274 */       reader.close();
/*      */     } 
/*      */ 
/*      */     
/*  278 */     ltbReader.throwFailure();
/*      */     
/*  280 */     DebuggerService.registerTemplate(this);
/*  281 */     this.namespaceURIToPrefixLookup = Collections.unmodifiableMap(this.namespaceURIToPrefixLookup);
/*  282 */     this.prefixToNamespaceURILookup = Collections.unmodifiableMap(this.prefixToNamespaceURILookup);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Template(String name, Reader reader) throws IOException {
/*  295 */     this(name, reader, (Configuration)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   Template(String name, TemplateElement root, Configuration cfg) {
/*  306 */     this(name, (String)null, cfg, (ParserConfiguration)null);
/*  307 */     this.rootElement = root;
/*  308 */     DebuggerService.registerTemplate(this);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Template getPlainTextTemplate(String name, String content, Configuration config) {
/*  316 */     return getPlainTextTemplate(name, (String)null, content, config);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Template getPlainTextTemplate(String name, String sourceName, String content, Configuration config) {
/*      */     Template template;
/*      */     try {
/*  336 */       template = new Template(name, sourceName, new StringReader("X"), config);
/*  337 */     } catch (IOException e) {
/*  338 */       throw new BugException("Plain text template creation failed", e);
/*      */     } 
/*  340 */     _CoreAPI.replaceText((TextBlock)template.rootElement, content);
/*  341 */     DebuggerService.registerTemplate(template);
/*  342 */     return template;
/*      */   }
/*      */   
/*      */   private static Version normalizeTemplateLanguageVersion(Version incompatibleImprovements) {
/*  346 */     _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
/*  347 */     int v = incompatibleImprovements.intValue();
/*  348 */     if (v < _TemplateAPI.VERSION_INT_2_3_19)
/*  349 */       return Configuration.VERSION_2_3_0; 
/*  350 */     if (v > _TemplateAPI.VERSION_INT_2_3_21) {
/*  351 */       return Configuration.VERSION_2_3_21;
/*      */     }
/*  353 */     return incompatibleImprovements;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void process(Object dataModel, Writer out) throws TemplateException, IOException {
/*  383 */     createProcessingEnvironment(dataModel, out, (ObjectWrapper)null).process();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void process(Object dataModel, Writer out, ObjectWrapper wrapper, TemplateNodeModel rootNode) throws TemplateException, IOException {
/*  399 */     Environment env = createProcessingEnvironment(dataModel, out, wrapper);
/*  400 */     if (rootNode != null) {
/*  401 */       env.setCurrentVisitorNode(rootNode);
/*      */     }
/*  403 */     env.process();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void process(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
/*  414 */     createProcessingEnvironment(dataModel, out, wrapper).process();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Environment createProcessingEnvironment(Object dataModel, Writer out, ObjectWrapper wrapper) throws TemplateException, IOException {
/*      */     TemplateHashModel dataModelHash;
/*  466 */     if (dataModel instanceof TemplateHashModel) {
/*  467 */       dataModelHash = (TemplateHashModel)dataModel;
/*      */     } else {
/*  469 */       if (wrapper == null) {
/*  470 */         wrapper = getObjectWrapper();
/*      */       }
/*      */       
/*  473 */       if (dataModel == null) {
/*  474 */         dataModelHash = new SimpleHash(wrapper);
/*      */       } else {
/*  476 */         TemplateModel wrappedDataModel = wrapper.wrap(dataModel);
/*  477 */         if (wrappedDataModel instanceof TemplateHashModel)
/*  478 */         { dataModelHash = (TemplateHashModel)wrappedDataModel; }
/*  479 */         else { if (wrappedDataModel == null) {
/*  480 */             throw new IllegalArgumentException(wrapper
/*  481 */                 .getClass().getName() + " converted " + dataModel.getClass().getName() + " to null.");
/*      */           }
/*  483 */           throw new IllegalArgumentException(wrapper
/*  484 */               .getClass().getName() + " didn't convert " + dataModel.getClass().getName() + " to a TemplateHashModel. Generally, you want to use a Map<String, Object> or a JavaBean as the root-map (aka. data-model) parameter. The Map key-s or JavaBean property names will be the variable names in the template."); }
/*      */       
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  491 */     return new Environment(this, dataModelHash, out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Environment createProcessingEnvironment(Object dataModel, Writer out) throws TemplateException, IOException {
/*  500 */     return createProcessingEnvironment(dataModel, out, (ObjectWrapper)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String toString() {
/*  509 */     StringWriter sw = new StringWriter();
/*      */     try {
/*  511 */       dump(sw);
/*  512 */     } catch (IOException ioe) {
/*  513 */       throw new RuntimeException(ioe.getMessage());
/*      */     } 
/*  515 */     return sw.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getName() {
/*  547 */     return this.name;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSourceName() {
/*  564 */     return (this.sourceName != null) ? this.sourceName : getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Configuration getConfiguration() {
/*  571 */     return (Configuration)getParent();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ParserConfiguration getParserConfiguration() {
/*  582 */     return this.parserConfiguration;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Version getTemplateLanguageVersion() {
/*  591 */     return this.templateLanguageVersion;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void setEncoding(String encoding) {
/*  604 */     this.encoding = encoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getEncoding() {
/*  613 */     return this.encoding;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Object getCustomLookupCondition() {
/*  624 */     return this.customLookupCondition;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setCustomLookupCondition(Object customLookupCondition) {
/*  636 */     this.customLookupCondition = customLookupCondition;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getActualTagSyntax() {
/*  651 */     return this.actualTagSyntax;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getInterpolationSyntax() {
/*  667 */     return this.interpolationSyntax;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getActualNamingConvention() {
/*  682 */     return this.actualNamingConvention;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OutputFormat getOutputFormat() {
/*  695 */     return this.outputFormat;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setOutputFormat(OutputFormat outputFormat) {
/*  702 */     this.outputFormat = outputFormat;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean getAutoEscaping() {
/*  715 */     return this.autoEscaping;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void setAutoEscaping(boolean autoEscaping) {
/*  722 */     this.autoEscaping = autoEscaping;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void dump(PrintStream ps) {
/*  729 */     ps.print(this.rootElement.getCanonicalForm());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void dump(Writer out) throws IOException {
/*  736 */     out.write(this.rootElement.getCanonicalForm());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void addMacro(Macro macro) {
/*  746 */     this.macros.put(macro.getName(), macro);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void addImport(LibraryLoad ll) {
/*  756 */     this.imports.add(ll);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getSource(int beginColumn, int beginLine, int endColumn, int endLine) {
/*  777 */     if (beginLine < 1 || endLine < 1) return null;
/*      */ 
/*      */     
/*  780 */     beginLine--;
/*  781 */     beginColumn--;
/*  782 */     endColumn--;
/*  783 */     endLine--;
/*  784 */     StringBuilder buf = new StringBuilder();
/*  785 */     for (int i = beginLine; i <= endLine; i++) {
/*  786 */       if (i < this.lines.size()) {
/*  787 */         buf.append(this.lines.get(i));
/*      */       }
/*      */     } 
/*  790 */     int lastLineLength = this.lines.get(endLine).toString().length();
/*  791 */     int trailingCharsToDelete = lastLineLength - endColumn - 1;
/*  792 */     buf.delete(0, beginColumn);
/*  793 */     buf.delete(buf.length() - trailingCharsToDelete, buf.length());
/*  794 */     return buf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private class LineTableBuilder
/*      */     extends FilterReader
/*      */   {
/*      */     private final int tabSize;
/*      */     
/*  804 */     private final StringBuilder lineBuf = new StringBuilder();
/*      */ 
/*      */     
/*      */     int lastChar;
/*      */     
/*      */     boolean closed;
/*      */     
/*      */     private Exception failure;
/*      */ 
/*      */     
/*      */     LineTableBuilder(Reader r, ParserConfiguration parserConfiguration) {
/*  815 */       super(r);
/*  816 */       this.tabSize = parserConfiguration.getTabSize();
/*      */     }
/*      */     
/*      */     public boolean hasFailure() {
/*  820 */       return (this.failure != null);
/*      */     }
/*      */     
/*      */     public void throwFailure() throws IOException {
/*  824 */       if (this.failure != null) {
/*  825 */         if (this.failure instanceof IOException) {
/*  826 */           throw (IOException)this.failure;
/*      */         }
/*  828 */         if (this.failure instanceof RuntimeException) {
/*  829 */           throw (RuntimeException)this.failure;
/*      */         }
/*  831 */         throw new UndeclaredThrowableException(this.failure);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public int read() throws IOException {
/*      */       try {
/*  838 */         int c = this.in.read();
/*  839 */         handleChar(c);
/*  840 */         return c;
/*  841 */       } catch (Exception e) {
/*  842 */         throw rememberException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     private IOException rememberException(Exception e) throws IOException {
/*  848 */       if (!this.closed) {
/*  849 */         this.failure = e;
/*      */       }
/*  851 */       if (e instanceof IOException) {
/*  852 */         return (IOException)e;
/*      */       }
/*  854 */       if (e instanceof RuntimeException) {
/*  855 */         throw (RuntimeException)e;
/*      */       }
/*  857 */       throw new UndeclaredThrowableException(e);
/*      */     }
/*      */ 
/*      */     
/*      */     public int read(char[] cbuf, int off, int len) throws IOException {
/*      */       try {
/*  863 */         int numchars = this.in.read(cbuf, off, len);
/*  864 */         for (int i = off; i < off + numchars; i++) {
/*  865 */           char c = cbuf[i];
/*  866 */           handleChar(c);
/*      */         } 
/*  868 */         return numchars;
/*  869 */       } catch (Exception e) {
/*  870 */         throw rememberException(e);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public void close() throws IOException {
/*  876 */       if (this.lineBuf.length() > 0) {
/*  877 */         Template.this.lines.add(this.lineBuf.toString());
/*  878 */         this.lineBuf.setLength(0);
/*      */       } 
/*  880 */       super.close();
/*  881 */       this.closed = true;
/*      */     }
/*      */     
/*      */     private void handleChar(int c) {
/*  885 */       if (c == 10 || c == 13) {
/*  886 */         if (this.lastChar == 13 && c == 10) {
/*  887 */           int lastIndex = Template.this.lines.size() - 1;
/*  888 */           String lastLine = Template.this.lines.get(lastIndex);
/*  889 */           Template.this.lines.set(lastIndex, lastLine + '\n');
/*      */         } else {
/*  891 */           this.lineBuf.append((char)c);
/*  892 */           Template.this.lines.add(this.lineBuf.toString());
/*  893 */           this.lineBuf.setLength(0);
/*      */         } 
/*  895 */       } else if (c == 9 && this.tabSize != 1) {
/*  896 */         int numSpaces = this.tabSize - this.lineBuf.length() % this.tabSize;
/*  897 */         for (int i = 0; i < numSpaces; i++) {
/*  898 */           this.lineBuf.append(' ');
/*      */         }
/*      */       } else {
/*  901 */         this.lineBuf.append((char)c);
/*      */       } 
/*  903 */       this.lastChar = c;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public TemplateElement getRootTreeNode() {
/*  912 */     return this.rootElement;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public Map getMacros() {
/*  920 */     return this.macros;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public List getImports() {
/*  928 */     return this.imports;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public void addPrefixNSMapping(String prefix, String nsURI) {
/*  938 */     if (nsURI.length() == 0) {
/*  939 */       throw new IllegalArgumentException("Cannot map empty string URI");
/*      */     }
/*  941 */     if (prefix.length() == 0) {
/*  942 */       throw new IllegalArgumentException("Cannot map empty string prefix");
/*      */     }
/*  944 */     if (prefix.equals("N")) {
/*  945 */       throw new IllegalArgumentException("The prefix: " + prefix + " cannot be registered, it's reserved for special internal use.");
/*      */     }
/*  947 */     if (this.prefixToNamespaceURILookup.containsKey(prefix)) {
/*  948 */       throw new IllegalArgumentException("The prefix: '" + prefix + "' was repeated. This is illegal.");
/*      */     }
/*  950 */     if (this.namespaceURIToPrefixLookup.containsKey(nsURI)) {
/*  951 */       throw new IllegalArgumentException("The namespace URI: " + nsURI + " cannot be mapped to 2 different prefixes.");
/*      */     }
/*  953 */     if (prefix.equals("D")) {
/*  954 */       this.defaultNS = nsURI;
/*      */     } else {
/*  956 */       this.prefixToNamespaceURILookup.put(prefix, nsURI);
/*  957 */       this.namespaceURIToPrefixLookup.put(nsURI, prefix);
/*      */     } 
/*      */   }
/*      */   
/*      */   public String getDefaultNS() {
/*  962 */     return this.defaultNS;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getNamespaceForPrefix(String prefix) {
/*  969 */     if (prefix.equals("")) {
/*  970 */       return (this.defaultNS == null) ? "" : this.defaultNS;
/*      */     }
/*  972 */     return (String)this.prefixToNamespaceURILookup.get(prefix);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPrefixForNamespace(String nsURI) {
/*  979 */     if (nsURI == null) {
/*  980 */       return null;
/*      */     }
/*  982 */     if (nsURI.length() == 0) {
/*  983 */       return (this.defaultNS == null) ? "" : "N";
/*      */     }
/*  985 */     if (nsURI.equals(this.defaultNS)) {
/*  986 */       return "";
/*      */     }
/*  988 */     return (String)this.namespaceURIToPrefixLookup.get(nsURI);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPrefixedName(String localName, String nsURI) {
/*  997 */     if (nsURI == null || nsURI.length() == 0) {
/*  998 */       if (this.defaultNS != null) {
/*  999 */         return "N:" + localName;
/*      */       }
/* 1001 */       return localName;
/*      */     } 
/*      */     
/* 1004 */     if (nsURI.equals(this.defaultNS)) {
/* 1005 */       return localName;
/*      */     }
/* 1007 */     String prefix = getPrefixForNamespace(nsURI);
/* 1008 */     if (prefix == null) {
/* 1009 */       return null;
/*      */     }
/* 1011 */     return prefix + ":" + localName;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   @Deprecated
/*      */   public TreePath containingElements(int column, int line) {
/* 1020 */     ArrayList<TemplateElement> elements = new ArrayList();
/* 1021 */     TemplateElement element = this.rootElement;
/* 1022 */     while (element.contains(column, line)) {
/* 1023 */       elements.add(element);
/* 1024 */       for (Enumeration<TemplateElement> enumeration = element.children(); enumeration.hasMoreElements(); ) {
/* 1025 */         TemplateElement elem = enumeration.nextElement();
/* 1026 */         if (elem.contains(column, line)) {
/* 1027 */           element = elem;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1033 */     if (elements.isEmpty()) {
/* 1034 */       return null;
/*      */     }
/* 1036 */     return new TreePath(elements.toArray());
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class WrongEncodingException
/*      */     extends ParseException
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public String specifiedEncoding;
/*      */ 
/*      */     
/*      */     private final String constructorSpecifiedEncoding;
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public WrongEncodingException(String templateSpecifiedEncoding) {
/* 1057 */       this(templateSpecifiedEncoding, null);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public WrongEncodingException(String templateSpecifiedEncoding, String constructorSpecifiedEncoding) {
/* 1064 */       this.specifiedEncoding = templateSpecifiedEncoding;
/* 1065 */       this.constructorSpecifiedEncoding = constructorSpecifiedEncoding;
/*      */     }
/*      */ 
/*      */     
/*      */     public String getMessage() {
/* 1070 */       return "Encoding specified inside the template (" + this.specifiedEncoding + ") doesn't match the encoding specified for the Template constructor" + ((this.constructorSpecifiedEncoding != null) ? (" (" + this.constructorSpecifiedEncoding + ").") : ".");
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getTemplateSpecifiedEncoding() {
/* 1079 */       return this.specifiedEncoding;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getConstructorSpecifiedEncoding() {
/* 1086 */       return this.constructorSpecifiedEncoding;
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\Template.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */