/*     */ package cn.hutool.poi.excel.cell;
/*     */ 
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.poi.excel.ExcelUtil;
/*     */ import cn.hutool.poi.excel.StyleSet;
/*     */ import cn.hutool.poi.excel.cell.setters.CellSetterFactory;
/*     */ import cn.hutool.poi.excel.cell.values.ErrorCellValue;
/*     */ import cn.hutool.poi.excel.cell.values.NumericCellValue;
/*     */ import cn.hutool.poi.excel.editors.TrimEditor;
/*     */ import org.apache.poi.ss.usermodel.Cell;
/*     */ import org.apache.poi.ss.usermodel.CellStyle;
/*     */ import org.apache.poi.ss.usermodel.CellType;
/*     */ import org.apache.poi.ss.usermodel.ClientAnchor;
/*     */ import org.apache.poi.ss.usermodel.Comment;
/*     */ import org.apache.poi.ss.usermodel.CreationHelper;
/*     */ import org.apache.poi.ss.usermodel.Drawing;
/*     */ import org.apache.poi.ss.usermodel.Row;
/*     */ import org.apache.poi.ss.usermodel.Sheet;
/*     */ import org.apache.poi.ss.usermodel.Workbook;
/*     */ import org.apache.poi.ss.util.CellRangeAddress;
/*     */ import org.apache.poi.ss.util.RegionUtil;
/*     */ import org.apache.poi.ss.util.SheetUtil;
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
/*     */ public class CellUtil
/*     */ {
/*     */   public static Object getCellValue(Cell cell) {
/*  41 */     return getCellValue(cell, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getCellValue(Cell cell, boolean isTrimCellValue) {
/*  52 */     if (null == cell) {
/*  53 */       return null;
/*     */     }
/*  55 */     return getCellValue(cell, cell.getCellType(), isTrimCellValue);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object getCellValue(Cell cell, CellEditor cellEditor) {
/*  66 */     return getCellValue(cell, (CellType)null, cellEditor);
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
/*     */   public static Object getCellValue(Cell cell, CellType cellType, boolean isTrimCellValue) {
/*  78 */     return getCellValue(cell, cellType, isTrimCellValue ? (CellEditor)new TrimEditor() : null);
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
/*     */   public static Object getCellValue(Cell cell, CellType cellType, CellEditor cellEditor) {
/*     */     Object value;
/*  91 */     if (null == cell) {
/*  92 */       return null;
/*     */     }
/*  94 */     if (cell instanceof NullCell) {
/*  95 */       return (null == cellEditor) ? null : cellEditor.edit(cell, null);
/*     */     }
/*  97 */     if (null == cellType) {
/*  98 */       cellType = cell.getCellType();
/*     */     }
/*     */ 
/*     */     
/* 102 */     Cell mergedCell = getMergedRegionCell(cell);
/* 103 */     if (mergedCell != cell) {
/* 104 */       cell = mergedCell;
/* 105 */       cellType = cell.getCellType();
/*     */     } 
/*     */ 
/*     */     
/* 109 */     switch (cellType) {
/*     */       case NUMERIC:
/* 111 */         value = (new NumericCellValue(cell)).getValue();
/*     */         break;
/*     */       case BOOLEAN:
/* 114 */         value = Boolean.valueOf(cell.getBooleanCellValue());
/*     */         break;
/*     */       case FORMULA:
/* 117 */         value = getCellValue(cell, cell.getCachedFormulaResultType(), cellEditor);
/*     */         break;
/*     */       case BLANK:
/* 120 */         value = "";
/*     */         break;
/*     */       case ERROR:
/* 123 */         value = (new ErrorCellValue(cell)).getValue();
/*     */         break;
/*     */       default:
/* 126 */         value = cell.getStringCellValue();
/*     */         break;
/*     */     } 
/* 129 */     return (null == cellEditor) ? value : cellEditor.edit(cell, value);
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
/*     */   public static void setCellValue(Cell cell, Object value, StyleSet styleSet, boolean isHeader) {
/* 143 */     if (null == cell) {
/*     */       return;
/*     */     }
/*     */     
/* 147 */     if (null != styleSet) {
/* 148 */       cell.setCellStyle(styleSet.getStyleByValueType(value, isHeader));
/*     */     }
/*     */     
/* 151 */     setCellValue(cell, value);
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
/*     */   public static void setCellValue(Cell cell, Object value, CellStyle style) {
/* 164 */     setCellValue(cell, cell1 -> {
/*     */           setCellValue(cell, value);
/*     */           if (null != style) {
/*     */             cell1.setCellStyle(style);
/*     */           }
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
/*     */   public static void setCellValue(Cell cell, Object value) {
/* 182 */     if (null == cell) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 190 */     if (CellType.BLANK != cell.getCellType()) {
/* 191 */       cell.setBlank();
/*     */     }
/*     */     
/* 194 */     CellSetterFactory.createCellSetter(value).setValue(cell);
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
/*     */   public static Cell getCell(Row row, int cellIndex) {
/* 206 */     if (null == row) {
/* 207 */       return null;
/*     */     }
/* 209 */     Cell cell = row.getCell(cellIndex);
/* 210 */     if (null == cell) {
/* 211 */       return new NullCell(row, cellIndex);
/*     */     }
/* 213 */     return cell;
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
/*     */   public static Cell getOrCreateCell(Row row, int cellIndex) {
/* 225 */     if (null == row) {
/* 226 */       return null;
/*     */     }
/* 228 */     Cell cell = row.getCell(cellIndex);
/* 229 */     if (null == cell) {
/* 230 */       cell = row.createCell(cellIndex);
/*     */     }
/* 232 */     return cell;
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
/*     */   public static boolean isMergedRegion(Sheet sheet, String locationRef) {
/* 244 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 245 */     return isMergedRegion(sheet, cellLocation.getX(), cellLocation.getY());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isMergedRegion(Cell cell) {
/* 256 */     return isMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex());
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
/*     */   public static boolean isMergedRegion(Sheet sheet, int x, int y) {
/* 268 */     if (sheet != null) {
/* 269 */       int sheetMergeCount = sheet.getNumMergedRegions();
/*     */       
/* 271 */       for (int i = 0; i < sheetMergeCount; i++) {
/* 272 */         CellRangeAddress ca = sheet.getMergedRegion(i);
/* 273 */         if (y >= ca.getFirstRow() && y <= ca.getLastRow() && x >= ca
/* 274 */           .getFirstColumn() && x <= ca.getLastColumn()) {
/* 275 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 279 */     return false;
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
/*     */   public static CellRangeAddress getCellRangeAddress(Sheet sheet, String locationRef) {
/* 291 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 292 */     return getCellRangeAddress(sheet, cellLocation.getX(), cellLocation.getY());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CellRangeAddress getCellRangeAddress(Cell cell) {
/* 303 */     return getCellRangeAddress(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex());
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
/*     */   public static CellRangeAddress getCellRangeAddress(Sheet sheet, int x, int y) {
/* 316 */     if (sheet != null) {
/* 317 */       int sheetMergeCount = sheet.getNumMergedRegions();
/*     */       
/* 319 */       for (int i = 0; i < sheetMergeCount; i++) {
/* 320 */         CellRangeAddress ca = sheet.getMergedRegion(i);
/* 321 */         if (y >= ca.getFirstRow() && y <= ca.getLastRow() && x >= ca
/* 322 */           .getFirstColumn() && x <= ca.getLastColumn()) {
/* 323 */           return ca;
/*     */         }
/*     */       } 
/*     */     } 
/* 327 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setMergedRegionStyle(Cell cell, CellStyle cellStyle) {
/* 337 */     CellRangeAddress cellRangeAddress = getCellRangeAddress(cell);
/* 338 */     if (cellRangeAddress != null) {
/* 339 */       setMergeCellStyle(cellStyle, cellRangeAddress, cell.getSheet());
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
/*     */ 
/*     */   
/*     */   public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn) {
/* 354 */     return mergingCells(sheet, firstRow, lastRow, firstColumn, lastColumn, null);
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
/*     */   public static int mergingCells(Sheet sheet, int firstRow, int lastRow, int firstColumn, int lastColumn, CellStyle cellStyle) {
/* 369 */     CellRangeAddress cellRangeAddress = new CellRangeAddress(firstRow, lastRow, firstColumn, lastColumn);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 376 */     setMergeCellStyle(cellStyle, cellRangeAddress, sheet);
/* 377 */     return sheet.addMergedRegion(cellRangeAddress);
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
/*     */   public static Object getMergedRegionValue(Sheet sheet, String locationRef) {
/* 390 */     CellLocation cellLocation = ExcelUtil.toLocation(locationRef);
/* 391 */     return getMergedRegionValue(sheet, cellLocation.getX(), cellLocation.getY());
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
/*     */   public static Object getMergedRegionValue(Sheet sheet, int x, int y) {
/* 406 */     return getCellValue(SheetUtil.getCell(sheet, x, y));
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
/*     */   public static Cell getMergedRegionCell(Cell cell) {
/* 418 */     if (null == cell) {
/* 419 */       return null;
/*     */     }
/* 421 */     return (Cell)ObjectUtil.defaultIfNull(
/* 422 */         getCellIfMergedRegion(cell.getSheet(), cell.getColumnIndex(), cell.getRowIndex()), cell);
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
/*     */   public static Cell getMergedRegionCell(Sheet sheet, int x, int y) {
/* 437 */     return (Cell)ObjectUtil.defaultIfNull(
/* 438 */         getCellIfMergedRegion(sheet, x, y), () -> SheetUtil.getCell(sheet, y, x));
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
/*     */   public static void setComment(Cell cell, String commentText, String commentAuthor, ClientAnchor anchor) {
/* 452 */     Sheet sheet = cell.getSheet();
/* 453 */     Workbook wb = sheet.getWorkbook();
/* 454 */     Drawing<?> drawing = sheet.createDrawingPatriarch();
/* 455 */     CreationHelper factory = wb.getCreationHelper();
/* 456 */     if (anchor == null) {
/* 457 */       anchor = factory.createClientAnchor();
/* 458 */       anchor.setCol1(cell.getColumnIndex() + 1);
/* 459 */       anchor.setCol2(cell.getColumnIndex() + 3);
/* 460 */       anchor.setRow1(cell.getRowIndex());
/* 461 */       anchor.setRow2(cell.getRowIndex() + 2);
/*     */     } 
/* 463 */     Comment comment = drawing.createCellComment(anchor);
/* 464 */     comment.setString(factory.createRichTextString(commentText));
/* 465 */     comment.setAuthor(StrUtil.nullToEmpty(commentAuthor));
/* 466 */     cell.setCellComment(comment);
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
/*     */   
/*     */   private static Cell getCellIfMergedRegion(Sheet sheet, int x, int y) {
/* 482 */     int sheetMergeCount = sheet.getNumMergedRegions();
/*     */     
/* 484 */     for (int i = 0; i < sheetMergeCount; i++) {
/* 485 */       CellRangeAddress ca = sheet.getMergedRegion(i);
/* 486 */       if (ca.isInRange(y, x)) {
/* 487 */         return SheetUtil.getCell(sheet, ca.getFirstRow(), ca.getFirstColumn());
/*     */       }
/*     */     } 
/* 490 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void setMergeCellStyle(CellStyle cellStyle, CellRangeAddress cellRangeAddress, Sheet sheet) {
/* 501 */     if (null != cellStyle) {
/* 502 */       RegionUtil.setBorderTop(cellStyle.getBorderTop(), cellRangeAddress, sheet);
/* 503 */       RegionUtil.setBorderRight(cellStyle.getBorderRight(), cellRangeAddress, sheet);
/* 504 */       RegionUtil.setBorderBottom(cellStyle.getBorderBottom(), cellRangeAddress, sheet);
/* 505 */       RegionUtil.setBorderLeft(cellStyle.getBorderLeft(), cellRangeAddress, sheet);
/* 506 */       RegionUtil.setTopBorderColor(cellStyle.getTopBorderColor(), cellRangeAddress, sheet);
/* 507 */       RegionUtil.setRightBorderColor(cellStyle.getRightBorderColor(), cellRangeAddress, sheet);
/* 508 */       RegionUtil.setLeftBorderColor(cellStyle.getLeftBorderColor(), cellRangeAddress, sheet);
/* 509 */       RegionUtil.setBottomBorderColor(cellStyle.getBottomBorderColor(), cellRangeAddress, sheet);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\CellUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */