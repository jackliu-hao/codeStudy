/*     */ package org.wildfly.common.cpu;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Locale;
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
/*     */ public final class CacheInfo
/*     */ {
/*     */   private static final CacheLevelInfo[] cacheLevels;
/*     */   
/*     */   public static int getLevelEntryCount() {
/*  48 */     return cacheLevels.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CacheLevelInfo getCacheLevelInfo(int index) {
/*  59 */     return cacheLevels[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getSmallestDataCacheLineSize() {
/*  69 */     int minSize = Integer.MAX_VALUE;
/*  70 */     for (CacheLevelInfo cacheLevel : cacheLevels) {
/*  71 */       if (cacheLevel.getCacheType().isData()) {
/*  72 */         int cacheLineSize = cacheLevel.getCacheLineSize();
/*  73 */         if (cacheLineSize != 0 && cacheLineSize < minSize) {
/*  74 */           minSize = cacheLineSize;
/*     */         }
/*     */       } 
/*     */     } 
/*  78 */     return (minSize == Integer.MAX_VALUE) ? 0 : minSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getSmallestInstructionCacheLineSize() {
/*  88 */     int minSize = Integer.MAX_VALUE;
/*  89 */     for (CacheLevelInfo cacheLevel : cacheLevels) {
/*  90 */       if (cacheLevel.getCacheType().isInstruction()) {
/*  91 */         int cacheLineSize = cacheLevel.getCacheLineSize();
/*  92 */         if (cacheLineSize != 0 && cacheLineSize < minSize) {
/*  93 */           minSize = cacheLineSize;
/*     */         }
/*     */       } 
/*     */     } 
/*  97 */     return (minSize == Integer.MAX_VALUE) ? 0 : minSize;
/*     */   }
/*     */   
/*     */   static {
/* 101 */     cacheLevels = AccessController.<CacheLevelInfo[]>doPrivileged(() -> {
/*     */           try {
/*     */             String osArch = System.getProperty("os.name", "unknown").toLowerCase(Locale.US);
/*     */             if (osArch.contains("linux")) {
/*     */               File cpu0 = new File("/sys/devices/system/cpu/cpu0/cache");
/*     */               if (cpu0.exists()) {
/*     */                 File[] files = cpu0.listFiles();
/*     */                 if (files != null) {
/*     */                   ArrayList<File> indexes = new ArrayList<>();
/*     */                   for (File file : files) {
/*     */                     if (file.getName().startsWith("index")) {
/*     */                       indexes.add(file);
/*     */                     }
/*     */                   } 
/*     */                   CacheLevelInfo[] levelInfoArray = new CacheLevelInfo[indexes.size()];
/*     */                   for (int i = 0; i < indexes.size(); i++) {
/*     */                     CacheType type;
/*     */                     File file = indexes.get(i);
/*     */                     int index = parseIntFile(new File(file, "level"));
/*     */                     switch (parseStringFile(new File(file, "type"))) {
/*     */                       case "Data":
/*     */                         type = CacheType.DATA;
/*     */                         break;
/*     */                       case "Instruction":
/*     */                         type = CacheType.INSTRUCTION;
/*     */                         break;
/*     */                       case "Unified":
/*     */                         type = CacheType.UNIFIED;
/*     */                         break;
/*     */                       default:
/*     */                         type = CacheType.UNKNOWN;
/*     */                         break;
/*     */                     } 
/*     */                     int size = parseIntKBFile(new File(file, "size"));
/*     */                     int lineSize = parseIntFile(new File(file, "coherency_line_size"));
/*     */                     levelInfoArray[i] = new CacheLevelInfo(index, type, size, lineSize);
/*     */                   } 
/*     */                   return levelInfoArray;
/*     */                 } 
/*     */               } 
/*     */             } else if (osArch.contains("mac os x")) {
/*     */               int lineSize = safeParseInt(parseProcessOutput(new String[] { "/usr/sbin/sysctl", "-n", "hw.cachelinesize" }));
/*     */               if (lineSize != 0) {
/*     */                 int l1d = safeParseInt(parseProcessOutput(new String[] { "/usr/sbin/sysctl", "-n", "hw.l1dcachesize" }));
/*     */                 int l1i = safeParseInt(parseProcessOutput(new String[] { "/usr/sbin/sysctl", "-n", "hw.l1icachesize" }));
/*     */                 int l2 = safeParseInt(parseProcessOutput(new String[] { "/usr/sbin/sysctl", "-n", "hw.l2cachesize" }));
/*     */                 int l3 = safeParseInt(parseProcessOutput(new String[] { "/usr/sbin/sysctl", "-n", "hw.l3cachesize" }));
/*     */                 ArrayList<CacheLevelInfo> list = new ArrayList<>();
/*     */                 if (l1d != 0) {
/*     */                   list.add(new CacheLevelInfo(1, CacheType.DATA, l1d / 1024, lineSize));
/*     */                 }
/*     */                 if (l1i != 0)
/*     */                   list.add(new CacheLevelInfo(1, CacheType.INSTRUCTION, l1i / 1024, lineSize)); 
/*     */                 if (l2 != 0)
/*     */                   list.add(new CacheLevelInfo(2, CacheType.UNIFIED, l2 / 1024, lineSize)); 
/*     */                 if (l3 != 0)
/*     */                   list.add(new CacheLevelInfo(3, CacheType.UNIFIED, l3 / 1024, lineSize)); 
/*     */                 if (list.size() > 0)
/*     */                   return list.<CacheLevelInfo>toArray(new CacheLevelInfo[list.size()]); 
/*     */               } 
/*     */             } else if (osArch.contains("windows")) {
/*     */             
/*     */             } 
/* 164 */           } catch (Throwable throwable) {}
/*     */           return new CacheLevelInfo[0];
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   static int parseIntFile(File file) {
/* 171 */     return safeParseInt(parseStringFile(file));
/*     */   }
/*     */   
/*     */   static int safeParseInt(String string) {
/*     */     try {
/* 176 */       return Integer.parseInt(string);
/* 177 */     } catch (Throwable ignored) {
/* 178 */       return 0;
/*     */     } 
/*     */   }
/*     */   
/*     */   static int parseIntKBFile(File file) {
/*     */     try {
/* 184 */       String s = parseStringFile(file);
/* 185 */       if (s.endsWith("K"))
/* 186 */         return Integer.parseInt(s.substring(0, s.length() - 1)); 
/* 187 */       if (s.endsWith("M"))
/* 188 */         return Integer.parseInt(s.substring(0, s.length() - 1)) * 1024; 
/* 189 */       if (s.endsWith("G")) {
/* 190 */         return Integer.parseInt(s.substring(0, s.length() - 1)) * 1024 * 1024;
/*     */       }
/* 192 */       return Integer.parseInt(s);
/*     */     }
/* 194 */     catch (Throwable ignored) {
/* 195 */       return 0;
/*     */     } 
/*     */   }
/*     */   static String parseStringFile(File file) {
/*     */     
/* 200 */     try { FileInputStream is = new FileInputStream(file); 
/* 201 */       try { String str = parseStringStream(is);
/* 202 */         is.close(); return str; } catch (Throwable throwable) { try { is.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Throwable ignored)
/* 203 */     { return ""; }
/*     */   
/*     */   }
/*     */   static String parseStringStream(InputStream is) {
/*     */     
/* 208 */     try { Reader r = new InputStreamReader(is, StandardCharsets.UTF_8); 
/* 209 */       try { StringBuilder b = new StringBuilder();
/* 210 */         char[] cb = new char[64];
/*     */         int res;
/* 212 */         while ((res = r.read(cb)) != -1) {
/* 213 */           b.append(cb, 0, res);
/*     */         }
/* 215 */         String str = b.toString().trim();
/* 216 */         r.close(); return str; } catch (Throwable throwable) { try { r.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }  } catch (Throwable ignored)
/* 217 */     { return ""; }
/*     */   
/*     */   }
/*     */   
/*     */   static String parseProcessOutput(String... args) {
/* 222 */     ProcessBuilder processBuilder = new ProcessBuilder(args); try {
/*     */       String result;
/* 224 */       Process process = processBuilder.start();
/* 225 */       process.getOutputStream().close();
/* 226 */       InputStream errorStream = process.getErrorStream();
/*     */       
/* 228 */       Thread errorThread = new Thread(null, new StreamConsumer(errorStream), "Process thread", 32768L);
/* 229 */       errorThread.start();
/*     */ 
/*     */       
/* 232 */       InputStream inputStream = process.getInputStream(); 
/* 233 */       try { result = parseStringStream(inputStream);
/* 234 */         if (inputStream != null) inputStream.close();  } catch (Throwable throwable) { if (inputStream != null)
/*     */           try { inputStream.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }
/* 236 */        boolean intr = false;
/*     */       try {
/* 238 */         process.waitFor();
/* 239 */       } catch (InterruptedException e) {
/* 240 */         intr = true;
/* 241 */         return null;
/*     */       } finally {
/*     */         try {
/* 244 */           errorThread.join();
/* 245 */         } catch (InterruptedException e) {
/* 246 */           intr = true;
/*     */         } finally {
/* 248 */           if (intr) {
/* 249 */             Thread.currentThread().interrupt();
/*     */           }
/*     */         } 
/*     */       } 
/* 253 */       return result;
/* 254 */     } catch (IOException e) {
/* 255 */       return "";
/*     */     } 
/*     */   }
/*     */   
/*     */   static class StreamConsumer
/*     */     implements Runnable {
/*     */     private final InputStream stream;
/*     */     
/*     */     StreamConsumer(InputStream stream) {
/* 264 */       this.stream = stream;
/*     */     }
/*     */     
/*     */     public void run() {
/* 268 */       byte[] buffer = new byte[128];
/*     */       
/* 270 */       try { while (this.stream.read(buffer) != -1); }
/* 271 */       catch (IOException iOException)
/*     */       
/*     */       { try {
/* 274 */           this.stream.close();
/* 275 */         } catch (IOException iOException1) {} } finally { try { this.stream.close(); } catch (IOException iOException) {} }
/*     */     
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void main(String[] args) {
/* 282 */     System.out.println("Detected cache info:");
/* 283 */     for (CacheLevelInfo levelInfo : cacheLevels) {
/* 284 */       System.out.printf("Level %d cache: type %s, size %d KiB, cache line is %d bytes%n", new Object[] {
/* 285 */             Integer.valueOf(levelInfo.getCacheLevel()), levelInfo
/* 286 */             .getCacheType(), 
/* 287 */             Integer.valueOf(levelInfo.getCacheLevelSizeKB()), 
/* 288 */             Integer.valueOf(levelInfo.getCacheLineSize())
/*     */           });
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\cpu\CacheInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */