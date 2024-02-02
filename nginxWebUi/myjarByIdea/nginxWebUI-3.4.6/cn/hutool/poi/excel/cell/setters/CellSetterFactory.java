package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.RichTextString;

public class CellSetterFactory {
   public static CellSetter createCellSetter(Object value) {
      if (null == value) {
         return NullCellSetter.INSTANCE;
      } else if (value instanceof CellSetter) {
         return (CellSetter)value;
      } else if (value instanceof Date) {
         return new DateCellSetter((Date)value);
      } else if (value instanceof TemporalAccessor) {
         return new TemporalAccessorCellSetter((TemporalAccessor)value);
      } else if (value instanceof Calendar) {
         return new CalendarCellSetter((Calendar)value);
      } else if (value instanceof Boolean) {
         return new BooleanCellSetter((Boolean)value);
      } else if (value instanceof RichTextString) {
         return new RichTextCellSetter((RichTextString)value);
      } else if (value instanceof Number) {
         return new NumberCellSetter((Number)value);
      } else {
         return (CellSetter)(value instanceof Hyperlink ? new HyperlinkCellSetter((Hyperlink)value) : new CharSequenceCellSetter(value.toString()));
      }
   }
}
