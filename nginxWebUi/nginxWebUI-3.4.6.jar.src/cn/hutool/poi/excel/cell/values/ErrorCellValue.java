/*    */ package cn.hutool.poi.excel.cell.values;
/*    */ 
/*    */ import cn.hutool.poi.excel.cell.CellValue;
/*    */ import org.apache.poi.ss.usermodel.Cell;
/*    */ import org.apache.poi.ss.usermodel.FormulaError;
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
/*    */ public class ErrorCellValue
/*    */   implements CellValue<String>
/*    */ {
/*    */   private final Cell cell;
/*    */   
/*    */   public ErrorCellValue(Cell cell) {
/* 24 */     this.cell = cell;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 29 */     FormulaError error = FormulaError.forInt(this.cell.getErrorCellValue());
/* 30 */     return (null == error) ? "" : error.getString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\values\ErrorCellValue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */