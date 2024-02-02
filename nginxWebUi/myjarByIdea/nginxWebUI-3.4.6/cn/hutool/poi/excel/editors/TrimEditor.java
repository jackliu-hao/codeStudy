package cn.hutool.poi.excel.editors;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import org.apache.poi.ss.usermodel.Cell;

public class TrimEditor implements CellEditor {
   public Object edit(Cell cell, Object value) {
      return value instanceof String ? StrUtil.trim((String)value) : value;
   }
}
