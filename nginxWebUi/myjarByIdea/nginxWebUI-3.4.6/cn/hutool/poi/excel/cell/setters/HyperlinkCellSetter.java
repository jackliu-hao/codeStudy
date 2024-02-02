package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Hyperlink;

public class HyperlinkCellSetter implements CellSetter {
   private final Hyperlink value;

   HyperlinkCellSetter(Hyperlink value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      cell.setHyperlink(this.value);
      cell.setCellValue(this.value.getLabel());
   }
}
