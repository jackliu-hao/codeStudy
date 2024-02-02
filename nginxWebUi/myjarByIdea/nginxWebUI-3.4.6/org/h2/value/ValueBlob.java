package org.h2.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.DataHandler;
import org.h2.store.FileStore;
import org.h2.store.FileStoreOutputStream;
import org.h2.store.LobStorageInterface;
import org.h2.util.Bits;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataFetchOnDemand;
import org.h2.value.lob.LobDataFile;
import org.h2.value.lob.LobDataInMemory;

public final class ValueBlob extends ValueLob {
   public static ValueBlob createSmall(byte[] var0) {
      return new ValueBlob(new LobDataInMemory(var0), (long)var0.length);
   }

   public static ValueBlob createTempBlob(InputStream var0, long var1, DataHandler var3) {
      try {
         long var4 = Long.MAX_VALUE;
         if (var1 >= 0L && var1 < var4) {
            var4 = var1;
         }

         int var6 = ValueLob.getBufferSize(var3, var4);
         byte[] var7;
         if (var6 >= Integer.MAX_VALUE) {
            var7 = IOUtils.readBytesAndClose(var0, -1);
            var6 = var7.length;
         } else {
            var7 = Utils.newBytes(var6);
            var6 = IOUtils.readFully(var0, var7, var6);
         }

         return var6 <= var3.getMaxLengthInplaceLob() ? createSmall(Utils.copyBytes(var7, var6)) : createTemporary(var3, var7, var6, var0, var4);
      } catch (IOException var8) {
         throw DbException.convertIOException(var8, (String)null);
      }
   }

   private static ValueBlob createTemporary(DataHandler var0, byte[] var1, int var2, InputStream var3, long var4) throws IOException {
      String var6 = ValueLob.createTempLobFileName(var0);
      FileStore var7 = var0.openFile(var6, "rw", false);
      var7.autoDelete();
      long var8 = 0L;
      FileStoreOutputStream var10 = new FileStoreOutputStream(var7, (String)null);
      Throwable var11 = null;

      try {
         do {
            var8 += (long)var2;
            var10.write(var1, 0, var2);
            var4 -= (long)var2;
            if (var4 <= 0L) {
               break;
            }

            var2 = ValueLob.getBufferSize(var0, var4);
            var2 = IOUtils.readFully(var3, var1, var2);
         } while(var2 > 0);
      } catch (Throwable var20) {
         var11 = var20;
         throw var20;
      } finally {
         if (var10 != null) {
            if (var11 != null) {
               try {
                  var10.close();
               } catch (Throwable var19) {
                  var11.addSuppressed(var19);
               }
            } else {
               var10.close();
            }
         }

      }

      return new ValueBlob(new LobDataFile(var0, var6, var7), var8);
   }

   public ValueBlob(LobData var1, long var2) {
      super(var1, var2, -1L);
   }

   public int getValueType() {
      return 7;
   }

   public String getString() {
      long var1 = this.charLength;
      if (var1 >= 0L) {
         if (var1 > 1048576L) {
            throw this.getStringTooLong(var1);
         } else {
            return this.readString((int)var1);
         }
      } else if (this.octetLength > 3145728L) {
         throw this.getStringTooLong(this.charLength());
      } else {
         String var3;
         if (this.lobData instanceof LobDataInMemory) {
            var3 = new String(((LobDataInMemory)this.lobData).getSmall(), StandardCharsets.UTF_8);
         } else {
            var3 = this.readString(Integer.MAX_VALUE);
         }

         this.charLength = var1 = (long)var3.length();
         if (var1 > 1048576L) {
            throw this.getStringTooLong(var1);
         } else {
            return var3;
         }
      }
   }

   byte[] getBytesInternal() {
      if (this.octetLength > 1048576L) {
         throw this.getBinaryTooLong(this.octetLength);
      } else {
         return this.readBytes((int)this.octetLength);
      }
   }

   public InputStream getInputStream() {
      return this.lobData.getInputStream(this.octetLength);
   }

   public InputStream getInputStream(long var1, long var3) {
      long var5 = this.octetLength;
      return rangeInputStream(this.lobData.getInputStream(var5), var1, var3, var5);
   }

   public Reader getReader(long var1, long var3) {
      return rangeReader(this.getReader(), var1, var3, -1L);
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      if (var1 == this) {
         return 0;
      } else {
         ValueBlob var4 = (ValueBlob)var1;
         LobData var5 = this.lobData;
         LobData var6 = var4.lobData;
         if (var5.getClass() == var6.getClass()) {
            if (var5 instanceof LobDataInMemory) {
               return Bits.compareNotNullUnsigned(((LobDataInMemory)var5).getSmall(), ((LobDataInMemory)var6).getSmall());
            }

            if (var5 instanceof LobDataDatabase) {
               if (((LobDataDatabase)var5).getLobId() == ((LobDataDatabase)var6).getLobId()) {
                  return 0;
               }
            } else if (var5 instanceof LobDataFetchOnDemand && ((LobDataFetchOnDemand)var5).getLobId() == ((LobDataFetchOnDemand)var6).getLobId()) {
               return 0;
            }
         }

         return compare(this, var4);
      }
   }

   private static int compare(ValueBlob var0, ValueBlob var1) {
      long var2 = Math.min(var0.octetLength, var1.octetLength);

      try {
         InputStream var4 = var0.getInputStream();
         Throwable var5 = null;

         try {
            InputStream var6 = var1.getInputStream();
            Throwable var7 = null;

            try {
               byte[] var8 = new byte[512];
               byte[] var9 = new byte[512];

               while(true) {
                  int var10;
                  int var11;
                  if (var2 >= 512L) {
                     if (IOUtils.readFully((InputStream)var4, (byte[])var8, 512) == 512 && IOUtils.readFully((InputStream)var6, (byte[])var9, 512) == 512) {
                        var10 = Bits.compareNotNullUnsigned(var8, var9);
                        if (var10 != 0) {
                           var11 = var10;
                           return var11;
                        }

                        var2 -= 512L;
                        continue;
                     }

                     throw DbException.getUnsupportedException("Invalid LOB");
                  }

                  int var12;
                  do {
                     var10 = var4.read();
                     var11 = var6.read();
                     if (var10 < 0) {
                        var12 = var11 < 0 ? 0 : -1;
                        return var12;
                     }

                     if (var11 < 0) {
                        var12 = 1;
                        return var12;
                     }
                  } while(var10 == var11);

                  var12 = (var10 & 255) < (var11 & 255) ? -1 : 1;
                  return var12;
               }
            } catch (Throwable var46) {
               var7 = var46;
               throw var46;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var45) {
                        var7.addSuppressed(var45);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var48) {
            var5 = var48;
            throw var48;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var44) {
                     var5.addSuppressed(var44);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (IOException var50) {
         throw DbException.convert(var50);
      }
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      if ((var2 & 2) == 0 || this.lobData instanceof LobDataInMemory && this.octetLength <= SysProperties.MAX_TRACE_DATA_LENGTH) {
         if ((var2 & 6) == 0) {
            var1.append("CAST(X'");
            StringUtils.convertBytesToHex(var1, this.getBytesNoCopy()).append("' AS BINARY LARGE OBJECT(").append(this.octetLength).append("))");
         } else {
            var1.append("X'");
            StringUtils.convertBytesToHex(var1, this.getBytesNoCopy()).append('\'');
         }
      } else {
         var1.append("CAST(REPEAT(CHAR(0), ").append(this.octetLength).append(") AS BINARY VARYING");
         LobDataDatabase var3 = (LobDataDatabase)this.lobData;
         var1.append(" /* table: ").append(var3.getTableId()).append(" id: ").append(var3.getLobId()).append(" */)");
      }

      return var1;
   }

   ValueBlob convertPrecision(long var1) {
      if (this.octetLength <= var1) {
         return this;
      } else {
         DataHandler var4 = this.lobData.getDataHandler();
         ValueBlob var3;
         if (var4 != null) {
            var3 = createTempBlob(this.getInputStream(), var1, var4);
         } else {
            try {
               var3 = createSmall(IOUtils.readBytesAndClose(this.getInputStream(), MathUtils.convertLongToInt(var1)));
            } catch (IOException var6) {
               throw DbException.convertIOException(var6, (String)null);
            }
         }

         return var3;
      }
   }

   public ValueLob copy(DataHandler var1, int var2) {
      if (this.lobData instanceof LobDataInMemory) {
         byte[] var3 = ((LobDataInMemory)this.lobData).getSmall();
         if (var3.length > var1.getMaxLengthInplaceLob()) {
            LobStorageInterface var4 = var1.getLobStorage();
            ValueBlob var5 = var4.createBlob(this.getInputStream(), this.octetLength);
            ValueLob var6 = var5.copy(var1, var2);
            var5.remove();
            return var6;
         } else {
            return this;
         }
      } else if (this.lobData instanceof LobDataDatabase) {
         return var1.getLobStorage().copyLob(this, var2);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public long charLength() {
      long var1 = this.charLength;
      if (var1 < 0L) {
         if (this.lobData instanceof LobDataInMemory) {
            var1 = (long)(new String(((LobDataInMemory)this.lobData).getSmall(), StandardCharsets.UTF_8)).length();
         } else {
            try {
               Reader var3 = this.getReader();
               Throwable var4 = null;

               try {
                  var1 = 0L;

                  while(true) {
                     var1 += var3.skip(Long.MAX_VALUE);
                     if (var3.read() < 0) {
                        break;
                     }

                     ++var1;
                  }
               } catch (Throwable var14) {
                  var4 = var14;
                  throw var14;
               } finally {
                  if (var3 != null) {
                     if (var4 != null) {
                        try {
                           var3.close();
                        } catch (Throwable var13) {
                           var4.addSuppressed(var13);
                        }
                     } else {
                        var3.close();
                     }
                  }

               }
            } catch (IOException var16) {
               throw DbException.convertIOException(var16, (String)null);
            }
         }

         this.charLength = var1;
      }

      return var1;
   }

   public long octetLength() {
      return this.octetLength;
   }
}
