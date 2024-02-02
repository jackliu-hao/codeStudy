/*    */ package org.wildfly.common.net;
/*    */ 
/*    */ import java.net.UnknownHostException;
/*    */ import java.security.PrivilegedAction;
/*    */ import java.util.regex.Pattern;
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
/*    */ final class GetHostInfoAction
/*    */   implements PrivilegedAction<String[]>
/*    */ {
/*    */   public String[] run() {
/* 33 */     String qualifiedHostName = System.getProperty("jboss.qualified.host.name");
/* 34 */     String providedHostName = System.getProperty("jboss.host.name");
/* 35 */     String providedNodeName = System.getProperty("jboss.node.name");
/* 36 */     if (qualifiedHostName == null) {
/*    */       
/* 38 */       qualifiedHostName = providedHostName;
/* 39 */       if (qualifiedHostName == null)
/*    */       {
/* 41 */         qualifiedHostName = System.getenv("HOSTNAME");
/*    */       }
/* 43 */       if (qualifiedHostName == null)
/*    */       {
/* 45 */         qualifiedHostName = System.getenv("COMPUTERNAME");
/*    */       }
/* 47 */       if (qualifiedHostName == null) {
/*    */         try {
/* 49 */           qualifiedHostName = HostName.getLocalHost().getHostName();
/* 50 */         } catch (UnknownHostException e) {
/* 51 */           qualifiedHostName = null;
/*    */         } 
/*    */       }
/* 54 */       if (qualifiedHostName != null && Pattern.compile("^\\d+\\.\\d+\\.\\d+\\.\\d+$|:").matcher(qualifiedHostName).find())
/*    */       {
/* 56 */         qualifiedHostName = null;
/*    */       }
/* 58 */       if (qualifiedHostName == null) {
/*    */         
/* 60 */         qualifiedHostName = "unknown-host.unknown-domain";
/*    */       } else {
/* 62 */         qualifiedHostName = qualifiedHostName.trim().toLowerCase();
/*    */       } 
/*    */     } 
/* 65 */     if (providedHostName == null) {
/*    */       
/* 67 */       int idx = qualifiedHostName.indexOf('.');
/* 68 */       providedHostName = (idx == -1) ? qualifiedHostName : qualifiedHostName.substring(0, idx);
/*    */     } 
/* 70 */     if (providedNodeName == null) {
/* 71 */       providedNodeName = providedHostName;
/*    */     }
/* 73 */     return new String[] { providedHostName, qualifiedHostName, providedNodeName };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\net\GetHostInfoAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */