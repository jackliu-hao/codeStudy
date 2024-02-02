/*    */ package ch.qos.logback.core.helpers;
/*    */ 
/*    */ import java.util.LinkedList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ThrowableToStringArray
/*    */ {
/*    */   public static String[] convert(Throwable t) {
/* 24 */     List<String> strList = new LinkedList<String>();
/* 25 */     extract(strList, t, null);
/* 26 */     return strList.<String>toArray(new String[0]);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   private static void extract(List<String> strList, Throwable t, StackTraceElement[] parentSTE) {
/* 32 */     StackTraceElement[] ste = t.getStackTrace();
/* 33 */     int numberOfcommonFrames = findNumberOfCommonFrames(ste, parentSTE);
/*    */     
/* 35 */     strList.add(formatFirstLine(t, parentSTE));
/* 36 */     for (int i = 0; i < ste.length - numberOfcommonFrames; i++) {
/* 37 */       strList.add("\tat " + ste[i].toString());
/*    */     }
/*    */     
/* 40 */     if (numberOfcommonFrames != 0) {
/* 41 */       strList.add("\t... " + numberOfcommonFrames + " common frames omitted");
/*    */     }
/*    */     
/* 44 */     Throwable cause = t.getCause();
/* 45 */     if (cause != null) {
/* 46 */       extract(strList, cause, ste);
/*    */     }
/*    */   }
/*    */   
/*    */   private static String formatFirstLine(Throwable t, StackTraceElement[] parentSTE) {
/* 51 */     String prefix = "";
/* 52 */     if (parentSTE != null) {
/* 53 */       prefix = "Caused by: ";
/*    */     }
/*    */     
/* 56 */     String result = prefix + t.getClass().getName();
/* 57 */     if (t.getMessage() != null) {
/* 58 */       result = result + ": " + t.getMessage();
/*    */     }
/* 60 */     return result;
/*    */   }
/*    */   
/*    */   private static int findNumberOfCommonFrames(StackTraceElement[] ste, StackTraceElement[] parentSTE) {
/* 64 */     if (parentSTE == null) {
/* 65 */       return 0;
/*    */     }
/*    */     
/* 68 */     int steIndex = ste.length - 1;
/* 69 */     int parentIndex = parentSTE.length - 1;
/* 70 */     int count = 0;
/* 71 */     while (steIndex >= 0 && parentIndex >= 0 && 
/* 72 */       ste[steIndex].equals(parentSTE[parentIndex])) {
/* 73 */       count++;
/*    */ 
/*    */ 
/*    */       
/* 77 */       steIndex--;
/* 78 */       parentIndex--;
/*    */     } 
/* 80 */     return count;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\helpers\ThrowableToStringArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */