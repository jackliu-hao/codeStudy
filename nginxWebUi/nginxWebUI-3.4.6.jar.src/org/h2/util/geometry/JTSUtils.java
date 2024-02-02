/*     */ package org.h2.util.geometry;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import org.h2.message.DbException;
/*     */ import org.locationtech.jts.geom.CoordinateSequence;
/*     */ import org.locationtech.jts.geom.CoordinateSequenceFactory;
/*     */ import org.locationtech.jts.geom.Geometry;
/*     */ import org.locationtech.jts.geom.GeometryCollection;
/*     */ import org.locationtech.jts.geom.GeometryFactory;
/*     */ import org.locationtech.jts.geom.LineString;
/*     */ import org.locationtech.jts.geom.LinearRing;
/*     */ import org.locationtech.jts.geom.MultiLineString;
/*     */ import org.locationtech.jts.geom.MultiPoint;
/*     */ import org.locationtech.jts.geom.MultiPolygon;
/*     */ import org.locationtech.jts.geom.Point;
/*     */ import org.locationtech.jts.geom.Polygon;
/*     */ import org.locationtech.jts.geom.PrecisionModel;
/*     */ import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
/*     */ import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
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
/*     */ public final class JTSUtils
/*     */ {
/*     */   public static final class GeometryTarget
/*     */     extends GeometryUtils.Target
/*     */   {
/*     */     private final int dimensionSystem;
/*     */     private GeometryFactory factory;
/*     */     private int type;
/*     */     private CoordinateSequence coordinates;
/*     */     private CoordinateSequence[] innerCoordinates;
/*     */     private int innerOffset;
/*     */     private Geometry[] subgeometries;
/*     */     
/*     */     public GeometryTarget(int param1Int) {
/*  77 */       this.dimensionSystem = param1Int;
/*     */     }
/*     */     
/*     */     private GeometryTarget(int param1Int, GeometryFactory param1GeometryFactory) {
/*  81 */       this.dimensionSystem = param1Int;
/*  82 */       this.factory = param1GeometryFactory;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void init(int param1Int) {
/*  87 */       this
/*     */         
/*  89 */         .factory = new GeometryFactory(new PrecisionModel(), param1Int, ((this.dimensionSystem & 0x2) != 0) ? (CoordinateSequenceFactory)PackedCoordinateSequenceFactory.DOUBLE_FACTORY : (CoordinateSequenceFactory)CoordinateArraySequenceFactory.instance());
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPoint() {
/*  94 */       this.type = 1;
/*  95 */       initCoordinates(1);
/*  96 */       this.innerOffset = -1;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startLineString(int param1Int) {
/* 101 */       this.type = 2;
/* 102 */       initCoordinates(param1Int);
/* 103 */       this.innerOffset = -1;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygon(int param1Int1, int param1Int2) {
/* 108 */       this.type = 3;
/* 109 */       initCoordinates(param1Int2);
/* 110 */       this.innerCoordinates = new CoordinateSequence[param1Int1];
/* 111 */       this.innerOffset = -1;
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startPolygonInner(int param1Int) {
/* 116 */       this.innerCoordinates[++this.innerOffset] = createCoordinates(param1Int);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void startCollection(int param1Int1, int param1Int2) {
/* 121 */       this.type = param1Int1;
/* 122 */       switch (param1Int1) {
/*     */         case 4:
/* 124 */           this.subgeometries = (Geometry[])new Point[param1Int2];
/*     */           return;
/*     */         case 5:
/* 127 */           this.subgeometries = (Geometry[])new LineString[param1Int2];
/*     */           return;
/*     */         case 6:
/* 130 */           this.subgeometries = (Geometry[])new Polygon[param1Int2];
/*     */           return;
/*     */         case 7:
/* 133 */           this.subgeometries = new Geometry[param1Int2];
/*     */           return;
/*     */       } 
/* 136 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     protected GeometryUtils.Target startCollectionItem(int param1Int1, int param1Int2) {
/* 142 */       return new GeometryTarget(this.dimensionSystem, this.factory);
/*     */     }
/*     */ 
/*     */     
/*     */     protected void endCollectionItem(GeometryUtils.Target param1Target, int param1Int1, int param1Int2, int param1Int3) {
/* 147 */       this.subgeometries[param1Int2] = ((GeometryTarget)param1Target).getGeometry();
/*     */     }
/*     */     
/*     */     private void initCoordinates(int param1Int) {
/* 151 */       this.coordinates = createCoordinates(param1Int);
/*     */     }
/*     */     private CoordinateSequence createCoordinates(int param1Int) {
/*     */       byte b;
/*     */       boolean bool;
/* 156 */       switch (this.dimensionSystem) {
/*     */         case 0:
/* 158 */           b = 2;
/* 159 */           bool = false;
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
/* 176 */           return this.factory.getCoordinateSequenceFactory().create(param1Int, b, bool);case 1: b = 3; bool = false; return this.factory.getCoordinateSequenceFactory().create(param1Int, b, bool);case 2: b = 3; bool = true; return this.factory.getCoordinateSequenceFactory().create(param1Int, b, bool);case 3: b = 4; bool = true; return this.factory.getCoordinateSequenceFactory().create(param1Int, b, bool);
/*     */       } 
/*     */       throw DbException.getInternalError();
/*     */     }
/*     */     
/* 181 */     protected void addCoordinate(double param1Double1, double param1Double2, double param1Double3, double param1Double4, int param1Int1, int param1Int2) { if (this.type == 1 && Double.isNaN(param1Double1) && Double.isNaN(param1Double2) && Double.isNaN(param1Double3) && Double.isNaN(param1Double4)) {
/* 182 */         this.coordinates = createCoordinates(0);
/*     */         return;
/*     */       } 
/* 185 */       CoordinateSequence coordinateSequence = (this.innerOffset < 0) ? this.coordinates : this.innerCoordinates[this.innerOffset];
/* 186 */       coordinateSequence.setOrdinate(param1Int1, 0, GeometryUtils.checkFinite(param1Double1));
/* 187 */       coordinateSequence.setOrdinate(param1Int1, 1, GeometryUtils.checkFinite(param1Double2));
/* 188 */       switch (this.dimensionSystem) {
/*     */         case 3:
/* 190 */           coordinateSequence.setOrdinate(param1Int1, 3, GeometryUtils.checkFinite(param1Double4));
/*     */         
/*     */         case 1:
/* 193 */           coordinateSequence.setOrdinate(param1Int1, 2, GeometryUtils.checkFinite(param1Double3));
/*     */           break;
/*     */         case 2:
/* 196 */           coordinateSequence.setOrdinate(param1Int1, 2, GeometryUtils.checkFinite(param1Double4)); break;
/*     */       }  } Geometry getGeometry() { LinearRing linearRing;
/*     */       int i;
/*     */       LinearRing[] arrayOfLinearRing;
/*     */       byte b;
/* 201 */       switch (this.type) {
/*     */         case 1:
/* 203 */           return (Geometry)new Point(this.coordinates, this.factory);
/*     */         case 2:
/* 205 */           return (Geometry)new LineString(this.coordinates, this.factory);
/*     */         case 3:
/* 207 */           linearRing = new LinearRing(this.coordinates, this.factory);
/* 208 */           i = this.innerCoordinates.length;
/* 209 */           arrayOfLinearRing = new LinearRing[i];
/* 210 */           for (b = 0; b < i; b++) {
/* 211 */             arrayOfLinearRing[b] = new LinearRing(this.innerCoordinates[b], this.factory);
/*     */           }
/* 213 */           return (Geometry)new Polygon(linearRing, arrayOfLinearRing, this.factory);
/*     */         
/*     */         case 4:
/* 216 */           return (Geometry)new MultiPoint((Point[])this.subgeometries, this.factory);
/*     */         case 5:
/* 218 */           return (Geometry)new MultiLineString((LineString[])this.subgeometries, this.factory);
/*     */         case 6:
/* 220 */           return (Geometry)new MultiPolygon((Polygon[])this.subgeometries, this.factory);
/*     */         case 7:
/* 222 */           return (Geometry)new GeometryCollection(this.subgeometries, this.factory);
/*     */       } 
/* 224 */       throw new IllegalStateException(); }
/*     */   
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
/*     */   public static Geometry ewkb2geometry(byte[] paramArrayOfbyte) {
/* 238 */     return ewkb2geometry(paramArrayOfbyte, EWKBUtils.getDimensionSystem(paramArrayOfbyte));
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
/*     */   public static Geometry ewkb2geometry(byte[] paramArrayOfbyte, int paramInt) {
/* 251 */     GeometryTarget geometryTarget = new GeometryTarget(paramInt);
/* 252 */     EWKBUtils.parseEWKB(paramArrayOfbyte, geometryTarget);
/* 253 */     return geometryTarget.getGeometry();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] geometry2ewkb(Geometry paramGeometry) {
/* 264 */     return geometry2ewkb(paramGeometry, getDimensionSystem(paramGeometry));
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
/*     */   public static byte[] geometry2ewkb(Geometry paramGeometry, int paramInt) {
/* 278 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 279 */     EWKBUtils.EWKBTarget eWKBTarget = new EWKBUtils.EWKBTarget(byteArrayOutputStream, paramInt);
/* 280 */     parseGeometry(paramGeometry, eWKBTarget);
/* 281 */     return byteArrayOutputStream.toByteArray();
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
/*     */   public static void parseGeometry(Geometry paramGeometry, GeometryUtils.Target paramTarget) {
/* 293 */     parseGeometry(paramGeometry, paramTarget, 0);
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
/*     */   private static void parseGeometry(Geometry paramGeometry, GeometryUtils.Target paramTarget, int paramInt) {
/* 307 */     if (paramInt == 0) {
/* 308 */       paramTarget.init(paramGeometry.getSRID());
/*     */     }
/* 310 */     if (paramGeometry instanceof Point) {
/* 311 */       if (paramInt != 0 && paramInt != 4 && paramInt != 7) {
/* 312 */         throw new IllegalArgumentException();
/*     */       }
/* 314 */       paramTarget.startPoint();
/* 315 */       Point point = (Point)paramGeometry;
/* 316 */       if (point.isEmpty()) {
/* 317 */         paramTarget.addCoordinate(Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0, 1);
/*     */       } else {
/* 319 */         addCoordinate(point.getCoordinateSequence(), paramTarget, 0, 1);
/*     */       } 
/* 321 */       paramTarget.endObject(1);
/* 322 */     } else if (paramGeometry instanceof LineString) {
/* 323 */       if (paramInt != 0 && paramInt != 5 && paramInt != 7) {
/* 324 */         throw new IllegalArgumentException();
/*     */       }
/* 326 */       LineString lineString = (LineString)paramGeometry;
/* 327 */       CoordinateSequence coordinateSequence = lineString.getCoordinateSequence();
/* 328 */       int i = coordinateSequence.size();
/* 329 */       if (i == 1) {
/* 330 */         throw new IllegalArgumentException();
/*     */       }
/* 332 */       paramTarget.startLineString(i);
/* 333 */       for (byte b = 0; b < i; b++) {
/* 334 */         addCoordinate(coordinateSequence, paramTarget, b, i);
/*     */       }
/* 336 */       paramTarget.endObject(2);
/* 337 */     } else if (paramGeometry instanceof Polygon) {
/* 338 */       if (paramInt != 0 && paramInt != 6 && paramInt != 7) {
/* 339 */         throw new IllegalArgumentException();
/*     */       }
/* 341 */       Polygon polygon = (Polygon)paramGeometry;
/* 342 */       int i = polygon.getNumInteriorRing();
/* 343 */       CoordinateSequence coordinateSequence = polygon.getExteriorRing().getCoordinateSequence();
/* 344 */       int j = coordinateSequence.size();
/*     */       
/* 346 */       if (j >= 1 && j <= 3) {
/* 347 */         throw new IllegalArgumentException();
/*     */       }
/* 349 */       if (j == 0 && i > 0) {
/* 350 */         throw new IllegalArgumentException();
/*     */       }
/* 352 */       paramTarget.startPolygon(i, j);
/* 353 */       if (j > 0) {
/* 354 */         addRing(coordinateSequence, paramTarget, j);
/* 355 */         for (byte b = 0; b < i; b++) {
/* 356 */           coordinateSequence = polygon.getInteriorRingN(b).getCoordinateSequence();
/* 357 */           j = coordinateSequence.size();
/*     */           
/* 359 */           if (j >= 1 && j <= 3) {
/* 360 */             throw new IllegalArgumentException();
/*     */           }
/* 362 */           paramTarget.startPolygonInner(j);
/* 363 */           addRing(coordinateSequence, paramTarget, j);
/*     */         } 
/* 365 */         paramTarget.endNonEmptyPolygon();
/*     */       } 
/* 367 */       paramTarget.endObject(3);
/* 368 */     } else if (paramGeometry instanceof GeometryCollection) {
/* 369 */       byte b1; if (paramInt != 0 && paramInt != 7) {
/* 370 */         throw new IllegalArgumentException();
/*     */       }
/* 372 */       GeometryCollection geometryCollection = (GeometryCollection)paramGeometry;
/*     */       
/* 374 */       if (geometryCollection instanceof MultiPoint) {
/* 375 */         b1 = 4;
/* 376 */       } else if (geometryCollection instanceof MultiLineString) {
/* 377 */         b1 = 5;
/* 378 */       } else if (geometryCollection instanceof MultiPolygon) {
/* 379 */         b1 = 6;
/*     */       } else {
/* 381 */         b1 = 7;
/*     */       } 
/* 383 */       int i = geometryCollection.getNumGeometries();
/* 384 */       paramTarget.startCollection(b1, i);
/* 385 */       for (byte b2 = 0; b2 < i; b2++) {
/* 386 */         GeometryUtils.Target target = paramTarget.startCollectionItem(b2, i);
/* 387 */         parseGeometry(geometryCollection.getGeometryN(b2), target, b1);
/* 388 */         paramTarget.endCollectionItem(target, b1, b2, i);
/*     */       } 
/* 390 */       paramTarget.endObject(b1);
/*     */     } else {
/* 392 */       throw new IllegalArgumentException();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static void addRing(CoordinateSequence paramCoordinateSequence, GeometryUtils.Target paramTarget, int paramInt) {
/* 398 */     if (paramInt >= 4) {
/* 399 */       double d1 = GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getX(0)), d2 = GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getY(0));
/* 400 */       addCoordinate(paramCoordinateSequence, paramTarget, 0, paramInt, d1, d2);
/* 401 */       for (byte b = 1; b < paramInt - 1; b++) {
/* 402 */         addCoordinate(paramCoordinateSequence, paramTarget, b, paramInt);
/*     */       }
/* 404 */       double d3 = GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getX(paramInt - 1));
/* 405 */       double d4 = GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getY(paramInt - 1));
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 410 */       if (d1 != d3 || d2 != d4) {
/* 411 */         throw new IllegalArgumentException();
/*     */       }
/* 413 */       addCoordinate(paramCoordinateSequence, paramTarget, paramInt - 1, paramInt, d3, d4);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void addCoordinate(CoordinateSequence paramCoordinateSequence, GeometryUtils.Target paramTarget, int paramInt1, int paramInt2) {
/* 418 */     addCoordinate(paramCoordinateSequence, paramTarget, paramInt1, paramInt2, GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getX(paramInt1)), 
/* 419 */         GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getY(paramInt1)));
/*     */   }
/*     */ 
/*     */   
/*     */   private static void addCoordinate(CoordinateSequence paramCoordinateSequence, GeometryUtils.Target paramTarget, int paramInt1, int paramInt2, double paramDouble1, double paramDouble2) {
/* 424 */     double d1 = GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getZ(paramInt1));
/* 425 */     double d2 = GeometryUtils.toCanonicalDouble(paramCoordinateSequence.getM(paramInt1));
/* 426 */     paramTarget.addCoordinate(paramDouble1, paramDouble2, d1, d2, paramInt1, paramInt2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getDimensionSystem(Geometry paramGeometry) {
/* 437 */     int i = getDimensionSystem1(paramGeometry);
/* 438 */     return (i >= 0) ? i : 0;
/*     */   }
/*     */   
/*     */   private static int getDimensionSystem1(Geometry paramGeometry) {
/*     */     int i;
/* 443 */     if (paramGeometry instanceof Point) {
/* 444 */       i = getDimensionSystemFromSequence(((Point)paramGeometry).getCoordinateSequence());
/* 445 */     } else if (paramGeometry instanceof LineString) {
/* 446 */       i = getDimensionSystemFromSequence(((LineString)paramGeometry).getCoordinateSequence());
/* 447 */     } else if (paramGeometry instanceof Polygon) {
/* 448 */       i = getDimensionSystemFromSequence(((Polygon)paramGeometry).getExteriorRing().getCoordinateSequence());
/* 449 */     } else if (paramGeometry instanceof GeometryCollection) {
/* 450 */       i = -1;
/* 451 */       GeometryCollection geometryCollection = (GeometryCollection)paramGeometry; byte b; int j;
/* 452 */       for (b = 0, j = geometryCollection.getNumGeometries(); b < j; b++) {
/* 453 */         i = getDimensionSystem1(geometryCollection.getGeometryN(b));
/* 454 */         if (i >= 0) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } else {
/* 459 */       throw new IllegalArgumentException();
/*     */     } 
/* 461 */     return i;
/*     */   }
/*     */   
/*     */   private static int getDimensionSystemFromSequence(CoordinateSequence paramCoordinateSequence) {
/* 465 */     int i = paramCoordinateSequence.size();
/* 466 */     if (i > 0) {
/* 467 */       for (byte b = 0; b < i; b++) {
/* 468 */         int j = getDimensionSystemFromCoordinate(paramCoordinateSequence, b);
/* 469 */         if (j >= 0) {
/* 470 */           return j;
/*     */         }
/*     */       } 
/*     */     }
/* 474 */     return (paramCoordinateSequence.hasZ() ? 1 : 0) | (paramCoordinateSequence.hasM() ? 2 : 0);
/*     */   }
/*     */   
/*     */   private static int getDimensionSystemFromCoordinate(CoordinateSequence paramCoordinateSequence, int paramInt) {
/* 478 */     if (Double.isNaN(paramCoordinateSequence.getX(paramInt))) {
/* 479 */       return -1;
/*     */     }
/* 481 */     return (!Double.isNaN(paramCoordinateSequence.getZ(paramInt)) ? 1 : 0) | (
/* 482 */       !Double.isNaN(paramCoordinateSequence.getM(paramInt)) ? 2 : 0);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\geometry\JTSUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */