/*     */ package oshi.driver.unix;
/*     */ 
/*     */ import java.time.LocalDateTime;
/*     */ import java.time.Year;
/*     */ import java.time.ZoneId;
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeFormatterBuilder;
/*     */ import java.time.format.DateTimeParseException;
/*     */ import java.time.temporal.ChronoField;
/*     */ import java.time.temporal.ChronoUnit;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.os.OSSession;
/*     */ import oshi.util.ExecutingCommand;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class Who
/*     */ {
/*  54 */   private static final Pattern WHO_FORMAT_LINUX = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2})\\s*(?:\\((.+)\\))?");
/*  55 */   private static final DateTimeFormatter WHO_DATE_FORMAT_LINUX = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
/*     */ 
/*     */ 
/*     */   
/*  59 */   private static final Pattern WHO_FORMAT_UNIX = Pattern.compile("(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\d+)\\s+(\\d{2}:\\d{2})\\s*(?:\\((.+)\\))?");
/*  60 */   private static final DateTimeFormatter WHO_DATE_FORMAT_UNIX = (new DateTimeFormatterBuilder())
/*  61 */     .appendPattern("MMM d HH:mm").parseDefaulting(ChronoField.YEAR, Year.now().getValue())
/*  62 */     .toFormatter(Locale.US);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized List<OSSession> queryWho() {
/*  73 */     List<OSSession> whoList = new ArrayList<>();
/*  74 */     List<String> who = ExecutingCommand.runNative("who");
/*  75 */     for (String s : who) {
/*  76 */       Matcher m = WHO_FORMAT_LINUX.matcher(s);
/*  77 */       if (m.matches()) {
/*     */         try {
/*  79 */           whoList.add(new OSSession(m.group(1), m.group(2), 
/*  80 */                 LocalDateTime.parse(m.group(3) + " " + m.group(4), WHO_DATE_FORMAT_LINUX)
/*  81 */                 .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), 
/*  82 */                 (m.group(5) == null) ? "unknown" : m.group(5)));
/*  83 */         } catch (DateTimeParseException|NullPointerException dateTimeParseException) {}
/*     */         
/*     */         continue;
/*     */       } 
/*  87 */       m = WHO_FORMAT_UNIX.matcher(s);
/*  88 */       if (m.matches()) {
/*     */         
/*     */         try {
/*  91 */           LocalDateTime login = LocalDateTime.parse(m.group(3) + " " + m.group(4) + " " + m.group(5), WHO_DATE_FORMAT_UNIX);
/*     */ 
/*     */           
/*  94 */           if (login.isAfter(LocalDateTime.now())) {
/*  95 */             login = login.minus(1L, ChronoUnit.YEARS);
/*     */           }
/*  97 */           long millis = login.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
/*  98 */           whoList.add(new OSSession(m
/*  99 */                 .group(1), m.group(2), millis, (m.group(6) == null) ? "" : m.group(6)));
/* 100 */         } catch (DateTimeParseException|NullPointerException dateTimeParseException) {}
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 106 */     return whoList;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\drive\\unix\Who.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */