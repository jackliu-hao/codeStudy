package org.h2.mvstore.db;

import java.util.Arrays;
import org.h2.engine.CastDataProvider;
import org.h2.mvstore.rtree.Spatial;
import org.h2.value.CompareMode;
import org.h2.value.TypeInfo;
import org.h2.value.Value;

public class SpatialKey extends Value implements Spatial {
   private final long id;
   private final float[] minMax;

   public SpatialKey(long var1, float... var3) {
      this.id = var1;
      this.minMax = var3;
   }

   public SpatialKey(long var1, SpatialKey var3) {
      this.id = var1;
      this.minMax = (float[])var3.minMax.clone();
   }

   public float min(int var1) {
      return this.minMax[var1 + var1];
   }

   public void setMin(int var1, float var2) {
      this.minMax[var1 + var1] = var2;
   }

   public float max(int var1) {
      return this.minMax[var1 + var1 + 1];
   }

   public void setMax(int var1, float var2) {
      this.minMax[var1 + var1 + 1] = var2;
   }

   public Spatial clone(long var1) {
      return new SpatialKey(var1, this);
   }

   public long getId() {
      return this.id;
   }

   public boolean isNull() {
      return this.minMax.length == 0;
   }

   public String toString() {
      return this.getString();
   }

   public int hashCode() {
      return (int)(this.id >>> 32 ^ this.id);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SpatialKey)) {
         return false;
      } else {
         SpatialKey var2 = (SpatialKey)var1;
         return this.id != var2.id ? false : this.equalsIgnoringId(var2);
      }
   }

   public int compareTypeSafe(Value var1, CompareMode var2, CastDataProvider var3) {
      throw new UnsupportedOperationException();
   }

   public boolean equalsIgnoringId(Spatial var1) {
      return Arrays.equals(this.minMax, ((SpatialKey)var1).minMax);
   }

   public StringBuilder getSQL(StringBuilder var1, int var2) {
      var1.append(this.id).append(": (");

      for(int var3 = 0; var3 < this.minMax.length; var3 += 2) {
         if (var3 > 0) {
            var1.append(", ");
         }

         var1.append(this.minMax[var3]).append('/').append(this.minMax[var3 + 1]);
      }

      var1.append(")");
      return var1;
   }

   public TypeInfo getType() {
      return TypeInfo.TYPE_GEOMETRY;
   }

   public int getValueType() {
      return 37;
   }

   public String getString() {
      return this.getTraceSQL();
   }
}
