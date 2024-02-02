package cn.hutool.poi.excel.reader;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.poi.ss.usermodel.Sheet;

public class ListSheetReader extends AbstractSheetReader<List<List<Object>>> {
   private final boolean aliasFirstLine;

   public ListSheetReader(int startRowIndex, int endRowIndex, boolean aliasFirstLine) {
      super(startRowIndex, endRowIndex);
      this.aliasFirstLine = aliasFirstLine;
   }

   public List<List<Object>> read(Sheet sheet) {
      List<List<Object>> resultList = new ArrayList();
      int startRowIndex = Math.max(this.startRowIndex, sheet.getFirstRowNum());
      int endRowIndex = Math.min(this.endRowIndex, sheet.getLastRowNum());

      for(int i = startRowIndex; i <= endRowIndex; ++i) {
         List<Object> rowList = this.readRow(sheet, i);
         if (CollUtil.isNotEmpty((Collection)rowList) || !this.ignoreEmptyRow) {
            if (this.aliasFirstLine && i == startRowIndex) {
               rowList = Convert.toList(Object.class, this.aliasHeader(rowList));
            }

            resultList.add(rowList);
         }
      }

      return resultList;
   }
}
