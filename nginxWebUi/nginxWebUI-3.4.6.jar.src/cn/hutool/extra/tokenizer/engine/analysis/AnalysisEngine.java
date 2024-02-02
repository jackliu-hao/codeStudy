/*    */ package cn.hutool.extra.tokenizer.engine.analysis;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import cn.hutool.extra.tokenizer.TokenizerException;
/*    */ import java.io.IOException;
/*    */ import org.apache.lucene.analysis.Analyzer;
/*    */ import org.apache.lucene.analysis.TokenStream;
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
/*    */ public class AnalysisEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final Analyzer analyzer;
/*    */   
/*    */   public AnalysisEngine(Analyzer analyzer) {
/* 30 */     this.analyzer = analyzer;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/*    */     TokenStream stream;
/*    */     try {
/* 37 */       stream = this.analyzer.tokenStream("text", StrUtil.str(text));
/* 38 */       stream.reset();
/* 39 */     } catch (IOException e) {
/* 40 */       throw new TokenizerException(e);
/*    */     } 
/* 42 */     return (Result)new AnalysisResult(stream);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\analysis\AnalysisEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */