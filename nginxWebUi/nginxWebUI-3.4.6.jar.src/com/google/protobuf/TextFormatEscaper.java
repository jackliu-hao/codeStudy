/*     */ package com.google.protobuf;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class TextFormatEscaper
/*     */ {
/*     */   static String escapeBytes(ByteSequence input) {
/*  50 */     StringBuilder builder = new StringBuilder(input.size());
/*  51 */     for (int i = 0; i < input.size(); i++) {
/*  52 */       byte b = input.byteAt(i);
/*  53 */       switch (b) {
/*     */         
/*     */         case 7:
/*  56 */           builder.append("\\a");
/*     */           break;
/*     */         case 8:
/*  59 */           builder.append("\\b");
/*     */           break;
/*     */         case 12:
/*  62 */           builder.append("\\f");
/*     */           break;
/*     */         case 10:
/*  65 */           builder.append("\\n");
/*     */           break;
/*     */         case 13:
/*  68 */           builder.append("\\r");
/*     */           break;
/*     */         case 9:
/*  71 */           builder.append("\\t");
/*     */           break;
/*     */         case 11:
/*  74 */           builder.append("\\v");
/*     */           break;
/*     */         case 92:
/*  77 */           builder.append("\\\\");
/*     */           break;
/*     */         case 39:
/*  80 */           builder.append("\\'");
/*     */           break;
/*     */         case 34:
/*  83 */           builder.append("\\\"");
/*     */           break;
/*     */ 
/*     */         
/*     */         default:
/*  88 */           if (b >= 32 && b <= 126) {
/*  89 */             builder.append((char)b); break;
/*     */           } 
/*  91 */           builder.append('\\');
/*  92 */           builder.append((char)(48 + (b >>> 6 & 0x3)));
/*  93 */           builder.append((char)(48 + (b >>> 3 & 0x7)));
/*  94 */           builder.append((char)(48 + (b & 0x7)));
/*     */           break;
/*     */       } 
/*     */     
/*     */     } 
/*  99 */     return builder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String escapeBytes(final ByteString input) {
/* 109 */     return escapeBytes(new ByteSequence()
/*     */         {
/*     */           public int size()
/*     */           {
/* 113 */             return input.size();
/*     */           }
/*     */ 
/*     */           
/*     */           public byte byteAt(int offset) {
/* 118 */             return input.byteAt(offset);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   static String escapeBytes(final byte[] input) {
/* 125 */     return escapeBytes(new ByteSequence()
/*     */         {
/*     */           public int size()
/*     */           {
/* 129 */             return input.length;
/*     */           }
/*     */ 
/*     */           
/*     */           public byte byteAt(int offset) {
/* 134 */             return input[offset];
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static String escapeText(String input) {
/* 145 */     return escapeBytes(ByteString.copyFromUtf8(input));
/*     */   }
/*     */ 
/*     */   
/*     */   static String escapeDoubleQuotesAndBackslashes(String input) {
/* 150 */     return input.replace("\\", "\\\\").replace("\"", "\\\"");
/*     */   }
/*     */   
/*     */   private static interface ByteSequence {
/*     */     int size();
/*     */     
/*     */     byte byteAt(int param1Int);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\TextFormatEscaper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */