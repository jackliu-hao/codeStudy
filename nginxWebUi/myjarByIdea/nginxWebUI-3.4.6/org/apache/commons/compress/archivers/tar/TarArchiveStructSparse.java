package org.apache.commons.compress.archivers.tar;

import java.util.Objects;

public final class TarArchiveStructSparse {
   private final long offset;
   private final long numbytes;

   public TarArchiveStructSparse(long offset, long numbytes) {
      if (offset < 0L) {
         throw new IllegalArgumentException("offset must not be negative");
      } else if (numbytes < 0L) {
         throw new IllegalArgumentException("numbytes must not be negative");
      } else {
         this.offset = offset;
         this.numbytes = numbytes;
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         TarArchiveStructSparse that = (TarArchiveStructSparse)o;
         return this.offset == that.offset && this.numbytes == that.numbytes;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.offset, this.numbytes});
   }

   public String toString() {
      return "TarArchiveStructSparse{offset=" + this.offset + ", numbytes=" + this.numbytes + '}';
   }

   public long getOffset() {
      return this.offset;
   }

   public long getNumbytes() {
      return this.numbytes;
   }
}
