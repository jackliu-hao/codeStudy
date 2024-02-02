/*    */ package freemarker.core;
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
/*    */ final class LocalContextStack
/*    */ {
/* 28 */   private LocalContext[] buffer = new LocalContext[8];
/*    */   private int size;
/*    */   
/*    */   void push(LocalContext localContext) {
/* 32 */     int newSize = ++this.size;
/* 33 */     LocalContext[] buffer = this.buffer;
/* 34 */     if (buffer.length < newSize) {
/* 35 */       LocalContext[] newBuffer = new LocalContext[newSize * 2];
/* 36 */       for (int i = 0; i < buffer.length; i++) {
/* 37 */         newBuffer[i] = buffer[i];
/*    */       }
/* 39 */       buffer = newBuffer;
/* 40 */       this.buffer = newBuffer;
/*    */     } 
/* 42 */     buffer[newSize - 1] = localContext;
/*    */   }
/*    */   
/*    */   void pop() {
/* 46 */     this.buffer[--this.size] = null;
/*    */   }
/*    */   
/*    */   public LocalContext get(int index) {
/* 50 */     return this.buffer[index];
/*    */   }
/*    */   
/*    */   public int size() {
/* 54 */     return this.size;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\LocalContextStack.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */