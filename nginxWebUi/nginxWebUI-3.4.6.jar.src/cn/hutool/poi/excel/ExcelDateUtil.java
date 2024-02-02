/*    */ package cn.hutool.poi.excel;
/*    */ 
/*    */ import cn.hutool.core.util.ArrayUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import org.apache.poi.ss.formula.ConditionalFormattingEvaluator;
/*    */ import org.apache.poi.ss.usermodel.Cell;
/*    */ import org.apache.poi.ss.usermodel.DateUtil;
/*    */ import org.apache.poi.ss.usermodel.ExcelNumberFormat;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ExcelDateUtil
/*    */ {
/* 20 */   private static final int[] customFormats = new int[] { 28, 30, 31, 32, 33, 55, 56, 57, 58 };
/*    */   
/*    */   public static boolean isDateFormat(Cell cell) {
/* 23 */     return isDateFormat(cell, (ConditionalFormattingEvaluator)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isDateFormat(Cell cell, ConditionalFormattingEvaluator cfEvaluator) {
/* 33 */     ExcelNumberFormat nf = ExcelNumberFormat.from(cell, cfEvaluator);
/* 34 */     return isDateFormat(nf);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean isDateFormat(ExcelNumberFormat numFmt) {
/* 43 */     return isDateFormat(numFmt.getIdx(), numFmt.getFormat());
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
/*    */   public static boolean isDateFormat(int formatIndex, String formatString) {
/* 56 */     if (ArrayUtil.contains(customFormats, formatIndex)) {
/* 57 */       return true;
/*    */     }
/*    */ 
/*    */     
/* 61 */     if (StrUtil.isNotEmpty(formatString) && 
/* 62 */       StrUtil.containsAny(formatString, new CharSequence[] {
/*    */           "周", "星期", "aa"
/*    */         })) {
/* 65 */       return true;
/*    */     }
/*    */     
/* 68 */     return DateUtil.isADateFormat(formatIndex, formatString);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelDateUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */