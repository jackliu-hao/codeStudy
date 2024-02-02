/*    */ package cn.hutool.poi.excel.editors;
/*    */ 
/*    */ import cn.hutool.poi.excel.cell.CellEditor;
/*    */ import org.apache.poi.ss.usermodel.Cell;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NumericToIntEditor
/*    */   implements CellEditor
/*    */ {
/*    */   public Object edit(Cell cell, Object value) {
/* 16 */     if (value instanceof Number) {
/* 17 */       return Integer.valueOf(((Number)value).intValue());
/*    */     }
/* 19 */     return value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\editors\NumericToIntEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */