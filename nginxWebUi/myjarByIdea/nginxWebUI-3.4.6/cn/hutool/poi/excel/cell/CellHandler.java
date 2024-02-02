package cn.hutool.poi.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

@FunctionalInterface
public interface CellHandler {
   void handle(Cell var1, Object var2);
}
