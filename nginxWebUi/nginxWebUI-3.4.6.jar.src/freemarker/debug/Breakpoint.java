/*    */ package freemarker.debug;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Breakpoint
/*    */   implements Serializable, Comparable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final String templateName;
/*    */   private final int line;
/*    */   
/*    */   public Breakpoint(String templateName, int line) {
/* 39 */     this.templateName = templateName;
/* 40 */     this.line = line;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLine() {
/* 47 */     return this.line;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getTemplateName() {
/* 53 */     return this.templateName;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 58 */     return this.templateName.hashCode() + 31 * this.line;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 63 */     if (o instanceof Breakpoint) {
/* 64 */       Breakpoint b = (Breakpoint)o;
/* 65 */       return (b.templateName.equals(this.templateName) && b.line == this.line);
/*    */     } 
/* 67 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int compareTo(Object o) {
/* 72 */     Breakpoint b = (Breakpoint)o;
/* 73 */     int r = this.templateName.compareTo(b.templateName);
/* 74 */     return (r == 0) ? (this.line - b.line) : r;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getLocationString() {
/* 81 */     return this.templateName + ":" + this.line;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\debug\Breakpoint.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */