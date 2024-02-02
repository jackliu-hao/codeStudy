/*    */ package cn.hutool.extra.tokenizer.engine.mynlp;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.mayabot.nlp.segment.WordTerm;
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
/*    */ public class MynlpWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final WordTerm word;
/*    */   
/*    */   public MynlpWord(WordTerm word) {
/* 23 */     this.word = word;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 28 */     return this.word.getWord();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 33 */     return this.word.offset;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 38 */     return getStartOffset() + this.word.word.length();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 43 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\mynlp\MynlpWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */