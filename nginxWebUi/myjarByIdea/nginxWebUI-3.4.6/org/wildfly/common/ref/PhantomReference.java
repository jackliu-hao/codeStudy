package org.wildfly.common.ref;

import java.lang.ref.ReferenceQueue;

public class PhantomReference<T, A> extends java.lang.ref.PhantomReference<T> implements Reference<T, A>, Reapable<T, A> {
   private final A attachment;
   private final Reaper<T, A> reaper;

   public PhantomReference(T referent, A attachment, ReferenceQueue<? super T> q) {
      super(referent, q);
      this.attachment = attachment;
      this.reaper = null;
   }

   public PhantomReference(T referent, A attachment, Reaper<T, A> reaper) {
      super(referent, References.ReaperThread.REAPER_QUEUE);
      this.reaper = reaper;
      this.attachment = attachment;
   }

   public A getAttachment() {
      return this.attachment;
   }

   public Reference.Type getType() {
      return Reference.Type.PHANTOM;
   }

   public Reaper<T, A> getReaper() {
      return this.reaper;
   }

   public String toString() {
      return "phantom reference";
   }
}
