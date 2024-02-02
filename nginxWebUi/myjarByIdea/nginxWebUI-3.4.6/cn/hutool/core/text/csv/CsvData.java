package cn.hutool.core.text.csv;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CsvData implements Iterable<CsvRow>, Serializable {
   private static final long serialVersionUID = 1L;
   private final List<String> header;
   private final List<CsvRow> rows;

   public CsvData(List<String> header, List<CsvRow> rows) {
      this.header = header;
      this.rows = rows;
   }

   public int getRowCount() {
      return this.rows.size();
   }

   public List<String> getHeader() {
      return Collections.unmodifiableList(this.header);
   }

   public CsvRow getRow(int index) {
      return (CsvRow)this.rows.get(index);
   }

   public List<CsvRow> getRows() {
      return this.rows;
   }

   public Iterator<CsvRow> iterator() {
      return this.rows.iterator();
   }

   public String toString() {
      return "CsvData{header=" + this.header + ", rows=" + this.rows + '}';
   }
}
