package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.RichTextString;

public class RichTextCellSetter implements CellSetter {
   private final RichTextString value;

   RichTextCellSetter(RichTextString value) {
      this.value = value;
   }

   public void setValue(Cell cell) {
      cell.setCellValue(this.value);
   }
}
