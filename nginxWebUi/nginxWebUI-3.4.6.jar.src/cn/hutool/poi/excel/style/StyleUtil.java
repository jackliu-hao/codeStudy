/*     */ package cn.hutool.poi.excel.style;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import org.apache.poi.ss.usermodel.BorderStyle;
/*     */ import org.apache.poi.ss.usermodel.Cell;
/*     */ import org.apache.poi.ss.usermodel.CellStyle;
/*     */ import org.apache.poi.ss.usermodel.DataFormat;
/*     */ import org.apache.poi.ss.usermodel.FillPatternType;
/*     */ import org.apache.poi.ss.usermodel.Font;
/*     */ import org.apache.poi.ss.usermodel.HorizontalAlignment;
/*     */ import org.apache.poi.ss.usermodel.IndexedColors;
/*     */ import org.apache.poi.ss.usermodel.VerticalAlignment;
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
/*     */ public class StyleUtil
/*     */ {
/*     */   public static CellStyle cloneCellStyle(Cell cell, CellStyle cellStyle) {
/*  31 */     return cloneCellStyle(cell.getSheet().getWorkbook(), cellStyle);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CellStyle cloneCellStyle(Workbook workbook, CellStyle cellStyle) {
/*  42 */     CellStyle newCellStyle = createCellStyle(workbook);
/*  43 */     newCellStyle.cloneStyleFrom(cellStyle);
/*  44 */     return newCellStyle;
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
/*     */   public static CellStyle setAlign(CellStyle cellStyle, HorizontalAlignment halign, VerticalAlignment valign) {
/*  56 */     cellStyle.setAlignment(halign);
/*  57 */     cellStyle.setVerticalAlignment(valign);
/*  58 */     return cellStyle;
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
/*     */   public static CellStyle setBorder(CellStyle cellStyle, BorderStyle borderSize, IndexedColors colorIndex) {
/*  70 */     cellStyle.setBorderBottom(borderSize);
/*  71 */     cellStyle.setBottomBorderColor(colorIndex.index);
/*     */     
/*  73 */     cellStyle.setBorderLeft(borderSize);
/*  74 */     cellStyle.setLeftBorderColor(colorIndex.index);
/*     */     
/*  76 */     cellStyle.setBorderRight(borderSize);
/*  77 */     cellStyle.setRightBorderColor(colorIndex.index);
/*     */     
/*  79 */     cellStyle.setBorderTop(borderSize);
/*  80 */     cellStyle.setTopBorderColor(colorIndex.index);
/*     */     
/*  82 */     return cellStyle;
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
/*     */   public static CellStyle setColor(CellStyle cellStyle, IndexedColors color, FillPatternType fillPattern) {
/*  94 */     return setColor(cellStyle, color.index, fillPattern);
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
/*     */   public static CellStyle setColor(CellStyle cellStyle, short color, FillPatternType fillPattern) {
/* 106 */     cellStyle.setFillForegroundColor(color);
/* 107 */     cellStyle.setFillPattern(fillPattern);
/* 108 */     return cellStyle;
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
/*     */   public static Font createFont(Workbook workbook, short color, short fontSize, String fontName) {
/* 121 */     Font font = workbook.createFont();
/* 122 */     return setFontStyle(font, color, fontSize, fontName);
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
/*     */   public static Font setFontStyle(Font font, short color, short fontSize, String fontName) {
/* 135 */     if (color > 0) {
/* 136 */       font.setColor(color);
/*     */     }
/* 138 */     if (fontSize > 0) {
/* 139 */       font.setFontHeightInPoints(fontSize);
/*     */     }
/* 141 */     if (StrUtil.isNotBlank(fontName)) {
/* 142 */       font.setFontName(fontName);
/*     */     }
/* 144 */     return font;
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
/*     */   public static CellStyle createCellStyle(Workbook workbook) {
/* 156 */     if (null == workbook) {
/* 157 */       return null;
/*     */     }
/* 159 */     return workbook.createCellStyle();
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
/*     */   public static CellStyle createDefaultCellStyle(Workbook workbook) {
/* 174 */     CellStyle cellStyle = createCellStyle(workbook);
/* 175 */     setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
/* 176 */     setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
/* 177 */     return cellStyle;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CellStyle createHeadCellStyle(Workbook workbook) {
/* 187 */     CellStyle cellStyle = createCellStyle(workbook);
/* 188 */     setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
/* 189 */     setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
/* 190 */     setColor(cellStyle, IndexedColors.GREY_25_PERCENT, FillPatternType.SOLID_FOREGROUND);
/* 191 */     return cellStyle;
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
/*     */   public static boolean isNullOrDefaultStyle(Workbook workbook, CellStyle style) {
/* 203 */     return (null == style || style.equals(workbook.getCellStyleAt(0)));
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
/*     */   public static Short getFormat(Workbook workbook, String format) {
/* 215 */     DataFormat dataFormat = workbook.createDataFormat();
/* 216 */     return Short.valueOf(dataFormat.getFormat(format));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\style\StyleUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */