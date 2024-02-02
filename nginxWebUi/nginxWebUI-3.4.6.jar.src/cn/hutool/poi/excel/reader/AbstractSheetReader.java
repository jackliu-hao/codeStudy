/*     */ package cn.hutool.poi.excel.reader;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.poi.excel.ExcelUtil;
/*     */ import cn.hutool.poi.excel.RowUtil;
/*     */ import cn.hutool.poi.excel.cell.CellEditor;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
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
/*     */ 
/*     */ public abstract class AbstractSheetReader<T>
/*     */   implements SheetReader<T>
/*     */ {
/*     */   protected final int startRowIndex;
/*     */   protected final int endRowIndex;
/*     */   protected boolean ignoreEmptyRow = true;
/*     */   protected CellEditor cellEditor;
/*     */   private Map<String, String> headerAlias;
/*     */   
/*     */   public AbstractSheetReader(int startRowIndex, int endRowIndex) {
/*  52 */     this.startRowIndex = startRowIndex;
/*  53 */     this.endRowIndex = endRowIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCellEditor(CellEditor cellEditor) {
/*  63 */     this.cellEditor = cellEditor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
/*  72 */     this.ignoreEmptyRow = ignoreEmptyRow;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHeaderAlias(Map<String, String> headerAlias) {
/*  81 */     this.headerAlias = headerAlias;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addHeaderAlias(String header, String alias) {
/*  91 */     Map<String, String> headerAlias = this.headerAlias;
/*  92 */     if (null == headerAlias) {
/*  93 */       headerAlias = new LinkedHashMap<>();
/*     */     }
/*  95 */     this.headerAlias = headerAlias;
/*  96 */     this.headerAlias.put(header, alias);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<String> aliasHeader(List<Object> headerList) {
/* 106 */     if (CollUtil.isEmpty(headerList)) {
/* 107 */       return new ArrayList<>(0);
/*     */     }
/*     */     
/* 110 */     int size = headerList.size();
/* 111 */     ArrayList<String> result = new ArrayList<>(size);
/* 112 */     for (int i = 0; i < size; i++) {
/* 113 */       result.add(aliasHeader(headerList.get(i), i));
/*     */     }
/* 115 */     return result;
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
/*     */   protected String aliasHeader(Object headerObj, int index) {
/* 127 */     if (null == headerObj) {
/* 128 */       return ExcelUtil.indexToColName(index);
/*     */     }
/*     */     
/* 131 */     String header = headerObj.toString();
/* 132 */     if (null != this.headerAlias) {
/* 133 */       return (String)ObjectUtil.defaultIfNull(this.headerAlias.get(header), header);
/*     */     }
/* 135 */     return header;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<Object> readRow(Sheet sheet, int rowIndex) {
/* 146 */     return RowUtil.readRow(sheet.getRow(rowIndex), this.cellEditor);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\reader\AbstractSheetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */