/*    */ package cn.hutool.extra.tokenizer.engine.ansj;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import org.ansj.domain.Term;
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
/*    */ public class AnsjWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Term term;
/*    */   
/*    */   public AnsjWord(Term term) {
/* 24 */     this.term = term;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 29 */     return this.term.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 34 */     return this.term.getOffe();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 39 */     return getStartOffset() + getText().length();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\ansj\AnsjWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */