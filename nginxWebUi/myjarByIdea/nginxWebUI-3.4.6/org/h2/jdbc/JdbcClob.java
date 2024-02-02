package org.h2.jdbc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import org.h2.message.DbException;
import org.h2.store.RangeReader;
import org.h2.util.IOUtils;
import org.h2.value.Value;

public final class JdbcClob extends JdbcLob implements NClob {
   public JdbcClob(JdbcConnection var1, Value var2, JdbcLob.State var3, int var4) {
      super(var1, var2, var3, 10, var4);
   }

   public long length() throws SQLException {
      try {
         this.debugCodeCall("length");
         this.checkReadable();
         if (this.value.getValueType() == 3) {
            long var1 = this.value.getType().getPrecision();
            if (var1 > 0L) {
               return var1;
            }
         }

         return IOUtils.copyAndCloseInput(this.value.getReader(), (Writer)null, Long.MAX_VALUE);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void truncate(long var1) throws SQLException {
      throw this.unsupported("LOB update");
   }

   public InputStream getAsciiStream() throws SQLException {
      try {
         this.debugCodeCall("getAsciiStream");
         this.checkReadable();
         String var1 = this.value.getString();
         return IOUtils.getInputStreamFromString(var1);
      } catch (Exception var2) {
         throw this.logAndConvert(var2);
      }
   }

   public OutputStream setAsciiStream(long var1) throws SQLException {
      throw this.unsupported("LOB update");
   }

   public Reader getCharacterStream() throws SQLException {
      return super.getCharacterStream();
   }

   public Writer setCharacterStream(long var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCodeCall("setCharacterStream", var1);
         }

         this.checkEditable();
         if (var1 != 1L) {
            throw DbException.getInvalidValueException("pos", var1);
         } else {
            this.state = JdbcLob.State.SET_CALLED;
            return this.setCharacterStreamImpl();
         }
      } catch (Exception var4) {
         throw this.logAndConvert(var4);
      }
   }

   public String getSubString(long var1, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getSubString(" + var1 + ", " + var3 + ')');
         }

         this.checkReadable();
         if (var1 < 1L) {
            throw DbException.getInvalidValueException("pos", var1);
         } else if (var3 < 0) {
            throw DbException.getInvalidValueException("length", var3);
         } else {
            StringWriter var4 = new StringWriter(Math.min(4096, var3));
            Reader var5 = this.value.getReader();
            Throwable var6 = null;

            try {
               IOUtils.skipFully(var5, var1 - 1L);
               IOUtils.copyAndCloseInput(var5, var4, (long)var3);
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     var5.close();
                  }
               }

            }

            return var4.toString();
         }
      } catch (Exception var18) {
         throw this.logAndConvert(var18);
      }
   }

   public int setString(long var1, String var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setString(" + var1 + ", " + quote(var3) + ')');
         }

         this.checkEditable();
         if (var1 != 1L) {
            throw DbException.getInvalidValueException("pos", var1);
         } else if (var3 == null) {
            throw DbException.getInvalidValueException("str", var3);
         } else {
            this.completeWrite(this.conn.createClob(new StringReader(var3), -1L));
            return var3.length();
         }
      } catch (Exception var5) {
         throw this.logAndConvert(var5);
      }
   }

   public int setString(long var1, String var3, int var4, int var5) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("setString(" + var1 + ", " + quote(var3) + ", " + var4 + ", " + var5 + ')');
         }

         this.checkEditable();
         if (var1 != 1L) {
            throw DbException.getInvalidValueException("pos", var1);
         } else if (var3 == null) {
            throw DbException.getInvalidValueException("str", var3);
         } else {
            this.completeWrite(this.conn.createClob(new RangeReader(new StringReader(var3), (long)var4, (long)var5), -1L));
            return (int)this.value.getType().getPrecision();
         }
      } catch (Exception var7) {
         throw this.logAndConvert(var7);
      }
   }

   public long position(String var1, long var2) throws SQLException {
      throw this.unsupported("LOB search");
   }

   public long position(Clob var1, long var2) throws SQLException {
      throw this.unsupported("LOB search");
   }

   public Reader getCharacterStream(long var1, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getCharacterStream(" + var1 + ", " + var3 + ')');
         }

         this.checkReadable();
         if (this.state == JdbcLob.State.NEW) {
            if (var1 != 1L) {
               throw DbException.getInvalidValueException("pos", var1);
            }

            if (var3 != 0L) {
               throw DbException.getInvalidValueException("length", var1);
            }
         }

         return this.value.getReader(var1, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }
}
