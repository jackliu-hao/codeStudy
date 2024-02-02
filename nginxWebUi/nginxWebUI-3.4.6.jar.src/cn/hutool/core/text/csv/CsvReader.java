/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Iterator;
/*     */ import java.util.stream.Stream;
/*     */ import java.util.stream.StreamSupport;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CsvReader
/*     */   extends CsvBaseReader
/*     */   implements Iterable<CsvRow>, Closeable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Reader reader;
/*     */   
/*     */   public CsvReader() {
/*  34 */     this((CsvReadConfig)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReader(CsvReadConfig config) {
/*  43 */     this((Reader)null, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReader(File file, CsvReadConfig config) {
/*  54 */     this(file, DEFAULT_CHARSET, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReader(Path path, CsvReadConfig config) {
/*  65 */     this(path, DEFAULT_CHARSET, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReader(File file, Charset charset, CsvReadConfig config) {
/*  77 */     this(FileUtil.getReader(file, charset), config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReader(Path path, Charset charset, CsvReadConfig config) {
/*  89 */     this(FileUtil.getReader(path, charset), config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvReader(Reader reader, CsvReadConfig config) {
/* 100 */     super(config);
/* 101 */     this.reader = reader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvData read() throws IORuntimeException {
/* 112 */     return read(this.reader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(CsvRowHandler rowHandler) throws IORuntimeException {
/* 124 */     read(this.reader, rowHandler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Stream<CsvRow> stream() {
/* 134 */     return StreamSupport.<CsvRow>stream(spliterator(), false)
/* 135 */       .onClose(() -> {
/*     */           try {
/*     */             close();
/* 138 */           } catch (IOException e) {
/*     */             throw new IORuntimeException(e);
/*     */           } 
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<CsvRow> iterator() {
/* 146 */     return (Iterator<CsvRow>)parse(this.reader);
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 151 */     IoUtil.close(this.reader);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */