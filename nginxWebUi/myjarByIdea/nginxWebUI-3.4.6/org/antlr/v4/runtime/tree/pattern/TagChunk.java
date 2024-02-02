package org.antlr.v4.runtime.tree.pattern;

class TagChunk extends Chunk {
   private final String tag;
   private final String label;

   public TagChunk(String tag) {
      this((String)null, tag);
   }

   public TagChunk(String label, String tag) {
      if (tag != null && !tag.isEmpty()) {
         this.label = label;
         this.tag = tag;
      } else {
         throw new IllegalArgumentException("tag cannot be null or empty");
      }
   }

   public final String getTag() {
      return this.tag;
   }

   public final String getLabel() {
      return this.label;
   }

   public String toString() {
      return this.label != null ? this.label + ":" + this.tag : this.tag;
   }
}
