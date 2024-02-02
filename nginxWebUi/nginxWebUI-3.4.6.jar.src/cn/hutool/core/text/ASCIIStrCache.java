/*    */ package cn.hutool.core.text;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ASCIIStrCache
/*    */ {
/*    */   private static final int ASCII_LENGTH = 128;
/* 13 */   private static final String[] CACHE = new String[128];
/*    */   static {
/* 15 */     for (char c = Character.MIN_VALUE; c < ''; c = (char)(c + 1)) {
/* 16 */       CACHE[c] = String.valueOf(c);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String toString(char c) {
/* 28 */     return (c < '') ? CACHE[c] : String.valueOf(c);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\ASCIIStrCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */