package org.noear.solon.web.servlet.holder;

import java.util.Objects;
import javax.servlet.Filter;
import javax.servlet.annotation.WebFilter;

public class FilterHodler {
   public final WebFilter anno;
   public final Filter filter;

   public FilterHodler(WebFilter anno, Filter filter) {
      this.anno = anno;
      this.filter = filter;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FilterHodler that = (FilterHodler)o;
         return Objects.equals(this.anno, that.anno) && Objects.equals(this.filter, that.filter);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.anno, this.filter});
   }
}
