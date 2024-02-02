/*    */ package freemarker.template;
/*    */ 
/*    */ import java.sql.Date;
/*    */ import java.sql.Time;
/*    */ import java.sql.Timestamp;
/*    */ import java.util.Date;
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
/*    */ public class SimpleDate
/*    */   implements TemplateDateModel
/*    */ {
/*    */   private final Date date;
/*    */   private final int type;
/*    */   
/*    */   public SimpleDate(Date date) {
/* 36 */     this(date, 2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SimpleDate(Time time) {
/* 44 */     this(time, 1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SimpleDate(Timestamp datetime) {
/* 52 */     this(datetime, 3);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SimpleDate(Date date, int type) {
/* 60 */     if (date == null) {
/* 61 */       throw new IllegalArgumentException("date == null");
/*    */     }
/* 63 */     this.date = date;
/* 64 */     this.type = type;
/*    */   }
/*    */ 
/*    */   
/*    */   public Date getAsDate() {
/* 69 */     return this.date;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getDateType() {
/* 74 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 79 */     return this.date.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\SimpleDate.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */