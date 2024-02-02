/*    */ package cn.hutool.extra.tokenizer.engine.ikanalyzer;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import org.wltea.analyzer.core.Lexeme;
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
/*    */ public class IKAnalyzerWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Lexeme word;
/*    */   
/*    */   public IKAnalyzerWord(Lexeme word) {
/* 24 */     this.word = word;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 29 */     return this.word.getLexemeText();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 34 */     return this.word.getBeginPosition();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 39 */     return this.word.getEndPosition();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\ikanalyzer\IKAnalyzerWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */