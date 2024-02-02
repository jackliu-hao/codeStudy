package io.undertow.server;

import java.util.TreeSet;

final class OverridableTreeSet<T> extends TreeSet<T> {
   public boolean add(T o) {
      super.remove(o);
      super.add(o);
      return true;
   }
}
