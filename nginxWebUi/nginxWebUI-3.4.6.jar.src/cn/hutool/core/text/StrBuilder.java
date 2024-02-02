/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StrBuilder
/*     */   implements CharSequence, Appendable, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 6341229705927508451L;
/*     */   public static final int DEFAULT_CAPACITY = 16;
/*     */   private char[] value;
/*     */   private int position;
/*     */   
/*     */   public static StrBuilder create() {
/*  40 */     return new StrBuilder();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StrBuilder create(int initialCapacity) {
/*  50 */     return new StrBuilder(initialCapacity);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StrBuilder create(CharSequence... strs) {
/*  61 */     return new StrBuilder(strs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder() {
/*  70 */     this(16);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder(int initialCapacity) {
/*  79 */     this.value = new char[initialCapacity];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder(CharSequence... strs) {
/*  89 */     this(ArrayUtil.isEmpty((Object[])strs) ? 16 : (totalLength(strs) + 16));
/*  90 */     for (CharSequence str : strs) {
/*  91 */       append(str);
/*     */     }
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
/*     */   public StrBuilder append(Object obj) {
/* 105 */     return insert(this.position, obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder append(char c) {
/* 116 */     return insert(this.position, c);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder append(char[] src) {
/* 126 */     if (ArrayUtil.isEmpty(src)) {
/* 127 */       return this;
/*     */     }
/* 129 */     return append(src, 0, src.length);
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
/*     */   public StrBuilder append(char[] src, int srcPos, int length) {
/* 141 */     return insert(this.position, src, srcPos, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public StrBuilder append(CharSequence csq) {
/* 146 */     return insert(this.position, csq);
/*     */   }
/*     */ 
/*     */   
/*     */   public StrBuilder append(CharSequence csq, int start, int end) {
/* 151 */     return insert(this.position, csq, start, end);
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
/*     */   public StrBuilder insert(int index, Object obj) {
/* 164 */     if (obj instanceof CharSequence) {
/* 165 */       return insert(index, (CharSequence)obj);
/*     */     }
/* 167 */     return insert(index, Convert.toStr(obj));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder insert(int index, char c) {
/* 178 */     if (index < 0) {
/* 179 */       index = this.position + index;
/*     */     }
/* 181 */     if (index < 0) {
/* 182 */       throw new StringIndexOutOfBoundsException(index);
/*     */     }
/*     */     
/* 185 */     moveDataAfterIndex(index, 1);
/* 186 */     this.value[index] = c;
/* 187 */     this.position = Math.max(this.position, index) + 1;
/* 188 */     return this;
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
/*     */   public StrBuilder insert(int index, char[] src) {
/* 201 */     if (ArrayUtil.isEmpty(src)) {
/* 202 */       return this;
/*     */     }
/* 204 */     return insert(index, src, 0, src.length);
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
/*     */   public StrBuilder insert(int index, char[] src, int srcPos, int length) {
/* 219 */     if (ArrayUtil.isEmpty(src) || srcPos > src.length || length <= 0) {
/* 220 */       return this;
/*     */     }
/* 222 */     if (index < 0) {
/* 223 */       index = this.position + index;
/*     */     }
/* 225 */     if (index < 0) {
/* 226 */       throw new StringIndexOutOfBoundsException(index);
/*     */     }
/*     */     
/* 229 */     if (srcPos < 0) {
/* 230 */       srcPos = 0;
/* 231 */     } else if (srcPos + length > src.length) {
/*     */       
/* 233 */       length = src.length - srcPos;
/*     */     } 
/*     */     
/* 236 */     moveDataAfterIndex(index, length);
/*     */     
/* 238 */     System.arraycopy(src, srcPos, this.value, index, length);
/* 239 */     this.position = Math.max(this.position, index) + length;
/* 240 */     return this;
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
/*     */   public StrBuilder insert(int index, CharSequence csq) {
/* 253 */     if (index < 0) {
/* 254 */       index = this.position + index;
/*     */     }
/* 256 */     if (index < 0) {
/* 257 */       throw new StringIndexOutOfBoundsException(index);
/*     */     }
/*     */     
/* 260 */     if (null == csq) {
/* 261 */       csq = "";
/*     */     }
/* 263 */     int len = csq.length();
/* 264 */     moveDataAfterIndex(index, csq.length());
/* 265 */     if (csq instanceof String) {
/* 266 */       ((String)csq).getChars(0, len, this.value, index);
/* 267 */     } else if (csq instanceof StringBuilder) {
/* 268 */       ((StringBuilder)csq).getChars(0, len, this.value, index);
/* 269 */     } else if (csq instanceof StringBuffer) {
/* 270 */       ((StringBuffer)csq).getChars(0, len, this.value, index);
/* 271 */     } else if (csq instanceof StrBuilder) {
/* 272 */       ((StrBuilder)csq).getChars(0, len, this.value, index);
/*     */     } else {
/* 274 */       for (int i = 0, j = this.position; i < len; i++, j++) {
/* 275 */         this.value[j] = csq.charAt(i);
/*     */       }
/*     */     } 
/* 278 */     this.position = Math.max(this.position, index) + len;
/* 279 */     return this;
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
/*     */   public StrBuilder insert(int index, CharSequence csq, int start, int end) {
/* 294 */     if (csq == null) {
/* 295 */       csq = "null";
/*     */     }
/* 297 */     int csqLen = csq.length();
/* 298 */     if (start > csqLen) {
/* 299 */       return this;
/*     */     }
/* 301 */     if (start < 0) {
/* 302 */       start = 0;
/*     */     }
/* 304 */     if (end > csqLen) {
/* 305 */       end = csqLen;
/*     */     }
/* 307 */     if (start >= end) {
/* 308 */       return this;
/*     */     }
/* 310 */     if (index < 0) {
/* 311 */       index = this.position + index;
/*     */     }
/* 313 */     if (index < 0) {
/* 314 */       throw new StringIndexOutOfBoundsException(index);
/*     */     }
/*     */     
/* 317 */     int length = end - start;
/* 318 */     moveDataAfterIndex(index, length);
/* 319 */     for (int i = start, j = this.position; i < end; i++, j++) {
/* 320 */       this.value[j] = csq.charAt(i);
/*     */     }
/* 322 */     this.position = Math.max(this.position, index) + length;
/* 323 */     return this;
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
/*     */   public StrBuilder getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
/* 338 */     if (srcBegin < 0) {
/* 339 */       srcBegin = 0;
/*     */     }
/* 341 */     if (srcEnd < 0) {
/* 342 */       srcEnd = 0;
/* 343 */     } else if (srcEnd > this.position) {
/* 344 */       srcEnd = this.position;
/*     */     } 
/* 346 */     if (srcBegin > srcEnd) {
/* 347 */       throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
/*     */     }
/* 349 */     System.arraycopy(this.value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
/* 350 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasContent() {
/* 359 */     return (this.position > 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 368 */     return (this.position == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder clear() {
/* 377 */     return reset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder reset() {
/* 386 */     this.position = 0;
/* 387 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder delTo(int newPosition) {
/* 398 */     if (newPosition < 0) {
/* 399 */       newPosition = 0;
/*     */     }
/* 401 */     return del(newPosition, this.position);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StrBuilder del(int start, int end) throws StringIndexOutOfBoundsException {
/* 421 */     if (start < 0) {
/* 422 */       start = 0;
/*     */     }
/*     */     
/* 425 */     if (end >= this.position) {
/*     */       
/* 427 */       this.position = start;
/* 428 */       return this;
/* 429 */     }  if (end < 0)
/*     */     {
/* 431 */       end = 0;
/*     */     }
/*     */     
/* 434 */     int len = end - start;
/*     */     
/* 436 */     if (len > 0) {
/* 437 */       System.arraycopy(this.value, start + len, this.value, start, this.position - end);
/* 438 */       this.position -= len;
/* 439 */     } else if (len < 0) {
/* 440 */       throw new StringIndexOutOfBoundsException("Start is greater than End.");
/*     */     } 
/* 442 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString(boolean isReset) {
/* 452 */     if (this.position > 0) {
/* 453 */       String s = new String(this.value, 0, this.position);
/* 454 */       if (isReset) {
/* 455 */         reset();
/*     */       }
/* 457 */       return s;
/*     */     } 
/* 459 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toStringAndReset() {
/* 468 */     return toString(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 476 */     return toString(false);
/*     */   }
/*     */ 
/*     */   
/*     */   public int length() {
/* 481 */     return this.position;
/*     */   }
/*     */ 
/*     */   
/*     */   public char charAt(int index) {
/* 486 */     if (index < 0) {
/* 487 */       index = this.position + index;
/*     */     }
/* 489 */     if (index < 0 || index > this.position) {
/* 490 */       throw new StringIndexOutOfBoundsException(index);
/*     */     }
/* 492 */     return this.value[index];
/*     */   }
/*     */ 
/*     */   
/*     */   public CharSequence subSequence(int start, int end) {
/* 497 */     return subString(start, end);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String subString(int start) {
/* 507 */     return subString(start, this.position);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String subString(int start, int end) {
/* 518 */     return new String(this.value, start, end - start);
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
/*     */   private void moveDataAfterIndex(int index, int length) {
/* 530 */     ensureCapacity(Math.max(this.position, index) + length);
/* 531 */     if (index < this.position) {
/*     */       
/* 533 */       System.arraycopy(this.value, index, this.value, index + length, this.position - index);
/* 534 */     } else if (index > this.position) {
/*     */       
/* 536 */       Arrays.fill(this.value, this.position, index, ' ');
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureCapacity(int minimumCapacity) {
/* 548 */     if (minimumCapacity - this.value.length > 0) {
/* 549 */       expandCapacity(minimumCapacity);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void expandCapacity(int minimumCapacity) {
/* 560 */     int newCapacity = (this.value.length << 1) + 2;
/*     */     
/* 562 */     if (newCapacity - minimumCapacity < 0) {
/* 563 */       newCapacity = minimumCapacity;
/*     */     }
/* 565 */     if (newCapacity < 0) {
/* 566 */       throw new OutOfMemoryError("Capacity is too long and max than Integer.MAX");
/*     */     }
/* 568 */     this.value = Arrays.copyOf(this.value, newCapacity);
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
/*     */   private static int totalLength(CharSequence... strs) {
/* 580 */     int totalLength = 0;
/* 581 */     for (CharSequence str : strs) {
/* 582 */       totalLength += (null == str) ? 0 : str.length();
/*     */     }
/* 584 */     return totalLength;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\StrBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */