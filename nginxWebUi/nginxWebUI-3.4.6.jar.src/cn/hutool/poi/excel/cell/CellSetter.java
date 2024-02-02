package cn.hutool.poi.excel.cell;

import org.apache.poi.ss.usermodel.Cell;

@FunctionalInterface
public interface CellSetter {
  void setValue(Cell paramCell);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\CellSetter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */