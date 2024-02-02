/*    */ package cn.hutool.extra.tokenizer.engine.analysis;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.AbstractResult;
/*    */ import cn.hutool.extra.tokenizer.TokenizerException;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.TokenStream;
/*    */ import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
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
/*    */ 
/*    */ public class AnalysisResult
/*    */   extends AbstractResult
/*    */ {
/*    */   private final TokenStream stream;
/*    */   
/*    */   public AnalysisResult(TokenStream stream) {
/* 29 */     this.stream = stream;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Word nextWord() {
/*    */     try {
/* 35 */       if (this.stream.incrementToken()) {
/* 36 */         return new AnalysisWord((CharTermAttribute)this.stream.getAttribute(CharTermAttribute.class));
/*    */       }
/* 38 */     } catch (IOException e) {
/* 39 */       throw new TokenizerException(e);
/*    */     } 
/* 41 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\analysis\AnalysisResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */