/*     */ package org.h2.value;
/*     */ 
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.Bits;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.geometry.EWKBUtils;
/*     */ import org.h2.util.geometry.EWKTUtils;
/*     */ import org.h2.util.geometry.GeometryUtils;
/*     */ import org.h2.util.geometry.JTSUtils;
/*     */ import org.locationtech.jts.geom.Geometry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ValueGeometry
/*     */   extends ValueBytesBase
/*     */ {
/*  31 */   private static final double[] UNKNOWN_ENVELOPE = new double[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int typeAndDimensionSystem;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int srid;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private double[] envelope;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Object geometry;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ValueGeometry(byte[] paramArrayOfbyte, double[] paramArrayOfdouble) {
/*  62 */     super(paramArrayOfbyte);
/*  63 */     if (paramArrayOfbyte.length < 9 || paramArrayOfbyte[0] != 0) {
/*  64 */       throw DbException.get(22018, StringUtils.convertBytesToHex(paramArrayOfbyte));
/*     */     }
/*  66 */     this.value = paramArrayOfbyte;
/*  67 */     this.envelope = paramArrayOfdouble;
/*  68 */     int i = Bits.readInt(paramArrayOfbyte, 1);
/*  69 */     this.srid = ((i & 0x20000000) != 0) ? Bits.readInt(paramArrayOfbyte, 5) : 0;
/*  70 */     this.typeAndDimensionSystem = (i & 0xFFFF) % 1000 + EWKBUtils.type2dimensionSystem(i) * 1000;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueGeometry getFromGeometry(Object paramObject) {
/*     */     try {
/*  82 */       Geometry geometry = (Geometry)paramObject;
/*  83 */       return (ValueGeometry)Value.cache(new ValueGeometry(JTSUtils.geometry2ewkb(geometry), UNKNOWN_ENVELOPE));
/*  84 */     } catch (RuntimeException runtimeException) {
/*  85 */       throw DbException.get(22018, String.valueOf(paramObject));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueGeometry get(String paramString) {
/*     */     try {
/*  97 */       return (ValueGeometry)Value.cache(new ValueGeometry(EWKTUtils.ewkt2ewkb(paramString), UNKNOWN_ENVELOPE));
/*  98 */     } catch (RuntimeException runtimeException) {
/*  99 */       throw DbException.get(22018, paramString);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueGeometry get(byte[] paramArrayOfbyte) {
/* 110 */     return (ValueGeometry)Value.cache(new ValueGeometry(paramArrayOfbyte, UNKNOWN_ENVELOPE));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueGeometry getFromEWKB(byte[] paramArrayOfbyte) {
/*     */     try {
/* 121 */       return (ValueGeometry)Value.cache(new ValueGeometry(EWKBUtils.ewkb2ewkb(paramArrayOfbyte), UNKNOWN_ENVELOPE));
/* 122 */     } catch (RuntimeException runtimeException) {
/* 123 */       throw DbException.get(22018, StringUtils.convertBytesToHex(paramArrayOfbyte));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Value fromEnvelope(double[] paramArrayOfdouble) {
/* 134 */     return (paramArrayOfdouble != null) ? 
/* 135 */       Value.cache(new ValueGeometry(EWKBUtils.envelope2wkb(paramArrayOfdouble), paramArrayOfdouble)) : ValueNull.INSTANCE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Geometry getGeometry() {
/* 146 */     if (this.geometry == null) {
/*     */       try {
/* 148 */         this.geometry = JTSUtils.ewkb2geometry(this.value, getDimensionSystem());
/* 149 */       } catch (RuntimeException runtimeException) {
/* 150 */         throw DbException.convert(runtimeException);
/*     */       } 
/*     */     }
/* 153 */     return ((Geometry)this.geometry).copy();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getTypeAndDimensionSystem() {
/* 163 */     return this.typeAndDimensionSystem;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getGeometryType() {
/* 172 */     return this.typeAndDimensionSystem % 1000;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getDimensionSystem() {
/* 181 */     return this.typeAndDimensionSystem / 1000;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSRID() {
/* 190 */     return this.srid;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double[] getEnvelopeNoCopy() {
/* 199 */     if (this.envelope == UNKNOWN_ENVELOPE) {
/* 200 */       GeometryUtils.EnvelopeTarget envelopeTarget = new GeometryUtils.EnvelopeTarget();
/* 201 */       EWKBUtils.parseEWKB(this.value, (GeometryUtils.Target)envelopeTarget);
/* 202 */       this.envelope = envelopeTarget.getEnvelope();
/*     */     } 
/* 204 */     return this.envelope;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean intersectsBoundingBox(ValueGeometry paramValueGeometry) {
/* 215 */     return GeometryUtils.intersects(getEnvelopeNoCopy(), paramValueGeometry.getEnvelopeNoCopy());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Value getEnvelopeUnion(ValueGeometry paramValueGeometry) {
/* 225 */     return fromEnvelope(GeometryUtils.union(getEnvelopeNoCopy(), paramValueGeometry.getEnvelopeNoCopy()));
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 230 */     return TypeInfo.TYPE_GEOMETRY;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getValueType() {
/* 235 */     return 37;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 240 */     paramStringBuilder.append("GEOMETRY ");
/* 241 */     if ((paramInt & 0x8) != 0) {
/* 242 */       EWKBUtils.parseEWKB(this.value, (GeometryUtils.Target)new EWKTUtils.EWKTTarget(paramStringBuilder.append('\''), getDimensionSystem()));
/* 243 */       paramStringBuilder.append('\'');
/*     */     } else {
/* 245 */       super.getSQL(paramStringBuilder, 0);
/*     */     } 
/* 247 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString() {
/* 252 */     return EWKTUtils.ewkb2ewkt(this.value, getDimensionSystem());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 257 */     return this.value.length * 20 + 24;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ValueGeometry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */