package org.h2.mvstore.tx;

import java.util.BitSet;

final class VersionedBitSet extends BitSet {
   private static final long serialVersionUID = 1L;
   private long version;

   public VersionedBitSet() {
   }

   public long getVersion() {
      return this.version;
   }

   public void setVersion(long var1) {
      this.version = var1;
   }

   public VersionedBitSet clone() {
      return (VersionedBitSet)super.clone();
   }
}
