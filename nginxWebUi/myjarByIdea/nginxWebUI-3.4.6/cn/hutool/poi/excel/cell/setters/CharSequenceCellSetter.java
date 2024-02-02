package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

public class CharSequenceCellSetter implements CellSetter {
   private final CharSequence value;

   CharSequenceCellSetter(CharSequence value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      cell.setCellValue(this.value.toString());
   }
}
