/*    */ package io.undertow.util;
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
/*    */ class SimpleAttachmentKey<T>
/*    */   extends AttachmentKey<T>
/*    */ {
/*    */   private final Class<T> valueClass;
/*    */   
/*    */   SimpleAttachmentKey(Class<T> valueClass) {
/* 28 */     this.valueClass = valueClass;
/*    */   }
/*    */   
/*    */   public T cast(Object value) {
/* 32 */     return this.valueClass.cast(value);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 37 */     if (this.valueClass != null) {
/* 38 */       StringBuilder sb = new StringBuilder(getClass().getName());
/* 39 */       sb.append("<");
/* 40 */       sb.append(this.valueClass.getName());
/* 41 */       sb.append(">");
/* 42 */       return sb.toString();
/*    */     } 
/* 44 */     return super.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\SimpleAttachmentKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */