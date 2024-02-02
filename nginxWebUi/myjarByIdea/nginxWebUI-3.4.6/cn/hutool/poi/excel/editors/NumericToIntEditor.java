package cn.hutool.poi.excel.editors;

import cn.hutool.poi.excel.cell.CellEditor;
import org.apache.poi.ss.usermodel.Cell;

public class NumericToIntEditor implements CellEditor {
   public Object edit(Cell cell, Object value) {
      return value instanceof Number ? ((Number)value).intValue() : value;
   }
}
