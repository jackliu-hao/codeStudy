/*    */ package cn.hutool.extra.tokenizer.engine.jieba;
/*    */ 
/*    */ import cn.hutool.extra.tokenizer.Result;
/*    */ import cn.hutool.extra.tokenizer.Word;
/*    */ import com.huaban.analysis.jieba.SegToken;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JiebaResult
/*    */   implements Result
/*    */ {
/*    */   Iterator<SegToken> result;
/*    */   
/*    */   public JiebaResult(List<SegToken> segTokenList) {
/* 26 */     this.result = segTokenList.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean hasNext() {
/* 31 */     return this.result.hasNext();
/*    */   }
/*    */ 
/*    */   
/*    */   public Word next() {
/* 36 */     return new JiebaWord(this.result.next());
/*    */   }
/*    */ 
/*    */   
/*    */   public void remove() {
/* 41 */     this.result.remove();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\tokenizer\engine\jieba\JiebaResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */