/*     */ package cn.hutool.core.text.csv;
/*     */ 
/*     */ import cn.hutool.core.collection.ComputeIter;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.text.StrBuilder;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
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
/*     */ public final class CsvParser
/*     */   extends ComputeIter<CsvRow>
/*     */   implements Closeable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int DEFAULT_ROW_CAPACITY = 10;
/*     */   private final Reader reader;
/*     */   private final CsvReadConfig config;
/*  36 */   private final Buffer buf = new Buffer(32768);
/*     */ 
/*     */ 
/*     */   
/*  40 */   private int preChar = -1;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean inQuotes;
/*     */ 
/*     */ 
/*     */   
/*  48 */   private final StrBuilder currentField = new StrBuilder(512);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private CsvRow header;
/*     */ 
/*     */ 
/*     */   
/*  57 */   private long lineNo = -1L;
/*     */ 
/*     */ 
/*     */   
/*     */   private long inQuotesLineCount;
/*     */ 
/*     */ 
/*     */   
/*  65 */   private int firstLineFieldCount = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int maxFieldCount;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean finished;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CsvParser(Reader reader, CsvReadConfig config) {
/*  82 */     this.reader = Objects.<Reader>requireNonNull(reader, "reader must not be null");
/*  83 */     this.config = (CsvReadConfig)ObjectUtil.defaultIfNull(config, CsvReadConfig::defaultConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> getHeader() {
/*  93 */     if (this.config.headerLineNo < 0L) {
/*  94 */       throw new IllegalStateException("No header available - header parsing is disabled");
/*     */     }
/*  96 */     if (this.lineNo < this.config.beginLineNo) {
/*  97 */       throw new IllegalStateException("No header available - call nextRow() first");
/*     */     }
/*  99 */     return this.header.fields;
/*     */   }
/*     */ 
/*     */   
/*     */   protected CsvRow computeNext() {
/* 104 */     return nextRow();
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
/*     */   public CsvRow nextRow() throws IORuntimeException {
/* 116 */     while (false == this.finished) {
/* 117 */       List<String> currentFields = readLine();
/* 118 */       int fieldCount = currentFields.size();
/* 119 */       if (fieldCount < 1) {
/*     */         break;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 125 */       if (this.lineNo < this.config.beginLineNo) {
/*     */         continue;
/*     */       }
/*     */       
/* 129 */       if (this.lineNo > this.config.endLineNo) {
/*     */         break;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 135 */       if (this.config.skipEmptyRows && fieldCount == 1 && ((String)currentFields.get(0)).isEmpty()) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 141 */       if (this.config.errorOnDifferentFieldCount) {
/* 142 */         if (this.firstLineFieldCount < 0) {
/* 143 */           this.firstLineFieldCount = fieldCount;
/* 144 */         } else if (fieldCount != this.firstLineFieldCount) {
/* 145 */           throw new IORuntimeException(String.format("Line %d has %d fields, but first line has %d fields", new Object[] { Long.valueOf(this.lineNo), Integer.valueOf(fieldCount), Integer.valueOf(this.firstLineFieldCount) }));
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/* 150 */       if (fieldCount > this.maxFieldCount) {
/* 151 */         this.maxFieldCount = fieldCount;
/*     */       }
/*     */ 
/*     */       
/* 155 */       if (this.lineNo == this.config.headerLineNo && null == this.header) {
/* 156 */         initHeader(currentFields);
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 161 */       return new CsvRow(this.lineNo, (null == this.header) ? null : this.header.headerMap, currentFields);
/*     */     } 
/*     */     
/* 164 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void initHeader(List<String> currentFields) {
/* 173 */     Map<String, Integer> localHeaderMap = new LinkedHashMap<>(currentFields.size());
/* 174 */     for (int i = 0; i < currentFields.size(); i++) {
/* 175 */       String field = currentFields.get(i);
/* 176 */       if (MapUtil.isNotEmpty(this.config.headerAlias))
/*     */       {
/* 178 */         field = (String)ObjectUtil.defaultIfNull(this.config.headerAlias.get(field), field);
/*     */       }
/* 180 */       if (StrUtil.isNotEmpty(field) && false == localHeaderMap.containsKey(field)) {
/* 181 */         localHeaderMap.put(field, Integer.valueOf(i));
/*     */       }
/*     */     } 
/*     */     
/* 185 */     this.header = new CsvRow(this.lineNo, Collections.unmodifiableMap(localHeaderMap), Collections.unmodifiableList(currentFields));
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
/*     */ 
/*     */ 
/*     */   
/*     */   private List<String> readLine() throws IORuntimeException {
/* 202 */     if (this.inQuotesLineCount > 0L) {
/* 203 */       this.lineNo += this.inQuotesLineCount;
/* 204 */       this.inQuotesLineCount = 0L;
/*     */     } 
/*     */     
/* 207 */     List<String> currentFields = new ArrayList<>((this.maxFieldCount > 0) ? this.maxFieldCount : 10);
/*     */     
/* 209 */     StrBuilder currentField = this.currentField;
/* 210 */     Buffer buf = this.buf;
/* 211 */     int preChar = this.preChar;
/* 212 */     int copyLen = 0;
/* 213 */     boolean inComment = false;
/*     */     
/*     */     while (true) {
/* 216 */       if (false == buf.hasRemaining()) {
/*     */         
/* 218 */         if (copyLen > 0) {
/* 219 */           buf.appendTo(currentField, copyLen);
/*     */         }
/*     */         
/* 222 */         if (buf.read(this.reader) < 0) {
/*     */           
/* 224 */           this.finished = true;
/*     */           
/* 226 */           if (currentField.hasContent() || preChar == this.config.fieldSeparator)
/*     */           {
/* 228 */             addField(currentFields, currentField.toStringAndReset());
/*     */           }
/*     */           
/*     */           break;
/*     */         } 
/*     */         
/* 234 */         copyLen = 0;
/*     */       } 
/*     */       
/* 237 */       char c = buf.get();
/*     */ 
/*     */       
/* 240 */       if (preChar < 0 || preChar == 13 || preChar == 10)
/*     */       {
/*     */         
/* 243 */         if (null != this.config.commentCharacter && c == this.config.commentCharacter.charValue()) {
/* 244 */           inComment = true;
/*     */         }
/*     */       }
/*     */       
/* 248 */       if (inComment) {
/* 249 */         if (c == '\r' || c == '\n') {
/*     */           
/* 251 */           this.lineNo++;
/* 252 */           inComment = false;
/*     */         } 
/*     */         
/* 255 */         buf.mark();
/* 256 */         preChar = c;
/*     */         
/*     */         continue;
/*     */       } 
/* 260 */       if (this.inQuotes)
/*     */       
/* 262 */       { if (c == this.config.textDelimiter) {
/*     */           
/* 264 */           this.inQuotes = false;
/*     */         
/*     */         }
/* 267 */         else if (isLineEnd(c, preChar)) {
/* 268 */           this.inQuotesLineCount++;
/*     */         } 
/*     */ 
/*     */         
/* 272 */         copyLen++;
/*     */          }
/*     */       
/* 275 */       else if (c == this.config.fieldSeparator)
/*     */       
/* 277 */       { if (copyLen > 0) {
/* 278 */           buf.appendTo(currentField, copyLen);
/* 279 */           copyLen = 0;
/*     */         } 
/* 281 */         buf.mark();
/* 282 */         addField(currentFields, currentField.toStringAndReset()); }
/* 283 */       else if (c == this.config.textDelimiter)
/*     */       
/* 285 */       { this.inQuotes = true;
/* 286 */         copyLen++; }
/* 287 */       else { if (c == '\r') {
/*     */           
/* 289 */           if (copyLen > 0) {
/* 290 */             buf.appendTo(currentField, copyLen);
/*     */           }
/* 292 */           buf.mark();
/* 293 */           addField(currentFields, currentField.toStringAndReset());
/* 294 */           preChar = c; break;
/*     */         } 
/* 296 */         if (c == '\n') {
/*     */           
/* 298 */           if (preChar != 13) {
/* 299 */             if (copyLen > 0) {
/* 300 */               buf.appendTo(currentField, copyLen);
/*     */             }
/* 302 */             buf.mark();
/* 303 */             addField(currentFields, currentField.toStringAndReset());
/* 304 */             preChar = c;
/*     */             
/*     */             break;
/*     */           } 
/* 308 */           buf.mark();
/*     */         } else {
/*     */           
/* 311 */           copyLen++;
/*     */         }  }
/*     */ 
/*     */       
/* 315 */       preChar = c;
/*     */     } 
/*     */ 
/*     */     
/* 319 */     this.preChar = preChar;
/*     */     
/* 321 */     this.lineNo++;
/* 322 */     return currentFields;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 327 */     this.reader.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addField(List<String> currentFields, String field) {
/* 337 */     char textDelimiter = this.config.textDelimiter;
/*     */ 
/*     */     
/* 340 */     field = StrUtil.trim(field, 1, c -> (c.charValue() == '\n' || c.charValue() == '\r'));
/*     */     
/* 342 */     field = StrUtil.unWrap(field, textDelimiter);
/* 343 */     field = StrUtil.replace(field, "" + textDelimiter + textDelimiter, textDelimiter + "");
/* 344 */     if (this.config.trimField)
/*     */     {
/* 346 */       field = StrUtil.trim(field);
/*     */     }
/* 348 */     currentFields.add(field);
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
/*     */   private boolean isLineEnd(char c, int preChar) {
/* 360 */     return ((c == '\r' || c == '\n') && preChar != 13);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static class Buffer
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */     
/*     */     final char[] buf;
/*     */ 
/*     */     
/*     */     private int mark;
/*     */ 
/*     */     
/*     */     private int position;
/*     */ 
/*     */     
/*     */     private int limit;
/*     */ 
/*     */ 
/*     */     
/*     */     Buffer(int capacity) {
/* 387 */       this.buf = new char[capacity];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public final boolean hasRemaining() {
/* 396 */       return (this.position < this.limit);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     int read(Reader reader) {
/*     */       int length;
/*     */       try {
/* 408 */         length = reader.read(this.buf);
/* 409 */       } catch (IOException e) {
/* 410 */         throw new IORuntimeException(e);
/*     */       } 
/* 412 */       this.mark = 0;
/* 413 */       this.position = 0;
/* 414 */       this.limit = length;
/* 415 */       return length;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     char get() {
/* 426 */       return this.buf[this.position++];
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void mark() {
/* 433 */       this.mark = this.position;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     void appendTo(StrBuilder builder, int length) {
/* 444 */       builder.append(this.buf, this.mark, length);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\csv\CsvParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */