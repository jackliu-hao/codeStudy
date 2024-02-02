package cn.hutool.poi.excel.style;

import cn.hutool.core.util.StrUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class StyleUtil {
   public static CellStyle cloneCellStyle(Cell cell, CellStyle cellStyle) {
      return cloneCellStyle(cell.getSheet().getWorkbook(), cellStyle);
   }

   public static CellStyle cloneCellStyle(Workbook workbook, CellStyle cellStyle) {
      CellStyle newCellStyle = createCellStyle(workbook);
      newCellStyle.cloneStyleFrom(cellStyle);
      return newCellStyle;
   }

   public static CellStyle setAlign(CellStyle cellStyle, HorizontalAlignment halign, VerticalAlignment valign) {
      cellStyle.setAlignment(halign);
      cellStyle.setVerticalAlignment(valign);
      return cellStyle;
   }

   public static CellStyle setBorder(CellStyle cellStyle, BorderStyle borderSize, IndexedColors colorIndex) {
      cellStyle.setBorderBottom(borderSize);
      cellStyle.setBottomBorderColor(colorIndex.index);
      cellStyle.setBorderLeft(borderSize);
      cellStyle.setLeftBorderColor(colorIndex.index);
      cellStyle.setBorderRight(borderSize);
      cellStyle.setRightBorderColor(colorIndex.index);
      cellStyle.setBorderTop(borderSize);
      cellStyle.setTopBorderColor(colorIndex.index);
      return cellStyle;
   }

   public static CellStyle setColor(CellStyle cellStyle, IndexedColors color, FillPatternType fillPattern) {
      return setColor(cellStyle, color.index, fillPattern);
   }

   public static CellStyle setColor(CellStyle cellStyle, short color, FillPatternType fillPattern) {
      cellStyle.setFillForegroundColor(color);
      cellStyle.setFillPattern(fillPattern);
      return cellStyle;
   }

   public static Font createFont(Workbook workbook, short color, short fontSize, String fontName) {
      Font font = workbook.createFont();
      return setFontStyle(font, color, fontSize, fontName);
   }

   public static Font setFontStyle(Font font, short color, short fontSize, String fontName) {
      if (color > 0) {
         font.setColor(color);
      }

      if (fontSize > 0) {
         font.setFontHeightInPoints(fontSize);
      }

      if (StrUtil.isNotBlank(fontName)) {
         font.setFontName(fontName);
      }

      return font;
   }

   public static CellStyle createCellStyle(Workbook workbook) {
      return null == workbook ? null : workbook.createCellStyle();
   }

   public static CellStyle createDefaultCellStyle(Workbook workbook) {
      CellStyle cellStyle = createCellStyle(workbook);
      setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
      setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
      return cellStyle;
   }

   public static CellStyle createHeadCellStyle(Workbook workbook) {
      CellStyle cellStyle = createCellStyle(workbook);
      setAlign(cellStyle, HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
      setBorder(cellStyle, BorderStyle.THIN, IndexedColors.BLACK);
      setColor(cellStyle, IndexedColors.GREY_25_PERCENT, FillPatternType.SOLID_FOREGROUND);
      return cellStyle;
   }

   public static boolean isNullOrDefaultStyle(Workbook workbook, CellStyle style) {
      return null == style || style.equals(workbook.getCellStyleAt(0));
   }

   public static Short getFormat(Workbook workbook, String format) {
      DataFormat dataFormat = workbook.createDataFormat();
      return dataFormat.getFormat(format);
   }
}
