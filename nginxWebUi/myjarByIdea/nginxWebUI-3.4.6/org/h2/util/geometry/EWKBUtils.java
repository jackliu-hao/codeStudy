package org.h2.util.geometry;

import java.io.ByteArrayOutputStream;
import org.h2.util.Bits;
import org.h2.util.StringUtils;

public final class EWKBUtils {
   public static final int EWKB_Z = Integer.MIN_VALUE;
   public static final int EWKB_M = 1073741824;
   public static final int EWKB_SRID = 536870912;

   public static byte[] ewkb2ewkb(byte[] var0) {
      return ewkb2ewkb(var0, getDimensionSystem(var0));
   }

   public static byte[] ewkb2ewkb(byte[] var0, int var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      EWKBTarget var3 = new EWKBTarget(var2, var1);
      parseEWKB(var0, var3);
      return var2.toByteArray();
   }

   public static void parseEWKB(byte[] var0, GeometryUtils.Target var1) {
      try {
         parseEWKB(new EWKBSource(var0), var1, 0);
      } catch (ArrayIndexOutOfBoundsException var3) {
         throw new IllegalArgumentException();
      }
   }

   public static int type2dimensionSystem(int var0) {
      boolean var1 = (var0 & Integer.MIN_VALUE) != 0;
      boolean var2 = (var0 & 1073741824) != 0;
      var0 &= 65535;
      switch (var0 / 1000) {
         case 1:
            var1 = true;
            break;
         case 3:
            var1 = true;
         case 2:
            var2 = true;
      }

      return (var1 ? 1 : 0) | (var2 ? 2 : 0);
   }

   private static void parseEWKB(EWKBSource var0, GeometryUtils.Target var1, int var2) {
      switch (var0.readByte()) {
         case 0:
            var0.bigEndian = true;
            break;
         case 1:
            var0.bigEndian = false;
            break;
         default:
            throw new IllegalArgumentException();
      }

      int var3 = var0.readInt();
      boolean var4 = (var3 & Integer.MIN_VALUE) != 0;
      boolean var5 = (var3 & 1073741824) != 0;
      int var6 = (var3 & 536870912) != 0 ? var0.readInt() : 0;
      if (var2 == 0) {
         var1.init(var6);
      }

      var3 &= 65535;
      switch (var3 / 1000) {
         case 1:
            var4 = true;
            break;
         case 3:
            var4 = true;
         case 2:
            var5 = true;
      }

      var1.dimensionSystem((var4 ? 1 : 0) | (var5 ? 2 : 0));
      var3 %= 1000;
      int var7;
      int var8;
      label151:
      switch (var3) {
         case 1:
            if (var2 != 0 && var2 != 4 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            var1.startPoint();
            addCoordinate(var0, var1, var4, var5, 0, 1);
            break;
         case 2:
            if (var2 != 0 && var2 != 5 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            var7 = var0.readInt();
            if (var7 < 0 || var7 == 1) {
               throw new IllegalArgumentException();
            }

            var1.startLineString(var7);
            var8 = 0;

            while(true) {
               if (var8 >= var7) {
                  break label151;
               }

               addCoordinate(var0, var1, var4, var5, var8, var7);
               ++var8;
            }
         case 3:
            if (var2 != 0 && var2 != 6 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            var7 = var0.readInt();
            if (var7 == 0) {
               var1.startPolygon(0, 0);
            } else {
               if (var7 < 0) {
                  throw new IllegalArgumentException();
               }

               --var7;
               var8 = var0.readInt();
               if (var8 < 0 || var8 >= 1 && var8 <= 3) {
                  throw new IllegalArgumentException();
               }

               if (var8 == 0 && var7 > 0) {
                  throw new IllegalArgumentException();
               }

               var1.startPolygon(var7, var8);
               if (var8 <= 0) {
                  break;
               }

               addRing(var0, var1, var4, var5, var8);

               for(int var10 = 0; var10 < var7; ++var10) {
                  var8 = var0.readInt();
                  if (var8 < 0 || var8 >= 1 && var8 <= 3) {
                     throw new IllegalArgumentException();
                  }

                  var1.startPolygonInner(var8);
                  addRing(var0, var1, var4, var5, var8);
               }

               var1.endNonEmptyPolygon();
            }
            break;
         case 4:
         case 5:
         case 6:
         case 7:
            if (var2 != 0 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            var7 = var0.readInt();
            if (var7 < 0) {
               throw new IllegalArgumentException();
            }

            var1.startCollection(var3, var7);
            var8 = 0;

            while(true) {
               if (var8 >= var7) {
                  break label151;
               }

               GeometryUtils.Target var9 = var1.startCollectionItem(var8, var7);
               parseEWKB(var0, var9, var3);
               var1.endCollectionItem(var9, var3, var8, var7);
               ++var8;
            }
         default:
            throw new IllegalArgumentException();
      }

      var1.endObject(var3);
   }

   private static void addRing(EWKBSource var0, GeometryUtils.Target var1, boolean var2, boolean var3, int var4) {
      if (var4 >= 4) {
         double var5 = var0.readCoordinate();
         double var7 = var0.readCoordinate();
         var1.addCoordinate(var5, var7, var2 ? var0.readCoordinate() : Double.NaN, var3 ? var0.readCoordinate() : Double.NaN, 0, var4);

         for(int var9 = 1; var9 < var4 - 1; ++var9) {
            addCoordinate(var0, var1, var2, var3, var9, var4);
         }

         double var13 = var0.readCoordinate();
         double var11 = var0.readCoordinate();
         if (var5 != var13 || var7 != var11) {
            throw new IllegalArgumentException();
         }

         var1.addCoordinate(var13, var11, var2 ? var0.readCoordinate() : Double.NaN, var3 ? var0.readCoordinate() : Double.NaN, var4 - 1, var4);
      }

   }

   private static void addCoordinate(EWKBSource var0, GeometryUtils.Target var1, boolean var2, boolean var3, int var4, int var5) {
      var1.addCoordinate(var0.readCoordinate(), var0.readCoordinate(), var2 ? var0.readCoordinate() : Double.NaN, var3 ? var0.readCoordinate() : Double.NaN, var4, var5);
   }

   public static int getDimensionSystem(byte[] var0) {
      EWKBSource var1 = new EWKBSource(var0);
      switch (var1.readByte()) {
         case 0:
            var1.bigEndian = true;
            break;
         case 1:
            var1.bigEndian = false;
            break;
         default:
            throw new IllegalArgumentException();
      }

      return type2dimensionSystem(var1.readInt());
   }

   public static byte[] envelope2wkb(double[] var0) {
      if (var0 == null) {
         return null;
      } else {
         double var2 = var0[0];
         double var4 = var0[1];
         double var6 = var0[2];
         double var8 = var0[3];
         byte[] var1;
         if (var2 == var4 && var6 == var8) {
            var1 = new byte[21];
            var1[4] = 1;
            Bits.writeDouble(var1, 5, var2);
            Bits.writeDouble(var1, 13, var6);
         } else if (var2 != var4 && var6 != var8) {
            var1 = new byte[93];
            var1[4] = 3;
            var1[8] = 1;
            var1[12] = 5;
            Bits.writeDouble(var1, 13, var2);
            Bits.writeDouble(var1, 21, var6);
            Bits.writeDouble(var1, 29, var2);
            Bits.writeDouble(var1, 37, var8);
            Bits.writeDouble(var1, 45, var4);
            Bits.writeDouble(var1, 53, var8);
            Bits.writeDouble(var1, 61, var4);
            Bits.writeDouble(var1, 69, var6);
            Bits.writeDouble(var1, 77, var2);
            Bits.writeDouble(var1, 85, var6);
         } else {
            var1 = new byte[41];
            var1[4] = 2;
            var1[8] = 2;
            Bits.writeDouble(var1, 9, var2);
            Bits.writeDouble(var1, 17, var6);
            Bits.writeDouble(var1, 25, var4);
            Bits.writeDouble(var1, 33, var8);
         }

         return var1;
      }
   }

   private EWKBUtils() {
   }

   private static final class EWKBSource {
      private final byte[] ewkb;
      private int offset;
      boolean bigEndian;

      EWKBSource(byte[] var1) {
         this.ewkb = var1;
      }

      byte readByte() {
         return this.ewkb[this.offset++];
      }

      int readInt() {
         int var1 = this.bigEndian ? Bits.readInt(this.ewkb, this.offset) : Bits.readIntLE(this.ewkb, this.offset);
         this.offset += 4;
         return var1;
      }

      double readCoordinate() {
         double var1 = this.bigEndian ? Bits.readDouble(this.ewkb, this.offset) : Bits.readDoubleLE(this.ewkb, this.offset);
         this.offset += 8;
         return GeometryUtils.toCanonicalDouble(var1);
      }

      public String toString() {
         String var1 = StringUtils.convertBytesToHex(this.ewkb);
         int var2 = this.offset * 2;
         return (new StringBuilder(var1.length() + 3)).append(var1, 0, var2).append("<*>").append(var1, var2, var1.length()).toString();
      }
   }

   public static final class EWKBTarget extends GeometryUtils.Target {
      private final ByteArrayOutputStream output;
      private final int dimensionSystem;
      private final byte[] buf = new byte[8];
      private int type;
      private int srid;

      public EWKBTarget(ByteArrayOutputStream var1, int var2) {
         this.output = var1;
         this.dimensionSystem = var2;
      }

      protected void init(int var1) {
         this.srid = var1;
      }

      protected void startPoint() {
         this.writeHeader(1);
      }

      protected void startLineString(int var1) {
         this.writeHeader(2);
         this.writeInt(var1);
      }

      protected void startPolygon(int var1, int var2) {
         this.writeHeader(3);
         if (var1 == 0 && var2 == 0) {
            this.writeInt(0);
         } else {
            this.writeInt(var1 + 1);
            this.writeInt(var2);
         }

      }

      protected void startPolygonInner(int var1) {
         this.writeInt(var1);
      }

      protected void startCollection(int var1, int var2) {
         this.writeHeader(var1);
         this.writeInt(var2);
      }

      private void writeHeader(int var1) {
         this.type = var1;
         switch (this.dimensionSystem) {
            case 1:
               var1 |= Integer.MIN_VALUE;
               break;
            case 3:
               var1 |= Integer.MIN_VALUE;
            case 2:
               var1 |= 1073741824;
         }

         if (this.srid != 0) {
            var1 |= 536870912;
         }

         this.output.write(0);
         this.writeInt(var1);
         if (this.srid != 0) {
            this.writeInt(this.srid);
            this.srid = 0;
         }

      }

      protected GeometryUtils.Target startCollectionItem(int var1, int var2) {
         return this;
      }

      protected void addCoordinate(double var1, double var3, double var5, double var7, int var9, int var10) {
         boolean var11 = this.type != 1 || !Double.isNaN(var1) || !Double.isNaN(var3) || !Double.isNaN(var5) || !Double.isNaN(var7);
         if (var11) {
            GeometryUtils.checkFinite(var1);
            GeometryUtils.checkFinite(var3);
         }

         this.writeDouble(var1);
         this.writeDouble(var3);
         if ((this.dimensionSystem & 1) != 0) {
            this.writeDouble(var11 ? GeometryUtils.checkFinite(var5) : var5);
         } else if (var11 && !Double.isNaN(var5)) {
            throw new IllegalArgumentException();
         }

         if ((this.dimensionSystem & 2) != 0) {
            this.writeDouble(var11 ? GeometryUtils.checkFinite(var7) : var7);
         } else if (var11 && !Double.isNaN(var7)) {
            throw new IllegalArgumentException();
         }

      }

      private void writeInt(int var1) {
         Bits.writeInt(this.buf, 0, var1);
         this.output.write(this.buf, 0, 4);
      }

      private void writeDouble(double var1) {
         var1 = GeometryUtils.toCanonicalDouble(var1);
         Bits.writeDouble(this.buf, 0, var1);
         this.output.write(this.buf, 0, 8);
      }
   }
}
