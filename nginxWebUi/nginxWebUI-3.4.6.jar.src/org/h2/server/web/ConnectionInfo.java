/*    */ package org.h2.server.web;
/*    */ 
/*    */ import org.h2.util.StringUtils;
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
/*    */ public class ConnectionInfo
/*    */   implements Comparable<ConnectionInfo>
/*    */ {
/*    */   public String driver;
/*    */   public String url;
/*    */   public String user;
/*    */   String name;
/*    */   int lastAccess;
/*    */   
/*    */   ConnectionInfo() {}
/*    */   
/*    */   public ConnectionInfo(String paramString) {
/* 46 */     String[] arrayOfString = StringUtils.arraySplit(paramString, '|', false);
/* 47 */     this.name = get(arrayOfString, 0);
/* 48 */     this.driver = get(arrayOfString, 1);
/* 49 */     this.url = get(arrayOfString, 2);
/* 50 */     this.user = get(arrayOfString, 3);
/*    */   }
/*    */   
/*    */   private static String get(String[] paramArrayOfString, int paramInt) {
/* 54 */     return (paramArrayOfString != null && paramArrayOfString.length > paramInt) ? paramArrayOfString[paramInt] : "";
/*    */   }
/*    */   
/*    */   String getString() {
/* 58 */     return StringUtils.arrayCombine(new String[] { this.name, this.driver, this.url, this.user }, '|');
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(ConnectionInfo paramConnectionInfo) {
/* 63 */     return Integer.compare(paramConnectionInfo.lastAccess, this.lastAccess);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\web\ConnectionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */