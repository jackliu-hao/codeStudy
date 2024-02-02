/*     */ package org.apache.http.conn.util;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
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
/*     */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*     */ public final class PublicSuffixListParser
/*     */ {
/*     */   public PublicSuffixList parse(Reader reader) throws IOException {
/*  60 */     List<String> rules = new ArrayList<String>();
/*  61 */     List<String> exceptions = new ArrayList<String>();
/*  62 */     BufferedReader r = new BufferedReader(reader);
/*     */     
/*     */     String line;
/*  65 */     while ((line = r.readLine()) != null) {
/*  66 */       if (line.isEmpty()) {
/*     */         continue;
/*     */       }
/*  69 */       if (line.startsWith("//")) {
/*     */         continue;
/*     */       }
/*  72 */       if (line.startsWith(".")) {
/*  73 */         line = line.substring(1);
/*     */       }
/*     */       
/*  76 */       boolean isException = line.startsWith("!");
/*  77 */       if (isException) {
/*  78 */         line = line.substring(1);
/*     */       }
/*     */       
/*  81 */       if (isException) {
/*  82 */         exceptions.add(line); continue;
/*     */       } 
/*  84 */       rules.add(line);
/*     */     } 
/*     */     
/*  87 */     return new PublicSuffixList(DomainType.UNKNOWN, rules, exceptions);
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
/*     */   public List<PublicSuffixList> parseByType(Reader reader) throws IOException {
/* 102 */     List<PublicSuffixList> result = new ArrayList<PublicSuffixList>(2);
/*     */     
/* 104 */     BufferedReader r = new BufferedReader(reader);
/* 105 */     StringBuilder sb = new StringBuilder(256);
/*     */     
/* 107 */     DomainType domainType = null;
/* 108 */     List<String> rules = null;
/* 109 */     List<String> exceptions = null;
/*     */     String line;
/* 111 */     while ((line = r.readLine()) != null) {
/* 112 */       if (line.isEmpty()) {
/*     */         continue;
/*     */       }
/* 115 */       if (line.startsWith("//")) {
/*     */         
/* 117 */         if (domainType == null) {
/* 118 */           if (line.contains("===BEGIN ICANN DOMAINS===")) {
/* 119 */             domainType = DomainType.ICANN; continue;
/* 120 */           }  if (line.contains("===BEGIN PRIVATE DOMAINS==="))
/* 121 */             domainType = DomainType.PRIVATE; 
/*     */           continue;
/*     */         } 
/* 124 */         if (line.contains("===END ICANN DOMAINS===") || line.contains("===END PRIVATE DOMAINS===")) {
/* 125 */           if (rules != null) {
/* 126 */             result.add(new PublicSuffixList(domainType, rules, exceptions));
/*     */           }
/* 128 */           domainType = null;
/* 129 */           rules = null;
/* 130 */           exceptions = null;
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 136 */       if (domainType == null) {
/*     */         continue;
/*     */       }
/*     */       
/* 140 */       if (line.startsWith(".")) {
/* 141 */         line = line.substring(1);
/*     */       }
/*     */       
/* 144 */       boolean isException = line.startsWith("!");
/* 145 */       if (isException) {
/* 146 */         line = line.substring(1);
/*     */       }
/*     */       
/* 149 */       if (isException) {
/* 150 */         if (exceptions == null) {
/* 151 */           exceptions = new ArrayList<String>();
/*     */         }
/* 153 */         exceptions.add(line); continue;
/*     */       } 
/* 155 */       if (rules == null) {
/* 156 */         rules = new ArrayList<String>();
/*     */       }
/* 158 */       rules.add(line);
/*     */     } 
/*     */     
/* 161 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\con\\util\PublicSuffixListParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */