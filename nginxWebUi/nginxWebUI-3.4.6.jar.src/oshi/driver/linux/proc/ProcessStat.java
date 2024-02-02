/*     */ package oshi.driver.linux.proc;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.stream.Collectors;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.software.os.OSProcess;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
/*     */ import oshi.util.tuples.Triplet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class ProcessStat
/*     */ {
/*  55 */   private static final Pattern DIGITS = Pattern.compile("\\d+");
/*     */ 
/*     */   
/*     */   public static final int PROC_PID_STAT_LENGTH;
/*     */ 
/*     */ 
/*     */   
/*     */   public enum PidStat
/*     */   {
/*  64 */     PID,
/*     */ 
/*     */ 
/*     */     
/*  68 */     COMM,
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
/*  96 */     STATE,
/*     */ 
/*     */ 
/*     */     
/* 100 */     PPID,
/*     */ 
/*     */ 
/*     */     
/* 104 */     PGRP,
/*     */ 
/*     */ 
/*     */     
/* 108 */     SESSION,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 114 */     TTY_NR,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 119 */     PTGID,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 125 */     FLAGS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 130 */     MINFLT,
/*     */ 
/*     */ 
/*     */     
/* 134 */     CMINFLT,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 139 */     MAJFLT,
/*     */ 
/*     */ 
/*     */     
/* 143 */     CMAJFLT,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     UTIME,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 155 */     STIME,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 161 */     CUTIME,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 166 */     CSTIME,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 176 */     PRIORITY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 181 */     NICE,
/*     */ 
/*     */ 
/*     */     
/* 185 */     NUM_THREADS,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 191 */     ITREALVALUE,
/*     */ 
/*     */ 
/*     */     
/* 195 */     STARTTIME,
/*     */ 
/*     */ 
/*     */     
/* 199 */     VSIZE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 205 */     RSS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 210 */     RSSLIM,
/*     */ 
/*     */ 
/*     */     
/* 214 */     STARTCODE,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 219 */     ENDCODE,
/*     */ 
/*     */ 
/*     */     
/* 223 */     STARTSTACK,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 228 */     KSTKESP,
/*     */ 
/*     */ 
/*     */     
/* 232 */     KSTKEIP,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 238 */     SIGNAL,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 244 */     BLOCKED,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 250 */     SIGIGNORE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 256 */     SIGCATCH,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 262 */     WCHAN,
/*     */ 
/*     */ 
/*     */     
/* 266 */     NSWAP,
/*     */ 
/*     */ 
/*     */     
/* 270 */     CNSWAP,
/*     */ 
/*     */ 
/*     */     
/* 274 */     EXIT_SIGNAL,
/*     */ 
/*     */ 
/*     */     
/* 278 */     PROCESSOR,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 284 */     RT_PRIORITY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 289 */     POLICY,
/*     */ 
/*     */ 
/*     */     
/* 293 */     DELAYACCT_BLKIO_TICKS,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 298 */     GUEST_TIME,
/*     */ 
/*     */ 
/*     */     
/* 302 */     CGUEST_TIME,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 307 */     START_DATA,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 312 */     END_DATA,
/*     */ 
/*     */ 
/*     */     
/* 316 */     START_BRK,
/*     */ 
/*     */ 
/*     */     
/* 320 */     ARG_START,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 325 */     ARG_END,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 330 */     ENV_START,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 335 */     ENV_END,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 340 */     EXIT_CODE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public enum PidStatM
/*     */   {
/* 350 */     SIZE,
/*     */ 
/*     */ 
/*     */     
/* 354 */     RESIDENT,
/*     */ 
/*     */ 
/*     */     
/* 358 */     SHARED,
/*     */ 
/*     */ 
/*     */     
/* 362 */     TEXT,
/*     */ 
/*     */ 
/*     */     
/* 366 */     LIB,
/*     */ 
/*     */ 
/*     */     
/* 370 */     DATA,
/*     */ 
/*     */ 
/*     */     
/* 374 */     DT;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static {
/* 383 */     String stat = FileUtil.getStringFromFile(ProcPath.SELF_STAT);
/* 384 */     if (!stat.isEmpty() && stat.contains(")")) {
/*     */       
/* 386 */       PROC_PID_STAT_LENGTH = ParseUtil.countStringToLongArray(stat, ' ') + 3;
/*     */     } else {
/*     */       
/* 389 */       PROC_PID_STAT_LENGTH = 52;
/*     */     } 
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Triplet<String, Character, Map<PidStat, Long>> getPidStats(int pid) {
/* 409 */     String stat = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, new Object[] { Integer.valueOf(pid) }));
/* 410 */     if (stat.isEmpty())
/*     */     {
/* 412 */       return null;
/*     */     }
/*     */     
/* 415 */     int nameStart = stat.indexOf('(') + 1;
/* 416 */     int nameEnd = stat.indexOf(')');
/* 417 */     String name = stat.substring(nameStart, nameEnd);
/* 418 */     Character state = Character.valueOf(stat.charAt(nameEnd + 2));
/*     */     
/* 420 */     String[] split = ParseUtil.whitespaces.split(stat.substring(nameEnd + 4).trim());
/*     */     
/* 422 */     Map<PidStat, Long> statMap = new EnumMap<>(PidStat.class);
/* 423 */     PidStat[] enumArray = PidStat.class.getEnumConstants();
/* 424 */     for (int i = 3; i < enumArray.length && i - 3 < split.length; i++) {
/* 425 */       statMap.put(enumArray[i], Long.valueOf(ParseUtil.parseLongOrDefault(split[i - 3], 0L)));
/*     */     }
/* 427 */     return new Triplet(name, state, statMap);
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
/*     */   
/*     */   public static Map<PidStatM, Long> getPidStatM(int pid) {
/* 441 */     String statm = FileUtil.getStringFromFile(String.format(ProcPath.PID_STATM, new Object[] { Integer.valueOf(pid) }));
/* 442 */     if (statm.isEmpty())
/*     */     {
/* 444 */       return null;
/*     */     }
/*     */     
/* 447 */     String[] split = ParseUtil.whitespaces.split(statm);
/*     */     
/* 449 */     Map<PidStatM, Long> statmMap = new EnumMap<>(PidStatM.class);
/* 450 */     PidStatM[] enumArray = PidStatM.class.getEnumConstants();
/* 451 */     for (int i = 0; i < enumArray.length && i < split.length; i++) {
/* 452 */       statmMap.put(enumArray[i], Long.valueOf(ParseUtil.parseLongOrDefault(split[i], 0L)));
/*     */     }
/* 454 */     return statmMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static File[] getPidFiles() {
/* 464 */     File procdir = new File(ProcPath.PROC);
/* 465 */     File[] pids = procdir.listFiles(f -> DIGITS.matcher(f.getName()).matches());
/* 466 */     return (pids != null) ? pids : new File[0];
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
/*     */   public static List<Integer> getThreadIds(int pid) {
/* 478 */     File threadDir = new File(String.format(ProcPath.TASK_PATH, new Object[] { Integer.valueOf(pid) }));
/*     */     
/* 480 */     File[] threads = threadDir.listFiles(file -> (DIGITS.matcher(file.getName()).matches() && Integer.valueOf(file.getName()).intValue() != pid));
/* 481 */     return (threads != null) ? (List<Integer>)Arrays.<File>stream(
/* 482 */         threads).map(thread -> Integer.valueOf(ParseUtil.parseIntOrDefault(thread.getName(), 0))).collect(Collectors.toList()) : 
/* 483 */       Collections.<Integer>emptyList();
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
/*     */   public static OSProcess.State getState(char stateValue) {
/* 496 */     switch (stateValue)
/*     */     { case 'R':
/* 498 */         state = OSProcess.State.RUNNING;
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
/* 516 */         return state;case 'S': state = OSProcess.State.SLEEPING; return state;case 'D': state = OSProcess.State.WAITING; return state;case 'Z': state = OSProcess.State.ZOMBIE; return state;case 'T': state = OSProcess.State.STOPPED; return state; }  OSProcess.State state = OSProcess.State.OTHER; return state;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\proc\ProcessStat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */