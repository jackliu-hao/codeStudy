package cn.hutool.core.text.csv;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ArrayIter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class CsvWriter implements Closeable, Flushable, Serializable {
   private static final long serialVersionUID = 1L;
   private final Writer writer;
   private final CsvWriteConfig config;
   private boolean newline;
   private boolean isFirstLine;

   public CsvWriter(String filePath) {
      this(FileUtil.file(filePath));
   }

   public CsvWriter(File file) {
      this(file, CharsetUtil.CHARSET_UTF_8);
   }

   public CsvWriter(String filePath, Charset charset) {
      this(FileUtil.file(filePath), charset);
   }

   public CsvWriter(File file, Charset charset) {
      this(file, charset, false);
   }

   public CsvWriter(String filePath, Charset charset, boolean isAppend) {
      this(FileUtil.file(filePath), charset, isAppend);
   }

   public CsvWriter(File file, Charset charset, boolean isAppend) {
      this((File)file, charset, isAppend, (CsvWriteConfig)null);
   }

   public CsvWriter(String filePath, Charset charset, boolean isAppend, CsvWriteConfig config) {
      this(FileUtil.file(filePath), charset, isAppend, config);
   }

   public CsvWriter(File file, Charset charset, boolean isAppend, CsvWriteConfig config) {
      this((Writer)FileUtil.getWriter(file, charset, isAppend), (CsvWriteConfig)config);
   }

   public CsvWriter(Writer writer) {
      this((Writer)writer, (CsvWriteConfig)null);
   }

   public CsvWriter(Writer writer, CsvWriteConfig config) {
      this.newline = true;
      this.isFirstLine = true;
      this.writer = (Writer)(writer instanceof BufferedWriter ? writer : new BufferedWriter(writer));
      this.config = (CsvWriteConfig)ObjectUtil.defaultIfNull(config, (Supplier)(CsvWriteConfig::defaultConfig));
   }

   public CsvWriter setAlwaysDelimitText(boolean alwaysDelimitText) {
      this.config.setAlwaysDelimitText(alwaysDelimitText);
      return this;
   }

   public CsvWriter setLineDelimiter(char[] lineDelimiter) {
      this.config.setLineDelimiter(lineDelimiter);
      return this;
   }

   public CsvWriter write(String[]... lines) throws IORuntimeException {
      return this.write((Iterable)(new ArrayIter(lines)));
   }

   public CsvWriter write(Iterable<?> lines) throws IORuntimeException {
      if (CollUtil.isNotEmpty(lines)) {
         Iterator var2 = lines.iterator();

         while(var2.hasNext()) {
            Object values = var2.next();
            this.appendLine(Convert.toStrArray(values));
         }

         this.flush();
      }

      return this;
   }

   public CsvWriter write(CsvData csvData) {
      if (csvData != null) {
         List<String> header = csvData.getHeader();
         if (CollUtil.isNotEmpty((Collection)header)) {
            this.writeHeaderLine((String[])header.toArray(new String[0]));
         }

         this.write((Iterable)csvData.getRows());
         this.flush();
      }

      return this;
   }

   public CsvWriter writeBeans(Iterable<?> beans) {
      if (CollUtil.isNotEmpty(beans)) {
         boolean isFirst = true;

         Map map;
         for(Iterator var4 = beans.iterator(); var4.hasNext(); this.writeLine(Convert.toStrArray(map.values()))) {
            Object bean = var4.next();
            map = BeanUtil.beanToMap(bean);
            if (isFirst) {
               this.writeHeaderLine((String[])map.keySet().toArray(new String[0]));
               isFirst = false;
            }
         }

         this.flush();
      }

      return this;
   }

   public CsvWriter writeHeaderLine(String... fields) throws IORuntimeException {
      Map<String, String> headerAlias = this.config.headerAlias;
      if (MapUtil.isNotEmpty(headerAlias)) {
         for(int i = 0; i < fields.length; ++i) {
            String alias = (String)headerAlias.get(fields[i]);
            if (null != alias) {
               fields[i] = alias;
            }
         }
      }

      return this.writeLine(fields);
   }

   public CsvWriter writeLine(String... fields) throws IORuntimeException {
      if (ArrayUtil.isEmpty((Object[])fields)) {
         return this.writeLine();
      } else {
         this.appendLine(fields);
         return this;
      }
   }

   public CsvWriter writeLine() throws IORuntimeException {
      try {
         this.writer.write(this.config.lineDelimiter);
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }

      this.newline = true;
      return this;
   }

   public CsvWriter writeComment(String comment) {
      Assert.notNull(this.config.commentCharacter, "Comment is disable!");

      try {
         if (this.isFirstLine) {
            this.isFirstLine = false;
         } else {
            this.writer.write(this.config.lineDelimiter);
         }

         this.writer.write(this.config.commentCharacter);
         this.writer.write(comment);
         this.newline = true;
         return this;
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public void close() {
      IoUtil.close(this.writer);
   }

   public void flush() throws IORuntimeException {
      try {
         this.writer.flush();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   private void appendLine(String... fields) throws IORuntimeException {
      try {
         this.doAppendLine(fields);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   private void doAppendLine(String... fields) throws IOException {
      if (null != fields) {
         if (this.isFirstLine) {
            this.isFirstLine = false;
         } else {
            this.writer.write(this.config.lineDelimiter);
         }

         String[] var2 = fields;
         int var3 = fields.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String field = var2[var4];
            this.appendField(field);
         }

         this.newline = true;
      }

   }

   private void appendField(String value) throws IOException {
      boolean alwaysDelimitText = this.config.alwaysDelimitText;
      char textDelimiter = this.config.textDelimiter;
      char fieldSeparator = this.config.fieldSeparator;
      if (!this.newline) {
         this.writer.write(fieldSeparator);
      } else {
         this.newline = false;
      }

      if (null == value) {
         if (alwaysDelimitText) {
            this.writer.write(new char[]{textDelimiter, textDelimiter});
         }

      } else {
         char[] valueChars = value.toCharArray();
         boolean needsTextDelimiter = alwaysDelimitText;
         boolean containsTextDelimiter = false;
         char[] var8 = valueChars;
         int var9 = valueChars.length;

         int var10;
         char c;
         for(var10 = 0; var10 < var9; ++var10) {
            c = var8[var10];
            if (c == textDelimiter) {
               needsTextDelimiter = true;
               containsTextDelimiter = true;
               break;
            }

            if (c == fieldSeparator || c == '\n' || c == '\r') {
               needsTextDelimiter = true;
            }
         }

         if (needsTextDelimiter) {
            this.writer.write(textDelimiter);
         }

         if (containsTextDelimiter) {
            var8 = valueChars;
            var9 = valueChars.length;

            for(var10 = 0; var10 < var9; ++var10) {
               c = var8[var10];
               if (c == textDelimiter) {
                  this.writer.write(textDelimiter);
               }

               this.writer.write(c);
            }
         } else {
            this.writer.write(valueChars);
         }

         if (needsTextDelimiter) {
            this.writer.write(textDelimiter);
         }

      }
   }
}
