/*    */ package cn.hutool.extra.tokenizer.engine.word;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
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
/*    */ public class WordWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Word word;
/*    */   
/*    */   public WordWord(Word word) {
/* 22 */     this.word = word;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 27 */     return this.word.getText();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 32 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 37 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 42 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\word\WordWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */