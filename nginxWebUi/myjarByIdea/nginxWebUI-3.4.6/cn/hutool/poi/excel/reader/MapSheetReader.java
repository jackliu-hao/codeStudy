package cn.hutool.poi.excel.reader;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

public class MapSheetReader extends AbstractSheetReader<List<Map<String, Object>>> {
   private final int headerRowIndex;

   public MapSheetReader(int headerRowIndex, int startRowIndex, int endRowIndex) {
      super(startRowIndex, endRowIndex);
      this.headerRowIndex = headerRowIndex;
   }

   public List<Map<String, Object>> read(Sheet sheet) {
      int firstRowNum = sheet.getFirstRowNum();
      int lastRowNum = sheet.getLastRowNum();
      if (lastRowNum < 0) {
         return ListUtil.empty();
      } else if (this.headerRowIndex < firstRowNum) {
         throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is lower than first row index {}.", new Object[]{this.headerRowIndex, firstRowNum}));
      } else if (this.headerRowIndex > lastRowNum) {
         throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is greater than last row index {}.", new Object[]{this.headerRowIndex, firstRowNum}));
      } else {
         int startRowIndex = Math.max(this.startRowIndex, firstRowNum);
         int endRowIndex = Math.min(this.endRowIndex, lastRowNum);
         List<String> headerList = this.aliasHeader(this.readRow(sheet, this.headerRowIndex));
         List<Map<String, Object>> result = new ArrayList(endRowIndex - startRowIndex + 1);

         for(int i = startRowIndex; i <= endRowIndex; ++i) {
            if (i != this.headerRowIndex) {
               List<Object> rowList = this.readRow(sheet, i);
               if (CollUtil.isNotEmpty((Collection)rowList) || !this.ignoreEmptyRow) {
                  result.add(IterUtil.toMap((Iterable)headerList, (Iterable)rowList, true));
               }
            }
         }

         return result;
      }
   }
}
