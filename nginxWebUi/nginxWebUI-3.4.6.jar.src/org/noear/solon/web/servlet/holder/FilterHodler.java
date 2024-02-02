/*    */ package org.noear.solon.web.servlet.holder;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import javax.servlet.Filter;
/*    */ import javax.servlet.annotation.WebFilter;
/*    */ 
/*    */ public class FilterHodler {
/*    */   public final WebFilter anno;
/*    */   public final Filter filter;
/*    */   
/*    */   public FilterHodler(WebFilter anno, Filter filter) {
/* 12 */     this.anno = anno;
/* 13 */     this.filter = filter;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 18 */     if (this == o) return true; 
/* 19 */     if (o == null || getClass() != o.getClass()) return false; 
/* 20 */     FilterHodler that = (FilterHodler)o;
/* 21 */     return (Objects.equals(this.anno, that.anno) && 
/* 22 */       Objects.equals(this.filter, that.filter));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 27 */     return Objects.hash(new Object[] { this.anno, this.filter });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\holder\FilterHodler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */