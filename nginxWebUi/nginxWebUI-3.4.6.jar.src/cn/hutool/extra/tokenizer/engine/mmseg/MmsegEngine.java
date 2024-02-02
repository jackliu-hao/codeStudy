/*    */ package cn.hutool.extra.tokenizer.engine.mmseg;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import com.chenlb.mmseg4j.ComplexSeg;
/*    */ import com.chenlb.mmseg4j.Dictionary;
/*    */ import com.chenlb.mmseg4j.MMSeg;
/*    */ import com.chenlb.mmseg4j.Seg;
/*    */ import java.io.StringReader;
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
/*    */ public class MmsegEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final MMSeg mmSeg;
/*    */   
/*    */   public MmsegEngine() {
/* 27 */     Dictionary dict = Dictionary.getInstance();
/* 28 */     ComplexSeg seg = new ComplexSeg(dict);
/* 29 */     this.mmSeg = new MMSeg(new StringReader(""), (Seg)seg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MmsegEngine(MMSeg mmSeg) {
/* 38 */     this.mmSeg = mmSeg;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/* 43 */     this.mmSeg.reset(StrUtil.getReader(text));
/* 44 */     return (Result)new MmsegResult(this.mmSeg);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\mmseg\MmsegEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */