/*     */ package com.sun.mail.iap;
/*     */ 
/*     */ import com.sun.mail.util.ASCIIUtility;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ResponseInputStream
/*     */ {
/*     */   private static final int minIncrement = 256;
/*     */   private static final int maxIncrement = 262144;
/*     */   private static final int incrementSlop = 16;
/*     */   private BufferedInputStream bin;
/*     */   
/*     */   public ResponseInputStream(InputStream in) {
/*  68 */     this.bin = new BufferedInputStream(in, 2048);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteArray readResponse() throws IOException {
/*  76 */     return readResponse(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteArray readResponse(ByteArray ba) throws IOException {
/*  84 */     if (ba == null) {
/*  85 */       ba = new ByteArray(new byte[128], 0, 128);
/*     */     }
/*  87 */     byte[] buffer = ba.getBytes();
/*  88 */     int idx = 0;
/*     */     
/*     */     while (true) {
/*  91 */       int b = 0;
/*  92 */       boolean gotCRLF = false;
/*     */ 
/*     */       
/*  95 */       while (!gotCRLF && (b = this.bin.read()) != -1) {
/*     */         
/*  97 */         switch (b) {
/*     */           case 10:
/*  99 */             if (idx > 0 && buffer[idx - 1] == 13)
/* 100 */               gotCRLF = true;  break;
/*     */         } 
/* 102 */         if (idx >= buffer.length) {
/* 103 */           int incr = buffer.length;
/* 104 */           if (incr > 262144)
/* 105 */             incr = 262144; 
/* 106 */           ba.grow(incr);
/* 107 */           buffer = ba.getBytes();
/*     */         } 
/* 109 */         buffer[idx++] = (byte)b;
/*     */       } 
/*     */ 
/*     */       
/* 113 */       if (b == -1) {
/* 114 */         throw new IOException("Connection dropped by server?");
/*     */       }
/*     */ 
/*     */       
/* 118 */       if (idx < 5 || buffer[idx - 3] != 125) {
/*     */         break;
/*     */       }
/*     */       
/*     */       int i;
/* 123 */       for (i = idx - 4; i >= 0 && 
/* 124 */         buffer[i] != 123; i--);
/*     */ 
/*     */       
/* 127 */       if (i < 0) {
/*     */         break;
/*     */       }
/* 130 */       int count = 0;
/*     */       
/*     */       try {
/* 133 */         count = ASCIIUtility.parseInt(buffer, i + 1, idx - 3);
/* 134 */       } catch (NumberFormatException e) {
/*     */         break;
/*     */       } 
/*     */ 
/*     */       
/* 139 */       if (count > 0) {
/* 140 */         int avail = buffer.length - idx;
/* 141 */         if (count + 16 > avail) {
/*     */           
/* 143 */           ba.grow((256 > count + 16 - avail) ? 256 : (count + 16 - avail));
/*     */           
/* 145 */           buffer = ba.getBytes();
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 153 */         while (count > 0) {
/* 154 */           int actual = this.bin.read(buffer, idx, count);
/* 155 */           count -= actual;
/* 156 */           idx += actual;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 161 */     ba.setCount(idx);
/* 162 */     return ba;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\iap\ResponseInputStream.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */