/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Objects;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CoordinateUtil
/*     */ {
/*     */   public static final double X_PI = 52.35987755982988D;
/*     */   public static final double PI = 3.141592653589793D;
/*     */   public static final double RADIUS = 6378245.0D;
/*     */   public static final double CORRECTION_PARAM = 0.006693421622965943D;
/*     */   
/*     */   public static boolean outOfChina(double lng, double lat) {
/*  51 */     return (lng < 72.004D || lng > 137.8347D || lat < 0.8293D || lat > 55.8271D);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Coordinate wgs84ToGcj02(double lng, double lat) {
/*  63 */     return (new Coordinate(lng, lat)).offset(offset(lng, lat, true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Coordinate wgs84ToBd09(double lng, double lat) {
/*  74 */     Coordinate gcj02 = wgs84ToGcj02(lng, lat);
/*  75 */     return gcj02ToBd09(gcj02.lng, gcj02.lat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Coordinate gcj02ToWgs84(double lng, double lat) {
/*  87 */     return (new Coordinate(lng, lat)).offset(offset(lng, lat, false));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Coordinate gcj02ToBd09(double lng, double lat) {
/*  98 */     double z = Math.sqrt(lng * lng + lat * lat) + 2.0E-5D * Math.sin(lat * 52.35987755982988D);
/*  99 */     double theta = Math.atan2(lat, lng) + 3.0E-6D * Math.cos(lng * 52.35987755982988D);
/* 100 */     double bd_lng = z * Math.cos(theta) + 0.0065D;
/* 101 */     double bd_lat = z * Math.sin(theta) + 0.006D;
/* 102 */     return new Coordinate(bd_lng, bd_lat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Coordinate bd09ToGcj02(double lng, double lat) {
/* 115 */     double x = lng - 0.0065D;
/* 116 */     double y = lat - 0.006D;
/* 117 */     double z = Math.sqrt(x * x + y * y) - 2.0E-5D * Math.sin(y * 52.35987755982988D);
/* 118 */     double theta = Math.atan2(y, x) - 3.0E-6D * Math.cos(x * 52.35987755982988D);
/* 119 */     double gg_lng = z * Math.cos(theta);
/* 120 */     double gg_lat = z * Math.sin(theta);
/* 121 */     return new Coordinate(gg_lng, gg_lat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Coordinate bd09toWgs84(double lng, double lat) {
/* 132 */     Coordinate gcj02 = bd09ToGcj02(lng, lat);
/* 133 */     return gcj02ToWgs84(gcj02.lng, gcj02.lat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static double transCore(double lng, double lat) {
/* 146 */     double ret = (20.0D * Math.sin(6.0D * lng * Math.PI) + 20.0D * Math.sin(2.0D * lng * Math.PI)) * 2.0D / 3.0D;
/* 147 */     ret += (20.0D * Math.sin(lat * Math.PI) + 40.0D * Math.sin(lat / 3.0D * Math.PI)) * 2.0D / 3.0D;
/* 148 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Coordinate offset(double lng, double lat, boolean isPlus) {
/* 160 */     double dlng = transLng(lng - 105.0D, lat - 35.0D);
/* 161 */     double dlat = transLat(lng - 105.0D, lat - 35.0D);
/*     */     
/* 163 */     double magic = Math.sin(lat / 180.0D * Math.PI);
/* 164 */     magic = 1.0D - 0.006693421622965943D * magic * magic;
/* 165 */     double sqrtMagic = Math.sqrt(magic);
/*     */     
/* 167 */     dlng = dlng * 180.0D / 6378245.0D / sqrtMagic * Math.cos(lat / 180.0D * Math.PI) * Math.PI;
/* 168 */     dlat = dlat * 180.0D / 6335552.717000426D / magic * sqrtMagic * Math.PI;
/*     */     
/* 170 */     if (false == isPlus) {
/* 171 */       dlng = -dlng;
/* 172 */       dlat = -dlat;
/*     */     } 
/*     */     
/* 175 */     return new Coordinate(dlng, dlat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static double transLng(double lng, double lat) {
/* 186 */     double ret = 300.0D + lng + 2.0D * lat + 0.1D * lng * lng + 0.1D * lng * lat + 0.1D * Math.sqrt(Math.abs(lng));
/* 187 */     ret += transCore(lng, lat);
/* 188 */     ret += (150.0D * Math.sin(lng / 12.0D * Math.PI) + 300.0D * Math.sin(lng / 30.0D * Math.PI)) * 2.0D / 3.0D;
/* 189 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static double transLat(double lng, double lat) {
/* 200 */     double ret = -100.0D + 2.0D * lng + 3.0D * lat + 0.2D * lat * lat + 0.1D * lng * lat + 0.2D * Math.sqrt(Math.abs(lng));
/* 201 */     ret += transCore(lng, lat);
/* 202 */     ret += (160.0D * Math.sin(lat / 12.0D * Math.PI) + 320.0D * Math.sin(lat * Math.PI / 30.0D)) * 2.0D / 3.0D;
/* 203 */     return ret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Coordinate
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private double lng;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private double lat;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Coordinate(double lng, double lat) {
/* 231 */       this.lng = lng;
/* 232 */       this.lat = lat;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double getLng() {
/* 241 */       return this.lng;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Coordinate setLng(double lng) {
/* 251 */       this.lng = lng;
/* 252 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double getLat() {
/* 261 */       return this.lat;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Coordinate setLat(double lat) {
/* 271 */       this.lat = lat;
/* 272 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Coordinate offset(Coordinate offset) {
/* 282 */       this.lng += offset.lng;
/* 283 */       this.lat += offset.lat;
/* 284 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 289 */       if (this == o) {
/* 290 */         return true;
/*     */       }
/* 292 */       if (o == null || getClass() != o.getClass()) {
/* 293 */         return false;
/*     */       }
/* 295 */       Coordinate that = (Coordinate)o;
/* 296 */       return (Double.compare(that.lng, this.lng) == 0 && Double.compare(that.lat, this.lat) == 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 301 */       return Objects.hash(new Object[] { Double.valueOf(this.lng), Double.valueOf(this.lat) });
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 306 */       return "Coordinate{lng=" + this.lng + ", lat=" + this.lat + '}';
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\CoordinateUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */