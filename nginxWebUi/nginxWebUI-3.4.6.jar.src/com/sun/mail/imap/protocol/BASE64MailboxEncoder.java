/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import java.io.CharArrayWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
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
/*     */ public class BASE64MailboxEncoder
/*     */ {
/* 110 */   protected byte[] buffer = new byte[4];
/* 111 */   protected int bufsize = 0;
/*     */   protected boolean started = false;
/* 113 */   protected Writer out = null;
/*     */ 
/*     */   
/*     */   public static String encode(String original) {
/* 117 */     BASE64MailboxEncoder base64stream = null;
/* 118 */     char[] origchars = original.toCharArray();
/* 119 */     int length = origchars.length;
/* 120 */     boolean changedString = false;
/* 121 */     CharArrayWriter writer = new CharArrayWriter(length);
/*     */ 
/*     */     
/* 124 */     for (int index = 0; index < length; index++) {
/* 125 */       char current = origchars[index];
/*     */ 
/*     */ 
/*     */       
/* 129 */       if (current >= ' ' && current <= '~') {
/* 130 */         if (base64stream != null) {
/* 131 */           base64stream.flush();
/*     */         }
/*     */         
/* 134 */         if (current == '&') {
/* 135 */           changedString = true;
/* 136 */           writer.write(38);
/* 137 */           writer.write(45);
/*     */         } else {
/* 139 */           writer.write(current);
/*     */         
/*     */         }
/*     */ 
/*     */       
/*     */       }
/*     */       else {
/*     */ 
/*     */         
/* 148 */         if (base64stream == null) {
/* 149 */           base64stream = new BASE64MailboxEncoder(writer);
/* 150 */           changedString = true;
/*     */         } 
/*     */         
/* 153 */         base64stream.write(current);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 158 */     if (base64stream != null) {
/* 159 */       base64stream.flush();
/*     */     }
/*     */     
/* 162 */     if (changedString) {
/* 163 */       return writer.toString();
/*     */     }
/* 165 */     return original;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BASE64MailboxEncoder(Writer what) {
/* 174 */     this.out = what;
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(int c) {
/*     */     try {
/* 180 */       if (!this.started) {
/* 181 */         this.started = true;
/* 182 */         this.out.write(38);
/*     */       } 
/*     */ 
/*     */       
/* 186 */       this.buffer[this.bufsize++] = (byte)(c >> 8);
/* 187 */       this.buffer[this.bufsize++] = (byte)(c & 0xFF);
/*     */       
/* 189 */       if (this.bufsize >= 3) {
/* 190 */         encode();
/* 191 */         this.bufsize -= 3;
/*     */       } 
/* 193 */     } catch (IOException e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flush() {
/*     */     try {
/* 202 */       if (this.bufsize > 0) {
/* 203 */         encode();
/* 204 */         this.bufsize = 0;
/*     */       } 
/*     */ 
/*     */       
/* 208 */       if (this.started) {
/* 209 */         this.out.write(45);
/* 210 */         this.started = false;
/*     */       } 
/* 212 */     } catch (IOException e) {}
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void encode() throws IOException {
/* 220 */     if (this.bufsize == 1) {
/* 221 */       byte a = this.buffer[0];
/* 222 */       byte b = 0;
/* 223 */       byte c = 0;
/* 224 */       this.out.write(pem_array[a >>> 2 & 0x3F]);
/* 225 */       this.out.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
/*     */     }
/* 227 */     else if (this.bufsize == 2) {
/* 228 */       byte a = this.buffer[0];
/* 229 */       byte b = this.buffer[1];
/* 230 */       byte c = 0;
/* 231 */       this.out.write(pem_array[a >>> 2 & 0x3F]);
/* 232 */       this.out.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
/* 233 */       this.out.write(pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)]);
/*     */     } else {
/*     */       
/* 236 */       byte a = this.buffer[0];
/* 237 */       byte b = this.buffer[1];
/* 238 */       byte c = this.buffer[2];
/* 239 */       this.out.write(pem_array[a >>> 2 & 0x3F]);
/* 240 */       this.out.write(pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
/* 241 */       this.out.write(pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)]);
/* 242 */       this.out.write(pem_array[c & 0x3F]);
/*     */ 
/*     */       
/* 245 */       if (this.bufsize == 4)
/* 246 */         this.buffer[0] = this.buffer[3]; 
/*     */     } 
/*     */   }
/*     */   
/* 250 */   private static final char[] pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', ',' };
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\BASE64MailboxEncoder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */