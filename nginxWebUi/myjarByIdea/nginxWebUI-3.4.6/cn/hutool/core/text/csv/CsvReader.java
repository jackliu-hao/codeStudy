package cn.hutool.core.text.csv;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CsvReader extends CsvBaseReader implements Iterable<CsvRow>, Closeable {
   private static final long serialVersionUID = 1L;
   private final Reader reader;

   public CsvReader() {
      this((CsvReadConfig)null);
   }

   public CsvReader(CsvReadConfig config) {
      this((Reader)null, config);
   }

   public CsvReader(File file, CsvReadConfig config) {
      this(file, DEFAULT_CHARSET, config);
   }

   public CsvReader(Path path, CsvReadConfig config) {
      this(path, DEFAULT_CHARSET, config);
   }

   public CsvReader(File file, Charset charset, CsvReadConfig config) {
      this((Reader)FileUtil.getReader(file, charset), config);
   }

   public CsvReader(Path path, Charset charset, CsvReadConfig config) {
      this((Reader)FileUtil.getReader((Path)path, (Charset)charset), config);
   }

   public CsvReader(Reader reader, CsvReadConfig config) {
      super(config);
      this.reader = reader;
   }

   public CsvData read() throws IORuntimeException {
      return this.read(this.reader);
   }

   public void read(CsvRowHandler rowHandler) throws IORuntimeException {
      this.read(this.reader, rowHandler);
   }

   public Stream<CsvRow> stream() {
      return (Stream)StreamSupport.stream(this.spliterator(), false).onClose(() -> {
         try {
            this.close();
         } catch (IOException var2) {
            throw new IORuntimeException(var2);
         }
      });
   }

   public Iterator<CsvRow> iterator() {
      return this.parse(this.reader);
   }

   public void close() throws IOException {
      IoUtil.close(this.reader);
   }
}
