package org.h2.mvstore.type;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;

public final class MetaType<D> extends BasicDataType<DataType<?>> {
   private final D database;
   private final Thread.UncaughtExceptionHandler exceptionHandler;
   private final Map<String, Object> cache = new HashMap();

   public MetaType(D var1, Thread.UncaughtExceptionHandler var2) {
      this.database = var1;
      this.exceptionHandler = var2;
   }

   public int compare(DataType<?> var1, DataType<?> var2) {
      throw new UnsupportedOperationException();
   }

   public int getMemory(DataType<?> var1) {
      return 24;
   }

   public void write(WriteBuffer var1, DataType<?> var2) {
      Class var3 = var2.getClass();
      StatefulDataType var4 = null;
      if (var2 instanceof StatefulDataType) {
         var4 = (StatefulDataType)var2;
         StatefulDataType.Factory var5 = var4.getFactory();
         if (var5 != null) {
            var3 = var5.getClass();
         }
      }

      String var7 = var3.getName();
      int var6 = var7.length();
      var1.putVarInt(var6).putStringData(var7, var6);
      if (var4 != null) {
         var4.save(var1, this);
      }

   }

   public DataType<?> read(ByteBuffer var1) {
      int var2 = DataUtils.readVarInt(var1);
      String var3 = DataUtils.readString(var1, var2);

      try {
         Object var4 = this.cache.get(var3);
         if (var4 != null) {
            return var4 instanceof StatefulDataType.Factory ? ((StatefulDataType.Factory)var4).create(var1, this, this.database) : (DataType)var4;
         } else {
            Class var5 = Class.forName(var3);
            boolean var6 = false;

            Object var7;
            try {
               var7 = var5.getDeclaredField("INSTANCE").get((Object)null);
               var6 = true;
            } catch (NullPointerException | ReflectiveOperationException var9) {
               var7 = var5.getDeclaredConstructor().newInstance();
            }

            if (var7 instanceof StatefulDataType.Factory) {
               StatefulDataType.Factory var8 = (StatefulDataType.Factory)var7;
               this.cache.put(var3, var8);
               return var8.create(var1, this, this.database);
            } else {
               if (var6) {
                  this.cache.put(var3, var7);
               }

               return (DataType)var7;
            }
         }
      } catch (SecurityException | IllegalArgumentException | ReflectiveOperationException var10) {
         if (this.exceptionHandler != null) {
            this.exceptionHandler.uncaughtException(Thread.currentThread(), var10);
         }

         throw new RuntimeException(var10);
      }
   }

   public DataType<?>[] createStorage(int var1) {
      return new DataType[var1];
   }
}
