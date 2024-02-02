/*    */ package cn.hutool.extra.tokenizer.engine.hanlp;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import com.hankcs.hanlp.HanLP;
/*    */ import com.hankcs.hanlp.seg.Segment;
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
/*    */ public class HanLPEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final Segment seg;
/*    */   
/*    */   public HanLPEngine() {
/* 26 */     this(HanLP.newSegment());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HanLPEngine(Segment seg) {
/* 35 */     this.seg = seg;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/* 40 */     return new HanLPResult(this.seg.seg(StrUtil.str(text)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\hanlp\HanLPEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */