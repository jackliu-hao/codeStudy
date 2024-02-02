/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.poi.excel.cell.CellEditor;
/*     */ import cn.hutool.poi.excel.cell.CellHandler;
/*     */ import cn.hutool.poi.excel.cell.CellUtil;
/*     */ import cn.hutool.poi.excel.reader.BeanSheetReader;
/*     */ import cn.hutool.poi.excel.reader.ColumnSheetReader;
/*     */ import cn.hutool.poi.excel.reader.ListSheetReader;
/*     */ import cn.hutool.poi.excel.reader.MapSheetReader;
/*     */ import cn.hutool.poi.excel.reader.SheetReader;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.poi.ss.extractor.ExcelExtractor;
/*     */ import org.apache.poi.ss.usermodel.Cell;
/*     */ import org.apache.poi.ss.usermodel.Row;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
/*     */ import org.apache.poi.ss.usermodel.Workbook;
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
/*     */ public class ExcelReader
/*     */   extends ExcelBase<ExcelReader>
/*     */ {
/*     */   private boolean ignoreEmptyRow = true;
/*     */   private CellEditor cellEditor;
/*     */   
/*     */   public ExcelReader(String excelFilePath, int sheetIndex) {
/*  50 */     this(FileUtil.file(excelFilePath), sheetIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(String excelFilePath, String sheetName) {
/*  61 */     this(FileUtil.file(excelFilePath), sheetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(File bookFile, int sheetIndex) {
/*  71 */     this(WorkbookUtil.createBook(bookFile, true), sheetIndex);
/*  72 */     this.destFile = bookFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(File bookFile, String sheetName) {
/*  82 */     this(WorkbookUtil.createBook(bookFile, true), sheetName);
/*  83 */     this.destFile = bookFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(InputStream bookStream, int sheetIndex) {
/*  93 */     this(WorkbookUtil.createBook(bookStream), sheetIndex);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(InputStream bookStream, String sheetName) {
/* 103 */     this(WorkbookUtil.createBook(bookStream), sheetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(Workbook book, int sheetIndex) {
/* 113 */     this(book.getSheetAt(sheetIndex));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(Workbook book, String sheetName) {
/* 123 */     this(book.getSheet(sheetName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader(Sheet sheet) {
/* 132 */     super(sheet);
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
/*     */   public boolean isIgnoreEmptyRow() {
/* 144 */     return this.ignoreEmptyRow;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader setIgnoreEmptyRow(boolean ignoreEmptyRow) {
/* 154 */     this.ignoreEmptyRow = ignoreEmptyRow;
/* 155 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelReader setCellEditor(CellEditor cellEditor) {
/* 166 */     this.cellEditor = cellEditor;
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<List<Object>> read() {
/* 177 */     return read(0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<List<Object>> read(int startRowIndex) {
/* 188 */     return read(startRowIndex, 2147483647);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<List<Object>> read(int startRowIndex, int endRowIndex) {
/* 199 */     return read(startRowIndex, endRowIndex, true);
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
/*     */   public List<List<Object>> read(int startRowIndex, int endRowIndex, boolean aliasFirstLine) {
/* 212 */     ListSheetReader reader = new ListSheetReader(startRowIndex, endRowIndex, aliasFirstLine);
/* 213 */     reader.setCellEditor(this.cellEditor);
/* 214 */     reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
/* 215 */     reader.setHeaderAlias(this.headerAlias);
/* 216 */     return read((SheetReader<List<List<Object>>>)reader);
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
/*     */   public List<Object> readColumn(int columnIndex, int startRowIndex) {
/* 228 */     return readColumn(columnIndex, startRowIndex, 2147483647);
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
/*     */   public List<Object> readColumn(int columnIndex, int startRowIndex, int endRowIndex) {
/* 241 */     ColumnSheetReader reader = new ColumnSheetReader(columnIndex, startRowIndex, endRowIndex);
/* 242 */     reader.setCellEditor(this.cellEditor);
/* 243 */     reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
/* 244 */     reader.setHeaderAlias(this.headerAlias);
/* 245 */     return read((SheetReader<List<Object>>)reader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(CellHandler cellHandler) {
/* 256 */     read(0, 2147483647, cellHandler);
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
/*     */   public void read(int startRowIndex, int endRowIndex, CellHandler cellHandler) {
/* 269 */     checkNotClosed();
/*     */     
/* 271 */     startRowIndex = Math.max(startRowIndex, this.sheet.getFirstRowNum());
/* 272 */     endRowIndex = Math.min(endRowIndex, this.sheet.getLastRowNum());
/*     */ 
/*     */ 
/*     */     
/* 276 */     for (int y = startRowIndex; y <= endRowIndex; y++) {
/* 277 */       Row row = this.sheet.getRow(y);
/* 278 */       if (null != row) {
/* 279 */         short columnSize = row.getLastCellNum();
/*     */         short x;
/* 281 */         for (x = 0; x < columnSize; x = (short)(x + 1)) {
/* 282 */           Cell cell = row.getCell(x);
/* 283 */           cellHandler.handle(cell, CellUtil.getCellValue(cell));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Map<String, Object>> readAll() {
/* 296 */     return read(0, 1, 2147483647);
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
/*     */   public List<Map<String, Object>> read(int headerRowIndex, int startRowIndex, int endRowIndex) {
/* 309 */     MapSheetReader reader = new MapSheetReader(headerRowIndex, startRowIndex, endRowIndex);
/* 310 */     reader.setCellEditor(this.cellEditor);
/* 311 */     reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
/* 312 */     reader.setHeaderAlias(this.headerAlias);
/* 313 */     return read((SheetReader<List<Map<String, Object>>>)reader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> List<T> readAll(Class<T> beanType) {
/* 324 */     return read(0, 1, 2147483647, beanType);
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
/*     */   public <T> List<T> read(int headerRowIndex, int startRowIndex, Class<T> beanType) {
/* 338 */     return read(headerRowIndex, startRowIndex, 2147483647, beanType);
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
/*     */   public <T> List<T> read(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> beanType) {
/* 352 */     BeanSheetReader<T> reader = new BeanSheetReader(headerRowIndex, startRowIndex, endRowIndex, beanType);
/* 353 */     reader.setCellEditor(this.cellEditor);
/* 354 */     reader.setIgnoreEmptyRow(this.ignoreEmptyRow);
/* 355 */     reader.setHeaderAlias(this.headerAlias);
/* 356 */     return read((SheetReader)reader);
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
/*     */   public <T> T read(SheetReader<T> sheetReader) {
/* 368 */     checkNotClosed();
/* 369 */     return (T)((SheetReader)Assert.notNull(sheetReader)).read(this.sheet);
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
/*     */   public String readAsText(boolean withSheetName) {
/* 381 */     return ExcelExtractorUtil.readAsText(this.workbook, withSheetName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExcelExtractor getExtractor() {
/* 391 */     return ExcelExtractorUtil.getExtractor(this.workbook);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Object> readRow(int rowIndex) {
/* 402 */     return readRow(this.sheet.getRow(rowIndex));
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
/*     */   public Object readCellValue(int x, int y) {
/* 414 */     return CellUtil.getCellValue(getCell(x, y), this.cellEditor);
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
/*     */   public ExcelWriter getWriter() {
/* 426 */     return ExcelUtil.getWriter(this.destFile, this.sheet.getSheetName());
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
/*     */   private List<Object> readRow(Row row) {
/* 438 */     return RowUtil.readRow(row, this.cellEditor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkNotClosed() {
/* 445 */     Assert.isFalse(this.isClosed, "ExcelReader has been closed!", new Object[0]);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */