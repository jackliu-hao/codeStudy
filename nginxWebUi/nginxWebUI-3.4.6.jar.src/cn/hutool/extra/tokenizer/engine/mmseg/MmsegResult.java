/*    */ package cn.hutool.extra.tokenizer.engine.mmseg;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.AbstractResult;
/*    */ import cn.hutool.extra.tokenizer.TokenizerException;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.chenlb.mmseg4j.MMSeg;
/*    */ import com.chenlb.mmseg4j.Word;
/*    */ import java.io.IOException;
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
/*    */ public class MmsegResult
/*    */   extends AbstractResult
/*    */ {
/*    */   private final MMSeg mmSeg;
/*    */   
/*    */   public MmsegResult(MMSeg mmSeg) {
/* 27 */     this.mmSeg = mmSeg;
/*    */   }
/*    */ 
/*    */   
/*    */   protected Word nextWord() {
/*    */     Word next;
/*    */     try {
/* 34 */       next = this.mmSeg.next();
/* 35 */     } catch (IOException e) {
/* 36 */       throw new TokenizerException(e);
/*    */     } 
/* 38 */     if (null == next) {
/* 39 */       return null;
/*    */     }
/* 41 */     return new MmsegWord(next);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\mmseg\MmsegResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */