package cn.hutool.poi.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

@FunctionalInterface
public interface CellSetter {
   void setValue(Cell var1);
}
