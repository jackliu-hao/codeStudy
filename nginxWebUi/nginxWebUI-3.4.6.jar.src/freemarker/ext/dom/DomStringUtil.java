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
/*    */ 
/*    */ final class DomStringUtil
/*    */ {
/*    */   static boolean isXMLNameLike(String name) {
/* 37 */     return isXMLNameLike(name, 0);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static boolean isXMLNameLike(String name, int firstCharIdx) {
/* 49 */     int ln = name.length();
/* 50 */     for (int i = firstCharIdx; i < ln; i++) {
/* 51 */       char c = name.charAt(i);
/* 52 */       if (i == firstCharIdx && (c == '-' || c == '.' || Character.isDigit(c))) {
/* 53 */         return false;
/*    */       }
/* 55 */       if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '.') {
/* 56 */         if (c == ':') {
/* 57 */           if (i + 1 < ln && name.charAt(i + 1) == ':')
/*    */           {
/* 59 */             return false;
/*    */           }
/*    */         } else {
/*    */           
/* 63 */           return false;
/*    */         } 
/*    */       }
/*    */     } 
/* 67 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
/* 74 */     String defaultNS = env.getDefaultNS();
/* 75 */     if (defaultNS != null && defaultNS.equals(nsURI)) {
/* 76 */       return (qname.equals(nodeName) || qname
/* 77 */         .equals("D:" + nodeName));
/*    */     }
/* 79 */     if ("".equals(nsURI)) {
/* 80 */       if (defaultNS != null) {
/* 81 */         return qname.equals("N:" + nodeName);
/*    */       }
/* 83 */       return (qname.equals(nodeName) || qname.equals("N:" + nodeName));
/*    */     } 
/*    */     
/* 86 */     String prefix = env.getPrefixForNamespace(nsURI);
/* 87 */     if (prefix == null) {
/* 88 */       return false;
/*    */     }
/* 90 */     return qname.equals(prefix + ":" + nodeName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\DomStringUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */