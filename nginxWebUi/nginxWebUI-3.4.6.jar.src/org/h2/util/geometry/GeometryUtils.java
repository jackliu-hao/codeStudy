/*     */ package org.h2.util.geometry;
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
/*     */ public final class GeometryUtils
/*     */ {
/*     */   public static final int POINT = 1;
/*     */   public static final int LINE_STRING = 2;
/*     */   public static final int POLYGON = 3;
/*     */   public static final int MULTI_POINT = 4;
/*     */   public static final int MULTI_LINE_STRING = 5;
/*     */   public static final int MULTI_POLYGON = 6;
/*     */   public static final int GEOMETRY_COLLECTION = 7;
/*     */   public static final int X = 0;
/*     */   public static final int Y = 1;
/*     */   public static final int Z = 2;
/*     */   public static final int M = 3;
/*     */   public static final int DIMENSION_SYSTEM_XY = 0;
/*     */   public static final int DIMENSION_SYSTEM_XYZ = 1;
/*     */   public static final int DIMENSION_SYSTEM_XYM = 2;
/*     */   public static final int DIMENSION_SYSTEM_XYZM = 3;
/*     */   public static final int MIN_X = 0;
/*     */   public static final int MAX_X = 1;
/*     */   public static final int MIN_Y = 2;
/*     */   public static final int MAX_Y = 3;
/*     */   
/*     */   public static abstract class Target
/*     */   {
/*     */     protected void init(int param1Int) {}
/*     */     
/*     */     protected void dimensionSystem(int param1Int) {}
/*     */     
/*     */     protected void startPoint() {}
/*     */     
/*     */     protected void startLineString(int param1Int) {}
/*     */     
/*     */     protected void startPolygon(int param1Int1, int param1Int2) {}
/*     */     
/*     */     protected void startPolygonInner(int param1Int) {}
/*     */     
/*     */     protected void endNonEmptyPolygon() {}
/*     */     
/*     */     protected void startCollection(int param1Int1, int param1Int2) {}
/*     */     
/*     */     protected Target startCollectionItem(int param1Int1, int param1Int2) {
/* 108 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void endCollectionItem(Target param1Target, int param1Int1, int param1Int2, int param1Int3) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void endObject(int param1Int) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected abstract void addCoordinate(double param1Double1, double param1Double2, double param1Double3, double param1Double4, int param1Int1, int param1Int2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class EnvelopeTarget
/*     */     extends Target
/*     */   {
/*     */     private boolean enabled;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private double minX;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private double maxX;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private double minY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private double maxY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void startPoint() {
/* 183 */       this.enabled = true;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startLineString(int param1Int) {
/* 188 */       this.enabled = true;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygon(int param1Int1, int param1Int2) {
/* 193 */       this.enabled = true;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygonInner(int param1Int) {
/* 198 */       this.enabled = false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void addCoordinate(double param1Double1, double param1Double2, double param1Double3, double param1Double4, int param1Int1, int param1Int2) {
/* 204 */       if (this.enabled && !Double.isNaN(param1Double1) && !Double.isNaN(param1Double2)) {
/*     */         
/* 206 */         this.minX = this.maxX = param1Double1;
/* 207 */         this.minY = this.maxY = param1Double2;
/* 208 */         this.set = true;
/*     */         
/* 210 */         if (this.minX > param1Double1) {
/* 211 */           this.minX = param1Double1;
/*     */         }
/* 213 */         if (this.maxX < param1Double1) {
/* 214 */           this.maxX = param1Double1;
/*     */         }
/* 216 */         if (this.minY > param1Double2) {
/* 217 */           this.minY = param1Double2;
/*     */         }
/* 219 */         if (this.maxY < param1Double2) {
/* 220 */           this.maxY = param1Double2;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public double[] getEnvelope() {
/* 232 */       (new double[4])[0] = this.minX; (new double[4])[1] = this.maxX; (new double[4])[2] = this.minY; (new double[4])[3] = this.maxY; return this.set ? new double[4] : null;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class DimensionSystemTarget
/*     */     extends Target
/*     */   {
/*     */     private boolean hasZ;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private boolean hasM;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected void dimensionSystem(int param1Int) {
/* 255 */       if ((param1Int & 0x1) != 0) {
/* 256 */         this.hasZ = true;
/*     */       }
/* 258 */       if ((param1Int & 0x2) != 0) {
/* 259 */         this.hasM = true;
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     protected void addCoordinate(double param1Double1, double param1Double2, double param1Double3, double param1Double4, int param1Int1, int param1Int2) {
/* 265 */       if (!this.hasZ && !Double.isNaN(param1Double3)) {
/* 266 */         this.hasZ = true;
/*     */       }
/* 268 */       if (!this.hasM && !Double.isNaN(param1Double4)) {
/* 269 */         this.hasM = true;
/*     */       }
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int getDimensionSystem() {
/* 279 */       return (this.hasZ ? 1 : 0) | (this.hasM ? 2 : 0);
/*     */     }
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
/*     */   public static double[] getEnvelope(byte[] paramArrayOfbyte) {
/* 391 */     EnvelopeTarget envelopeTarget = new EnvelopeTarget();
/* 392 */     EWKBUtils.parseEWKB(paramArrayOfbyte, envelopeTarget);
/* 393 */     return envelopeTarget.getEnvelope();
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
/*     */   public static boolean intersects(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
/* 406 */     return (paramArrayOfdouble1 != null && paramArrayOfdouble2 != null && paramArrayOfdouble1[1] >= paramArrayOfdouble2[0] && paramArrayOfdouble1[0] <= paramArrayOfdouble2[1] && paramArrayOfdouble1[3] >= paramArrayOfdouble2[2] && paramArrayOfdouble1[2] <= paramArrayOfdouble2[3]);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static double[] union(double[] paramArrayOfdouble1, double[] paramArrayOfdouble2) {
/* 424 */     if (paramArrayOfdouble1 == null)
/* 425 */       return paramArrayOfdouble2; 
/* 426 */     if (paramArrayOfdouble2 == null) {
/* 427 */       return paramArrayOfdouble1;
/*     */     }
/* 429 */     double d1 = paramArrayOfdouble1[0], d2 = paramArrayOfdouble1[1], d3 = paramArrayOfdouble1[2], d4 = paramArrayOfdouble1[3];
/* 430 */     double d5 = paramArrayOfdouble2[0], d6 = paramArrayOfdouble2[1], d7 = paramArrayOfdouble2[2], d8 = paramArrayOfdouble2[3];
/* 431 */     boolean bool = false;
/* 432 */     if (d1 > d5) {
/* 433 */       d1 = d5;
/* 434 */       bool = true;
/*     */     } 
/* 436 */     if (d2 < d6) {
/* 437 */       d2 = d6;
/* 438 */       bool = true;
/*     */     } 
/* 440 */     if (d3 > d7) {
/* 441 */       d3 = d7;
/* 442 */       bool = true;
/*     */     } 
/* 444 */     if (d4 < d8) {
/* 445 */       d4 = d8;
/* 446 */       bool = true;
/*     */     } 
/* 448 */     (new double[4])[0] = d1; (new double[4])[1] = d2; (new double[4])[2] = d3; (new double[4])[3] = d4; return bool ? new double[4] : paramArrayOfdouble1;
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
/*     */   static double toCanonicalDouble(double paramDouble) {
/* 460 */     return Double.isNaN(paramDouble) ? Double.NaN : ((paramDouble == 0.0D) ? 0.0D : paramDouble);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static double checkFinite(double paramDouble) {
/* 471 */     if (!Double.isFinite(paramDouble)) {
/* 472 */       throw new IllegalArgumentException();
/*     */     }
/* 474 */     return paramDouble;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\geometry\GeometryUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */