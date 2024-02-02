package org.h2.value;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.h2.engine.CastDataProvider;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.store.DataHandler;
import org.h2.store.LobStorageInterface;
import org.h2.store.RangeInputStream;
import org.h2.store.RangeReader;
import org.h2.store.fs.FileUtils;
import org.h2.util.IOUtils;
import org.h2.util.MathUtils;
import org.h2.util.StringUtils;
import org.h2.util.Utils;
import org.h2.value.lob.LobData;
import org.h2.value.lob.LobDataDatabase;
import org.h2.value.lob.LobDataInMemory;

public abstract class ValueLob extends Value {
   static final int BLOCK_COMPARISON_SIZE = 512;
   private TypeInfo type;
   final LobData lobData;
   long octetLength;
   long charLength;
   private int hash;

   private static void rangeCheckUnknown(long var0, long var2) {
      if (var0 < 0L) {
         throw DbException.getInvalidValueException("offset", var0 + 1L);
      } else if (var2 < 0L) {
         throw DbException.getInvalidValueException("length", var2);
      }
   }

   protected static InputStream rangeInputStream(InputStream var0, long var1, long var3, long var5) {
      if (var5 > 0L) {
         rangeCheck(var1 - 1L, var3, var5);
      } else {
         rangeCheckUnknown(var1 - 1L, var3);
      }

      try {
         return new RangeInputStream(var0, var1 - 1L, var3);
      } catch (IOException var8) {
         throw DbException.getInvalidValueException("offset", var1);
      }
   }

   static Reader rangeReader(Reader var0, long var1, long var3, long var5) {
      if (var5 > 0L) {
         rangeCheck(var1 - 1L, var3, var5);
      } else {
         rangeCheckUnknown(var1 - 1L, var3);
      }

      try {
         return new RangeReader(var0, var1 - 1L, var3);
      } catch (IOException var8) {
         throw DbException.getInvalidValueException("offset", var1);
      }
   }

   ValueLob(LobData var1, long var2, long var4) {
      this.lobData = var1;
      this.octetLength = var2;
      this.charLength = var4;
   }

   static String createTempLobFileName(DataHandler var0) throws IOException {
      String var1 = var0.getDatabasePath();
      if (var1.isEmpty()) {
         var1 = SysProperties.PREFIX_TEMP_FILE;
      }

      return FileUtils.createTempFile(var1, ".temp.db", true);
   }

   static int getBufferSize(DataHandler var0, long var1) {
      if (var1 < 0L || var1 > 2147483647L) {
         var1 = 2147483647L;
      }

      int var3 = var0.getMaxLengthInplaceLob();
      long var4 = 4096L;
      if (var4 < var1 && var4 <= (long)var3) {
         var4 = Math.min(var1, (long)var3 + 1L);
         var4 = MathUtils.roundUpLong(var4, 4096L);
      }

      var4 = Math.min(var1, var4);
      var4 = (long)MathUtils.convertLongToInt(var4);
      if (var4 < 0L) {
         var4 = 2147483647L;
      }

      return (int)var4;
   }

   public boolean isLinkedToTable() {
      return this.lobData.isLinkedToTable();
   }

   public void remove() {
      this.lobData.remove(this);
   }

   public abstract ValueLob copy(DataHandler var1, int var2);

   public TypeInfo getType() {
      TypeInfo var1 = this.type;
      if (var1 == null) {
         int var2 = this.getValueType();
         this.type = var1 = new TypeInfo(var2, var2 == 3 ? this.charLength : this.octetLength, 0, (ExtTypeInfo)null);
      }

      return var1;
   }

   DbException getStringTooLong(long var1) {
      return DbException.getValueTooLongException("CHARACTER VARYING", this.readString(81), var1);
   }

   String readString(int var1) {
      try {
         return IOUtils.readStringAndClose(this.getReader(), var1);
      } catch (IOException var3) {
         throw DbException.convertIOException(var3, this.toString());
      }
   }

   public Reader getReader() {
      return IOUtils.getReader(this.getInputStream());
   }

   public byte[] getBytes() {
      return this.lobData instanceof LobDataInMemory ? Utils.cloneByteArray(this.getSmall()) : this.getBytesInternal();
   }

   public byte[] getBytesNoCopy() {
      return this.lobData instanceof LobDataInMemory ? this.getSmall() : this.getBytesInternal();
   }

   private byte[] getSmall() {
      byte[] var1 = ((LobDataInMemory)this.lobData).getSmall();
      int var2 = var1.length;
      if (var2 > 1048576) {
         throw DbException.getValueTooLongException("BINARY VARYING", StringUtils.convertBytesToHex(var1, 41), (long)var2);
      } else {
         return var1;
      }
   }

   abstract byte[] getBytesInternal();

   DbException getBinaryTooLong(long var1) {
      return DbException.getValueTooLongException("BINARY VARYING", StringUtils.convertBytesToHex(this.readBytes(41)), var1);
   }

   byte[] readBytes(int var1) {
      try {
         return IOUtils.readBytesAndClose(this.getInputStream(), var1);
      } catch (IOException var3) {
         throw DbException.convertIOException(var3, this.toString());
      }
   }

   public int hashCode() {
      if (this.hash == 0) {
         int var1 = this.getValueType();
         long var2 = var1 == 3 ? this.charLength : this.octetLength;
         if (var2 > 4096L) {
            return (int)(var2 ^ var2 >>> 32);
         }

         this.hash = Utils.getByteArrayHash(this.getBytesNoCopy());
      }

      return this.hash;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ValueLob)) {
         return false;
      } else {
         ValueLob var2 = (ValueLob)var1;
         if (this.hashCode() != var2.hashCode()) {
            return false;
         } else {
            return this.compareTypeSafe((Value)var1, (CompareMode)null, (CastDataProvider)null) == 0;
         }
      }
   }

   public int getMemory() {
      return this.lobData.getMemory();
   }

   public LobData getLobData() {
      return this.lobData;
   }

   public ValueLob copyToResult() {
      if (this.lobData instanceof LobDataDatabase) {
         LobStorageInterface var1 = this.lobData.getDataHandler().getLobStorage();
         if (!var1.isReadOnly()) {
            return var1.copyLob(this, -3);
         }
      }

      return this;
   }
}
