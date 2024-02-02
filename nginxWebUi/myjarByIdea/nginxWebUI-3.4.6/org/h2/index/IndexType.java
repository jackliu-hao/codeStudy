package org.h2.index;

public class IndexType {
   private boolean primaryKey;
   private boolean persistent;
   private boolean unique;
   private boolean hash;
   private boolean scan;
   private boolean spatial;
   private boolean belongsToConstraint;

   public static IndexType createPrimaryKey(boolean var0, boolean var1) {
      IndexType var2 = new IndexType();
      var2.primaryKey = true;
      var2.persistent = var0;
      var2.hash = var1;
      var2.unique = true;
      return var2;
   }

   public static IndexType createUnique(boolean var0, boolean var1) {
      IndexType var2 = new IndexType();
      var2.unique = true;
      var2.persistent = var0;
      var2.hash = var1;
      return var2;
   }

   public static IndexType createNonUnique(boolean var0) {
      return createNonUnique(var0, false, false);
   }

   public static IndexType createNonUnique(boolean var0, boolean var1, boolean var2) {
      IndexType var3 = new IndexType();
      var3.persistent = var0;
      var3.hash = var1;
      var3.spatial = var2;
      return var3;
   }

   public static IndexType createScan(boolean var0) {
      IndexType var1 = new IndexType();
      var1.persistent = var0;
      var1.scan = true;
      return var1;
   }

   public void setBelongsToConstraint(boolean var1) {
      this.belongsToConstraint = var1;
   }

   public boolean getBelongsToConstraint() {
      return this.belongsToConstraint;
   }

   public boolean isHash() {
      return this.hash;
   }

   public boolean isSpatial() {
      return this.spatial;
   }

   public boolean isPersistent() {
      return this.persistent;
   }

   public boolean isPrimaryKey() {
      return this.primaryKey;
   }

   public boolean isUnique() {
      return this.unique;
   }

   public String getSQL() {
      StringBuilder var1 = new StringBuilder();
      if (this.primaryKey) {
         var1.append("PRIMARY KEY");
         if (this.hash) {
            var1.append(" HASH");
         }
      } else {
         if (this.unique) {
            var1.append("UNIQUE ");
         }

         if (this.hash) {
            var1.append("HASH ");
         }

         if (this.spatial) {
            var1.append("SPATIAL ");
         }

         var1.append("INDEX");
      }

      return var1.toString();
   }

   public boolean isScan() {
      return this.scan;
   }
}
