/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.PrintWriter;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.StringTokenizer;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Tool;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ConvertTraceFile
/*     */   extends Tool
/*     */ {
/*  28 */   private final HashMap<String, Stat> stats = new HashMap<>();
/*     */   
/*     */   private long timeTotal;
/*     */ 
/*     */   
/*     */   static class Stat
/*     */     implements Comparable<Stat>
/*     */   {
/*     */     String sql;
/*     */     int executeCount;
/*     */     long time;
/*     */     long resultCount;
/*     */     
/*     */     public int compareTo(Stat param1Stat) {
/*  42 */       if (param1Stat == this) {
/*  43 */         return 0;
/*     */       }
/*  45 */       int i = Long.compare(param1Stat.time, this.time);
/*  46 */       if (i == 0) {
/*  47 */         i = Integer.compare(param1Stat.executeCount, this.executeCount);
/*  48 */         if (i == 0) {
/*  49 */           i = this.sql.compareTo(param1Stat.sql);
/*     */         }
/*     */       } 
/*  52 */       return i;
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
/*     */ 
/*     */   
/*     */   public static void main(String... paramVarArgs) throws SQLException {
/*  74 */     (new ConvertTraceFile()).runTool(paramVarArgs);
/*     */   }
/*     */ 
/*     */   
/*     */   public void runTool(String... paramVarArgs) throws SQLException {
/*  79 */     String str1 = "test.trace.db";
/*  80 */     String str2 = "Test";
/*  81 */     String str3 = "test.sql";
/*  82 */     for (byte b = 0; paramVarArgs != null && b < paramVarArgs.length; b++) {
/*  83 */       String str = paramVarArgs[b];
/*  84 */       if (str.equals("-traceFile"))
/*  85 */       { str1 = paramVarArgs[++b]; }
/*  86 */       else if (str.equals("-javaClass"))
/*  87 */       { str2 = paramVarArgs[++b]; }
/*  88 */       else if (str.equals("-script"))
/*  89 */       { str3 = paramVarArgs[++b]; }
/*  90 */       else { if (str.equals("-help") || str.equals("-?")) {
/*  91 */           showUsage();
/*     */           return;
/*     */         } 
/*  94 */         showUsageAndThrowUnsupportedOption(str); }
/*     */     
/*     */     } 
/*     */     try {
/*  98 */       convertFile(str1, str2, str3);
/*  99 */     } catch (IOException iOException) {
/* 100 */       throw DbException.convertIOException(iOException, str1);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void convertFile(String paramString1, String paramString2, String paramString3) throws IOException {
/* 110 */     LineNumberReader lineNumberReader = new LineNumberReader(IOUtils.getReader(
/* 111 */           FileUtils.newInputStream(paramString1)));
/*     */     
/* 113 */     PrintWriter printWriter1 = new PrintWriter(IOUtils.getBufferedWriter(
/* 114 */           FileUtils.newOutputStream(paramString2 + ".java", false)));
/*     */     
/* 116 */     PrintWriter printWriter2 = new PrintWriter(IOUtils.getBufferedWriter(
/* 117 */           FileUtils.newOutputStream(paramString3, false)));
/* 118 */     printWriter1.println("import java.io.*;");
/* 119 */     printWriter1.println("import java.sql.*;");
/* 120 */     printWriter1.println("import java.math.*;");
/* 121 */     printWriter1.println("import java.util.Calendar;");
/* 122 */     String str = paramString2.replace('\\', '/');
/* 123 */     int i = str.lastIndexOf('/');
/* 124 */     if (i > 0) {
/* 125 */       str = str.substring(i + 1);
/*     */     }
/* 127 */     printWriter1.println("public class " + str + " {");
/* 128 */     printWriter1.println("    public static void main(String... args) throws Exception {");
/*     */     
/* 130 */     printWriter1.println("        Class.forName(\"org.h2.Driver\");");
/*     */     while (true) {
/* 132 */       String str1 = lineNumberReader.readLine();
/* 133 */       if (str1 == null) {
/*     */         break;
/*     */       }
/* 136 */       if (str1.startsWith("/**/")) {
/* 137 */         str1 = "        " + str1.substring(4);
/* 138 */         printWriter1.println(str1); continue;
/* 139 */       }  if (str1.startsWith("/*SQL")) {
/* 140 */         int j = str1.indexOf("*/");
/* 141 */         String str2 = str1.substring(j + "*/".length());
/* 142 */         str2 = StringUtils.javaDecode(str2);
/* 143 */         str1 = str1.substring("/*SQL".length(), j);
/* 144 */         if (str1.length() > 0) {
/* 145 */           String str3 = str2;
/* 146 */           int k = 0;
/* 147 */           long l = 0L;
/* 148 */           str1 = str1.trim();
/* 149 */           if (str1.length() > 0) {
/* 150 */             StringTokenizer stringTokenizer = new StringTokenizer(str1, " :");
/* 151 */             while (stringTokenizer.hasMoreElements()) {
/* 152 */               String str4 = stringTokenizer.nextToken();
/* 153 */               if ("l".equals(str4)) {
/* 154 */                 int m = Integer.parseInt(stringTokenizer.nextToken());
/* 155 */                 str3 = str2.substring(0, m) + ";"; continue;
/* 156 */               }  if ("#".equals(str4)) {
/* 157 */                 k = Integer.parseInt(stringTokenizer.nextToken()); continue;
/* 158 */               }  if ("t".equals(str4)) {
/* 159 */                 l = Long.parseLong(stringTokenizer.nextToken());
/*     */               }
/*     */             } 
/*     */           } 
/* 163 */           addToStats(str3, k, l);
/*     */         } 
/* 165 */         printWriter2.println(str2);
/*     */       } 
/*     */     } 
/* 168 */     printWriter1.println("    }");
/* 169 */     printWriter1.println('}');
/* 170 */     lineNumberReader.close();
/* 171 */     printWriter1.close();
/* 172 */     if (this.stats.size() > 0) {
/* 173 */       printWriter2.println("-----------------------------------------");
/* 174 */       printWriter2.println("-- SQL Statement Statistics");
/* 175 */       printWriter2.println("-- time: total time in milliseconds (accumulated)");
/* 176 */       printWriter2.println("-- count: how many times the statement ran");
/* 177 */       printWriter2.println("-- result: total update count or row count");
/* 178 */       printWriter2.println("-----------------------------------------");
/* 179 */       printWriter2.println("-- self accu    time   count  result sql");
/* 180 */       int j = 0;
/* 181 */       ArrayList<Comparable> arrayList = new ArrayList(this.stats.values());
/* 182 */       Collections.sort(arrayList);
/* 183 */       if (this.timeTotal == 0L) {
/* 184 */         this.timeTotal = 1L;
/*     */       }
/* 186 */       for (Stat stat : arrayList) {
/* 187 */         j = (int)(j + stat.time);
/* 188 */         StringBuilder stringBuilder = new StringBuilder(100);
/* 189 */         stringBuilder.append("-- ")
/* 190 */           .append(padNumberLeft(100L * stat.time / this.timeTotal, 3))
/* 191 */           .append("% ")
/* 192 */           .append(padNumberLeft((100 * j) / this.timeTotal, 3))
/* 193 */           .append('%')
/* 194 */           .append(padNumberLeft(stat.time, 8))
/* 195 */           .append(padNumberLeft(stat.executeCount, 8))
/* 196 */           .append(padNumberLeft(stat.resultCount, 8))
/* 197 */           .append(' ')
/* 198 */           .append(removeNewlines(stat.sql));
/* 199 */         printWriter2.println(stringBuilder.toString());
/*     */       } 
/*     */     } 
/* 202 */     printWriter2.close();
/*     */   }
/*     */   
/*     */   private static String removeNewlines(String paramString) {
/* 206 */     return (paramString == null) ? paramString : paramString.replace('\r', ' ').replace('\n', ' ');
/*     */   }
/*     */   
/*     */   private static String padNumberLeft(long paramLong, int paramInt) {
/* 210 */     return StringUtils.pad(Long.toString(paramLong), paramInt, " ", false);
/*     */   }
/*     */   
/*     */   private void addToStats(String paramString, int paramInt, long paramLong) {
/* 214 */     Stat stat = this.stats.get(paramString);
/* 215 */     if (stat == null) {
/* 216 */       stat = new Stat();
/* 217 */       stat.sql = paramString;
/* 218 */       this.stats.put(paramString, stat);
/*     */     } 
/* 220 */     stat.executeCount++;
/* 221 */     stat.resultCount += paramInt;
/* 222 */     stat.time += paramLong;
/* 223 */     this.timeTotal += paramLong;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\ConvertTraceFile.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */