/*    */ package cn.hutool.poi.excel.cell.values;
/*    */ 
/*    */ import cn.hutool.core.date.DateUtil;
/*    */ import cn.hutool.poi.excel.ExcelDateUtil;
/*    */ import cn.hutool.poi.excel.cell.CellValue;
/*    */ import java.util.Date;
/*    */ import org.apache.poi.ss.usermodel.Cell;
/*    */ import org.apache.poi.ss.usermodel.CellStyle;
/*    */ import org.apache.poi.ss.util.NumberToTextConverter;
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
/*    */ public class NumericCellValue
/*    */   implements CellValue<Object>
/*    */ {
/*    */   private final Cell cell;
/*    */   
/*    */   public NumericCellValue(Cell cell) {
/* 29 */     this.cell = cell;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object getValue() {
/* 34 */     double value = this.cell.getNumericCellValue();
/*    */     
/* 36 */     CellStyle style = this.cell.getCellStyle();
/* 37 */     if (null != style) {
/*    */       
/* 39 */       if (ExcelDateUtil.isDateFormat(this.cell)) {
/*    */         
/* 41 */         Date dateCellValue = this.cell.getDateCellValue();
/* 42 */         if ("1899".equals(DateUtil.format(dateCellValue, "yyyy"))) {
/* 43 */           return DateUtil.format(dateCellValue, style.getDataFormatString());
/*    */         }
/*    */         
/* 46 */         return DateUtil.date(dateCellValue);
/*    */       } 
/*    */       
/* 49 */       String format = style.getDataFormatString();
/*    */       
/* 51 */       if (null != format && format.indexOf('.') < 0) {
/* 52 */         long longPart = (long)value;
/* 53 */         if (longPart == value)
/*    */         {
/* 55 */           return Long.valueOf(longPart);
/*    */         }
/*    */       } 
/*    */     } 
/*    */ 
/*    */     
/* 61 */     return Double.valueOf(Double.parseDouble(NumberToTextConverter.toText(value)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\values\NumericCellValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */