/*    */ package org.xnio;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ public abstract class TranslatingResult<T, O>
/*    */   implements Result<T>
/*    */ {
/*    */   private final Result<O> output;
/*    */   
/*    */   protected TranslatingResult(Result<O> output) {
/* 33 */     this.output = output;
/*    */   }
/*    */   
/*    */   public boolean setException(IOException exception) {
/* 37 */     return this.output.setException(exception);
/*    */   }
/*    */   
/*    */   public boolean setCancelled() {
/* 41 */     return this.output.setCancelled();
/*    */   }
/*    */   
/*    */   public boolean setResult(T result) {
/*    */     try {
/* 46 */       return this.output.setResult(translate(result));
/* 47 */     } catch (IOException e) {
/* 48 */       return this.output.setException(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   protected abstract O translate(T paramT) throws IOException;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\TranslatingResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */