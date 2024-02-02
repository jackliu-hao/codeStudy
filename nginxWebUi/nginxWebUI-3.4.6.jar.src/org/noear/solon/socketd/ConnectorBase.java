/*    */ package org.noear.solon.socketd;
/*    */ 
/*    */ import java.net.URI;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ConnectorBase<T>
/*    */   implements Connector<T>
/*    */ {
/*    */   private URI uri;
/*    */   private boolean autoReconnect;
/*    */   
/*    */   public ConnectorBase(URI uri, boolean autoReconnect) {
/* 16 */     this.uri = uri;
/* 17 */     this.autoReconnect = autoReconnect;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public URI uri() {
/* 25 */     return this.uri;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean autoReconnect() {
/* 33 */     return this.autoReconnect;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\ConnectorBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */