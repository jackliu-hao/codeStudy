/*    */ package cn.hutool.extra.tokenizer.engine.ikanalyzer;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import org.wltea.analyzer.core.IKSegmenter;
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
/*    */ public class IKAnalyzerEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final IKSegmenter seg;
/*    */   
/*    */   public IKAnalyzerEngine() {
/* 25 */     this(new IKSegmenter(null, true));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IKAnalyzerEngine(IKSegmenter seg) {
/* 34 */     this.seg = seg;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/* 39 */     this.seg.reset(StrUtil.getReader(text));
/* 40 */     return (Result)new IKAnalyzerResult(this.seg);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\ikanalyzer\IKAnalyzerEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */