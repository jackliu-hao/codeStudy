package org.h2.value;

import org.h2.message.DbException;
import org.h2.util.Bits;
import org.h2.util.StringUtils;
import org.h2.util.geometry.EWKBUtils;
import org.h2.util.geometry.EWKTUtils;
import org.h2.util.geometry.GeometryUtils;
import org.h2.util.geometry.JTSUtils;
import org.locationtech.jts.geom.Geometry;

public final class ValueGeometry extends ValueBytesBase {
   private static final double[] UNKNOWN_ENVELOPE = new double[0];
   private final int typeAndDimensionSystem;
   private final int srid;
   private double[] envelope;
   private Object geometry;

   private ValueGeometry(byte[] var1, double[] var2) {
      super(var1);
      if (var1.length >= 9 && var1[0] == 0) {
         this.value = var1;
         this.envelope = var2;
         int var3 = Bits.readInt(var1, 1);
         this.srid = (var3 & 536870912) != 0 ? Bits.readInt(var1, 5) : 0;
         this.typeAndDimensionSystem = (var3 & '\uffff') % 1000 + EWKBUtils.type2dimensionSystem(var3) * 1000;
      } else {
         throw DbException.get(22018, (String)StringUtils.convertBytesToHex(var1));
      }
   }

   public static ValueGeometry getFromGeometry(Object var0) {
      try {
         Geometry var1 = (Geometry)var0;
         return (ValueGeometry)Value.cache(new ValueGeometry(JTSUtils.geometry2ewkb(var1), UNKNOWN_ENVELOPE));
      } catch (RuntimeException var2) {
         throw DbException.get(22018, (String)String.valueOf(var0));
      }
   }

   public static ValueGeometry get(String var0) {
      try {
         return (ValueGeometry)Value.cache(new ValueGeometry(EWKTUtils.ewkt2ewkb(var0), UNKNOWN_ENVELOPE));
      } catch (RuntimeException var2) {
         throw DbException.get(22018, (String)var0);
      }
   }

   public static ValueGeometry get(byte[] var0) {
      return (ValueGeometry)Value.cache(new ValueGeometry(var0, UNKNOWN_ENVELOPE));
   }

   public static ValueGeometry getFromEWKB(byte[] var0) {
      try {
         return (ValueGeometry)Value.cache(new ValueGeometry(EWKBUtils.ewkb2ewkb(var0), UNKNOWN_ENVELOPE));
      } catch (RuntimeException var2) {
         throw DbException.get(22018, (String)StringUtils.convertBytesToHex(var0));
      }
   }

   public static Value fromEnvelope(double[] var0) {
      return (Value)(var0 != null ? Value.cache(new ValueGeometry(EWKBUtils.envelope2wkb(var0), var0)) : ValueNull.INSTANCE);
   }

   public Geometry getGeometry() {
      if (this.geometry == null) {
         try {
            this.geometry = JTSUtils.ewkb2geometry(this.value, this.getDimensionSystem());
         } catch (RuntimeException var2) {
            throw DbException.convert(var2);
         }
      }

      return ((Geometry)this.geometry).copy();
   }

   public int getTypeAndDimensionSystem() {
      return this.typeAndDimensionSystem;
   }

   public int getGeometryType() {
      return this.typeAndDimensionSystem % 1000;
   }

   public int getDimensionSystem() {
      return this.typeAndDimensionSystem / 1000;
   }

   public int getSRID() {
      return this.srid;
   }

   public double[] getEnvelopeNoCopy() {
      if (this.envelope == UNKNOWN_ENVELOPE) {
         GeometryUtils.EnvelopeTarget var1 = new GeometryUtils.EnvelopeTarget();
         EWKBUtils.parseEWKB(this.value, var1);
         this.envelope = var1.getEnvelope();
      }

      return this.envelope;
   }

   public boolean intersectsBoundingBox(ValueGeometry var1) {
      return GeometryUtils.intersects(this.getEnvelopeNoCopy(), var1.getEnvelopeNoCopy());
   }

   public Value getEnvelopeUnion(ValueGeometry var1) {
      return fromEnvelope(GeometryUtils.union(this.getEnvelopeNoCopy(), var1.getEnvelopeNoCopy()));
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_GEOMETRY;
   }

   public int getValueType() {
      return 37;
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append("GEOMETRY ");
      if ((var2 & 8) != 0) {
         EWKBUtils.parseEWKB(this.value, new EWKTUtils.EWKTTarget(var1.append('\''), this.getDimensionSystem()));
         var1.append('\'');
      } else {
         super.getSQL(var1, 0);
      }

      return var1;
   }

   public String getString() {
      return EWKTUtils.ewkb2ewkt(this.value, this.getDimensionSystem());
   }

   public int getMemory() {
      return this.value.length * 20 + 24;
   }
}
