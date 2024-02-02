package org.wildfly.common.ref;

import java.lang.ref.ReferenceQueue;

public class WeakReference<T, A> extends java.lang.ref.WeakReference<T> implements Reference<T, A>, Reapable<T, A> {
   private final A attachment;
   private final Reaper<T, A> reaper;

   public WeakReference(T referent) {
      this(referent, (Object)null, (Reaper)((Reaper)null));
   }

   public WeakReference(T referent, A attachment) {
      this(referent, attachment, (Reaper)null);
   }

   public WeakReference(T referent, A attachment, ReferenceQueue<? super T> q) {
      super(referent, q);
      this.attachment = attachment;
      this.reaper = null;
   }

   public WeakReference(T referent, A attachment, Reaper<T, A> reaper) {
      super(referent, References.ReaperThread.REAPER_QUEUE);
      this.attachment = attachment;
      this.reaper = reaper;
   }

   public A getAttachment() {
      return this.attachment;
   }

   public Reference.Type getType() {
      return Reference.Type.WEAK;
   }

   public Reaper<T, A> getReaper() {
      return this.reaper;
   }

   public String toString() {
      return "weak reference to " + String.valueOf(this.get());
   }
}
