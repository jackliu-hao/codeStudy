package cn.hutool.poi.excel.sax.handler;

import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;

@FunctionalInterface
public interface RowHandler {
   void handle(int var1, long var2, List<Object> var4);

   default void handleCell(int sheetIndex, long rowIndex, int cellIndex, Object value, CellStyle xssfCellStyle) {
   }

   default void doAfterAllAnalysed() {
   }
}
