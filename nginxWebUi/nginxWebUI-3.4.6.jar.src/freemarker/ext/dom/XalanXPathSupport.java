/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.util.List;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import org.apache.xml.utils.PrefixResolver;
/*     */ import org.apache.xpath.XPath;
/*     */ import org.apache.xpath.XPathContext;
/*     */ import org.apache.xpath.objects.XBoolean;
/*     */ import org.apache.xpath.objects.XNumber;
/*     */ import org.apache.xpath.objects.XObject;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.traversal.NodeIterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class XalanXPathSupport
/*     */   implements XPathSupport
/*     */ {
/*  51 */   private XPathContext xpathContext = new XPathContext();
/*     */ 
/*     */   
/*     */   public synchronized TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
/*  55 */     if (!(context instanceof Node)) {
/*  56 */       if (context == null || isNodeList(context)) {
/*  57 */         int cnt = (context != null) ? ((List)context).size() : 0;
/*  58 */         throw new TemplateModelException(((cnt != 0) ? ("Xalan can't perform an XPath query against a Node Set (contains " + cnt + " node(s)). Expecting a single Node.") : "Xalan can't perform an XPath query against an empty Node Set.") + " (There's no such restriction if you configure FreeMarker to use Jaxen for XPath.)");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  66 */       throw new TemplateModelException("Can't perform an XPath query against a " + context
/*  67 */           .getClass().getName() + ". Expecting a single org.w3c.dom.Node.");
/*     */     } 
/*     */ 
/*     */     
/*  71 */     Node node = (Node)context;
/*     */     try {
/*  73 */       XPath xpath = new XPath(xpathQuery, null, CUSTOM_PREFIX_RESOLVER, 0, null);
/*  74 */       int ctxtNode = this.xpathContext.getDTMHandleFromNode(node);
/*  75 */       XObject xresult = xpath.execute(this.xpathContext, ctxtNode, CUSTOM_PREFIX_RESOLVER);
/*  76 */       if (xresult instanceof org.apache.xpath.objects.XNodeSet) {
/*  77 */         NodeListModel result = new NodeListModel(node);
/*  78 */         result.xpathSupport = this;
/*  79 */         NodeIterator nodeIterator = xresult.nodeset();
/*     */         
/*     */         while (true) {
/*  82 */           Node n = nodeIterator.nextNode();
/*  83 */           if (n != null) {
/*  84 */             result.add(n);
/*     */           }
/*  86 */           if (n == null)
/*  87 */             return (result.size() == 1) ? result.get(0) : (TemplateModel)result; 
/*     */         } 
/*  89 */       }  if (xresult instanceof XBoolean) {
/*  90 */         return ((XBoolean)xresult).bool() ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */       }
/*  92 */       if (xresult instanceof org.apache.xpath.objects.XNull) {
/*  93 */         return null;
/*     */       }
/*  95 */       if (xresult instanceof org.apache.xpath.objects.XString) {
/*  96 */         return (TemplateModel)new SimpleScalar(xresult.toString());
/*     */       }
/*  98 */       if (xresult instanceof XNumber) {
/*  99 */         return (TemplateModel)new SimpleNumber(Double.valueOf(((XNumber)xresult).num()));
/*     */       }
/* 101 */       throw new TemplateModelException("Cannot deal with type: " + xresult.getClass().getName());
/* 102 */     } catch (TransformerException te) {
/* 103 */       throw new TemplateModelException(te);
/*     */     } 
/*     */   }
/*     */   
/* 107 */   private static final PrefixResolver CUSTOM_PREFIX_RESOLVER = new PrefixResolver()
/*     */     {
/*     */       public String getNamespaceForPrefix(String prefix, Node node)
/*     */       {
/* 111 */         return getNamespaceForPrefix(prefix);
/*     */       }
/*     */ 
/*     */       
/*     */       public String getNamespaceForPrefix(String prefix) {
/* 116 */         if (prefix.equals("D")) {
/* 117 */           return Environment.getCurrentEnvironment().getDefaultNS();
/*     */         }
/* 119 */         return Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
/*     */       }
/*     */ 
/*     */       
/*     */       public String getBaseIdentifier() {
/* 124 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean handlesNullPrefixes() {
/* 129 */         return false;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isNodeList(Object context) {
/* 137 */     if (!(context instanceof List)) {
/* 138 */       return false;
/*     */     }
/*     */     
/* 141 */     List ls = (List)context;
/* 142 */     int ln = ls.size();
/* 143 */     for (int i = 0; i < ln; i++) {
/* 144 */       if (!(ls.get(i) instanceof Node)) {
/* 145 */         return false;
/*     */       }
/*     */     } 
/* 148 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\XalanXPathSupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */