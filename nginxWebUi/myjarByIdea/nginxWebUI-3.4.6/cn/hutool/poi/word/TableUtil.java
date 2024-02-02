package cn.hutool.poi.word;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class TableUtil {
   public static XWPFTable createTable(XWPFDocument doc) {
      return createTable(doc, (Iterable)null);
   }

   public static XWPFTable createTable(XWPFDocument doc, Iterable<?> data) {
      Assert.notNull(doc, "XWPFDocument must be not null !");
      XWPFTable table = doc.createTable();
      table.removeRow(0);
      return writeTable(table, data);
   }

   public static XWPFTable writeTable(XWPFTable table, Iterable<?> data) {
      Assert.notNull(table, "XWPFTable must be not null !");
      if (IterUtil.isEmpty(data)) {
         return table;
      } else {
         boolean isFirst = true;
         Iterator var3 = data.iterator();

         while(var3.hasNext()) {
            Object rowData = var3.next();
            writeRow(table.createRow(), rowData, isFirst);
            if (isFirst) {
               isFirst = false;
            }
         }

         return table;
      }
   }

   public static void writeRow(XWPFTableRow row, Object rowBean, boolean isWriteKeyAsHead) {
      if (rowBean instanceof Iterable) {
         writeRow(row, (Iterable)rowBean);
      } else {
         Map rowMap;
         if (rowBean instanceof Map) {
            rowMap = (Map)rowBean;
         } else {
            if (!BeanUtil.isBean(rowBean.getClass())) {
               writeRow(row, (Object)CollUtil.newArrayList(rowBean), isWriteKeyAsHead);
               return;
            }

            rowMap = BeanUtil.beanToMap(rowBean, new LinkedHashMap(), false, false);
         }

         writeRow(row, rowMap, isWriteKeyAsHead);
      }
   }

   public static void writeRow(XWPFTableRow row, Map<?, ?> rowMap, boolean isWriteKeyAsHead) {
      if (!MapUtil.isEmpty(rowMap)) {
         if (isWriteKeyAsHead) {
            writeRow(row, rowMap.keySet());
            row = row.getTable().createRow();
         }

         writeRow(row, rowMap.values());
      }
   }

   public static void writeRow(XWPFTableRow row, Iterable<?> rowData) {
      int index = 0;

      for(Iterator var4 = rowData.iterator(); var4.hasNext(); ++index) {
         Object cellData = var4.next();
         XWPFTableCell cell = getOrCreateCell(row, index);
         cell.setText(Convert.toStr(cellData));
      }

   }

   public static XWPFTableRow getOrCreateRow(XWPFTable table, int index) {
      XWPFTableRow row = table.getRow(index);
      if (null == row) {
         row = table.createRow();
      }

      return row;
   }

   public static XWPFTableCell getOrCreateCell(XWPFTableRow row, int index) {
      XWPFTableCell cell = row.getCell(index);
      if (null == cell) {
         cell = row.createCell();
      }

      return cell;
   }
}
