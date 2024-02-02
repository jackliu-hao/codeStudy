/*    */ package cn.hutool.poi.excel.cell.setters;
/*    */ 
/*    */ import cn.hutool.poi.excel.cell.CellSetter;
/*    */ import org.apache.poi.ss.usermodel.Cell;
/*    */ import org.apache.poi.ss.usermodel.Hyperlink;
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
/*    */ public class HyperlinkCellSetter
/*    */   implements CellSetter
/*    */ {
/*    */   private final Hyperlink value;
/*    */   
/*    */   HyperlinkCellSetter(Hyperlink value) {
/* 23 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setValue(Cell cell) {
/* 28 */     cell.setHyperlink(this.value);
/* 29 */     cell.setCellValue(this.value.getLabel());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\setters\HyperlinkCellSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */