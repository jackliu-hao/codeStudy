/*    */ package org.noear.solon.core.util;
/*    */ 
/*    */ import java.util.Objects;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RankEntity<T>
/*    */ {
/*    */   public final T target;
/*    */   public final int index;
/*    */   
/*    */   public RankEntity(T t, int i) {
/* 14 */     this.target = t;
/* 15 */     this.index = i;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 20 */     if (this == o) return true; 
/* 21 */     if (!(o instanceof RankEntity)) return false; 
/* 22 */     RankEntity that = (RankEntity)o;
/* 23 */     return Objects.equals(this.target, that.target);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 28 */     return Objects.hash(new Object[] { this.target });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\RankEntity.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */