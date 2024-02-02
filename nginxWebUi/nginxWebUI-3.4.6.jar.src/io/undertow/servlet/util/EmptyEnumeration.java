/*    */ package io.undertow.servlet.util;
/*    */ 
/*    */ import java.util.Enumeration;
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
/*    */ public class EmptyEnumeration
/*    */   implements Enumeration<Object>
/*    */ {
/* 28 */   private static final Enumeration<?> INSTANCE = new EmptyEnumeration();
/*    */ 
/*    */   
/*    */   public static <T> Enumeration<T> instance() {
/* 32 */     return (Enumeration)INSTANCE;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean hasMoreElements() {
/* 41 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object nextElement() {
/* 46 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\EmptyEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */