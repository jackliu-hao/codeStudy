/*    */ package cn.hutool.extra.tokenizer.engine.hanlp;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.hankcs.hanlp.seg.common.Term;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HanLPResult
/*    */   implements Result
/*    */ {
/*    */   Iterator<Term> result;
/*    */   
/*    */   public HanLPResult(List<Term> termList) {
/* 22 */     this.result = termList.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 27 */     return this.result.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public Word next() {
/* 32 */     return new HanLPWord(this.result.next());
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 37 */     this.result.remove();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\hanlp\HanLPResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */