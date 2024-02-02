/*    */ package cn.hutool.poi.excel.reader;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.convert.Convert;
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
/*    */ 
/*    */ public class ListSheetReader
/*    */   extends AbstractSheetReader<List<List<Object>>>
/*    */ {
/*    */   private final boolean aliasFirstLine;
/*    */   
/*    */   public ListSheetReader(int startRowIndex, int endRowIndex, boolean aliasFirstLine) {
/* 29 */     super(startRowIndex, endRowIndex);
/* 30 */     this.aliasFirstLine = aliasFirstLine;
/*    */   }
/*    */ 
/*    */   
/*    */   public List<List<Object>> read(Sheet sheet) {
/* 35 */     List<List<Object>> resultList = new ArrayList<>();
/*    */     
/* 37 */     int startRowIndex = Math.max(this.startRowIndex, sheet.getFirstRowNum());
/* 38 */     int endRowIndex = Math.min(this.endRowIndex, sheet.getLastRowNum());
/*    */     
/* 40 */     for (int i = startRowIndex; i <= endRowIndex; i++) {
/* 41 */       List<Object> rowList = readRow(sheet, i);
/* 42 */       if (CollUtil.isNotEmpty(rowList) || false == this.ignoreEmptyRow) {
/* 43 */         if (this.aliasFirstLine && i == startRowIndex)
/*    */         {
/* 45 */           rowList = Convert.toList(Object.class, aliasHeader(rowList));
/*    */         }
/* 47 */         resultList.add(rowList);
/*    */       } 
/*    */     } 
/* 50 */     return resultList;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\reader\ListSheetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */