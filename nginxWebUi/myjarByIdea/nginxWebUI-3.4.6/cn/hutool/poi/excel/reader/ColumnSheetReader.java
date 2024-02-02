package cn.hutool.poi.excel.reader;

import cn.hutool.poi.excel.cell.CellUtil;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;

public class ColumnSheetReader extends AbstractSheetReader<List<Object>> {
   private final int columnIndex;

   public ColumnSheetReader(int columnIndex, int startRowIndex, int endRowIndex) {
      super(startRowIndex, endRowIndex);
      this.columnIndex = columnIndex;
   }

   public List<Object> read(Sheet sheet) {
      List<Object> resultList = new ArrayList();
      int startRowIndex = Math.max(this.startRowIndex, sheet.getFirstRowNum());
      int endRowIndex = Math.min(this.endRowIndex, sheet.getLastRowNum());

      for(int i = startRowIndex; i <= endRowIndex; ++i) {
         Object value = CellUtil.getCellValue(CellUtil.getCell(sheet.getRow(i), this.columnIndex), this.cellEditor);
         if (null != value || !this.ignoreEmptyRow) {
            resultList.add(value);
         }
      }

      return resultList;
   }
}
