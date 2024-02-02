package cn.hutool.core.lang;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ConsoleTable {
   private static final char ROW_LINE = '－';
   private static final char COLUMN_LINE = '|';
   private static final char CORNER = '+';
   private static final char SPACE = '　';
   private static final char LF = '\n';
   private boolean isSBCMode = true;
   private final List<List<String>> headerList = new ArrayList();
   private final List<List<String>> bodyList = new ArrayList();
   private List<Integer> columnCharNumber;

   public static ConsoleTable create() {
      return new ConsoleTable();
   }

   public ConsoleTable setSBCMode(boolean isSBCMode) {
      this.isSBCMode = isSBCMode;
      return this;
   }

   public ConsoleTable addHeader(String... titles) {
      if (this.columnCharNumber == null) {
         this.columnCharNumber = new ArrayList(Collections.nCopies(titles.length, 0));
      }

      List<String> l = new ArrayList();
      this.fillColumns(l, titles);
      this.headerList.add(l);
      return this;
   }

   public ConsoleTable addBody(String... values) {
      List<String> l = new ArrayList();
      this.bodyList.add(l);
      this.fillColumns(l, values);
      return this;
   }

   private void fillColumns(List<String> l, String[] columns) {
      for(int i = 0; i < columns.length; ++i) {
         String column = columns[i];
         if (this.isSBCMode) {
            column = Convert.toSBC(column);
         }

         l.add(column);
         int width = column.length();
         if (width > (Integer)this.columnCharNumber.get(i)) {
            this.columnCharNumber.set(i, width);
         }
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      this.fillBorder(sb);
      this.fillRows(sb, this.headerList);
      this.fillBorder(sb);
      this.fillRows(sb, this.bodyList);
      this.fillBorder(sb);
      return sb.toString();
   }

   private void fillRows(StringBuilder sb, List<List<String>> list) {
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         List<String> row = (List)var3.next();
         sb.append('|');
         this.fillRow(sb, row);
         sb.append('\n');
      }

   }

   private void fillRow(StringBuilder sb, List<String> row) {
      int size = row.size();

      for(int i = 0; i < size; ++i) {
         String value = (String)row.get(i);
         sb.append('　');
         sb.append(value);
         int length = value.length();
         int sbcCount = this.sbcCount(value);
         if (sbcCount % 2 == 1) {
            sb.append(' ');
         }

         sb.append('　');
         int maxLength = (Integer)this.columnCharNumber.get(i);

         for(int j = 0; j < maxLength - length + sbcCount / 2; ++j) {
            sb.append('　');
         }

         sb.append('|');
      }

   }

   private void fillBorder(StringBuilder sb) {
      sb.append('+');
      Iterator var2 = this.columnCharNumber.iterator();

      while(var2.hasNext()) {
         Integer width = (Integer)var2.next();
         sb.append(StrUtil.repeat('－', width + 2));
         sb.append('+');
      }

      sb.append('\n');
   }

   public void print() {
      Console.print(this.toString());
   }

   private int sbcCount(String value) {
      int count = 0;

      for(int i = 0; i < value.length(); ++i) {
         if (value.charAt(i) < 127) {
            ++count;
         }
      }

      return count;
   }
}
