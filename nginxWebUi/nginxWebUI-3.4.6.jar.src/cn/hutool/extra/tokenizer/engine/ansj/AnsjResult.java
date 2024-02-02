/*    */ package cn.hutool.extra.tokenizer.engine.ansj;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import java.util.Iterator;
/*    */ import org.ansj.domain.Result;
/*    */ import org.ansj.domain.Term;
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
/*    */ public class AnsjResult
/*    */   implements Result
/*    */ {
/*    */   private final Iterator<Term> result;
/*    */   
/*    */   public AnsjResult(Result ansjResult) {
/* 25 */     this.result = ansjResult.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 30 */     return this.result.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public Word next() {
/* 35 */     return new AnsjWord(this.result.next());
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 40 */     this.result.remove();
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<Word> iterator() {
/* 45 */     return (Iterator<Word>)this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\ansj\AnsjResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */