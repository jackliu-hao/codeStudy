/*      */ package cn.hutool.poi.excel;
/*      */ 
/*      */ import cn.hutool.core.bean.BeanUtil;
/*      */ import cn.hutool.core.builder.Builder;
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.comparator.IndexedComparator;
/*      */ import cn.hutool.core.io.FileUtil;
/*      */ import cn.hutool.core.io.IORuntimeException;
/*      */ import cn.hutool.core.io.IoUtil;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import cn.hutool.core.map.multi.RowKeyTable;
/*      */ import cn.hutool.core.map.multi.Table;
/*      */ import cn.hutool.core.util.CharsetUtil;
/*      */ import cn.hutool.core.util.IdUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.core.util.URLUtil;
/*      */ import cn.hutool.poi.excel.cell.CellLocation;
/*      */ import cn.hutool.poi.excel.cell.CellUtil;
/*      */ import cn.hutool.poi.excel.style.Align;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.lang.invoke.SerializedLambda;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.ConcurrentHashMap;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import org.apache.poi.ss.usermodel.Cell;
/*      */ import org.apache.poi.ss.usermodel.CellStyle;
/*      */ import org.apache.poi.ss.usermodel.ClientAnchor;
/*      */ import org.apache.poi.ss.usermodel.DataValidation;
/*      */ import org.apache.poi.ss.usermodel.DataValidationConstraint;
/*      */ import org.apache.poi.ss.usermodel.DataValidationHelper;
/*      */ import org.apache.poi.ss.usermodel.Drawing;
/*      */ import org.apache.poi.ss.usermodel.Font;
/*      */ import org.apache.poi.ss.usermodel.HeaderFooter;
/*      */ import org.apache.poi.ss.usermodel.Row;
/*      */ import org.apache.poi.ss.usermodel.Sheet;
/*      */ import org.apache.poi.ss.usermodel.Workbook;
/*      */ import org.apache.poi.ss.util.CellRangeAddressList;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ExcelWriter
/*      */   extends ExcelBase<ExcelWriter>
/*      */ {
/*   67 */   private AtomicInteger currentRow = new AtomicInteger(0);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean onlyAlias;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Comparator<String> aliasComparator;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private StyleSet styleSet;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Map<String, Integer> headLocationCache;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter() {
/*   95 */     this(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(boolean isXlsx) {
/*  107 */     this(WorkbookUtil.createBook(isXlsx), (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(String destFilePath) {
/*  116 */     this(destFilePath, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(boolean isXlsx, String sheetName) {
/*  129 */     this(WorkbookUtil.createBook(isXlsx), sheetName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(String destFilePath, String sheetName) {
/*  139 */     this(FileUtil.file(destFilePath), sheetName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(File destFile) {
/*  148 */     this(destFile, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(File destFile, String sheetName) {
/*  158 */     this(WorkbookUtil.createBookForWriter(destFile), sheetName);
/*  159 */     this.destFile = destFile;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(Workbook workbook, String sheetName) {
/*  171 */     this(WorkbookUtil.getOrCreateSheet(workbook, sheetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter(Sheet sheet) {
/*  183 */     super(sheet);
/*  184 */     this.styleSet = new StyleSet(this.workbook);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setSheet(int sheetIndex) {
/*  192 */     reset();
/*  193 */     return super.setSheet(sheetIndex);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setSheet(String sheetName) {
/*  199 */     reset();
/*  200 */     return super.setSheet(sheetName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter reset() {
/*  215 */     resetRow();
/*  216 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter renameSheet(String sheetName) {
/*  227 */     return renameSheet(this.workbook.getSheetIndex(this.sheet), sheetName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter renameSheet(int sheet, String sheetName) {
/*  239 */     this.workbook.setSheetName(sheet, sheetName);
/*  240 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter autoSizeColumnAll() {
/*  252 */     int columnCount = getColumnCount();
/*  253 */     for (int i = 0; i < columnCount; i++) {
/*  254 */       autoSizeColumn(i);
/*      */     }
/*  256 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter autoSizeColumn(int columnIndex) {
/*  268 */     this.sheet.autoSizeColumn(columnIndex);
/*  269 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter autoSizeColumn(int columnIndex, boolean useMergedCells) {
/*  282 */     this.sheet.autoSizeColumn(columnIndex, useMergedCells);
/*  283 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter disableDefaultStyle() {
/*  294 */     return setStyleSet((StyleSet)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setStyleSet(StyleSet styleSet) {
/*  305 */     this.styleSet = styleSet;
/*  306 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public StyleSet getStyleSet() {
/*  323 */     return this.styleSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CellStyle getHeadCellStyle() {
/*  332 */     return this.styleSet.headCellStyle;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public CellStyle getCellStyle() {
/*  341 */     if (null == this.styleSet) {
/*  342 */       return null;
/*      */     }
/*  344 */     return this.styleSet.cellStyle;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getCurrentRow() {
/*  353 */     return this.currentRow.get();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getDisposition(String fileName, Charset charset) {
/*  368 */     if (null == charset) {
/*  369 */       charset = CharsetUtil.CHARSET_UTF_8;
/*      */     }
/*      */     
/*  372 */     if (StrUtil.isBlank(fileName))
/*      */     {
/*  374 */       fileName = IdUtil.fastSimpleUUID();
/*      */     }
/*      */     
/*  377 */     fileName = StrUtil.addSuffixIfNot(URLUtil.encodeAll(fileName, charset), isXlsx() ? ".xlsx" : ".xls");
/*  378 */     return StrUtil.format("attachment; filename=\"{}\"", new Object[] { fileName });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getContentType() {
/*  392 */     return isXlsx() ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "application/vnd.ms-excel";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setCurrentRow(int rowIndex) {
/*  402 */     this.currentRow.set(rowIndex);
/*  403 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setCurrentRowToEnd() {
/*  413 */     return setCurrentRow(getRowCount());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter passCurrentRow() {
/*  422 */     this.currentRow.incrementAndGet();
/*  423 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter passRows(int rows) {
/*  433 */     this.currentRow.addAndGet(rows);
/*  434 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter resetRow() {
/*  443 */     this.currentRow.set(0);
/*  444 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setDestFile(File destFile) {
/*  454 */     this.destFile = destFile;
/*  455 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setHeaderAlias(Map<String, String> headerAlias) {
/*  462 */     this.aliasComparator = null;
/*  463 */     return super.setHeaderAlias(headerAlias);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter clearHeaderAlias() {
/*  469 */     this.aliasComparator = null;
/*  470 */     return super.clearHeaderAlias();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter addHeaderAlias(String name, String alias) {
/*  476 */     this.aliasComparator = null;
/*  477 */     return super.addHeaderAlias(name, alias);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setOnlyAlias(boolean isOnlyAlias) {
/*  488 */     this.onlyAlias = isOnlyAlias;
/*  489 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setFreezePane(int rowSplit) {
/*  501 */     return setFreezePane(0, rowSplit);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setFreezePane(int colSplit, int rowSplit) {
/*  513 */     getSheet().createFreezePane(colSplit, rowSplit);
/*  514 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setColumnWidth(int columnIndex, int width) {
/*  526 */     if (columnIndex < 0) {
/*  527 */       this.sheet.setDefaultColumnWidth(width);
/*      */     } else {
/*  529 */       this.sheet.setColumnWidth(columnIndex, width * 256);
/*      */     } 
/*  531 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setDefaultRowHeight(int height) {
/*  542 */     return setRowHeight(-1, height);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setRowHeight(int rownum, int height) {
/*  554 */     if (rownum < 0) {
/*  555 */       this.sheet.setDefaultRowHeightInPoints(height);
/*      */     } else {
/*  557 */       Row row = this.sheet.getRow(rownum);
/*  558 */       if (null != row) {
/*  559 */         row.setHeightInPoints(height);
/*      */       }
/*      */     } 
/*  562 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setHeaderOrFooter(String text, Align align, boolean isFooter) {
/*  575 */     HeaderFooter headerFooter = isFooter ? (HeaderFooter)this.sheet.getFooter() : (HeaderFooter)this.sheet.getHeader();
/*  576 */     switch (align) {
/*      */       case LEFT:
/*  578 */         headerFooter.setLeft(text);
/*      */         break;
/*      */       case RIGHT:
/*  581 */         headerFooter.setRight(text);
/*      */         break;
/*      */       case CENTER:
/*  584 */         headerFooter.setCenter(text);
/*      */         break;
/*      */     } 
/*      */ 
/*      */     
/*  589 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter addSelect(int x, int y, String... selectList) {
/*  602 */     return addSelect(new CellRangeAddressList(y, y, x, x), selectList);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter addSelect(CellRangeAddressList regions, String... selectList) {
/*  614 */     DataValidationHelper validationHelper = this.sheet.getDataValidationHelper();
/*  615 */     DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(selectList);
/*      */ 
/*      */     
/*  618 */     DataValidation dataValidation = validationHelper.createValidation(constraint, regions);
/*      */ 
/*      */     
/*  621 */     if (dataValidation instanceof org.apache.poi.xssf.usermodel.XSSFDataValidation) {
/*  622 */       dataValidation.setSuppressDropDownArrow(true);
/*  623 */       dataValidation.setShowErrorBox(true);
/*      */     } else {
/*  625 */       dataValidation.setSuppressDropDownArrow(false);
/*      */     } 
/*      */     
/*  628 */     return addValidationData(dataValidation);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter addValidationData(DataValidation dataValidation) {
/*  639 */     this.sheet.addValidationData(dataValidation);
/*  640 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter merge(int lastColumn) {
/*  651 */     return merge(lastColumn, (Object)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter merge(int lastColumn, Object content) {
/*  664 */     return merge(lastColumn, content, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter merge(int lastColumn, Object content, boolean isSetHeaderStyle) {
/*  679 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/*      */     
/*  681 */     int rowIndex = this.currentRow.get();
/*  682 */     merge(rowIndex, rowIndex, 0, lastColumn, content, isSetHeaderStyle);
/*      */ 
/*      */     
/*  685 */     if (null != content) {
/*  686 */       this.currentRow.incrementAndGet();
/*      */     }
/*  688 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter merge(int firstRow, int lastRow, int firstColumn, int lastColumn, Object content, boolean isSetHeaderStyle) {
/*  706 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/*      */     
/*  708 */     CellStyle style = null;
/*  709 */     if (null != this.styleSet) {
/*  710 */       style = this.styleSet.getStyleByValueType(content, isSetHeaderStyle);
/*      */     }
/*      */     
/*  713 */     return merge(firstRow, lastRow, firstColumn, lastColumn, content, style);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter merge(int firstRow, int lastRow, int firstColumn, int lastColumn, Object content, CellStyle cellStyle) {
/*  730 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/*      */     
/*  732 */     CellUtil.mergingCells(getSheet(), firstRow, lastRow, firstColumn, lastColumn, cellStyle);
/*      */ 
/*      */     
/*  735 */     if (null != content) {
/*  736 */       Cell cell = getOrCreateCell(firstColumn, firstRow);
/*  737 */       CellUtil.setCellValue(cell, content, cellStyle);
/*      */     } 
/*  739 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter write(Iterable<?> data) {
/*  762 */     return write(data, (0 == getCurrentRow()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter write(Iterable<?> data, boolean isWriteKeyAsHead) {
/*  785 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/*  786 */     boolean isFirst = true;
/*  787 */     for (Object object : data) {
/*  788 */       writeRow(object, (isFirst && isWriteKeyAsHead));
/*  789 */       if (isFirst) {
/*  790 */         isFirst = false;
/*      */       }
/*      */     } 
/*  793 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter write(Iterable<?> data, Comparator<String> comparator) {
/*  814 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/*  815 */     boolean isFirstRow = true;
/*      */     
/*  817 */     for (Object obj : data) {
/*  818 */       Map<?, ?> map; if (obj instanceof Map) {
/*  819 */         map = new TreeMap<>(comparator);
/*  820 */         map.putAll((Map<?, ?>)obj);
/*      */       } else {
/*  822 */         map = BeanUtil.beanToMap(obj, new TreeMap<>(comparator), false, false);
/*      */       } 
/*  824 */       writeRow(map, isFirstRow);
/*  825 */       if (isFirstRow) {
/*  826 */         isFirstRow = false;
/*      */       }
/*      */     } 
/*  829 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeImg(File imgFile, int col1, int row1, int col2, int row2) {
/*  846 */     return writeImg(imgFile, 0, 0, 0, 0, col1, row1, col2, row2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeImg(File imgFile, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
/*  868 */     return writeImg(imgFile, 6, dx1, dy1, dx2, dy2, col1, row1, col2, row2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeImg(File imgFile, int imgType, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
/*  891 */     return writeImg(FileUtil.readBytes(imgFile), imgType, dx1, dy1, dx2, dy2, col1, row1, col2, row2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeImg(byte[] pictureData, int imgType, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
/*  915 */     Drawing<?> patriarch = this.sheet.createDrawingPatriarch();
/*  916 */     ClientAnchor anchor = this.workbook.getCreationHelper().createClientAnchor();
/*  917 */     anchor.setDx1(dx1);
/*  918 */     anchor.setDy1(dy1);
/*  919 */     anchor.setDx2(dx2);
/*  920 */     anchor.setDy2(dy2);
/*  921 */     anchor.setCol1(col1);
/*  922 */     anchor.setRow1(row1);
/*  923 */     anchor.setCol2(col2);
/*  924 */     anchor.setRow2(row2);
/*      */     
/*  926 */     patriarch.createPicture(anchor, this.workbook.addPicture(pictureData, imgType));
/*  927 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeHeadRow(Iterable<?> rowData) {
/*  940 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/*  941 */     this.headLocationCache = new ConcurrentHashMap<>();
/*  942 */     Row row = this.sheet.createRow(this.currentRow.getAndIncrement());
/*  943 */     int i = 0;
/*      */     
/*  945 */     for (Object value : rowData) {
/*  946 */       Cell cell = row.createCell(i);
/*  947 */       CellUtil.setCellValue(cell, value, this.styleSet, true);
/*  948 */       this.headLocationCache.put(StrUtil.toString(value), Integer.valueOf(i));
/*  949 */       i++;
/*      */     } 
/*  951 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeSecHeadRow(Iterable<?> rowData) {
/*  968 */     Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
/*  969 */     Iterator<?> iterator = rowData.iterator();
/*      */     
/*  971 */     if (row.getLastCellNum() != 0) {
/*  972 */       for (int i = 0; i < this.workbook.getSpreadsheetVersion().getMaxColumns(); i++) {
/*  973 */         Cell cell = row.getCell(i);
/*  974 */         if (cell == null)
/*      */         {
/*      */           
/*  977 */           if (iterator.hasNext()) {
/*  978 */             cell = row.createCell(i);
/*  979 */             CellUtil.setCellValue(cell, iterator.next(), this.styleSet, true);
/*      */           } else {
/*      */             break;
/*      */           }  } 
/*      */       } 
/*      */     } else {
/*  985 */       writeHeadRow(rowData);
/*      */     } 
/*  987 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeRow(Object rowBean, boolean isWriteKeyAsHead) {
/*      */     Map<?, ?> rowMap;
/* 1008 */     if (rowBean instanceof Iterable) {
/* 1009 */       return writeRow((Iterable)rowBean);
/*      */     }
/*      */     
/* 1012 */     if (rowBean instanceof Map)
/* 1013 */     { if (MapUtil.isNotEmpty(this.headerAlias)) {
/* 1014 */         rowMap = MapUtil.newTreeMap((Map)rowBean, getCachedAliasComparator());
/*      */       } else {
/* 1016 */         rowMap = (Map)rowBean;
/*      */       }  }
/* 1018 */     else { if (rowBean instanceof org.apache.poi.common.usermodel.Hyperlink)
/*      */       {
/* 1020 */         return writeRow(CollUtil.newArrayList(new Object[] { rowBean }, ), isWriteKeyAsHead); } 
/* 1021 */       if (BeanUtil.isBean(rowBean.getClass())) {
/* 1022 */         if (MapUtil.isEmpty(this.headerAlias)) {
/* 1023 */           rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap<>(), false, false);
/*      */         } else {
/*      */           
/* 1026 */           rowMap = BeanUtil.beanToMap(rowBean, new TreeMap<>(getCachedAliasComparator()), false, false);
/*      */         } 
/*      */       } else {
/*      */         
/* 1030 */         return writeRow(CollUtil.newArrayList(new Object[] { rowBean }, ), isWriteKeyAsHead);
/*      */       }  }
/* 1032 */      return writeRow(rowMap, isWriteKeyAsHead);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeRow(Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
/* 1044 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/* 1045 */     if (MapUtil.isEmpty(rowMap))
/*      */     {
/* 1047 */       return passCurrentRow();
/*      */     }
/*      */     
/* 1050 */     Table<?, ?, ?> aliasTable = aliasTable(rowMap);
/* 1051 */     if (isWriteKeyAsHead) {
/*      */       
/* 1053 */       writeHeadRow(aliasTable.columnKeys());
/*      */       
/* 1055 */       int i = 0;
/* 1056 */       for (Object key : aliasTable.rowKeySet()) {
/* 1057 */         this.headLocationCache.putIfAbsent(StrUtil.toString(key), Integer.valueOf(i));
/* 1058 */         i++;
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/* 1063 */     if (MapUtil.isNotEmpty(this.headLocationCache)) {
/* 1064 */       Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
/*      */       
/* 1066 */       for (Table.Cell<?, ?, ?> cell : aliasTable) {
/*      */         
/* 1068 */         Integer location = this.headLocationCache.get(StrUtil.toString(cell.getRowKey()));
/* 1069 */         if (null == location)
/*      */         {
/* 1071 */           location = this.headLocationCache.get(StrUtil.toString(cell.getColumnKey()));
/*      */         }
/* 1073 */         if (null != location) {
/* 1074 */           CellUtil.setCellValue(CellUtil.getOrCreateCell(row, location.intValue()), cell.getValue(), this.styleSet, false);
/*      */         }
/*      */       } 
/*      */     } else {
/* 1078 */       writeRow(aliasTable.values());
/*      */     } 
/* 1080 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeRow(Iterable<?> rowData) {
/* 1093 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/* 1094 */     RowUtil.writeRow(this.sheet.createRow(this.currentRow.getAndIncrement()), rowData, this.styleSet, false);
/* 1095 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeCellValue(String locationRef, Object value) {
/* 1107 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 1108 */     return writeCellValue(cellLocation.getX(), cellLocation.getY(), value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter writeCellValue(int x, int y, Object value) {
/* 1121 */     Cell cell = getOrCreateCell(x, y);
/* 1122 */     CellUtil.setCellValue(cell, value, this.styleSet, false);
/* 1123 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setStyle(CellStyle style, String locationRef) {
/* 1140 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 1141 */     return setStyle(style, cellLocation.getX(), cellLocation.getY());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setStyle(CellStyle style, int x, int y) {
/* 1159 */     Cell cell = getOrCreateCell(x, y);
/* 1160 */     cell.setCellStyle(style);
/* 1161 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setRowStyle(int y, CellStyle style) {
/* 1174 */     getOrCreateRow(y).setRowStyle(style);
/* 1175 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setRowStyleIfHasData(int y, CellStyle style) {
/* 1191 */     if (y < 0) {
/* 1192 */       throw new IllegalArgumentException("Invalid row number (" + y + ")");
/*      */     }
/* 1194 */     int columnCount = getColumnCount();
/* 1195 */     for (int i = 0; i < columnCount; i++) {
/* 1196 */       setStyle(style, i, y);
/*      */     }
/* 1198 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setColumnStyle(int x, CellStyle style) {
/* 1210 */     this.sheet.setDefaultColumnStyle(x, style);
/* 1211 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter setColumnStyleIfHasData(int x, int y, CellStyle style) {
/* 1228 */     if (x < 0) {
/* 1229 */       throw new IllegalArgumentException("Invalid column number (" + x + ")");
/*      */     }
/* 1231 */     if (y < 0) {
/* 1232 */       throw new IllegalArgumentException("Invalid row number (" + y + ")");
/*      */     }
/* 1234 */     int rowCount = getRowCount();
/* 1235 */     for (int i = y; i < rowCount; i++) {
/* 1236 */       setStyle(style, x, i);
/*      */     }
/* 1238 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public Font createFont() {
/* 1248 */     return getWorkbook().createFont();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter flush() throws IORuntimeException {
/* 1260 */     return flush(this.destFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter flush(File destFile) throws IORuntimeException {
/* 1273 */     Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).", new Object[0]);
/* 1274 */     return flush(FileUtil.getOutputStream(destFile), true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter flush(OutputStream out) throws IORuntimeException {
/* 1285 */     return flush(out, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public ExcelWriter flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
/* 1298 */     Assert.isFalse(this.isClosed, "ExcelWriter has been closed!", new Object[0]);
/*      */     try {
/* 1300 */       this.workbook.write(out);
/* 1301 */       out.flush();
/* 1302 */     } catch (IOException e) {
/* 1303 */       throw new IORuntimeException(e);
/*      */     } finally {
/* 1305 */       if (isCloseOut) {
/* 1306 */         IoUtil.close(out);
/*      */       }
/*      */     } 
/* 1309 */     return this;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void close() {
/* 1318 */     if (null != this.destFile) {
/* 1319 */       flush();
/*      */     }
/* 1321 */     closeWithoutFlush();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   protected void closeWithoutFlush() {
/* 1328 */     super.close();
/*      */ 
/*      */     
/* 1331 */     this.currentRow = null;
/* 1332 */     this.styleSet = null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Table<?, ?, ?> aliasTable(Map<?, ?> rowMap) {
/* 1345 */     RowKeyTable rowKeyTable = new RowKeyTable(new LinkedHashMap<>(), cn.hutool.core.map.TableMap::new);
/* 1346 */     if (MapUtil.isEmpty(this.headerAlias)) {
/* 1347 */       rowMap.forEach((key, value) -> filteredTable.put(key, key, value));
/*      */     } else {
/* 1349 */       rowMap.forEach((key, value) -> {
/*      */             String aliasName = this.headerAlias.get(StrUtil.toString(key));
/*      */ 
/*      */             
/*      */             if (null != aliasName) {
/*      */               filteredTable.put(key, aliasName, value);
/*      */             } else if (false == this.onlyAlias) {
/*      */               filteredTable.put(key, key, value);
/*      */             } 
/*      */           });
/*      */     } 
/*      */     
/* 1361 */     return (Table<?, ?, ?>)rowKeyTable;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Comparator<String> getCachedAliasComparator() {
/*      */     IndexedComparator indexedComparator;
/* 1371 */     if (MapUtil.isEmpty(this.headerAlias)) {
/* 1372 */       return null;
/*      */     }
/* 1374 */     Comparator<String> aliasComparator = this.aliasComparator;
/* 1375 */     if (null == aliasComparator) {
/* 1376 */       Set<String> keySet = this.headerAlias.keySet();
/* 1377 */       indexedComparator = new IndexedComparator(keySet.toArray((Object[])new String[0]));
/* 1378 */       this.aliasComparator = (Comparator<String>)indexedComparator;
/*      */     } 
/* 1380 */     return (Comparator<String>)indexedComparator;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\ExcelWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */