/*     */ package org.wildfly.common.iteration;
/*     */ 
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class Utf8DecodingIterator
/*     */   extends CodePointIterator
/*     */ {
/*     */   private final ByteIterator iter;
/*  27 */   private long offset = 0L;
/*     */   
/*     */   Utf8DecodingIterator(ByteIterator iter) {
/*  30 */     this.iter = iter;
/*     */   }
/*     */   
/*     */   public boolean hasNext() {
/*  34 */     return this.iter.hasNext();
/*     */   }
/*     */   
/*     */   public boolean hasPrevious() {
/*  38 */     return (this.offset > 0L);
/*     */   }
/*     */ 
/*     */   
/*     */   private void seekToNext() {
/*  43 */     while (this.iter.hasNext()) {
/*  44 */       int b = this.iter.next();
/*  45 */       if ((b & 0xC0) != 128) {
/*     */         
/*  47 */         this.iter.previous();
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void seekToPrev() {
/*  55 */     while (this.iter.hasPrevious()) {
/*  56 */       int b = this.iter.previous();
/*  57 */       if ((b & 0xC0) != 128) {
/*     */         return;
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public int next() {
/*  64 */     if (!this.iter.hasNext()) throw new NoSuchElementException(); 
/*  65 */     this.offset++;
/*     */     
/*  67 */     int a = this.iter.next();
/*  68 */     if ((a & 0x80) == 0)
/*     */     {
/*  70 */       return a;
/*     */     }
/*  72 */     if ((a & 0xC0) == 128) {
/*     */       
/*  74 */       seekToNext();
/*  75 */       return 65533;
/*     */     } 
/*     */     
/*  78 */     if (!this.iter.hasNext())
/*     */     {
/*  80 */       return 65533;
/*     */     }
/*  82 */     int b = this.iter.next();
/*  83 */     if ((b & 0xC0) != 128) {
/*     */       
/*  85 */       seekToNext();
/*  86 */       return 65533;
/*     */     } 
/*  88 */     if ((a & 0xE0) == 192)
/*     */     {
/*  90 */       return (a & 0x1F) << 6 | b & 0x3F;
/*     */     }
/*     */     
/*  93 */     if (!this.iter.hasNext())
/*     */     {
/*  95 */       return 65533;
/*     */     }
/*  97 */     int c = this.iter.next();
/*  98 */     if ((c & 0xC0) != 128) {
/*     */       
/* 100 */       seekToNext();
/* 101 */       return 65533;
/*     */     } 
/* 103 */     if ((a & 0xF0) == 224)
/*     */     {
/* 105 */       return (a & 0xF) << 12 | (b & 0x3F) << 6 | c & 0x3F;
/*     */     }
/*     */     
/* 108 */     if (!this.iter.hasNext())
/*     */     {
/* 110 */       return 65533;
/*     */     }
/* 112 */     int d = this.iter.next();
/* 113 */     if ((d & 0xC0) != 128) {
/*     */       
/* 115 */       seekToNext();
/* 116 */       return 65533;
/*     */     } 
/* 118 */     if ((a & 0xF8) == 240)
/*     */     {
/* 120 */       return (a & 0x7) << 18 | (b & 0x3F) << 12 | (c & 0x3F) << 6 | d & 0x3F;
/*     */     }
/*     */     
/* 123 */     seekToNext();
/* 124 */     return 65533;
/*     */   }
/*     */   
/*     */   public int peekNext() throws NoSuchElementException {
/* 128 */     if (!this.iter.hasNext()) throw new NoSuchElementException(); 
/* 129 */     int a = this.iter.peekNext();
/* 130 */     if ((a & 0x80) == 0)
/*     */     {
/* 132 */       return a;
/*     */     }
/* 134 */     if ((a & 0xC0) == 128)
/*     */     {
/* 136 */       return 65533;
/*     */     }
/*     */     
/* 139 */     this.iter.next();
/* 140 */     if (!this.iter.hasNext()) {
/* 141 */       this.iter.previous();
/*     */       
/* 143 */       return 65533;
/*     */     } 
/* 145 */     int b = this.iter.peekNext();
/* 146 */     if ((b & 0xC0) != 128) {
/*     */       
/* 148 */       this.iter.previous();
/* 149 */       return 65533;
/*     */     } 
/* 151 */     if ((a & 0xE0) == 192) {
/*     */       
/* 153 */       this.iter.previous();
/* 154 */       return (a & 0x1F) << 6 | b & 0x3F;
/*     */     } 
/*     */     
/* 157 */     this.iter.next();
/* 158 */     if (!this.iter.hasNext()) {
/*     */       
/* 160 */       this.iter.previous();
/* 161 */       this.iter.previous();
/* 162 */       return 65533;
/*     */     } 
/* 164 */     int c = this.iter.peekNext();
/* 165 */     if ((c & 0xC0) != 128) {
/*     */       
/* 167 */       this.iter.previous();
/* 168 */       this.iter.previous();
/* 169 */       return 65533;
/*     */     } 
/* 171 */     if ((a & 0xF0) == 224) {
/*     */       
/* 173 */       this.iter.previous();
/* 174 */       this.iter.previous();
/* 175 */       return (a & 0xF) << 12 | (b & 0x3F) << 6 | c & 0x3F;
/*     */     } 
/*     */     
/* 178 */     this.iter.next();
/* 179 */     if (!this.iter.hasNext()) {
/*     */       
/* 181 */       this.iter.previous();
/* 182 */       this.iter.previous();
/* 183 */       this.iter.previous();
/* 184 */       return 65533;
/*     */     } 
/* 186 */     int d = this.iter.peekNext();
/* 187 */     if ((d & 0xC0) != 128) {
/*     */       
/* 189 */       this.iter.previous();
/* 190 */       this.iter.previous();
/* 191 */       this.iter.previous();
/* 192 */       return 65533;
/*     */     } 
/* 194 */     if ((a & 0xF8) == 240) {
/*     */       
/* 196 */       this.iter.previous();
/* 197 */       this.iter.previous();
/* 198 */       this.iter.previous();
/* 199 */       return (a & 0x7) << 18 | (b & 0x3F) << 12 | (c & 0x3F) << 6 | d & 0x3F;
/*     */     } 
/*     */     
/* 202 */     this.iter.previous();
/* 203 */     this.iter.previous();
/* 204 */     this.iter.previous();
/* 205 */     return 65533;
/*     */   }
/*     */ 
/*     */   
/*     */   public int previous() {
/* 210 */     if (!this.iter.hasPrevious()) throw new NoSuchElementException(); 
/* 211 */     this.offset--;
/*     */     
/* 213 */     int a = this.iter.previous();
/* 214 */     if ((a & 0x80) == 0)
/*     */     {
/* 216 */       return a;
/*     */     }
/* 218 */     if ((a & 0xC0) != 128) {
/*     */       
/* 220 */       seekToPrev();
/* 221 */       return 65533;
/*     */     } 
/* 223 */     int cp = a & 0x3F;
/*     */     
/* 225 */     a = this.iter.previous();
/* 226 */     if ((a & 0xE0) == 192)
/*     */     {
/* 228 */       return (a & 0x1F) << 6 | cp;
/*     */     }
/* 230 */     if ((a & 0xC0) != 128) {
/*     */       
/* 232 */       seekToPrev();
/* 233 */       return 65533;
/*     */     } 
/* 235 */     cp |= (a & 0x3F) << 6;
/*     */     
/* 237 */     a = this.iter.previous();
/* 238 */     if ((a & 0xF0) == 224)
/*     */     {
/* 240 */       return (a & 0xF) << 12 | cp;
/*     */     }
/* 242 */     if ((a & 0xC0) != 128) {
/*     */       
/* 244 */       seekToPrev();
/* 245 */       return 65533;
/*     */     } 
/* 247 */     cp |= (a & 0x3F) << 12;
/*     */     
/* 249 */     a = this.iter.previous();
/* 250 */     if ((a & 0xF8) == 240)
/*     */     {
/* 252 */       return (a & 0x7) << 18 | cp;
/*     */     }
/*     */     
/* 255 */     seekToPrev();
/* 256 */     return 65533;
/*     */   }
/*     */ 
/*     */   
/*     */   public int peekPrevious() throws NoSuchElementException {
/* 261 */     if (!this.iter.hasPrevious()) throw new NoSuchElementException();
/*     */     
/* 263 */     int a = this.iter.peekPrevious();
/* 264 */     if ((a & 0x80) == 0)
/*     */     {
/* 266 */       return a;
/*     */     }
/* 268 */     if ((a & 0xC0) != 128)
/*     */     {
/* 270 */       return 65533;
/*     */     }
/* 272 */     int cp = a & 0x3F;
/*     */     
/* 274 */     this.iter.previous();
/* 275 */     a = this.iter.peekPrevious();
/* 276 */     if ((a & 0xE0) == 192) {
/*     */       
/* 278 */       this.iter.next();
/* 279 */       return (a & 0x1F) << 6 | cp;
/*     */     } 
/* 281 */     if ((a & 0xC0) != 128) {
/*     */       
/* 283 */       this.iter.next();
/* 284 */       return 65533;
/*     */     } 
/* 286 */     cp |= (a & 0x3F) << 6;
/*     */     
/* 288 */     this.iter.previous();
/* 289 */     a = this.iter.peekPrevious();
/* 290 */     if ((a & 0xF0) == 224) {
/*     */       
/* 292 */       this.iter.next();
/* 293 */       this.iter.next();
/* 294 */       return (a & 0xF) << 12 | cp;
/*     */     } 
/* 296 */     if ((a & 0xC0) != 128) {
/*     */       
/* 298 */       this.iter.next();
/* 299 */       this.iter.next();
/* 300 */       return 65533;
/*     */     } 
/* 302 */     cp |= (a & 0x3F) << 12;
/*     */     
/* 304 */     this.iter.previous();
/* 305 */     a = this.iter.peekPrevious();
/* 306 */     if ((a & 0xF8) == 240) {
/*     */       
/* 308 */       this.iter.next();
/* 309 */       this.iter.next();
/* 310 */       this.iter.next();
/* 311 */       return (a & 0x7) << 18 | cp;
/*     */     } 
/*     */     
/* 314 */     this.iter.next();
/* 315 */     this.iter.next();
/* 316 */     this.iter.next();
/* 317 */     return 65533;
/*     */   }
/*     */   
/*     */   public long getIndex() {
/* 321 */     return this.offset;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\Utf8DecodingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */