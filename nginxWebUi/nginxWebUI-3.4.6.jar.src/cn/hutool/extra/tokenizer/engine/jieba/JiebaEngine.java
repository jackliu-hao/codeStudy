/*    */ package cn.hutool.extra.tokenizer.engine.jieba;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import com.huaban.analysis.jieba.JiebaSegmenter;
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
/*    */ public class JiebaEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final JiebaSegmenter jiebaSegmenter;
/*    */   private final JiebaSegmenter.SegMode mode;
/*    */   
/*    */   public JiebaEngine() {
/* 26 */     this(JiebaSegmenter.SegMode.SEARCH);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JiebaEngine(JiebaSegmenter.SegMode mode) {
/* 35 */     this.jiebaSegmenter = new JiebaSegmenter();
/* 36 */     this.mode = mode;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/* 41 */     return new JiebaResult(this.jiebaSegmenter.process(StrUtil.str(text), this.mode));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\jieba\JiebaEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */