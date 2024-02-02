/*    */ package cn.hutool.core.date.format;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Locale;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class AbstractDateBasic
/*    */   implements DateBasic, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 6333136319870641818L;
/*    */   protected final String pattern;
/*    */   protected final TimeZone timeZone;
/*    */   protected final Locale locale;
/*    */   
/*    */   protected AbstractDateBasic(String pattern, TimeZone timeZone, Locale locale) {
/* 24 */     this.pattern = pattern;
/* 25 */     this.timeZone = timeZone;
/* 26 */     this.locale = locale;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getPattern() {
/* 32 */     return this.pattern;
/*    */   }
/*    */ 
/*    */   
/*    */   public TimeZone getTimeZone() {
/* 37 */     return this.timeZone;
/*    */   }
/*    */ 
/*    */   
/*    */   public Locale getLocale() {
/* 42 */     return this.locale;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 48 */     if (!(obj instanceof FastDatePrinter)) {
/* 49 */       return false;
/*    */     }
/* 51 */     AbstractDateBasic other = (AbstractDateBasic)obj;
/* 52 */     return (this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 57 */     return this.pattern.hashCode() + 13 * (this.timeZone.hashCode() + 13 * this.locale.hashCode());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 62 */     return "FastDatePrinter[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\date\format\AbstractDateBasic.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */