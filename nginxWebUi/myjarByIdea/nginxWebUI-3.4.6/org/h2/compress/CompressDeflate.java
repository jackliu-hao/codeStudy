package org.h2.compress;

import java.util.StringTokenizer;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.h2.mvstore.DataUtils;

public class CompressDeflate implements Compressor {
   private int level = -1;
   private int strategy = 0;

   public void setOptions(String var1) {
      if (var1 != null) {
         try {
            StringTokenizer var2 = new StringTokenizer(var1);

            while(var2.hasMoreElements()) {
               String var3 = var2.nextToken();
               if (!"level".equals(var3) && !"l".equals(var3)) {
                  if ("strategy".equals(var3) || "s".equals(var3)) {
                     this.strategy = Integer.parseInt(var2.nextToken());
                  }
               } else {
                  this.level = Integer.parseInt(var2.nextToken());
               }

               Deflater var4 = new Deflater(this.level);
               var4.setStrategy(this.strategy);
            }

         } catch (Exception var5) {
            throw DataUtils.newMVStoreException(90102, var1);
         }
      }
   }

   public int compress(byte[] var1, int var2, int var3, byte[] var4, int var5) {
      Deflater var6 = new Deflater(this.level);
      var6.setStrategy(this.strategy);
      var6.setInput(var1, var2, var3);
      var6.finish();
      int var7 = var6.deflate(var4, var5, var4.length - var5);
      if (var7 == 0) {
         this.strategy = 0;
         this.level = -1;
         return this.compress(var1, var2, var3, var4, var5);
      } else {
         var6.end();
         return var5 + var7;
      }
   }

   public int getAlgorithm() {
      return 2;
   }

   public void expand(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) {
      Inflater var7 = new Inflater();
      var7.setInput(var1, var2, var3);
      var7.finished();

      try {
         int var8 = var7.inflate(var4, var5, var6);
         if (var8 != var6) {
            throw new DataFormatException(var8 + " " + var6);
         }
      } catch (DataFormatException var9) {
         throw DataUtils.newMVStoreException(90104, var9.getMessage(), var9);
      }

      var7.end();
   }
}
