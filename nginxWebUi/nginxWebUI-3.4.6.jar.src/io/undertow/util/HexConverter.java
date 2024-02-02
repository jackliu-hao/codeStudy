/*     */ package io.undertow.util;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HexConverter
/*     */ {
/*  28 */   private static final char[] HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
/*     */ 
/*     */   
/*  31 */   private static final byte[] HEX_BYTES = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String convertToHexString(byte[] toBeConverted) {
/*  41 */     if (toBeConverted == null) {
/*  42 */       throw new NullPointerException("Parameter to be converted can not be null");
/*     */     }
/*     */     
/*  45 */     char[] converted = new char[toBeConverted.length * 2];
/*  46 */     for (int i = 0; i < toBeConverted.length; i++) {
/*  47 */       byte b = toBeConverted[i];
/*  48 */       converted[i * 2] = HEX_CHARS[b >> 4 & 0xF];
/*  49 */       converted[i * 2 + 1] = HEX_CHARS[b & 0xF];
/*     */     } 
/*     */     
/*  52 */     return String.valueOf(converted);
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
/*     */   public static byte[] convertToHexBytes(byte[] toBeConverted) {
/*  66 */     if (toBeConverted == null) {
/*  67 */       throw new NullPointerException("Parameter to be converted can not be null");
/*     */     }
/*     */     
/*  70 */     byte[] converted = new byte[toBeConverted.length * 2];
/*  71 */     for (int i = 0; i < toBeConverted.length; i++) {
/*  72 */       byte b = toBeConverted[i];
/*  73 */       converted[i * 2] = HEX_BYTES[b >> 4 & 0xF];
/*  74 */       converted[i * 2 + 1] = HEX_BYTES[b & 0xF];
/*     */     } 
/*     */     
/*  77 */     return converted;
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
/*     */   public static byte[] convertFromHex(char[] toConvert) {
/*  90 */     if (toConvert.length % 2 != 0) {
/*  91 */       throw new IllegalArgumentException("The supplied character array must contain an even number of hex chars.");
/*     */     }
/*     */     
/*  94 */     byte[] response = new byte[toConvert.length / 2];
/*     */     
/*  96 */     for (int i = 0; i < response.length; i++) {
/*  97 */       int posOne = i * 2;
/*  98 */       response[i] = (byte)(toByte(toConvert, posOne) << 4 | toByte(toConvert, posOne + 1));
/*     */     } 
/*     */     
/* 101 */     return response;
/*     */   }
/*     */   
/*     */   private static byte toByte(char[] toConvert, int pos) {
/* 105 */     int response = Character.digit(toConvert[pos], 16);
/* 106 */     if (response < 0 || response > 15) {
/* 107 */       throw new IllegalArgumentException("Non-hex character '" + toConvert[pos] + "' at index=" + pos);
/*     */     }
/*     */     
/* 110 */     return (byte)response;
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
/*     */   public static byte[] convertFromHex(String toConvert) {
/* 123 */     return convertFromHex(toConvert.toCharArray());
/*     */   }
/*     */   
/*     */   public static void main(String[] args) {
/* 127 */     byte[] toConvert = new byte[256];
/* 128 */     for (int i = 0; i < toConvert.length; i++) {
/* 129 */       toConvert[i] = (byte)i;
/*     */     }
/*     */     
/* 132 */     String hexValue = convertToHexString(toConvert);
/*     */     
/* 134 */     System.out.println("Converted - " + hexValue);
/*     */     
/* 136 */     byte[] convertedBack = convertFromHex(hexValue);
/*     */     
/* 138 */     StringBuilder sb = new StringBuilder();
/* 139 */     for (byte current : convertedBack) {
/* 140 */       sb.append(current).append(" ");
/*     */     }
/* 142 */     System.out.println("Converted Back " + sb.toString());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\HexConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */