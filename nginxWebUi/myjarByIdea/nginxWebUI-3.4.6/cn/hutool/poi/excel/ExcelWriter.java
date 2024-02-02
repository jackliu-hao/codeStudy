package cn.hutool.poi.excel;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.comparator.IndexedComparator;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.TableMap;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.cell.CellLocation;
import cn.hutool.poi.excel.cell.CellUtil;
import cn.hutool.poi.excel.style.Align;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.SerializedLambda;
import java.nio.charset.Charset;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.common.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

public class ExcelWriter extends ExcelBase<ExcelWriter> {
   private AtomicInteger currentRow;
   private boolean onlyAlias;
   private Comparator<String> aliasComparator;
   private StyleSet styleSet;
   private Map<String, Integer> headLocationCache;

   public ExcelWriter() {
      this(false);
   }

   public ExcelWriter(boolean isXlsx) {
      this((Workbook)WorkbookUtil.createBook(isXlsx), (String)null);
   }

   public ExcelWriter(String destFilePath) {
      this((String)destFilePath, (String)null);
   }

   public ExcelWriter(boolean isXlsx, String sheetName) {
      this(WorkbookUtil.createBook(isXlsx), sheetName);
   }

   public ExcelWriter(String destFilePath, String sheetName) {
      this(FileUtil.file(destFilePath), sheetName);
   }

   public ExcelWriter(File destFile) {
      this((File)destFile, (String)null);
   }

   public ExcelWriter(File destFile, String sheetName) {
      this(WorkbookUtil.createBookForWriter(destFile), sheetName);
      this.destFile = destFile;
   }

   public ExcelWriter(Workbook workbook, String sheetName) {
      this(WorkbookUtil.getOrCreateSheet(workbook, sheetName));
   }

   public ExcelWriter(Sheet sheet) {
      super(sheet);
      this.currentRow = new AtomicInteger(0);
      this.styleSet = new StyleSet(this.workbook);
   }

   public ExcelWriter setSheet(int sheetIndex) {
      this.reset();
      return (ExcelWriter)super.setSheet(sheetIndex);
   }

   public ExcelWriter setSheet(String sheetName) {
      this.reset();
      return (ExcelWriter)super.setSheet(sheetName);
   }

   public ExcelWriter reset() {
      this.resetRow();
      return this;
   }

   public ExcelWriter renameSheet(String sheetName) {
      return this.renameSheet(this.workbook.getSheetIndex(this.sheet), sheetName);
   }

   public ExcelWriter renameSheet(int sheet, String sheetName) {
      this.workbook.setSheetName(sheet, sheetName);
      return this;
   }

   public ExcelWriter autoSizeColumnAll() {
      int columnCount = this.getColumnCount();

      for(int i = 0; i < columnCount; ++i) {
         this.autoSizeColumn(i);
      }

      return this;
   }

   public ExcelWriter autoSizeColumn(int columnIndex) {
      this.sheet.autoSizeColumn(columnIndex);
      return this;
   }

   public ExcelWriter autoSizeColumn(int columnIndex, boolean useMergedCells) {
      this.sheet.autoSizeColumn(columnIndex, useMergedCells);
      return this;
   }

   public ExcelWriter disableDefaultStyle() {
      return this.setStyleSet((StyleSet)null);
   }

   public ExcelWriter setStyleSet(StyleSet styleSet) {
      this.styleSet = styleSet;
      return this;
   }

   public StyleSet getStyleSet() {
      return this.styleSet;
   }

   public CellStyle getHeadCellStyle() {
      return this.styleSet.headCellStyle;
   }

   public CellStyle getCellStyle() {
      return null == this.styleSet ? null : this.styleSet.cellStyle;
   }

   public int getCurrentRow() {
      return this.currentRow.get();
   }

   public String getDisposition(String fileName, Charset charset) {
      if (null == charset) {
         charset = CharsetUtil.CHARSET_UTF_8;
      }

      if (StrUtil.isBlank(fileName)) {
         fileName = IdUtil.fastSimpleUUID();
      }

      fileName = StrUtil.addSuffixIfNot(URLUtil.encodeAll(fileName, charset), this.isXlsx() ? ".xlsx" : ".xls");
      return StrUtil.format("attachment; filename=\"{}\"", new Object[]{fileName});
   }

   public String getContentType() {
      return this.isXlsx() ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" : "application/vnd.ms-excel";
   }

   public ExcelWriter setCurrentRow(int rowIndex) {
      this.currentRow.set(rowIndex);
      return this;
   }

   public ExcelWriter setCurrentRowToEnd() {
      return this.setCurrentRow(this.getRowCount());
   }

   public ExcelWriter passCurrentRow() {
      this.currentRow.incrementAndGet();
      return this;
   }

   public ExcelWriter passRows(int rows) {
      this.currentRow.addAndGet(rows);
      return this;
   }

   public ExcelWriter resetRow() {
      this.currentRow.set(0);
      return this;
   }

   public ExcelWriter setDestFile(File destFile) {
      this.destFile = destFile;
      return this;
   }

   public ExcelWriter setHeaderAlias(Map<String, String> headerAlias) {
      this.aliasComparator = null;
      return (ExcelWriter)super.setHeaderAlias(headerAlias);
   }

   public ExcelWriter clearHeaderAlias() {
      this.aliasComparator = null;
      return (ExcelWriter)super.clearHeaderAlias();
   }

   public ExcelWriter addHeaderAlias(String name, String alias) {
      this.aliasComparator = null;
      return (ExcelWriter)super.addHeaderAlias(name, alias);
   }

   public ExcelWriter setOnlyAlias(boolean isOnlyAlias) {
      this.onlyAlias = isOnlyAlias;
      return this;
   }

   public ExcelWriter setFreezePane(int rowSplit) {
      return this.setFreezePane(0, rowSplit);
   }

   public ExcelWriter setFreezePane(int colSplit, int rowSplit) {
      this.getSheet().createFreezePane(colSplit, rowSplit);
      return this;
   }

   public ExcelWriter setColumnWidth(int columnIndex, int width) {
      if (columnIndex < 0) {
         this.sheet.setDefaultColumnWidth(width);
      } else {
         this.sheet.setColumnWidth(columnIndex, width * 256);
      }

      return this;
   }

   public ExcelWriter setDefaultRowHeight(int height) {
      return this.setRowHeight(-1, height);
   }

   public ExcelWriter setRowHeight(int rownum, int height) {
      if (rownum < 0) {
         this.sheet.setDefaultRowHeightInPoints((float)height);
      } else {
         Row row = this.sheet.getRow(rownum);
         if (null != row) {
            row.setHeightInPoints((float)height);
         }
      }

      return this;
   }

   public ExcelWriter setHeaderOrFooter(String text, Align align, boolean isFooter) {
      HeaderFooter headerFooter = isFooter ? this.sheet.getFooter() : this.sheet.getHeader();
      switch (align) {
         case LEFT:
            ((HeaderFooter)headerFooter).setLeft(text);
            break;
         case RIGHT:
            ((HeaderFooter)headerFooter).setRight(text);
            break;
         case CENTER:
            ((HeaderFooter)headerFooter).setCenter(text);
      }

      return this;
   }

   public ExcelWriter addSelect(int x, int y, String... selectList) {
      return this.addSelect(new CellRangeAddressList(y, y, x, x), selectList);
   }

   public ExcelWriter addSelect(CellRangeAddressList regions, String... selectList) {
      DataValidationHelper validationHelper = this.sheet.getDataValidationHelper();
      DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(selectList);
      DataValidation dataValidation = validationHelper.createValidation(constraint, regions);
      if (dataValidation instanceof XSSFDataValidation) {
         dataValidation.setSuppressDropDownArrow(true);
         dataValidation.setShowErrorBox(true);
      } else {
         dataValidation.setSuppressDropDownArrow(false);
      }

      return this.addValidationData(dataValidation);
   }

   public ExcelWriter addValidationData(DataValidation dataValidation) {
      this.sheet.addValidationData(dataValidation);
      return this;
   }

   public ExcelWriter merge(int lastColumn) {
      return this.merge(lastColumn, (Object)null);
   }

   public ExcelWriter merge(int lastColumn, Object content) {
      return this.merge(lastColumn, content, true);
   }

   public ExcelWriter merge(int lastColumn, Object content, boolean isSetHeaderStyle) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      int rowIndex = this.currentRow.get();
      this.merge(rowIndex, rowIndex, 0, lastColumn, content, isSetHeaderStyle);
      if (null != content) {
         this.currentRow.incrementAndGet();
      }

      return this;
   }

   public ExcelWriter merge(int firstRow, int lastRow, int firstColumn, int lastColumn, Object content, boolean isSetHeaderStyle) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      CellStyle style = null;
      if (null != this.styleSet) {
         style = this.styleSet.getStyleByValueType(content, isSetHeaderStyle);
      }

      return this.merge(firstRow, lastRow, firstColumn, lastColumn, content, style);
   }

   public ExcelWriter merge(int firstRow, int lastRow, int firstColumn, int lastColumn, Object content, CellStyle cellStyle) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      CellUtil.mergingCells(this.getSheet(), firstRow, lastRow, firstColumn, lastColumn, cellStyle);
      if (null != content) {
         Cell cell = this.getOrCreateCell(firstColumn, firstRow);
         CellUtil.setCellValue(cell, content, cellStyle);
      }

      return this;
   }

   public ExcelWriter write(Iterable<?> data) {
      return this.write(data, 0 == this.getCurrentRow());
   }

   public ExcelWriter write(Iterable<?> data, boolean isWriteKeyAsHead) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      boolean isFirst = true;
      Iterator var4 = data.iterator();

      while(var4.hasNext()) {
         Object object = var4.next();
         this.writeRow(object, isFirst && isWriteKeyAsHead);
         if (isFirst) {
            isFirst = false;
         }
      }

      return this;
   }

   public ExcelWriter write(Iterable<?> data, Comparator<String> comparator) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      boolean isFirstRow = true;
      Iterator var5 = data.iterator();

      while(var5.hasNext()) {
         Object obj = var5.next();
         Object map;
         if (obj instanceof Map) {
            map = new TreeMap(comparator);
            ((Map)map).putAll((Map)obj);
         } else {
            map = BeanUtil.beanToMap(obj, new TreeMap(comparator), false, false);
         }

         this.writeRow((Map)map, isFirstRow);
         if (isFirstRow) {
            isFirstRow = false;
         }
      }

      return this;
   }

   public ExcelWriter writeImg(File imgFile, int col1, int row1, int col2, int row2) {
      return this.writeImg(imgFile, 0, 0, 0, 0, col1, row1, col2, row2);
   }

   public ExcelWriter writeImg(File imgFile, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
      return this.writeImg((File)imgFile, 6, dx1, dy1, dx2, dy2, col1, row1, col2, row2);
   }

   public ExcelWriter writeImg(File imgFile, int imgType, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
      return this.writeImg(FileUtil.readBytes(imgFile), imgType, dx1, dy1, dx2, dy2, col1, row1, col2, row2);
   }

   public ExcelWriter writeImg(byte[] pictureData, int imgType, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
      Drawing<?> patriarch = this.sheet.createDrawingPatriarch();
      ClientAnchor anchor = this.workbook.getCreationHelper().createClientAnchor();
      anchor.setDx1(dx1);
      anchor.setDy1(dy1);
      anchor.setDx2(dx2);
      anchor.setDy2(dy2);
      anchor.setCol1(col1);
      anchor.setRow1(row1);
      anchor.setCol2(col2);
      anchor.setRow2(row2);
      patriarch.createPicture(anchor, this.workbook.addPicture(pictureData, imgType));
      return this;
   }

   public ExcelWriter writeHeadRow(Iterable<?> rowData) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      this.headLocationCache = new ConcurrentHashMap();
      Row row = this.sheet.createRow(this.currentRow.getAndIncrement());
      int i = 0;

      for(Iterator var5 = rowData.iterator(); var5.hasNext(); ++i) {
         Object value = var5.next();
         Cell cell = row.createCell(i);
         CellUtil.setCellValue(cell, value, this.styleSet, true);
         this.headLocationCache.put(StrUtil.toString(value), i);
      }

      return this;
   }

   public ExcelWriter writeSecHeadRow(Iterable<?> rowData) {
      Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
      Iterator<?> iterator = rowData.iterator();
      if (row.getLastCellNum() != 0) {
         for(int i = 0; i < this.workbook.getSpreadsheetVersion().getMaxColumns(); ++i) {
            Cell cell = row.getCell(i);
            if (cell == null) {
               if (!iterator.hasNext()) {
                  break;
               }

               cell = row.createCell(i);
               CellUtil.setCellValue(cell, iterator.next(), this.styleSet, true);
            }
         }
      } else {
         this.writeHeadRow(rowData);
      }

      return this;
   }

   public ExcelWriter writeRow(Object rowBean, boolean isWriteKeyAsHead) {
      if (rowBean instanceof Iterable) {
         return this.writeRow((Iterable)rowBean);
      } else {
         Object rowMap;
         if (rowBean instanceof Map) {
            if (MapUtil.isNotEmpty(this.headerAlias)) {
               rowMap = MapUtil.newTreeMap((Map)rowBean, this.getCachedAliasComparator());
            } else {
               rowMap = (Map)rowBean;
            }
         } else {
            if (rowBean instanceof Hyperlink) {
               return this.writeRow((Object)CollUtil.newArrayList(rowBean), isWriteKeyAsHead);
            }

            if (!BeanUtil.isBean(rowBean.getClass())) {
               return this.writeRow((Object)CollUtil.newArrayList(rowBean), isWriteKeyAsHead);
            }

            if (MapUtil.isEmpty(this.headerAlias)) {
               rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap(), false, false);
            } else {
               rowMap = BeanUtil.beanToMap(rowBean, new TreeMap(this.getCachedAliasComparator()), false, false);
            }
         }

         return this.writeRow((Map)rowMap, isWriteKeyAsHead);
      }
   }

   public ExcelWriter writeRow(Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      if (MapUtil.isEmpty(rowMap)) {
         return this.passCurrentRow();
      } else {
         Table<?, ?, ?> aliasTable = this.aliasTable(rowMap);
         if (isWriteKeyAsHead) {
            this.writeHeadRow(aliasTable.columnKeys());
            int i = 0;

            for(Iterator var5 = aliasTable.rowKeySet().iterator(); var5.hasNext(); ++i) {
               Object key = var5.next();
               this.headLocationCache.putIfAbsent(StrUtil.toString(key), i);
            }
         }

         if (MapUtil.isNotEmpty(this.headLocationCache)) {
            Row row = RowUtil.getOrCreateRow(this.sheet, this.currentRow.getAndIncrement());
            Iterator var10 = aliasTable.iterator();

            while(var10.hasNext()) {
               Table.Cell<?, ?, ?> cell = (Table.Cell)var10.next();
               Integer location = (Integer)this.headLocationCache.get(StrUtil.toString(cell.getRowKey()));
               if (null == location) {
                  location = (Integer)this.headLocationCache.get(StrUtil.toString(cell.getColumnKey()));
               }

               if (null != location) {
                  CellUtil.setCellValue(CellUtil.getOrCreateCell(row, location), cell.getValue(), this.styleSet, false);
               }
            }
         } else {
            this.writeRow(aliasTable.values());
         }

         return this;
      }
   }

   public ExcelWriter writeRow(Iterable<?> rowData) {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");
      RowUtil.writeRow(this.sheet.createRow(this.currentRow.getAndIncrement()), rowData, this.styleSet, false);
      return this;
   }

   public ExcelWriter writeCellValue(String locationRef, Object value) {
      CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
      return this.writeCellValue(cellLocation.getX(), cellLocation.getY(), value);
   }

   public ExcelWriter writeCellValue(int x, int y, Object value) {
      Cell cell = this.getOrCreateCell(x, y);
      CellUtil.setCellValue(cell, value, this.styleSet, false);
      return this;
   }

   public ExcelWriter setStyle(CellStyle style, String locationRef) {
      CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
      return this.setStyle(style, cellLocation.getX(), cellLocation.getY());
   }

   public ExcelWriter setStyle(CellStyle style, int x, int y) {
      Cell cell = this.getOrCreateCell(x, y);
      cell.setCellStyle(style);
      return this;
   }

   public ExcelWriter setRowStyle(int y, CellStyle style) {
      this.getOrCreateRow(y).setRowStyle(style);
      return this;
   }

   public ExcelWriter setRowStyleIfHasData(int y, CellStyle style) {
      if (y < 0) {
         throw new IllegalArgumentException("Invalid row number (" + y + ")");
      } else {
         int columnCount = this.getColumnCount();

         for(int i = 0; i < columnCount; ++i) {
            this.setStyle(style, i, y);
         }

         return this;
      }
   }

   public ExcelWriter setColumnStyle(int x, CellStyle style) {
      this.sheet.setDefaultColumnStyle(x, style);
      return this;
   }

   public ExcelWriter setColumnStyleIfHasData(int x, int y, CellStyle style) {
      if (x < 0) {
         throw new IllegalArgumentException("Invalid column number (" + x + ")");
      } else if (y < 0) {
         throw new IllegalArgumentException("Invalid row number (" + y + ")");
      } else {
         int rowCount = this.getRowCount();

         for(int i = y; i < rowCount; ++i) {
            this.setStyle(style, x, i);
         }

         return this;
      }
   }

   public Font createFont() {
      return this.getWorkbook().createFont();
   }

   public ExcelWriter flush() throws IORuntimeException {
      return this.flush(this.destFile);
   }

   public ExcelWriter flush(File destFile) throws IORuntimeException {
      Assert.notNull(destFile, "[destFile] is null, and you must call setDestFile(File) first or call flush(OutputStream).");
      return this.flush(FileUtil.getOutputStream(destFile), true);
   }

   public ExcelWriter flush(OutputStream out) throws IORuntimeException {
      return this.flush(out, false);
   }

   public ExcelWriter flush(OutputStream out, boolean isCloseOut) throws IORuntimeException {
      Assert.isFalse(this.isClosed, "ExcelWriter has been closed!");

      try {
         this.workbook.write(out);
         out.flush();
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      } finally {
         if (isCloseOut) {
            IoUtil.close(out);
         }

      }

      return this;
   }

   public void close() {
      if (null != this.destFile) {
         this.flush();
      }

      this.closeWithoutFlush();
   }

   protected void closeWithoutFlush() {
      super.close();
      this.currentRow = null;
      this.styleSet = null;
   }

   private Table<?, ?, ?> aliasTable(Map<?, ?> rowMap) {
      Table<Object, Object, Object> filteredTable = new RowKeyTable(new LinkedHashMap(), TableMap::new);
      if (MapUtil.isEmpty(this.headerAlias)) {
         rowMap.forEach((key, value) -> {
            filteredTable.put(key, key, value);
         });
      } else {
         rowMap.forEach((key, value) -> {
            String aliasName = (String)this.headerAlias.get(StrUtil.toString(key));
            if (null != aliasName) {
               filteredTable.put(key, aliasName, value);
            } else if (!this.onlyAlias) {
               filteredTable.put(key, key, value);
            }

         });
      }

      return filteredTable;
   }

   private Comparator<String> getCachedAliasComparator() {
      if (MapUtil.isEmpty(this.headerAlias)) {
         return null;
      } else {
         Comparator<String> aliasComparator = this.aliasComparator;
         if (null == aliasComparator) {
            Set<String> keySet = this.headerAlias.keySet();
            aliasComparator = new IndexedComparator(keySet.toArray(new String[0]));
            this.aliasComparator = (Comparator)aliasComparator;
         }

         return (Comparator)aliasComparator;
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "<init>":
            if (lambda.getImplMethodKind() == 8 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/builder/Builder") && lambda.getFunctionalInterfaceMethodName().equals("build") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/map/TableMap") && lambda.getImplMethodSignature().equals("()V")) {
               return TableMap::new;
            }
         default:
            throw new IllegalArgumentException("Invalid lambda deserialization");
      }
   }
}
