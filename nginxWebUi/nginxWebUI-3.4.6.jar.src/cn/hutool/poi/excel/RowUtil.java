/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.excel.cell.CellEditor;
/*     */ import cn.hutool.poi.excel.cell.CellUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Optional;
/*     */ import java.util.stream.Collector;
/*     */ import java.util.stream.Collectors;
/*     */ import java.util.stream.IntStream;
/*     */ import org.apache.poi.ss.usermodel.Cell;
/*     */ import org.apache.poi.ss.usermodel.Row;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
/*     */ import org.apache.poi.ss.util.CellRangeAddress;
/*     */ import org.apache.poi.ss.util.CellRangeUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RowUtil
/*     */ {
/*     */   public static Row getOrCreateRow(Sheet sheet, int rowIndex) {
/*  35 */     Row row = sheet.getRow(rowIndex);
/*  36 */     if (null == row) {
/*  37 */       row = sheet.createRow(rowIndex);
/*     */     }
/*  39 */     return row;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<Object> readRow(Row row, CellEditor cellEditor) {
/*  50 */     return readRow(row, 0, 32767, cellEditor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<Object> readRow(Row row, int startCellNumInclude, int endCellNumInclude, CellEditor cellEditor) {
/*  63 */     if (null == row) {
/*  64 */       return new ArrayList(0);
/*     */     }
/*  66 */     short rowLength = row.getLastCellNum();
/*  67 */     if (rowLength < 0) {
/*  68 */       return ListUtil.empty();
/*     */     }
/*     */     
/*  71 */     int size = Math.min(endCellNumInclude + 1, rowLength);
/*  72 */     List<Object> cellValues = new ArrayList(size);
/*     */     
/*  74 */     boolean isAllNull = true;
/*  75 */     for (int i = startCellNumInclude; i < size; i++) {
/*  76 */       Object cellValue = CellUtil.getCellValue(CellUtil.getCell(row, i), cellEditor);
/*  77 */       isAllNull &= StrUtil.isEmptyIfStr(cellValue);
/*  78 */       cellValues.add(cellValue);
/*     */     } 
/*     */     
/*  81 */     if (isAllNull)
/*     */     {
/*  83 */       return ListUtil.empty();
/*     */     }
/*  85 */     return cellValues;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeRow(Row row, Iterable<?> rowData) {
/*  95 */     writeRow(row, rowData, null, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void writeRow(Row row, Iterable<?> rowData, StyleSet styleSet, boolean isHeader) {
/* 107 */     int i = 0;
/*     */     
/* 109 */     for (Object value : rowData) {
/* 110 */       Cell cell = row.createCell(i);
/* 111 */       CellUtil.setCellValue(cell, value, styleSet, isHeader);
/* 112 */       i++;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void insertRow(Sheet sheet, int startRow, int insertNumber) {
/* 125 */     if (insertNumber <= 0) {
/*     */       return;
/*     */     }
/*     */     
/* 129 */     Row sourceRow = Optional.<Row>ofNullable(sheet.getRow(startRow)).orElseGet(() -> sheet.createRow(insertNumber));
/*     */     
/* 131 */     sheet.shiftRows(startRow, sheet.getLastRowNum(), insertNumber, true, false);
/*     */ 
/*     */     
/* 134 */     IntStream.range(startRow, startRow + insertNumber).forEachOrdered(i -> {
/*     */           Row row = sheet.createRow(i);
/*     */           row.setHeightInPoints(sourceRow.getHeightInPoints());
/*     */           short lastCellNum = sourceRow.getLastCellNum();
/*     */           IntStream.range(0, lastCellNum).forEachOrdered(());
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void removeRow(Row row) {
/* 153 */     if (row == null) {
/*     */       return;
/*     */     }
/* 156 */     int rowIndex = row.getRowNum();
/* 157 */     Sheet sheet = row.getSheet();
/* 158 */     int lastRow = sheet.getLastRowNum();
/* 159 */     if (rowIndex >= 0 && rowIndex < lastRow) {
/* 160 */       List<CellRangeAddress> updateMergedRegions = new ArrayList<>();
/*     */       
/* 162 */       IntStream.range(0, sheet.getNumMergedRegions())
/* 163 */         .forEach(i -> {
/*     */             CellRangeAddress mr = sheet.getMergedRegion(i);
/*     */             
/*     */             if (!mr.containsRow(rowIndex)) {
/*     */               return;
/*     */             }
/*     */             
/*     */             if (mr.getFirstRow() == mr.getLastRow() - 1 && mr.getFirstColumn() == mr.getLastColumn()) {
/*     */               return;
/*     */             }
/*     */             
/*     */             updateMergedRegions.add(mr);
/*     */           });
/* 176 */       sheet.shiftRows(rowIndex + 1, lastRow, -1);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 183 */       List<Integer> removeMergedRegions = IntStream.range(0, sheet.getNumMergedRegions()).filter(i -> updateMergedRegions.stream().anyMatch(())).boxed().collect((Collector)Collectors.toList());
/*     */       
/* 185 */       sheet.removeMergedRegions(removeMergedRegions);
/* 186 */       updateMergedRegions.forEach(mr -> {
/*     */             mr.setLastRow(mr.getLastRow() - 1);
/*     */             sheet.addMergedRegion(mr);
/*     */           });
/* 190 */       sheet.validateMergedRegions();
/*     */     } 
/* 192 */     if (rowIndex == lastRow) {
/* 193 */       Row removingRow = sheet.getRow(rowIndex);
/* 194 */       if (removingRow != null)
/* 195 */         sheet.removeRow(removingRow); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\RowUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */