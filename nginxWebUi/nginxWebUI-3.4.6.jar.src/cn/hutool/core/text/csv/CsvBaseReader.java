/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.File;
/*     */ import java.io.Reader;
/*     */ import java.io.Serializable;
/*     */ import java.io.StringReader;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Path;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
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
/*     */ public class CsvBaseReader
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  33 */   protected static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */   
/*     */   private final CsvReadConfig config;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvBaseReader() {
/*  43 */     this(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvBaseReader(CsvReadConfig config) {
/*  52 */     this.config = (CsvReadConfig)ObjectUtil.defaultIfNull(config, CsvReadConfig::defaultConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFieldSeparator(char fieldSeparator) {
/*  62 */     this.config.setFieldSeparator(fieldSeparator);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTextDelimiter(char textDelimiter) {
/*  71 */     this.config.setTextDelimiter(textDelimiter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setContainsHeader(boolean containsHeader) {
/*  80 */     this.config.setContainsHeader(containsHeader);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSkipEmptyRows(boolean skipEmptyRows) {
/*  89 */     this.config.setSkipEmptyRows(skipEmptyRows);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setErrorOnDifferentFieldCount(boolean errorOnDifferentFieldCount) {
/*  98 */     this.config.setErrorOnDifferentFieldCount(errorOnDifferentFieldCount);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvData read(File file) throws IORuntimeException {
/* 109 */     return read(file, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvData readFromStr(String csvStr) {
/* 119 */     return read(new StringReader(csvStr));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void readFromStr(String csvStr, CsvRowHandler rowHandler) {
/* 129 */     read(parse(new StringReader(csvStr)), rowHandler);
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
/*     */   
/*     */   public CsvData read(File file, Charset charset) throws IORuntimeException {
/* 142 */     return read(Objects.<Path>requireNonNull(file.toPath(), "file must not be null"), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvData read(Path path) throws IORuntimeException {
/* 153 */     return read(path, DEFAULT_CHARSET);
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
/*     */   public CsvData read(Path path, Charset charset) throws IORuntimeException {
/* 165 */     Assert.notNull(path, "path must not be null", new Object[0]);
/* 166 */     return read(FileUtil.getReader(path, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvData read(Reader reader) throws IORuntimeException {
/* 177 */     CsvParser csvParser = parse(reader);
/* 178 */     List<CsvRow> rows = new ArrayList<>();
/* 179 */     read(csvParser, rows::add);
/* 180 */     List<String> header = (this.config.headerLineNo > -1L) ? csvParser.getHeader() : null;
/*     */     
/* 182 */     return new CsvData(header, rows);
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
/*     */   
/*     */   public List<Map<String, String>> readMapList(Reader reader) throws IORuntimeException {
/* 195 */     this.config.setContainsHeader(true);
/*     */     
/* 197 */     List<Map<String, String>> result = new ArrayList<>();
/* 198 */     read(reader, row -> result.add(row.getFieldMap()));
/* 199 */     return result;
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
/*     */ 
/*     */   
/*     */   public <T> List<T> read(Reader reader, Class<T> clazz) {
/* 213 */     this.config.setContainsHeader(true);
/*     */     
/* 215 */     List<T> result = new ArrayList<>();
/* 216 */     read(reader, row -> result.add(row.toBean(clazz)));
/* 217 */     return result;
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
/*     */ 
/*     */   
/*     */   public <T> List<T> read(String csvStr, Class<T> clazz) {
/* 231 */     this.config.setContainsHeader(true);
/*     */     
/* 233 */     List<T> result = new ArrayList<>();
/* 234 */     read(new StringReader(csvStr), row -> result.add(row.toBean(clazz)));
/* 235 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void read(Reader reader, CsvRowHandler rowHandler) throws IORuntimeException {
/* 246 */     read(parse(reader), rowHandler);
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
/*     */ 
/*     */   
/*     */   private void read(CsvParser csvParser, CsvRowHandler rowHandler) throws IORuntimeException {
/*     */     try {
/* 261 */       while (csvParser.hasNext()) {
/* 262 */         rowHandler.handle((CsvRow)csvParser.next());
/*     */       }
/*     */     } finally {
/* 265 */       IoUtil.close(csvParser);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CsvParser parse(Reader reader) throws IORuntimeException {
/* 277 */     return new CsvParser(reader, this.config);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvBaseReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */