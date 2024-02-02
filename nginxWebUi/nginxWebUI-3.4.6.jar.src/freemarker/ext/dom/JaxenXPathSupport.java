/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import freemarker.core.CustomAttribute;
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateDateModel;
/*     */ import freemarker.template.TemplateException;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import freemarker.template.TemplateNumberModel;
/*     */ import freemarker.template.TemplateScalarModel;
/*     */ import freemarker.template.utility.UndeclaredThrowableException;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import org.jaxen.BaseXPath;
/*     */ import org.jaxen.Function;
/*     */ import org.jaxen.FunctionCallException;
/*     */ import org.jaxen.FunctionContext;
/*     */ import org.jaxen.JaxenException;
/*     */ import org.jaxen.NamespaceContext;
/*     */ import org.jaxen.Navigator;
/*     */ import org.jaxen.UnresolvableException;
/*     */ import org.jaxen.VariableContext;
/*     */ import org.jaxen.XPathFunctionContext;
/*     */ import org.jaxen.dom.DocumentNavigator;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.EntityResolver;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class JaxenXPathSupport
/*     */   implements XPathSupport
/*     */ {
/*  68 */   private static final CustomAttribute XPATH_CACHE_ATTR = new CustomAttribute(1)
/*     */     {
/*     */       protected Object create()
/*     */       {
/*  72 */         return new HashMap<>();
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*  77 */   private static final ArrayList EMPTY_ARRAYLIST = new ArrayList();
/*     */ 
/*     */   
/*     */   public TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
/*     */     try {
/*     */       BaseXPath xpath;
/*  83 */       Map<String, BaseXPath> xpathCache = (Map<String, BaseXPath>)XPATH_CACHE_ATTR.get();
/*  84 */       synchronized (xpathCache) {
/*  85 */         xpath = xpathCache.get(xpathQuery);
/*  86 */         if (xpath == null) {
/*  87 */           xpath = new BaseXPath(xpathQuery, FM_DOM_NAVIGATOR);
/*  88 */           xpath.setNamespaceContext(customNamespaceContext);
/*  89 */           xpath.setFunctionContext(FM_FUNCTION_CONTEXT);
/*  90 */           xpath.setVariableContext(FM_VARIABLE_CONTEXT);
/*  91 */           xpathCache.put(xpathQuery, xpath);
/*     */         } 
/*     */       } 
/*  94 */       List result = xpath.selectNodes((context != null) ? context : EMPTY_ARRAYLIST);
/*  95 */       if (result.size() == 1)
/*     */       {
/*  97 */         return ObjectWrapper.DEFAULT_WRAPPER.wrap(result.get(0));
/*     */       }
/*  99 */       NodeListModel nlm = new NodeListModel(result, null);
/* 100 */       nlm.xpathSupport = this;
/* 101 */       return (TemplateModel)nlm;
/* 102 */     } catch (UndeclaredThrowableException e) {
/* 103 */       BaseXPath xpath; Throwable t = xpath.getUndeclaredThrowable();
/* 104 */       if (t instanceof TemplateModelException) {
/* 105 */         throw (TemplateModelException)t;
/*     */       }
/* 107 */       throw xpath;
/* 108 */     } catch (JaxenException je) {
/* 109 */       throw new TemplateModelException(je);
/*     */     } 
/*     */   }
/*     */   
/* 113 */   private static final NamespaceContext customNamespaceContext = new NamespaceContext()
/*     */     {
/*     */       public String translateNamespacePrefixToUri(String prefix)
/*     */       {
/* 117 */         if (prefix.equals("D")) {
/* 118 */           return Environment.getCurrentEnvironment().getDefaultNS();
/*     */         }
/* 120 */         return Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
/*     */       }
/*     */     };
/*     */   
/* 124 */   private static final VariableContext FM_VARIABLE_CONTEXT = new VariableContext()
/*     */     {
/*     */       public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException
/*     */       {
/*     */         try {
/* 129 */           TemplateModel model = Environment.getCurrentEnvironment().getVariable(localName);
/* 130 */           if (model == null) {
/* 131 */             throw new UnresolvableException("Variable \"" + localName + "\" not found.");
/*     */           }
/* 133 */           if (model instanceof TemplateScalarModel) {
/* 134 */             return ((TemplateScalarModel)model).getAsString();
/*     */           }
/* 136 */           if (model instanceof TemplateNumberModel) {
/* 137 */             return ((TemplateNumberModel)model).getAsNumber();
/*     */           }
/* 139 */           if (model instanceof TemplateDateModel) {
/* 140 */             return ((TemplateDateModel)model).getAsDate();
/*     */           }
/* 142 */           if (model instanceof TemplateBooleanModel) {
/* 143 */             return Boolean.valueOf(((TemplateBooleanModel)model).getAsBoolean());
/*     */           }
/* 145 */         } catch (TemplateModelException e) {
/* 146 */           throw new UndeclaredThrowableException(e);
/*     */         } 
/* 148 */         throw new UnresolvableException("Variable \"" + localName + "\" exists, but it's not a string, number, date, or boolean");
/*     */       }
/*     */     };
/*     */ 
/*     */   
/* 153 */   private static final FunctionContext FM_FUNCTION_CONTEXT = (FunctionContext)new XPathFunctionContext()
/*     */     {
/*     */       public Function getFunction(String namespaceURI, String prefix, String localName) throws UnresolvableException
/*     */       {
/*     */         try {
/* 158 */           return super.getFunction(namespaceURI, prefix, localName);
/* 159 */         } catch (UnresolvableException e) {
/* 160 */           return super.getFunction(null, null, localName);
/*     */         } 
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 168 */   private static final CustomAttribute FM_DOM_NAVIAGOTOR_CACHED_DOM = new CustomAttribute(1);
/*     */ 
/*     */   
/* 171 */   private static final Navigator FM_DOM_NAVIGATOR = (Navigator)new DocumentNavigator()
/*     */     {
/*     */       public Object getDocument(String uri) throws FunctionCallException {
/*     */         try {
/* 175 */           Template raw = JaxenXPathSupport.getTemplate(uri);
/* 176 */           Document doc = (Document)JaxenXPathSupport.FM_DOM_NAVIAGOTOR_CACHED_DOM.get(raw);
/* 177 */           if (doc == null) {
/* 178 */             DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/* 179 */             factory.setNamespaceAware(true);
/* 180 */             DocumentBuilder builder = factory.newDocumentBuilder();
/* 181 */             JaxenXPathSupport.FmEntityResolver er = new JaxenXPathSupport.FmEntityResolver();
/* 182 */             builder.setEntityResolver(er);
/* 183 */             doc = builder.parse(JaxenXPathSupport.createInputSource(null, raw));
/*     */ 
/*     */             
/* 186 */             if (er.getCallCount() == 0) {
/* 187 */               JaxenXPathSupport.FM_DOM_NAVIAGOTOR_CACHED_DOM.set(doc, raw);
/*     */             }
/*     */           } 
/* 190 */           return doc;
/* 191 */         } catch (Exception e) {
/* 192 */           throw new FunctionCallException("Failed to parse document for URI: " + uri, e);
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*     */   static Template getTemplate(String systemId) throws IOException {
/* 198 */     Environment env = Environment.getCurrentEnvironment();
/* 199 */     String encoding = env.getTemplate().getEncoding();
/* 200 */     if (encoding == null) {
/* 201 */       encoding = env.getConfiguration().getEncoding(env.getLocale());
/*     */     }
/* 203 */     String templatePath = env.getTemplate().getName();
/* 204 */     int lastSlash = templatePath.lastIndexOf('/');
/* 205 */     templatePath = (lastSlash == -1) ? "" : templatePath.substring(0, lastSlash + 1);
/* 206 */     systemId = env.toFullTemplateName(templatePath, systemId);
/* 207 */     Template raw = env.getConfiguration().getTemplate(systemId, env.getLocale(), encoding, false);
/* 208 */     return raw;
/*     */   }
/*     */   
/*     */   private static InputSource createInputSource(String publicId, Template raw) throws IOException, SAXException {
/* 212 */     StringWriter sw = new StringWriter();
/*     */     try {
/* 214 */       raw.process(Collections.EMPTY_MAP, sw);
/* 215 */     } catch (TemplateException e) {
/* 216 */       throw new SAXException(e);
/*     */     } 
/* 218 */     InputSource is = new InputSource();
/* 219 */     is.setPublicId(publicId);
/* 220 */     is.setSystemId(raw.getName());
/* 221 */     is.setCharacterStream(new StringReader(sw.toString()));
/* 222 */     return is;
/*     */   }
/*     */   
/*     */   private static class FmEntityResolver implements EntityResolver {
/* 226 */     private int callCount = 0;
/*     */ 
/*     */ 
/*     */     
/*     */     public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
/* 231 */       this.callCount++;
/* 232 */       return JaxenXPathSupport.createInputSource(publicId, JaxenXPathSupport.getTemplate(systemId));
/*     */     }
/*     */     
/*     */     int getCallCount() {
/* 236 */       return this.callCount;
/*     */     }
/*     */     
/*     */     private FmEntityResolver() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\JaxenXPathSupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */