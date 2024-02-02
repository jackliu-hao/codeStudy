/*    */ package cn.hutool.poi.excel.cell.setters;
/*    */ 
/*    */ import cn.hutool.poi.excel.cell.CellSetter;
/*    */ import java.util.Date;
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
/*    */ 
/*    */ public class DateCellSetter
/*    */   implements CellSetter
/*    */ {
/*    */   private final Date value;
/*    */   
/*    */   DateCellSetter(Date value) {
/* 24 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(Cell cell) {
/* 29 */     cell.setCellValue(this.value);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\setters\DateCellSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */