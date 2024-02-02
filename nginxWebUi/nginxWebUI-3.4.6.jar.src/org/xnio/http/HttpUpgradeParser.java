/*     */ package org.xnio.http;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
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
/*     */ class HttpUpgradeParser
/*     */ {
/*     */   private static final int VERSION = 0;
/*     */   private static final int STATUS_CODE = 1;
/*     */   private static final int MESSAGE = 2;
/*     */   private static final int HEADER_NAME = 3;
/*     */   private static final int HEADER_VALUE = 4;
/*     */   private static final int COMPLETE = 5;
/*  42 */   private int parseState = 0;
/*     */   private String httpVersion;
/*     */   private int responseCode;
/*     */   private String message;
/*  46 */   private final Map<String, List<String>> headers = new HashMap<>();
/*     */   
/*  48 */   private final StringBuilder current = new StringBuilder();
/*     */   private String headerName;
/*     */   
/*     */   void parse(ByteBuffer buffer) throws IOException {
/*  52 */     while (buffer.hasRemaining() && !isComplete()) {
/*  53 */       switch (this.parseState) {
/*     */         case 0:
/*  55 */           parseVersion(buffer);
/*     */         
/*     */         case 1:
/*  58 */           parseStatusCode(buffer);
/*     */         
/*     */         case 2:
/*  61 */           parseMessage(buffer);
/*     */         
/*     */         case 3:
/*  64 */           parseHeaderName(buffer);
/*     */         
/*     */         case 4:
/*  67 */           parseHeaderValue(buffer);
/*     */         case 5:
/*     */           return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseHeaderValue(ByteBuffer buffer) {
/*  77 */     while (buffer.hasRemaining()) {
/*  78 */       byte b = buffer.get();
/*  79 */       if (b == 13 || b == 10) {
/*  80 */         String key = this.headerName.toLowerCase(Locale.ENGLISH);
/*  81 */         List<String> list = this.headers.get(key);
/*  82 */         if (list == null) {
/*  83 */           this.headers.put(key, list = new ArrayList<>());
/*     */         }
/*  85 */         list.add(this.current.toString().trim());
/*  86 */         this.parseState--;
/*  87 */         this.current.setLength(0);
/*     */         return;
/*     */       } 
/*  90 */       this.current.append((char)b);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseHeaderName(ByteBuffer buffer) throws IOException {
/*  96 */     while (buffer.hasRemaining()) {
/*  97 */       byte b = buffer.get();
/*  98 */       if (b == 13 || b == 10) {
/*  99 */         if (this.current.length() > 2)
/* 100 */           throw new IOException("Invalid response"); 
/* 101 */         if (this.current.length() == 2) {
/*     */           
/* 103 */           if (this.current.charAt(0) == '\n' && this.current
/* 104 */             .charAt(1) == '\r' && b == 10) {
/*     */             
/* 106 */             this.parseState = 5;
/*     */             return;
/*     */           } 
/* 109 */           throw new IOException("Invalid response");
/*     */         } 
/* 111 */         this.current.append((char)b); continue;
/* 112 */       }  if (b == 58) {
/* 113 */         this.headerName = this.current.toString().trim();
/* 114 */         this.parseState++;
/* 115 */         this.current.setLength(0);
/*     */         return;
/*     */       } 
/* 118 */       this.current.append((char)b);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseMessage(ByteBuffer buffer) throws IOException {
/* 124 */     while (buffer.hasRemaining()) {
/* 125 */       byte b = buffer.get();
/* 126 */       if (b == 13 || b == 10) {
/* 127 */         this.message = this.current.toString().trim();
/* 128 */         this.parseState++;
/* 129 */         this.current.setLength(0);
/*     */         return;
/*     */       } 
/* 132 */       this.current.append((char)b);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseStatusCode(ByteBuffer buffer) throws IOException {
/* 138 */     while (buffer.hasRemaining()) {
/* 139 */       byte b = buffer.get();
/* 140 */       if (b == 32 || b == 9) {
/* 141 */         this.responseCode = Integer.parseInt(this.current.toString().trim());
/* 142 */         this.parseState++;
/* 143 */         this.current.setLength(0); return;
/*     */       } 
/* 145 */       if (Character.isDigit(b)) {
/* 146 */         this.current.append((char)b); continue;
/*     */       } 
/* 148 */       throw new IOException("Invalid response");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void parseVersion(ByteBuffer buffer) throws IOException {
/* 154 */     while (buffer.hasRemaining()) {
/* 155 */       byte b = buffer.get();
/* 156 */       if (b == 32 || b == 9) {
/* 157 */         this.httpVersion = this.current.toString().trim();
/* 158 */         this.parseState++;
/* 159 */         this.current.setLength(0); return;
/*     */       } 
/* 161 */       if (Character.isDigit(b) || Character.isAlphabetic(b) || b == 46 || b == 47) {
/* 162 */         this.current.append((char)b); continue;
/*     */       } 
/* 164 */       throw new IOException("Invalid response");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   boolean isComplete() {
/* 171 */     return (this.parseState == 5);
/*     */   }
/*     */   
/*     */   public String getHttpVersion() {
/* 175 */     return this.httpVersion;
/*     */   }
/*     */   
/*     */   public int getResponseCode() {
/* 179 */     return this.responseCode;
/*     */   }
/*     */   
/*     */   public String getMessage() {
/* 183 */     return this.message;
/*     */   }
/*     */   
/*     */   public Map<String, List<String>> getHeaders() {
/* 187 */     return this.headers;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\http\HttpUpgradeParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */