/*    */ package org.antlr.v4.runtime.misc;
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
/*    */ public class IntegerStack
/*    */   extends IntegerList
/*    */ {
/*    */   public IntegerStack() {}
/*    */   
/*    */   public IntegerStack(int capacity) {
/* 42 */     super(capacity);
/*    */   }
/*    */   
/*    */   public IntegerStack(IntegerStack list) {
/* 46 */     super(list);
/*    */   }
/*    */   
/*    */   public final void push(int value) {
/* 50 */     add(value);
/*    */   }
/*    */   
/*    */   public final int pop() {
/* 54 */     return removeAt(size() - 1);
/*    */   }
/*    */   
/*    */   public final int peek() {
/* 58 */     return get(size() - 1);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\IntegerStack.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */