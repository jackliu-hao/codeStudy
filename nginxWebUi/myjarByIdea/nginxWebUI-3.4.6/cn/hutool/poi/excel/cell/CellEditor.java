package cn.hutool.poi.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

@FunctionalInterface
public interface CellEditor {
   Object edit(Cell var1, Object var2);
}
