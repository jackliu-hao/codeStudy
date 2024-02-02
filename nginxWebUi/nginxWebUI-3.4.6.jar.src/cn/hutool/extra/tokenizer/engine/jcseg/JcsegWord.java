/*    */ package cn.hutool.extra.tokenizer.engine.jcseg;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import org.lionsoul.jcseg.IWord;
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
/*    */ public class JcsegWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final IWord word;
/*    */   
/*    */   public JcsegWord(IWord word) {
/* 23 */     this.word = word;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 28 */     return this.word.getValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 33 */     return this.word.getPosition();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 38 */     return getStartOffset() + this.word.getLength();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 43 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\jcseg\JcsegWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */