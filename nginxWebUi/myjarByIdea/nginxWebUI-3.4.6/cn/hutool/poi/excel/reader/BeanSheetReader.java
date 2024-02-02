package cn.hutool.poi.excel.reader;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.poi.excel.cell.CellEditor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Sheet;

public class BeanSheetReader<T> implements SheetReader<List<T>> {
   private final Class<T> beanClass;
   private final MapSheetReader mapSheetReader;

   public BeanSheetReader(int headerRowIndex, int startRowIndex, int endRowIndex, Class<T> beanClass) {
      this.mapSheetReader = new MapSheetReader(headerRowIndex, startRowIndex, endRowIndex);
      this.beanClass = beanClass;
   }

   public List<T> read(Sheet sheet) {
      List<Map<String, Object>> mapList = this.mapSheetReader.read(sheet);
      if (Map.class.isAssignableFrom(this.beanClass)) {
         return mapList;
      } else {
         List<T> beanList = new ArrayList(mapList.size());
         CopyOptions copyOptions = CopyOptions.create().setIgnoreError(true);
         Iterator var5 = mapList.iterator();

         while(var5.hasNext()) {
            Map<String, Object> map = (Map)var5.next();
            beanList.add(BeanUtil.toBean((Object)map, (Class)this.beanClass, copyOptions));
         }

         return beanList;
      }
   }

   public void setCellEditor(CellEditor cellEditor) {
      this.mapSheetReader.setCellEditor(cellEditor);
   }

   public void setIgnoreEmptyRow(boolean ignoreEmptyRow) {
      this.mapSheetReader.setIgnoreEmptyRow(ignoreEmptyRow);
   }

   public void setHeaderAlias(Map<String, String> headerAlias) {
      this.mapSheetReader.setHeaderAlias(headerAlias);
   }

   public void addHeaderAlias(String header, String alias) {
      this.mapSheetReader.addHeaderAlias(header, alias);
   }
}
