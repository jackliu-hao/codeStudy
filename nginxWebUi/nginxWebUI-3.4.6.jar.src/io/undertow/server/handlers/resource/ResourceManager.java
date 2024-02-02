/*    */ package io.undertow.server.handlers.resource;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
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
/*    */ public interface ResourceManager
/*    */   extends Closeable
/*    */ {
/* 64 */   public static final ResourceManager EMPTY_RESOURCE_MANAGER = new ResourceManager()
/*    */     {
/*    */       public Resource getResource(String path) {
/* 67 */         return null;
/*    */       }
/*    */ 
/*    */       
/*    */       public boolean isResourceChangeListenerSupported() {
/* 72 */         return false;
/*    */       }
/*    */ 
/*    */       
/*    */       public void registerResourceChangeListener(ResourceChangeListener listener) {
/* 77 */         throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
/*    */       }
/*    */ 
/*    */       
/*    */       public void removeResourceChangeListener(ResourceChangeListener listener) {
/* 82 */         throw UndertowMessages.MESSAGES.resourceChangeListenerNotSupported();
/*    */       }
/*    */       
/*    */       public void close() throws IOException {}
/*    */     };
/*    */   
/*    */   Resource getResource(String paramString) throws IOException;
/*    */   
/*    */   boolean isResourceChangeListenerSupported();
/*    */   
/*    */   void registerResourceChangeListener(ResourceChangeListener paramResourceChangeListener);
/*    */   
/*    */   void removeResourceChangeListener(ResourceChangeListener paramResourceChangeListener);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\ResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */