package oshi.driver.linux.proc;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.software.os.OSProcess;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Triplet;

@ThreadSafe
public final class ProcessStat {
   private static final Pattern DIGITS = Pattern.compile("\\d+");
   public static final int PROC_PID_STAT_LENGTH;

   private ProcessStat() {
   }

   public static Triplet<String, Character, Map<PidStat, Long>> getPidStats(int pid) {
      String stat = FileUtil.getStringFromFile(String.format(ProcPath.PID_STAT, pid));
      if (stat.isEmpty()) {
         return null;
      } else {
         int nameStart = stat.indexOf(40) + 1;
         int nameEnd = stat.indexOf(41);
         String name = stat.substring(nameStart, nameEnd);
         Character state = stat.charAt(nameEnd + 2);
         String[] split = ParseUtil.whitespaces.split(stat.substring(nameEnd + 4).trim());
         Map<PidStat, Long> statMap = new EnumMap(PidStat.class);
         PidStat[] enumArray = (PidStat[])PidStat.class.getEnumConstants();

         for(int i = 3; i < enumArray.length && i - 3 < split.length; ++i) {
            statMap.put(enumArray[i], ParseUtil.parseLongOrDefault(split[i - 3], 0L));
         }

         return new Triplet(name, state, statMap);
      }
   }

   public static Map<PidStatM, Long> getPidStatM(int pid) {
      String statm = FileUtil.getStringFromFile(String.format(ProcPath.PID_STATM, pid));
      if (statm.isEmpty()) {
         return null;
      } else {
         String[] split = ParseUtil.whitespaces.split(statm);
         Map<PidStatM, Long> statmMap = new EnumMap(PidStatM.class);
         PidStatM[] enumArray = (PidStatM[])PidStatM.class.getEnumConstants();

         for(int i = 0; i < enumArray.length && i < split.length; ++i) {
            statmMap.put(enumArray[i], ParseUtil.parseLongOrDefault(split[i], 0L));
         }

         return statmMap;
      }
   }

   public static File[] getPidFiles() {
      File procdir = new File(ProcPath.PROC);
      File[] pids = procdir.listFiles((f) -> {
         return DIGITS.matcher(f.getName()).matches();
      });
      return pids != null ? pids : new File[0];
   }

   public static List<Integer> getThreadIds(int pid) {
      File threadDir = new File(String.format(ProcPath.TASK_PATH, pid));
      File[] threads = threadDir.listFiles((file) -> {
         return DIGITS.matcher(file.getName()).matches() && Integer.valueOf(file.getName()) != pid;
      });
      return threads != null ? (List)Arrays.stream(threads).map((thread) -> {
         return ParseUtil.parseIntOrDefault(thread.getName(), 0);
      }).collect(Collectors.toList()) : Collections.emptyList();
   }

   public static OSProcess.State getState(char stateValue) {
      OSProcess.State state;
      switch (stateValue) {
         case 'D':
            state = OSProcess.State.WAITING;
            break;
         case 'R':
            state = OSProcess.State.RUNNING;
            break;
         case 'S':
            state = OSProcess.State.SLEEPING;
            break;
         case 'T':
            state = OSProcess.State.STOPPED;
            break;
         case 'Z':
            state = OSProcess.State.ZOMBIE;
            break;
         default:
            state = OSProcess.State.OTHER;
      }

      return state;
   }

   static {
      String stat = FileUtil.getStringFromFile(ProcPath.SELF_STAT);
      if (!stat.isEmpty() && stat.contains(")")) {
         PROC_PID_STAT_LENGTH = ParseUtil.countStringToLongArray(stat, ' ') + 3;
      } else {
         PROC_PID_STAT_LENGTH = 52;
      }

   }

   public static enum PidStat {
      PID,
      COMM,
      STATE,
      PPID,
      PGRP,
      SESSION,
      TTY_NR,
      PTGID,
      FLAGS,
      MINFLT,
      CMINFLT,
      MAJFLT,
      CMAJFLT,
      UTIME,
      STIME,
      CUTIME,
      CSTIME,
      PRIORITY,
      NICE,
      NUM_THREADS,
      ITREALVALUE,
      STARTTIME,
      VSIZE,
      RSS,
      RSSLIM,
      STARTCODE,
      ENDCODE,
      STARTSTACK,
      KSTKESP,
      KSTKEIP,
      SIGNAL,
      BLOCKED,
      SIGIGNORE,
      SIGCATCH,
      WCHAN,
      NSWAP,
      CNSWAP,
      EXIT_SIGNAL,
      PROCESSOR,
      RT_PRIORITY,
      POLICY,
      DELAYACCT_BLKIO_TICKS,
      GUEST_TIME,
      CGUEST_TIME,
      START_DATA,
      END_DATA,
      START_BRK,
      ARG_START,
      ARG_END,
      ENV_START,
      ENV_END,
      EXIT_CODE;
   }

   public static enum PidStatM {
      SIZE,
      RESIDENT,
      SHARED,
      TEXT,
      LIB,
      DATA,
      DT;
   }
}
