package org.h2.mvstore.rtree;

public interface Spatial {
   float min(int var1);

   void setMin(int var1, float var2);

   float max(int var1);

   void setMax(int var1, float var2);

   Spatial clone(long var1);

   long getId();

   boolean isNull();

   boolean equalsIgnoringId(Spatial var1);
}
