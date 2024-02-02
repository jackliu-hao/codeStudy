/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.bytes.ByteStringBuilder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class Utf8EncodingByteIterator
/*     */   extends ByteIterator
/*     */ {
/*     */   private final CodePointIterator iter;
/*     */   private final boolean escapeNul;
/*     */   private int st;
/*     */   private int cp;
/*     */   private long offset;
/*     */   
/*     */   Utf8EncodingByteIterator(CodePointIterator iter, boolean escapeNul) {
/*  44 */     this.iter = iter;
/*  45 */     this.escapeNul = escapeNul;
/*  46 */     this.cp = -1;
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/*  50 */     return (this.st != 0 || this.iter.hasNext());
/*     */   }
/*     */   
/*     */   public boolean hasPrevious() {
/*  54 */     return (this.st != 0 || this.iter.hasPrevious());
/*     */   }
/*     */   public int next() throws NoSuchElementException {
/*     */     int cp;
/*  58 */     if (!hasNext()) throw new NoSuchElementException(); 
/*  59 */     this.offset++;
/*  60 */     switch (this.st) {
/*     */       case 0:
/*  62 */         cp = this.iter.next();
/*  63 */         if ((cp == 0 && !this.escapeNul) || cp < 128)
/*  64 */           return cp; 
/*  65 */         if (cp < 2048) {
/*  66 */           this.cp = cp;
/*  67 */           this.st = 1;
/*  68 */           return 0xC0 | cp >> 6;
/*  69 */         }  if (cp < 65536) {
/*  70 */           this.cp = cp;
/*  71 */           this.st = 2;
/*  72 */           return 0xE0 | cp >> 12;
/*  73 */         }  if (cp < 1114112) {
/*  74 */           this.cp = cp;
/*  75 */           this.st = 4;
/*  76 */           return 0xF0 | cp >> 18;
/*     */         } 
/*  78 */         this.cp = 65533;
/*  79 */         this.st = 2;
/*  80 */         return 239;
/*     */ 
/*     */       
/*     */       case 1:
/*     */       case 3:
/*     */       case 6:
/*  86 */         this.st = 0;
/*  87 */         return 0x80 | this.cp & 0x3F;
/*     */       
/*     */       case 2:
/*  90 */         this.st = 3;
/*  91 */         return 0x80 | this.cp >> 6 & 0x3F;
/*     */       
/*     */       case 4:
/*  94 */         this.st = 5;
/*  95 */         return 0x80 | this.cp >> 12 & 0x3F;
/*     */       
/*     */       case 5:
/*  98 */         this.st = 6;
/*  99 */         return 0x80 | this.cp >> 6 & 0x3F;
/*     */     } 
/*     */     
/* 102 */     throw Assert.impossibleSwitchCase(this.st);
/*     */   }
/*     */ 
/*     */   
/*     */   public int peekNext() throws NoSuchElementException {
/*     */     int cp;
/* 108 */     if (!hasNext()) throw new NoSuchElementException(); 
/* 109 */     switch (this.st) {
/*     */       case 0:
/* 111 */         cp = this.iter.peekNext();
/* 112 */         if (cp < 128)
/* 113 */           return cp; 
/* 114 */         if (cp < 2048)
/* 115 */           return 0xC0 | cp >> 6; 
/* 116 */         if (cp < 65536)
/* 117 */           return 0xE0 | cp >> 12; 
/* 118 */         if (cp < 1114112) {
/* 119 */           return 0xF0 | cp >> 18;
/*     */         }
/* 121 */         return 239;
/*     */ 
/*     */       
/*     */       case 1:
/*     */       case 3:
/*     */       case 6:
/* 127 */         return 0x80 | this.cp & 0x3F;
/*     */       
/*     */       case 2:
/*     */       case 5:
/* 131 */         return 0x80 | this.cp >> 6 & 0x3F;
/*     */       
/*     */       case 4:
/* 134 */         return 0x80 | this.cp >> 12 & 0x3F;
/*     */     } 
/*     */     
/* 137 */     throw Assert.impossibleSwitchCase(this.st);
/*     */   }
/*     */ 
/*     */   
/*     */   public int previous() throws NoSuchElementException {
/*     */     int cp;
/* 143 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 144 */     this.offset--;
/* 145 */     switch (this.st) {
/*     */       case 0:
/* 147 */         cp = this.iter.previous();
/* 148 */         if ((cp == 0 && !this.escapeNul) || cp < 128)
/* 149 */           return cp; 
/* 150 */         if (cp < 2048) {
/* 151 */           this.cp = cp;
/* 152 */           this.st = 1;
/* 153 */           return 0x80 | cp & 0x3F;
/* 154 */         }  if (cp < 65536) {
/* 155 */           this.cp = cp;
/* 156 */           this.st = 3;
/* 157 */           return 0x80 | cp & 0x3F;
/* 158 */         }  if (cp < 1114112) {
/* 159 */           this.cp = cp;
/* 160 */           this.st = 6;
/* 161 */           return 0x80 | cp & 0x3F;
/*     */         } 
/* 163 */         this.cp = 65533;
/* 164 */         this.st = 3;
/* 165 */         return 189;
/*     */ 
/*     */       
/*     */       case 1:
/* 169 */         this.st = 0;
/* 170 */         return 0xC0 | this.cp >> 6;
/*     */       
/*     */       case 2:
/* 173 */         this.st = 0;
/* 174 */         return 0xE0 | this.cp >> 12;
/*     */       
/*     */       case 3:
/* 177 */         this.st = 2;
/* 178 */         return 0x80 | this.cp >> 6 & 0x3F;
/*     */       
/*     */       case 4:
/* 181 */         this.st = 0;
/* 182 */         return 0xF0 | this.cp >> 18;
/*     */       
/*     */       case 5:
/* 185 */         this.st = 4;
/* 186 */         return 0x80 | this.cp >> 12 & 0x3F;
/*     */       
/*     */       case 6:
/* 189 */         this.st = 5;
/* 190 */         return 0x80 | this.cp >> 6 & 0x3F;
/*     */     } 
/*     */     
/* 193 */     throw Assert.impossibleSwitchCase(this.st);
/*     */   }
/*     */ 
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/*     */     int cp;
/* 199 */     if (!hasPrevious()) throw new NoSuchElementException(); 
/* 200 */     switch (this.st) {
/*     */       case 0:
/* 202 */         cp = this.iter.peekPrevious();
/* 203 */         if ((cp == 0 && !this.escapeNul) || cp < 128)
/* 204 */           return cp; 
/* 205 */         if (cp < 2048)
/* 206 */           return 0x80 | cp & 0x3F; 
/* 207 */         if (cp < 65536)
/* 208 */           return 0x80 | cp & 0x3F; 
/* 209 */         if (cp < 1114112) {
/* 210 */           return 0x80 | cp & 0x3F;
/*     */         }
/* 212 */         return 189;
/*     */ 
/*     */       
/*     */       case 1:
/* 216 */         return 0xC0 | this.cp >> 6;
/*     */       
/*     */       case 2:
/* 219 */         return 0xE0 | this.cp >> 12;
/*     */       
/*     */       case 3:
/*     */       case 6:
/* 223 */         return 0x80 | this.cp >> 6 & 0x3F;
/*     */       
/*     */       case 4:
/* 226 */         return 0xF0 | this.cp >> 18;
/*     */       
/*     */       case 5:
/* 229 */         return 0x80 | this.cp >> 12 & 0x3F;
/*     */     } 
/*     */     
/* 232 */     throw Assert.impossibleSwitchCase(this.st);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteStringBuilder appendTo(ByteStringBuilder builder) {
/* 238 */     if (this.st == 0) {
/*     */       
/* 240 */       int oldLen = builder.length();
/* 241 */       builder.appendUtf8(this.iter);
/* 242 */       this.offset += (builder.length() - oldLen);
/*     */     } else {
/* 244 */       super.appendTo(builder);
/*     */     } 
/* 246 */     return builder;
/*     */   }
/*     */   
/*     */   public long getIndex() {
/* 250 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Utf8EncodingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */