/*     */ package cn.hutool.poi.excel;
/*     */ 
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.poi.excel.cell.CellLocation;
/*     */ import cn.hutool.poi.excel.cell.CellUtil;
/*     */ import cn.hutool.poi.excel.style.StyleUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.poi.common.usermodel.HyperlinkType;
/*     */ import org.apache.poi.ss.usermodel.Cell;
/*     */ import org.apache.poi.ss.usermodel.CellStyle;
/*     */ import org.apache.poi.ss.usermodel.Hyperlink;
/*     */ import org.apache.poi.ss.usermodel.Row;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
/*     */ import org.apache.poi.ss.usermodel.Workbook;
/*     */ import org.apache.poi.xssf.usermodel.XSSFSheet;
/*     */ import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
/*     */ public class ExcelBase<T extends ExcelBase<T>>
/*     */   implements Closeable
/*     */ {
/*     */   protected boolean isClosed;
/*     */   protected File destFile;
/*     */   protected Workbook workbook;
/*     */   protected Sheet sheet;
/*     */   protected Map<String, String> headerAlias;
/*     */   
/*     */   public ExcelBase(Sheet sheet) {
/*  61 */     Assert.notNull(sheet, "No Sheet provided.", new Object[0]);
/*  62 */     this.sheet = sheet;
/*  63 */     this.workbook = sheet.getWorkbook();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Workbook getWorkbook() {
/*  72 */     return this.workbook;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSheetCount() {
/*  82 */     return this.workbook.getNumberOfSheets();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Sheet> getSheets() {
/*  92 */     int totalSheet = getSheetCount();
/*  93 */     List<Sheet> result = new ArrayList<>(totalSheet);
/*  94 */     for (int i = 0; i < totalSheet; i++) {
/*  95 */       result.add(this.workbook.getSheetAt(i));
/*     */     }
/*  97 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getSheetNames() {
/* 107 */     int totalSheet = this.workbook.getNumberOfSheets();
/* 108 */     List<String> result = new ArrayList<>(totalSheet);
/* 109 */     for (int i = 0; i < totalSheet; i++) {
/* 110 */       result.add(this.workbook.getSheetAt(i).getSheetName());
/*     */     }
/* 112 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sheet getSheet() {
/* 121 */     return this.sheet;
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
/*     */   public T renameSheet(String newName) {
/* 135 */     this.workbook.setSheetName(this.workbook.getSheetIndex(this.sheet), newName);
/* 136 */     return (T)this;
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
/*     */   public T setSheet(String sheetName) {
/* 148 */     return setSheet(WorkbookUtil.getOrCreateSheet(this.workbook, sheetName));
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
/*     */   public T setSheet(int sheetIndex) {
/* 160 */     return setSheet(WorkbookUtil.getOrCreateSheet(this.workbook, sheetIndex));
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
/*     */   public T setSheet(Sheet sheet) {
/* 172 */     this.sheet = sheet;
/* 173 */     return (T)this;
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
/*     */   public T cloneSheet(int sheetIndex, String newSheetName, boolean setAsCurrentSheet) {
/*     */     Sheet sheet;
/* 187 */     if (this.workbook instanceof XSSFWorkbook) {
/* 188 */       XSSFWorkbook workbook = (XSSFWorkbook)this.workbook;
/* 189 */       XSSFSheet xSSFSheet = workbook.cloneSheet(sheetIndex, newSheetName);
/*     */     } else {
/* 191 */       sheet = this.workbook.cloneSheet(sheetIndex);
/* 192 */       this.workbook.setSheetName(sheetIndex, newSheetName);
/*     */     } 
/* 194 */     if (setAsCurrentSheet) {
/* 195 */       this.sheet = sheet;
/*     */     }
/*     */     
/* 198 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cell getCell(String locationRef) {
/* 209 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 210 */     return getCell(cellLocation.getX(), cellLocation.getY());
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
/*     */   public Cell getCell(int x, int y) {
/* 222 */     return getCell(x, y, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Cell getOrCreateCell(String locationRef) {
/* 233 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 234 */     return getOrCreateCell(cellLocation.getX(), cellLocation.getY());
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
/*     */   public Cell getOrCreateCell(int x, int y) {
/* 246 */     return getCell(x, y, true);
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
/*     */   public Cell getCell(String locationRef, boolean isCreateIfNotExist) {
/* 258 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 259 */     return getCell(cellLocation.getX(), cellLocation.getY(), isCreateIfNotExist);
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
/*     */   public Cell getCell(int x, int y, boolean isCreateIfNotExist) {
/* 272 */     Row row = isCreateIfNotExist ? RowUtil.getOrCreateRow(this.sheet, y) : this.sheet.getRow(y);
/* 273 */     if (null != row) {
/* 274 */       return isCreateIfNotExist ? CellUtil.getOrCreateCell(row, x) : row.getCell(x);
/*     */     }
/* 276 */     return null;
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
/*     */   public Row getOrCreateRow(int y) {
/* 288 */     return RowUtil.getOrCreateRow(this.sheet, y);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle getOrCreateCellStyle(String locationRef) {
/* 299 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 300 */     return getOrCreateCellStyle(cellLocation.getX(), cellLocation.getY());
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
/*     */   public CellStyle getOrCreateCellStyle(int x, int y) {
/* 312 */     CellStyle cellStyle = getOrCreateCell(x, y).getCellStyle();
/* 313 */     return StyleUtil.isNullOrDefaultStyle(this.workbook, cellStyle) ? createCellStyle(x, y) : cellStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle createCellStyle(String locationRef) {
/* 324 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 325 */     return createCellStyle(cellLocation.getX(), cellLocation.getY());
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
/*     */   public CellStyle createCellStyle(int x, int y) {
/* 337 */     Cell cell = getOrCreateCell(x, y);
/* 338 */     CellStyle cellStyle = this.workbook.createCellStyle();
/* 339 */     cell.setCellStyle(cellStyle);
/* 340 */     return cellStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle createCellStyle() {
/* 351 */     return StyleUtil.createCellStyle(this.workbook);
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
/*     */   public CellStyle getOrCreateRowStyle(int y) {
/* 363 */     CellStyle rowStyle = getOrCreateRow(y).getRowStyle();
/* 364 */     return StyleUtil.isNullOrDefaultStyle(this.workbook, rowStyle) ? createRowStyle(y) : rowStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle createRowStyle(int y) {
/* 375 */     CellStyle rowStyle = this.workbook.createCellStyle();
/* 376 */     getOrCreateRow(y).setRowStyle(rowStyle);
/* 377 */     return rowStyle;
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
/*     */   public CellStyle getOrCreateColumnStyle(int x) {
/* 389 */     CellStyle columnStyle = this.sheet.getColumnStyle(x);
/* 390 */     return StyleUtil.isNullOrDefaultStyle(this.workbook, columnStyle) ? createColumnStyle(x) : columnStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CellStyle createColumnStyle(int x) {
/* 401 */     CellStyle columnStyle = this.workbook.createCellStyle();
/* 402 */     this.sheet.setDefaultColumnStyle(x, columnStyle);
/* 403 */     return columnStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Hyperlink createHyperlink(HyperlinkType type, String address) {
/* 414 */     return createHyperlink(type, address, address);
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
/*     */   public Hyperlink createHyperlink(HyperlinkType type, String address, String label) {
/* 426 */     Hyperlink hyperlink = this.workbook.getCreationHelper().createHyperlink(type);
/* 427 */     hyperlink.setAddress(address);
/* 428 */     hyperlink.setLabel(label);
/* 429 */     return hyperlink;
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
/*     */   public int getRowCount() {
/* 443 */     return this.sheet.getLastRowNum() + 1;
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
/*     */   public int getPhysicalRowCount() {
/* 457 */     return this.sheet.getPhysicalNumberOfRows();
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
/*     */   public int getColumnCount() {
/* 470 */     return getColumnCount(0);
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
/*     */   public int getColumnCount(int rowNum) {
/* 484 */     Row row = this.sheet.getRow(rowNum);
/* 485 */     if (null != row)
/*     */     {
/* 487 */       return row.getLastCellNum();
/*     */     }
/* 489 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isXlsx() {
/* 499 */     return (this.sheet instanceof XSSFSheet || this.sheet instanceof org.apache.poi.xssf.streaming.SXSSFSheet);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 508 */     IoUtil.close((Closeable)this.workbook);
/* 509 */     this.sheet = null;
/* 510 */     this.workbook = null;
/* 511 */     this.isClosed = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, String> getHeaderAlias() {
/* 520 */     return this.headerAlias;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T setHeaderAlias(Map<String, String> headerAlias) {
/* 530 */     this.headerAlias = headerAlias;
/*     */     
/* 532 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T addHeaderAlias(String header, String alias) {
/* 543 */     Map<String, String> headerAlias = this.headerAlias;
/* 544 */     if (null == headerAlias) {
/* 545 */       headerAlias = new LinkedHashMap<>();
/*     */     }
/* 547 */     this.headerAlias = headerAlias;
/* 548 */     this.headerAlias.put(header, alias);
/*     */     
/* 550 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T removeHeaderAlias(String header) {
/* 560 */     this.headerAlias.remove(header);
/*     */     
/* 562 */     return (T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T clearHeaderAlias() {
/* 571 */     this.headerAlias = null;
/*     */     
/* 573 */     return (T)this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */