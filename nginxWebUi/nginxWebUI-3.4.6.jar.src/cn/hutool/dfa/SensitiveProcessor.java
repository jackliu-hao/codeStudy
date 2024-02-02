/*    */ package cn.hutool.dfa;
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
/*    */ public interface SensitiveProcessor
/*    */ {
/*    */   default String process(FoundWord foundWord) {
/* 15 */     int length = foundWord.getFoundWord().length();
/* 16 */     StringBuilder sb = new StringBuilder(length);
/* 17 */     for (int i = 0; i < length; i++) {
/* 18 */       sb.append("*");
/*    */     }
/* 20 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\dfa\SensitiveProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */