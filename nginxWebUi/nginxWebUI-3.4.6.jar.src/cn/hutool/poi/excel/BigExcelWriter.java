/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import java.io.File;
/*     */ import java.io.OutputStream;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
/*     */ import org.apache.poi.ss.usermodel.Workbook;
/*     */ import org.apache.poi.xssf.streaming.SXSSFSheet;
/*     */ import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
/*     */ public class BigExcelWriter
/*     */   extends ExcelWriter
/*     */ {
/*     */   public static final int DEFAULT_WINDOW_SIZE = 100;
/*     */   private boolean isFlushed;
/*     */   
/*     */   public BigExcelWriter() {
/*  37 */     this(100);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigExcelWriter(int rowAccessWindowSize) {
/*  48 */     this(WorkbookUtil.createSXSSFBook(rowAccessWindowSize), (String)null);
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
/*     */   
/*     */   public BigExcelWriter(int rowAccessWindowSize, boolean compressTmpFiles, boolean useSharedStringsTable, String sheetName) {
/*  63 */     this(WorkbookUtil.createSXSSFBook(rowAccessWindowSize, compressTmpFiles, useSharedStringsTable), sheetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigExcelWriter(String destFilePath) {
/*  72 */     this(destFilePath, (String)null);
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
/*     */   public BigExcelWriter(int rowAccessWindowSize, String sheetName) {
/*  85 */     this(WorkbookUtil.createSXSSFBook(rowAccessWindowSize), sheetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigExcelWriter(String destFilePath, String sheetName) {
/*  95 */     this(FileUtil.file(destFilePath), sheetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigExcelWriter(File destFile) {
/* 104 */     this(destFile, (String)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigExcelWriter(File destFile, String sheetName) {
/* 114 */     this(destFile.exists() ? WorkbookUtil.createSXSSFBook(destFile) : WorkbookUtil.createSXSSFBook(), sheetName);
/* 115 */     this.destFile = destFile;
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
/*     */   public BigExcelWriter(SXSSFWorkbook workbook, String sheetName) {
/* 127 */     this(WorkbookUtil.getOrCreateSheet((Workbook)workbook, sheetName));
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
/*     */   public BigExcelWriter(Sheet sheet) {
/* 139 */     super(sheet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BigExcelWriter autoSizeColumn(int columnIndex) {
/* 146 */     SXSSFSheet sheet = (SXSSFSheet)this.sheet;
/* 147 */     sheet.trackColumnForAutoSizing(columnIndex);
/* 148 */     super.autoSizeColumn(columnIndex);
/* 149 */     sheet.untrackColumnForAutoSizing(columnIndex);
/* 150 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public BigExcelWriter autoSizeColumnAll() {
/* 155 */     SXSSFSheet sheet = (SXSSFSheet)this.sheet;
/* 156 */     sheet.trackAllColumnsForAutoSizing();
/* 157 */     super.autoSizeColumnAll();
/* 158 */     sheet.untrackAllColumnsForAutoSizing();
/* 159 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public ExcelWriter flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
/* 164 */     if (false == this.isFlushed) {
/* 165 */       this.isFlushed = true;
/* 166 */       return super.flush(out, isCloseOut);
/*     */     } 
/* 168 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 173 */     if (null != this.destFile && false == this.isFlushed) {
/* 174 */       flush();
/*     */     }
/*     */ 
/*     */     
/* 178 */     ((SXSSFWorkbook)this.workbook).dispose();
/* 179 */     closeWithoutFlush();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\BigExcelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */