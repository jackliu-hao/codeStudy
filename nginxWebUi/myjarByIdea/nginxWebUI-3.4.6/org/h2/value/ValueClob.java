package org.h2.value;

import java.io.BufferedReader;
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
import org.h2.store.RangeReader;
import org.h2.util.Bits;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataFetchOnDemand;
import org.h2.value.lob.LobDataFile;
import org.h2.value.lob.LobDataInMemory;

public final class ValueClob extends ValueLob {
   public static ValueClob createSmall(byte[] var0) {
      return new ValueClob(new LobDataInMemory(var0), (long)var0.length, (long)(new String(var0, StandardCharsets.UTF_8)).length());
   }

   public static ValueClob createSmall(byte[] var0, long var1) {
      return new ValueClob(new LobDataInMemory(var0), (long)var0.length, var1);
   }

   public static ValueClob createSmall(String var0) {
      byte[] var1 = var0.getBytes(StandardCharsets.UTF_8);
      return new ValueClob(new LobDataInMemory(var1), (long)var1.length, (long)var0.length());
   }

   public static ValueClob createTempClob(Reader var0, long var1, DataHandler var3) {
      if (var1 >= 0L) {
         try {
            var0 = new RangeReader((Reader)var0, 0L, var1);
         } catch (IOException var11) {
            throw DbException.convert(var11);
         }
      }

      BufferedReader var4;
      if (var0 instanceof BufferedReader) {
         var4 = (BufferedReader)var0;
      } else {
         var4 = new BufferedReader((Reader)var0, 4096);
      }

      try {
         long var5 = Long.MAX_VALUE;
         if (var1 >= 0L && var1 < var5) {
            var5 = var1;
         }

         int var7 = ValueLob.getBufferSize(var3, var5);
         char[] var8;
         if (var7 >= Integer.MAX_VALUE) {
            String var9 = IOUtils.readStringAndClose(var4, -1);
            var8 = var9.toCharArray();
            var7 = var8.length;
         } else {
            var8 = new char[var7];
            var4.mark(var7);
            var7 = IOUtils.readFully((Reader)var4, (char[])var8, var7);
         }

         if (var7 <= var3.getMaxLengthInplaceLob()) {
            return createSmall(new String(var8, 0, var7));
         } else {
            var4.reset();
            return createTemporary(var3, var4, var5);
         }
      } catch (IOException var10) {
         throw DbException.convertIOException(var10, (String)null);
      }
   }

   private static ValueClob createTemporary(DataHandler var0, Reader var1, long var2) throws IOException {
      String var4 = ValueLob.createTempLobFileName(var0);
      FileStore var5 = var0.openFile(var4, "rw", false);
      var5.autoDelete();
      long var6 = 0L;
      long var8 = 0L;
      FileStoreOutputStream var10 = new FileStoreOutputStream(var5, (String)null);
      Throwable var11 = null;

      try {
         char[] var12 = new char[4096];

         while(true) {
            int var13 = ValueLob.getBufferSize(var0, var2);
            var13 = IOUtils.readFully(var1, var12, var13);
            if (var13 == 0) {
               return new ValueClob(new LobDataFile(var0, var4, var5), var6, var8);
            }

            byte[] var14 = (new String(var12, 0, var13)).getBytes(StandardCharsets.UTF_8);
            var10.write(var14);
            var6 += (long)var14.length;
            var8 += (long)var13;
         }
      } catch (Throwable var22) {
         var11 = var22;
         throw var22;
      } finally {
         if (var10 != null) {
            if (var11 != null) {
               try {
                  var10.close();
               } catch (Throwable var21) {
                  var11.addSuppressed(var21);
               }
            } else {
               var10.close();
            }
         }

      }
   }

   public ValueClob(LobData var1, long var2, long var4) {
      super(var1, var2, var4);
   }

   public int getValueType() {
      return 3;
   }

   public String getString() {
      if (this.charLength > 1048576L) {
         throw this.getStringTooLong(this.charLength);
      } else {
         return this.lobData instanceof LobDataInMemory ? new String(((LobDataInMemory)this.lobData).getSmall(), StandardCharsets.UTF_8) : this.readString((int)this.charLength);
      }
   }

   byte[] getBytesInternal() {
      long var1 = this.octetLength;
      if (var1 >= 0L) {
         if (var1 > 1048576L) {
            throw this.getBinaryTooLong(var1);
         } else {
            return this.readBytes((int)var1);
         }
      } else if (this.octetLength > 1048576L) {
         throw this.getBinaryTooLong(this.octetLength());
      } else {
         byte[] var3 = this.readBytes(Integer.MAX_VALUE);
         this.octetLength = var1 = (long)var3.length;
         if (var1 > 1048576L) {
            throw this.getBinaryTooLong(var1);
         } else {
            return var3;
         }
      }
   }

   public InputStream getInputStream() {
      return this.lobData.getInputStream(-1L);
   }

   public InputStream getInputStream(long var1, long var3) {
      return rangeInputStream(this.lobData.getInputStream(-1L), var1, var3, -1L);
   }

   public Reader getReader(long var1, long var3) {
      return rangeReader(this.getReader(), var1, var3, this.charLength);
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      if (var1 == this) {
         return 0;
      } else {
         ValueClob var4 = (ValueClob)var1;
         LobData var5 = this.lobData;
         LobData var6 = var4.lobData;
         if (var5.getClass() == var6.getClass()) {
            if (var5 instanceof LobDataInMemory) {
               return Integer.signum(this.getString().compareTo(var4.getString()));
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

   private static int compare(ValueClob var0, ValueClob var1) {
      long var2 = Math.min(var0.charLength, var1.charLength);

      try {
         Reader var4 = var0.getReader();
         Throwable var5 = null;

         try {
            Reader var6 = var1.getReader();
            Throwable var7 = null;

            try {
               char[] var8 = new char[512];
               char[] var9 = new char[512];

               while(true) {
                  int var10;
                  int var11;
                  if (var2 >= 512L) {
                     if (IOUtils.readFully((Reader)var4, (char[])var8, 512) == 512 && IOUtils.readFully((Reader)var6, (char[])var9, 512) == 512) {
                        var10 = Bits.compareNotNull(var8, var9);
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

                  var12 = var10 < var11 ? -1 : 1;
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
      if ((var2 & 2) == 0 || this.lobData instanceof LobDataInMemory && this.charLength <= SysProperties.MAX_TRACE_DATA_LENGTH) {
         if ((var2 & 6) == 0) {
            StringUtils.quoteStringSQL(var1.append("CAST("), this.getString()).append(" AS CHARACTER LARGE OBJECT(").append(this.charLength).append("))");
         } else {
            StringUtils.quoteStringSQL(var1, this.getString());
         }
      } else {
         var1.append("SPACE(").append(this.charLength);
         LobDataDatabase var3 = (LobDataDatabase)this.lobData;
         var1.append(" /* table: ").append(var3.getTableId()).append(" id: ").append(var3.getLobId()).append(" */)");
      }

      return var1;
   }

   ValueClob convertPrecision(long var1) {
      if (this.charLength <= var1) {
         return this;
      } else {
         DataHandler var4 = this.lobData.getDataHandler();
         ValueClob var3;
         if (var4 != null) {
            var3 = createTempClob(this.getReader(), var1, var4);
         } else {
            try {
               var3 = createSmall(IOUtils.readStringAndClose(this.getReader(), MathUtils.convertLongToInt(var1)));
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
            ValueClob var5 = var4.createClob(this.getReader(), this.charLength);
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
      return this.charLength;
   }

   public long octetLength() {
      long var1 = this.octetLength;
      if (var1 < 0L) {
         if (this.lobData instanceof LobDataInMemory) {
            var1 = (long)((LobDataInMemory)this.lobData).getSmall().length;
         } else {
            try {
               InputStream var3 = this.getInputStream();
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

         this.octetLength = var1;
      }

      return var1;
   }
}
