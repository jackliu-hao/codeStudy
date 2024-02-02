package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.util.COWArrayList;
import java.util.ArrayList;
import java.util.List;

public final class FilterAttachableImpl<E> implements FilterAttachable<E> {
   COWArrayList<Filter<E>> filterList = new COWArrayList(new Filter[0]);

   public void addFilter(Filter<E> newFilter) {
      this.filterList.add(newFilter);
   }

   public void clearAllFilters() {
      this.filterList.clear();
   }

   public FilterReply getFilterChainDecision(E event) {
      Filter<E>[] filterArrray = (Filter[])this.filterList.asTypedArray();
      int len = filterArrray.length;

      for(int i = 0; i < len; ++i) {
         FilterReply r = filterArrray[i].decide(event);
         if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
            return r;
         }
      }

      return FilterReply.NEUTRAL;
   }

   public List<Filter<E>> getCopyOfAttachedFiltersList() {
      return new ArrayList(this.filterList);
   }
}
