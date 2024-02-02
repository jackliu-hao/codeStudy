package cn.hutool.poi.excel.cell;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.StyleSet;
import cn.hutool.poi.excel.cell.setters.CellSetterFactory;
import cn.hutool.poi.excel.cell.values.ErrorCellValue;
import cn.hutool.poi.excel.cell.values.NumericCellValue;
import cn.hutool.poi.excel.editors.TrimEditor;
import java.util.function.Supplier;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.util.SheetUtil;

public class CellUtil {
   public static Object getCellValue(Cell cell) {
      return getCellValue(cell, false);
   }

   public static Object getCellValue(Cell cell, boolean isTrimCellValue) {
      return null == cell ? null : getCellValue(cell, cell.getCellType(), isTrimCellValue);
   }

   public static Object getCellValue(Cell cell, CellEditor cellEditor) {
      return getCellValue(cell, (CellType)null, cellEditor);
   }

   public static Object getCellValue(Cell cell, CellType cellType, boolean isTrimCellValue) {
      return getCellValue(cell, cellType, isTrimCellValue ? new TrimEditor() : null);
   }

   public static Object getCellValue(Cell cell, CellType cellType, CellEditor cellEditor) {
      if (null == cell) {
         return null;
      } else if (cell instanceof NullCell) {
         return null == cellEditor ? null : cellEditor.edit(cell, (Object)null);
      } else {
         if (null == cellType) {
            cellType = cell.getCellType();
         }

         Cell mergedCell = getMergedRegionCell(cell);
         if (mergedCell != cell) {
            cell = mergedCell;
            cellType = mergedCell.getCellType();
         }

         Object value;
         switch (cellType) {
            case NUMERIC:
               value = (new NumericCellValue(cell)).getValue();
               break;
            case BOOLEAN:
               value = cell.getBooleanCellValue();
               break;
            case FORMULA:
               value = getCellValue(cell, cell.getCachedFormulaResultType(), cellEditor);
               break;
            case BLANK:
               value = "";
               break;
            case ERROR:
               value = (new ErrorCellValue(cell)).getValue();
               break;
            default:
               value = cell.getStringCellValue();
         }

         return null == cellEditor ? value : cellEditor.edit(cell, value);
      }
   }

   public static void setCellValue(Cell cell, Object value, StyleSet styleSet, boolean isHeader) {
      if (null != cell) {
         if (null != styleSet) {
            cell.setCellStyle(styleSet.getStyleByValueType(value, isHeader));
         }

         setCellValue(cell, value);
      }
   }

   public static void setCellValue(Cell cell, Object value, CellStyle style) {
      setCellValue(cell, (cell1) -> {
         setCellValue(cell, value);
         if (null != style) {
            cell1.setCellStyle(style);
         }

      });
   }

   public static void setCellValue(Cell cell, Object value) {
      if (null != cell) {
         if (CellType.BLANK != cell.getCellType()) {
            cell.setBlank();
         }

         CellSetterFactory.createCellSetter(value).setValue(cell);
      }
   }

   public static Cell getCell(Row row, int cellIndex) {
      if (null == row) {
         return null;
      } else {
         Cell cell = row.getCell(cellIndex);
         return (Cell)(null == cell ? new NullCell(row, cellIndex) : cell);
      }
   }

   public static Cell getOrCreateCell(Row row, int cellIndex) {
      if (null == row) {
         return null;
      } else {
         Cell cell = row.getCell(cellIndex);
         if (null == cell) {
            cell = row.createCell(cellIndex);
         }

         return cell;
      }
   }

   public static boolean isMergedRegion(Sheet sheet, String locationRef) {
      CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
      return isMergedRegion(sheet, cellLocation.getX(), cellLocation.getY());
   }

   public static boolean isMergedRegion(Cell cell) {
      return isMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex());
   }

   public static boolean isMergedRegion(Sheet sheet, int x, int y) {
      if (sheet != null) {
         int sheetMergeCount = sheet.getNumMergedRegions();

         for(int i = 0; i < sheetMergeCount; ++i) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            if (y >= ca.getFirstRow() && y <= ca.getLastRow() && x >= ca.getFirstColumn() && x <= ca.getLastColumn()) {
               return true;
            }
         }
      }

      return false;
   }

   public static CellRangeAddress getCellRangeAddress(Sheet sheet, String locationRef) {
      CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
      return getCellRangeAddress(sheet, cellLocation.getX(), cellLocation.getY());
   }

   public static CellRangeAddress getCellRangeAddress(Cell cell) {
      return getCellRangeAddress(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex());
   }

   public static CellRangeAddress getCellRangeAddress(Sheet sheet, int x, int y) {
      if (sheet != null) {
         int sheetMergeCount = sheet.getNumMergedRegions();

         for(int i = 0; i < sheetMergeCount; ++i) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            if (y >= ca.getFirstRow() && y <= ca.getLastRow() && x >= ca.getFirstColumn() && x <= ca.getLastColumn()) {
               return ca;
            }
         }
      }

      return null;
   }

   public static void setMergedRegionStyle(Cell cell, CellStyle cellStyle) {
      CellRangeAddress cellRangeAddress = getCellRangeAddress(cell);
      if (cellRangeAddress != null) {
         setMergeCellStyle(cellStyle, cellRangeAddress, cell.getSheet());
      }

   }

   public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn) {
      return mergingCells(sheet, firstRow, lastRow, firstColumn, lastColumn, (CellStyle)null);
   }

   public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn, CellStyle cellStyle) {
      CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn);
      setMergeCellStyle(cellStyle, cellRangeAddress, sheet);
      return sheet.addMergedRegion(cellRangeAddress);
   }

   public static Object getMergedRegionValue(Sheet sheet, String locationRef) {
      CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
      return getMergedRegionValue(sheet, cellLocation.getX(), cellLocation.getY());
   }

   public static Object getMergedRegionValue(Sheet sheet, int x, int y) {
      return getCellValue(SheetUtil.getCell(sheet, x, y));
   }

   public static Cell getMergedRegionCell(Cell cell) {
      return null == cell ? null : (Cell)ObjectUtil.defaultIfNull(getCellIfMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex()), (Object)cell);
   }

   public static Cell getMergedRegionCell(Sheet sheet, int x, int y) {
      return (Cell)ObjectUtil.defaultIfNull(getCellIfMergedRegion(sheet, x, y), (Supplier)(() -> {
         return SheetUtil.getCell(sheet, y, x);
      }));
   }

   public static void setComment(Cell cell, String commentText, String commentAuthor, ClientAnchor anchor) {
      Sheet sheet = cell.getSheet();
      Workbook wb = sheet.getWorkbook();
      Drawing<?> drawing = sheet.createDrawingPatriarch();
      CreationHelper factory = wb.getCreationHelper();
      if (anchor == null) {
         anchor = factory.createClientAnchor();
         anchor.setCol1(cell.getColumnIndex() + 1);
         anchor.setCol2(cell.getColumnIndex() + 3);
         anchor.setRow1(cell.getRowIndex());
         anchor.setRow2(cell.getRowIndex() + 2);
      }

      Comment comment = drawing.createCellComment(anchor);
      comment.setString(factory.createRichTextString(commentText));
      comment.setAuthor(StrUtil.nullToEmpty(commentAuthor));
      cell.setCellComment(comment);
   }

   private static Cell getCellIfMergedRegion(Sheet sheet, int x, int y) {
      int sheetMergeCount = sheet.getNumMergedRegions();

      for(int i = 0; i < sheetMergeCount; ++i) {
         CellRangeAddress ca = sheet.getMergedRegion(i);
         if (ca.isInRange(y, x)) {
            return SheetUtil.getCell(sheet, ca.getFirstRow(), ca.getFirstColumn());
         }
      }

      return null;
   }

   private static void setMergeCellStyle(CellStyle cellStyle, CellRangeAddress cellRangeAddress, Sheet sheet) {
      if (null != cellStyle) {
         RegionUtil.setBorderTop(cellStyle.getBorderTop(), cellRangeAddress, sheet);
         RegionUtil.setBorderRight(cellStyle.getBorderRight(), cellRangeAddress, sheet);
         RegionUtil.setBorderBottom(cellStyle.getBorderBottom(), cellRangeAddress, sheet);
         RegionUtil.setBorderLeft(cellStyle.getBorderLeft(), cellRangeAddress, sheet);
         RegionUtil.setTopBorderColor(cellStyle.getTopBorderColor(), cellRangeAddress, sheet);
         RegionUtil.setRightBorderColor(cellStyle.getRightBorderColor(), cellRangeAddress, sheet);
         RegionUtil.setLeftBorderColor(cellStyle.getLeftBorderColor(), cellRangeAddress, sheet);
         RegionUtil.setBottomBorderColor(cellStyle.getBottomBorderColor(), cellRangeAddress, sheet);
      }

   }
}
