/*    */ package cn.hutool.extra.tokenizer.engine.ansj;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import org.ansj.splitWord.Analysis;
/*    */ import org.ansj.splitWord.analysis.ToAnalysis;
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
/*    */ public class AnsjEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final Analysis analysis;
/*    */   
/*    */   public AnsjEngine() {
/* 25 */     this((Analysis)new ToAnalysis());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AnsjEngine(Analysis analysis) {
/* 34 */     this.analysis = analysis;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/* 39 */     return new AnsjResult(this.analysis.parseStr(StrUtil.str(text)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\ansj\AnsjEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */