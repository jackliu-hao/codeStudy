package cn.hutool.poi.excel.cell.setters;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

public class NumberCellSetter implements CellSetter {
   private final Number value;

   NumberCellSetter(Number value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      cell.setCellValue(NumberUtil.toDouble(this.value));
   }
}
