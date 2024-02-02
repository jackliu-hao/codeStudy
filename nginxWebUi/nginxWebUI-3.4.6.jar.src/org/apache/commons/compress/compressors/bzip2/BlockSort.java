/*      */ package org.apache.commons.compress.compressors.bzip2;
/*      */ 
/*      */ import java.util.BitSet;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ class BlockSort
/*      */ {
/*      */   private static final int QSORT_STACK_SIZE = 1000;
/*      */   private static final int FALLBACK_QSORT_STACK_SIZE = 100;
/*      */   private static final int STACK_SIZE = 1000;
/*      */   private int workDone;
/*      */   private int workLimit;
/*      */   private boolean firstAttempt;
/*  132 */   private final int[] stack_ll = new int[1000];
/*  133 */   private final int[] stack_hh = new int[1000];
/*  134 */   private final int[] stack_dd = new int[1000];
/*      */   
/*  136 */   private final int[] mainSort_runningOrder = new int[256];
/*  137 */   private final int[] mainSort_copy = new int[256];
/*  138 */   private final boolean[] mainSort_bigDone = new boolean[256];
/*      */   
/*  140 */   private final int[] ftab = new int[65537];
/*      */   
/*      */   private final char[] quadrant;
/*      */   
/*      */   private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
/*      */   
/*      */   private int[] eclass;
/*      */ 
/*      */   
/*      */   BlockSort(BZip2CompressorOutputStream.Data data) {
/*  150 */     this.quadrant = data.sfmap;
/*      */   }
/*      */   
/*      */   void blockSort(BZip2CompressorOutputStream.Data data, int last) {
/*  154 */     this.workLimit = 30 * last;
/*  155 */     this.workDone = 0;
/*  156 */     this.firstAttempt = true;
/*      */     
/*  158 */     if (last + 1 < 10000) {
/*  159 */       fallbackSort(data, last);
/*      */     } else {
/*  161 */       mainSort(data, last);
/*      */       
/*  163 */       if (this.firstAttempt && this.workDone > this.workLimit) {
/*  164 */         fallbackSort(data, last);
/*      */       }
/*      */     } 
/*      */     
/*  168 */     int[] fmap = data.fmap;
/*  169 */     data.origPtr = -1;
/*  170 */     for (int i = 0; i <= last; i++) {
/*  171 */       if (fmap[i] == 0) {
/*  172 */         data.origPtr = i;
/*      */         break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void fallbackSort(BZip2CompressorOutputStream.Data data, int last) {
/*  187 */     data.block[0] = data.block[last + 1];
/*  188 */     fallbackSort(data.fmap, data.block, last + 1); int i;
/*  189 */     for (i = 0; i < last + 1; i++) {
/*  190 */       data.fmap[i] = data.fmap[i] - 1;
/*      */     }
/*  192 */     for (i = 0; i < last + 1; i++) {
/*  193 */       if (data.fmap[i] == -1) {
/*  194 */         data.fmap[i] = last;
/*      */         break;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fallbackSimpleSort(int[] fmap, int[] eclass, int lo, int hi) {
/*  271 */     if (lo == hi) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/*  276 */     if (hi - lo > 3) {
/*  277 */       for (int j = hi - 4; j >= lo; j--) {
/*  278 */         int tmp = fmap[j];
/*  279 */         int ec_tmp = eclass[tmp]; int k;
/*  280 */         for (k = j + 4; k <= hi && ec_tmp > eclass[fmap[k]]; 
/*  281 */           k += 4) {
/*  282 */           fmap[k - 4] = fmap[k];
/*      */         }
/*  284 */         fmap[k - 4] = tmp;
/*      */       } 
/*      */     }
/*      */     
/*  288 */     for (int i = hi - 1; i >= lo; i--) {
/*  289 */       int tmp = fmap[i];
/*  290 */       int ec_tmp = eclass[tmp]; int j;
/*  291 */       for (j = i + 1; j <= hi && ec_tmp > eclass[fmap[j]]; j++) {
/*  292 */         fmap[j - 1] = fmap[j];
/*      */       }
/*  294 */       fmap[j - 1] = tmp;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fswap(int[] fmap, int zz1, int zz2) {
/*  304 */     int zztmp = fmap[zz1];
/*  305 */     fmap[zz1] = fmap[zz2];
/*  306 */     fmap[zz2] = zztmp;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fvswap(int[] fmap, int yyp1, int yyp2, int yyn) {
/*  313 */     while (yyn > 0) {
/*  314 */       fswap(fmap, yyp1, yyp2);
/*  315 */       yyp1++; yyp2++; yyn--;
/*      */     } 
/*      */   }
/*      */   
/*      */   private int fmin(int a, int b) {
/*  320 */     return (a < b) ? a : b;
/*      */   }
/*      */   
/*      */   private void fpush(int sp, int lz, int hz) {
/*  324 */     this.stack_ll[sp] = lz;
/*  325 */     this.stack_hh[sp] = hz;
/*      */   }
/*      */   
/*      */   private int[] fpop(int sp) {
/*  329 */     return new int[] { this.stack_ll[sp], this.stack_hh[sp] };
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void fallbackQSort3(int[] fmap, int[] eclass, int loSt, int hiSt) {
/*  348 */     long r = 0L;
/*  349 */     int sp = 0;
/*  350 */     fpush(sp++, loSt, hiSt);
/*      */     
/*  352 */     while (sp > 0) {
/*  353 */       long med; int[] s = fpop(--sp);
/*  354 */       int lo = s[0], hi = s[1];
/*      */       
/*  356 */       if (hi - lo < 10) {
/*  357 */         fallbackSimpleSort(fmap, eclass, lo, hi);
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         continue;
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  368 */       r = (r * 7621L + 1L) % 32768L;
/*  369 */       long r3 = r % 3L;
/*      */       
/*  371 */       if (r3 == 0L) {
/*  372 */         med = eclass[fmap[lo]];
/*  373 */       } else if (r3 == 1L) {
/*  374 */         med = eclass[fmap[lo + hi >>> 1]];
/*      */       } else {
/*  376 */         med = eclass[fmap[hi]];
/*      */       } 
/*      */       
/*  379 */       int ltLo = lo, unLo = ltLo;
/*  380 */       int gtHi = hi, unHi = gtHi;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       while (true) {
/*  386 */         if (unLo <= unHi) {
/*      */ 
/*      */           
/*  389 */           int i = eclass[fmap[unLo]] - (int)med;
/*  390 */           if (i == 0) {
/*  391 */             fswap(fmap, unLo, ltLo);
/*  392 */             ltLo++; unLo++;
/*      */             continue;
/*      */           } 
/*  395 */           if (i <= 0) {
/*      */ 
/*      */             
/*  398 */             unLo++; continue;
/*      */           } 
/*      */         } 
/*  401 */         while (unLo <= unHi) {
/*      */ 
/*      */           
/*  404 */           int i = eclass[fmap[unHi]] - (int)med;
/*  405 */           if (i == 0) {
/*  406 */             fswap(fmap, unHi, gtHi);
/*  407 */             gtHi--; unHi--;
/*      */             continue;
/*      */           } 
/*  410 */           if (i < 0) {
/*      */             break;
/*      */           }
/*  413 */           unHi--;
/*      */         } 
/*  415 */         if (unLo > unHi) {
/*      */           break;
/*      */         }
/*  418 */         fswap(fmap, unLo, unHi); unLo++; unHi--;
/*      */       } 
/*      */       
/*  421 */       if (gtHi < ltLo) {
/*      */         continue;
/*      */       }
/*      */       
/*  425 */       int n = fmin(ltLo - lo, unLo - ltLo);
/*  426 */       fvswap(fmap, lo, unLo - n, n);
/*  427 */       int m = fmin(hi - gtHi, gtHi - unHi);
/*  428 */       fvswap(fmap, unHi + 1, hi - m + 1, m);
/*      */       
/*  430 */       n = lo + unLo - ltLo - 1;
/*  431 */       m = hi - gtHi - unHi + 1;
/*      */       
/*  433 */       if (n - lo > hi - m) {
/*  434 */         fpush(sp++, lo, n);
/*  435 */         fpush(sp++, m, hi); continue;
/*      */       } 
/*  437 */       fpush(sp++, m, hi);
/*  438 */       fpush(sp++, lo, n);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private int[] getEclass() {
/*  449 */     if (this.eclass == null) {
/*  450 */       this.eclass = new int[this.quadrant.length / 2];
/*      */     }
/*  452 */     return this.eclass;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void fallbackSort(int[] fmap, byte[] block, int nblock) {
/*  473 */     int nNotDone, ftab[] = new int[257];
/*      */ 
/*      */ 
/*      */     
/*  477 */     int[] eclass = getEclass();
/*      */     int i;
/*  479 */     for (i = 0; i < nblock; i++) {
/*  480 */       eclass[i] = 0;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  486 */     for (i = 0; i < nblock; i++) {
/*  487 */       ftab[block[i] & 0xFF] = ftab[block[i] & 0xFF] + 1;
/*      */     }
/*  489 */     for (i = 1; i < 257; i++) {
/*  490 */       ftab[i] = ftab[i] + ftab[i - 1];
/*      */     }
/*      */     
/*  493 */     for (i = 0; i < nblock; i++) {
/*  494 */       int j = block[i] & 0xFF;
/*  495 */       int k = ftab[j] - 1;
/*  496 */       ftab[j] = k;
/*  497 */       fmap[k] = i;
/*      */     } 
/*      */     
/*  500 */     int nBhtab = 64 + nblock;
/*  501 */     BitSet bhtab = new BitSet(nBhtab);
/*  502 */     for (i = 0; i < 256; i++) {
/*  503 */       bhtab.set(ftab[i]);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  513 */     for (i = 0; i < 32; i++) {
/*  514 */       bhtab.set(nblock + 2 * i);
/*  515 */       bhtab.clear(nblock + 2 * i + 1);
/*      */     } 
/*      */ 
/*      */     
/*  519 */     int H = 1;
/*      */     
/*      */     do {
/*  522 */       int j = 0;
/*  523 */       for (i = 0; i < nblock; i++) {
/*  524 */         if (bhtab.get(i)) {
/*  525 */           j = i;
/*      */         }
/*  527 */         int k = fmap[i] - H;
/*  528 */         if (k < 0) {
/*  529 */           k += nblock;
/*      */         }
/*  531 */         eclass[k] = j;
/*      */       } 
/*      */       
/*  534 */       nNotDone = 0;
/*  535 */       int r = -1;
/*      */ 
/*      */       
/*      */       while (true) {
/*  539 */         int k = r + 1;
/*  540 */         k = bhtab.nextClearBit(k);
/*  541 */         int l = k - 1;
/*  542 */         if (l >= nblock) {
/*      */           break;
/*      */         }
/*  545 */         k = bhtab.nextSetBit(k + 1);
/*  546 */         r = k - 1;
/*  547 */         if (r >= nblock) {
/*      */           break;
/*      */         }
/*      */ 
/*      */         
/*  552 */         if (r > l) {
/*  553 */           nNotDone += r - l + 1;
/*  554 */           fallbackQSort3(fmap, eclass, l, r);
/*      */ 
/*      */           
/*  557 */           int cc = -1;
/*  558 */           for (i = l; i <= r; i++) {
/*  559 */             int cc1 = eclass[fmap[i]];
/*  560 */             if (cc != cc1) {
/*  561 */               bhtab.set(i);
/*  562 */               cc = cc1;
/*      */             } 
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/*  568 */       H *= 2;
/*  569 */     } while (H <= nblock && nNotDone != 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  582 */   private static final int[] INCS = new int[] { 1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484 };
/*      */ 
/*      */   
/*      */   private static final int SMALL_THRESH = 20;
/*      */ 
/*      */   
/*      */   private static final int DEPTH_THRESH = 10;
/*      */ 
/*      */   
/*      */   private static final int WORK_FACTOR = 30;
/*      */   
/*      */   private static final int SETMASK = 2097152;
/*      */   
/*      */   private static final int CLEARMASK = -2097153;
/*      */ 
/*      */   
/*      */   private boolean mainSimpleSort(BZip2CompressorOutputStream.Data dataShadow, int lo, int hi, int d, int lastShadow) {
/*  599 */     int bigN = hi - lo + 1;
/*  600 */     if (bigN < 2) {
/*  601 */       return (this.firstAttempt && this.workDone > this.workLimit);
/*      */     }
/*      */     
/*  604 */     int hp = 0;
/*  605 */     while (INCS[hp] < bigN) {
/*  606 */       hp++;
/*      */     }
/*      */     
/*  609 */     int[] fmap = dataShadow.fmap;
/*  610 */     char[] quadrant = this.quadrant;
/*  611 */     byte[] block = dataShadow.block;
/*  612 */     int lastPlus1 = lastShadow + 1;
/*  613 */     boolean firstAttemptShadow = this.firstAttempt;
/*  614 */     int workLimitShadow = this.workLimit;
/*  615 */     int workDoneShadow = this.workDone;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  620 */     label97: while (--hp >= 0) {
/*  621 */       int h = INCS[hp];
/*  622 */       int mj = lo + h - 1;
/*      */       
/*  624 */       for (int i = lo + h; i <= hi; ) {
/*      */         
/*  626 */         for (int k = 3; i <= hi && --k >= 0; i++) {
/*  627 */           int v = fmap[i];
/*  628 */           int vd = v + d;
/*  629 */           int j = i;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  641 */           boolean onceRunned = false;
/*  642 */           int a = 0;
/*      */           
/*      */           while (true) {
/*  645 */             if (onceRunned) {
/*  646 */               fmap[j] = a;
/*  647 */               if ((j -= h) <= mj) {
/*      */                 break;
/*      */               }
/*      */             } else {
/*  651 */               onceRunned = true;
/*      */             } 
/*      */             
/*  654 */             a = fmap[j - h];
/*  655 */             int i1 = a + d;
/*  656 */             int i2 = vd;
/*      */ 
/*      */ 
/*      */             
/*  660 */             if (block[i1 + 1] == block[i2 + 1]) {
/*  661 */               if (block[i1 + 2] == block[i2 + 2]) {
/*  662 */                 if (block[i1 + 3] == block[i2 + 3]) {
/*  663 */                   if (block[i1 + 4] == block[i2 + 4]) {
/*  664 */                     if (block[i1 + 5] == block[i2 + 5]) {
/*  665 */                       i1 += 6; i2 += 6; if (block[i1] == block[i2]) {
/*  666 */                         int x = lastShadow;
/*  667 */                         while (x > 0) {
/*  668 */                           x -= 4;
/*  669 */                           if (block[i1 + 1] == block[i2 + 1]) {
/*  670 */                             if (quadrant[i1] == quadrant[i2]) {
/*  671 */                               if (block[i1 + 2] == block[i2 + 2]) {
/*  672 */                                 if (quadrant[i1 + 1] == quadrant[i2 + 1]) {
/*  673 */                                   if (block[i1 + 3] == block[i2 + 3]) {
/*  674 */                                     if (quadrant[i1 + 2] == quadrant[i2 + 2]) {
/*  675 */                                       if (block[i1 + 4] == block[i2 + 4]) {
/*  676 */                                         if (quadrant[i1 + 3] == quadrant[i2 + 3]) {
/*  677 */                                           i1 += 4; if (i1 >= lastPlus1) {
/*  678 */                                             i1 -= lastPlus1;
/*      */                                           }
/*  680 */                                           i2 += 4; if (i2 >= lastPlus1) {
/*  681 */                                             i2 -= lastPlus1;
/*      */                                           }
/*  683 */                                           workDoneShadow++;
/*      */                                           continue;
/*      */                                         } 
/*  686 */                                         if (quadrant[i1 + 3] > quadrant[i2 + 3]) {
/*      */                                           continue;
/*      */                                         }
/*      */                                         break;
/*      */                                       } 
/*  691 */                                       if ((block[i1 + 4] & 0xFF) > (block[i2 + 4] & 0xFF)) {
/*      */                                         continue;
/*      */                                       }
/*      */                                       break;
/*      */                                     } 
/*  696 */                                     if (quadrant[i1 + 2] > quadrant[i2 + 2]) {
/*      */                                       continue;
/*      */                                     }
/*      */                                     break;
/*      */                                   } 
/*  701 */                                   if ((block[i1 + 3] & 0xFF) > (block[i2 + 3] & 0xFF)) {
/*      */                                     continue;
/*      */                                   }
/*      */                                   break;
/*      */                                 } 
/*  706 */                                 if (quadrant[i1 + 1] > quadrant[i2 + 1]) {
/*      */                                   continue;
/*      */                                 }
/*      */                                 break;
/*      */                               } 
/*  711 */                               if ((block[i1 + 2] & 0xFF) > (block[i2 + 2] & 0xFF)) {
/*      */                                 continue;
/*      */                               }
/*      */                               break;
/*      */                             } 
/*  716 */                             if (quadrant[i1] > quadrant[i2]) {
/*      */                               continue;
/*      */                             }
/*      */                             break;
/*      */                           } 
/*  721 */                           if ((block[i1 + 1] & 0xFF) > (block[i2 + 1] & 0xFF));
/*      */                         } 
/*      */ 
/*      */                         
/*      */                         break;
/*      */                       } 
/*      */ 
/*      */                       
/*  729 */                       if ((block[i1] & 0xFF) > (block[i2] & 0xFF)) {
/*      */                         continue;
/*      */                       }
/*      */                       break;
/*      */                     } 
/*  734 */                     if ((block[i1 + 5] & 0xFF) > (block[i2 + 5] & 0xFF)) {
/*      */                       continue;
/*      */                     }
/*      */                     break;
/*      */                   } 
/*  739 */                   if ((block[i1 + 4] & 0xFF) > (block[i2 + 4] & 0xFF)) {
/*      */                     continue;
/*      */                   }
/*      */                   break;
/*      */                 } 
/*  744 */                 if ((block[i1 + 3] & 0xFF) > (block[i2 + 3] & 0xFF)) {
/*      */                   continue;
/*      */                 }
/*      */                 break;
/*      */               } 
/*  749 */               if ((block[i1 + 2] & 0xFF) > (block[i2 + 2] & 0xFF)) {
/*      */                 continue;
/*      */               }
/*      */               break;
/*      */             } 
/*  754 */             if ((block[i1 + 1] & 0xFF) > (block[i2 + 1] & 0xFF)) {
/*      */               continue;
/*      */             }
/*      */ 
/*      */             
/*      */             break;
/*      */           } 
/*      */           
/*  762 */           fmap[j] = v;
/*      */         } 
/*      */         
/*  765 */         if (firstAttemptShadow && i <= hi && workDoneShadow > workLimitShadow) {
/*      */           break label97;
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  772 */     this.workDone = workDoneShadow;
/*  773 */     return (firstAttemptShadow && workDoneShadow > workLimitShadow);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void vswap(int[] fmap, int p1, int p2, int n) {
/*  785 */     n += p1;
/*  786 */     while (p1 < n) {
/*  787 */       int t = fmap[p1];
/*  788 */       fmap[p1++] = fmap[p2];
/*  789 */       fmap[p2++] = t;
/*      */     } 
/*      */   }
/*      */   
/*      */   private static byte med3(byte a, byte b, byte c) {
/*  794 */     return (a < b) ? ((b < c) ? b : ((a < c) ? c : a)) : ((b > c) ? b : ((a > c) ? c : a));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void mainQSort3(BZip2CompressorOutputStream.Data dataShadow, int loSt, int hiSt, int dSt, int last) {
/*  808 */     int[] stack_ll = this.stack_ll;
/*  809 */     int[] stack_hh = this.stack_hh;
/*  810 */     int[] stack_dd = this.stack_dd;
/*  811 */     int[] fmap = dataShadow.fmap;
/*  812 */     byte[] block = dataShadow.block;
/*      */     
/*  814 */     stack_ll[0] = loSt;
/*  815 */     stack_hh[0] = hiSt;
/*  816 */     stack_dd[0] = dSt;
/*      */     
/*  818 */     for (int sp = 1; --sp >= 0; ) {
/*  819 */       int lo = stack_ll[sp];
/*  820 */       int hi = stack_hh[sp];
/*  821 */       int d = stack_dd[sp];
/*      */       
/*  823 */       if (hi - lo < 20 || d > 10) {
/*  824 */         if (mainSimpleSort(dataShadow, lo, hi, d, last))
/*      */           return; 
/*      */         continue;
/*      */       } 
/*  828 */       int d1 = d + 1;
/*  829 */       int med = med3(block[fmap[lo] + d1], block[fmap[hi] + d1], block[fmap[lo + hi >>> 1] + d1]) & 0xFF;
/*      */ 
/*      */       
/*  832 */       int unLo = lo;
/*  833 */       int unHi = hi;
/*  834 */       int ltLo = lo;
/*  835 */       int gtHi = hi;
/*      */       
/*      */       while (true) {
/*  838 */         if (unLo <= unHi) {
/*  839 */           int i = (block[fmap[unLo] + d1] & 0xFF) - med;
/*      */           
/*  841 */           if (i == 0) {
/*  842 */             int j = fmap[unLo];
/*  843 */             fmap[unLo++] = fmap[ltLo];
/*  844 */             fmap[ltLo++] = j; continue;
/*  845 */           }  if (i < 0) {
/*  846 */             unLo++;
/*      */             
/*      */             continue;
/*      */           } 
/*      */         } 
/*      */         
/*  852 */         while (unLo <= unHi) {
/*  853 */           int i = (block[fmap[unHi] + d1] & 0xFF) - med;
/*      */           
/*  855 */           if (i == 0) {
/*  856 */             int j = fmap[unHi];
/*  857 */             fmap[unHi--] = fmap[gtHi];
/*  858 */             fmap[gtHi--] = j; continue;
/*  859 */           }  if (i > 0) {
/*  860 */             unHi--;
/*      */           }
/*      */         } 
/*      */ 
/*      */ 
/*      */         
/*  866 */         if (unLo > unHi) {
/*      */           break;
/*      */         }
/*  869 */         int temp = fmap[unLo];
/*  870 */         fmap[unLo++] = fmap[unHi];
/*  871 */         fmap[unHi--] = temp;
/*      */       } 
/*      */       
/*  874 */       if (gtHi < ltLo) {
/*  875 */         stack_ll[sp] = lo;
/*  876 */         stack_hh[sp] = hi;
/*  877 */         stack_dd[sp] = d1;
/*  878 */         sp++; continue;
/*      */       } 
/*  880 */       int n = (ltLo - lo < unLo - ltLo) ? (ltLo - lo) : (unLo - ltLo);
/*      */       
/*  882 */       vswap(fmap, lo, unLo - n, n);
/*  883 */       int m = (hi - gtHi < gtHi - unHi) ? (hi - gtHi) : (gtHi - unHi);
/*      */       
/*  885 */       vswap(fmap, unLo, hi - m + 1, m);
/*      */       
/*  887 */       n = lo + unLo - ltLo - 1;
/*  888 */       m = hi - gtHi - unHi + 1;
/*      */       
/*  890 */       stack_ll[sp] = lo;
/*  891 */       stack_hh[sp] = n;
/*  892 */       stack_dd[sp] = d;
/*  893 */       sp++;
/*      */       
/*  895 */       stack_ll[sp] = n + 1;
/*  896 */       stack_hh[sp] = m - 1;
/*  897 */       stack_dd[sp] = d1;
/*  898 */       sp++;
/*      */       
/*  900 */       stack_ll[sp] = m;
/*  901 */       stack_hh[sp] = hi;
/*  902 */       stack_dd[sp] = d;
/*  903 */       sp++;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final void mainSort(BZip2CompressorOutputStream.Data dataShadow, int lastShadow) {
/*  914 */     int[] runningOrder = this.mainSort_runningOrder;
/*  915 */     int[] copy = this.mainSort_copy;
/*  916 */     boolean[] bigDone = this.mainSort_bigDone;
/*  917 */     int[] ftab = this.ftab;
/*  918 */     byte[] block = dataShadow.block;
/*  919 */     int[] fmap = dataShadow.fmap;
/*  920 */     char[] quadrant = this.quadrant;
/*  921 */     int workLimitShadow = this.workLimit;
/*  922 */     boolean firstAttemptShadow = this.firstAttempt;
/*      */     
/*      */     int i;
/*  925 */     for (i = 65537; --i >= 0;) {
/*  926 */       ftab[i] = 0;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  934 */     for (i = 0; i < 20; i++) {
/*  935 */       block[lastShadow + i + 2] = block[i % (lastShadow + 1) + 1];
/*      */     }
/*  937 */     for (i = lastShadow + 20 + 1; --i >= 0;) {
/*  938 */       quadrant[i] = Character.MIN_VALUE;
/*      */     }
/*  940 */     block[0] = block[lastShadow + 1];
/*      */ 
/*      */ 
/*      */     
/*  944 */     int c1 = block[0] & 0xFF; int k;
/*  945 */     for (k = 0; k <= lastShadow; k++) {
/*  946 */       int c2 = block[k + 1] & 0xFF;
/*  947 */       ftab[(c1 << 8) + c2] = ftab[(c1 << 8) + c2] + 1;
/*  948 */       c1 = c2;
/*      */     } 
/*      */     
/*  951 */     for (k = 1; k <= 65536; k++) {
/*  952 */       ftab[k] = ftab[k] + ftab[k - 1];
/*      */     }
/*      */     
/*  955 */     c1 = block[1] & 0xFF;
/*  956 */     for (k = 0; k < lastShadow; k++) {
/*  957 */       int c2 = block[k + 2] & 0xFF;
/*  958 */       ftab[(c1 << 8) + c2] = ftab[(c1 << 8) + c2] - 1; fmap[ftab[(c1 << 8) + c2] - 1] = k;
/*  959 */       c1 = c2;
/*      */     } 
/*      */     
/*  962 */     ftab[((block[lastShadow + 1] & 0xFF) << 8) + (block[1] & 0xFF)] = ftab[((block[lastShadow + 1] & 0xFF) << 8) + (block[1] & 0xFF)] - 1; fmap[ftab[((block[lastShadow + 1] & 0xFF) << 8) + (block[1] & 0xFF)] - 1] = lastShadow;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  968 */     for (k = 256; --k >= 0; ) {
/*  969 */       bigDone[k] = false;
/*  970 */       runningOrder[k] = k;
/*      */     } 
/*      */ 
/*      */     
/*  974 */     for (int h = 364; h != 1; ) {
/*  975 */       h /= 3;
/*  976 */       for (int m = h; m <= 255; m++) {
/*  977 */         int vv = runningOrder[m];
/*  978 */         int a = ftab[vv + 1 << 8] - ftab[vv << 8];
/*  979 */         int b = h - 1;
/*  980 */         int n = m; int ro;
/*  981 */         for (ro = runningOrder[n - h]; ftab[ro + 1 << 8] - ftab[ro << 8] > a; ro = runningOrder[n - h]) {
/*      */           
/*  983 */           runningOrder[n] = ro;
/*  984 */           n -= h;
/*  985 */           if (n <= b) {
/*      */             break;
/*      */           }
/*      */         } 
/*  989 */         runningOrder[n] = vv;
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  996 */     for (int j = 0; j <= 255; j++) {
/*      */ 
/*      */ 
/*      */       
/* 1000 */       int ss = runningOrder[j];
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       int m;
/*      */ 
/*      */ 
/*      */       
/* 1009 */       for (m = 0; m <= 255; m++) {
/* 1010 */         int sb = (ss << 8) + m;
/* 1011 */         int ftab_sb = ftab[sb];
/* 1012 */         if ((ftab_sb & 0x200000) != 2097152) {
/* 1013 */           int lo = ftab_sb & 0xFFDFFFFF;
/* 1014 */           int hi = (ftab[sb + 1] & 0xFFDFFFFF) - 1;
/* 1015 */           if (hi > lo) {
/* 1016 */             mainQSort3(dataShadow, lo, hi, 2, lastShadow);
/* 1017 */             if (firstAttemptShadow && this.workDone > workLimitShadow) {
/*      */               return;
/*      */             }
/*      */           } 
/*      */           
/* 1022 */           ftab[sb] = ftab_sb | 0x200000;
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1030 */       for (m = 0; m <= 255; m++) {
/* 1031 */         copy[m] = ftab[(m << 8) + ss] & 0xFFDFFFFF;
/*      */       }
/*      */       int hj;
/* 1034 */       for (m = ftab[ss << 8] & 0xFFDFFFFF, hj = ftab[ss + 1 << 8] & 0xFFDFFFFF; m < hj; m++) {
/* 1035 */         int fmap_j = fmap[m];
/* 1036 */         c1 = block[fmap_j] & 0xFF;
/* 1037 */         if (!bigDone[c1]) {
/* 1038 */           fmap[copy[c1]] = (fmap_j == 0) ? lastShadow : (fmap_j - 1);
/* 1039 */           copy[c1] = copy[c1] + 1;
/*      */         } 
/*      */       } 
/*      */       
/* 1043 */       for (m = 256; --m >= 0;) {
/* 1044 */         ftab[(m << 8) + ss] = ftab[(m << 8) + ss] | 0x200000;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1055 */       bigDone[ss] = true;
/*      */       
/* 1057 */       if (j < 255) {
/* 1058 */         int bbStart = ftab[ss << 8] & 0xFFDFFFFF;
/* 1059 */         int bbSize = (ftab[ss + 1 << 8] & 0xFFDFFFFF) - bbStart;
/* 1060 */         int shifts = 0;
/*      */         
/* 1062 */         while (bbSize >> shifts > 65534) {
/* 1063 */           shifts++;
/*      */         }
/*      */         
/* 1066 */         for (int n = 0; n < bbSize; n++) {
/* 1067 */           int a2update = fmap[bbStart + n];
/* 1068 */           char qVal = (char)(n >> shifts);
/* 1069 */           quadrant[a2update] = qVal;
/* 1070 */           if (a2update < 20)
/* 1071 */             quadrant[a2update + lastShadow + 1] = qVal; 
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\compressors\bzip2\BlockSort.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */