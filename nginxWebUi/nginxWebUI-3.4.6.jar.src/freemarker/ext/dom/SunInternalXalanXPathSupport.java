/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import com.sun.org.apache.xml.internal.utils.PrefixResolver;
/*     */ import com.sun.org.apache.xpath.internal.XPath;
/*     */ import com.sun.org.apache.xpath.internal.XPathContext;
/*     */ import com.sun.org.apache.xpath.internal.objects.XBoolean;
/*     */ import com.sun.org.apache.xpath.internal.objects.XNumber;
/*     */ import com.sun.org.apache.xpath.internal.objects.XObject;
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.SimpleNumber;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.TemplateBooleanModel;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateModelException;
/*     */ import java.util.List;
/*     */ import javax.xml.transform.TransformerException;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SunInternalXalanXPathSupport
/*     */   implements XPathSupport
/*     */ {
/*  55 */   private XPathContext xpathContext = new XPathContext();
/*     */ 
/*     */   
/*     */   public synchronized TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
/*  59 */     if (!(context instanceof Node)) {
/*  60 */       if (context == null || isNodeList(context)) {
/*  61 */         int cnt = (context != null) ? ((List)context).size() : 0;
/*  62 */         throw new TemplateModelException(((cnt != 0) ? ("Xalan can't perform an XPath query against a Node Set (contains " + cnt + " node(s)). Expecting a single Node.") : "Xalan can't perform an XPath query against an empty Node Set.") + " (There's no such restriction if you configure FreeMarker to use Jaxen for XPath.)");
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  70 */       throw new TemplateModelException("Can't perform an XPath query against a " + context
/*  71 */           .getClass().getName() + ". Expecting a single org.w3c.dom.Node.");
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*  76 */     Node node = (Node)context;
/*     */     try {
/*  78 */       XPath xpath = new XPath(xpathQuery, null, CUSTOM_PREFIX_RESOLVER, 0, null);
/*  79 */       int ctxtNode = this.xpathContext.getDTMHandleFromNode(node);
/*  80 */       XObject xresult = xpath.execute(this.xpathContext, ctxtNode, CUSTOM_PREFIX_RESOLVER);
/*  81 */       if (xresult instanceof com.sun.org.apache.xpath.internal.objects.XNodeSet) {
/*  82 */         NodeListModel result = new NodeListModel(node);
/*  83 */         result.xpathSupport = this;
/*  84 */         NodeIterator nodeIterator = xresult.nodeset();
/*     */         
/*     */         while (true) {
/*  87 */           Node n = nodeIterator.nextNode();
/*  88 */           if (n != null) {
/*  89 */             result.add(n);
/*     */           }
/*  91 */           if (n == null)
/*  92 */             return (result.size() == 1) ? result.get(0) : (TemplateModel)result; 
/*     */         } 
/*  94 */       }  if (xresult instanceof XBoolean) {
/*  95 */         return ((XBoolean)xresult).bool() ? (TemplateModel)TemplateBooleanModel.TRUE : (TemplateModel)TemplateBooleanModel.FALSE;
/*     */       }
/*  97 */       if (xresult instanceof com.sun.org.apache.xpath.internal.objects.XNull) {
/*  98 */         return null;
/*     */       }
/* 100 */       if (xresult instanceof com.sun.org.apache.xpath.internal.objects.XString) {
/* 101 */         return (TemplateModel)new SimpleScalar(xresult.toString());
/*     */       }
/* 103 */       if (xresult instanceof XNumber) {
/* 104 */         return (TemplateModel)new SimpleNumber(Double.valueOf(((XNumber)xresult).num()));
/*     */       }
/* 106 */       throw new TemplateModelException("Cannot deal with type: " + xresult.getClass().getName());
/* 107 */     } catch (TransformerException te) {
/* 108 */       throw new TemplateModelException(te);
/*     */     } 
/*     */   }
/*     */   
/* 112 */   private static final PrefixResolver CUSTOM_PREFIX_RESOLVER = new PrefixResolver()
/*     */     {
/*     */       public String getNamespaceForPrefix(String prefix, Node node)
/*     */       {
/* 116 */         return getNamespaceForPrefix(prefix);
/*     */       }
/*     */ 
/*     */       
/*     */       public String getNamespaceForPrefix(String prefix) {
/* 121 */         if (prefix.equals("D")) {
/* 122 */           return Environment.getCurrentEnvironment().getDefaultNS();
/*     */         }
/* 124 */         return Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
/*     */       }
/*     */ 
/*     */       
/*     */       public String getBaseIdentifier() {
/* 129 */         return null;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean handlesNullPrefixes() {
/* 134 */         return false;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isNodeList(Object context) {
/* 142 */     if (!(context instanceof List)) {
/* 143 */       return false;
/*     */     }
/*     */     
/* 146 */     List ls = (List)context;
/* 147 */     int ln = ls.size();
/* 148 */     for (int i = 0; i < ln; i++) {
/* 149 */       if (!(ls.get(i) instanceof Node)) {
/* 150 */         return false;
/*     */       }
/*     */     } 
/* 153 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\SunInternalXalanXPathSupport.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */