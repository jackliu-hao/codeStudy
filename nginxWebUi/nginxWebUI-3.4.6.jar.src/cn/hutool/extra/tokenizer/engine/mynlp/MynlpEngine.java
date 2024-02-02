/*    */ package cn.hutool.extra.tokenizer.engine.mynlp;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.TokenizerEngine;
/*    */ import com.mayabot.nlp.segment.Lexer;
/*    */ import com.mayabot.nlp.segment.Lexers;
/*    */ import com.mayabot.nlp.segment.Sentence;
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
/*    */ public class MynlpEngine
/*    */   implements TokenizerEngine
/*    */ {
/*    */   private final Lexer lexer;
/*    */   
/*    */   public MynlpEngine() {
/* 26 */     this.lexer = Lexers.core();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MynlpEngine(Lexer lexer) {
/* 35 */     this.lexer = lexer;
/*    */   }
/*    */ 
/*    */   
/*    */   public Result parse(CharSequence text) {
/* 40 */     Sentence sentence = this.lexer.scan(StrUtil.str(text));
/* 41 */     return new MynlpResult(sentence);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\mynlp\MynlpEngine.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */