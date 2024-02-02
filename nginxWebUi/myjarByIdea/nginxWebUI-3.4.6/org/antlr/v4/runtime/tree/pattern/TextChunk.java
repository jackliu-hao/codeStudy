package org.antlr.v4.runtime.tree.pattern;

class TextChunk extends Chunk {
   private final String text;

   public TextChunk(String text) {
      if (text == null) {
         throw new IllegalArgumentException("text cannot be null");
      } else {
         this.text = text;
      }
   }

   public final String getText() {
      return this.text;
   }

   public String toString() {
      return "'" + this.text + "'";
   }
}
