/*    */ package cn.hutool.extra.tokenizer.engine.word;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.apdplat.word.segmentation.Word;
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
/*    */ public class WordResult
/*    */   implements Result
/*    */ {
/*    */   private final Iterator<Word> wordIter;
/*    */   
/*    */   public WordResult(List<Word> result) {
/* 26 */     this.wordIter = result.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 31 */     return this.wordIter.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public Word next() {
/* 36 */     return new WordWord(this.wordIter.next());
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 41 */     this.wordIter.remove();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\word\WordResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */