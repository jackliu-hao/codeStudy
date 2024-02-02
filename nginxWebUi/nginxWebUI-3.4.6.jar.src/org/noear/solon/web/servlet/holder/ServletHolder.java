/*    */ package org.noear.solon.web.servlet.holder;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import javax.servlet.Servlet;
/*    */ import javax.servlet.annotation.WebServlet;
/*    */ 
/*    */ public class ServletHolder {
/*    */   public final WebServlet anno;
/*    */   public final Servlet servlet;
/*    */   
/*    */   public ServletHolder(WebServlet anno, Servlet servlet) {
/* 12 */     this.anno = anno;
/* 13 */     this.servlet = servlet;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 18 */     if (this == o) return true; 
/* 19 */     if (o == null || getClass() != o.getClass()) return false; 
/* 20 */     ServletHolder that = (ServletHolder)o;
/* 21 */     return (Objects.equals(this.anno, that.anno) && 
/* 22 */       Objects.equals(this.servlet, that.servlet));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 27 */     return Objects.hash(new Object[] { this.anno, this.servlet });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\web\servlet\holder\ServletHolder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */