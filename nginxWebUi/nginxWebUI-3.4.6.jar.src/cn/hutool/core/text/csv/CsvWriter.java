/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import cn.hutool.core.bean.BeanUtil;
/*     */ import cn.hutool.core.collection.ArrayIter;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CsvWriter
/*     */   implements Closeable, Flushable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final Writer writer;
/*     */   private final CsvWriteConfig config;
/*     */   private boolean newline = true;
/*     */   private boolean isFirstLine = true;
/*     */   
/*     */   public CsvWriter(String filePath) {
/*  62 */     this(FileUtil.file(filePath));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter(File file) {
/*  71 */     this(file, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter(String filePath, Charset charset) {
/*  81 */     this(FileUtil.file(filePath), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter(File file, Charset charset) {
/*  91 */     this(file, charset, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter(String filePath, Charset charset, boolean isAppend) {
/* 102 */     this(FileUtil.file(filePath), charset, isAppend);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter(File file, Charset charset, boolean isAppend) {
/* 113 */     this(file, charset, isAppend, (CsvWriteConfig)null);
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
/*     */   public CsvWriter(String filePath, Charset charset, boolean isAppend, CsvWriteConfig config) {
/* 125 */     this(FileUtil.file(filePath), charset, isAppend, config);
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
/*     */   public CsvWriter(File file, Charset charset, boolean isAppend, CsvWriteConfig config) {
/* 137 */     this(FileUtil.getWriter(file, charset, isAppend), config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter(Writer writer) {
/* 146 */     this(writer, (CsvWriteConfig)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter(Writer writer, CsvWriteConfig config) {
/* 156 */     this.writer = (writer instanceof BufferedWriter) ? writer : new BufferedWriter(writer);
/* 157 */     this.config = (CsvWriteConfig)ObjectUtil.defaultIfNull(config, CsvWriteConfig::defaultConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter setAlwaysDelimitText(boolean alwaysDelimitText) {
/* 168 */     this.config.setAlwaysDelimitText(alwaysDelimitText);
/* 169 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter setLineDelimiter(char[] lineDelimiter) {
/* 179 */     this.config.setLineDelimiter(lineDelimiter);
/* 180 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter write(String[]... lines) throws IORuntimeException {
/* 191 */     return write((Iterable<?>)new ArrayIter((Object[])lines));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter write(Iterable<?> lines) throws IORuntimeException {
/* 202 */     if (CollUtil.isNotEmpty(lines)) {
/* 203 */       for (Object values : lines) {
/* 204 */         appendLine(Convert.toStrArray(values));
/*     */       }
/* 206 */       flush();
/*     */     } 
/* 208 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter write(CsvData csvData) {
/* 219 */     if (csvData != null) {
/*     */       
/* 221 */       List<String> header = csvData.getHeader();
/* 222 */       if (CollUtil.isNotEmpty(header)) {
/* 223 */         writeHeaderLine(header.<String>toArray(new String[0]));
/*     */       }
/*     */       
/* 226 */       write(csvData.getRows());
/* 227 */       flush();
/*     */     } 
/* 229 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter writeBeans(Iterable<?> beans) {
/* 239 */     if (CollUtil.isNotEmpty(beans)) {
/* 240 */       boolean isFirst = true;
/*     */       
/* 242 */       for (Object bean : beans) {
/* 243 */         Map<String, Object> map = BeanUtil.beanToMap(bean, new String[0]);
/* 244 */         if (isFirst) {
/* 245 */           writeHeaderLine((String[])map.keySet().toArray((Object[])new String[0]));
/* 246 */           isFirst = false;
/*     */         } 
/* 248 */         writeLine(Convert.toStrArray(map.values()));
/*     */       } 
/* 250 */       flush();
/*     */     } 
/* 252 */     return this;
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
/*     */   public CsvWriter writeHeaderLine(String... fields) throws IORuntimeException {
/* 264 */     Map<String, String> headerAlias = this.config.headerAlias;
/* 265 */     if (MapUtil.isNotEmpty(headerAlias))
/*     */     {
/*     */       
/* 268 */       for (int i = 0; i < fields.length; i++) {
/* 269 */         String alias = headerAlias.get(fields[i]);
/* 270 */         if (null != alias) {
/* 271 */           fields[i] = alias;
/*     */         }
/*     */       } 
/*     */     }
/* 275 */     return writeLine(fields);
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
/*     */   public CsvWriter writeLine(String... fields) throws IORuntimeException {
/* 287 */     if (ArrayUtil.isEmpty((Object[])fields)) {
/* 288 */       return writeLine();
/*     */     }
/* 290 */     appendLine(fields);
/* 291 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvWriter writeLine() throws IORuntimeException {
/*     */     try {
/* 302 */       this.writer.write(this.config.lineDelimiter);
/* 303 */     } catch (IOException e) {
/* 304 */       throw new IORuntimeException(e);
/*     */     } 
/* 306 */     this.newline = true;
/* 307 */     return this;
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
/*     */   public CsvWriter writeComment(String comment) {
/* 320 */     Assert.notNull(this.config.commentCharacter, "Comment is disable!", new Object[0]);
/*     */     try {
/* 322 */       if (this.isFirstLine) {
/*     */         
/* 324 */         this.isFirstLine = false;
/*     */       } else {
/* 326 */         this.writer.write(this.config.lineDelimiter);
/*     */       } 
/* 328 */       this.writer.write(this.config.commentCharacter.charValue());
/* 329 */       this.writer.write(comment);
/* 330 */       this.newline = true;
/* 331 */     } catch (IOException e) {
/* 332 */       throw new IORuntimeException(e);
/*     */     } 
/* 334 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 339 */     IoUtil.close(this.writer);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() throws IORuntimeException {
/*     */     try {
/* 345 */       this.writer.flush();
/* 346 */     } catch (IOException e) {
/* 347 */       throw new IORuntimeException(e);
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
/*     */   
/*     */   private void appendLine(String... fields) throws IORuntimeException {
/*     */     try {
/* 361 */       doAppendLine(fields);
/* 362 */     } catch (IOException e) {
/* 363 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doAppendLine(String... fields) throws IOException {
/* 374 */     if (null != fields) {
/* 375 */       if (this.isFirstLine) {
/*     */         
/* 377 */         this.isFirstLine = false;
/*     */       } else {
/* 379 */         this.writer.write(this.config.lineDelimiter);
/*     */       } 
/* 381 */       for (String field : fields) {
/* 382 */         appendField(field);
/*     */       }
/* 384 */       this.newline = true;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void appendField(String value) throws IOException {
/* 395 */     boolean alwaysDelimitText = this.config.alwaysDelimitText;
/* 396 */     char textDelimiter = this.config.textDelimiter;
/* 397 */     char fieldSeparator = this.config.fieldSeparator;
/*     */     
/* 399 */     if (false == this.newline) {
/* 400 */       this.writer.write(fieldSeparator);
/*     */     } else {
/* 402 */       this.newline = false;
/*     */     } 
/*     */     
/* 405 */     if (null == value) {
/* 406 */       if (alwaysDelimitText) {
/* 407 */         this.writer.write(new char[] { textDelimiter, textDelimiter });
/*     */       }
/*     */       
/*     */       return;
/*     */     } 
/* 412 */     char[] valueChars = value.toCharArray();
/* 413 */     boolean needsTextDelimiter = alwaysDelimitText;
/* 414 */     boolean containsTextDelimiter = false;
/*     */     
/* 416 */     for (char c : valueChars) {
/* 417 */       if (c == textDelimiter) {
/*     */         
/* 419 */         containsTextDelimiter = needsTextDelimiter = true; break;
/*     */       } 
/* 421 */       if (c == fieldSeparator || c == '\n' || c == '\r')
/*     */       {
/* 423 */         needsTextDelimiter = true;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 428 */     if (needsTextDelimiter) {
/* 429 */       this.writer.write(textDelimiter);
/*     */     }
/*     */ 
/*     */     
/* 433 */     if (containsTextDelimiter) {
/* 434 */       for (char c : valueChars) {
/*     */         
/* 436 */         if (c == textDelimiter) {
/* 437 */           this.writer.write(textDelimiter);
/*     */         }
/* 439 */         this.writer.write(c);
/*     */       } 
/*     */     } else {
/* 442 */       this.writer.write(valueChars);
/*     */     } 
/*     */ 
/*     */     
/* 446 */     if (needsTextDelimiter)
/* 447 */       this.writer.write(textDelimiter); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */