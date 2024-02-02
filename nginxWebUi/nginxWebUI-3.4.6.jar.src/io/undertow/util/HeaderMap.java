/*     */ package io.undertow.util;
/*     */ 
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HeaderMap
/*     */   implements Iterable<HeaderValues>
/*     */ {
/*  40 */   private Object[] table = new Object[16]; private int size;
/*     */   private Collection<HttpString> headerNames;
/*     */   
/*     */   private HeaderValues getEntry(HttpString headerName) {
/*  44 */     if (headerName == null) {
/*  45 */       return null;
/*     */     }
/*  47 */     int hc = headerName.hashCode();
/*  48 */     int idx = hc & this.table.length - 1;
/*  49 */     Object o = this.table[idx];
/*  50 */     if (o == null) {
/*  51 */       return null;
/*     */     }
/*     */     
/*  54 */     if (o instanceof HeaderValues) {
/*  55 */       HeaderValues headerValues = (HeaderValues)o;
/*  56 */       if (!headerName.equals(headerValues.key)) {
/*  57 */         return null;
/*     */       }
/*  59 */       return headerValues;
/*     */     } 
/*  61 */     HeaderValues[] row = (HeaderValues[])o;
/*  62 */     for (int i = 0; i < row.length; i++) {
/*  63 */       HeaderValues headerValues = row[i];
/*  64 */       if (headerValues != null && headerName.equals(headerValues.key)) {
/*  65 */         return headerValues;
/*     */       }
/*     */     } 
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private HeaderValues getEntry(String headerName) {
/*  74 */     if (headerName == null) {
/*  75 */       return null;
/*     */     }
/*  77 */     int hc = HttpString.hashCodeOf(headerName);
/*  78 */     int idx = hc & this.table.length - 1;
/*  79 */     Object o = this.table[idx];
/*  80 */     if (o == null) {
/*  81 */       return null;
/*     */     }
/*     */     
/*  84 */     if (o instanceof HeaderValues) {
/*  85 */       HeaderValues headerValues = (HeaderValues)o;
/*  86 */       if (!headerValues.key.equalToString(headerName)) {
/*  87 */         return null;
/*     */       }
/*  89 */       return headerValues;
/*     */     } 
/*  91 */     HeaderValues[] row = (HeaderValues[])o;
/*  92 */     for (int i = 0; i < row.length; i++) {
/*  93 */       HeaderValues headerValues = row[i];
/*  94 */       if (headerValues != null && headerValues.key.equalToString(headerName)) {
/*  95 */         return headerValues;
/*     */       }
/*     */     } 
/*  98 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private HeaderValues removeEntry(HttpString headerName) {
/* 103 */     if (headerName == null) {
/* 104 */       return null;
/*     */     }
/* 106 */     int hc = headerName.hashCode();
/* 107 */     Object[] table = this.table;
/* 108 */     int idx = hc & table.length - 1;
/* 109 */     Object o = table[idx];
/* 110 */     if (o == null) {
/* 111 */       return null;
/*     */     }
/*     */     
/* 114 */     if (o instanceof HeaderValues) {
/* 115 */       HeaderValues headerValues = (HeaderValues)o;
/* 116 */       if (!headerName.equals(headerValues.key)) {
/* 117 */         return null;
/*     */       }
/* 119 */       table[idx] = null;
/* 120 */       this.size--;
/* 121 */       return headerValues;
/*     */     } 
/* 123 */     HeaderValues[] row = (HeaderValues[])o;
/* 124 */     for (int i = 0; i < row.length; i++) {
/* 125 */       HeaderValues headerValues = row[i];
/* 126 */       if (headerValues != null && headerName.equals(headerValues.key)) {
/* 127 */         row[i] = null;
/* 128 */         this.size--;
/* 129 */         return headerValues;
/*     */       } 
/*     */     } 
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private HeaderValues removeEntry(String headerName) {
/* 138 */     if (headerName == null) {
/* 139 */       return null;
/*     */     }
/* 141 */     int hc = HttpString.hashCodeOf(headerName);
/* 142 */     Object[] table = this.table;
/* 143 */     int idx = hc & table.length - 1;
/* 144 */     Object o = table[idx];
/* 145 */     if (o == null) {
/* 146 */       return null;
/*     */     }
/*     */     
/* 149 */     if (o instanceof HeaderValues) {
/* 150 */       HeaderValues headerValues = (HeaderValues)o;
/* 151 */       if (!headerValues.key.equalToString(headerName)) {
/* 152 */         return null;
/*     */       }
/* 154 */       table[idx] = null;
/* 155 */       this.size--;
/* 156 */       return headerValues;
/*     */     } 
/* 158 */     HeaderValues[] row = (HeaderValues[])o;
/* 159 */     for (int i = 0; i < row.length; i++) {
/* 160 */       HeaderValues headerValues = row[i];
/* 161 */       if (headerValues != null && headerValues.key.equalToString(headerName)) {
/* 162 */         row[i] = null;
/* 163 */         this.size--;
/* 164 */         return headerValues;
/*     */       } 
/*     */     } 
/* 167 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private void resize() {
/* 172 */     int oldLen = this.table.length;
/* 173 */     if (oldLen == 1073741824) {
/*     */       return;
/*     */     }
/* 176 */     assert Integer.bitCount(oldLen) == 1;
/* 177 */     Object[] newTable = Arrays.copyOf(this.table, oldLen << 1);
/* 178 */     this.table = newTable;
/* 179 */     for (int i = 0; i < oldLen; i++) {
/* 180 */       if (newTable[i] != null)
/*     */       {
/*     */         
/* 183 */         if (newTable[i] instanceof HeaderValues) {
/* 184 */           HeaderValues e = (HeaderValues)newTable[i];
/* 185 */           if ((e.key.hashCode() & oldLen) != 0) {
/* 186 */             newTable[i] = null;
/* 187 */             newTable[i + oldLen] = e;
/*     */           } 
/*     */         } else {
/*     */           
/* 191 */           HeaderValues[] oldRow = (HeaderValues[])newTable[i];
/* 192 */           HeaderValues[] newRow = (HeaderValues[])oldRow.clone();
/* 193 */           int rowLen = oldRow.length;
/* 194 */           newTable[i + oldLen] = newRow;
/*     */           
/* 196 */           for (int j = 0; j < rowLen; j++) {
/* 197 */             HeaderValues item = oldRow[j];
/* 198 */             if (item != null)
/* 199 */               if ((item.key.hashCode() & oldLen) != 0) {
/* 200 */                 oldRow[j] = null;
/*     */               } else {
/* 202 */                 newRow[j] = null;
/*     */               }  
/*     */           } 
/*     */         }  } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private HeaderValues getOrCreateEntry(HttpString headerName) {
/* 210 */     if (headerName == null) {
/* 211 */       return null;
/*     */     }
/* 213 */     int hc = headerName.hashCode();
/* 214 */     Object[] table = this.table;
/* 215 */     int length = table.length;
/* 216 */     int idx = hc & length - 1;
/* 217 */     Object o = table[idx];
/*     */     
/* 219 */     if (o == null) {
/* 220 */       if (this.size >= length >> 1) {
/* 221 */         resize();
/* 222 */         return getOrCreateEntry(headerName);
/*     */       } 
/* 224 */       HeaderValues headerValues = new HeaderValues(headerName);
/* 225 */       table[idx] = headerValues;
/* 226 */       this.size++;
/* 227 */       return headerValues;
/*     */     } 
/* 229 */     return getOrCreateNonEmpty(headerName, table, length, idx, o);
/*     */   }
/*     */ 
/*     */   
/*     */   private HeaderValues getOrCreateNonEmpty(HttpString headerName, Object[] table, int length, int idx, Object o) {
/* 234 */     if (o instanceof HeaderValues) {
/* 235 */       HeaderValues headerValues1 = (HeaderValues)o;
/* 236 */       if (!headerName.equals(headerValues1.key)) {
/* 237 */         if (this.size >= length >> 1) {
/* 238 */           resize();
/* 239 */           return getOrCreateEntry(headerName);
/*     */         } 
/* 241 */         this.size++;
/* 242 */         HeaderValues[] arrayOfHeaderValues = { headerValues1, new HeaderValues(headerName), null, null };
/* 243 */         table[idx] = arrayOfHeaderValues;
/* 244 */         return arrayOfHeaderValues[1];
/*     */       } 
/* 246 */       return headerValues1;
/*     */     } 
/* 248 */     HeaderValues[] row = (HeaderValues[])o;
/* 249 */     int empty = -1;
/* 250 */     for (int i = 0; i < row.length; i++) {
/* 251 */       HeaderValues headerValues1 = row[i];
/* 252 */       if (headerValues1 != null) {
/* 253 */         if (headerName.equals(headerValues1.key)) {
/* 254 */           return headerValues1;
/*     */         }
/* 256 */       } else if (empty == -1) {
/* 257 */         empty = i;
/*     */       } 
/*     */     } 
/* 260 */     if (this.size >= length >> 1) {
/* 261 */       resize();
/* 262 */       return getOrCreateEntry(headerName);
/*     */     } 
/* 264 */     this.size++;
/* 265 */     HeaderValues headerValues = new HeaderValues(headerName);
/* 266 */     if (empty != -1) {
/* 267 */       row[empty] = headerValues;
/*     */     } else {
/* 269 */       if (row.length >= 16) {
/* 270 */         throw new SecurityException("Excessive collisions");
/*     */       }
/* 272 */       HeaderValues[] newRow = Arrays.<HeaderValues>copyOf(row, row.length + 3);
/* 273 */       newRow[row.length] = headerValues;
/* 274 */       table[idx] = newRow;
/*     */     } 
/* 276 */     return headerValues;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderValues get(HttpString headerName) {
/* 283 */     return getEntry(headerName);
/*     */   }
/*     */   
/*     */   public HeaderValues get(String headerName) {
/* 287 */     return getEntry(headerName);
/*     */   }
/*     */   
/*     */   public String getFirst(HttpString headerName) {
/* 291 */     HeaderValues headerValues = getEntry(headerName);
/* 292 */     if (headerValues == null) return null; 
/* 293 */     return headerValues.getFirst();
/*     */   }
/*     */   
/*     */   public String getFirst(String headerName) {
/* 297 */     HeaderValues headerValues = getEntry(headerName);
/* 298 */     if (headerValues == null) return null; 
/* 299 */     return headerValues.getFirst();
/*     */   }
/*     */   
/*     */   public String get(HttpString headerName, int index) throws IndexOutOfBoundsException {
/* 303 */     if (headerName == null) {
/* 304 */       return null;
/*     */     }
/* 306 */     HeaderValues headerValues = getEntry(headerName);
/* 307 */     if (headerValues == null) {
/* 308 */       return null;
/*     */     }
/* 310 */     return headerValues.get(index);
/*     */   }
/*     */   
/*     */   public String get(String headerName, int index) throws IndexOutOfBoundsException {
/* 314 */     if (headerName == null) {
/* 315 */       return null;
/*     */     }
/* 317 */     HeaderValues headerValues = getEntry(headerName);
/* 318 */     if (headerValues == null) {
/* 319 */       return null;
/*     */     }
/* 321 */     return headerValues.get(index);
/*     */   }
/*     */   
/*     */   public String getLast(HttpString headerName) {
/* 325 */     if (headerName == null) {
/* 326 */       return null;
/*     */     }
/* 328 */     HeaderValues headerValues = getEntry(headerName);
/* 329 */     if (headerValues == null) return null; 
/* 330 */     return headerValues.getLast();
/*     */   }
/*     */   
/*     */   public String getLast(String headerName) {
/* 334 */     if (headerName == null) {
/* 335 */       return null;
/*     */     }
/* 337 */     HeaderValues headerValues = getEntry(headerName);
/* 338 */     if (headerValues == null) return null; 
/* 339 */     return headerValues.getLast();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int count(HttpString headerName) {
/* 345 */     if (headerName == null) {
/* 346 */       return 0;
/*     */     }
/* 348 */     HeaderValues headerValues = getEntry(headerName);
/* 349 */     if (headerValues == null) {
/* 350 */       return 0;
/*     */     }
/* 352 */     return headerValues.size();
/*     */   }
/*     */   
/*     */   public int count(String headerName) {
/* 356 */     if (headerName == null) {
/* 357 */       return 0;
/*     */     }
/* 359 */     HeaderValues headerValues = getEntry(headerName);
/* 360 */     if (headerValues == null) {
/* 361 */       return 0;
/*     */     }
/* 363 */     return headerValues.size();
/*     */   }
/*     */   
/*     */   public int size() {
/* 367 */     return this.size;
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
/*     */   public long fastIterate() {
/* 381 */     Object[] table = this.table;
/* 382 */     int len = table.length;
/* 383 */     int ri = 0;
/*     */     
/* 385 */     while (ri < len) {
/* 386 */       Object item = table[ri];
/* 387 */       if (item != null) {
/* 388 */         if (item instanceof HeaderValues) {
/* 389 */           return ri << 32L;
/*     */         }
/* 391 */         HeaderValues[] row = (HeaderValues[])item;
/* 392 */         int ci = 0;
/* 393 */         int rowLen = row.length;
/* 394 */         while (ci < rowLen) {
/* 395 */           if (row[ci] != null) {
/* 396 */             return ri << 32L | ci & 0xFFFFFFFFL;
/*     */           }
/* 398 */           ci++;
/*     */         } 
/*     */       } 
/*     */       
/* 402 */       ri++;
/*     */     } 
/* 404 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long fastIterateNonEmpty() {
/* 413 */     Object[] table = this.table;
/* 414 */     int len = table.length;
/* 415 */     int ri = 0;
/*     */     
/* 417 */     while (ri < len) {
/* 418 */       Object item = table[ri];
/* 419 */       if (item != null) {
/* 420 */         if (item instanceof HeaderValues) {
/* 421 */           if (!((HeaderValues)item).isEmpty()) {
/* 422 */             return ri << 32L;
/*     */           }
/*     */         } else {
/* 425 */           HeaderValues[] row = (HeaderValues[])item;
/* 426 */           int ci = 0;
/* 427 */           int rowLen = row.length;
/* 428 */           while (ci < rowLen) {
/* 429 */             if (row[ci] != null && !row[ci].isEmpty()) {
/* 430 */               return ri << 32L | ci & 0xFFFFFFFFL;
/*     */             }
/* 432 */             ci++;
/*     */           } 
/*     */         } 
/*     */       }
/* 436 */       ri++;
/*     */     } 
/* 438 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long fiNext(long cookie) {
/* 448 */     if (cookie == -1L) return -1L; 
/* 449 */     Object[] table = this.table;
/* 450 */     int len = table.length;
/* 451 */     int ri = (int)(cookie >> 32L);
/* 452 */     int ci = (int)cookie;
/* 453 */     Object item = table[ri];
/* 454 */     if (item instanceof HeaderValues[]) {
/* 455 */       HeaderValues[] row = (HeaderValues[])item;
/* 456 */       int rowLen = row.length;
/* 457 */       if (++ci >= rowLen) {
/* 458 */         ri++; ci = 0;
/* 459 */       } else if (row[ci] != null) {
/* 460 */         return ri << 32L | ci & 0xFFFFFFFFL;
/*     */       } 
/*     */     } else {
/* 463 */       ri++; ci = 0;
/*     */     } 
/* 465 */     while (ri < len) {
/* 466 */       item = table[ri];
/* 467 */       if (item instanceof HeaderValues)
/* 468 */         return ri << 32L; 
/* 469 */       if (item instanceof HeaderValues[]) {
/* 470 */         HeaderValues[] row = (HeaderValues[])item;
/* 471 */         int rowLen = row.length;
/* 472 */         while (ci < rowLen) {
/* 473 */           if (row[ci] != null) {
/* 474 */             return ri << 32L | ci & 0xFFFFFFFFL;
/*     */           }
/* 476 */           ci++;
/*     */         } 
/*     */       } 
/* 479 */       ci = 0;
/* 480 */       ri++;
/*     */     } 
/* 482 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long fiNextNonEmpty(long cookie) {
/* 492 */     if (cookie == -1L) return -1L; 
/* 493 */     Object[] table = this.table;
/* 494 */     int len = table.length;
/* 495 */     int ri = (int)(cookie >> 32L);
/* 496 */     int ci = (int)cookie;
/* 497 */     Object item = table[ri];
/* 498 */     if (item instanceof HeaderValues[]) {
/* 499 */       HeaderValues[] row = (HeaderValues[])item;
/* 500 */       int rowLen = row.length;
/* 501 */       if (++ci >= rowLen) {
/* 502 */         ri++; ci = 0;
/* 503 */       } else if (row[ci] != null && !row[ci].isEmpty()) {
/* 504 */         return ri << 32L | ci & 0xFFFFFFFFL;
/*     */       } 
/*     */     } else {
/* 507 */       ri++; ci = 0;
/*     */     } 
/* 509 */     while (ri < len) {
/* 510 */       item = table[ri];
/* 511 */       if (item instanceof HeaderValues && !((HeaderValues)item).isEmpty())
/* 512 */         return ri << 32L; 
/* 513 */       if (item instanceof HeaderValues[]) {
/* 514 */         HeaderValues[] row = (HeaderValues[])item;
/* 515 */         int rowLen = row.length;
/* 516 */         while (ci < rowLen) {
/* 517 */           if (row[ci] != null && !row[ci].isEmpty()) {
/* 518 */             return ri << 32L | ci & 0xFFFFFFFFL;
/*     */           }
/* 520 */           ci++;
/*     */         } 
/*     */       } 
/* 523 */       ci = 0;
/* 524 */       ri++;
/*     */     } 
/* 526 */     return -1L;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderValues fiCurrent(long cookie) {
/*     */     try {
/* 538 */       Object[] table = this.table;
/* 539 */       int ri = (int)(cookie >> 32L);
/* 540 */       int ci = (int)cookie;
/* 541 */       Object item = table[ri];
/* 542 */       if (item instanceof HeaderValues[])
/* 543 */         return ((HeaderValues[])item)[ci]; 
/* 544 */       if (ci == 0) {
/* 545 */         return (HeaderValues)item;
/*     */       }
/* 547 */       throw new NoSuchElementException();
/*     */     }
/* 549 */     catch (RuntimeException e) {
/* 550 */       throw new NoSuchElementException();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Iterable<String> eachValue(HttpString headerName) {
/* 555 */     if (headerName == null) {
/* 556 */       return Collections.emptyList();
/*     */     }
/* 558 */     HeaderValues entry = getEntry(headerName);
/* 559 */     if (entry == null) {
/* 560 */       return Collections.emptyList();
/*     */     }
/* 562 */     return entry;
/*     */   }
/*     */   
/*     */   public Iterator<HeaderValues> iterator() {
/* 566 */     return new Iterator<HeaderValues>() {
/* 567 */         final Object[] table = HeaderMap.this.table;
/*     */         boolean consumed;
/*     */         
/*     */         private HeaderValues _next() {
/*     */           HeaderValues headerValues;
/*     */           while (true) {
/* 573 */             if (this.ri >= this.table.length) {
/* 574 */               return null;
/*     */             }
/* 576 */             Object o = this.table[this.ri];
/* 577 */             if (o == null) {
/*     */               
/* 579 */               this.ri++;
/* 580 */               this.ci = 0;
/* 581 */               this.consumed = false;
/*     */               continue;
/*     */             } 
/* 584 */             if (o instanceof HeaderValues) {
/*     */               
/* 586 */               if (this.ci > 0 || this.consumed) {
/* 587 */                 this.ri++;
/* 588 */                 this.ci = 0;
/* 589 */                 this.consumed = false;
/*     */                 continue;
/*     */               } 
/* 592 */               return (HeaderValues)o;
/*     */             } 
/* 594 */             HeaderValues[] row = (HeaderValues[])o;
/* 595 */             int len = row.length;
/* 596 */             if (this.ci >= len) {
/* 597 */               this.ri++;
/* 598 */               this.ci = 0;
/* 599 */               this.consumed = false;
/*     */               continue;
/*     */             } 
/* 602 */             if (this.consumed) {
/* 603 */               this.ci++;
/* 604 */               this.consumed = false;
/*     */               continue;
/*     */             } 
/* 607 */             headerValues = row[this.ci];
/* 608 */             if (headerValues == null) {
/* 609 */               this.ci++; continue;
/*     */             }  break;
/*     */           } 
/* 612 */           return headerValues;
/*     */         }
/*     */         int ri; int ci;
/*     */         
/*     */         public boolean hasNext() {
/* 617 */           return (_next() != null);
/*     */         }
/*     */         
/*     */         public HeaderValues next() {
/* 621 */           HeaderValues next = _next();
/* 622 */           if (next == null) {
/* 623 */             throw new NoSuchElementException();
/*     */           }
/* 625 */           this.consumed = true;
/* 626 */           return next;
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {}
/*     */       };
/*     */   }
/*     */   
/*     */   public Collection<HttpString> getHeaderNames() {
/* 635 */     if (this.headerNames != null) {
/* 636 */       return this.headerNames;
/*     */     }
/* 638 */     return this.headerNames = new AbstractCollection<HttpString>() {
/*     */         public boolean contains(Object o) {
/* 640 */           return (o instanceof HttpString && HeaderMap.this.getEntry((HttpString)o) != null);
/*     */         }
/*     */         
/*     */         public boolean add(HttpString httpString) {
/* 644 */           HeaderMap.this.getOrCreateEntry(httpString);
/* 645 */           return true;
/*     */         }
/*     */         
/*     */         public boolean remove(Object o) {
/* 649 */           if (!(o instanceof HttpString)) return false; 
/* 650 */           HttpString s = (HttpString)o;
/* 651 */           HeaderValues entry = HeaderMap.this.getEntry(s);
/* 652 */           if (entry == null) {
/* 653 */             return false;
/*     */           }
/* 655 */           entry.clear();
/* 656 */           return true;
/*     */         }
/*     */         
/*     */         public void clear() {
/* 660 */           HeaderMap.this.clear();
/*     */         }
/*     */         
/*     */         public Iterator<HttpString> iterator() {
/* 664 */           final Iterator<HeaderValues> iterator = HeaderMap.this.iterator();
/* 665 */           return new Iterator<HttpString>() {
/*     */               public boolean hasNext() {
/* 667 */                 return iterator.hasNext();
/*     */               }
/*     */               
/*     */               public HttpString next() {
/* 671 */                 return ((HeaderValues)iterator.next()).getHeaderName();
/*     */               }
/*     */               
/*     */               public void remove() {
/* 675 */                 iterator.remove();
/*     */               }
/*     */             };
/*     */         }
/*     */         
/*     */         public int size() {
/* 681 */           return HeaderMap.this.size();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderMap add(HttpString headerName, String headerValue) {
/* 689 */     addLast(headerName, headerValue);
/* 690 */     return this;
/*     */   }
/*     */   
/*     */   public HeaderMap addFirst(HttpString headerName, String headerValue) {
/* 694 */     if (headerName == null) {
/* 695 */       throw new IllegalArgumentException("headerName is null");
/*     */     }
/* 697 */     if (headerValue == null) {
/* 698 */       return this;
/*     */     }
/* 700 */     getOrCreateEntry(headerName).addFirst(headerValue);
/* 701 */     return this;
/*     */   }
/*     */   
/*     */   public HeaderMap addLast(HttpString headerName, String headerValue) {
/* 705 */     if (headerName == null) {
/* 706 */       throw new IllegalArgumentException("headerName is null");
/*     */     }
/* 708 */     if (headerValue == null) {
/* 709 */       return this;
/*     */     }
/* 711 */     getOrCreateEntry(headerName).addLast(headerValue);
/* 712 */     return this;
/*     */   }
/*     */   
/*     */   public HeaderMap add(HttpString headerName, long headerValue) {
/* 716 */     add(headerName, Long.toString(headerValue));
/* 717 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public HeaderMap addAll(HttpString headerName, Collection<String> headerValues) {
/* 722 */     if (headerName == null) {
/* 723 */       throw new IllegalArgumentException("headerName is null");
/*     */     }
/* 725 */     if (headerValues == null || headerValues.isEmpty()) {
/* 726 */       return this;
/*     */     }
/* 728 */     getOrCreateEntry(headerName).addAll(headerValues);
/* 729 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderMap put(HttpString headerName, String headerValue) {
/* 735 */     if (headerName == null) {
/* 736 */       throw new IllegalArgumentException("headerName is null");
/*     */     }
/* 738 */     if (headerValue == null) {
/* 739 */       remove(headerName);
/* 740 */       return this;
/*     */     } 
/* 742 */     HeaderValues headerValues = getOrCreateEntry(headerName);
/* 743 */     headerValues.clear();
/* 744 */     headerValues.add(headerValue);
/* 745 */     return this;
/*     */   }
/*     */   
/*     */   public HeaderMap put(HttpString headerName, long headerValue) {
/* 749 */     if (headerName == null) {
/* 750 */       throw new IllegalArgumentException("headerName is null");
/*     */     }
/* 752 */     HeaderValues entry = getOrCreateEntry(headerName);
/* 753 */     entry.clear();
/* 754 */     entry.add(Long.toString(headerValue));
/* 755 */     return this;
/*     */   }
/*     */   
/*     */   public HeaderMap putAll(HttpString headerName, Collection<String> headerValues) {
/* 759 */     if (headerName == null) {
/* 760 */       throw new IllegalArgumentException("headerName is null");
/*     */     }
/* 762 */     if (headerValues == null || headerValues.isEmpty()) {
/* 763 */       remove(headerName);
/* 764 */       return this;
/*     */     } 
/* 766 */     HeaderValues entry = getOrCreateEntry(headerName);
/* 767 */     entry.clear();
/* 768 */     entry.addAll(headerValues);
/* 769 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HeaderMap clear() {
/* 775 */     Arrays.fill(this.table, (Object)null);
/* 776 */     this.size = 0;
/* 777 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Collection<String> remove(HttpString headerName) {
/* 783 */     if (headerName == null) {
/* 784 */       return Collections.emptyList();
/*     */     }
/* 786 */     Collection<String> values = removeEntry(headerName);
/* 787 */     return (values != null) ? values : Collections.<String>emptyList();
/*     */   }
/*     */   
/*     */   public Collection<String> remove(String headerName) {
/* 791 */     if (headerName == null) {
/* 792 */       return Collections.emptyList();
/*     */     }
/* 794 */     Collection<String> values = removeEntry(headerName);
/* 795 */     return (values != null) ? values : Collections.<String>emptyList();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(HttpString headerName) {
/* 801 */     HeaderValues headerValues = getEntry(headerName);
/* 802 */     if (headerValues == null) {
/* 803 */       return false;
/*     */     }
/* 805 */     Object v = headerValues.value;
/* 806 */     if (v instanceof String) {
/* 807 */       return true;
/*     */     }
/* 809 */     String[] list = (String[])v;
/* 810 */     for (int i = 0; i < list.length; i++) {
/* 811 */       if (list[i] != null) {
/* 812 */         return true;
/*     */       }
/*     */     } 
/* 815 */     return false;
/*     */   }
/*     */   
/*     */   public boolean contains(String headerName) {
/* 819 */     HeaderValues headerValues = getEntry(headerName);
/* 820 */     if (headerValues == null) {
/* 821 */       return false;
/*     */     }
/* 823 */     Object v = headerValues.value;
/* 824 */     if (v instanceof String) {
/* 825 */       return true;
/*     */     }
/* 827 */     String[] list = (String[])v;
/* 828 */     for (int i = 0; i < list.length; i++) {
/* 829 */       if (list[i] != null) {
/* 830 */         return true;
/*     */       }
/*     */     } 
/* 833 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 840 */     return (o == this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 845 */     return super.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 850 */     StringBuilder sb = new StringBuilder("{");
/* 851 */     boolean first = true;
/* 852 */     for (HttpString name : getHeaderNames()) {
/* 853 */       if (first) {
/* 854 */         first = false;
/*     */       } else {
/* 856 */         sb.append(", ");
/*     */       } 
/* 858 */       sb.append(name);
/* 859 */       sb.append("=[");
/* 860 */       boolean f = true;
/* 861 */       for (String val : get(name)) {
/* 862 */         if (f) {
/* 863 */           f = false;
/*     */         } else {
/* 865 */           sb.append(", ");
/*     */         } 
/* 867 */         sb.append(val);
/*     */       } 
/* 869 */       sb.append("]");
/*     */     } 
/* 871 */     sb.append("}");
/* 872 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\HeaderMap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */