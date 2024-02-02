package org.h2.util.geometry;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.h2.util.StringUtils;

public final class EWKTUtils {
   static final String[] TYPES = new String[]{"POINT", "LINESTRING", "POLYGON", "MULTIPOINT", "MULTILINESTRING", "MULTIPOLYGON", "GEOMETRYCOLLECTION"};
   private static final String[] DIMENSION_SYSTEMS = new String[]{"XY", "Z", "M", "ZM"};

   public static String ewkb2ewkt(byte[] var0) {
      return ewkb2ewkt(var0, EWKBUtils.getDimensionSystem(var0));
   }

   public static String ewkb2ewkt(byte[] var0, int var1) {
      StringBuilder var2 = new StringBuilder();
      EWKBUtils.parseEWKB(var0, new EWKTTarget(var2, var1));
      return var2.toString();
   }

   public static byte[] ewkt2ewkb(String var0) {
      return ewkt2ewkb(var0, getDimensionSystem(var0));
   }

   public static byte[] ewkt2ewkb(String var0, int var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      EWKBUtils.EWKBTarget var3 = new EWKBUtils.EWKBTarget(var2, var1);
      parseEWKT(var0, var3);
      return var2.toByteArray();
   }

   public static void parseEWKT(String var0, GeometryUtils.Target var1) {
      parseEWKT(new EWKTSource(var0), var1, 0, 0);
   }

   public static int parseGeometryType(String var0) {
      EWKTSource var1 = new EWKTSource(var0);
      int var2 = var1.readType();
      int var3 = 0;
      if (var1.hasData()) {
         var3 = var1.readDimensionSystem();
         if (var1.hasData()) {
            throw new IllegalArgumentException();
         }
      }

      return var3 * 1000 + var2;
   }

   public static int parseDimensionSystem(String var0) {
      EWKTSource var1 = new EWKTSource(var0);
      int var2 = var1.readDimensionSystem();
      if (!var1.hasData() && var2 != 0) {
         return var2;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static StringBuilder formatGeometryTypeAndDimensionSystem(StringBuilder var0, int var1) {
      int var2 = var1 % 1000;
      int var3 = var1 / 1000;
      if (var2 >= 1 && var2 <= 7 && var3 >= 0 && var3 <= 3) {
         var0.append(TYPES[var2 - 1]);
         if (var3 != 0) {
            var0.append(' ').append(DIMENSION_SYSTEMS[var3]);
         }

         return var0;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static void parseEWKT(EWKTSource var0, GeometryUtils.Target var1, int var2, int var3) {
      if (var2 == 0) {
         var1.init(var0.readSRID());
      }

      int var4;
      switch (var2) {
         case 4:
            var4 = 1;
            break;
         case 5:
            var4 = 2;
            break;
         case 6:
            var4 = 3;
            break;
         default:
            var4 = var0.readType();
            var3 = var0.readDimensionSystem();
      }

      var1.dimensionSystem(var3);
      boolean var5;
      ArrayList var6;
      int var8;
      switch (var4) {
         case 1:
            if (var2 != 0 && var2 != 4 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            var5 = var0.readEmpty();
            var1.startPoint();
            if (var5) {
               var1.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
            } else {
               addCoordinate(var0, var1, var3, 0, 1);
               var0.read(')');
            }
            break;
         case 2:
            if (var2 != 0 && var2 != 5 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            var5 = var0.readEmpty();
            if (var5) {
               var1.startLineString(0);
               break;
            }

            var6 = new ArrayList();

            do {
               var6.add(readCoordinate(var0, var3));
            } while(var0.hasMoreCoordinates());

            int var12 = var6.size();
            if (var12 < 0 || var12 == 1) {
               throw new IllegalArgumentException();
            }

            var1.startLineString(var12);

            for(var8 = 0; var8 < var12; ++var8) {
               double[] var13 = (double[])var6.get(var8);
               var1.addCoordinate(var13[0], var13[1], var13[2], var13[3], var8, var12);
            }
            break;
         case 3:
            if (var2 != 0 && var2 != 6 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            var5 = var0.readEmpty();
            if (var5) {
               var1.startPolygon(0, 0);
            } else {
               var6 = readRing(var0, var3);
               ArrayList var7 = new ArrayList();

               while(var0.hasMoreCoordinates()) {
                  var7.add(readRing(var0, var3));
               }

               var8 = var7.size();
               int var9 = var6.size();
               if (var9 >= 1 && var9 <= 3) {
                  throw new IllegalArgumentException();
               }

               if (var9 == 0 && var8 > 0) {
                  throw new IllegalArgumentException();
               }

               var1.startPolygon(var8, var9);
               if (var9 > 0) {
                  addRing(var6, var1);

                  for(int var10 = 0; var10 < var8; ++var10) {
                     ArrayList var11 = (ArrayList)var7.get(var10);
                     var9 = var11.size();
                     if (var9 >= 1 && var9 <= 3) {
                        throw new IllegalArgumentException();
                     }

                     var1.startPolygonInner(var9);
                     addRing(var11, var1);
                  }

                  var1.endNonEmptyPolygon();
               }
            }
            break;
         case 4:
         case 5:
         case 6:
            parseCollection(var0, var1, var4, var2, var3);
            break;
         case 7:
            parseCollection(var0, var1, 7, var2, 0);
            break;
         default:
            throw new IllegalArgumentException();
      }

      var1.endObject(var4);
      if (var2 == 0 && var0.hasData()) {
         throw new IllegalArgumentException();
      }
   }

   private static void parseCollection(EWKTSource var0, GeometryUtils.Target var1, int var2, int var3, int var4) {
      if (var3 != 0 && var3 != 7) {
         throw new IllegalArgumentException();
      } else {
         if (var0.readEmpty()) {
            var1.startCollection(var2, 0);
         } else if (var2 == 4 && var0.hasCoordinate()) {
            parseMultiPointAlternative(var0, var1, var4);
         } else {
            int var5 = var0.getItemCount();
            var1.startCollection(var2, var5);

            for(int var6 = 0; var6 < var5; ++var6) {
               if (var6 > 0) {
                  var0.read(',');
               }

               GeometryUtils.Target var7 = var1.startCollectionItem(var6, var5);
               parseEWKT(var0, var7, var2, var4);
               var1.endCollectionItem(var7, var2, var6, var5);
            }

            var0.read(')');
         }

      }
   }

   private static void parseMultiPointAlternative(EWKTSource var0, GeometryUtils.Target var1, int var2) {
      ArrayList var3 = new ArrayList();

      do {
         var3.add(readCoordinate(var0, var2));
      } while(var0.hasMoreCoordinates());

      int var4 = var3.size();
      var1.startCollection(4, var4);

      for(int var5 = 0; var5 < var3.size(); ++var5) {
         GeometryUtils.Target var6 = var1.startCollectionItem(var5, var4);
         var1.startPoint();
         double[] var7 = (double[])var3.get(var5);
         var1.addCoordinate(var7[0], var7[1], var7[2], var7[3], 0, 1);
         var1.endCollectionItem(var6, 4, var5, var4);
      }

   }

   private static ArrayList<double[]> readRing(EWKTSource var0, int var1) {
      if (var0.readEmpty()) {
         return new ArrayList(0);
      } else {
         ArrayList var2 = new ArrayList();
         double[] var3 = readCoordinate(var0, var1);
         double var4 = var3[0];
         double var6 = var3[1];
         var2.add(var3);

         while(var0.hasMoreCoordinates()) {
            var2.add(readCoordinate(var0, var1));
         }

         int var8 = var2.size();
         if (var8 < 4) {
            throw new IllegalArgumentException();
         } else {
            var3 = (double[])var2.get(var8 - 1);
            double var9 = var3[0];
            double var11 = var3[1];
            if (var4 == var9 && var6 == var11) {
               return var2;
            } else {
               throw new IllegalArgumentException();
            }
         }
      }
   }

   private static void addRing(ArrayList<double[]> var0, GeometryUtils.Target var1) {
      int var2 = 0;

      for(int var3 = var0.size(); var2 < var3; ++var2) {
         double[] var4 = (double[])var0.get(var2);
         var1.addCoordinate(var4[0], var4[1], var4[2], var4[3], var2, var3);
      }

   }

   private static void addCoordinate(EWKTSource var0, GeometryUtils.Target var1, int var2, int var3, int var4) {
      double var5 = var0.readCoordinate();
      double var7 = var0.readCoordinate();
      double var9 = (var2 & 1) != 0 ? var0.readCoordinate() : Double.NaN;
      double var11 = (var2 & 2) != 0 ? var0.readCoordinate() : Double.NaN;
      var1.addCoordinate(var5, var7, var9, var11, var3, var4);
   }

   private static double[] readCoordinate(EWKTSource var0, int var1) {
      double var2 = var0.readCoordinate();
      double var4 = var0.readCoordinate();
      double var6 = (var1 & 1) != 0 ? var0.readCoordinate() : Double.NaN;
      double var8 = (var1 & 2) != 0 ? var0.readCoordinate() : Double.NaN;
      return new double[]{var2, var4, var6, var8};
   }

   public static int getDimensionSystem(String var0) {
      EWKTSource var1 = new EWKTSource(var0);
      var1.readSRID();
      var1.readType();
      return var1.readDimensionSystem();
   }

   private EWKTUtils() {
   }

   private static final class EWKTSource {
      private final String ewkt;
      private int offset;

      EWKTSource(String var1) {
         this.ewkt = var1;
      }

      int readSRID() {
         this.skipWS();
         int var1;
         if (this.ewkt.regionMatches(true, this.offset, "SRID=", 0, 5)) {
            this.offset += 5;
            int var2 = this.ewkt.indexOf(59, 5);
            if (var2 < 0) {
               throw new IllegalArgumentException();
            }

            int var3;
            for(var3 = var2; this.ewkt.charAt(var3 - 1) <= ' '; --var3) {
            }

            var1 = Integer.parseInt(StringUtils.trimSubstring(this.ewkt, this.offset, var3));
            this.offset = var2 + 1;
         } else {
            var1 = 0;
         }

         return var1;
      }

      void read(char var1) {
         this.skipWS();
         int var2 = this.ewkt.length();
         if (this.offset >= var2) {
            throw new IllegalArgumentException();
         } else if (this.ewkt.charAt(this.offset) != var1) {
            throw new IllegalArgumentException();
         } else {
            ++this.offset;
         }
      }

      int readType() {
         this.skipWS();
         int var1 = this.ewkt.length();
         if (this.offset >= var1) {
            throw new IllegalArgumentException();
         } else {
            int var2 = 0;
            char var3 = this.ewkt.charAt(this.offset);
            switch (var3) {
               case 'G':
               case 'g':
                  var2 = this.match("GEOMETRYCOLLECTION", 7);
                  break;
               case 'L':
               case 'l':
                  var2 = this.match("LINESTRING", 2);
                  break;
               case 'M':
               case 'm':
                  if (this.match("MULTI", 1) != 0) {
                     var2 = this.match("POINT", 4);
                     if (var2 == 0) {
                        var2 = this.match("POLYGON", 6);
                        if (var2 == 0) {
                           var2 = this.match("LINESTRING", 5);
                        }
                     }
                  }
                  break;
               case 'P':
               case 'p':
                  var2 = this.match("POINT", 1);
                  if (var2 == 0) {
                     var2 = this.match("POLYGON", 3);
                  }
            }

            if (var2 == 0) {
               throw new IllegalArgumentException();
            } else {
               return var2;
            }
         }
      }

      int readDimensionSystem() {
         int var1 = this.offset;
         this.skipWS();
         int var2 = this.ewkt.length();
         if (this.offset >= var2) {
            throw new IllegalArgumentException();
         } else {
            char var4 = this.ewkt.charAt(this.offset);
            byte var3;
            switch (var4) {
               case 'M':
               case 'm':
                  var3 = 2;
                  ++this.offset;
                  break;
               case 'Z':
               case 'z':
                  ++this.offset;
                  if (this.offset >= var2) {
                     var3 = 1;
                  } else {
                     var4 = this.ewkt.charAt(this.offset);
                     if (var4 != 'M' && var4 != 'm') {
                        var3 = 1;
                     } else {
                        ++this.offset;
                        var3 = 3;
                     }
                  }
                  break;
               default:
                  var3 = 0;
                  if (var1 != this.offset) {
                     return var3;
                  }
            }

            this.checkStringEnd(var2);
            return var3;
         }
      }

      boolean readEmpty() {
         this.skipWS();
         int var1 = this.ewkt.length();
         if (this.offset >= var1) {
            throw new IllegalArgumentException();
         } else if (this.ewkt.charAt(this.offset) == '(') {
            ++this.offset;
            return false;
         } else if (this.match("EMPTY", 1) != 0) {
            this.checkStringEnd(var1);
            return true;
         } else {
            throw new IllegalArgumentException();
         }
      }

      private int match(String var1, int var2) {
         int var3 = var1.length();
         if (this.offset <= this.ewkt.length() - var3 && this.ewkt.regionMatches(true, this.offset, var1, 0, var3)) {
            this.offset += var3;
         } else {
            var2 = 0;
         }

         return var2;
      }

      private void checkStringEnd(int var1) {
         if (this.offset < var1) {
            char var2 = this.ewkt.charAt(this.offset);
            if (var2 > ' ' && var2 != '(' && var2 != ')' && var2 != ',') {
               throw new IllegalArgumentException();
            }
         }

      }

      public boolean hasCoordinate() {
         this.skipWS();
         return this.offset >= this.ewkt.length() ? false : isNumberStart(this.ewkt.charAt(this.offset));
      }

      public double readCoordinate() {
         this.skipWS();
         int var1 = this.ewkt.length();
         if (this.offset >= var1) {
            throw new IllegalArgumentException();
         } else {
            char var2 = this.ewkt.charAt(this.offset);
            if (!isNumberStart(var2)) {
               throw new IllegalArgumentException();
            } else {
               int var3;
               for(var3 = this.offset++; this.offset < var1 && isNumberPart(var2 = this.ewkt.charAt(this.offset)); ++this.offset) {
               }

               if (this.offset < var1 && var2 > ' ' && var2 != ')' && var2 != ',') {
                  throw new IllegalArgumentException();
               } else {
                  Double var4 = Double.parseDouble(this.ewkt.substring(var3, this.offset));
                  return var4 == 0.0 ? 0.0 : var4;
               }
            }
         }
      }

      private static boolean isNumberStart(char var0) {
         if (var0 >= '0' && var0 <= '9') {
            return true;
         } else {
            switch (var0) {
               case '+':
               case '-':
               case '.':
                  return true;
               case ',':
               default:
                  return false;
            }
         }
      }

      private static boolean isNumberPart(char var0) {
         if (var0 >= '0' && var0 <= '9') {
            return true;
         } else {
            switch (var0) {
               case '+':
               case '-':
               case '.':
               case 'E':
               case 'e':
                  return true;
               default:
                  return false;
            }
         }
      }

      public boolean hasMoreCoordinates() {
         this.skipWS();
         if (this.offset >= this.ewkt.length()) {
            throw new IllegalArgumentException();
         } else {
            switch (this.ewkt.charAt(this.offset)) {
               case ')':
                  ++this.offset;
                  return false;
               case ',':
                  ++this.offset;
                  return true;
               default:
                  throw new IllegalArgumentException();
            }
         }
      }

      boolean hasData() {
         this.skipWS();
         return this.offset < this.ewkt.length();
      }

      int getItemCount() {
         int var1 = 1;
         int var2 = this.offset;
         int var3 = 0;
         int var4 = this.ewkt.length();

         while(var2 < var4) {
            switch (this.ewkt.charAt(var2++)) {
               case '(':
                  ++var3;
                  break;
               case ')':
                  --var3;
                  if (var3 < 0) {
                     return var1;
                  }
               case '*':
               case '+':
               default:
                  break;
               case ',':
                  if (var3 == 0) {
                     ++var1;
                  }
            }
         }

         throw new IllegalArgumentException();
      }

      private void skipWS() {
         for(int var1 = this.ewkt.length(); this.offset < var1 && this.ewkt.charAt(this.offset) <= ' '; ++this.offset) {
         }

      }

      public String toString() {
         return (new StringBuilder(this.ewkt.length() + 3)).append(this.ewkt, 0, this.offset).append("<*>").append(this.ewkt, this.offset, this.ewkt.length()).toString();
      }
   }

   public static final class EWKTTarget extends GeometryUtils.Target {
      private final StringBuilder output;
      private final int dimensionSystem;
      private int type;
      private boolean inMulti;

      public EWKTTarget(StringBuilder var1, int var2) {
         this.output = var1;
         this.dimensionSystem = var2;
      }

      protected void init(int var1) {
         if (var1 != 0) {
            this.output.append("SRID=").append(var1).append(';');
         }

      }

      protected void startPoint() {
         this.writeHeader(1);
      }

      protected void startLineString(int var1) {
         this.writeHeader(2);
         if (var1 == 0) {
            this.output.append("EMPTY");
         }

      }

      protected void startPolygon(int var1, int var2) {
         this.writeHeader(3);
         if (var2 == 0) {
            this.output.append("EMPTY");
         } else {
            this.output.append('(');
         }

      }

      protected void startPolygonInner(int var1) {
         this.output.append(var1 > 0 ? ", " : ", EMPTY");
      }

      protected void endNonEmptyPolygon() {
         this.output.append(')');
      }

      protected void startCollection(int var1, int var2) {
         this.writeHeader(var1);
         if (var2 == 0) {
            this.output.append("EMPTY");
         }

         if (var1 != 7) {
            this.inMulti = true;
         }

      }

      private void writeHeader(int var1) {
         this.type = var1;
         if (!this.inMulti) {
            this.output.append(EWKTUtils.TYPES[var1 - 1]);
            switch (this.dimensionSystem) {
               case 1:
                  this.output.append(" Z");
                  break;
               case 2:
                  this.output.append(" M");
                  break;
               case 3:
                  this.output.append(" ZM");
            }

            this.output.append(' ');
         }
      }

      protected GeometryUtils.Target startCollectionItem(int var1, int var2) {
         if (var1 == 0) {
            this.output.append('(');
         } else {
            this.output.append(", ");
         }

         return this;
      }

      protected void endCollectionItem(GeometryUtils.Target var1, int var2, int var3, int var4) {
         if (var3 + 1 == var4) {
            this.output.append(')');
         }

      }

      protected void endObject(int var1) {
         switch (var1) {
            case 4:
            case 5:
            case 6:
               this.inMulti = false;
            default:
         }
      }

      protected void addCoordinate(double var1, double var3, double var5, double var7, int var9, int var10) {
         if (this.type == 1 && Double.isNaN(var1) && Double.isNaN(var3) && Double.isNaN(var5) && Double.isNaN(var7)) {
            this.output.append("EMPTY");
         } else {
            if (var9 == 0) {
               this.output.append('(');
            } else {
               this.output.append(", ");
            }

            this.writeDouble(var1);
            this.output.append(' ');
            this.writeDouble(var3);
            if ((this.dimensionSystem & 1) != 0) {
               this.output.append(' ');
               this.writeDouble(var5);
            }

            if ((this.dimensionSystem & 2) != 0) {
               this.output.append(' ');
               this.writeDouble(var7);
            }

            if (var9 + 1 == var10) {
               this.output.append(')');
            }

         }
      }

      private void writeDouble(double var1) {
         String var3 = Double.toString(GeometryUtils.checkFinite(var1));
         if (var3.endsWith(".0")) {
            this.output.append(var3, 0, var3.length() - 2);
         } else {
            int var4 = var3.indexOf(".0E");
            if (var4 < 0) {
               this.output.append(var3);
            } else {
               this.output.append(var3, 0, var4).append(var3, var4 + 2, var3.length());
            }
         }

      }
   }
}
