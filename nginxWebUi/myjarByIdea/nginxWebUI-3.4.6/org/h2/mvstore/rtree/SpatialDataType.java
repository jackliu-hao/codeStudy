package org.h2.mvstore.rtree;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import org.h2.mvstore.DataUtils;
import org.h2.mvstore.WriteBuffer;
import org.h2.mvstore.type.BasicDataType;

public class SpatialDataType extends BasicDataType<Spatial> {
   private final int dimensions;

   public SpatialDataType(int var1) {
      DataUtils.checkArgument(var1 >= 1 && var1 < 32, "Dimensions must be between 1 and 31, is {0}", var1);
      this.dimensions = var1;
   }

   protected Spatial create(long var1, float... var3) {
      return new DefaultSpatial(var1, var3);
   }

   public Spatial[] createStorage(int var1) {
      return new Spatial[var1];
   }

   public int compare(Spatial var1, Spatial var2) {
      if (var1 == var2) {
         return 0;
      } else if (var1 == null) {
         return -1;
      } else if (var2 == null) {
         return 1;
      } else {
         long var3 = var1.getId();
         long var5 = var2.getId();
         return Long.compare(var3, var5);
      }
   }

   public boolean equals(Object var1, Object var2) {
      if (var1 == var2) {
         return true;
      } else if (var1 != null && var2 != null) {
         long var3 = ((Spatial)var1).getId();
         long var5 = ((Spatial)var2).getId();
         return var3 == var5;
      } else {
         return false;
      }
   }

   public int getMemory(Spatial var1) {
      return 40 + this.dimensions * 4;
   }

   public void write(WriteBuffer var1, Spatial var2) {
      if (var2.isNull()) {
         var1.putVarInt(-1);
         var1.putVarLong(var2.getId());
      } else {
         int var3 = 0;

         int var4;
         for(var4 = 0; var4 < this.dimensions; ++var4) {
            if (var2.min(var4) == var2.max(var4)) {
               var3 |= 1 << var4;
            }
         }

         var1.putVarInt(var3);

         for(var4 = 0; var4 < this.dimensions; ++var4) {
            var1.putFloat(var2.min(var4));
            if ((var3 & 1 << var4) == 0) {
               var1.putFloat(var2.max(var4));
            }
         }

         var1.putVarLong(var2.getId());
      }
   }

   public Spatial read(ByteBuffer var1) {
      int var2 = DataUtils.readVarInt(var1);
      if (var2 == -1) {
         long var7 = DataUtils.readVarLong(var1);
         return this.create(var7);
      } else {
         float[] var3 = new float[this.dimensions * 2];

         for(int var4 = 0; var4 < this.dimensions; ++var4) {
            float var5 = var1.getFloat();
            float var6;
            if ((var2 & 1 << var4) != 0) {
               var6 = var5;
            } else {
               var6 = var1.getFloat();
            }

            var3[var4 + var4] = var5;
            var3[var4 + var4 + 1] = var6;
         }

         long var8 = DataUtils.readVarLong(var1);
         return this.create(var8, var3);
      }
   }

   public boolean isOverlap(Spatial var1, Spatial var2) {
      if (!var1.isNull() && !var2.isNull()) {
         for(int var3 = 0; var3 < this.dimensions; ++var3) {
            if (var1.max(var3) < var2.min(var3) || var1.min(var3) > var2.max(var3)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public void increaseBounds(Object var1, Object var2) {
      Spatial var3 = (Spatial)var2;
      Spatial var4 = (Spatial)var1;
      if (!var3.isNull() && !var4.isNull()) {
         for(int var5 = 0; var5 < this.dimensions; ++var5) {
            float var6 = var3.min(var5);
            if (var6 < var4.min(var5)) {
               var4.setMin(var5, var6);
            }

            var6 = var3.max(var5);
            if (var6 > var4.max(var5)) {
               var4.setMax(var5, var6);
            }
         }

      }
   }

   public float getAreaIncrease(Object var1, Object var2) {
      Spatial var3 = (Spatial)var2;
      Spatial var4 = (Spatial)var1;
      if (!var4.isNull() && !var3.isNull()) {
         float var5 = var4.min(0);
         float var6 = var4.max(0);
         float var7 = var6 - var5;
         var5 = Math.min(var5, var3.min(0));
         var6 = Math.max(var6, var3.max(0));
         float var8 = var6 - var5;

         for(int var9 = 1; var9 < this.dimensions; ++var9) {
            var5 = var4.min(var9);
            var6 = var4.max(var9);
            var7 *= var6 - var5;
            var5 = Math.min(var5, var3.min(var9));
            var6 = Math.max(var6, var3.max(var9));
            var8 *= var6 - var5;
         }

         return var8 - var7;
      } else {
         return 0.0F;
      }
   }

   float getCombinedArea(Object var1, Object var2) {
      Spatial var3 = (Spatial)var1;
      Spatial var4 = (Spatial)var2;
      if (var3.isNull()) {
         return this.getArea(var4);
      } else if (var4.isNull()) {
         return this.getArea(var3);
      } else {
         float var5 = 1.0F;

         for(int var6 = 0; var6 < this.dimensions; ++var6) {
            float var7 = Math.min(var3.min(var6), var4.min(var6));
            float var8 = Math.max(var3.max(var6), var4.max(var6));
            var5 *= var8 - var7;
         }

         return var5;
      }
   }

   private float getArea(Spatial var1) {
      if (var1.isNull()) {
         return 0.0F;
      } else {
         float var2 = 1.0F;

         for(int var3 = 0; var3 < this.dimensions; ++var3) {
            var2 *= var1.max(var3) - var1.min(var3);
         }

         return var2;
      }
   }

   public boolean contains(Object var1, Object var2) {
      Spatial var3 = (Spatial)var1;
      Spatial var4 = (Spatial)var2;
      if (!var3.isNull() && !var4.isNull()) {
         for(int var5 = 0; var5 < this.dimensions; ++var5) {
            if (var3.min(var5) > var4.min(var5) || var3.max(var5) < var4.max(var5)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean isInside(Object var1, Object var2) {
      Spatial var3 = (Spatial)var1;
      Spatial var4 = (Spatial)var2;
      if (!var3.isNull() && !var4.isNull()) {
         for(int var5 = 0; var5 < this.dimensions; ++var5) {
            if (var3.min(var5) <= var4.min(var5) || var3.max(var5) >= var4.max(var5)) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   Spatial createBoundingBox(Object var1) {
      Spatial var2 = (Spatial)var1;
      return var2.isNull() ? var2 : var2.clone(0L);
   }

   public int[] getExtremes(ArrayList<Object> var1) {
      var1 = getNotNull(var1);
      if (var1.isEmpty()) {
         return null;
      } else {
         Spatial var2 = this.createBoundingBox(var1.get(0));
         Spatial var3 = this.createBoundingBox(var2);

         for(int var4 = 0; var4 < this.dimensions; ++var4) {
            float var5 = var3.min(var4);
            var3.setMin(var4, var3.max(var4));
            var3.setMax(var4, var5);
         }

         Iterator var13 = var1.iterator();

         while(var13.hasNext()) {
            Object var15 = var13.next();
            this.increaseBounds(var2, var15);
            this.increaseMaxInnerBounds(var3, var15);
         }

         double var14 = 0.0;
         int var6 = 0;

         float var8;
         for(int var7 = 0; var7 < this.dimensions; ++var7) {
            var8 = var3.max(var7) - var3.min(var7);
            if (!(var8 < 0.0F)) {
               float var9 = var2.max(var7) - var2.min(var7);
               float var10 = var8 / var9;
               if ((double)var10 > var14) {
                  var14 = (double)var10;
                  var6 = var7;
               }
            }
         }

         if (var14 <= 0.0) {
            return null;
         } else {
            float var16 = var3.min(var6);
            var8 = var3.max(var6);
            int var17 = -1;
            int var18 = -1;

            for(int var11 = 0; var11 < var1.size() && (var17 < 0 || var18 < 0); ++var11) {
               Spatial var12 = (Spatial)var1.get(var11);
               if (var17 < 0 && var12.max(var6) == var16) {
                  var17 = var11;
               } else if (var18 < 0 && var12.min(var6) == var8) {
                  var18 = var11;
               }
            }

            return new int[]{var17, var18};
         }
      }
   }

   private static ArrayList<Object> getNotNull(ArrayList<Object> var0) {
      boolean var1 = false;
      Iterator var2 = var0.iterator();

      while(var2.hasNext()) {
         Object var3 = var2.next();
         Spatial var4 = (Spatial)var3;
         if (var4.isNull()) {
            var1 = true;
            break;
         }
      }

      if (!var1) {
         return var0;
      } else {
         ArrayList var6 = new ArrayList();
         Iterator var7 = var0.iterator();

         while(var7.hasNext()) {
            Object var8 = var7.next();
            Spatial var5 = (Spatial)var8;
            if (!var5.isNull()) {
               var6.add(var5);
            }
         }

         return var6;
      }
   }

   private void increaseMaxInnerBounds(Object var1, Object var2) {
      Spatial var3 = (Spatial)var1;
      Spatial var4 = (Spatial)var2;

      for(int var5 = 0; var5 < this.dimensions; ++var5) {
         var3.setMin(var5, Math.min(var3.min(var5), var4.max(var5)));
         var3.setMax(var5, Math.max(var3.max(var5), var4.min(var5)));
      }

   }
}
