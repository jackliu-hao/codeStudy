package cn.hutool.poi.excel.sax.handler;

import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;

@FunctionalInterface
public interface RowHandler {
  void handle(int paramInt, long paramLong, List<Object> paramList);
  
  default void handleCell(int sheetIndex, long rowIndex, int cellIndex, Object value, CellStyle xssfCellStyle) {}
  
  default void doAfterAllAnalysed() {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\sax\handler\RowHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */