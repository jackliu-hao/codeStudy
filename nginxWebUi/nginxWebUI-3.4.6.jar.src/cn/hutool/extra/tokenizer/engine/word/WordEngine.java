/*    */ package cn.hutool.extra.tokenizer.engine.word;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import org.apdplat.word.segmentation.Segmentation;
/*    */ import org.apdplat.word.segmentation.SegmentationAlgorithm;
/*    */ import org.apdplat.word.segmentation.SegmentationFactory;
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
/*    */ public class WordEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final Segmentation segmentation;
/*    */   
/*    */   public WordEngine() {
/* 26 */     this(SegmentationAlgorithm.BidirectionalMaximumMatching);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WordEngine(SegmentationAlgorithm algorithm) {
/* 35 */     this(SegmentationFactory.getSegmentation(algorithm));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WordEngine(Segmentation segmentation) {
/* 44 */     this.segmentation = segmentation;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/* 49 */     return new WordResult(this.segmentation.seg(StrUtil.str(text)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\word\WordEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */