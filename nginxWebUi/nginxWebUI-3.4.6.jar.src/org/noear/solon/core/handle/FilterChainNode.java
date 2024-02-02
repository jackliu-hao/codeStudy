/*    */ package org.noear.solon.core.handle;
/*    */ 
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class FilterChainNode
/*    */   implements FilterChain
/*    */ {
/*    */   private final List<FilterEntity> filterList;
/*    */   private int index;
/*    */   
/*    */   public FilterChainNode(List<FilterEntity> filterList) {
/* 16 */     this.filterList = filterList;
/* 17 */     this.index = 0;
/*    */   }
/*    */ 
/*    */   
/*    */   public void doFilter(Context ctx) throws Throwable {
/* 22 */     ((FilterEntity)this.filterList.get(this.index++)).filter.doFilter(ctx, this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\handle\FilterChainNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */