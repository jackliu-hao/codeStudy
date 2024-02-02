package cn.hutool.core.text.csv;

import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

public class CsvUtil {
   public static CsvReader getReader(CsvReadConfig config) {
      return new CsvReader(config);
   }

   public static CsvReader getReader() {
      return new CsvReader();
   }

   public static CsvReader getReader(Reader reader, CsvReadConfig config) {
      return new CsvReader(reader, config);
   }

   public static CsvReader getReader(Reader reader) {
      return getReader(reader, (CsvReadConfig)null);
   }

   public static CsvWriter getWriter(String filePath, Charset charset) {
      return new CsvWriter(filePath, charset);
   }

   public static CsvWriter getWriter(File file, Charset charset) {
      return new CsvWriter(file, charset);
   }

   public static CsvWriter getWriter(String filePath, Charset charset, boolean isAppend) {
      return new CsvWriter(filePath, charset, isAppend);
   }

   public static CsvWriter getWriter(File file, Charset charset, boolean isAppend) {
      return new CsvWriter(file, charset, isAppend);
   }

   public static CsvWriter getWriter(File file, Charset charset, boolean isAppend, CsvWriteConfig config) {
      return new CsvWriter(file, charset, isAppend, config);
   }

   public static CsvWriter getWriter(Writer writer) {
      return new CsvWriter(writer);
   }

   public static CsvWriter getWriter(Writer writer, CsvWriteConfig config) {
      return new CsvWriter(writer, config);
   }
}
