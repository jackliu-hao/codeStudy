/*    */ package cn.hutool.poi.excel.cell.setters;
/*    */ 
/*    */ import cn.hutool.poi.excel.cell.CellSetter;
/*    */ import java.time.Instant;
/*    */ import java.time.LocalDate;
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.temporal.TemporalAccessor;
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
/*    */ public class TemporalAccessorCellSetter
/*    */   implements CellSetter
/*    */ {
/*    */   private final TemporalAccessor value;
/*    */   
/*    */   TemporalAccessorCellSetter(TemporalAccessor value) {
/* 28 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(Cell cell) {
/* 33 */     if (this.value instanceof Instant) {
/* 34 */       cell.setCellValue(Date.from((Instant)this.value));
/* 35 */     } else if (this.value instanceof LocalDateTime) {
/* 36 */       cell.setCellValue((LocalDateTime)this.value);
/* 37 */     } else if (this.value instanceof LocalDate) {
/* 38 */       cell.setCellValue((LocalDate)this.value);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\setters\TemporalAccessorCellSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */