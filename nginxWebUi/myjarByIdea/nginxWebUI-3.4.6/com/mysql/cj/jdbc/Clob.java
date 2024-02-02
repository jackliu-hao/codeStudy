package com.mysql.cj.jdbc;

import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJException;
import com.mysql.cj.exceptions.ExceptionInterceptor;
import com.mysql.cj.jdbc.exceptions.SQLError;
import com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping;
import com.mysql.cj.protocol.OutputStreamWatcher;
import com.mysql.cj.protocol.WatchableOutputStream;
import com.mysql.cj.protocol.WatchableStream;
import com.mysql.cj.protocol.WatchableWriter;
import com.mysql.cj.protocol.WriterWatcher;
import com.mysql.cj.util.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.SQLException;

public class Clob implements java.sql.Clob, OutputStreamWatcher, WriterWatcher {
   private String charData;
   private ExceptionInterceptor exceptionInterceptor;

   Clob(ExceptionInterceptor exceptionInterceptor) {
      this.charData = "";
      this.exceptionInterceptor = exceptionInterceptor;
   }

   public Clob(String charDataInit, ExceptionInterceptor exceptionInterceptor) {
      this.charData = charDataInit;
      this.exceptionInterceptor = exceptionInterceptor;
   }

   public InputStream getAsciiStream() throws SQLException {
      try {
         return this.charData != null ? new ByteArrayInputStream(StringUtils.getBytes(this.charData)) : null;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public Reader getCharacterStream() throws SQLException {
      try {
         return this.charData != null ? new StringReader(this.charData) : null;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public String getSubString(long startPos, int length) throws SQLException {
      try {
         if (startPos < 1L) {
            throw SQLError.createSQLException(Messages.getString("Clob.6"), "S1009", this.exceptionInterceptor);
         } else {
            int adjustedStartPos = (int)startPos - 1;
            int adjustedEndIndex = adjustedStartPos + length;
            if (this.charData != null) {
               if (adjustedEndIndex > this.charData.length()) {
                  throw SQLError.createSQLException(Messages.getString("Clob.7"), "S1009", this.exceptionInterceptor);
               } else {
                  return this.charData.substring(adjustedStartPos, adjustedEndIndex);
               }
            } else {
               return null;
            }
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.exceptionInterceptor);
      }
   }

   public long length() throws SQLException {
      try {
         return this.charData != null ? (long)this.charData.length() : 0L;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public long position(java.sql.Clob arg0, long arg1) throws SQLException {
      try {
         return this.position(arg0.getSubString(1L, (int)arg0.length()), arg1);
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public long position(String stringToFind, long startPos) throws SQLException {
      try {
         if (startPos < 1L) {
            throw SQLError.createSQLException(Messages.getString("Clob.8", new Object[]{startPos}), "S1009", this.exceptionInterceptor);
         } else if (this.charData != null) {
            if (startPos - 1L > (long)this.charData.length()) {
               throw SQLError.createSQLException(Messages.getString("Clob.10"), "S1009", this.exceptionInterceptor);
            } else {
               int pos = this.charData.indexOf(stringToFind, (int)(startPos - 1L));
               return pos == -1 ? -1L : (long)(pos + 1);
            }
         } else {
            return -1L;
         }
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.exceptionInterceptor);
      }
   }

   public OutputStream setAsciiStream(long indexToWriteAt) throws SQLException {
      try {
         if (indexToWriteAt < 1L) {
            throw SQLError.createSQLException(Messages.getString("Clob.0"), "S1009", this.exceptionInterceptor);
         } else {
            WatchableOutputStream bytesOut = new WatchableOutputStream();
            bytesOut.setWatcher(this);
            if (indexToWriteAt > 0L) {
               bytesOut.write(StringUtils.getBytes(this.charData), 0, (int)(indexToWriteAt - 1L));
            }

            return bytesOut;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public Writer setCharacterStream(long indexToWriteAt) throws SQLException {
      try {
         if (indexToWriteAt < 1L) {
            throw SQLError.createSQLException(Messages.getString("Clob.1"), "S1009", this.exceptionInterceptor);
         } else {
            WatchableWriter writer = new WatchableWriter();
            writer.setWatcher(this);
            if (indexToWriteAt > 1L) {
               writer.write(this.charData, 0, (int)(indexToWriteAt - 1L));
            }

            return writer;
         }
      } catch (CJException var5) {
         throw SQLExceptionsMapping.translateException(var5, this.exceptionInterceptor);
      }
   }

   public int setString(long pos, String str) throws SQLException {
      try {
         if (pos < 1L) {
            throw SQLError.createSQLException(Messages.getString("Clob.2"), "S1009", this.exceptionInterceptor);
         } else if (str == null) {
            throw SQLError.createSQLException(Messages.getString("Clob.3"), "S1009", this.exceptionInterceptor);
         } else {
            StringBuilder charBuf = new StringBuilder(this.charData);
            --pos;
            int strLength = str.length();
            charBuf.replace((int)pos, (int)(pos + (long)strLength), str);
            this.charData = charBuf.toString();
            return strLength;
         }
      } catch (CJException var7) {
         throw SQLExceptionsMapping.translateException(var7, this.exceptionInterceptor);
      }
   }

   public int setString(long pos, String str, int offset, int len) throws SQLException {
      try {
         if (pos < 1L) {
            throw SQLError.createSQLException(Messages.getString("Clob.4"), "S1009", this.exceptionInterceptor);
         } else if (str == null) {
            throw SQLError.createSQLException(Messages.getString("Clob.5"), "S1009", this.exceptionInterceptor);
         } else {
            StringBuilder charBuf = new StringBuilder(this.charData);
            --pos;

            try {
               String replaceString = str.substring(offset, offset + len);
               charBuf.replace((int)pos, (int)(pos + (long)replaceString.length()), replaceString);
            } catch (StringIndexOutOfBoundsException var9) {
               throw SQLError.createSQLException(var9.getMessage(), "S1009", var9, this.exceptionInterceptor);
            }

            this.charData = charBuf.toString();
            return len;
         }
      } catch (CJException var10) {
         throw SQLExceptionsMapping.translateException(var10, this.exceptionInterceptor);
      }
   }

   public void streamClosed(WatchableStream out) {
      int streamSize = out.size();
      if (streamSize < this.charData.length()) {
         out.write(StringUtils.getBytes(this.charData), streamSize, this.charData.length() - streamSize);
      }

      this.charData = StringUtils.toAsciiString(out.toByteArray());
   }

   public void truncate(long length) throws SQLException {
      try {
         if (length > (long)this.charData.length()) {
            throw SQLError.createSQLException(Messages.getString("Clob.11") + this.charData.length() + Messages.getString("Clob.12") + length + Messages.getString("Clob.13"), this.exceptionInterceptor);
         } else {
            this.charData = this.charData.substring(0, (int)length);
         }
      } catch (CJException var4) {
         throw SQLExceptionsMapping.translateException(var4, this.exceptionInterceptor);
      }
   }

   public void writerClosed(char[] charDataBeingWritten) {
      this.charData = new String(charDataBeingWritten);
   }

   public void writerClosed(WatchableWriter out) {
      int dataLength = out.size();
      if (dataLength < this.charData.length()) {
         out.write(this.charData, dataLength, this.charData.length() - dataLength);
      }

      this.charData = out.toString();
   }

   public void free() throws SQLException {
      try {
         this.charData = null;
      } catch (CJException var2) {
         throw SQLExceptionsMapping.translateException(var2, this.exceptionInterceptor);
      }
   }

   public Reader getCharacterStream(long pos, long length) throws SQLException {
      try {
         return new StringReader(this.getSubString(pos, (int)length));
      } catch (CJException var6) {
         throw SQLExceptionsMapping.translateException(var6, this.exceptionInterceptor);
      }
   }
}
