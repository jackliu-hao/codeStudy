/*    */ package cn.hutool.core.codec;
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
/*    */ public class Caesar
/*    */ {
/*    */   public static final String TABLE = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
/*    */   
/*    */   public static String encode(String message, int offset) {
/* 22 */     int len = message.length();
/* 23 */     char[] plain = message.toCharArray();
/*    */     
/* 25 */     for (int i = 0; i < len; i++) {
/* 26 */       char c = message.charAt(i);
/* 27 */       if (false != Character.isLetter(c))
/*    */       {
/*    */         
/* 30 */         plain[i] = encodeChar(c, offset); } 
/*    */     } 
/* 32 */     return new String(plain);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String decode(String cipherText, int offset) {
/* 43 */     int len = cipherText.length();
/* 44 */     char[] plain = cipherText.toCharArray();
/*    */     
/* 46 */     for (int i = 0; i < len; i++) {
/* 47 */       char c = cipherText.charAt(i);
/* 48 */       if (false != Character.isLetter(c))
/*    */       {
/*    */         
/* 51 */         plain[i] = decodeChar(c, offset); } 
/*    */     } 
/* 53 */     return new String(plain);
/*    */   }
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
/*    */   private static char encodeChar(char c, int offset) {
/* 66 */     int position = ("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".indexOf(c) + offset) % 52;
/* 67 */     return "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".charAt(position);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static char decodeChar(char c, int offset) {
/* 78 */     int position = ("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".indexOf(c) - offset) % 52;
/* 79 */     if (position < 0) {
/* 80 */       position += 52;
/*    */     }
/* 82 */     return "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz".charAt(position);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\codec\Caesar.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */