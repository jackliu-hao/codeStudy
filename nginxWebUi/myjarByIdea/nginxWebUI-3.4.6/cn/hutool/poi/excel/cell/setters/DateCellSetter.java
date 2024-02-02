package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;

public class DateCellSetter implements CellSetter {
   private final Date value;

   DateCellSetter(Date value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      cell.setCellValue(this.value);
   }
}
