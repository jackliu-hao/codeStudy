/*    */ package cn.hutool.extra.tokenizer.engine.jcseg;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.AbstractResult;
/*    */ import cn.hutool.extra.tokenizer.TokenizerException;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import java.io.IOException;
/*    */ import org.lionsoul.jcseg.ISegment;
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
/*    */ 
/*    */ public class JcsegResult
/*    */   extends AbstractResult
/*    */ {
/*    */   private final ISegment result;
/*    */   
/*    */   public JcsegResult(ISegment segment) {
/* 27 */     this.result = segment;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Word nextWord() {
/*    */     IWord word;
/*    */     try {
/* 34 */       word = this.result.next();
/* 35 */     } catch (IOException e) {
/* 36 */       throw new TokenizerException(e);
/*    */     } 
/* 38 */     if (null == word) {
/* 39 */       return null;
/*    */     }
/* 41 */     return new JcsegWord(word);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\jcseg\JcsegResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */