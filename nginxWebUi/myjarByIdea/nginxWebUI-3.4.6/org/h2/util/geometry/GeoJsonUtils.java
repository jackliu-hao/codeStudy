package org.h2.util.geometry;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import org.h2.message.DbException;
import org.h2.util.json.JSONArray;
import org.h2.util.json.JSONByteArrayTarget;
import org.h2.util.json.JSONBytesSource;
import org.h2.util.json.JSONNull;
import org.h2.util.json.JSONNumber;
import org.h2.util.json.JSONObject;
import org.h2.util.json.JSONString;
import org.h2.util.json.JSONValue;
import org.h2.util.json.JSONValueTarget;

public final class GeoJsonUtils {
   static final String[] TYPES = new String[]{"Point", "LineString", "Polygon", "MultiPoint", "MultiLineString", "MultiPolygon", "GeometryCollection"};

   public static byte[] ewkbToGeoJson(byte[] var0, int var1) {
      JSONByteArrayTarget var2 = new JSONByteArrayTarget();
      GeoJsonTarget var3 = new GeoJsonTarget(var2, var1);
      EWKBUtils.parseEWKB(var0, var3);
      return var2.getResult();
   }

   public static byte[] geoJsonToEwkb(byte[] var0, int var1) {
      JSONValue var2 = (JSONValue)JSONBytesSource.parse(var0, new JSONValueTarget());
      GeometryUtils.DimensionSystemTarget var3 = new GeometryUtils.DimensionSystemTarget();
      parse(var2, var3);
      ByteArrayOutputStream var4 = new ByteArrayOutputStream();
      EWKBUtils.EWKBTarget var5 = new EWKBUtils.EWKBTarget(var4, var3.getDimensionSystem());
      var5.init(var1);
      parse(var2, var5);
      return var4.toByteArray();
   }

   private static void parse(JSONValue var0, GeometryUtils.Target var1) {
      if (var0 instanceof JSONNull) {
         var1.startPoint();
         var1.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
         var1.endObject(1);
      } else {
         if (!(var0 instanceof JSONObject)) {
            throw new IllegalArgumentException();
         }

         JSONObject var2 = (JSONObject)var0;
         JSONValue var3 = var2.getFirst("type");
         if (!(var3 instanceof JSONString)) {
            throw new IllegalArgumentException();
         }

         switch (((JSONString)var3).getString()) {
            case "Point":
               parse(var2, var1, 1);
               break;
            case "LineString":
               parse(var2, var1, 2);
               break;
            case "Polygon":
               parse(var2, var1, 3);
               break;
            case "MultiPoint":
               parse(var2, var1, 4);
               break;
            case "MultiLineString":
               parse(var2, var1, 5);
               break;
            case "MultiPolygon":
               parse(var2, var1, 6);
               break;
            case "GeometryCollection":
               parseGeometryCollection(var2, var1);
               break;
            default:
               throw new IllegalArgumentException();
         }
      }

   }

   private static void parse(JSONObject var0, GeometryUtils.Target var1, int var2) {
      JSONValue var3 = var0.getFirst("coordinates");
      if (!(var3 instanceof JSONArray)) {
         throw new IllegalArgumentException();
      } else {
         JSONArray var4 = (JSONArray)var3;
         JSONValue[] var5;
         int var6;
         int var7;
         JSONValue var8;
         switch (var2) {
            case 1:
               var1.startPoint();
               parseCoordinate(var4, var1, 0, 1);
               var1.endObject(1);
               break;
            case 2:
               parseLineString(var4, var1);
               break;
            case 3:
               parsePolygon(var4, var1);
               break;
            case 4:
               var5 = var4.getArray();
               var6 = var5.length;
               var1.startCollection(4, var6);

               for(var7 = 0; var7 < var6; ++var7) {
                  var1.startPoint();
                  parseCoordinate(var5[var7], var1, 0, 1);
                  var1.endObject(1);
                  var1.endCollectionItem(var1, 4, var7, var6);
               }

               var1.endObject(4);
               break;
            case 5:
               var5 = var4.getArray();
               var6 = var5.length;
               var1.startCollection(5, var6);

               for(var7 = 0; var7 < var6; ++var7) {
                  var8 = var5[var7];
                  if (!(var8 instanceof JSONArray)) {
                     throw new IllegalArgumentException();
                  }

                  parseLineString((JSONArray)var8, var1);
                  var1.endCollectionItem(var1, 5, var7, var6);
               }

               var1.endObject(5);
               break;
            case 6:
               var5 = var4.getArray();
               var6 = var5.length;
               var1.startCollection(6, var6);

               for(var7 = 0; var7 < var6; ++var7) {
                  var8 = var5[var7];
                  if (!(var8 instanceof JSONArray)) {
                     throw new IllegalArgumentException();
                  }

                  parsePolygon((JSONArray)var8, var1);
                  var1.endCollectionItem(var1, 6, var7, var6);
               }

               var1.endObject(6);
               break;
            default:
               throw new IllegalArgumentException();
         }

      }
   }

   private static void parseGeometryCollection(JSONObject var0, GeometryUtils.Target var1) {
      JSONValue var2 = var0.getFirst("geometries");
      if (!(var2 instanceof JSONArray)) {
         throw new IllegalArgumentException();
      } else {
         JSONArray var3 = (JSONArray)var2;
         JSONValue[] var4 = var3.getArray();
         int var5 = var4.length;
         var1.startCollection(7, var5);

         for(int var6 = 0; var6 < var5; ++var6) {
            JSONValue var7 = var4[var6];
            parse(var7, var1);
            var1.endCollectionItem(var1, 7, var6, var5);
         }

         var1.endObject(7);
      }
   }

   private static void parseLineString(JSONArray var0, GeometryUtils.Target var1) {
      JSONValue[] var2 = var0.getArray();
      int var3 = var2.length;
      var1.startLineString(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         parseCoordinate(var2[var4], var1, var4, var3);
      }

      var1.endObject(2);
   }

   private static void parsePolygon(JSONArray var0, GeometryUtils.Target var1) {
      JSONValue[] var2 = var0.getArray();
      int var3 = var2.length;
      if (var3 == 0) {
         var1.startPolygon(0, 0);
      } else {
         JSONValue var4 = var2[0];
         if (!(var4 instanceof JSONArray)) {
            throw new IllegalArgumentException();
         }

         JSONValue[] var5 = ((JSONArray)var4).getArray();
         var1.startPolygon(var3 - 1, var5.length);
         parseRing(var5, var1);

         for(int var6 = 1; var6 < var3; ++var6) {
            var4 = var2[var6];
            if (!(var4 instanceof JSONArray)) {
               throw new IllegalArgumentException();
            }

            var5 = ((JSONArray)var4).getArray();
            var1.startPolygonInner(var5.length);
            parseRing(var5, var1);
         }

         var1.endNonEmptyPolygon();
      }

      var1.endObject(3);
   }

   private static void parseRing(JSONValue[] var0, GeometryUtils.Target var1) {
      int var2 = var0.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         parseCoordinate(var0[var3], var1, var3, var2);
      }

   }

   private static void parseCoordinate(JSONValue var0, GeometryUtils.Target var1, int var2, int var3) {
      if (var0 instanceof JSONNull) {
         var1.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
      } else if (!(var0 instanceof JSONArray)) {
         throw new IllegalArgumentException();
      } else {
         JSONValue[] var4 = ((JSONArray)var0).getArray();
         int var5 = var4.length;
         if (var5 < 2) {
            throw new IllegalArgumentException();
         } else {
            var1.addCoordinate(readCoordinate(var4, 0), readCoordinate(var4, 1), readCoordinate(var4, 2), readCoordinate(var4, 3), var2, var3);
         }
      }
   }

   private static double readCoordinate(JSONValue[] var0, int var1) {
      if (var1 >= var0.length) {
         return Double.NaN;
      } else {
         JSONValue var2 = var0[var1];
         if (!(var2 instanceof JSONNumber)) {
            throw new IllegalArgumentException();
         } else {
            return ((JSONNumber)var2).getBigDecimal().doubleValue();
         }
      }
   }

   private GeoJsonUtils() {
   }

   public static final class GeoJsonTarget extends GeometryUtils.Target {
      private final JSONByteArrayTarget output;
      private final int dimensionSystem;
      private int type;
      private boolean inMulti;
      private boolean inMultiLine;
      private boolean wasEmpty;

      public GeoJsonTarget(JSONByteArrayTarget var1, int var2) {
         if (var2 == 2) {
            throw DbException.get(22018, (String)"M (XYM) dimension system is not supported in GeoJson");
         } else {
            this.output = var1;
            this.dimensionSystem = var2;
         }
      }

      protected void startPoint() {
         this.type = 1;
         this.wasEmpty = false;
      }

      protected void startLineString(int var1) {
         this.writeHeader(2);
         if (var1 == 0) {
            this.output.endArray();
         }

      }

      protected void startPolygon(int var1, int var2) {
         this.writeHeader(3);
         if (var2 == 0) {
            this.output.endArray();
         } else {
            this.output.startArray();
         }

      }

      protected void startPolygonInner(int var1) {
         this.output.startArray();
         if (var1 == 0) {
            this.output.endArray();
         }

      }

      protected void endNonEmptyPolygon() {
         this.output.endArray();
      }

      protected void startCollection(int var1, int var2) {
         this.writeHeader(var1);
         if (var1 != 7) {
            this.inMulti = true;
            if (var1 == 5 || var1 == 6) {
               this.inMultiLine = true;
            }
         }

      }

      protected GeometryUtils.Target startCollectionItem(int var1, int var2) {
         if (this.inMultiLine) {
            this.output.startArray();
         }

         return this;
      }

      protected void endObject(int var1) {
         switch (var1) {
            case 4:
            case 5:
            case 6:
               this.inMultiLine = this.inMulti = false;
            case 7:
               this.output.endArray();
            default:
               if (!this.inMulti && !this.wasEmpty) {
                  this.output.endObject();
               }

         }
      }

      private void writeHeader(int var1) {
         this.type = var1;
         this.wasEmpty = false;
         if (!this.inMulti) {
            this.writeStartObject(var1);
         }

      }

      protected void addCoordinate(double var1, double var3, double var5, double var7, int var9, int var10) {
         if (this.type == 1) {
            if (Double.isNaN(var1) && Double.isNaN(var3) && Double.isNaN(var5) && Double.isNaN(var7)) {
               this.wasEmpty = true;
               this.output.valueNull();
               return;
            }

            if (!this.inMulti) {
               this.writeStartObject(1);
            }
         }

         this.output.startArray();
         this.writeDouble(var1);
         this.writeDouble(var3);
         if ((this.dimensionSystem & 1) != 0) {
            this.writeDouble(var5);
         }

         if ((this.dimensionSystem & 2) != 0) {
            this.writeDouble(var7);
         }

         this.output.endArray();
         if (this.type != 1 && var9 + 1 == var10) {
            this.output.endArray();
         }

      }

      private void writeStartObject(int var1) {
         this.output.startObject();
         this.output.member("type");
         this.output.valueString(GeoJsonUtils.TYPES[var1 - 1]);
         this.output.member(var1 != 7 ? "coordinates" : "geometries");
         if (var1 != 1) {
            this.output.startArray();
         }

      }

      private void writeDouble(double var1) {
         this.output.valueNumber(BigDecimal.valueOf(GeometryUtils.checkFinite(var1)).stripTrailingZeros());
      }
   }
}
