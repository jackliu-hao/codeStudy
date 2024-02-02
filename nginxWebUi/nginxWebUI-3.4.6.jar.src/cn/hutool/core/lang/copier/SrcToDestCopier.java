/*    */ package cn.hutool.core.lang.copier;
/*    */ 
/*    */ import cn.hutool.core.lang.Filter;
/*    */ import java.io.Serializable;
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
/*    */ 
/*    */ public abstract class SrcToDestCopier<T, C extends SrcToDestCopier<T, C>>
/*    */   implements Copier<T>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected T src;
/*    */   protected T dest;
/*    */   protected Filter<T> copyFilter;
/*    */   
/*    */   public T getSrc() {
/* 33 */     return this.src;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public C setSrc(T src) {
/* 43 */     this.src = src;
/* 44 */     return (C)this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public T getDest() {
/* 53 */     return this.dest;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public C setDest(T dest) {
/* 63 */     this.dest = dest;
/* 64 */     return (C)this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Filter<T> getCopyFilter() {
/* 72 */     return this.copyFilter;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public C setCopyFilter(Filter<T> copyFilter) {
/* 82 */     this.copyFilter = copyFilter;
/* 83 */     return (C)this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\copier\SrcToDestCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */