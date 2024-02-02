/*    */ package cn.hutool.poi.excel.cell.setters;
/*    */ 
/*    */ import cn.hutool.core.util.NumberUtil;
/*    */ import cn.hutool.poi.excel.cell.CellSetter;
/*    */ import org.apache.poi.ss.usermodel.Cell;
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
/*    */ public class NumberCellSetter
/*    */   implements CellSetter
/*    */ {
/*    */   private final Number value;
/*    */   
/*    */   NumberCellSetter(Number value) {
/* 23 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setValue(Cell cell) {
/* 30 */     cell.setCellValue(NumberUtil.toDouble(this.value));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\setters\NumberCellSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */