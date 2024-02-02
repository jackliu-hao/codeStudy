/*    */ package cn.hutool.extra.tokenizer.engine.mmseg;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.chenlb.mmseg4j.Word;
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
/*    */ public class MmsegWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Word word;
/*    */   
/*    */   public MmsegWord(Word word) {
/* 22 */     this.word = word;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 27 */     return this.word.getString();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 32 */     return this.word.getStartOffset();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 37 */     return this.word.getEndOffset();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 42 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\mmseg\MmsegWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */