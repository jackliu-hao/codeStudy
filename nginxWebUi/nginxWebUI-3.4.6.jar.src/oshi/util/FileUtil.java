/*     */ package oshi.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
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
/*     */ public final class FileUtil
/*     */ {
/*  51 */   private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String READING_LOG = "Reading file {}";
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String READ_LOG = "Read {}";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> readFile(String filename) {
/*  69 */     return readFile(filename, true);
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
/*     */   public static List<String> readFile(String filename, boolean reportError) {
/*  84 */     if ((new File(filename)).canRead()) {
/*  85 */       if (LOG.isDebugEnabled()) {
/*  86 */         LOG.debug("Reading file {}", filename);
/*     */       }
/*     */       try {
/*  89 */         return Files.readAllLines(Paths.get(filename, new String[0]), StandardCharsets.UTF_8);
/*  90 */       } catch (IOException e) {
/*  91 */         if (reportError) {
/*  92 */           LOG.error("Error reading file {}. {}", filename, e.getMessage());
/*     */         }
/*     */       } 
/*  95 */     } else if (reportError) {
/*  96 */       LOG.warn("File not found or not readable: {}", filename);
/*     */     } 
/*  98 */     return new ArrayList<>();
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
/*     */   public static long getLongFromFile(String filename) {
/* 110 */     if (LOG.isDebugEnabled()) {
/* 111 */       LOG.debug("Reading file {}", filename);
/*     */     }
/* 113 */     List<String> read = readFile(filename, false);
/* 114 */     if (!read.isEmpty()) {
/* 115 */       if (LOG.isTraceEnabled()) {
/* 116 */         LOG.trace("Read {}", read.get(0));
/*     */       }
/* 118 */       return ParseUtil.parseLongOrDefault(read.get(0), 0L);
/*     */     } 
/* 120 */     return 0L;
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
/*     */   public static long getUnsignedLongFromFile(String filename) {
/* 132 */     if (LOG.isDebugEnabled()) {
/* 133 */       LOG.debug("Reading file {}", filename);
/*     */     }
/* 135 */     List<String> read = readFile(filename, false);
/* 136 */     if (!read.isEmpty()) {
/* 137 */       if (LOG.isTraceEnabled()) {
/* 138 */         LOG.trace("Read {}", read.get(0));
/*     */       }
/* 140 */       return ParseUtil.parseUnsignedLongOrDefault(read.get(0), 0L);
/*     */     } 
/* 142 */     return 0L;
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
/*     */   public static int getIntFromFile(String filename) {
/* 154 */     if (LOG.isDebugEnabled()) {
/* 155 */       LOG.debug("Reading file {}", filename);
/*     */     }
/*     */     try {
/* 158 */       List<String> read = readFile(filename, false);
/* 159 */       if (!read.isEmpty()) {
/* 160 */         if (LOG.isTraceEnabled()) {
/* 161 */           LOG.trace("Read {}", read.get(0));
/*     */         }
/* 163 */         return Integer.parseInt(read.get(0));
/*     */       } 
/* 165 */     } catch (NumberFormatException ex) {
/* 166 */       LOG.warn("Unable to read value from {}. {}", filename, ex.getMessage());
/*     */     } 
/* 168 */     return 0;
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
/*     */   public static String getStringFromFile(String filename) {
/* 180 */     if (LOG.isDebugEnabled()) {
/* 181 */       LOG.debug("Reading file {}", filename);
/*     */     }
/* 183 */     List<String> read = readFile(filename, false);
/* 184 */     if (!read.isEmpty()) {
/* 185 */       if (LOG.isTraceEnabled()) {
/* 186 */         LOG.trace("Read {}", read.get(0));
/*     */       }
/* 188 */       return read.get(0);
/*     */     } 
/* 190 */     return "";
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
/*     */   public static Map<String, String> getKeyValueMapFromFile(String filename, String separator) {
/* 205 */     Map<String, String> map = new HashMap<>();
/* 206 */     if (LOG.isDebugEnabled()) {
/* 207 */       LOG.debug("Reading file {}", filename);
/*     */     }
/* 209 */     List<String> lines = readFile(filename, false);
/* 210 */     for (String line : lines) {
/* 211 */       String[] parts = line.split(separator);
/* 212 */       if (parts.length == 2) {
/* 213 */         map.put(parts[0], parts[1].trim());
/*     */       }
/*     */     } 
/* 216 */     return map;
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
/*     */   public static Properties readPropertiesFromFilename(String propsFilename) {
/* 228 */     Properties archProps = new Properties();
/*     */     
/* 230 */     if (readPropertiesFromClassLoader(propsFilename, archProps, Thread.currentThread().getContextClassLoader()) || 
/* 231 */       readPropertiesFromClassLoader(propsFilename, archProps, ClassLoader.getSystemClassLoader()) || 
/* 232 */       readPropertiesFromClassLoader(propsFilename, archProps, FileUtil.class.getClassLoader())) {
/* 233 */       return archProps;
/*     */     }
/*     */     
/* 236 */     LOG.warn("Failed to load default configuration");
/* 237 */     return archProps;
/*     */   }
/*     */ 
/*     */   
/*     */   private static boolean readPropertiesFromClassLoader(String propsFilename, Properties archProps, ClassLoader loader) {
/* 242 */     if (loader == null) {
/* 243 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 247 */     try { List<URL> resources = Collections.list(loader.getResources(propsFilename));
/* 248 */       if (resources.isEmpty()) {
/* 249 */         LOG.info("No {} file found from ClassLoader {}", propsFilename, loader);
/* 250 */         return false;
/*     */       } 
/* 252 */       if (resources.size() > 1) {
/* 253 */         LOG.warn("Configuration conflict: there is more than one {} file on the classpath", propsFilename);
/* 254 */         return true;
/*     */       } 
/*     */       
/* 257 */       InputStream in = ((URL)resources.get(0)).openStream(); 
/* 258 */       try { if (in != null) {
/* 259 */           archProps.load(in);
/*     */         }
/* 261 */         if (in != null) in.close();  } catch (Throwable throwable) { if (in != null)
/* 262 */           try { in.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }   throw throwable; }  } catch (IOException e)
/* 263 */     { return false; }
/*     */     
/* 265 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\osh\\util\FileUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */