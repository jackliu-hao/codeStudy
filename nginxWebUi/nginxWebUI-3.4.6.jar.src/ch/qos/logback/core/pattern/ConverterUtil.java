/*    */ package ch.qos.logback.core.pattern;
/*    */ 
/*    */ import ch.qos.logback.core.Context;
/*    */ import ch.qos.logback.core.spi.ContextAware;
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
/*    */ public class ConverterUtil
/*    */ {
/*    */   public static <E> void startConverters(Converter<E> head) {
/* 27 */     Converter<E> c = head;
/* 28 */     while (c != null) {
/*    */       
/* 30 */       if (c instanceof CompositeConverter) {
/* 31 */         CompositeConverter<E> cc = (CompositeConverter<E>)c;
/* 32 */         Converter<E> childConverter = cc.childConverter;
/* 33 */         startConverters(childConverter);
/* 34 */         cc.start();
/* 35 */       } else if (c instanceof DynamicConverter) {
/* 36 */         DynamicConverter<E> dc = (DynamicConverter<E>)c;
/* 37 */         dc.start();
/*    */       } 
/* 39 */       c = c.getNext();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static <E> Converter<E> findTail(Converter<E> head) {
/* 44 */     Converter<E> p = head;
/* 45 */     while (p != null) {
/* 46 */       Converter<E> next = p.getNext();
/* 47 */       if (next == null) {
/*    */         break;
/*    */       }
/* 50 */       p = next;
/*    */     } 
/*    */     
/* 53 */     return p;
/*    */   }
/*    */   
/*    */   public static <E> void setContextForConverters(Context context, Converter<E> head) {
/* 57 */     Converter<E> c = head;
/* 58 */     while (c != null) {
/* 59 */       if (c instanceof ContextAware) {
/* 60 */         ((ContextAware)c).setContext(context);
/*    */       }
/* 62 */       c = c.getNext();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\ConverterUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */