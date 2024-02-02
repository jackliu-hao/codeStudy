package org.noear.solon.core.handle;

import java.util.List;

public class FilterChainNode implements FilterChain {
   private final List<FilterEntity> filterList;
   private int index;

   public FilterChainNode(List<FilterEntity> filterList) {
      this.filterList = filterList;
      this.index = 0;
   }

   public void doFilter(Context ctx) throws Throwable {
      ((FilterEntity)this.filterList.get(this.index++)).filter.doFilter(ctx, this);
   }
}
