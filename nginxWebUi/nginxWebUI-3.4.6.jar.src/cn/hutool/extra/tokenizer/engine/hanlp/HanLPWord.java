/*    */ package cn.hutool.extra.tokenizer.engine.hanlp;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.hankcs.hanlp.seg.common.Term;
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
/*    */ public class HanLPWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Term term;
/*    */   
/*    */   public HanLPWord(Term term) {
/* 24 */     this.term = term;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 29 */     return this.term.word;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 34 */     return this.term.offset;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 39 */     return getStartOffset() + this.term.length();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\hanlp\HanLPWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */