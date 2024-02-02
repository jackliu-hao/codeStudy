package cn.hutool.poi.excel.cell.setters;

import cn.hutool.poi.excel.cell.CellSetter;
import org.apache.poi.ss.usermodel.Cell;

public class NullCellSetter implements CellSetter {
   public static final NullCellSetter INSTANCE = new NullCellSetter();

   public void setValue(Cell cell) {
      cell.setCellValue("");
   }
}
