/*    */ package ch.qos.logback.core.spi;
/*    */ 
/*    */ import ch.qos.logback.core.filter.Filter;
/*    */ import ch.qos.logback.core.util.COWArrayList;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class FilterAttachableImpl<E>
/*    */   implements FilterAttachable<E>
/*    */ {
/* 29 */   COWArrayList<Filter<E>> filterList = new COWArrayList((Object[])new Filter[0]);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void addFilter(Filter<E> newFilter) {
/* 36 */     this.filterList.add(newFilter);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void clearAllFilters() {
/* 43 */     this.filterList.clear();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FilterReply getFilterChainDecision(E event) {
/* 53 */     Filter[] arrayOfFilter = (Filter[])this.filterList.asTypedArray();
/* 54 */     int len = arrayOfFilter.length;
/*    */     
/* 56 */     for (int i = 0; i < len; i++) {
/* 57 */       FilterReply r = arrayOfFilter[i].decide(event);
/* 58 */       if (r == FilterReply.DENY || r == FilterReply.ACCEPT) {
/* 59 */         return r;
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 64 */     return FilterReply.NEUTRAL;
/*    */   }
/*    */   
/*    */   public List<Filter<E>> getCopyOfAttachedFiltersList() {
/* 68 */     return new ArrayList<Filter<E>>((Collection<? extends Filter<E>>)this.filterList);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\FilterAttachableImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */