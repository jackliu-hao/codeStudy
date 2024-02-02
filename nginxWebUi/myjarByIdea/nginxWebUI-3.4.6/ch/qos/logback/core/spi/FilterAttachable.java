package ch.qos.logback.core.spi;

import ch.qos.logback.core.filter.Filter;
import java.util.List;

public interface FilterAttachable<E> {
   void addFilter(Filter<E> var1);

   void clearAllFilters();

   List<Filter<E>> getCopyOfAttachedFiltersList();

   FilterReply getFilterChainDecision(E var1);
}
