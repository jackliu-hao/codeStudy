/*    */ package freemarker.ext.dom;
/*    */ 
/*    */ import freemarker.core.Environment;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class _ExtDomApi
/*    */ {
/*    */   public static boolean isXMLNameLike(String name) {
/* 36 */     return DomStringUtil.isXMLNameLike(name);
/*    */   }
/*    */   
/*    */   public static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
/* 40 */     return DomStringUtil.matchesName(qname, nodeName, nsURI, env);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\_ExtDomApi.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */