/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.util.Arrays;
/*     */ import java.util.BitSet;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ public class Utils
/*     */ {
/*     */   public static <T> String join(Iterator<T> iter, String separator) {
/*  49 */     StringBuilder buf = new StringBuilder();
/*  50 */     while (iter.hasNext()) {
/*  51 */       buf.append(iter.next());
/*  52 */       if (iter.hasNext()) {
/*  53 */         buf.append(separator);
/*     */       }
/*     */     } 
/*  56 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public static <T> String join(T[] array, String separator) {
/*  60 */     StringBuilder builder = new StringBuilder();
/*  61 */     for (int i = 0; i < array.length; i++) {
/*  62 */       builder.append(array[i]);
/*  63 */       if (i < array.length - 1) {
/*  64 */         builder.append(separator);
/*     */       }
/*     */     } 
/*     */     
/*  68 */     return builder.toString();
/*     */   }
/*     */   
/*     */   public static int numNonnull(Object[] data) {
/*  72 */     int n = 0;
/*  73 */     if (data == null) return n; 
/*  74 */     for (Object o : data) {
/*  75 */       if (o != null) n++; 
/*     */     } 
/*  77 */     return n;
/*     */   }
/*     */   
/*     */   public static <T> void removeAllElements(Collection<T> data, T value) {
/*  81 */     if (data == null)
/*  82 */       return;  for (; data.contains(value); data.remove(value));
/*     */   }
/*     */   
/*     */   public static String escapeWhitespace(String s, boolean escapeSpaces) {
/*  86 */     StringBuilder buf = new StringBuilder();
/*  87 */     for (char c : s.toCharArray()) {
/*  88 */       if (c == ' ' && escapeSpaces) { buf.append('·'); }
/*  89 */       else if (c == '\t') { buf.append("\\t"); }
/*  90 */       else if (c == '\n') { buf.append("\\n"); }
/*  91 */       else if (c == '\r') { buf.append("\\r"); }
/*  92 */       else { buf.append(c); }
/*     */     
/*  94 */     }  return buf.toString();
/*     */   }
/*     */   
/*     */   public static void writeFile(String fileName, String content) throws IOException {
/*  98 */     writeFile(fileName, content, null);
/*     */   }
/*     */   public static void writeFile(String fileName, String content, String encoding) throws IOException {
/*     */     OutputStreamWriter osw;
/* 102 */     File f = new File(fileName);
/* 103 */     FileOutputStream fos = new FileOutputStream(f);
/*     */     
/* 105 */     if (encoding != null) {
/* 106 */       osw = new OutputStreamWriter(fos, encoding);
/*     */     } else {
/*     */       
/* 109 */       osw = new OutputStreamWriter(fos);
/*     */     } 
/*     */     
/*     */     try {
/* 113 */       osw.write(content);
/*     */     } finally {
/*     */       
/* 116 */       osw.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static char[] readFile(String fileName) throws IOException {
/* 122 */     return readFile(fileName, null);
/*     */   }
/*     */   
/*     */   public static char[] readFile(String fileName, String encoding) throws IOException {
/*     */     InputStreamReader isr;
/* 127 */     File f = new File(fileName);
/* 128 */     int size = (int)f.length();
/*     */     
/* 130 */     FileInputStream fis = new FileInputStream(fileName);
/* 131 */     if (encoding != null) {
/* 132 */       isr = new InputStreamReader(fis, encoding);
/*     */     } else {
/*     */       
/* 135 */       isr = new InputStreamReader(fis);
/*     */     } 
/* 137 */     char[] data = null;
/*     */     try {
/* 139 */       data = new char[size];
/* 140 */       int n = isr.read(data);
/* 141 */       if (n < data.length) {
/* 142 */         data = Arrays.copyOf(data, n);
/*     */       }
/*     */     } finally {
/*     */       
/* 146 */       isr.close();
/*     */     } 
/* 148 */     return data;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<String, Integer> toMap(String[] keys) {
/* 155 */     Map<String, Integer> m = new HashMap<String, Integer>();
/* 156 */     for (int i = 0; i < keys.length; i++) {
/* 157 */       m.put(keys[i], Integer.valueOf(i));
/*     */     }
/* 159 */     return m;
/*     */   }
/*     */   
/*     */   public static char[] toCharArray(IntegerList data) {
/* 163 */     if (data == null) return null; 
/* 164 */     char[] cdata = new char[data.size()];
/* 165 */     for (int i = 0; i < data.size(); i++) {
/* 166 */       cdata[i] = (char)data.get(i);
/*     */     }
/* 168 */     return cdata;
/*     */   }
/*     */   
/*     */   public static IntervalSet toSet(BitSet bits) {
/* 172 */     IntervalSet s = new IntervalSet(new int[0]);
/* 173 */     int i = bits.nextSetBit(0);
/* 174 */     while (i >= 0) {
/* 175 */       s.add(i);
/* 176 */       i = bits.nextSetBit(i + 1);
/*     */     } 
/* 178 */     return s;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */