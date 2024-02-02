/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.Arrays;
/*     */ import org.h2.message.DbException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ScriptReader
/*     */   implements Closeable
/*     */ {
/*     */   private final Reader reader;
/*     */   private char[] buffer;
/*     */   private int bufferPos;
/*  33 */   private int bufferStart = -1;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int bufferEnd;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean endOfFile;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean insideRemark;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean blockRemark;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean skipRemarks;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int remarkStart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ScriptReader(Reader paramReader) {
/*  72 */     this.reader = paramReader;
/*  73 */     this.buffer = new char[8192];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/*  82 */       this.reader.close();
/*  83 */     } catch (IOException iOException) {
/*  84 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readStatement() {
/*  95 */     if (this.endOfFile) {
/*  96 */       return null;
/*     */     }
/*     */     try {
/*  99 */       return readStatementLoop();
/* 100 */     } catch (IOException iOException) {
/* 101 */       throw DbException.convertIOException(iOException, null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private String readStatementLoop() throws IOException {
/* 106 */     this.bufferStart = this.bufferPos;
/* 107 */     int i = read();
/*     */     while (true) {
/* 109 */       if (i < 0) {
/* 110 */         this.endOfFile = true;
/* 111 */         if (this.bufferPos - 1 == this.bufferStart)
/* 112 */           return null; 
/*     */         break;
/*     */       } 
/* 115 */       if (i == 59) {
/*     */         break;
/*     */       }
/* 118 */       switch (i) {
/*     */         case 36:
/* 120 */           i = read();
/* 121 */           if (i == 36 && (this.bufferPos - this.bufferStart < 3 || this.buffer[this.bufferPos - 3] <= ' ')) {
/*     */             
/*     */             while (true) {
/* 124 */               i = read();
/* 125 */               if (i < 0) {
/*     */                 break;
/*     */               }
/* 128 */               if (i == 36) {
/* 129 */                 i = read();
/* 130 */                 if (i < 0) {
/*     */                   break;
/*     */                 }
/* 133 */                 if (i == 36) {
/*     */                   break;
/*     */                 }
/*     */               } 
/*     */             } 
/* 138 */             i = read();
/*     */           } 
/*     */           continue;
/*     */         
/*     */         case 39:
/*     */           do {
/* 144 */             i = read();
/* 145 */             if (i < 0) {
/*     */               break;
/*     */             }
/* 148 */           } while (i != 39);
/*     */ 
/*     */ 
/*     */           
/* 152 */           i = read();
/*     */           continue;
/*     */         case 34:
/*     */           do {
/* 156 */             i = read();
/* 157 */             if (i < 0) {
/*     */               break;
/*     */             }
/* 160 */           } while (i != 34);
/*     */ 
/*     */ 
/*     */           
/* 164 */           i = read();
/*     */           continue;
/*     */         case 47:
/* 167 */           i = read();
/* 168 */           if (i == 42) {
/*     */             
/* 170 */             startRemark(true);
/* 171 */             byte b = 1;
/*     */             while (true) {
/* 173 */               i = read();
/* 174 */               if (i < 0) {
/*     */                 break;
/*     */               }
/* 177 */               if (i == 42) {
/* 178 */                 i = read();
/* 179 */                 if (i < 0) {
/* 180 */                   clearRemark();
/*     */                   break;
/*     */                 } 
/* 183 */                 if (i == 47 && 
/* 184 */                   --b == 0) {
/* 185 */                   endRemark(); break;
/*     */                 } 
/*     */                 continue;
/*     */               } 
/* 189 */               if (i == 47) {
/* 190 */                 i = read();
/* 191 */                 if (i < 0) {
/* 192 */                   clearRemark();
/*     */                   break;
/*     */                 } 
/* 195 */                 if (i == 42) {
/* 196 */                   b++;
/*     */                 }
/*     */               } 
/*     */             } 
/* 200 */             i = read(); continue;
/* 201 */           }  if (i == 47) {
/*     */             
/* 203 */             startRemark(false);
/*     */             while (true) {
/* 205 */               i = read();
/* 206 */               if (i < 0) {
/* 207 */                 clearRemark();
/*     */                 break;
/*     */               } 
/* 210 */               if (i == 13 || i == 10) {
/* 211 */                 endRemark();
/*     */                 break;
/*     */               } 
/*     */             } 
/* 215 */             i = read();
/*     */           } 
/*     */           continue;
/*     */         
/*     */         case 45:
/* 220 */           i = read();
/* 221 */           if (i == 45) {
/*     */             
/* 223 */             startRemark(false);
/*     */             while (true) {
/* 225 */               i = read();
/* 226 */               if (i < 0) {
/* 227 */                 clearRemark();
/*     */                 break;
/*     */               } 
/* 230 */               if (i == 13 || i == 10) {
/* 231 */                 endRemark();
/*     */                 break;
/*     */               } 
/*     */             } 
/* 235 */             i = read();
/*     */           } 
/*     */           continue;
/*     */       } 
/*     */       
/* 240 */       i = read();
/*     */     } 
/*     */ 
/*     */     
/* 244 */     return new String(this.buffer, this.bufferStart, this.bufferPos - 1 - this.bufferStart);
/*     */   }
/*     */   
/*     */   private void startRemark(boolean paramBoolean) {
/* 248 */     this.blockRemark = paramBoolean;
/* 249 */     this.remarkStart = this.bufferPos - 2;
/* 250 */     this.insideRemark = true;
/*     */   }
/*     */   
/*     */   private void endRemark() {
/* 254 */     clearRemark();
/* 255 */     this.insideRemark = false;
/*     */   }
/*     */   
/*     */   private void clearRemark() {
/* 259 */     if (this.skipRemarks) {
/* 260 */       Arrays.fill(this.buffer, this.remarkStart, this.bufferPos, ' ');
/*     */     }
/*     */   }
/*     */   
/*     */   private int read() throws IOException {
/* 265 */     if (this.bufferPos >= this.bufferEnd) {
/* 266 */       return readBuffer();
/*     */     }
/* 268 */     return this.buffer[this.bufferPos++];
/*     */   }
/*     */   
/*     */   private int readBuffer() throws IOException {
/* 272 */     if (this.endOfFile) {
/* 273 */       return -1;
/*     */     }
/* 275 */     int i = this.bufferPos - this.bufferStart;
/* 276 */     if (i > 0) {
/* 277 */       char[] arrayOfChar = this.buffer;
/* 278 */       if (i + 4096 > arrayOfChar.length) {
/*     */         
/* 280 */         if (arrayOfChar.length >= 1073741823) {
/* 281 */           throw new IOException("Error in parsing script, statement size exceeds 1G, first 80 characters of statement looks like: " + new String(this.buffer, this.bufferStart, 80));
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 286 */         this.buffer = new char[arrayOfChar.length * 2];
/*     */       } 
/* 288 */       System.arraycopy(arrayOfChar, this.bufferStart, this.buffer, 0, i);
/*     */     } 
/* 290 */     this.remarkStart -= this.bufferStart;
/* 291 */     this.bufferStart = 0;
/* 292 */     this.bufferPos = i;
/* 293 */     int j = this.reader.read(this.buffer, i, 4096);
/* 294 */     if (j == -1) {
/*     */       
/* 296 */       this.bufferEnd = -1024;
/* 297 */       this.endOfFile = true;
/*     */ 
/*     */       
/* 300 */       this.bufferPos++;
/* 301 */       return -1;
/*     */     } 
/* 303 */     this.bufferEnd = i + j;
/* 304 */     return this.buffer[this.bufferPos++];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isInsideRemark() {
/* 314 */     return this.insideRemark;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBlockRemark() {
/* 324 */     return this.blockRemark;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSkipRemarks(boolean paramBoolean) {
/* 333 */     this.skipRemarks = paramBoolean;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\ScriptReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */