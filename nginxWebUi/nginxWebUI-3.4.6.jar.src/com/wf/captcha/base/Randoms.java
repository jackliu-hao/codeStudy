/*    */ package com.wf.captcha.base;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Randoms
/*    */ {
/* 10 */   protected static final SecureRandom RANDOM = new SecureRandom();
/*    */   
/* 12 */   public static final char[] ALPHA = new char[] { '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
/*    */   
/*    */   protected static final int numMaxIndex = 8;
/*    */   
/*    */   protected static final int charMinIndex = 8;
/* 17 */   protected static final int charMaxIndex = ALPHA.length;
/*    */   protected static final int upperMinIndex = 8;
/*    */   protected static final int upperMaxIndex = 31;
/*    */   protected static final int lowerMinIndex = 31;
/* 21 */   protected static final int lowerMaxIndex = charMaxIndex;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int num(int min, int max) {
/* 31 */     return min + RANDOM.nextInt(max - min);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int num(int num) {
/* 41 */     return RANDOM.nextInt(num);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static char alpha() {
/* 50 */     return ALPHA[num(ALPHA.length)];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static char alpha(int num) {
/* 60 */     return ALPHA[num(num)];
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static char alpha(int min, int max) {
/* 71 */     return ALPHA[num(min, max)];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\base\Randoms.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */