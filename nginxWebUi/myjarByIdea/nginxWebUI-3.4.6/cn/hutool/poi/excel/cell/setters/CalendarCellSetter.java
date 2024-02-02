package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.util.Calendar;
import org.apache.poi.ss.usermodel.Cell;

public class CalendarCellSetter implements CellSetter {
   private final Calendar value;

   CalendarCellSetter(Calendar value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      cell.setCellValue(this.value);
   }
}
