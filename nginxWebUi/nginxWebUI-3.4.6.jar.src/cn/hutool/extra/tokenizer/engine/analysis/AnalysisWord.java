/*    */ package cn.hutool.extra.tokenizer.engine.analysis;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
/*    */ import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
/*    */ import org.apache.lucene.util.Attribute;
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
/*    */ public class AnalysisWord
/*    */   implements Word
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Attribute word;
/*    */   
/*    */   public AnalysisWord(CharTermAttribute word) {
/* 26 */     this.word = (Attribute)word;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getText() {
/* 31 */     return this.word.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public int getStartOffset() {
/* 36 */     if (this.word instanceof OffsetAttribute) {
/* 37 */       return ((OffsetAttribute)this.word).startOffset();
/*    */     }
/* 39 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getEndOffset() {
/* 44 */     if (this.word instanceof OffsetAttribute) {
/* 45 */       return ((OffsetAttribute)this.word).endOffset();
/*    */     }
/* 47 */     return -1;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 52 */     return getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\analysis\AnalysisWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */