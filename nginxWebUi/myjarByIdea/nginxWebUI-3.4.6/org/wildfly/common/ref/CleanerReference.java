package org.wildfly.common.ref;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CleanerReference<T, A> extends PhantomReference<T, A> {
   private static final Set<CleanerReference<?, ?>> set = Collections.newSetFromMap(new ConcurrentHashMap());

   public CleanerReference(T referent, A attachment, Reaper<T, A> reaper) {
      super(referent, attachment, reaper);
      set.add(this);
   }

   void clean() {
      set.remove(this);
   }

   public final int hashCode() {
      return super.hashCode();
   }

   public final boolean equals(Object obj) {
      return super.equals(obj);
   }
}
