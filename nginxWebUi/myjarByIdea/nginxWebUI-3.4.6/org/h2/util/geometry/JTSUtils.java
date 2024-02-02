package org.h2.util.geometry;

import java.io.ByteArrayOutputStream;
import org.h2.message.DbException;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;

public final class JTSUtils {
   public static Geometry ewkb2geometry(byte[] var0) {
      return ewkb2geometry(var0, EWKBUtils.getDimensionSystem(var0));
   }

   public static Geometry ewkb2geometry(byte[] var0, int var1) {
      GeometryTarget var2 = new GeometryTarget(var1);
      EWKBUtils.parseEWKB(var0, var2);
      return var2.getGeometry();
   }

   public static byte[] geometry2ewkb(Geometry var0) {
      return geometry2ewkb(var0, getDimensionSystem(var0));
   }

   public static byte[] geometry2ewkb(Geometry var0, int var1) {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      EWKBUtils.EWKBTarget var3 = new EWKBUtils.EWKBTarget(var2, var1);
      parseGeometry(var0, var3);
      return var2.toByteArray();
   }

   public static void parseGeometry(Geometry var0, GeometryUtils.Target var1) {
      parseGeometry(var0, var1, 0);
   }

   private static void parseGeometry(Geometry var0, GeometryUtils.Target var1, int var2) {
      if (var2 == 0) {
         var1.init(var0.getSRID());
      }

      if (var0 instanceof Point) {
         if (var2 != 0 && var2 != 4 && var2 != 7) {
            throw new IllegalArgumentException();
         }

         var1.startPoint();
         Point var3 = (Point)var0;
         if (var3.isEmpty()) {
            var1.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
         } else {
            addCoordinate(var3.getCoordinateSequence(), var1, 0, 1);
         }

         var1.endObject(1);
      } else {
         int var5;
         int var6;
         if (var0 instanceof LineString) {
            if (var2 != 0 && var2 != 5 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            LineString var8 = (LineString)var0;
            CoordinateSequence var4 = var8.getCoordinateSequence();
            var5 = var4.size();
            if (var5 == 1) {
               throw new IllegalArgumentException();
            }

            var1.startLineString(var5);

            for(var6 = 0; var6 < var5; ++var6) {
               addCoordinate(var4, var1, var6, var5);
            }

            var1.endObject(2);
         } else if (var0 instanceof Polygon) {
            if (var2 != 0 && var2 != 6 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            Polygon var9 = (Polygon)var0;
            int var11 = var9.getNumInteriorRing();
            CoordinateSequence var13 = var9.getExteriorRing().getCoordinateSequence();
            var6 = var13.size();
            if (var6 >= 1 && var6 <= 3) {
               throw new IllegalArgumentException();
            }

            if (var6 == 0 && var11 > 0) {
               throw new IllegalArgumentException();
            }

            var1.startPolygon(var11, var6);
            if (var6 > 0) {
               addRing(var13, var1, var6);

               for(int var7 = 0; var7 < var11; ++var7) {
                  var13 = var9.getInteriorRingN(var7).getCoordinateSequence();
                  var6 = var13.size();
                  if (var6 >= 1 && var6 <= 3) {
                     throw new IllegalArgumentException();
                  }

                  var1.startPolygonInner(var6);
                  addRing(var13, var1, var6);
               }

               var1.endNonEmptyPolygon();
            }

            var1.endObject(3);
         } else {
            if (!(var0 instanceof GeometryCollection)) {
               throw new IllegalArgumentException();
            }

            if (var2 != 0 && var2 != 7) {
               throw new IllegalArgumentException();
            }

            GeometryCollection var10 = (GeometryCollection)var0;
            byte var12;
            if (var10 instanceof MultiPoint) {
               var12 = 4;
            } else if (var10 instanceof MultiLineString) {
               var12 = 5;
            } else if (var10 instanceof MultiPolygon) {
               var12 = 6;
            } else {
               var12 = 7;
            }

            var5 = var10.getNumGeometries();
            var1.startCollection(var12, var5);

            for(var6 = 0; var6 < var5; ++var6) {
               GeometryUtils.Target var14 = var1.startCollectionItem(var6, var5);
               parseGeometry(var10.getGeometryN(var6), var14, var12);
               var1.endCollectionItem(var14, var12, var6, var5);
            }

            var1.endObject(var12);
         }
      }

   }

   private static void addRing(CoordinateSequence var0, GeometryUtils.Target var1, int var2) {
      if (var2 >= 4) {
         double var3 = GeometryUtils.toCanonicalDouble(var0.getX(0));
         double var5 = GeometryUtils.toCanonicalDouble(var0.getY(0));
         addCoordinate(var0, var1, 0, var2, var3, var5);
         int var7 = 1;

         while(true) {
            if (var7 >= var2 - 1) {
               double var11 = GeometryUtils.toCanonicalDouble(var0.getX(var2 - 1));
               double var9 = GeometryUtils.toCanonicalDouble(var0.getY(var2 - 1));
               if (var3 != var11 || var5 != var9) {
                  throw new IllegalArgumentException();
               }

               addCoordinate(var0, var1, var2 - 1, var2, var11, var9);
               break;
            }

            addCoordinate(var0, var1, var7, var2);
            ++var7;
         }
      }

   }

   private static void addCoordinate(CoordinateSequence var0, GeometryUtils.Target var1, int var2, int var3) {
      addCoordinate(var0, var1, var2, var3, GeometryUtils.toCanonicalDouble(var0.getX(var2)), GeometryUtils.toCanonicalDouble(var0.getY(var2)));
   }

   private static void addCoordinate(CoordinateSequence var0, GeometryUtils.Target var1, int var2, int var3, double var4, double var6) {
      double var8 = GeometryUtils.toCanonicalDouble(var0.getZ(var2));
      double var10 = GeometryUtils.toCanonicalDouble(var0.getM(var2));
      var1.addCoordinate(var4, var6, var8, var10, var2, var3);
   }

   public static int getDimensionSystem(Geometry var0) {
      int var1 = getDimensionSystem1(var0);
      return var1 >= 0 ? var1 : 0;
   }

   private static int getDimensionSystem1(Geometry var0) {
      int var1;
      if (var0 instanceof Point) {
         var1 = getDimensionSystemFromSequence(((Point)var0).getCoordinateSequence());
      } else if (var0 instanceof LineString) {
         var1 = getDimensionSystemFromSequence(((LineString)var0).getCoordinateSequence());
      } else if (var0 instanceof Polygon) {
         var1 = getDimensionSystemFromSequence(((Polygon)var0).getExteriorRing().getCoordinateSequence());
      } else {
         if (!(var0 instanceof GeometryCollection)) {
            throw new IllegalArgumentException();
         }

         var1 = -1;
         GeometryCollection var2 = (GeometryCollection)var0;
         int var3 = 0;

         for(int var4 = var2.getNumGeometries(); var3 < var4; ++var3) {
            var1 = getDimensionSystem1(var2.getGeometryN(var3));
            if (var1 >= 0) {
               break;
            }
         }
      }

      return var1;
   }

   private static int getDimensionSystemFromSequence(CoordinateSequence var0) {
      int var1 = var0.size();
      if (var1 > 0) {
         for(int var2 = 0; var2 < var1; ++var2) {
            int var3 = getDimensionSystemFromCoordinate(var0, var2);
            if (var3 >= 0) {
               return var3;
            }
         }
      }

      return (var0.hasZ() ? 1 : 0) | (var0.hasM() ? 2 : 0);
   }

   private static int getDimensionSystemFromCoordinate(CoordinateSequence var0, int var1) {
      return Double.isNaN(var0.getX(var1)) ? -1 : (!Double.isNaN(var0.getZ(var1)) ? 1 : 0) | (!Double.isNaN(var0.getM(var1)) ? 2 : 0);
   }

   private JTSUtils() {
   }

   public static final class GeometryTarget extends GeometryUtils.Target {
      private final int dimensionSystem;
      private GeometryFactory factory;
      private int type;
      private CoordinateSequence coordinates;
      private CoordinateSequence[] innerCoordinates;
      private int innerOffset;
      private Geometry[] subgeometries;

      public GeometryTarget(int var1) {
         this.dimensionSystem = var1;
      }

      private GeometryTarget(int var1, GeometryFactory var2) {
         this.dimensionSystem = var1;
         this.factory = var2;
      }

      protected void init(int var1) {
         this.factory = new GeometryFactory(new PrecisionModel(), var1, (CoordinateSequenceFactory)((this.dimensionSystem & 2) != 0 ? PackedCoordinateSequenceFactory.DOUBLE_FACTORY : CoordinateArraySequenceFactory.instance()));
      }

      protected void startPoint() {
         this.type = 1;
         this.initCoordinates(1);
         this.innerOffset = -1;
      }

      protected void startLineString(int var1) {
         this.type = 2;
         this.initCoordinates(var1);
         this.innerOffset = -1;
      }

      protected void startPolygon(int var1, int var2) {
         this.type = 3;
         this.initCoordinates(var2);
         this.innerCoordinates = new CoordinateSequence[var1];
         this.innerOffset = -1;
      }

      protected void startPolygonInner(int var1) {
         this.innerCoordinates[++this.innerOffset] = this.createCoordinates(var1);
      }

      protected void startCollection(int var1, int var2) {
         this.type = var1;
         switch (var1) {
            case 4:
               this.subgeometries = new Point[var2];
               break;
            case 5:
               this.subgeometries = new LineString[var2];
               break;
            case 6:
               this.subgeometries = new Polygon[var2];
               break;
            case 7:
               this.subgeometries = new Geometry[var2];
               break;
            default:
               throw new IllegalArgumentException();
         }

      }

      protected GeometryUtils.Target startCollectionItem(int var1, int var2) {
         return new GeometryTarget(this.dimensionSystem, this.factory);
      }

      protected void endCollectionItem(GeometryUtils.Target var1, int var2, int var3, int var4) {
         this.subgeometries[var3] = ((GeometryTarget)var1).getGeometry();
      }

      private void initCoordinates(int var1) {
         this.coordinates = this.createCoordinates(var1);
      }

      private CoordinateSequence createCoordinates(int var1) {
         byte var2;
         byte var3;
         switch (this.dimensionSystem) {
            case 0:
               var2 = 2;
               var3 = 0;
               break;
            case 1:
               var2 = 3;
               var3 = 0;
               break;
            case 2:
               var2 = 3;
               var3 = 1;
               break;
            case 3:
               var2 = 4;
               var3 = 1;
               break;
            default:
               throw DbException.getInternalError();
         }

         return this.factory.getCoordinateSequenceFactory().create(var1, var2, var3);
      }

      protected void addCoordinate(double var1, double var3, double var5, double var7, int var9, int var10) {
         if (this.type == 1 && Double.isNaN(var1) && Double.isNaN(var3) && Double.isNaN(var5) && Double.isNaN(var7)) {
            this.coordinates = this.createCoordinates(0);
         } else {
            CoordinateSequence var11 = this.innerOffset < 0 ? this.coordinates : this.innerCoordinates[this.innerOffset];
            var11.setOrdinate(var9, 0, GeometryUtils.checkFinite(var1));
            var11.setOrdinate(var9, 1, GeometryUtils.checkFinite(var3));
            switch (this.dimensionSystem) {
               case 2:
                  var11.setOrdinate(var9, 2, GeometryUtils.checkFinite(var7));
                  break;
               case 3:
                  var11.setOrdinate(var9, 3, GeometryUtils.checkFinite(var7));
               case 1:
                  var11.setOrdinate(var9, 2, GeometryUtils.checkFinite(var5));
            }

         }
      }

      Geometry getGeometry() {
         switch (this.type) {
            case 1:
               return new Point(this.coordinates, this.factory);
            case 2:
               return new LineString(this.coordinates, this.factory);
            case 3:
               LinearRing var1 = new LinearRing(this.coordinates, this.factory);
               int var2 = this.innerCoordinates.length;
               LinearRing[] var3 = new LinearRing[var2];

               for(int var4 = 0; var4 < var2; ++var4) {
                  var3[var4] = new LinearRing(this.innerCoordinates[var4], this.factory);
               }

               return new Polygon(var1, var3, this.factory);
            case 4:
               return new MultiPoint((Point[])((Point[])this.subgeometries), this.factory);
            case 5:
               return new MultiLineString((LineString[])((LineString[])this.subgeometries), this.factory);
            case 6:
               return new MultiPolygon((Polygon[])((Polygon[])this.subgeometries), this.factory);
            case 7:
               return new GeometryCollection(this.subgeometries, this.factory);
            default:
               throw new IllegalStateException();
         }
      }
   }
}
