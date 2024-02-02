/*    */ package cn.hutool.extra.tokenizer.engine.jieba;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.huaban.analysis.jieba.SegToken;
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
/*    */ public class JiebaWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final SegToken segToken;
/*    */   
/*    */   public JiebaWord(SegToken segToken) {
/* 24 */     this.segToken = segToken;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 29 */     return this.segToken.word;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 34 */     return this.segToken.startOffset;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 39 */     return this.segToken.endOffset;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 44 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\jieba\JiebaWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */