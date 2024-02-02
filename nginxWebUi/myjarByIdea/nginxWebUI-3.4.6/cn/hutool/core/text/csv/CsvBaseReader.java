package cn.hutool.core.text.csv;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.File;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class CsvBaseReader implements Serializable {
   private static final long serialVersionUID = 1L;
   protected static final Charset DEFAULT_CHARSET;
   private final CsvReadConfig config;

   public CsvBaseReader() {
      this((CsvReadConfig)null);
   }

   public CsvBaseReader(CsvReadConfig config) {
      this.config = (CsvReadConfig)ObjectUtil.defaultIfNull(config, (Supplier)(CsvReadConfig::defaultConfig));
   }

   public void setFieldSeparator(char fieldSeparator) {
      this.config.setFieldSeparator(fieldSeparator);
   }

   public void setTextDelimiter(char textDelimiter) {
      this.config.setTextDelimiter(textDelimiter);
   }

   public void setContainsHeader(boolean containsHeader) {
      this.config.setContainsHeader(containsHeader);
   }

   public void setSkipEmptyRows(boolean skipEmptyRows) {
      this.config.setSkipEmptyRows(skipEmptyRows);
   }

   public void setErrorOnDifferentFieldCount(boolean errorOnDifferentFieldCount) {
      this.config.setErrorOnDifferentFieldCount(errorOnDifferentFieldCount);
   }

   public CsvData read(File file) throws IORuntimeException {
      return this.read(file, DEFAULT_CHARSET);
   }

   public CsvData readFromStr(String csvStr) {
      return this.read((Reader)(new StringReader(csvStr)));
   }

   public void readFromStr(String csvStr, CsvRowHandler rowHandler) {
      this.read(this.parse(new StringReader(csvStr)), rowHandler);
   }

   public CsvData read(File file, Charset charset) throws IORuntimeException {
      return this.read((Path)Objects.requireNonNull(file.toPath(), "file must not be null"), charset);
   }

   public CsvData read(Path path) throws IORuntimeException {
      return this.read(path, DEFAULT_CHARSET);
   }

   public CsvData read(Path path, Charset charset) throws IORuntimeException {
      Assert.notNull(path, "path must not be null");
      return this.read((Reader)FileUtil.getReader((Path)path, (Charset)charset));
   }

   public CsvData read(Reader reader) throws IORuntimeException {
      CsvParser csvParser = this.parse(reader);
      List<CsvRow> rows = new ArrayList();
      this.read(csvParser, rows::add);
      List<String> header = this.config.headerLineNo > -1L ? csvParser.getHeader() : null;
      return new CsvData(header, rows);
   }

   public List<Map<String, String>> readMapList(Reader reader) throws IORuntimeException {
      this.config.setContainsHeader(true);
      List<Map<String, String>> result = new ArrayList();
      this.read(reader, (row) -> {
         result.add(row.getFieldMap());
      });
      return result;
   }

   public <T> List<T> read(Reader reader, Class<T> clazz) {
      this.config.setContainsHeader(true);
      List<T> result = new ArrayList();
      this.read(reader, (row) -> {
         result.add(row.toBean(clazz));
      });
      return result;
   }

   public <T> List<T> read(String csvStr, Class<T> clazz) {
      this.config.setContainsHeader(true);
      List<T> result = new ArrayList();
      this.read((Reader)(new StringReader(csvStr)), (CsvRowHandler)((row) -> {
         result.add(row.toBean(clazz));
      }));
      return result;
   }

   public void read(Reader reader, CsvRowHandler rowHandler) throws IORuntimeException {
      this.read(this.parse(reader), rowHandler);
   }

   private void read(CsvParser csvParser, CsvRowHandler rowHandler) throws IORuntimeException {
      try {
         while(csvParser.hasNext()) {
            rowHandler.handle((CsvRow)csvParser.next());
         }
      } finally {
         IoUtil.close(csvParser);
      }

   }

   protected CsvParser parse(Reader reader) throws IORuntimeException {
      return new CsvParser(reader, this.config);
   }

   static {
      DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
   }
}
