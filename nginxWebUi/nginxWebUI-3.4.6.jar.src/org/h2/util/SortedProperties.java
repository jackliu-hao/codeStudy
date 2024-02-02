/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.LineNumberReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.Collections;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.TreeMap;
/*     */ import java.util.Vector;
/*     */ import org.h2.store.fs.FileUtils;
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
/*     */ public class SortedProperties
/*     */   extends Properties
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public synchronized Enumeration<Object> keys() {
/*  37 */     Vector<String> vector = new Vector();
/*  38 */     for (Object object : keySet()) {
/*  39 */       vector.add(object.toString());
/*     */     }
/*  41 */     Collections.sort(vector);
/*  42 */     return (new Vector(vector)).elements();
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
/*     */   public static boolean getBooleanProperty(Properties paramProperties, String paramString, boolean paramBoolean) {
/*     */     try {
/*  56 */       return Utils.parseBoolean(paramProperties.getProperty(paramString, null), paramBoolean, true);
/*  57 */     } catch (IllegalArgumentException illegalArgumentException) {
/*  58 */       illegalArgumentException.printStackTrace();
/*  59 */       return paramBoolean;
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
/*     */   public static int getIntProperty(Properties paramProperties, String paramString, int paramInt) {
/*  72 */     String str = paramProperties.getProperty(paramString, Integer.toString(paramInt));
/*     */     try {
/*  74 */       return Integer.decode(str).intValue();
/*  75 */     } catch (Exception exception) {
/*  76 */       exception.printStackTrace();
/*  77 */       return paramInt;
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
/*     */   public static String getStringProperty(Properties paramProperties, String paramString1, String paramString2) {
/*  90 */     return paramProperties.getProperty(paramString1, paramString2);
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
/*     */   public static synchronized SortedProperties loadProperties(String paramString) throws IOException {
/* 102 */     SortedProperties sortedProperties = new SortedProperties();
/* 103 */     if (FileUtils.exists(paramString)) {
/* 104 */       try (InputStream null = FileUtils.newInputStream(paramString)) {
/* 105 */         sortedProperties.load(inputStream);
/*     */       } 
/*     */     }
/* 108 */     return sortedProperties;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void store(String paramString) throws IOException {
/*     */     OutputStreamWriter outputStreamWriter;
/* 118 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 119 */     store(byteArrayOutputStream, (String)null);
/* 120 */     ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
/* 121 */     InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream, StandardCharsets.ISO_8859_1);
/* 122 */     LineNumberReader lineNumberReader = new LineNumberReader(inputStreamReader);
/*     */     
/*     */     try {
/* 125 */       outputStreamWriter = new OutputStreamWriter(FileUtils.newOutputStream(paramString, false));
/* 126 */     } catch (Exception exception) {
/* 127 */       throw new IOException(exception.toString(), exception);
/*     */     } 
/* 129 */     try (PrintWriter null = new PrintWriter(new BufferedWriter(outputStreamWriter))) {
/*     */       while (true) {
/* 131 */         String str = lineNumberReader.readLine();
/* 132 */         if (str == null) {
/*     */           break;
/*     */         }
/* 135 */         if (!str.startsWith("#")) {
/* 136 */           printWriter.print(str + "\n");
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized String toLines() {
/* 148 */     StringBuilder stringBuilder = new StringBuilder();
/* 149 */     for (Map.Entry<?, ?> entry : (new TreeMap<>(this)).entrySet()) {
/* 150 */       stringBuilder.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
/*     */     }
/* 152 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static SortedProperties fromLines(String paramString) {
/* 162 */     SortedProperties sortedProperties = new SortedProperties();
/* 163 */     for (String str : StringUtils.arraySplit(paramString, '\n', true)) {
/* 164 */       int i = str.indexOf('=');
/* 165 */       if (i > 0) {
/* 166 */         sortedProperties.put(str.substring(0, i), str.substring(i + 1));
/*     */       }
/*     */     } 
/* 169 */     return sortedProperties;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\SortedProperties.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */