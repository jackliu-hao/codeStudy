/*    */ package cn.hutool.extra.tokenizer.engine.ikanalyzer;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.AbstractResult;
/*    */ import cn.hutool.extra.tokenizer.TokenizerException;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import java.io.IOException;
/*    */ import org.wltea.analyzer.core.IKSegmenter;
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
/*    */ 
/*    */ public class IKAnalyzerResult
/*    */   extends AbstractResult
/*    */ {
/*    */   private final IKSegmenter seg;
/*    */   
/*    */   public IKAnalyzerResult(IKSegmenter seg) {
/* 28 */     this.seg = seg;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Word nextWord() {
/*    */     Lexeme next;
/*    */     try {
/* 35 */       next = this.seg.next();
/* 36 */     } catch (IOException e) {
/* 37 */       throw new TokenizerException(e);
/*    */     } 
/* 39 */     if (null == next) {
/* 40 */       return null;
/*    */     }
/* 42 */     return new IKAnalyzerWord(next);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\ikanalyzer\IKAnalyzerResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */