package cn.hutool.core.text.csv;

import java.io.Serializable;

public class CsvWriteConfig extends CsvConfig<CsvWriteConfig> implements Serializable {
   private static final long serialVersionUID = 5396453565371560052L;
   protected boolean alwaysDelimitText;
   protected char[] lineDelimiter = new char[]{'\r', '\n'};

   public static CsvWriteConfig defaultConfig() {
      return new CsvWriteConfig();
   }

   public CsvWriteConfig setAlwaysDelimitText(boolean alwaysDelimitText) {
      this.alwaysDelimitText = alwaysDelimitText;
      return this;
   }

   public CsvWriteConfig setLineDelimiter(char[] lineDelimiter) {
      this.lineDelimiter = lineDelimiter;
      return this;
   }
}
