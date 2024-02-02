/*    */ package io.undertow.server;
/*    */ 
/*    */ import io.undertow.util.AttachmentKey;
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
/*    */ public interface DefaultResponseListener
/*    */ {
/* 35 */   public static final AttachmentKey<Throwable> EXCEPTION = AttachmentKey.create(Throwable.class);
/*    */   
/*    */   boolean handleDefaultResponse(HttpServerExchange paramHttpServerExchange);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\DefaultResponseListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */