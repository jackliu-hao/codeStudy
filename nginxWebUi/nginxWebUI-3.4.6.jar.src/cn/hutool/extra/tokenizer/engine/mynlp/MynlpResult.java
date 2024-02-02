/*    */ package cn.hutool.extra.tokenizer.engine.mynlp;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.mayabot.nlp.segment.Sentence;
/*    */ import com.mayabot.nlp.segment.WordTerm;
/*    */ import java.util.Iterator;
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
/*    */ public class MynlpResult
/*    */   implements Result
/*    */ {
/*    */   private final Iterator<WordTerm> result;
/*    */   
/*    */   public MynlpResult(Sentence sentence) {
/* 27 */     this.result = sentence.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 32 */     return this.result.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public Word next() {
/* 37 */     return new MynlpWord(this.result.next());
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 42 */     this.result.remove();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\mynlp\MynlpResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */