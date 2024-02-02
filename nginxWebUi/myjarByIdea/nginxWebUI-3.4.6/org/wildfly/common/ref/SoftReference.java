package org.wildfly.common.ref;

import java.lang.ref.ReferenceQueue;

public class SoftReference<T, A> extends java.lang.ref.SoftReference<T> implements Reference<T, A>, Reapable<T, A> {
   private final A attachment;
   private final Reaper<T, A> reaper;

   public SoftReference(T referent) {
      this(referent, (Object)null, (ReferenceQueue)((ReferenceQueue)null));
   }

   public SoftReference(T referent, A attachment) {
      this(referent, attachment, (ReferenceQueue)null);
   }

   public SoftReference(T referent, A attachment, ReferenceQueue<? super T> q) {
      super(referent, q);
      this.reaper = null;
      this.attachment = attachment;
   }

   public SoftReference(T referent, A attachment, Reaper<T, A> reaper) {
      super(referent, References.ReaperThread.REAPER_QUEUE);
      this.reaper = reaper;
      this.attachment = attachment;
   }

   public Reaper<T, A> getReaper() {
      return this.reaper;
   }

   public A getAttachment() {
      return this.attachment;
   }

   public Reference.Type getType() {
      return Reference.Type.SOFT;
   }

   public String toString() {
      return "soft reference to " + String.valueOf(this.get());
   }
}
