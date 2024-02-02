/*    */ package cn.hutool.core.lang.ansi;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AnsiEncoder
/*    */ {
/*    */   private static final String ENCODE_JOIN = ";";
/*    */   private static final String ENCODE_START = "\033[";
/*    */   private static final String ENCODE_END = "m";
/* 14 */   private static final String RESET = "0;" + AnsiColor.DEFAULT;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String encode(Object... elements) {
/* 23 */     StringBuilder sb = new StringBuilder();
/* 24 */     buildEnabled(sb, elements);
/* 25 */     return sb.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static void buildEnabled(StringBuilder sb, Object[] elements) {
/* 35 */     boolean writingAnsi = false;
/* 36 */     boolean containsEncoding = false;
/* 37 */     for (Object element : elements) {
/* 38 */       if (null != element) {
/*    */ 
/*    */         
/* 41 */         if (element instanceof AnsiElement) {
/* 42 */           containsEncoding = true;
/* 43 */           if (writingAnsi) {
/* 44 */             sb.append(";");
/*    */           } else {
/* 46 */             sb.append("\033[");
/* 47 */             writingAnsi = true;
/*    */           }
/*    */         
/* 50 */         } else if (writingAnsi) {
/* 51 */           sb.append("m");
/* 52 */           writingAnsi = false;
/*    */         } 
/*    */         
/* 55 */         sb.append(element);
/*    */       } 
/*    */     } 
/*    */     
/* 59 */     if (containsEncoding) {
/* 60 */       sb.append(writingAnsi ? ";" : "\033[");
/* 61 */       sb.append(RESET);
/* 62 */       sb.append("m");
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\ansi\AnsiEncoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */