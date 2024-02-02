package org.wildfly.common.ref;

public class StrongReference<T, A> implements Reference<T, A> {
   private volatile T referent;
   private final A attachment;

   public StrongReference(T referent, A attachment) {
      this.referent = referent;
      this.attachment = attachment;
   }

   public StrongReference(T referent) {
      this(referent, (Object)null);
   }

   public T get() {
      return this.referent;
   }

   public void clear() {
      this.referent = null;
   }

   public A getAttachment() {
      return this.attachment;
   }

   public Reference.Type getType() {
      return Reference.Type.STRONG;
   }

   public String toString() {
      return "strong reference to " + String.valueOf(this.get());
   }
}
