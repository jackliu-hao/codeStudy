/*    */ package cn.hutool.poi.excel.editors;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import cn.hutool.poi.excel.cell.CellEditor;
/*    */ import org.apache.poi.ss.usermodel.Cell;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TrimEditor
/*    */   implements CellEditor
/*    */ {
/*    */   public Object edit(Cell cell, Object value) {
/* 17 */     if (value instanceof String) {
/* 18 */       return StrUtil.trim((String)value);
/*    */     }
/* 20 */     return value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\editors\TrimEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */