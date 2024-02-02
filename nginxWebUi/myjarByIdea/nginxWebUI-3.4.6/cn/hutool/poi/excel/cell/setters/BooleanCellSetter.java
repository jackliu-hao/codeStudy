package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

public class BooleanCellSetter implements CellSetter {
   private final Boolean value;

   BooleanCellSetter(Boolean value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      cell.setCellValue(this.value);
   }
}
