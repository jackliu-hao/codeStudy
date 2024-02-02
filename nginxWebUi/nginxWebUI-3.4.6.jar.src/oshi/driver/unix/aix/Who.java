/*    */ package oshi.driver.unix.aix;
/*    */ 
/*    */ import java.time.LocalDateTime;
/*    */ import java.time.ZoneId;
/*    */ import java.time.format.DateTimeFormatter;
/*    */ import java.time.format.DateTimeParseException;
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.ExecutingCommand;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @ThreadSafe
/*    */ public final class Who
/*    */ {
/* 44 */   private static final Pattern BOOT_FORMAT_AIX = Pattern.compile("\\D+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}).*");
/* 45 */   private static final DateTimeFormatter BOOT_DATE_FORMAT_AIX = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static long queryBootTime() {
/* 56 */     String s = ExecutingCommand.getFirstAnswer("who -b");
/* 57 */     Matcher m = BOOT_FORMAT_AIX.matcher(s);
/* 58 */     if (m.matches()) {
/*    */       try {
/* 60 */         return LocalDateTime.parse(m.group(1) + " " + m.group(2), BOOT_DATE_FORMAT_AIX)
/* 61 */           .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
/* 62 */       } catch (DateTimeParseException|NullPointerException dateTimeParseException) {}
/*    */     }
/*    */ 
/*    */     
/* 66 */     return 0L;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\aix\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */