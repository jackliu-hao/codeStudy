package freemarker.ext.dom;

import freemarker.core.CustomAttribute;
import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.jaxen.BaseXPath;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.Navigator;
import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;
import org.jaxen.XPathFunctionContext;
import org.jaxen.dom.DocumentNavigator;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

class JaxenXPathSupport implements XPathSupport {
   private static final CustomAttribute XPATH_CACHE_ATTR = new CustomAttribute(1) {
      protected Object create() {
         return new HashMap();
      }
   };
   private static final ArrayList EMPTY_ARRAYLIST = new ArrayList();
   private static final NamespaceContext customNamespaceContext = new NamespaceContext() {
      public String translateNamespacePrefixToUri(String prefix) {
         return prefix.equals("D") ? Environment.getCurrentEnvironment().getDefaultNS() : Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
      }
   };
   private static final VariableContext FM_VARIABLE_CONTEXT = new VariableContext() {
      public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException {
         try {
            TemplateModel model = Environment.getCurrentEnvironment().getVariable(localName);
            if (model == null) {
               throw new UnresolvableException("Variable \"" + localName + "\" not found.");
            }

            if (model instanceof TemplateScalarModel) {
               return ((TemplateScalarModel)model).getAsString();
            }

            if (model instanceof TemplateNumberModel) {
               return ((TemplateNumberModel)model).getAsNumber();
            }

            if (model instanceof TemplateDateModel) {
               return ((TemplateDateModel)model).getAsDate();
            }

            if (model instanceof TemplateBooleanModel) {
               return ((TemplateBooleanModel)model).getAsBoolean();
            }
         } catch (TemplateModelException var5) {
            throw new UndeclaredThrowableException(var5);
         }

         throw new UnresolvableException("Variable \"" + localName + "\" exists, but it's not a string, number, date, or boolean");
      }
   };
   private static final FunctionContext FM_FUNCTION_CONTEXT = new XPathFunctionContext() {
      public Function getFunction(String namespaceURI, String prefix, String localName) throws UnresolvableException {
         try {
            return super.getFunction(namespaceURI, prefix, localName);
         } catch (UnresolvableException var5) {
            return super.getFunction((String)null, (String)null, localName);
         }
      }
   };
   private static final CustomAttribute FM_DOM_NAVIAGOTOR_CACHED_DOM = new CustomAttribute(1);
   private static final Navigator FM_DOM_NAVIGATOR = new DocumentNavigator() {
      public Object getDocument(String uri) throws FunctionCallException {
         try {
            Template raw = JaxenXPathSupport.getTemplate(uri);
            Document doc = (Document)JaxenXPathSupport.FM_DOM_NAVIAGOTOR_CACHED_DOM.get(raw);
            if (doc == null) {
               DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
               factory.setNamespaceAware(true);
               DocumentBuilder builder = factory.newDocumentBuilder();
               FmEntityResolver er = new FmEntityResolver();
               builder.setEntityResolver(er);
               doc = builder.parse(JaxenXPathSupport.createInputSource((String)null, raw));
               if (er.getCallCount() == 0) {
                  JaxenXPathSupport.FM_DOM_NAVIAGOTOR_CACHED_DOM.set(doc, (Template)raw);
               }
            }

            return doc;
         } catch (Exception var7) {
            throw new FunctionCallException("Failed to parse document for URI: " + uri, var7);
         }
      }
   };

   public TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
      try {
         Map<String, BaseXPath> xpathCache = (Map)XPATH_CACHE_ATTR.get();
         BaseXPath xpath;
         synchronized(xpathCache) {
            xpath = (BaseXPath)xpathCache.get(xpathQuery);
            if (xpath == null) {
               xpath = new BaseXPath(xpathQuery, FM_DOM_NAVIGATOR);
               xpath.setNamespaceContext(customNamespaceContext);
               xpath.setFunctionContext(FM_FUNCTION_CONTEXT);
               xpath.setVariableContext(FM_VARIABLE_CONTEXT);
               xpathCache.put(xpathQuery, xpath);
            }
         }

         List result = xpath.selectNodes(context != null ? context : EMPTY_ARRAYLIST);
         if (result.size() == 1) {
            return ObjectWrapper.DEFAULT_WRAPPER.wrap(result.get(0));
         } else {
            NodeListModel nlm = new NodeListModel(result, (NodeModel)null);
            nlm.xpathSupport = this;
            return nlm;
         }
      } catch (UndeclaredThrowableException var8) {
         Throwable t = var8.getUndeclaredThrowable();
         if (t instanceof TemplateModelException) {
            throw (TemplateModelException)t;
         } else {
            throw var8;
         }
      } catch (JaxenException var9) {
         throw new TemplateModelException(var9);
      }
   }

   static Template getTemplate(String systemId) throws IOException {
      Environment env = Environment.getCurrentEnvironment();
      String encoding = env.getTemplate().getEncoding();
      if (encoding == null) {
         encoding = env.getConfiguration().getEncoding(env.getLocale());
      }

      String templatePath = env.getTemplate().getName();
      int lastSlash = templatePath.lastIndexOf(47);
      templatePath = lastSlash == -1 ? "" : templatePath.substring(0, lastSlash + 1);
      systemId = env.toFullTemplateName(templatePath, systemId);
      Template raw = env.getConfiguration().getTemplate(systemId, env.getLocale(), encoding, false);
      return raw;
   }

   private static InputSource createInputSource(String publicId, Template raw) throws IOException, SAXException {
      StringWriter sw = new StringWriter();

      try {
         raw.process(Collections.EMPTY_MAP, sw);
      } catch (TemplateException var4) {
         throw new SAXException(var4);
      }

      InputSource is = new InputSource();
      is.setPublicId(publicId);
      is.setSystemId(raw.getName());
      is.setCharacterStream(new StringReader(sw.toString()));
      return is;
   }

   private static class FmEntityResolver implements EntityResolver {
      private int callCount;

      private FmEntityResolver() {
         this.callCount = 0;
      }

      public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
         ++this.callCount;
         return JaxenXPathSupport.createInputSource(publicId, JaxenXPathSupport.getTemplate(systemId));
      }

      int getCallCount() {
         return this.callCount;
      }

      // $FF: synthetic method
      FmEntityResolver(Object x0) {
         this();
      }
   }
}
