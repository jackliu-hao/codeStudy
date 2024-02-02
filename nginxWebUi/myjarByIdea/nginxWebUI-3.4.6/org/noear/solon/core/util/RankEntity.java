package org.noear.solon.core.util;

import java.util.Objects;

public class RankEntity<T> {
   public final T target;
   public final int index;

   public RankEntity(T t, int i) {
      this.target = t;
      this.index = i;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof RankEntity)) {
         return false;
      } else {
         RankEntity that = (RankEntity)o;
         return Objects.equals(this.target, that.target);
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.target});
   }
}
