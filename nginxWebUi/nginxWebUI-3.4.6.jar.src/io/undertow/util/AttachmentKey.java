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
/*    */ public abstract class AttachmentKey<T>
/*    */ {
/*    */   public abstract T cast(Object paramObject);
/*    */   
/*    */   public static <T> AttachmentKey<T> create(Class<? super T> valueClass) {
/* 51 */     return new SimpleAttachmentKey<>((Class)valueClass);
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
/*    */   public static <T> AttachmentKey<AttachmentList<T>> createList(Class<? super T> valueClass) {
/* 63 */     return new ListAttachmentKey<>((Class)valueClass);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\AttachmentKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */