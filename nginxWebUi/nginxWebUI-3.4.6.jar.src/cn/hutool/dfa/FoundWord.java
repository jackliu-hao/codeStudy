/*    */ package cn.hutool.dfa;
/*    */ 
/*    */ import cn.hutool.core.lang.DefaultSegment;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FoundWord
/*    */   extends DefaultSegment<Integer>
/*    */ {
/*    */   private final String word;
/*    */   private final String foundWord;
/*    */   
/*    */   public FoundWord(String word, String foundWord, int startIndex, int endIndex) {
/* 31 */     super(Integer.valueOf(startIndex), Integer.valueOf(endIndex));
/* 32 */     this.word = word;
/* 33 */     this.foundWord = foundWord;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getWord() {
/* 42 */     return this.word;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getFoundWord() {
/* 50 */     return this.foundWord;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return this.foundWord;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\dfa\FoundWord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */