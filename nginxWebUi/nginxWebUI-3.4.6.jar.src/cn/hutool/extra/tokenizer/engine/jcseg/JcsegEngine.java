/*    */ package cn.hutool.extra.tokenizer.engine.jcseg;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import cn.hutool.extra.tokenizer.TokenizerException;
/*    */ import java.io.IOException;
/*    */ import java.io.StringReader;
/*    */ import org.lionsoul.jcseg.ISegment;
/*    */ import org.lionsoul.jcseg.dic.ADictionary;
/*    */ import org.lionsoul.jcseg.dic.DictionaryFactory;
/*    */ import org.lionsoul.jcseg.segmenter.SegmenterConfig;
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
/*    */ public class JcsegEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final ISegment segment;
/*    */   
/*    */   public JcsegEngine() {
/* 31 */     SegmenterConfig config = new SegmenterConfig(true);
/*    */     
/* 33 */     ADictionary dic = DictionaryFactory.createSingletonDictionary(config);
/*    */ 
/*    */     
/* 36 */     this.segment = ISegment.COMPLEX.factory.create(config, dic);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JcsegEngine(ISegment segment) {
/* 45 */     this.segment = segment;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/*    */     try {
/* 51 */       this.segment.reset(new StringReader(StrUtil.str(text)));
/* 52 */     } catch (IOException e) {
/* 53 */       throw new TokenizerException(e);
/*    */     } 
/* 55 */     return (Result)new JcsegResult(this.segment);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\jcseg\JcsegEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */