/*     */ package cn.hutool.json.serialize;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.date.DateUtil;
/*     */ import cn.hutool.core.date.TemporalAccessorUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.json.JSON;
/*     */ import cn.hutool.json.JSONArray;
/*     */ import cn.hutool.json.JSONConfig;
/*     */ import cn.hutool.json.JSONException;
/*     */ import cn.hutool.json.JSONNull;
/*     */ import cn.hutool.json.JSONObject;
/*     */ import cn.hutool.json.JSONString;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.time.temporal.TemporalAccessor;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
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
/*     */ public class JSONWriter
/*     */   extends Writer
/*     */ {
/*     */   private final int indentFactor;
/*     */   private final int indent;
/*     */   private final Writer writer;
/*     */   private final JSONConfig config;
/*     */   private boolean needSeparator;
/*     */   private boolean arrayMode;
/*     */   
/*     */   public static JSONWriter of(Writer writer, int indentFactor, int indent, JSONConfig config) {
/*  75 */     return new JSONWriter(writer, indentFactor, indent, config);
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
/*     */   public JSONWriter(Writer writer, int indentFactor, int indent, JSONConfig config) {
/*  87 */     this.writer = writer;
/*  88 */     this.indentFactor = indentFactor;
/*  89 */     this.indent = indent;
/*  90 */     this.config = config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONWriter beginObj() {
/*  99 */     writeRaw('{');
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONWriter beginArray() {
/* 109 */     writeRaw('[');
/* 110 */     this.arrayMode = true;
/* 111 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONWriter end() {
/* 121 */     writeLF().writeSpace(this.indent);
/* 122 */     writeRaw(this.arrayMode ? 93 : 125);
/* 123 */     flush();
/* 124 */     this.arrayMode = false;
/*     */     
/* 126 */     this.needSeparator = true;
/* 127 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONWriter writeKey(String key) {
/* 137 */     if (this.needSeparator) {
/* 138 */       writeRaw(',');
/*     */     }
/*     */     
/* 141 */     writeLF().writeSpace(this.indentFactor + this.indent);
/* 142 */     return writeRaw(JSONUtil.quote(key));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public JSONWriter writeValue(Object value) {
/* 153 */     if (JSONUtil.isNull(value) && this.config.isIgnoreNullValue()) {
/* 154 */       return this;
/*     */     }
/* 156 */     return writeValueDirect(value);
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
/*     */   public JSONWriter writeField(String key, Object value) {
/* 168 */     if (JSONUtil.isNull(value) && this.config.isIgnoreNullValue()) {
/* 169 */       return this;
/*     */     }
/* 171 */     return writeKey(key).writeValueDirect(value);
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(char[] cbuf, int off, int len) throws IOException {
/* 176 */     this.writer.write(cbuf, off, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public void flush() {
/*     */     try {
/* 182 */       this.writer.flush();
/* 183 */     } catch (IOException e) {
/* 184 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 190 */     this.writer.close();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JSONWriter writeValueDirect(Object value) {
/* 201 */     if (this.arrayMode) {
/* 202 */       if (this.needSeparator) {
/* 203 */         writeRaw(',');
/*     */       }
/*     */       
/* 206 */       writeLF().writeSpace(this.indentFactor + this.indent);
/*     */     } else {
/* 208 */       writeRaw(':').writeSpace(1);
/*     */     } 
/* 210 */     this.needSeparator = true;
/* 211 */     return writeObjValue(value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JSONWriter writeObjValue(Object value) {
/* 221 */     int indent = this.indentFactor + this.indent;
/* 222 */     if (value == null || value instanceof JSONNull) {
/* 223 */       writeRaw(JSONNull.NULL.toString());
/* 224 */     } else if (value instanceof JSON) {
/* 225 */       ((JSON)value).write(this.writer, this.indentFactor, indent);
/* 226 */     } else if (value instanceof java.util.Map || value instanceof java.util.Map.Entry) {
/* 227 */       (new JSONObject(value)).write(this.writer, this.indentFactor, indent);
/* 228 */     } else if (value instanceof Iterable || value instanceof java.util.Iterator || ArrayUtil.isArray(value)) {
/* 229 */       if (value instanceof byte[]) {
/*     */ 
/*     */         
/* 232 */         writeStrValue(Base64.encode((byte[])value));
/*     */       } else {
/* 234 */         (new JSONArray(value)).write(this.writer, this.indentFactor, indent);
/*     */       } 
/* 236 */     } else if (value instanceof Number) {
/* 237 */       writeNumberValue((Number)value);
/* 238 */     } else if (value instanceof Date || value instanceof Calendar || value instanceof TemporalAccessor) {
/* 239 */       String format = (null == this.config) ? null : this.config.getDateFormat();
/* 240 */       writeRaw(formatDate(value, format));
/* 241 */     } else if (value instanceof Boolean) {
/* 242 */       writeBooleanValue((Boolean)value);
/* 243 */     } else if (value instanceof JSONString) {
/* 244 */       writeJSONStringValue((JSONString)value);
/*     */     } else {
/* 246 */       writeStrValue(value.toString());
/*     */     } 
/*     */     
/* 249 */     return this;
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
/*     */   private void writeNumberValue(Number number) {
/* 261 */     boolean isStripTrailingZeros = (null == this.config || this.config.isStripTrailingZeros());
/* 262 */     writeRaw(NumberUtil.toStr(number, isStripTrailingZeros));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeBooleanValue(Boolean value) {
/* 271 */     writeRaw(value.toString());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeJSONStringValue(JSONString jsonString) {
/*     */     String valueStr;
/*     */     try {
/* 284 */       valueStr = jsonString.toJSONString();
/* 285 */     } catch (Exception e) {
/* 286 */       throw new JSONException(e);
/*     */     } 
/* 288 */     if (null != valueStr) {
/* 289 */       writeRaw(valueStr);
/*     */     } else {
/* 291 */       writeStrValue(jsonString.toString());
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
/*     */   private void writeStrValue(String csq) {
/*     */     try {
/* 305 */       JSONUtil.quote(csq, this.writer);
/* 306 */     } catch (IOException e) {
/* 307 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void writeSpace(int count) {
/* 317 */     if (this.indentFactor > 0) {
/* 318 */       for (int i = 0; i < count; i++) {
/* 319 */         writeRaw(' ');
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JSONWriter writeLF() {
/* 330 */     if (this.indentFactor > 0) {
/* 331 */       writeRaw('\n');
/*     */     }
/* 333 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JSONWriter writeRaw(String csq) {
/*     */     try {
/* 344 */       this.writer.append(csq);
/* 345 */     } catch (IOException e) {
/* 346 */       throw new IORuntimeException(e);
/*     */     } 
/* 348 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private JSONWriter writeRaw(char c) {
/*     */     try {
/* 359 */       this.writer.write(c);
/* 360 */     } catch (IOException e) {
/* 361 */       throw new IORuntimeException(e);
/*     */     } 
/* 363 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String formatDate(Object dateObj, String format) {
/*     */     long timeMillis;
/* 374 */     if (StrUtil.isNotBlank(format)) {
/*     */       String dateStr;
/* 376 */       if (dateObj instanceof TemporalAccessor) {
/* 377 */         dateStr = TemporalAccessorUtil.format((TemporalAccessor)dateObj, format);
/*     */       } else {
/* 379 */         dateStr = DateUtil.format(Convert.toDate(dateObj), format);
/*     */       } 
/*     */       
/* 382 */       if ("#sss".equals(format) || "#SSS"
/* 383 */         .equals(format))
/*     */       {
/* 385 */         return dateStr;
/*     */       }
/*     */       
/* 388 */       return JSONUtil.quote(dateStr);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 393 */     if (dateObj instanceof TemporalAccessor) {
/* 394 */       timeMillis = TemporalAccessorUtil.toEpochMilli((TemporalAccessor)dateObj);
/* 395 */     } else if (dateObj instanceof Date) {
/* 396 */       timeMillis = ((Date)dateObj).getTime();
/* 397 */     } else if (dateObj instanceof Calendar) {
/* 398 */       timeMillis = ((Calendar)dateObj).getTimeInMillis();
/*     */     } else {
/* 400 */       throw new UnsupportedOperationException("Unsupported Date type: " + dateObj.getClass());
/*     */     } 
/* 402 */     return String.valueOf(timeMillis);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\serialize\JSONWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */