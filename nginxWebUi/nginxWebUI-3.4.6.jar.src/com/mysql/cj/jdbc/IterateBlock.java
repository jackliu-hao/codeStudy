/*    */ package com.mysql.cj.jdbc;
/*    */ 
/*    */ import java.sql.SQLException;
/*    */ import java.util.Iterator;
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
/*    */ public abstract class IterateBlock<T>
/*    */ {
/*    */   DatabaseMetaData.IteratorWithCleanup<T> iteratorWithCleanup;
/*    */   Iterator<T> javaIterator;
/*    */   boolean stopIterating = false;
/*    */   
/*    */   IterateBlock(DatabaseMetaData.IteratorWithCleanup<T> i) {
/* 43 */     this.iteratorWithCleanup = i;
/* 44 */     this.javaIterator = null;
/*    */   }
/*    */   
/*    */   IterateBlock(Iterator<T> i) {
/* 48 */     this.javaIterator = i;
/* 49 */     this.iteratorWithCleanup = null;
/*    */   }
/*    */   
/*    */   public void doForAll() throws SQLException {
/* 53 */     if (this.iteratorWithCleanup != null) {
/*    */       try {
/* 55 */         while (this.iteratorWithCleanup.hasNext()) {
/* 56 */           forEach(this.iteratorWithCleanup.next());
/*    */           
/* 58 */           if (this.stopIterating) {
/*    */             break;
/*    */           }
/*    */         } 
/*    */       } finally {
/* 63 */         this.iteratorWithCleanup.close();
/*    */       } 
/*    */     } else {
/* 66 */       while (this.javaIterator.hasNext()) {
/* 67 */         forEach(this.javaIterator.next());
/*    */         
/* 69 */         if (this.stopIterating) {
/*    */           break;
/*    */         }
/*    */       } 
/*    */     } 
/*    */   }
/*    */   
/*    */   abstract void forEach(T paramT) throws SQLException;
/*    */   
/*    */   public final boolean fullIteration() {
/* 79 */     return !this.stopIterating;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\IterateBlock.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */