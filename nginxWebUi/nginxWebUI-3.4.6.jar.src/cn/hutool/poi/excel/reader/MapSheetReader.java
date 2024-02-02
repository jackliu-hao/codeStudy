/*    */ package cn.hutool.poi.excel.reader;
/*    */ 
/*    */ import cn.hutool.core.collection.CollUtil;
/*    */ import cn.hutool.core.collection.IterUtil;
/*    */ import cn.hutool.core.collection.ListUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class MapSheetReader
/*    */   extends AbstractSheetReader<List<Map<String, Object>>>
/*    */ {
/*    */   private final int headerRowIndex;
/*    */   
/*    */   public MapSheetReader(int headerRowIndex, int startRowIndex, int endRowIndex) {
/* 31 */     super(startRowIndex, endRowIndex);
/* 32 */     this.headerRowIndex = headerRowIndex;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public List<Map<String, Object>> read(Sheet sheet) {
/* 38 */     int firstRowNum = sheet.getFirstRowNum();
/* 39 */     int lastRowNum = sheet.getLastRowNum();
/* 40 */     if (lastRowNum < 0) {
/* 41 */       return ListUtil.empty();
/*    */     }
/*    */     
/* 44 */     if (this.headerRowIndex < firstRowNum)
/* 45 */       throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is lower than first row index {}.", new Object[] { Integer.valueOf(this.headerRowIndex), Integer.valueOf(firstRowNum) })); 
/* 46 */     if (this.headerRowIndex > lastRowNum) {
/* 47 */       throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is greater than last row index {}.", new Object[] { Integer.valueOf(this.headerRowIndex), Integer.valueOf(firstRowNum) }));
/*    */     }
/* 49 */     int startRowIndex = Math.max(this.startRowIndex, firstRowNum);
/* 50 */     int endRowIndex = Math.min(this.endRowIndex, lastRowNum);
/*    */ 
/*    */     
/* 53 */     List<String> headerList = aliasHeader(readRow(sheet, this.headerRowIndex));
/*    */     
/* 55 */     List<Map<String, Object>> result = new ArrayList<>(endRowIndex - startRowIndex + 1);
/*    */     
/* 57 */     for (int i = startRowIndex; i <= endRowIndex; i++) {
/*    */       
/* 59 */       if (i != this.headerRowIndex) {
/* 60 */         List<Object> rowList = readRow(sheet, i);
/* 61 */         if (CollUtil.isNotEmpty(rowList) || false == this.ignoreEmptyRow) {
/* 62 */           result.add(IterUtil.toMap(headerList, rowList, true));
/*    */         }
/*    */       } 
/*    */     } 
/* 66 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\reader\MapSheetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */