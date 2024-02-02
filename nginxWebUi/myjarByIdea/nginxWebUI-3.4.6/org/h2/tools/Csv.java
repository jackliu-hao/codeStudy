package org.h2.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.h2.message.DbException;
import org.h2.mvstore.DataUtils;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.JdbcUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;

public class Csv implements SimpleRowSource {
   private String[] columnNames;
   private String characterSet;
   private char escapeCharacter = '"';
   private char fieldDelimiter = '"';
   private char fieldSeparatorRead = ',';
   private String fieldSeparatorWrite = ",";
   private boolean caseSensitiveColumnNames;
   private boolean preserveWhitespace;
   private boolean writeColumnHeader = true;
   private char lineComment;
   private String lineSeparator = System.lineSeparator();
   private String nullString = "";
   private String fileName;
   private BufferedReader input;
   private char[] inputBuffer;
   private int inputBufferPos;
   private int inputBufferStart = -1;
   private int inputBufferEnd;
   private Writer output;
   private boolean endOfLine;
   private boolean endOfFile;

   private int writeResultSet(ResultSet var1) throws SQLException {
      try {
         int var2 = 0;
         ResultSetMetaData var3 = var1.getMetaData();
         int var4 = var3.getColumnCount();
         String[] var5 = new String[var4];

         int var6;
         for(var6 = 0; var6 < var4; ++var6) {
            var5[var6] = var3.getColumnLabel(var6 + 1);
         }

         if (this.writeColumnHeader) {
            this.writeRow(var5);
         }

         while(var1.next()) {
            for(var6 = 0; var6 < var4; ++var6) {
               var5[var6] = var1.getString(var6 + 1);
            }

            this.writeRow(var5);
            ++var2;
         }

         this.output.close();
         var6 = var2;
         return var6;
      } catch (IOException var10) {
         throw DbException.convertIOException(var10, (String)null);
      } finally {
         this.close();
         JdbcUtils.closeSilently(var1);
      }
   }

   public int write(Writer var1, ResultSet var2) throws SQLException {
      this.output = var1;
      return this.writeResultSet(var2);
   }

   public int write(String var1, ResultSet var2, String var3) throws SQLException {
      this.init(var1, var3);

      try {
         this.initWrite();
         return this.writeResultSet(var2);
      } catch (IOException var5) {
         throw convertException("IOException writing " + var1, var5);
      }
   }

   public int write(Connection var1, String var2, String var3, String var4) throws SQLException {
      Statement var5 = var1.createStatement();
      ResultSet var6 = var5.executeQuery(var3);
      int var7 = this.write(var2, var6, var4);
      var5.close();
      return var7;
   }

   public ResultSet read(String var1, String[] var2, String var3) throws SQLException {
      this.init(var1, var3);

      try {
         return this.readResultSet(var2);
      } catch (IOException var5) {
         throw convertException("IOException reading " + var1, var5);
      }
   }

   public ResultSet read(Reader var1, String[] var2) throws IOException {
      this.init((String)null, (String)null);
      this.input = var1 instanceof BufferedReader ? (BufferedReader)var1 : new BufferedReader(var1, 4096);
      return this.readResultSet(var2);
   }

   private ResultSet readResultSet(String[] var1) throws IOException {
      this.columnNames = var1;
      this.initRead();
      SimpleResultSet var2 = new SimpleResultSet(this);
      this.makeColumnNamesUnique();
      String[] var3 = this.columnNames;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         var2.addColumn(var6, 12, Integer.MAX_VALUE, 0);
      }

      return var2;
   }

   private void makeColumnNamesUnique() {
      for(int var1 = 0; var1 < this.columnNames.length; ++var1) {
         StringBuilder var2 = new StringBuilder();
         String var3 = this.columnNames[var1];
         if (var3 != null && !var3.isEmpty()) {
            var2.append(var3);
         } else {
            var2.append('C').append(var1 + 1);
         }

         for(int var4 = 0; var4 < var1; ++var4) {
            String var5 = this.columnNames[var4];
            if (var2.toString().equals(var5)) {
               var2.append('1');
               var4 = -1;
            }
         }

         this.columnNames[var1] = var2.toString();
      }

   }

   private void init(String var1, String var2) {
      this.fileName = var1;
      this.characterSet = var2;
   }

   private void initWrite() throws IOException {
      if (this.output == null) {
         try {
            OutputStream var1 = FileUtils.newOutputStream(this.fileName, false);
            BufferedOutputStream var3 = new BufferedOutputStream(var1, 4096);
            this.output = new BufferedWriter(this.characterSet != null ? new OutputStreamWriter(var3, this.characterSet) : new OutputStreamWriter(var3));
         } catch (Exception var2) {
            this.close();
            throw DataUtils.convertToIOException(var2);
         }
      }

   }

   private void writeRow(String[] var1) throws IOException {
      for(int var2 = 0; var2 < var1.length; ++var2) {
         if (var2 > 0 && this.fieldSeparatorWrite != null) {
            this.output.write(this.fieldSeparatorWrite);
         }

         String var3 = var1[var2];
         if (var3 != null) {
            if (this.escapeCharacter != 0) {
               if (this.fieldDelimiter != 0) {
                  this.output.write(this.fieldDelimiter);
               }

               this.output.write(this.escape(var3));
               if (this.fieldDelimiter != 0) {
                  this.output.write(this.fieldDelimiter);
               }
            } else {
               this.output.write(var3);
            }
         } else if (this.nullString != null && this.nullString.length() > 0) {
            this.output.write(this.nullString);
         }
      }

      this.output.write(this.lineSeparator);
   }

   private String escape(String var1) {
      if (var1.indexOf(this.fieldDelimiter) >= 0 || this.escapeCharacter != this.fieldDelimiter && var1.indexOf(this.escapeCharacter) >= 0) {
         int var2 = var1.length();
         StringBuilder var3 = new StringBuilder(var2);

         for(int var4 = 0; var4 < var2; ++var4) {
            char var5 = var1.charAt(var4);
            if (var5 == this.fieldDelimiter || var5 == this.escapeCharacter) {
               var3.append(this.escapeCharacter);
            }

            var3.append(var5);
         }

         return var3.toString();
      } else {
         return var1;
      }
   }

   private void initRead() throws IOException {
      if (this.input == null) {
         try {
            this.input = FileUtils.newBufferedReader(this.fileName, this.characterSet != null ? Charset.forName(this.characterSet) : StandardCharsets.UTF_8);
         } catch (IOException var2) {
            this.close();
            throw var2;
         }
      }

      this.input.mark(1);
      int var1 = this.input.read();
      if (var1 != 65279) {
         this.input.reset();
      }

      this.inputBuffer = new char[8192];
      if (this.columnNames == null) {
         this.readHeader();
      }

   }

   private void readHeader() throws IOException {
      ArrayList var1 = new ArrayList();

      while(true) {
         String var2 = this.readValue();
         if (var2 == null) {
            if (this.endOfLine) {
               if (this.endOfFile || !var1.isEmpty()) {
                  break;
               }
            } else {
               var2 = "COLUMN" + var1.size();
               var1.add(var2);
            }
         } else {
            if (var2.isEmpty()) {
               var2 = "COLUMN" + var1.size();
            } else if (!this.caseSensitiveColumnNames && isSimpleColumnName(var2)) {
               var2 = StringUtils.toUpperEnglish(var2);
            }

            var1.add(var2);
            if (this.endOfLine) {
               break;
            }
         }
      }

      this.columnNames = (String[])var1.toArray(new String[0]);
   }

   private static boolean isSimpleColumnName(String var0) {
      int var1 = 0;

      for(int var2 = var0.length(); var1 < var2; ++var1) {
         char var3 = var0.charAt(var1);
         if (var1 == 0) {
            if (var3 != '_' && !Character.isLetter(var3)) {
               return false;
            }
         } else if (var3 != '_' && !Character.isLetterOrDigit(var3)) {
            return false;
         }
      }

      return var0.length() != 0;
   }

   private void pushBack() {
      --this.inputBufferPos;
   }

   private int readChar() throws IOException {
      return this.inputBufferPos >= this.inputBufferEnd ? this.readBuffer() : this.inputBuffer[this.inputBufferPos++];
   }

   private int readBuffer() throws IOException {
      if (this.endOfFile) {
         return -1;
      } else {
         int var1;
         if (this.inputBufferStart >= 0) {
            var1 = this.inputBufferPos - this.inputBufferStart;
            if (var1 > 0) {
               char[] var2 = this.inputBuffer;
               if (var1 + 4096 > var2.length) {
                  this.inputBuffer = new char[var2.length * 2];
               }

               System.arraycopy(var2, this.inputBufferStart, this.inputBuffer, 0, var1);
            }

            this.inputBufferStart = 0;
         } else {
            var1 = 0;
         }

         this.inputBufferPos = var1;
         int var3 = this.input.read(this.inputBuffer, var1, 4096);
         if (var3 == -1) {
            this.inputBufferEnd = -1024;
            this.endOfFile = true;
            ++this.inputBufferPos;
            return -1;
         } else {
            this.inputBufferEnd = var1 + var3;
            return this.inputBuffer[this.inputBufferPos++];
         }
      }
   }

   private String readValue() throws IOException {
      this.endOfLine = false;
      this.inputBufferStart = this.inputBufferPos;

      int var1;
      do {
         var1 = this.readChar();
         if (var1 == this.fieldDelimiter) {
            boolean var5 = false;
            this.inputBufferStart = this.inputBufferPos;

            byte var3;
            while(true) {
               var1 = this.readChar();
               if (var1 == this.fieldDelimiter) {
                  var1 = this.readChar();
                  if (var1 != this.fieldDelimiter) {
                     var3 = 2;
                     break;
                  }

                  var5 = true;
               } else if (var1 == this.escapeCharacter) {
                  var1 = this.readChar();
                  if (var1 < 0) {
                     var3 = 1;
                     break;
                  }

                  var5 = true;
               } else if (var1 < 0) {
                  var3 = 1;
                  break;
               }
            }

            String var4 = new String(this.inputBuffer, this.inputBufferStart, this.inputBufferPos - this.inputBufferStart - var3);
            if (var5) {
               var4 = this.unEscape(var4);
            }

            for(this.inputBufferStart = -1; var1 != this.fieldSeparatorRead; var1 = this.readChar()) {
               if (var1 == 10 || var1 < 0 || var1 == 13) {
                  this.endOfLine = true;
                  break;
               }

               if (var1 != 32 && var1 != 9) {
                  this.pushBack();
                  break;
               }
            }

            return var4;
         }

         if (var1 == 10 || var1 < 0 || var1 == 13) {
            this.endOfLine = true;
            return null;
         }

         if (var1 == this.fieldSeparatorRead) {
            return null;
         }
      } while(var1 <= 32);

      if (this.lineComment != 0 && var1 == this.lineComment) {
         this.inputBufferStart = -1;

         do {
            var1 = this.readChar();
         } while(var1 != 10 && var1 >= 0 && var1 != 13);

         this.endOfLine = true;
         return null;
      } else {
         while(true) {
            var1 = this.readChar();
            if (var1 == this.fieldSeparatorRead) {
               break;
            }

            if (var1 == 10 || var1 < 0 || var1 == 13) {
               this.endOfLine = true;
               break;
            }
         }

         String var2 = new String(this.inputBuffer, this.inputBufferStart, this.inputBufferPos - this.inputBufferStart - 1);
         if (!this.preserveWhitespace) {
            var2 = var2.trim();
         }

         this.inputBufferStart = -1;
         return this.readNull(var2);
      }
   }

   private String readNull(String var1) {
      return var1.equals(this.nullString) ? null : var1;
   }

   private String unEscape(String var1) {
      StringBuilder var2 = new StringBuilder(var1.length());
      int var3 = 0;
      char[] var4 = null;

      while(true) {
         int var5 = var1.indexOf(this.escapeCharacter, var3);
         if (var5 < 0) {
            var5 = var1.indexOf(this.fieldDelimiter, var3);
            if (var5 < 0) {
               break;
            }
         }

         if (var4 == null) {
            var4 = var1.toCharArray();
         }

         var2.append(var4, var3, var5 - var3);
         if (var5 == var1.length() - 1) {
            var3 = var1.length();
            break;
         }

         var2.append(var4[var5 + 1]);
         var3 = var5 + 2;
      }

      var2.append(var1, var3, var1.length());
      return var2.toString();
   }

   public Object[] readRow() throws SQLException {
      if (this.input == null) {
         return null;
      } else {
         String[] var1 = new String[this.columnNames.length];

         try {
            int var2 = 0;

            while(true) {
               String var3 = this.readValue();
               if (var3 == null && this.endOfLine) {
                  if (var2 != 0) {
                     break;
                  }

                  if (this.endOfFile) {
                     return null;
                  }
               } else {
                  if (var2 < var1.length) {
                     var1[var2++] = var3;
                  }

                  if (this.endOfLine) {
                     break;
                  }
               }
            }

            return var1;
         } catch (IOException var4) {
            throw convertException("IOException reading from " + this.fileName, var4);
         }
      }
   }

   private static SQLException convertException(String var0, Exception var1) {
      return DbException.getJdbcSQLException(90028, var1, var0);
   }

   public void close() {
      IOUtils.closeSilently(this.input);
      this.input = null;
      IOUtils.closeSilently(this.output);
      this.output = null;
   }

   public void reset() throws SQLException {
      throw new SQLException("Method is not supported", "CSV");
   }

   public void setFieldSeparatorWrite(String var1) {
      this.fieldSeparatorWrite = var1;
   }

   public String getFieldSeparatorWrite() {
      return this.fieldSeparatorWrite;
   }

   public void setCaseSensitiveColumnNames(boolean var1) {
      this.caseSensitiveColumnNames = var1;
   }

   public boolean getCaseSensitiveColumnNames() {
      return this.caseSensitiveColumnNames;
   }

   public void setFieldSeparatorRead(char var1) {
      this.fieldSeparatorRead = var1;
   }

   public char getFieldSeparatorRead() {
      return this.fieldSeparatorRead;
   }

   public void setLineCommentCharacter(char var1) {
      this.lineComment = var1;
   }

   public char getLineCommentCharacter() {
      return this.lineComment;
   }

   public void setFieldDelimiter(char var1) {
      this.fieldDelimiter = var1;
   }

   public char getFieldDelimiter() {
      return this.fieldDelimiter;
   }

   public void setEscapeCharacter(char var1) {
      this.escapeCharacter = var1;
   }

   public char getEscapeCharacter() {
      return this.escapeCharacter;
   }

   public void setLineSeparator(String var1) {
      this.lineSeparator = var1;
   }

   public String getLineSeparator() {
      return this.lineSeparator;
   }

   public void setNullString(String var1) {
      this.nullString = var1;
   }

   public String getNullString() {
      return this.nullString;
   }

   public void setPreserveWhitespace(boolean var1) {
      this.preserveWhitespace = var1;
   }

   public boolean getPreserveWhitespace() {
      return this.preserveWhitespace;
   }

   public void setWriteColumnHeader(boolean var1) {
      this.writeColumnHeader = var1;
   }

   public boolean getWriteColumnHeader() {
      return this.writeColumnHeader;
   }

   public String setOptions(String var1) {
      String var2 = null;
      String[] var3 = StringUtils.arraySplit(var1, ' ', false);
      String[] var4 = var3;
      int var5 = var3.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = var4[var6];
         if (!var7.isEmpty()) {
            int var8 = var7.indexOf(61);
            String var9 = StringUtils.trim(var7.substring(0, var8), true, true, " ");
            String var10 = var7.substring(var8 + 1);
            char var11 = var10.isEmpty() ? 0 : var10.charAt(0);
            if (isParam(var9, "escape", "esc", "escapeCharacter")) {
               this.setEscapeCharacter(var11);
            } else if (isParam(var9, "fieldDelimiter", "fieldDelim")) {
               this.setFieldDelimiter(var11);
            } else if (isParam(var9, "fieldSeparator", "fieldSep")) {
               this.setFieldSeparatorRead(var11);
               this.setFieldSeparatorWrite(var10);
            } else if (isParam(var9, "lineComment", "lineCommentCharacter")) {
               this.setLineCommentCharacter(var11);
            } else if (isParam(var9, "lineSeparator", "lineSep")) {
               this.setLineSeparator(var10);
            } else if (isParam(var9, "null", "nullString")) {
               this.setNullString(var10);
            } else if (isParam(var9, "charset", "characterSet")) {
               var2 = var10;
            } else if (isParam(var9, "preserveWhitespace")) {
               this.setPreserveWhitespace(Utils.parseBoolean(var10, false, false));
            } else if (isParam(var9, "writeColumnHeader")) {
               this.setWriteColumnHeader(Utils.parseBoolean(var10, true, false));
            } else {
               if (!isParam(var9, "caseSensitiveColumnNames")) {
                  throw DbException.getUnsupportedException(var9);
               }

               this.setCaseSensitiveColumnNames(Utils.parseBoolean(var10, false, false));
            }
         }
      }

      return var2;
   }

   private static boolean isParam(String var0, String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         if (var0.equalsIgnoreCase(var5)) {
            return true;
         }
      }

      return false;
   }
}
