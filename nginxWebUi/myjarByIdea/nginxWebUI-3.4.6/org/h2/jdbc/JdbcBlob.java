package org.h2.jdbc;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.sql.Blob;
import java.sql.SQLException;
import org.h2.message.DbException;
import org.h2.util.IOUtils;
import org.h2.util.Task;
import org.h2.value.Value;

public final class JdbcBlob extends JdbcLob implements Blob {
   public JdbcBlob(JdbcConnection var1, Value var2, JdbcLob.State var3, int var4) {
      super(var1, var2, var3, 9, var4);
   }

   public long length() throws SQLException {
      try {
         this.debugCodeCall("length");
         this.checkReadable();
         if (this.value.getValueType() == 7) {
            long var1 = this.value.getType().getPrecision();
            if (var1 > 0L) {
               return var1;
            }
         }

         return IOUtils.copyAndCloseInput(this.value.getInputStream(), (OutputStream)null);
      } catch (Exception var3) {
         throw this.logAndConvert(var3);
      }
   }

   public void truncate(long var1) throws SQLException {
      throw this.unsupported("LOB update");
   }

   public byte[] getBytes(long var1, int var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getBytes(" + var1 + ", " + var3 + ')');
         }

         this.checkReadable();
         ByteArrayOutputStream var4 = new ByteArrayOutputStream();
         InputStream var5 = this.value.getInputStream();
         Throwable var6 = null;

         try {
            IOUtils.skipFully(var5, var1 - 1L);
            IOUtils.copy(var5, var4, (long)var3);
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

         return var4.toByteArray();
      } catch (Exception var18) {
         throw this.logAndConvert(var18);
      }
   }

   public int setBytes(long var1, byte[] var3) throws SQLException {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         try {
            if (this.isDebugEnabled()) {
               this.debugCode("setBytes(" + var1 + ", " + quoteBytes(var3) + ')');
            }

            this.checkEditable();
            if (var1 != 1L) {
               throw DbException.getInvalidValueException("pos", var1);
            } else {
               this.completeWrite(this.conn.createBlob(new ByteArrayInputStream(var3), -1L));
               return var3.length;
            }
         } catch (Exception var5) {
            throw this.logAndConvert(var5);
         }
      }
   }

   public int setBytes(long var1, byte[] var3, int var4, int var5) throws SQLException {
      if (var3 == null) {
         throw new NullPointerException();
      } else {
         try {
            if (this.isDebugEnabled()) {
               this.debugCode("setBytes(" + var1 + ", " + quoteBytes(var3) + ", " + var4 + ", " + var5 + ')');
            }

            this.checkEditable();
            if (var1 != 1L) {
               throw DbException.getInvalidValueException("pos", var1);
            } else {
               this.completeWrite(this.conn.createBlob(new ByteArrayInputStream(var3, var4, var5), -1L));
               return (int)this.value.getType().getPrecision();
            }
         } catch (Exception var7) {
            throw this.logAndConvert(var7);
         }
      }
   }

   public InputStream getBinaryStream() throws SQLException {
      return super.getBinaryStream();
   }

   public OutputStream setBinaryStream(long var1) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCodeCall("setBinaryStream", var1);
         }

         this.checkEditable();
         if (var1 != 1L) {
            throw DbException.getInvalidValueException("pos", var1);
         } else {
            final PipedInputStream var3 = new PipedInputStream();
            Task var4 = new Task() {
               public void call() {
                  JdbcBlob.this.completeWrite(JdbcBlob.this.conn.createBlob(var3, -1L));
               }
            };
            JdbcLob.LobPipedOutputStream var5 = new JdbcLob.LobPipedOutputStream(var3, var4);
            var4.execute();
            this.state = JdbcLob.State.SET_CALLED;
            return new BufferedOutputStream(var5);
         }
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }

   public long position(byte[] var1, long var2) throws SQLException {
      if (this.isDebugEnabled()) {
         this.debugCode("position(" + quoteBytes(var1) + ", " + var2 + ')');
      }

      throw this.unsupported("LOB search");
   }

   public long position(Blob var1, long var2) throws SQLException {
      if (this.isDebugEnabled()) {
         this.debugCode("position(blobPattern, " + var2 + ')');
      }

      throw this.unsupported("LOB subset");
   }

   public InputStream getBinaryStream(long var1, long var3) throws SQLException {
      try {
         if (this.isDebugEnabled()) {
            this.debugCode("getBinaryStream(" + var1 + ", " + var3 + ')');
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

         return this.value.getInputStream(var1, var3);
      } catch (Exception var6) {
         throw this.logAndConvert(var6);
      }
   }
}
