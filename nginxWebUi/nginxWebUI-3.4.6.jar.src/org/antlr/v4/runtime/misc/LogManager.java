/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
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
/*     */ public class LogManager
/*     */ {
/*     */   protected List<Record> records;
/*     */   
/*     */   protected static class Record
/*     */   {
/*  48 */     long timestamp = System.currentTimeMillis();
/*  49 */     StackTraceElement location = (new Throwable()).getStackTrace()[0];
/*     */     String component;
/*     */     String msg;
/*     */     
/*     */     public String toString() {
/*  54 */       StringBuilder buf = new StringBuilder();
/*  55 */       buf.append((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")).format(new Date(this.timestamp)));
/*  56 */       buf.append(" ");
/*  57 */       buf.append(this.component);
/*  58 */       buf.append(" ");
/*  59 */       buf.append(this.location.getFileName());
/*  60 */       buf.append(":");
/*  61 */       buf.append(this.location.getLineNumber());
/*  62 */       buf.append(" ");
/*  63 */       buf.append(this.msg);
/*  64 */       return buf.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void log(String component, String msg) {
/*  71 */     Record r = new Record();
/*  72 */     r.component = component;
/*  73 */     r.msg = msg;
/*  74 */     if (this.records == null) {
/*  75 */       this.records = new ArrayList<Record>();
/*     */     }
/*  77 */     this.records.add(r);
/*     */   }
/*     */   public void log(String msg) {
/*  80 */     log(null, msg);
/*     */   }
/*     */   public void save(String filename) throws IOException {
/*  83 */     FileWriter fw = new FileWriter(filename);
/*  84 */     BufferedWriter bw = new BufferedWriter(fw);
/*     */     try {
/*  86 */       bw.write(toString());
/*     */     } finally {
/*     */       
/*  89 */       bw.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String save() throws IOException {
/*  95 */     String dir = ".";
/*  96 */     String defaultFilename = dir + "/antlr-" + (new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss")).format(new Date()) + ".log";
/*     */ 
/*     */     
/*  99 */     save(defaultFilename);
/* 100 */     return defaultFilename;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 105 */     if (this.records == null) return ""; 
/* 106 */     String nl = System.getProperty("line.separator");
/* 107 */     StringBuilder buf = new StringBuilder();
/* 108 */     for (Record r : this.records) {
/* 109 */       buf.append(r);
/* 110 */       buf.append(nl);
/*     */     } 
/* 112 */     return buf.toString();
/*     */   }
/*     */   
/*     */   public static void main(String[] args) throws IOException {
/* 116 */     LogManager mgr = new LogManager();
/* 117 */     mgr.log("atn", "test msg");
/* 118 */     mgr.log("dfa", "test msg 2");
/* 119 */     System.out.println(mgr);
/* 120 */     mgr.save();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\LogManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */