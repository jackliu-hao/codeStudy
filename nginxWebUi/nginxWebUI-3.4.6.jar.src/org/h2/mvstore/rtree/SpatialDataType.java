/*     */ package org.h2.mvstore.rtree;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.WriteBuffer;
/*     */ import org.h2.mvstore.type.BasicDataType;
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
/*     */ public class SpatialDataType
/*     */   extends BasicDataType<Spatial>
/*     */ {
/*     */   private final int dimensions;
/*     */   
/*     */   public SpatialDataType(int paramInt) {
/*  28 */     DataUtils.checkArgument((paramInt >= 1 && paramInt < 32), "Dimensions must be between 1 and 31, is {0}", new Object[] {
/*     */           
/*  30 */           Integer.valueOf(paramInt) });
/*  31 */     this.dimensions = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Spatial create(long paramLong, float... paramVarArgs) {
/*  42 */     return new DefaultSpatial(paramLong, paramVarArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public Spatial[] createStorage(int paramInt) {
/*  47 */     return new Spatial[paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public int compare(Spatial paramSpatial1, Spatial paramSpatial2) {
/*  52 */     if (paramSpatial1 == paramSpatial2)
/*  53 */       return 0; 
/*  54 */     if (paramSpatial1 == null)
/*  55 */       return -1; 
/*  56 */     if (paramSpatial2 == null) {
/*  57 */       return 1;
/*     */     }
/*  59 */     long l1 = paramSpatial1.getId();
/*  60 */     long l2 = paramSpatial2.getId();
/*  61 */     return Long.compare(l1, l2);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject1, Object paramObject2) {
/*  72 */     if (paramObject1 == paramObject2)
/*  73 */       return true; 
/*  74 */     if (paramObject1 == null || paramObject2 == null) {
/*  75 */       return false;
/*     */     }
/*  77 */     long l1 = ((Spatial)paramObject1).getId();
/*  78 */     long l2 = ((Spatial)paramObject2).getId();
/*  79 */     return (l1 == l2);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory(Spatial paramSpatial) {
/*  84 */     return 40 + this.dimensions * 4;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(WriteBuffer paramWriteBuffer, Spatial paramSpatial) {
/*  89 */     if (paramSpatial.isNull()) {
/*  90 */       paramWriteBuffer.putVarInt(-1);
/*  91 */       paramWriteBuffer.putVarLong(paramSpatial.getId());
/*     */       return;
/*     */     } 
/*  94 */     int i = 0; byte b;
/*  95 */     for (b = 0; b < this.dimensions; b++) {
/*  96 */       if (paramSpatial.min(b) == paramSpatial.max(b)) {
/*  97 */         i |= 1 << b;
/*     */       }
/*     */     } 
/* 100 */     paramWriteBuffer.putVarInt(i);
/* 101 */     for (b = 0; b < this.dimensions; b++) {
/* 102 */       paramWriteBuffer.putFloat(paramSpatial.min(b));
/* 103 */       if ((i & 1 << b) == 0) {
/* 104 */         paramWriteBuffer.putFloat(paramSpatial.max(b));
/*     */       }
/*     */     } 
/* 107 */     paramWriteBuffer.putVarLong(paramSpatial.getId());
/*     */   }
/*     */ 
/*     */   
/*     */   public Spatial read(ByteBuffer paramByteBuffer) {
/* 112 */     int i = DataUtils.readVarInt(paramByteBuffer);
/* 113 */     if (i == -1) {
/* 114 */       long l1 = DataUtils.readVarLong(paramByteBuffer);
/* 115 */       return create(l1, new float[0]);
/*     */     } 
/* 117 */     float[] arrayOfFloat = new float[this.dimensions * 2];
/* 118 */     for (byte b = 0; b < this.dimensions; b++) {
/* 119 */       float f2, f1 = paramByteBuffer.getFloat();
/*     */       
/* 121 */       if ((i & 1 << b) != 0) {
/* 122 */         f2 = f1;
/*     */       } else {
/* 124 */         f2 = paramByteBuffer.getFloat();
/*     */       } 
/* 126 */       arrayOfFloat[b + b] = f1;
/* 127 */       arrayOfFloat[b + b + 1] = f2;
/*     */     } 
/* 129 */     long l = DataUtils.readVarLong(paramByteBuffer);
/* 130 */     return create(l, arrayOfFloat);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOverlap(Spatial paramSpatial1, Spatial paramSpatial2) {
/* 141 */     if (paramSpatial1.isNull() || paramSpatial2.isNull()) {
/* 142 */       return false;
/*     */     }
/* 144 */     for (byte b = 0; b < this.dimensions; b++) {
/* 145 */       if (paramSpatial1.max(b) < paramSpatial2.min(b) || paramSpatial1.min(b) > paramSpatial2.max(b)) {
/* 146 */         return false;
/*     */       }
/*     */     } 
/* 149 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void increaseBounds(Object paramObject1, Object paramObject2) {
/* 159 */     Spatial spatial1 = (Spatial)paramObject2;
/* 160 */     Spatial spatial2 = (Spatial)paramObject1;
/* 161 */     if (spatial1.isNull() || spatial2.isNull()) {
/*     */       return;
/*     */     }
/* 164 */     for (byte b = 0; b < this.dimensions; b++) {
/* 165 */       float f = spatial1.min(b);
/* 166 */       if (f < spatial2.min(b)) {
/* 167 */         spatial2.setMin(b, f);
/*     */       }
/* 169 */       f = spatial1.max(b);
/* 170 */       if (f > spatial2.max(b)) {
/* 171 */         spatial2.setMax(b, f);
/*     */       }
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
/*     */   public float getAreaIncrease(Object paramObject1, Object paramObject2) {
/* 184 */     Spatial spatial1 = (Spatial)paramObject2;
/* 185 */     Spatial spatial2 = (Spatial)paramObject1;
/* 186 */     if (spatial2.isNull() || spatial1.isNull()) {
/* 187 */       return 0.0F;
/*     */     }
/* 189 */     float f1 = spatial2.min(0);
/* 190 */     float f2 = spatial2.max(0);
/* 191 */     float f3 = f2 - f1;
/* 192 */     f1 = Math.min(f1, spatial1.min(0));
/* 193 */     f2 = Math.max(f2, spatial1.max(0));
/* 194 */     float f4 = f2 - f1;
/* 195 */     for (byte b = 1; b < this.dimensions; b++) {
/* 196 */       f1 = spatial2.min(b);
/* 197 */       f2 = spatial2.max(b);
/* 198 */       f3 *= f2 - f1;
/* 199 */       f1 = Math.min(f1, spatial1.min(b));
/* 200 */       f2 = Math.max(f2, spatial1.max(b));
/* 201 */       f4 *= f2 - f1;
/*     */     } 
/* 203 */     return f4 - f3;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   float getCombinedArea(Object paramObject1, Object paramObject2) {
/* 214 */     Spatial spatial1 = (Spatial)paramObject1;
/* 215 */     Spatial spatial2 = (Spatial)paramObject2;
/* 216 */     if (spatial1.isNull())
/* 217 */       return getArea(spatial2); 
/* 218 */     if (spatial2.isNull()) {
/* 219 */       return getArea(spatial1);
/*     */     }
/* 221 */     float f = 1.0F;
/* 222 */     for (byte b = 0; b < this.dimensions; b++) {
/* 223 */       float f1 = Math.min(spatial1.min(b), spatial2.min(b));
/* 224 */       float f2 = Math.max(spatial1.max(b), spatial2.max(b));
/* 225 */       f *= f2 - f1;
/*     */     } 
/* 227 */     return f;
/*     */   }
/*     */   
/*     */   private float getArea(Spatial paramSpatial) {
/* 231 */     if (paramSpatial.isNull()) {
/* 232 */       return 0.0F;
/*     */     }
/* 234 */     float f = 1.0F;
/* 235 */     for (byte b = 0; b < this.dimensions; b++) {
/* 236 */       f *= paramSpatial.max(b) - paramSpatial.min(b);
/*     */     }
/* 238 */     return f;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Object paramObject1, Object paramObject2) {
/* 249 */     Spatial spatial1 = (Spatial)paramObject1;
/* 250 */     Spatial spatial2 = (Spatial)paramObject2;
/* 251 */     if (spatial1.isNull() || spatial2.isNull()) {
/* 252 */       return false;
/*     */     }
/* 254 */     for (byte b = 0; b < this.dimensions; b++) {
/* 255 */       if (spatial1.min(b) > spatial2.min(b) || spatial1.max(b) < spatial2.max(b)) {
/* 256 */         return false;
/*     */       }
/*     */     } 
/* 259 */     return true;
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
/*     */   public boolean isInside(Object paramObject1, Object paramObject2) {
/* 271 */     Spatial spatial1 = (Spatial)paramObject1;
/* 272 */     Spatial spatial2 = (Spatial)paramObject2;
/* 273 */     if (spatial1.isNull() || spatial2.isNull()) {
/* 274 */       return false;
/*     */     }
/* 276 */     for (byte b = 0; b < this.dimensions; b++) {
/* 277 */       if (spatial1.min(b) <= spatial2.min(b) || spatial1.max(b) >= spatial2.max(b)) {
/* 278 */         return false;
/*     */       }
/*     */     } 
/* 281 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Spatial createBoundingBox(Object paramObject) {
/* 291 */     Spatial spatial = (Spatial)paramObject;
/* 292 */     if (spatial.isNull()) {
/* 293 */       return spatial;
/*     */     }
/* 295 */     return spatial.clone(0L);
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
/*     */   public int[] getExtremes(ArrayList<Object> paramArrayList) {
/* 307 */     paramArrayList = getNotNull(paramArrayList);
/* 308 */     if (paramArrayList.isEmpty()) {
/* 309 */       return null;
/*     */     }
/* 311 */     Spatial spatial1 = createBoundingBox(paramArrayList.get(0));
/* 312 */     Spatial spatial2 = createBoundingBox(spatial1);
/* 313 */     for (byte b1 = 0; b1 < this.dimensions; b1++) {
/* 314 */       float f = spatial2.min(b1);
/* 315 */       spatial2.setMin(b1, spatial2.max(b1));
/* 316 */       spatial2.setMax(b1, f);
/*     */     } 
/* 318 */     for (Object object : paramArrayList) {
/* 319 */       increaseBounds(spatial1, object);
/* 320 */       increaseMaxInnerBounds(spatial2, object);
/*     */     } 
/* 322 */     double d = 0.0D;
/* 323 */     byte b2 = 0;
/* 324 */     for (byte b3 = 0; b3 < this.dimensions; b3++) {
/* 325 */       float f = spatial2.max(b3) - spatial2.min(b3);
/* 326 */       if (f >= 0.0F) {
/*     */ 
/*     */         
/* 329 */         float f3 = spatial1.max(b3) - spatial1.min(b3);
/* 330 */         float f4 = f / f3;
/* 331 */         if (f4 > d) {
/* 332 */           d = f4;
/* 333 */           b2 = b3;
/*     */         } 
/*     */       } 
/* 336 */     }  if (d <= 0.0D) {
/* 337 */       return null;
/*     */     }
/* 339 */     float f1 = spatial2.min(b2);
/* 340 */     float f2 = spatial2.max(b2);
/* 341 */     byte b4 = -1, b5 = -1;
/* 342 */     for (byte b6 = 0; b6 < paramArrayList.size() && (b4 < 0 || b5 < 0); 
/* 343 */       b6++) {
/* 344 */       Spatial spatial = (Spatial)paramArrayList.get(b6);
/* 345 */       if (b4 < 0 && spatial.max(b2) == f1) {
/* 346 */         b4 = b6;
/* 347 */       } else if (b5 < 0 && spatial.min(b2) == f2) {
/* 348 */         b5 = b6;
/*     */       } 
/*     */     } 
/* 351 */     return new int[] { b4, b5 };
/*     */   }
/*     */   
/*     */   private static ArrayList<Object> getNotNull(ArrayList<Object> paramArrayList) {
/* 355 */     boolean bool = false;
/* 356 */     for (Spatial spatial1 : paramArrayList) {
/* 357 */       Spatial spatial2 = spatial1;
/* 358 */       if (spatial2.isNull()) {
/* 359 */         bool = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 363 */     if (!bool) {
/* 364 */       return paramArrayList;
/*     */     }
/* 366 */     ArrayList<Spatial> arrayList = new ArrayList();
/* 367 */     for (Spatial spatial1 : paramArrayList) {
/* 368 */       Spatial spatial2 = spatial1;
/* 369 */       if (!spatial2.isNull()) {
/* 370 */         arrayList.add(spatial2);
/*     */       }
/*     */     } 
/* 373 */     return (ArrayList)arrayList;
/*     */   }
/*     */   
/*     */   private void increaseMaxInnerBounds(Object paramObject1, Object paramObject2) {
/* 377 */     Spatial spatial1 = (Spatial)paramObject1;
/* 378 */     Spatial spatial2 = (Spatial)paramObject2;
/* 379 */     for (byte b = 0; b < this.dimensions; b++) {
/* 380 */       spatial1.setMin(b, Math.min(spatial1.min(b), spatial2.max(b)));
/* 381 */       spatial1.setMax(b, Math.max(spatial1.max(b), spatial2.min(b)));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\rtree\SpatialDataType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */