package org.h2.mvstore.rtree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.h2.mvstore.CursorPos;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.Page;
import org.h2.mvstore.RootReference;
import org.h2.mvstore.type.DataType;

public final class MVRTreeMap<V> extends MVMap<Spatial, V> {
   private final SpatialDataType keyType;
   private boolean quadraticSplit;

   public MVRTreeMap(Map<String, Object> var1, SpatialDataType var2, DataType<V> var3) {
      super(var1, var2, var3);
      this.keyType = var2;
      this.quadraticSplit = Boolean.parseBoolean(String.valueOf(var1.get("quadraticSplit")));
   }

   private MVRTreeMap(MVRTreeMap<V> var1) {
      super(var1);
      this.keyType = var1.keyType;
      this.quadraticSplit = var1.quadraticSplit;
   }

   public MVRTreeMap<V> cloneIt() {
      return new MVRTreeMap(this);
   }

   public RTreeCursor<V> findIntersectingKeys(Spatial var1) {
      return new IntersectsRTreeCursor(this.getRootPage(), var1, this.keyType);
   }

   public RTreeCursor<V> findContainedKeys(Spatial var1) {
      return new ContainsRTreeCursor(this.getRootPage(), var1, this.keyType);
   }

   private boolean contains(Page<Spatial, V> var1, int var2, Object var3) {
      return this.keyType.contains(var1.getKey(var2), var3);
   }

   public V get(Page<Spatial, V> var1, Spatial var2) {
      int var3 = var1.getKeyCount();
      int var4;
      if (!var1.isLeaf()) {
         for(var4 = 0; var4 < var3; ++var4) {
            if (this.contains(var1, var4, var2)) {
               Object var5 = this.get(var1.getChildPage(var4), var2);
               if (var5 != null) {
                  return var5;
               }
            }
         }
      } else {
         for(var4 = 0; var4 < var3; ++var4) {
            if (this.keyType.equals(var1.getKey(var4), var2)) {
               return var1.getValue(var4);
            }
         }
      }

      return null;
   }

   public V remove(Object var1) {
      return this.operate((Spatial)((Spatial)var1), (Object)null, MVMap.DecisionMaker.REMOVE);
   }

   public V operate(Spatial var1, V var2, MVMap.DecisionMaker<? super V> var3) {
      int var4 = 0;
      ArrayList var5 = this.isPersistent() ? new ArrayList() : null;

      while(true) {
         RootReference var6 = this.flushAndGetRoot();
         if (var4++ == 0 && !var6.isLockedByCurrentThread()) {
            this.beforeWrite();
         }

         Page var7 = var6.root;
         if (var5 != null && var7.getTotalCount() > 0L) {
            var5.add(var7);
         }

         var7 = var7.copy();
         Object var8 = this.operate(var7, var1, var2, var3, var5);
         if (!var7.isLeaf() && var7.getTotalCount() == 0L) {
            if (var5 != null) {
               var5.add(var7);
            }

            var7 = this.createEmptyLeaf();
         } else if (var7.getKeyCount() > this.store.getKeysPerPage() || (long)var7.getMemory() > this.store.getMaxPageSize() && var7.getKeyCount() > 3) {
            long var9 = var7.getTotalCount();
            Page var11 = this.split(var7);
            Spatial var12 = this.getBounds(var7);
            Spatial var13 = this.getBounds(var11);
            Spatial[] var14 = (Spatial[])var7.createKeyStorage(2);
            var14[0] = var12;
            var14[1] = var13;
            Page.PageReference[] var15 = Page.createRefStorage(3);
            var15[0] = new Page.PageReference(var7);
            var15[1] = new Page.PageReference(var11);
            var15[2] = Page.PageReference.empty();
            var7 = Page.createNode(this, var14, var15, var9, 0);
            if (this.isPersistent()) {
               this.store.registerUnsavedMemory(var7.getMemory());
            }
         }

         if (var5 == null) {
            if (updateRoot(var6, var7, var4)) {
               return var8;
            }
         } else {
            RootReference var19 = this.tryLock(var6, var4);
            if (var19 != null) {
               try {
                  long var10 = var19.version;
                  int var20 = 0;
                  Iterator var21 = var5.iterator();

                  while(var21.hasNext()) {
                     Page var22 = (Page)var21.next();
                     if (!var22.isRemoved()) {
                        var20 += var22.removePage(var10);
                     }
                  }

                  this.store.registerUnsavedMemory(var20);
                  return var8;
               } finally {
                  this.unlockRoot(var7);
               }
            }

            var5.clear();
         }

         var3.reset();
      }
   }

   private V operate(Page<Spatial, V> var1, Spatial var2, V var3, MVMap.DecisionMaker<? super V> var4, Collection<Page<Spatial, V>> var5) {
      Object var6;
      int var7;
      int var8;
      int var13;
      if (var1.isLeaf()) {
         var7 = -1;
         var8 = var1.getKeyCount();

         for(var13 = 0; var13 < var8; ++var13) {
            if (this.keyType.equals(var1.getKey(var13), var2)) {
               var7 = var13;
            }
         }

         var6 = var7 < 0 ? null : var1.getValue(var7);
         MVMap.Decision var16 = var4.decide(var6, var3);
         switch (var16) {
            case REPEAT:
            case ABORT:
            default:
               break;
            case REMOVE:
               if (var7 >= 0) {
                  var1.remove(var7);
               }
               break;
            case PUT:
               var3 = var4.selectValue(var6, var3);
               if (var7 < 0) {
                  var1.insertLeaf(var1.getKeyCount(), var2, var3);
               } else {
                  var1.setKey(var7, var2);
                  var1.setValue(var7, var3);
               }
         }

         return var6;
      } else {
         var7 = -1;

         Page var9;
         for(var8 = 0; var8 < var1.getKeyCount(); ++var8) {
            if (this.contains(var1, var8, var2)) {
               var9 = var1.getChildPage(var8);
               if (this.get(var9, var2) != null) {
                  var7 = var8;
                  break;
               }

               if (var7 < 0) {
                  var7 = var8;
               }
            }
         }

         if (var7 < 0) {
            float var12 = Float.MAX_VALUE;

            for(var13 = 0; var13 < var1.getKeyCount(); ++var13) {
               Object var10 = var1.getKey(var13);
               float var11 = this.keyType.getAreaIncrease(var10, var2);
               if (var11 < var12) {
                  var7 = var13;
                  var12 = var11;
               }
            }
         }

         Page var14 = var1.getChildPage(var7);
         if (var5 != null) {
            var5.add(var14);
         }

         var14 = var14.copy();
         if (var14.getKeyCount() <= this.store.getKeysPerPage() && ((long)var14.getMemory() <= this.store.getMaxPageSize() || var14.getKeyCount() <= 4)) {
            var6 = this.operate(var14, var2, var3, var4, var5);
            Spatial var15 = (Spatial)var1.getKey(var7);
            if (!this.keyType.contains(var15, var2)) {
               var15 = this.keyType.createBoundingBox(var15);
               this.keyType.increaseBounds(var15, var2);
               var1.setKey(var7, var15);
            }

            if (var14.getTotalCount() > 0L) {
               var1.setChild(var7, var14);
            } else {
               var1.remove(var7);
            }
         } else {
            var9 = this.split(var14);
            var1.setKey(var7, this.getBounds(var14));
            var1.setChild(var7, var14);
            var1.insertNode(var7, this.getBounds(var9), var9);
            var6 = this.operate(var1, var2, var3, var4, var5);
         }

         return var6;
      }
   }

   private Spatial getBounds(Page<Spatial, V> var1) {
      Spatial var2 = this.keyType.createBoundingBox(var1.getKey(0));
      int var3 = var1.getKeyCount();

      for(int var4 = 1; var4 < var3; ++var4) {
         this.keyType.increaseBounds(var2, var1.getKey(var4));
      }

      return var2;
   }

   public V put(Spatial var1, V var2) {
      return this.operate(var1, var2, MVMap.DecisionMaker.PUT);
   }

   public void add(Spatial var1, V var2) {
      this.operate(var1, var2, MVMap.DecisionMaker.PUT);
   }

   private Page<Spatial, V> split(Page<Spatial, V> var1) {
      return this.quadraticSplit ? this.splitQuadratic(var1) : this.splitLinear(var1);
   }

   private Page<Spatial, V> splitLinear(Page<Spatial, V> var1) {
      int var2 = var1.getKeyCount();
      ArrayList var3 = new ArrayList(var2);

      for(int var4 = 0; var4 < var2; ++var4) {
         var3.add(var1.getKey(var4));
      }

      int[] var12 = this.keyType.getExtremes(var3);
      if (var12 == null) {
         return this.splitQuadratic(var1);
      } else {
         Page var5 = this.newPage(var1.isLeaf());
         Page var6 = this.newPage(var1.isLeaf());
         move(var1, var5, var12[0]);
         if (var12[1] > var12[0]) {
            int var10002 = var12[1]--;
         }

         move(var1, var6, var12[1]);
         Spatial var7 = this.keyType.createBoundingBox(var5.getKey(0));
         Spatial var8 = this.keyType.createBoundingBox(var6.getKey(0));

         while(var1.getKeyCount() > 0) {
            Object var9 = var1.getKey(0);
            float var10 = this.keyType.getAreaIncrease(var7, var9);
            float var11 = this.keyType.getAreaIncrease(var8, var9);
            if (var10 < var11) {
               this.keyType.increaseBounds(var7, var9);
               move(var1, var5, 0);
            } else {
               this.keyType.increaseBounds(var8, var9);
               move(var1, var6, 0);
            }
         }

         while(var6.getKeyCount() > 0) {
            move(var6, var1, 0);
         }

         return var5;
      }
   }

   private Page<Spatial, V> splitQuadratic(Page<Spatial, V> var1) {
      Page var2 = this.newPage(var1.isLeaf());
      Page var3 = this.newPage(var1.isLeaf());
      float var4 = Float.MIN_VALUE;
      int var5 = 0;
      int var6 = 0;
      int var7 = var1.getKeyCount();

      float var12;
      for(int var8 = 0; var8 < var7; ++var8) {
         Object var9 = var1.getKey(var8);

         for(int var10 = 0; var10 < var7; ++var10) {
            if (var8 != var10) {
               Object var11 = var1.getKey(var10);
               var12 = this.keyType.getCombinedArea(var9, var11);
               if (var12 > var4) {
                  var4 = var12;
                  var5 = var8;
                  var6 = var10;
               }
            }
         }
      }

      move(var1, var2, var5);
      if (var5 < var6) {
         --var6;
      }

      move(var1, var3, var6);
      Spatial var19 = this.keyType.createBoundingBox(var2.getKey(0));
      Spatial var20 = this.keyType.createBoundingBox(var3.getKey(0));

      while(var1.getKeyCount() > 0) {
         float var21 = 0.0F;
         float var22 = 0.0F;
         var12 = 0.0F;
         int var13 = 0;
         var7 = var1.getKeyCount();

         for(int var14 = 0; var14 < var7; ++var14) {
            Object var15 = var1.getKey(var14);
            float var16 = this.keyType.getAreaIncrease(var19, var15);
            float var17 = this.keyType.getAreaIncrease(var20, var15);
            float var18 = Math.abs(var16 - var17);
            if (var18 > var21) {
               var21 = var18;
               var22 = var16;
               var12 = var17;
               var13 = var14;
            }
         }

         if (var22 < var12) {
            this.keyType.increaseBounds(var19, var1.getKey(var13));
            move(var1, var2, var13);
         } else {
            this.keyType.increaseBounds(var20, var1.getKey(var13));
            move(var1, var3, var13);
         }
      }

      while(var3.getKeyCount() > 0) {
         move(var3, var1, 0);
      }

      return var2;
   }

   private Page<Spatial, V> newPage(boolean var1) {
      Page var2 = var1 ? this.createEmptyLeaf() : this.createEmptyNode();
      if (this.isPersistent()) {
         this.store.registerUnsavedMemory(var2.getMemory());
      }

      return var2;
   }

   private static <V> void move(Page<Spatial, V> var0, Page<Spatial, V> var1, int var2) {
      Spatial var3 = (Spatial)var0.getKey(var2);
      if (var0.isLeaf()) {
         Object var4 = var0.getValue(var2);
         var1.insertLeaf(0, var3, var4);
      } else {
         Page var5 = var0.getChildPage(var2);
         var1.insertNode(0, var3, var5);
      }

      var0.remove(var2);
   }

   public void addNodeKeys(ArrayList<Spatial> var1, Page<Spatial, V> var2) {
      if (var2 != null && !var2.isLeaf()) {
         int var3 = var2.getKeyCount();

         for(int var4 = 0; var4 < var3; ++var4) {
            var1.add(var2.getKey(var4));
            this.addNodeKeys(var1, var2.getChildPage(var4));
         }
      }

   }

   public boolean isQuadraticSplit() {
      return this.quadraticSplit;
   }

   public void setQuadraticSplit(boolean var1) {
      this.quadraticSplit = var1;
   }

   protected int getChildPageCount(Page<Spatial, V> var1) {
      return var1.getRawChildPageCount() - 1;
   }

   public String getType() {
      return "rtree";
   }

   public static class Builder<V> extends MVMap.BasicBuilder<MVRTreeMap<V>, Spatial, V> {
      private int dimensions = 2;

      public Builder() {
         this.setKeyType(new SpatialDataType(this.dimensions));
      }

      public Builder<V> dimensions(int var1) {
         this.dimensions = var1;
         this.setKeyType(new SpatialDataType(var1));
         return this;
      }

      public Builder<V> valueType(DataType<? super V> var1) {
         this.setValueType(var1);
         return this;
      }

      public MVRTreeMap<V> create(Map<String, Object> var1) {
         return new MVRTreeMap(var1, (SpatialDataType)this.getKeyType(), this.getValueType());
      }
   }

   private static final class ContainsRTreeCursor<V> extends RTreeCursor<V> {
      private final SpatialDataType keyType;

      public ContainsRTreeCursor(Page<Spatial, V> var1, Spatial var2, SpatialDataType var3) {
         super(var1, var2);
         this.keyType = var3;
      }

      protected boolean check(boolean var1, Spatial var2, Spatial var3) {
         return var1 ? this.keyType.isInside(var2, var3) : this.keyType.isOverlap(var2, var3);
      }
   }

   private static final class IntersectsRTreeCursor<V> extends RTreeCursor<V> {
      private final SpatialDataType keyType;

      public IntersectsRTreeCursor(Page<Spatial, V> var1, Spatial var2, SpatialDataType var3) {
         super(var1, var2);
         this.keyType = var3;
      }

      protected boolean check(boolean var1, Spatial var2, Spatial var3) {
         return this.keyType.isOverlap(var2, var3);
      }
   }

   public abstract static class RTreeCursor<V> implements Iterator<Spatial> {
      private final Spatial filter;
      private CursorPos<Spatial, V> pos;
      private Spatial current;
      private final Page<Spatial, V> root;
      private boolean initialized;

      protected RTreeCursor(Page<Spatial, V> var1, Spatial var2) {
         this.root = var1;
         this.filter = var2;
      }

      public boolean hasNext() {
         if (!this.initialized) {
            this.pos = new CursorPos(this.root, 0, (CursorPos)null);
            this.fetchNext();
            this.initialized = true;
         }

         return this.current != null;
      }

      public void skip(long var1) {
         while(this.hasNext() && var1-- > 0L) {
            this.fetchNext();
         }

      }

      public Spatial next() {
         if (!this.hasNext()) {
            return null;
         } else {
            Spatial var1 = this.current;
            this.fetchNext();
            return var1;
         }
      }

      void fetchNext() {
         while(this.pos != null) {
            Page var1 = this.pos.page;
            if (!var1.isLeaf()) {
               boolean var6 = false;

               label31: {
                  int var3;
                  Spatial var4;
                  do {
                     if (this.pos.index >= var1.getKeyCount()) {
                        break label31;
                     }

                     var3 = this.pos.index++;
                     var4 = (Spatial)var1.getKey(var3);
                  } while(this.filter != null && !this.check(false, var4, this.filter));

                  Page var5 = this.pos.page.getChildPage(var3);
                  this.pos = new CursorPos(var5, 0, this.pos);
                  var6 = true;
               }

               if (var6) {
                  continue;
               }
            } else {
               label54: {
                  Spatial var2;
                  do {
                     if (this.pos.index >= var1.getKeyCount()) {
                        break label54;
                     }

                     var2 = (Spatial)var1.getKey(this.pos.index++);
                  } while(this.filter != null && !this.check(true, var2, this.filter));

                  this.current = var2;
                  return;
               }
            }

            this.pos = this.pos.parent;
         }

         this.current = null;
      }

      protected abstract boolean check(boolean var1, Spatial var2, Spatial var3);
   }
}
