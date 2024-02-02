package io.undertow.server.handlers.encoding;

import io.undertow.predicate.Predicate;

final class EncodingMapping implements Comparable<EncodingMapping> {
   private final String name;
   private final ContentEncodingProvider encoding;
   private final int priority;
   private final Predicate allowed;

   EncodingMapping(String name, ContentEncodingProvider encoding, int priority, Predicate allowed) {
      this.name = name;
      this.encoding = encoding;
      this.priority = priority;
      this.allowed = allowed;
   }

   public String getName() {
      return this.name;
   }

   public ContentEncodingProvider getEncoding() {
      return this.encoding;
   }

   public int getPriority() {
      return this.priority;
   }

   public Predicate getAllowed() {
      return this.allowed;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof EncodingMapping)) {
         return false;
      } else {
         EncodingMapping that = (EncodingMapping)o;
         return this.compareTo(that) == 0;
      }
   }

   public int hashCode() {
      return this.getPriority();
   }

   public int compareTo(EncodingMapping o) {
      return this.priority - o.priority;
   }
}
