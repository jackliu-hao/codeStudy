/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
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
/*    */ public interface FetchResult<T>
/*    */   extends Iterator<T>, Iterable<T>
/*    */ {
/*    */   default boolean hasData() {
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default T fetchOne() {
/* 58 */     if (hasNext()) {
/* 59 */       return next();
/*    */     }
/* 61 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   default Iterator<T> iterator() {
/* 70 */     return fetchAll().iterator();
/*    */   }
/*    */   
/*    */   long count();
/*    */   
/*    */   List<T> fetchAll();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\FetchResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */