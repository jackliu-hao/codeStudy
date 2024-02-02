/*    */ package ch.qos.logback.classic.util;
/*    */ 
/*    */ import java.util.ArrayList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LoggerNameUtil
/*    */ {
/*    */   public static int getFirstSeparatorIndexOf(String name) {
/* 27 */     return getSeparatorIndexOf(name, 0);
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
/*    */   public static int getSeparatorIndexOf(String name, int fromIndex) {
/* 39 */     int dotIndex = name.indexOf('.', fromIndex);
/* 40 */     int dollarIndex = name.indexOf('$', fromIndex);
/*    */     
/* 42 */     if (dotIndex == -1 && dollarIndex == -1)
/* 43 */       return -1; 
/* 44 */     if (dotIndex == -1)
/* 45 */       return dollarIndex; 
/* 46 */     if (dollarIndex == -1) {
/* 47 */       return dotIndex;
/*    */     }
/* 49 */     return (dotIndex < dollarIndex) ? dotIndex : dollarIndex;
/*    */   }
/*    */   
/*    */   public static List<String> computeNameParts(String loggerName) {
/* 53 */     List<String> partList = new ArrayList<String>();
/*    */     
/* 55 */     int fromIndex = 0;
/*    */     while (true) {
/* 57 */       int index = getSeparatorIndexOf(loggerName, fromIndex);
/* 58 */       if (index == -1) {
/* 59 */         partList.add(loggerName.substring(fromIndex));
/*    */         break;
/*    */       } 
/* 62 */       partList.add(loggerName.substring(fromIndex, index));
/* 63 */       fromIndex = index + 1;
/*    */     } 
/* 65 */     return partList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classi\\util\LoggerNameUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */