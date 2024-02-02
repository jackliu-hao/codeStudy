package cn.hutool.poi.excel.cell.values;

import cn.hutool.poi.excel.cell.CellValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;

public class ErrorCellValue implements CellValue<String> {
   private final Cell cell;

   public ErrorCellValue(Cell cell) {
      this.cell = cell;
   }

   public String getValue() {
      FormulaError error = FormulaError.forInt(this.cell.getErrorCellValue());
      return null == error ? "" : error.getString();
   }
}
