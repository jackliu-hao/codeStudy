/*     */ package ch.qos.logback.core.status;
/*     */ 
/*     */ import ch.qos.logback.core.Context;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public class StatusUtil
/*     */ {
/*     */   StatusManager sm;
/*     */   
/*     */   public StatusUtil(StatusManager sm) {
/*  30 */     this.sm = sm;
/*     */   }
/*     */   
/*     */   public StatusUtil(Context context) {
/*  34 */     this.sm = context.getStatusManager();
/*     */   }
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
/*     */   public static boolean contextHasStatusListener(Context context) {
/*  47 */     StatusManager sm = context.getStatusManager();
/*  48 */     if (sm == null)
/*  49 */       return false; 
/*  50 */     List<StatusListener> listeners = sm.getCopyOfStatusListenerList();
/*  51 */     if (listeners == null || listeners.size() == 0) {
/*  52 */       return false;
/*     */     }
/*  54 */     return true;
/*     */   }
/*     */   
/*     */   public static List<Status> filterStatusListByTimeThreshold(List<Status> rawList, long threshold) {
/*  58 */     List<Status> filteredList = new ArrayList<Status>();
/*  59 */     for (Status s : rawList) {
/*  60 */       if (s.getDate().longValue() >= threshold)
/*  61 */         filteredList.add(s); 
/*     */     } 
/*  63 */     return filteredList;
/*     */   }
/*     */   
/*     */   public void addStatus(Status status) {
/*  67 */     if (this.sm != null) {
/*  68 */       this.sm.add(status);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addInfo(Object caller, String msg) {
/*  73 */     addStatus(new InfoStatus(msg, caller));
/*     */   }
/*     */   
/*     */   public void addWarn(Object caller, String msg) {
/*  77 */     addStatus(new WarnStatus(msg, caller));
/*     */   }
/*     */   
/*     */   public void addError(Object caller, String msg, Throwable t) {
/*  81 */     addStatus(new ErrorStatus(msg, caller, t));
/*     */   }
/*     */   
/*     */   public boolean hasXMLParsingErrors(long threshold) {
/*  85 */     return containsMatch(threshold, 2, "XML_PARSING");
/*     */   }
/*     */   
/*     */   public boolean noXMLParsingErrorsOccurred(long threshold) {
/*  89 */     return !hasXMLParsingErrors(threshold);
/*     */   }
/*     */   
/*     */   public int getHighestLevel(long threshold) {
/*  93 */     List<Status> filteredList = filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
/*  94 */     int maxLevel = 0;
/*  95 */     for (Status s : filteredList) {
/*  96 */       if (s.getLevel() > maxLevel)
/*  97 */         maxLevel = s.getLevel(); 
/*     */     } 
/*  99 */     return maxLevel;
/*     */   }
/*     */   
/*     */   public boolean isErrorFree(long threshold) {
/* 103 */     return (2 > getHighestLevel(threshold));
/*     */   }
/*     */   
/*     */   public boolean isWarningOrErrorFree(long threshold) {
/* 107 */     return (1 > getHighestLevel(threshold));
/*     */   }
/*     */   
/*     */   public boolean containsMatch(long threshold, int level, String regex) {
/* 111 */     List<Status> filteredList = filterStatusListByTimeThreshold(this.sm.getCopyOfStatusList(), threshold);
/* 112 */     Pattern p = Pattern.compile(regex);
/*     */     
/* 114 */     for (Status status : filteredList) {
/* 115 */       if (level != status.getLevel()) {
/*     */         continue;
/*     */       }
/* 118 */       String msg = status.getMessage();
/* 119 */       Matcher matcher = p.matcher(msg);
/* 120 */       if (matcher.lookingAt()) {
/* 121 */         return true;
/*     */       }
/*     */     } 
/* 124 */     return false;
/*     */   }
/*     */   
/*     */   public boolean containsMatch(int level, String regex) {
/* 128 */     return containsMatch(0L, level, regex);
/*     */   }
/*     */   
/*     */   public boolean containsMatch(String regex) {
/* 132 */     Pattern p = Pattern.compile(regex);
/* 133 */     for (Status status : this.sm.getCopyOfStatusList()) {
/* 134 */       String msg = status.getMessage();
/* 135 */       Matcher matcher = p.matcher(msg);
/* 136 */       if (matcher.lookingAt()) {
/* 137 */         return true;
/*     */       }
/*     */     } 
/* 140 */     return false;
/*     */   }
/*     */   
/*     */   public int matchCount(String regex) {
/* 144 */     int count = 0;
/* 145 */     Pattern p = Pattern.compile(regex);
/* 146 */     for (Status status : this.sm.getCopyOfStatusList()) {
/* 147 */       String msg = status.getMessage();
/* 148 */       Matcher matcher = p.matcher(msg);
/* 149 */       if (matcher.lookingAt()) {
/* 150 */         count++;
/*     */       }
/*     */     } 
/* 153 */     return count;
/*     */   }
/*     */   
/*     */   public boolean containsException(Class<?> exceptionType) {
/* 157 */     Iterator<Status> stati = this.sm.getCopyOfStatusList().iterator();
/* 158 */     while (stati.hasNext()) {
/* 159 */       Status status = stati.next();
/* 160 */       Throwable t = status.getThrowable();
/* 161 */       while (t != null) {
/* 162 */         if (t.getClass().getName().equals(exceptionType.getName())) {
/* 163 */           return true;
/*     */         }
/* 165 */         t = t.getCause();
/*     */       } 
/*     */     } 
/* 168 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long timeOfLastReset() {
/* 177 */     List<Status> statusList = this.sm.getCopyOfStatusList();
/* 178 */     if (statusList == null) {
/* 179 */       return -1L;
/*     */     }
/* 181 */     int len = statusList.size();
/* 182 */     for (int i = len - 1; i >= 0; i--) {
/* 183 */       Status s = statusList.get(i);
/* 184 */       if ("Will reset and reconfigure context ".equals(s.getMessage())) {
/* 185 */         return s.getDate().longValue();
/*     */       }
/*     */     } 
/* 188 */     return -1L;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\status\StatusUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */