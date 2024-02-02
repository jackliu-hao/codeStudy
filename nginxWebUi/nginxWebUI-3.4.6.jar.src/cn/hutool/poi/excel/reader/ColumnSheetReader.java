/*    */ package cn.hutool.poi.excel.reader;
/*    */ 
/*    */ import cn.hutool.poi.excel.cell.CellUtil;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.apache.poi.ss.usermodel.Sheet;
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
/*    */ 
/*    */ public class ColumnSheetReader
/*    */   extends AbstractSheetReader<List<Object>>
/*    */ {
/*    */   private final int columnIndex;
/*    */   
/*    */   public ColumnSheetReader(int columnIndex, int startRowIndex, int endRowIndex) {
/* 27 */     super(startRowIndex, endRowIndex);
/* 28 */     this.columnIndex = columnIndex;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<Object> read(Sheet sheet) {
/* 33 */     List<Object> resultList = new ArrayList();
/*    */     
/* 35 */     int startRowIndex = Math.max(this.startRowIndex, sheet.getFirstRowNum());
/* 36 */     int endRowIndex = Math.min(this.endRowIndex, sheet.getLastRowNum());
/*    */ 
/*    */     
/* 39 */     for (int i = startRowIndex; i <= endRowIndex; i++) {
/* 40 */       Object value = CellUtil.getCellValue(CellUtil.getCell(sheet.getRow(i), this.columnIndex), this.cellEditor);
/* 41 */       if (null != value || false == this.ignoreEmptyRow) {
/* 42 */         resultList.add(value);
/*    */       }
/*    */     } 
/*    */     
/* 46 */     return resultList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\reader\ColumnSheetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */