/*    */ package cn.hutool.extra.tokenizer;
/*    */ 
/*    */ import cn.hutool.core.collection.ComputeIter;
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
/*    */ public abstract class AbstractResult
/*    */   extends ComputeIter<Word>
/*    */   implements Result
/*    */ {
/*    */   protected Word computeNext() {
/* 24 */     return nextWord();
/*    */   }
/*    */   
/*    */   protected abstract Word nextWord();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\AbstractResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */