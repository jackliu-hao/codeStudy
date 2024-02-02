/*    */ package io.undertow.server.handlers.resource;
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
/*    */ public class ResourceChangeEvent
/*    */ {
/*    */   private final String resource;
/*    */   private final Type type;
/*    */   
/*    */   public ResourceChangeEvent(String resource, Type type) {
/* 32 */     this.resource = resource;
/* 33 */     this.type = type;
/*    */   }
/*    */   
/*    */   public String getResource() {
/* 37 */     return this.resource;
/*    */   }
/*    */   
/*    */   public Type getType() {
/* 41 */     return this.type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public enum Type
/*    */   {
/* 51 */     ADDED,
/*    */ 
/*    */ 
/*    */     
/* 55 */     REMOVED,
/*    */ 
/*    */ 
/*    */     
/* 59 */     MODIFIED;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\ResourceChangeEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */