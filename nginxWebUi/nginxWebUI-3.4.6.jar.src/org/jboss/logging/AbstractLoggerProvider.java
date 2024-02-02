/*    */ package org.jboss.logging;
/*    */ 
/*    */ import java.util.ArrayDeque;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ abstract class AbstractLoggerProvider
/*    */ {
/* 25 */   private final ThreadLocal<ArrayDeque<Entry>> ndcStack = new ThreadLocal<>();
/*    */   
/*    */   public void clearNdc() {
/* 28 */     ArrayDeque<Entry> stack = this.ndcStack.get();
/* 29 */     if (stack != null)
/* 30 */       stack.clear(); 
/*    */   }
/*    */   
/*    */   public String getNdc() {
/* 34 */     ArrayDeque<Entry> stack = this.ndcStack.get();
/* 35 */     return (stack == null || stack.isEmpty()) ? null : (stack.peek()).merged;
/*    */   }
/*    */   
/*    */   public int getNdcDepth() {
/* 39 */     ArrayDeque<Entry> stack = this.ndcStack.get();
/* 40 */     return (stack == null) ? 0 : stack.size();
/*    */   }
/*    */   
/*    */   public String peekNdc() {
/* 44 */     ArrayDeque<Entry> stack = this.ndcStack.get();
/* 45 */     return (stack == null || stack.isEmpty()) ? "" : (stack.peek()).current;
/*    */   }
/*    */   
/*    */   public String popNdc() {
/* 49 */     ArrayDeque<Entry> stack = this.ndcStack.get();
/* 50 */     return (stack == null || stack.isEmpty()) ? "" : (stack.pop()).current;
/*    */   }
/*    */   
/*    */   public void pushNdc(String message) {
/* 54 */     ArrayDeque<Entry> stack = this.ndcStack.get();
/* 55 */     if (stack == null) {
/* 56 */       stack = new ArrayDeque<>();
/* 57 */       this.ndcStack.set(stack);
/*    */     } 
/* 59 */     stack.push(stack.isEmpty() ? new Entry(message) : new Entry(stack.peek(), message));
/*    */   }
/*    */   
/*    */   public void setNdcMaxDepth(int maxDepth) {
/* 63 */     ArrayDeque<Entry> stack = this.ndcStack.get();
/* 64 */     if (stack != null) for (; stack.size() > maxDepth; stack.pop()); 
/*    */   }
/*    */   
/*    */   private static class Entry
/*    */   {
/*    */     private String merged;
/*    */     private String current;
/*    */     
/*    */     Entry(String current) {
/* 73 */       this.merged = current;
/* 74 */       this.current = current;
/*    */     }
/*    */     
/*    */     Entry(Entry parent, String current) {
/* 78 */       parent.merged += ' ' + current;
/* 79 */       this.current = current;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\AbstractLoggerProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */