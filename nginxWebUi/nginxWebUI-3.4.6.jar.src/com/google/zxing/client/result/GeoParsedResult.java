/*     */ package com.google.zxing.client.result;
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
/*     */ public final class GeoParsedResult
/*     */   extends ParsedResult
/*     */ {
/*     */   private final double latitude;
/*     */   private final double longitude;
/*     */   private final double altitude;
/*     */   private final String query;
/*     */   
/*     */   GeoParsedResult(double latitude, double longitude, double altitude, String query) {
/*  33 */     super(ParsedResultType.GEO);
/*  34 */     this.latitude = latitude;
/*  35 */     this.longitude = longitude;
/*  36 */     this.altitude = altitude;
/*  37 */     this.query = query;
/*     */   }
/*     */   
/*     */   public String getGeoURI() {
/*     */     StringBuilder result;
/*  42 */     (result = new StringBuilder()).append("geo:");
/*  43 */     result.append(this.latitude);
/*  44 */     result.append(',');
/*  45 */     result.append(this.longitude);
/*  46 */     if (this.altitude > 0.0D) {
/*  47 */       result.append(',');
/*  48 */       result.append(this.altitude);
/*     */     } 
/*  50 */     if (this.query != null) {
/*  51 */       result.append('?');
/*  52 */       result.append(this.query);
/*     */     } 
/*  54 */     return result.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getLatitude() {
/*  61 */     return this.latitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getLongitude() {
/*  68 */     return this.longitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getAltitude() {
/*  75 */     return this.altitude;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getQuery() {
/*  82 */     return this.query;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDisplayResult() {
/*     */     StringBuilder result;
/*  88 */     (result = new StringBuilder(20)).append(this.latitude);
/*  89 */     result.append(", ");
/*  90 */     result.append(this.longitude);
/*  91 */     if (this.altitude > 0.0D) {
/*  92 */       result.append(", ");
/*  93 */       result.append(this.altitude);
/*  94 */       result.append('m');
/*     */     } 
/*  96 */     if (this.query != null) {
/*  97 */       result.append(" (");
/*  98 */       result.append(this.query);
/*  99 */       result.append(')');
/*     */     } 
/* 101 */     return result.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\result\GeoParsedResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */