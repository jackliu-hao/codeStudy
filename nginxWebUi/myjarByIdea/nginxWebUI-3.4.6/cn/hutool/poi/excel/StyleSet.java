package cn.hutool.poi.excel;

import cn.hutool.poi.excel.style.StyleUtil;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

public class StyleSet implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Workbook workbook;
   protected final CellStyle headCellStyle;
   protected final CellStyle cellStyle;
   protected final CellStyle cellStyleForNumber;
   protected final CellStyle cellStyleForDate;
   protected final CellStyle cellStyleForHyperlink;

   public StyleSet(Workbook workbook) {
      this.workbook = workbook;
      this.headCellStyle = StyleUtil.createHeadCellStyle(workbook);
      this.cellStyle = StyleUtil.createDefaultCellStyle(workbook);
      this.cellStyleForNumber = StyleUtil.cloneCellStyle(workbook, this.cellStyle);
      this.cellStyleForNumber.setDataFormat((short)2);
      this.cellStyleForDate = StyleUtil.cloneCellStyle(workbook, this.cellStyle);
      this.cellStyleForDate.setDataFormat((short)22);
      this.cellStyleForHyperlink = StyleUtil.cloneCellStyle(workbook, this.cellStyle);
      Font font = this.workbook.createFont();
      font.setUnderline((byte)1);
      font.setColor(HSSFColorPredefined.BLUE.getIndex());
      this.cellStyleForHyperlink.setFont(font);
   }

   public CellStyle getHeadCellStyle() {
      return this.headCellStyle;
   }

   public CellStyle getCellStyle() {
      return this.cellStyle;
   }

   public CellStyle getCellStyleForNumber() {
      return this.cellStyleForNumber;
   }

   public CellStyle getCellStyleForDate() {
      return this.cellStyleForDate;
   }

   public CellStyle getCellStyleForHyperlink() {
      return this.cellStyleForHyperlink;
   }

   public StyleSet setBorder(BorderStyle borderSize, IndexedColors colorIndex) {
      StyleUtil.setBorder(this.headCellStyle, borderSize, colorIndex);
      StyleUtil.setBorder(this.cellStyle, borderSize, colorIndex);
      StyleUtil.setBorder(this.cellStyleForNumber, borderSize, colorIndex);
      StyleUtil.setBorder(this.cellStyleForDate, borderSize, colorIndex);
      StyleUtil.setBorder(this.cellStyleForHyperlink, borderSize, colorIndex);
      return this;
   }

   public StyleSet setAlign(HorizontalAlignment halign, VerticalAlignment valign) {
      StyleUtil.setAlign(this.headCellStyle, halign, valign);
      StyleUtil.setAlign(this.cellStyle, halign, valign);
      StyleUtil.setAlign(this.cellStyleForNumber, halign, valign);
      StyleUtil.setAlign(this.cellStyleForDate, halign, valign);
      StyleUtil.setAlign(this.cellStyleForHyperlink, halign, valign);
      return this;
   }

   public StyleSet setBackgroundColor(IndexedColors backgroundColor, boolean withHeadCell) {
      if (withHeadCell) {
         StyleUtil.setColor(this.headCellStyle, backgroundColor, FillPatternType.SOLID_FOREGROUND);
      }

      StyleUtil.setColor(this.cellStyle, backgroundColor, FillPatternType.SOLID_FOREGROUND);
      StyleUtil.setColor(this.cellStyleForNumber, backgroundColor, FillPatternType.SOLID_FOREGROUND);
      StyleUtil.setColor(this.cellStyleForDate, backgroundColor, FillPatternType.SOLID_FOREGROUND);
      StyleUtil.setColor(this.cellStyleForHyperlink, backgroundColor, FillPatternType.SOLID_FOREGROUND);
      return this;
   }

   public StyleSet setFont(short color, short fontSize, String fontName, boolean ignoreHead) {
      Font font = StyleUtil.createFont(this.workbook, color, fontSize, fontName);
      return this.setFont(font, ignoreHead);
   }

   public StyleSet setFont(Font font, boolean ignoreHead) {
      if (!ignoreHead) {
         this.headCellStyle.setFont(font);
      }

      this.cellStyle.setFont(font);
      this.cellStyleForNumber.setFont(font);
      this.cellStyleForDate.setFont(font);
      this.cellStyleForHyperlink.setFont(font);
      return this;
   }

   public StyleSet setWrapText() {
      this.cellStyle.setWrapText(true);
      this.cellStyleForNumber.setWrapText(true);
      this.cellStyleForDate.setWrapText(true);
      this.cellStyleForHyperlink.setWrapText(true);
      return this;
   }

   public CellStyle getStyleByValueType(Object value, boolean isHeader) {
      CellStyle style = null;
      if (isHeader && null != this.headCellStyle) {
         style = this.headCellStyle;
      } else if (null != this.cellStyle) {
         style = this.cellStyle;
      }

      if (!(value instanceof Date) && !(value instanceof TemporalAccessor) && !(value instanceof Calendar)) {
         if (value instanceof Number) {
            if ((value instanceof Double || value instanceof Float || value instanceof BigDecimal) && null != this.cellStyleForNumber) {
               style = this.cellStyleForNumber;
            }
         } else if (value instanceof Hyperlink && null != this.cellStyleForHyperlink) {
            style = this.cellStyleForHyperlink;
         }
      } else if (null != this.cellStyleForDate) {
         style = this.cellStyleForDate;
      }

      return style;
   }
}
