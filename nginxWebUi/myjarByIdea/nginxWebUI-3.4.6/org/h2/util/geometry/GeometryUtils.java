package org.h2.util.geometry;

public final class GeometryUtils {
   public static final int POINT = 1;
   public static final int LINE_STRING = 2;
   public static final int POLYGON = 3;
   public static final int MULTI_POINT = 4;
   public static final int MULTI_LINE_STRING = 5;
   public static final int MULTI_POLYGON = 6;
   public static final int GEOMETRY_COLLECTION = 7;
   public static final int X = 0;
   public static final int Y = 1;
   public static final int Z = 2;
   public static final int M = 3;
   public static final int DIMENSION_SYSTEM_XY = 0;
   public static final int DIMENSION_SYSTEM_XYZ = 1;
   public static final int DIMENSION_SYSTEM_XYM = 2;
   public static final int DIMENSION_SYSTEM_XYZM = 3;
   public static final int MIN_X = 0;
   public static final int MAX_X = 1;
   public static final int MIN_Y = 2;
   public static final int MAX_Y = 3;

   public static double[] getEnvelope(byte[] var0) {
      EnvelopeTarget var1 = new EnvelopeTarget();
      EWKBUtils.parseEWKB(var0, var1);
      return var1.getEnvelope();
   }

   public static boolean intersects(double[] var0, double[] var1) {
      return var0 != null && var1 != null && var0[1] >= var1[0] && var0[0] <= var1[1] && var0[3] >= var1[2] && var0[2] <= var1[3];
   }

   public static double[] union(double[] var0, double[] var1) {
      if (var0 == null) {
         return var1;
      } else if (var1 == null) {
         return var0;
      } else {
         double var2 = var0[0];
         double var4 = var0[1];
         double var6 = var0[2];
         double var8 = var0[3];
         double var10 = var1[0];
         double var12 = var1[1];
         double var14 = var1[2];
         double var16 = var1[3];
         boolean var18 = false;
         if (var2 > var10) {
            var2 = var10;
            var18 = true;
         }

         if (var4 < var12) {
            var4 = var12;
            var18 = true;
         }

         if (var6 > var14) {
            var6 = var14;
            var18 = true;
         }

         if (var8 < var16) {
            var8 = var16;
            var18 = true;
         }

         return var18 ? new double[]{var2, var4, var6, var8} : var0;
      }
   }

   static double toCanonicalDouble(double var0) {
      return Double.isNaN(var0) ? Double.NaN : (var0 == 0.0 ? 0.0 : var0);
   }

   static double checkFinite(double var0) {
      if (!Double.isFinite(var0)) {
         throw new IllegalArgumentException();
      } else {
         return var0;
      }
   }

   private GeometryUtils() {
   }

   public static final class DimensionSystemTarget extends Target {
      private boolean hasZ;
      private boolean hasM;

      protected void dimensionSystem(int var1) {
         if ((var1 & 1) != 0) {
            this.hasZ = true;
         }

         if ((var1 & 2) != 0) {
            this.hasM = true;
         }

      }

      protected void addCoordinate(double var1, double var3, double var5, double var7, int var9, int var10) {
         if (!this.hasZ && !Double.isNaN(var5)) {
            this.hasZ = true;
         }

         if (!this.hasM && !Double.isNaN(var7)) {
            this.hasM = true;
         }

      }

      public int getDimensionSystem() {
         return (this.hasZ ? 1 : 0) | (this.hasM ? 2 : 0);
      }
   }

   public static final class EnvelopeTarget extends Target {
      private boolean enabled;
      private boolean set;
      private double minX;
      private double maxX;
      private double minY;
      private double maxY;

      protected void startPoint() {
         this.enabled = true;
      }

      protected void startLineString(int var1) {
         this.enabled = true;
      }

      protected void startPolygon(int var1, int var2) {
         this.enabled = true;
      }

      protected void startPolygonInner(int var1) {
         this.enabled = false;
      }

      protected void addCoordinate(double var1, double var3, double var5, double var7, int var9, int var10) {
         if (this.enabled && !Double.isNaN(var1) && !Double.isNaN(var3)) {
            if (!this.set) {
               this.minX = this.maxX = var1;
               this.minY = this.maxY = var3;
               this.set = true;
            } else {
               if (this.minX > var1) {
                  this.minX = var1;
               }

               if (this.maxX < var1) {
                  this.maxX = var1;
               }

               if (this.minY > var3) {
                  this.minY = var3;
               }

               if (this.maxY < var3) {
                  this.maxY = var3;
               }
            }
         }

      }

      public double[] getEnvelope() {
         return this.set ? new double[]{this.minX, this.maxX, this.minY, this.maxY} : null;
      }
   }

   public abstract static class Target {
      protected void init(int var1) {
      }

      protected void dimensionSystem(int var1) {
      }

      protected void startPoint() {
      }

      protected void startLineString(int var1) {
      }

      protected void startPolygon(int var1, int var2) {
      }

      protected void startPolygonInner(int var1) {
      }

      protected void endNonEmptyPolygon() {
      }

      protected void startCollection(int var1, int var2) {
      }

      protected Target startCollectionItem(int var1, int var2) {
         return this;
      }

      protected void endCollectionItem(Target var1, int var2, int var3, int var4) {
      }

      protected void endObject(int var1) {
      }

      protected abstract void addCoordinate(double var1, double var3, double var5, double var7, int var9, int var10);
   }
}
