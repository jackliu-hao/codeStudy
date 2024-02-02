/*     */ package com.sun.mail.imap.protocol;
/*     */ 
/*     */ import java.text.CharacterIterator;
/*     */ import java.text.StringCharacterIterator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BASE64MailboxDecoder
/*     */ {
/*     */   public static String decode(String original) {
/*  57 */     if (original == null || original.length() == 0) {
/*  58 */       return original;
/*     */     }
/*  60 */     boolean changedString = false;
/*  61 */     int copyTo = 0;
/*     */     
/*  63 */     char[] chars = new char[original.length()];
/*  64 */     StringCharacterIterator iter = new StringCharacterIterator(original);
/*     */     char c;
/*  66 */     for (c = iter.first(); c != Character.MAX_VALUE; 
/*  67 */       c = iter.next()) {
/*     */       
/*  69 */       if (c == '&') {
/*  70 */         changedString = true;
/*  71 */         copyTo = base64decode(chars, copyTo, iter);
/*     */       } else {
/*  73 */         chars[copyTo++] = c;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*  78 */     if (changedString) {
/*  79 */       return new String(chars, 0, copyTo);
/*     */     }
/*  81 */     return original;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static int base64decode(char[] buffer, int offset, CharacterIterator iter) {
/*  88 */     boolean firsttime = true;
/*  89 */     int leftover = -1;
/*     */ 
/*     */     
/*     */     while (true) {
/*  93 */       byte orig_0 = (byte)iter.next();
/*  94 */       if (orig_0 == -1)
/*  95 */         break;  if (orig_0 == 45) {
/*  96 */         if (firsttime)
/*     */         {
/*  98 */           buffer[offset++] = '&';
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/* 103 */       firsttime = false;
/*     */ 
/*     */       
/* 106 */       byte orig_1 = (byte)iter.next();
/* 107 */       if (orig_1 == -1 || orig_1 == 45) {
/*     */         break;
/*     */       }
/*     */       
/* 111 */       byte a = pem_convert_array[orig_0 & 0xFF];
/* 112 */       byte b = pem_convert_array[orig_1 & 0xFF];
/*     */       
/* 114 */       byte current = (byte)(a << 2 & 0xFC | b >>> 4 & 0x3);
/*     */ 
/*     */       
/* 117 */       if (leftover != -1) {
/* 118 */         buffer[offset++] = (char)(leftover << 8 | current & 0xFF);
/* 119 */         leftover = -1;
/*     */       } else {
/* 121 */         leftover = current & 0xFF;
/*     */       } 
/*     */       
/* 124 */       byte orig_2 = (byte)iter.next();
/* 125 */       if (orig_2 == 61)
/*     */         continue; 
/* 127 */       if (orig_2 == -1 || orig_2 == 45) {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/* 132 */       a = b;
/* 133 */       b = pem_convert_array[orig_2 & 0xFF];
/* 134 */       current = (byte)(a << 4 & 0xF0 | b >>> 2 & 0xF);
/*     */ 
/*     */       
/* 137 */       if (leftover != -1) {
/* 138 */         buffer[offset++] = (char)(leftover << 8 | current & 0xFF);
/* 139 */         leftover = -1;
/*     */       } else {
/* 141 */         leftover = current & 0xFF;
/*     */       } 
/*     */       
/* 144 */       byte orig_3 = (byte)iter.next();
/* 145 */       if (orig_3 == 61)
/*     */         continue; 
/* 147 */       if (orig_3 == -1 || orig_3 == 45) {
/*     */         break;
/*     */       }
/*     */ 
/*     */       
/* 152 */       a = b;
/* 153 */       b = pem_convert_array[orig_3 & 0xFF];
/* 154 */       current = (byte)(a << 6 & 0xC0 | b & 0x3F);
/*     */ 
/*     */       
/* 157 */       if (leftover != -1) {
/* 158 */         buffer[offset++] = (char)(leftover << 8 | current & 0xFF);
/* 159 */         leftover = -1; continue;
/*     */       } 
/* 161 */       leftover = current & 0xFF;
/*     */     } 
/*     */ 
/*     */     
/* 165 */     return offset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 175 */   static final char[] pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', ',' };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 186 */   private static final byte[] pem_convert_array = new byte[256];
/*     */   static {
/*     */     int i;
/* 189 */     for (i = 0; i < 255; i++)
/* 190 */       pem_convert_array[i] = -1; 
/* 191 */     for (i = 0; i < pem_array.length; i++)
/* 192 */       pem_convert_array[pem_array[i]] = (byte)i; 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\mail\imap\protocol\BASE64MailboxDecoder.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */