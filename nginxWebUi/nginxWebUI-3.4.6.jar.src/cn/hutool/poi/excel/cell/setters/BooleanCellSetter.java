/*    */ package cn.hutool.poi.excel.cell.setters;
/*    */ 
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
/*    */ public class BooleanCellSetter
/*    */   implements CellSetter
/*    */ {
/*    */   private final Boolean value;
/*    */   
/*    */   BooleanCellSetter(Boolean value) {
/* 22 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(Cell cell) {
/* 27 */     cell.setCellValue(this.value.booleanValue());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\setters\BooleanCellSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */