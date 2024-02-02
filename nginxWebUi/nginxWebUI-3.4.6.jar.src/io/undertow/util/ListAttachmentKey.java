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
/*    */ class ListAttachmentKey<T>
/*    */   extends AttachmentKey<AttachmentList<T>>
/*    */ {
/*    */   private final Class<T> valueClass;
/*    */   
/*    */   ListAttachmentKey(Class<T> valueClass) {
/* 72 */     this.valueClass = valueClass;
/*    */   }
/*    */ 
/*    */   
/*    */   public AttachmentList<T> cast(Object value) {
/* 77 */     if (value == null) {
/* 78 */       return null;
/*    */     }
/* 80 */     AttachmentList<?> list = (AttachmentList)value;
/* 81 */     Class<?> listValueClass = list.getValueClass();
/* 82 */     if (listValueClass != this.valueClass) {
/* 83 */       throw new ClassCastException();
/*    */     }
/* 85 */     return (AttachmentList)list;
/*    */   }
/*    */   
/*    */   Class<T> getValueClass() {
/* 89 */     return this.valueClass;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\ListAttachmentKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */