package cn.hutool.core.text.csv;

import cn.hutool.core.collection.ComputeIter;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class CsvParser extends ComputeIter<CsvRow> implements Closeable, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int DEFAULT_ROW_CAPACITY = 10;
   private final Reader reader;
   private final CsvReadConfig config;
   private final Buffer buf = new Buffer(32768);
   private int preChar = -1;
   private boolean inQuotes;
   private final StrBuilder currentField = new StrBuilder(512);
   private CsvRow header;
   private long lineNo = -1L;
   private long inQuotesLineCount;
   private int firstLineFieldCount = -1;
   private int maxFieldCount;
   private boolean finished;

   public CsvParser(Reader reader, CsvReadConfig config) {
      this.reader = (Reader)Objects.requireNonNull(reader, "reader must not be null");
      this.config = (CsvReadConfig)ObjectUtil.defaultIfNull(config, (Supplier)(CsvReadConfig::defaultConfig));
   }

   public List<String> getHeader() {
      if (this.config.headerLineNo < 0L) {
         throw new IllegalStateException("No header available - header parsing is disabled");
      } else if (this.lineNo < this.config.beginLineNo) {
         throw new IllegalStateException("No header available - call nextRow() first");
      } else {
         return this.header.fields;
      }
   }

   protected CsvRow computeNext() {
      return this.nextRow();
   }

   public CsvRow nextRow() throws IORuntimeException {
      while(true) {
         if (!this.finished) {
            List<String> currentFields = this.readLine();
            int fieldCount = currentFields.size();
            if (fieldCount >= 1) {
               if (this.lineNo < this.config.beginLineNo) {
                  continue;
               }

               if (this.lineNo <= this.config.endLineNo) {
                  if (this.config.skipEmptyRows && fieldCount == 1 && ((String)currentFields.get(0)).isEmpty()) {
                     continue;
                  }

                  if (this.config.errorOnDifferentFieldCount) {
                     if (this.firstLineFieldCount < 0) {
                        this.firstLineFieldCount = fieldCount;
                     } else if (fieldCount != this.firstLineFieldCount) {
                        throw new IORuntimeException(String.format("Line %d has %d fields, but first line has %d fields", this.lineNo, fieldCount, this.firstLineFieldCount));
                     }
                  }

                  if (fieldCount > this.maxFieldCount) {
                     this.maxFieldCount = fieldCount;
                  }

                  if (this.lineNo == this.config.headerLineNo && null == this.header) {
                     this.initHeader(currentFields);
                     continue;
                  }

                  return new CsvRow(this.lineNo, null == this.header ? null : this.header.headerMap, currentFields);
               }
            }
         }

         return null;
      }
   }

   private void initHeader(List<String> currentFields) {
      Map<String, Integer> localHeaderMap = new LinkedHashMap(currentFields.size());

      for(int i = 0; i < currentFields.size(); ++i) {
         String field = (String)currentFields.get(i);
         if (MapUtil.isNotEmpty(this.config.headerAlias)) {
            field = (String)ObjectUtil.defaultIfNull(this.config.headerAlias.get(field), (Object)field);
         }

         if (StrUtil.isNotEmpty(field) && !localHeaderMap.containsKey(field)) {
            localHeaderMap.put(field, i);
         }
      }

      this.header = new CsvRow(this.lineNo, Collections.unmodifiableMap(localHeaderMap), Collections.unmodifiableList(currentFields));
   }

   private List<String> readLine() throws IORuntimeException {
      if (this.inQuotesLineCount > 0L) {
         this.lineNo += this.inQuotesLineCount;
         this.inQuotesLineCount = 0L;
      }

      List<String> currentFields = new ArrayList(this.maxFieldCount > 0 ? this.maxFieldCount : 10);
      StrBuilder currentField = this.currentField;
      Buffer buf = this.buf;
      int preChar = this.preChar;
      int copyLen = 0;
      boolean inComment = false;

      while(true) {
         if (!buf.hasRemaining()) {
            if (copyLen > 0) {
               buf.appendTo(currentField, copyLen);
            }

            if (buf.read(this.reader) < 0) {
               this.finished = true;
               if (currentField.hasContent() || preChar == this.config.fieldSeparator) {
                  this.addField(currentFields, currentField.toStringAndReset());
               }
               break;
            }

            copyLen = 0;
         }

         char c = buf.get();
         if ((preChar < 0 || preChar == 13 || preChar == 10) && null != this.config.commentCharacter && c == this.config.commentCharacter) {
            inComment = true;
         }

         if (!inComment) {
            if (this.inQuotes) {
               if (c == this.config.textDelimiter) {
                  this.inQuotes = false;
               } else if (this.isLineEnd(c, preChar)) {
                  ++this.inQuotesLineCount;
               }

               ++copyLen;
            } else if (c == this.config.fieldSeparator) {
               if (copyLen > 0) {
                  buf.appendTo(currentField, copyLen);
                  copyLen = 0;
               }

               buf.mark();
               this.addField(currentFields, currentField.toStringAndReset());
            } else if (c == this.config.textDelimiter) {
               this.inQuotes = true;
               ++copyLen;
            } else {
               if (c == '\r') {
                  if (copyLen > 0) {
                     buf.appendTo(currentField, copyLen);
                  }

                  buf.mark();
                  this.addField(currentFields, currentField.toStringAndReset());
                  preChar = c;
                  break;
               }

               if (c == '\n') {
                  if (preChar != 13) {
                     if (copyLen > 0) {
                        buf.appendTo(currentField, copyLen);
                     }

                     buf.mark();
                     this.addField(currentFields, currentField.toStringAndReset());
                     preChar = c;
                     break;
                  }

                  buf.mark();
               } else {
                  ++copyLen;
               }
            }

            preChar = c;
         } else {
            if (c == '\r' || c == '\n') {
               ++this.lineNo;
               inComment = false;
            }

            buf.mark();
            preChar = c;
         }
      }

      this.preChar = preChar;
      ++this.lineNo;
      return currentFields;
   }

   public void close() throws IOException {
      this.reader.close();
   }

   private void addField(List<String> currentFields, String field) {
      char textDelimiter = this.config.textDelimiter;
      field = StrUtil.trim(field, 1, (c) -> {
         return c == '\n' || c == '\r';
      });
      field = StrUtil.unWrap(field, textDelimiter);
      field = StrUtil.replace(field, "" + textDelimiter + textDelimiter, textDelimiter + "");
      if (this.config.trimField) {
         field = StrUtil.trim(field);
      }

      currentFields.add(field);
   }

   private boolean isLineEnd(char c, int preChar) {
      return (c == '\r' || c == '\n') && preChar != 13;
   }

   private static class Buffer implements Serializable {
      private static final long serialVersionUID = 1L;
      final char[] buf;
      private int mark;
      private int position;
      private int limit;

      Buffer(int capacity) {
         this.buf = new char[capacity];
      }

      public final boolean hasRemaining() {
         return this.position < this.limit;
      }

      int read(Reader reader) {
         int length;
         try {
            length = reader.read(this.buf);
         } catch (IOException var4) {
            throw new IORuntimeException(var4);
         }

         this.mark = 0;
         this.position = 0;
         this.limit = length;
         return length;
      }

      char get() {
         return this.buf[this.position++];
      }

      void mark() {
         this.mark = this.position;
      }

      void appendTo(StrBuilder builder, int length) {
         builder.append(this.buf, this.mark, length);
      }
   }
}
