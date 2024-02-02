package cn.hutool.poi.excel.reader;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.RowUtil;
import cn.hutool.poi.excel.cell.CellEditor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

public abstract class AbstractSheetReader<T> implements SheetReader<T> {
   protected final int startRowIndex;
   protected final int endRowIndex;
   protected boolean ignoreEmptyRow = true;
   protected CellEditor cellEditor;
   private Map<String, String> headerAlias;

   public AbstractSheetReader(int startRowIndex, int endRowIndex) {
      this.startRowIndex = startRowIndex;
      this.endRowIndex = endRowIndex;
   }

   public void setCellEditor(CellEditor cellEditor) {
      this.cellEditor = cellEditor;
   }

   public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
      this.ignoreEmptyRow = ignoreEmptyRow;
   }

   public void setHeaderAlias(Map<String, String> headerAlias) {
      this.headerAlias = headerAlias;
   }

   public void addHeaderAlias(String header, String alias) {
      Map<String, String> headerAlias = this.headerAlias;
      if (null == headerAlias) {
         headerAlias = new LinkedHashMap();
      }

      this.headerAlias = (Map)headerAlias;
      this.headerAlias.put(header, alias);
   }

   protected List<String> aliasHeader(List<Object> headerList) {
      if (CollUtil.isEmpty((Collection)headerList)) {
         return new ArrayList(0);
      } else {
         int size = headerList.size();
         ArrayList<String> result = new ArrayList(size);

         for(int i = 0; i < size; ++i) {
            result.add(this.aliasHeader(headerList.get(i), i));
         }

         return result;
      }
   }

   protected String aliasHeader(Object headerObj, int index) {
      if (null == headerObj) {
         return ExcelUtil.indexToColName(index);
      } else {
         String header = headerObj.toString();
         return null != this.headerAlias ? (String)ObjectUtil.defaultIfNull(this.headerAlias.get(header), (Object)header) : header;
      }
   }

   protected List<Object> readRow(Sheet sheet, int rowIndex) {
      return RowUtil.readRow(sheet.getRow(rowIndex), this.cellEditor);
   }
}
