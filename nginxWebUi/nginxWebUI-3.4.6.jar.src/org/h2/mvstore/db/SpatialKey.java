/*     */ package org.h2.mvstore.db;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.mvstore.rtree.Spatial;
/*     */ import org.h2.value.CompareMode;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SpatialKey
/*     */   extends Value
/*     */   implements Spatial
/*     */ {
/*     */   private final long id;
/*     */   private final float[] minMax;
/*     */   
/*     */   public SpatialKey(long paramLong, float... paramVarArgs) {
/*  30 */     this.id = paramLong;
/*  31 */     this.minMax = paramVarArgs;
/*     */   }
/*     */   
/*     */   public SpatialKey(long paramLong, SpatialKey paramSpatialKey) {
/*  35 */     this.id = paramLong;
/*  36 */     this.minMax = (float[])paramSpatialKey.minMax.clone();
/*     */   }
/*     */ 
/*     */   
/*     */   public float min(int paramInt) {
/*  41 */     return this.minMax[paramInt + paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMin(int paramInt, float paramFloat) {
/*  46 */     this.minMax[paramInt + paramInt] = paramFloat;
/*     */   }
/*     */ 
/*     */   
/*     */   public float max(int paramInt) {
/*  51 */     return this.minMax[paramInt + paramInt + 1];
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMax(int paramInt, float paramFloat) {
/*  56 */     this.minMax[paramInt + paramInt + 1] = paramFloat;
/*     */   }
/*     */ 
/*     */   
/*     */   public Spatial clone(long paramLong) {
/*  61 */     return new SpatialKey(paramLong, this);
/*     */   }
/*     */ 
/*     */   
/*     */   public long getId() {
/*  66 */     return this.id;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNull() {
/*  71 */     return (this.minMax.length == 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  76 */     return getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  81 */     return (int)(this.id >>> 32L ^ this.id);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/*  86 */     if (paramObject == this)
/*  87 */       return true; 
/*  88 */     if (!(paramObject instanceof SpatialKey)) {
/*  89 */       return false;
/*     */     }
/*  91 */     SpatialKey spatialKey = (SpatialKey)paramObject;
/*  92 */     if (this.id != spatialKey.id) {
/*  93 */       return false;
/*     */     }
/*  95 */     return equalsIgnoringId(spatialKey);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compareTypeSafe(Value paramValue, CompareMode paramCompareMode, CastDataProvider paramCastDataProvider) {
/* 100 */     throw new UnsupportedOperationException();
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
/*     */   public boolean equalsIgnoringId(Spatial paramSpatial) {
/* 112 */     return Arrays.equals(this.minMax, ((SpatialKey)paramSpatial).minMax);
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 117 */     paramStringBuilder.append(this.id).append(": (");
/* 118 */     for (byte b = 0; b < this.minMax.length; b += 2) {
/* 119 */       if (b > 0) {
/* 120 */         paramStringBuilder.append(", ");
/*     */       }
/* 122 */       paramStringBuilder.append(this.minMax[b]).append('/').append(this.minMax[b + 1]);
/*     */     } 
/* 124 */     paramStringBuilder.append(")");
/* 125 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 130 */     return TypeInfo.TYPE_GEOMETRY;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 135 */     return 37;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 140 */     return getTraceSQL();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\SpatialKey.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */