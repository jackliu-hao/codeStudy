/*    */ package freemarker.core;
/*    */ 
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
/*    */ public class _TimeZoneBuilder
/*    */ {
/*    */   private final String timeZoneId;
/*    */   
/*    */   public _TimeZoneBuilder(String timeZoneId) {
/* 32 */     this.timeZoneId = timeZoneId;
/*    */   }
/*    */   
/*    */   public TimeZone build() {
/* 36 */     TimeZone timeZone = TimeZone.getTimeZone(this.timeZoneId);
/* 37 */     if (timeZone.getID().equals("GMT") && !this.timeZoneId.equals("GMT") && !this.timeZoneId.equals("UTC") && 
/* 38 */       !this.timeZoneId.equals("GMT+00") && !this.timeZoneId.equals("GMT+00:00") && !this.timeZoneId.equals("GMT+0000")) {
/* 39 */       throw new IllegalArgumentException("Unrecognized time zone: " + this.timeZoneId);
/*    */     }
/* 41 */     return timeZone;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\_TimeZoneBuilder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */