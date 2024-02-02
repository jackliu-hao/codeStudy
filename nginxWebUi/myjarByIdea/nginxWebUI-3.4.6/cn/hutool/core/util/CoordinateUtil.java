package cn.hutool.core.util;

import java.io.Serializable;
import java.util.Objects;

public class CoordinateUtil {
   public static final double X_PI = 52.35987755982988;
   public static final double PI = Math.PI;
   public static final double RADIUS = 6378245.0;
   public static final double CORRECTION_PARAM = 0.006693421622965943;

   public static boolean outOfChina(double lng, double lat) {
      return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
   }

   public static Coordinate wgs84ToGcj02(double lng, double lat) {
      return (new Coordinate(lng, lat)).offset(offset(lng, lat, true));
   }

   public static Coordinate wgs84ToBd09(double lng, double lat) {
      Coordinate gcj02 = wgs84ToGcj02(lng, lat);
      return gcj02ToBd09(gcj02.lng, gcj02.lat);
   }

   public static Coordinate gcj02ToWgs84(double lng, double lat) {
      return (new Coordinate(lng, lat)).offset(offset(lng, lat, false));
   }

   public static Coordinate gcj02ToBd09(double lng, double lat) {
      double z = Math.sqrt(lng * lng + lat * lat) + 2.0E-5 * Math.sin(lat * 52.35987755982988);
      double theta = Math.atan2(lat, lng) + 3.0E-6 * Math.cos(lng * 52.35987755982988);
      double bd_lng = z * Math.cos(theta) + 0.0065;
      double bd_lat = z * Math.sin(theta) + 0.006;
      return new Coordinate(bd_lng, bd_lat);
   }

   public static Coordinate bd09ToGcj02(double lng, double lat) {
      double x = lng - 0.0065;
      double y = lat - 0.006;
      double z = Math.sqrt(x * x + y * y) - 2.0E-5 * Math.sin(y * 52.35987755982988);
      double theta = Math.atan2(y, x) - 3.0E-6 * Math.cos(x * 52.35987755982988);
      double gg_lng = z * Math.cos(theta);
      double gg_lat = z * Math.sin(theta);
      return new Coordinate(gg_lng, gg_lat);
   }

   public static Coordinate bd09toWgs84(double lng, double lat) {
      Coordinate gcj02 = bd09ToGcj02(lng, lat);
      return gcj02ToWgs84(gcj02.lng, gcj02.lat);
   }

   private static double transCore(double lng, double lat) {
      double ret = (20.0 * Math.sin(6.0 * lng * Math.PI) + 20.0 * Math.sin(2.0 * lng * Math.PI)) * 2.0 / 3.0;
      ret += (20.0 * Math.sin(lat * Math.PI) + 40.0 * Math.sin(lat / 3.0 * Math.PI)) * 2.0 / 3.0;
      return ret;
   }

   private static Coordinate offset(double lng, double lat, boolean isPlus) {
      double dlng = transLng(lng - 105.0, lat - 35.0);
      double dlat = transLat(lng - 105.0, lat - 35.0);
      double magic = Math.sin(lat / 180.0 * Math.PI);
      magic = 1.0 - 0.006693421622965943 * magic * magic;
      double sqrtMagic = Math.sqrt(magic);
      dlng = dlng * 180.0 / (6378245.0 / sqrtMagic * Math.cos(lat / 180.0 * Math.PI) * Math.PI);
      dlat = dlat * 180.0 / (6335552.717000426 / (magic * sqrtMagic) * Math.PI);
      if (!isPlus) {
         dlng = -dlng;
         dlat = -dlat;
      }

      return new Coordinate(dlng, dlat);
   }

   private static double transLng(double lng, double lat) {
      double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
      ret += transCore(lng, lat);
      ret += (150.0 * Math.sin(lng / 12.0 * Math.PI) + 300.0 * Math.sin(lng / 30.0 * Math.PI)) * 2.0 / 3.0;
      return ret;
   }

   private static double transLat(double lng, double lat) {
      double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
      ret += transCore(lng, lat);
      ret += (160.0 * Math.sin(lat / 12.0 * Math.PI) + 320.0 * Math.sin(lat * Math.PI / 30.0)) * 2.0 / 3.0;
      return ret;
   }

   public static class Coordinate implements Serializable {
      private static final long serialVersionUID = 1L;
      private double lng;
      private double lat;

      public Coordinate(double lng, double lat) {
         this.lng = lng;
         this.lat = lat;
      }

      public double getLng() {
         return this.lng;
      }

      public Coordinate setLng(double lng) {
         this.lng = lng;
         return this;
      }

      public double getLat() {
         return this.lat;
      }

      public Coordinate setLat(double lat) {
         this.lat = lat;
         return this;
      }

      public Coordinate offset(Coordinate offset) {
         this.lng += offset.lng;
         this.lat += offset.lat;
         return this;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Coordinate that = (Coordinate)o;
            return Double.compare(that.lng, this.lng) == 0 && Double.compare(that.lat, this.lat) == 0;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.lng, this.lat});
      }

      public String toString() {
         return "Coordinate{lng=" + this.lng + ", lat=" + this.lat + '}';
      }
   }
}
