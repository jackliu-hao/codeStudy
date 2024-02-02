package cn.hutool.json.serialize;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TemporalAccessorUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONNull;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONString;
import cn.hutool.json.JSONUtil;
import java.io.IOException;
import java.io.Writer;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class JSONWriter extends Writer {
   private final int indentFactor;
   private final int indent;
   private final Writer writer;
   private final JSONConfig config;
   private boolean needSeparator;
   private boolean arrayMode;

   public static JSONWriter of(Writer writer, int indentFactor, int indent, JSONConfig config) {
      return new JSONWriter(writer, indentFactor, indent, config);
   }

   public JSONWriter(Writer writer, int indentFactor, int indent, JSONConfig config) {
      this.writer = writer;
      this.indentFactor = indentFactor;
      this.indent = indent;
      this.config = config;
   }

   public JSONWriter beginObj() {
      this.writeRaw('{');
      return this;
   }

   public JSONWriter beginArray() {
      this.writeRaw('[');
      this.arrayMode = true;
      return this;
   }

   public JSONWriter end() {
      this.writeLF().writeSpace(this.indent);
      this.writeRaw((char)(this.arrayMode ? ']' : '}'));
      this.flush();
      this.arrayMode = false;
      this.needSeparator = true;
      return this;
   }

   public JSONWriter writeKey(String key) {
      if (this.needSeparator) {
         this.writeRaw(',');
      }

      this.writeLF().writeSpace(this.indentFactor + this.indent);
      return this.writeRaw(JSONUtil.quote(key));
   }

   public JSONWriter writeValue(Object value) {
      return JSONUtil.isNull(value) && this.config.isIgnoreNullValue() ? this : this.writeValueDirect(value);
   }

   public JSONWriter writeField(String key, Object value) {
      return JSONUtil.isNull(value) && this.config.isIgnoreNullValue() ? this : this.writeKey(key).writeValueDirect(value);
   }

   public void write(char[] cbuf, int off, int len) throws IOException {
      this.writer.write(cbuf, off, len);
   }

   public void flush() {
      try {
         this.writer.flush();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public void close() throws IOException {
      this.writer.close();
   }

   private JSONWriter writeValueDirect(Object value) {
      if (this.arrayMode) {
         if (this.needSeparator) {
            this.writeRaw(',');
         }

         this.writeLF().writeSpace(this.indentFactor + this.indent);
      } else {
         this.writeRaw(':').writeSpace(1);
      }

      this.needSeparator = true;
      return this.writeObjValue(value);
   }

   private JSONWriter writeObjValue(Object value) {
      int indent = this.indentFactor + this.indent;
      if (value != null && !(value instanceof JSONNull)) {
         if (value instanceof JSON) {
            ((JSON)value).write(this.writer, this.indentFactor, indent);
         } else if (!(value instanceof Map) && !(value instanceof Map.Entry)) {
            if (!(value instanceof Iterable) && !(value instanceof Iterator) && !ArrayUtil.isArray(value)) {
               if (value instanceof Number) {
                  this.writeNumberValue((Number)value);
               } else if (!(value instanceof Date) && !(value instanceof Calendar) && !(value instanceof TemporalAccessor)) {
                  if (value instanceof Boolean) {
                     this.writeBooleanValue((Boolean)value);
                  } else if (value instanceof JSONString) {
                     this.writeJSONStringValue((JSONString)value);
                  } else {
                     this.writeStrValue(value.toString());
                  }
               } else {
                  String format = null == this.config ? null : this.config.getDateFormat();
                  this.writeRaw(formatDate(value, format));
               }
            } else if (value instanceof byte[]) {
               this.writeStrValue(Base64.encode((byte[])((byte[])value)));
            } else {
               (new JSONArray(value)).write(this.writer, this.indentFactor, indent);
            }
         } else {
            (new JSONObject(value)).write(this.writer, this.indentFactor, indent);
         }
      } else {
         this.writeRaw(JSONNull.NULL.toString());
      }

      return this;
   }

   private void writeNumberValue(Number number) {
      boolean isStripTrailingZeros = null == this.config || this.config.isStripTrailingZeros();
      this.writeRaw(NumberUtil.toStr(number, isStripTrailingZeros));
   }

   private void writeBooleanValue(Boolean value) {
      this.writeRaw(value.toString());
   }

   private void writeJSONStringValue(JSONString jsonString) {
      String valueStr;
      try {
         valueStr = jsonString.toJSONString();
      } catch (Exception var4) {
         throw new JSONException(var4);
      }

      if (null != valueStr) {
         this.writeRaw(valueStr);
      } else {
         this.writeStrValue(jsonString.toString());
      }

   }

   private void writeStrValue(String csq) {
      try {
         JSONUtil.quote(csq, this.writer);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   private void writeSpace(int count) {
      if (this.indentFactor > 0) {
         for(int i = 0; i < count; ++i) {
            this.writeRaw(' ');
         }
      }

   }

   private JSONWriter writeLF() {
      if (this.indentFactor > 0) {
         this.writeRaw('\n');
      }

      return this;
   }

   private JSONWriter writeRaw(String csq) {
      try {
         this.writer.append(csq);
         return this;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   private JSONWriter writeRaw(char c) {
      try {
         this.writer.write(c);
         return this;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   private static String formatDate(Object dateObj, String format) {
      if (StrUtil.isNotBlank(format)) {
         String dateStr;
         if (dateObj instanceof TemporalAccessor) {
            dateStr = TemporalAccessorUtil.format((TemporalAccessor)dateObj, format);
         } else {
            dateStr = DateUtil.format(Convert.toDate(dateObj), format);
         }

         return !"#sss".equals(format) && !"#SSS".equals(format) ? JSONUtil.quote(dateStr) : dateStr;
      } else {
         long timeMillis;
         if (dateObj instanceof TemporalAccessor) {
            timeMillis = TemporalAccessorUtil.toEpochMilli((TemporalAccessor)dateObj);
         } else if (dateObj instanceof Date) {
            timeMillis = ((Date)dateObj).getTime();
         } else {
            if (!(dateObj instanceof Calendar)) {
               throw new UnsupportedOperationException("Unsupported Date type: " + dateObj.getClass());
            }

            timeMillis = ((Calendar)dateObj).getTimeInMillis();
         }

         return String.valueOf(timeMillis);
      }
   }
}
