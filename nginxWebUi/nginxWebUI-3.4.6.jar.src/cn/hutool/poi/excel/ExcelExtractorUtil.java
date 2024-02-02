/*    */ package cn.hutool.poi.excel;
/*    */ 
/*    */ import org.apache.poi.hssf.extractor.ExcelExtractor;
/*    */ import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/*    */ import org.apache.poi.ss.extractor.ExcelExtractor;
/*    */ import org.apache.poi.ss.usermodel.Workbook;
/*    */ import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
/*    */ import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
/*    */ public class ExcelExtractorUtil
/*    */ {
/*    */   public static ExcelExtractor getExtractor(Workbook wb) {
/*    */     XSSFExcelExtractor xSSFExcelExtractor;
/* 24 */     if (wb instanceof HSSFWorkbook) {
/* 25 */       ExcelExtractor excelExtractor = new ExcelExtractor((HSSFWorkbook)wb);
/*    */     } else {
/* 27 */       xSSFExcelExtractor = new XSSFExcelExtractor((XSSFWorkbook)wb);
/*    */     } 
/* 29 */     return (ExcelExtractor)xSSFExcelExtractor;
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
/*    */   public static String readAsText(Workbook wb, boolean withSheetName) {
/* 42 */     ExcelExtractor extractor = getExtractor(wb);
/* 43 */     extractor.setIncludeSheetNames(withSheetName);
/* 44 */     return extractor.getText();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelExtractorUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */