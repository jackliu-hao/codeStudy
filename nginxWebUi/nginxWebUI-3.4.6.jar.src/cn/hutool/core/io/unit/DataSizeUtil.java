/*    */ package cn.hutool.core.io.unit;
/*    */ 
/*    */ import java.text.DecimalFormat;
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
/*    */ public class DataSizeUtil
/*    */ {
/*    */   public static long parse(String text) {
/* 20 */     return DataSize.parse(text).toBytes();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String format(long size) {
/* 31 */     if (size <= 0L) {
/* 32 */       return "0";
/*    */     }
/* 34 */     int digitGroups = Math.min(DataUnit.UNIT_NAMES.length - 1, (int)(Math.log10(size) / Math.log10(1024.0D)));
/* 35 */     return (new DecimalFormat("#,##0.##"))
/* 36 */       .format(size / Math.pow(1024.0D, digitGroups)) + " " + DataUnit.UNIT_NAMES[digitGroups];
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\i\\unit\DataSizeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */