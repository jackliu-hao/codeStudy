package org.h2.mvstore.rtree;

import java.util.Arrays;

final class DefaultSpatial implements Spatial {
   private final long id;
   private final float[] minMax;

   public DefaultSpatial(long var1, float... var3) {
      this.id = var1;
      this.minMax = var3;
   }

   private DefaultSpatial(long var1, DefaultSpatial var3) {
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
      return new DefaultSpatial(var1, this);
   }

   public long getId() {
      return this.id;
   }

   public boolean isNull() {
      return this.minMax.length == 0;
   }

   public boolean equalsIgnoringId(Spatial var1) {
      return Arrays.equals(this.minMax, ((DefaultSpatial)var1).minMax);
   }
}
