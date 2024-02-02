/*    */ package com.mysql.cj.conf.url;
/*    */ 
/*    */ import com.mysql.cj.conf.ConnectionUrl;
/*    */ import com.mysql.cj.conf.ConnectionUrlParser;
/*    */ import java.util.Properties;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FailoverConnectionUrl
/*    */   extends ConnectionUrl
/*    */ {
/*    */   public FailoverConnectionUrl(ConnectionUrlParser connStrParser, Properties info) {
/* 47 */     super(connStrParser, info);
/* 48 */     this.type = ConnectionUrl.Type.FAILOVER_CONNECTION;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\con\\url\FailoverConnectionUrl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */