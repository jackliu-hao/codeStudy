package org.apache.commons.compress.compressors.bzip2;

import java.util.BitSet;

class BlockSort {
   private static final int QSORT_STACK_SIZE = 1000;
   private static final int FALLBACK_QSORT_STACK_SIZE = 100;
   private static final int STACK_SIZE = 1000;
   private int workDone;
   private int workLimit;
   private boolean firstAttempt;
   private final int[] stack_ll = new int[1000];
   private final int[] stack_hh = new int[1000];
   private final int[] stack_dd = new int[1000];
   private final int[] mainSort_runningOrder = new int[256];
   private final int[] mainSort_copy = new int[256];
   private final boolean[] mainSort_bigDone = new boolean[256];
   private final int[] ftab = new int[65537];
   private final char[] quadrant;
   private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
   private int[] eclass;
   private static final int[] INCS = new int[]{1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484};
   private static final int SMALL_THRESH = 20;
   private static final int DEPTH_THRESH = 10;
   private static final int WORK_FACTOR = 30;
   private static final int SETMASK = 2097152;
   private static final int CLEARMASK = -2097153;

   BlockSort(BZip2CompressorOutputStream.Data data) {
      this.quadrant = data.sfmap;
   }

   void blockSort(BZip2CompressorOutputStream.Data data, int last) {
      this.workLimit = 30 * last;
      this.workDone = 0;
      this.firstAttempt = true;
      if (last + 1 < 10000) {
         this.fallbackSort(data, last);
      } else {
         this.mainSort(data, last);
         if (this.firstAttempt && this.workDone > this.workLimit) {
            this.fallbackSort(data, last);
         }
      }

      int[] fmap = data.fmap;
      data.origPtr = -1;

      for(int i = 0; i <= last; ++i) {
         if (fmap[i] == 0) {
            data.origPtr = i;
            break;
         }
      }

   }

   final void fallbackSort(BZip2CompressorOutputStream.Data data, int last) {
      data.block[0] = data.block[last + 1];
      this.fallbackSort(data.fmap, data.block, last + 1);

      int i;
      for(i = 0; i < last + 1; ++i) {
         int var10002 = data.fmap[i]--;
      }

      for(i = 0; i < last + 1; ++i) {
         if (data.fmap[i] == -1) {
            data.fmap[i] = last;
            break;
         }
      }

   }

   private void fallbackSimpleSort(int[] fmap, int[] eclass, int lo, int hi) {
      if (lo != hi) {
         int j;
         int i;
         int tmp;
         int ec_tmp;
         if (hi - lo > 3) {
            for(i = hi - 4; i >= lo; --i) {
               tmp = fmap[i];
               ec_tmp = eclass[tmp];

               for(j = i + 4; j <= hi && ec_tmp > eclass[fmap[j]]; j += 4) {
                  fmap[j - 4] = fmap[j];
               }

               fmap[j - 4] = tmp;
            }
         }

         for(i = hi - 1; i >= lo; --i) {
            tmp = fmap[i];
            ec_tmp = eclass[tmp];

            for(j = i + 1; j <= hi && ec_tmp > eclass[fmap[j]]; ++j) {
               fmap[j - 1] = fmap[j];
            }

            fmap[j - 1] = tmp;
         }

      }
   }

   private void fswap(int[] fmap, int zz1, int zz2) {
      int zztmp = fmap[zz1];
      fmap[zz1] = fmap[zz2];
      fmap[zz2] = zztmp;
   }

   private void fvswap(int[] fmap, int yyp1, int yyp2, int yyn) {
      while(yyn > 0) {
         this.fswap(fmap, yyp1, yyp2);
         ++yyp1;
         ++yyp2;
         --yyn;
      }

   }

   private int fmin(int a, int b) {
      return a < b ? a : b;
   }

   private void fpush(int sp, int lz, int hz) {
      this.stack_ll[sp] = lz;
      this.stack_hh[sp] = hz;
   }

   private int[] fpop(int sp) {
      return new int[]{this.stack_ll[sp], this.stack_hh[sp]};
   }

   private void fallbackQSort3(int[] fmap, int[] eclass, int loSt, int hiSt) {
      long r = 0L;
      int sp = 0;
      this.fpush(sp++, loSt, hiSt);

      while(true) {
         label69:
         while(sp > 0) {
            --sp;
            int[] s = this.fpop(sp);
            int lo = s[0];
            int hi = s[1];
            if (hi - lo < 10) {
               this.fallbackSimpleSort(fmap, eclass, lo, hi);
            } else {
               r = (r * 7621L + 1L) % 32768L;
               long r3 = r % 3L;
               long med;
               if (r3 == 0L) {
                  med = (long)eclass[fmap[lo]];
               } else if (r3 == 1L) {
                  med = (long)eclass[fmap[lo + hi >>> 1]];
               } else {
                  med = (long)eclass[fmap[hi]];
               }

               int ltLo = lo;
               int unLo = lo;
               int gtHi = hi;
               int unHi = hi;

               while(true) {
                  while(true) {
                     int n;
                     if (unLo <= unHi) {
                        n = eclass[fmap[unLo]] - (int)med;
                        if (n == 0) {
                           this.fswap(fmap, unLo, ltLo);
                           ++ltLo;
                           ++unLo;
                           continue;
                        }

                        if (n <= 0) {
                           ++unLo;
                           continue;
                        }
                     }

                     while(unLo <= unHi) {
                        n = eclass[fmap[unHi]] - (int)med;
                        if (n == 0) {
                           this.fswap(fmap, unHi, gtHi);
                           --gtHi;
                           --unHi;
                        } else {
                           if (n < 0) {
                              break;
                           }

                           --unHi;
                        }
                     }

                     if (unLo > unHi) {
                        if (gtHi >= ltLo) {
                           n = this.fmin(ltLo - lo, unLo - ltLo);
                           this.fvswap(fmap, lo, unLo - n, n);
                           int m = this.fmin(hi - gtHi, gtHi - unHi);
                           this.fvswap(fmap, unHi + 1, hi - m + 1, m);
                           n = lo + unLo - ltLo - 1;
                           m = hi - (gtHi - unHi) + 1;
                           if (n - lo > hi - m) {
                              this.fpush(sp++, lo, n);
                              this.fpush(sp++, m, hi);
                           } else {
                              this.fpush(sp++, m, hi);
                              this.fpush(sp++, lo, n);
                           }
                        }
                        continue label69;
                     }

                     this.fswap(fmap, unLo, unHi);
                     ++unLo;
                     --unHi;
                  }
               }
            }
         }

         return;
      }
   }

   private int[] getEclass() {
      if (this.eclass == null) {
         this.eclass = new int[this.quadrant.length / 2];
      }

      return this.eclass;
   }

   final void fallbackSort(int[] fmap, byte[] block, int nblock) {
      int[] ftab = new int[257];
      int[] eclass = this.getEclass();

      int i;
      for(i = 0; i < nblock; ++i) {
         eclass[i] = 0;
      }

      for(i = 0; i < nblock; ++i) {
         ++ftab[block[i] & 255];
      }

      for(i = 1; i < 257; ++i) {
         ftab[i] += ftab[i - 1];
      }

      int j;
      int k;
      for(i = 0; i < nblock; fmap[k] = i++) {
         j = block[i] & 255;
         k = ftab[j] - 1;
         ftab[j] = k;
      }

      int nBhtab = 64 + nblock;
      BitSet bhtab = new BitSet(nBhtab);

      for(i = 0; i < 256; ++i) {
         bhtab.set(ftab[i]);
      }

      for(i = 0; i < 32; ++i) {
         bhtab.set(nblock + 2 * i);
         bhtab.clear(nblock + 2 * i + 1);
      }

      int H = 1;

      int nNotDone;
      do {
         j = 0;

         for(i = 0; i < nblock; ++i) {
            if (bhtab.get(i)) {
               j = i;
            }

            k = fmap[i] - H;
            if (k < 0) {
               k += nblock;
            }

            eclass[k] = j;
         }

         nNotDone = 0;
         int r = -1;

         label71:
         while(true) {
            int l;
            do {
               k = r + 1;
               k = bhtab.nextClearBit(k);
               l = k - 1;
               if (l >= nblock) {
                  break label71;
               }

               k = bhtab.nextSetBit(k + 1);
               r = k - 1;
               if (r >= nblock) {
                  break label71;
               }
            } while(r <= l);

            nNotDone += r - l + 1;
            this.fallbackQSort3(fmap, eclass, l, r);
            int cc = -1;

            for(i = l; i <= r; ++i) {
               int cc1 = eclass[fmap[i]];
               if (cc != cc1) {
                  bhtab.set(i);
                  cc = cc1;
               }
            }
         }

         H *= 2;
      } while(H <= nblock && nNotDone != 0);

   }

   private boolean mainSimpleSort(BZip2CompressorOutputStream.Data dataShadow, int lo, int hi, int d, int lastShadow) {
      int bigN = hi - lo + 1;
      if (bigN < 2) {
         return this.firstAttempt && this.workDone > this.workLimit;
      } else {
         int hp;
         for(hp = 0; INCS[hp] < bigN; ++hp) {
         }

         int[] fmap = dataShadow.fmap;
         char[] quadrant = this.quadrant;
         byte[] block = dataShadow.block;
         int lastPlus1 = lastShadow + 1;
         boolean firstAttemptShadow = this.firstAttempt;
         int workLimitShadow = this.workLimit;
         int workDoneShadow = this.workDone;

         label193:
         while(true) {
            --hp;
            if (hp < 0) {
               break;
            }

            int h = INCS[hp];
            int mj = lo + h - 1;
            int i = lo + h;

            while(i <= hi) {
               for(int k = 3; i <= hi; ++i) {
                  --k;
                  if (k < 0) {
                     break;
                  }

                  int v = fmap[i];
                  int vd = v + d;
                  int j = i;
                  boolean onceRunned = false;
                  int a = 0;

                  int i1;
                  int i2;
                  label185:
                  do {
                     while(true) {
                        while(true) {
                           while(true) {
                              while(true) {
                                 while(true) {
                                    while(true) {
                                       while(true) {
                                          if (onceRunned) {
                                             fmap[j] = a;
                                             if ((j -= h) <= mj) {
                                                break label185;
                                             }
                                          } else {
                                             onceRunned = true;
                                          }

                                          a = fmap[j - h];
                                          i1 = a + d;
                                          if (block[i1 + 1] == block[vd + 1]) {
                                             if (block[i1 + 2] == block[vd + 2]) {
                                                if (block[i1 + 3] == block[vd + 3]) {
                                                   if (block[i1 + 4] == block[vd + 4]) {
                                                      if (block[i1 + 5] == block[vd + 5]) {
                                                         i1 += 6;
                                                         byte var10000 = block[i1];
                                                         i2 = vd + 6;
                                                         if (var10000 == block[i2]) {
                                                            int x = lastShadow;

                                                            while(true) {
                                                               if (x <= 0) {
                                                                  break label185;
                                                               }

                                                               x -= 4;
                                                               if (block[i1 + 1] != block[i2 + 1]) {
                                                                  if ((block[i1 + 1] & 255) <= (block[i2 + 1] & 255)) {
                                                                     break label185;
                                                                  }
                                                                  break;
                                                               }

                                                               if (quadrant[i1] != quadrant[i2]) {
                                                                  if (quadrant[i1] <= quadrant[i2]) {
                                                                     break label185;
                                                                  }
                                                                  break;
                                                               }

                                                               if (block[i1 + 2] != block[i2 + 2]) {
                                                                  if ((block[i1 + 2] & 255) <= (block[i2 + 2] & 255)) {
                                                                     break label185;
                                                                  }
                                                                  break;
                                                               }

                                                               if (quadrant[i1 + 1] != quadrant[i2 + 1]) {
                                                                  if (quadrant[i1 + 1] <= quadrant[i2 + 1]) {
                                                                     break label185;
                                                                  }
                                                                  break;
                                                               }

                                                               if (block[i1 + 3] != block[i2 + 3]) {
                                                                  if ((block[i1 + 3] & 255) <= (block[i2 + 3] & 255)) {
                                                                     break label185;
                                                                  }
                                                                  break;
                                                               }

                                                               if (quadrant[i1 + 2] != quadrant[i2 + 2]) {
                                                                  if (quadrant[i1 + 2] <= quadrant[i2 + 2]) {
                                                                     break label185;
                                                                  }
                                                                  break;
                                                               }

                                                               if (block[i1 + 4] != block[i2 + 4]) {
                                                                  if ((block[i1 + 4] & 255) <= (block[i2 + 4] & 255)) {
                                                                     break label185;
                                                                  }
                                                                  break;
                                                               }

                                                               if (quadrant[i1 + 3] != quadrant[i2 + 3]) {
                                                                  continue label185;
                                                               }

                                                               i1 += 4;
                                                               if (i1 >= lastPlus1) {
                                                                  i1 -= lastPlus1;
                                                               }

                                                               i2 += 4;
                                                               if (i2 >= lastPlus1) {
                                                                  i2 -= lastPlus1;
                                                               }

                                                               ++workDoneShadow;
                                                            }
                                                         } else if ((block[i1] & 255) <= (block[i2] & 255)) {
                                                            break label185;
                                                         }
                                                      } else if ((block[i1 + 5] & 255) <= (block[vd + 5] & 255)) {
                                                         break label185;
                                                      }
                                                   } else if ((block[i1 + 4] & 255) <= (block[vd + 4] & 255)) {
                                                      break label185;
                                                   }
                                                } else if ((block[i1 + 3] & 255) <= (block[vd + 3] & 255)) {
                                                   break label185;
                                                }
                                             } else if ((block[i1 + 2] & 255) <= (block[vd + 2] & 255)) {
                                                break label185;
                                             }
                                          } else if ((block[i1 + 1] & 255) <= (block[vd + 1] & 255)) {
                                             break label185;
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  } while(quadrant[i1 + 3] > quadrant[i2 + 3]);

                  fmap[j] = v;
               }

               if (firstAttemptShadow && i <= hi && workDoneShadow > workLimitShadow) {
                  break label193;
               }
            }
         }

         this.workDone = workDoneShadow;
         return firstAttemptShadow && workDoneShadow > workLimitShadow;
      }
   }

   private static void vswap(int[] fmap, int p1, int p2, int n) {
      int t;
      for(n += p1; p1 < n; fmap[p2++] = t) {
         t = fmap[p1];
         fmap[p1++] = fmap[p2];
      }

   }

   private static byte med3(byte a, byte b, byte c) {
      return a < b ? (b < c ? b : (a < c ? c : a)) : (b > c ? b : (a > c ? c : a));
   }

   private void mainQSort3(BZip2CompressorOutputStream.Data dataShadow, int loSt, int hiSt, int dSt, int last) {
      int[] stack_ll = this.stack_ll;
      int[] stack_hh = this.stack_hh;
      int[] stack_dd = this.stack_dd;
      int[] fmap = dataShadow.fmap;
      byte[] block = dataShadow.block;
      stack_ll[0] = loSt;
      stack_hh[0] = hiSt;
      stack_dd[0] = dSt;
      int sp = 1;

      while(true) {
         label49:
         while(true) {
            --sp;
            if (sp < 0) {
               return;
            }

            int lo = stack_ll[sp];
            int hi = stack_hh[sp];
            int d = stack_dd[sp];
            if (hi - lo >= 20 && d <= 10) {
               int d1 = d + 1;
               int med = med3(block[fmap[lo] + d1], block[fmap[hi] + d1], block[fmap[lo + hi >>> 1] + d1]) & 255;
               int unLo = lo;
               int unHi = hi;
               int ltLo = lo;
               int gtHi = hi;

               while(true) {
                  while(true) {
                     int n;
                     int m;
                     if (unLo <= unHi) {
                        n = (block[fmap[unLo] + d1] & 255) - med;
                        if (n == 0) {
                           m = fmap[unLo];
                           fmap[unLo++] = fmap[ltLo];
                           fmap[ltLo++] = m;
                           continue;
                        }

                        if (n < 0) {
                           ++unLo;
                           continue;
                        }
                     }

                     while(unLo <= unHi) {
                        n = (block[fmap[unHi] + d1] & 255) - med;
                        if (n == 0) {
                           m = fmap[unHi];
                           fmap[unHi--] = fmap[gtHi];
                           fmap[gtHi--] = m;
                        } else {
                           if (n <= 0) {
                              break;
                           }

                           --unHi;
                        }
                     }

                     if (unLo > unHi) {
                        if (gtHi < ltLo) {
                           stack_ll[sp] = lo;
                           stack_hh[sp] = hi;
                           stack_dd[sp] = d1;
                           ++sp;
                        } else {
                           n = ltLo - lo < unLo - ltLo ? ltLo - lo : unLo - ltLo;
                           vswap(fmap, lo, unLo - n, n);
                           m = hi - gtHi < gtHi - unHi ? hi - gtHi : gtHi - unHi;
                           vswap(fmap, unLo, hi - m + 1, m);
                           n = lo + unLo - ltLo - 1;
                           m = hi - (gtHi - unHi) + 1;
                           stack_ll[sp] = lo;
                           stack_hh[sp] = n;
                           stack_dd[sp] = d;
                           ++sp;
                           stack_ll[sp] = n + 1;
                           stack_hh[sp] = m - 1;
                           stack_dd[sp] = d1;
                           ++sp;
                           stack_ll[sp] = m;
                           stack_hh[sp] = hi;
                           stack_dd[sp] = d;
                           ++sp;
                        }
                        continue label49;
                     }

                     n = fmap[unLo];
                     fmap[unLo++] = fmap[unHi];
                     fmap[unHi--] = n;
                  }
               }
            } else if (this.mainSimpleSort(dataShadow, lo, hi, d, last)) {
               return;
            }
         }
      }
   }

   final void mainSort(BZip2CompressorOutputStream.Data dataShadow, int lastShadow) {
      int[] runningOrder = this.mainSort_runningOrder;
      int[] copy = this.mainSort_copy;
      boolean[] bigDone = this.mainSort_bigDone;
      int[] ftab = this.ftab;
      byte[] block = dataShadow.block;
      int[] fmap = dataShadow.fmap;
      char[] quadrant = this.quadrant;
      int workLimitShadow = this.workLimit;
      boolean firstAttemptShadow = this.firstAttempt;
      int c1 = 65537;

      while(true) {
         --c1;
         if (c1 < 0) {
            for(c1 = 0; c1 < 20; ++c1) {
               block[lastShadow + c1 + 2] = block[c1 % (lastShadow + 1) + 1];
            }

            c1 = lastShadow + 20 + 1;

            while(true) {
               --c1;
               if (c1 < 0) {
                  block[0] = block[lastShadow + 1];
                  c1 = block[0] & 255;

                  int i;
                  int ss;
                  for(i = 0; i <= lastShadow; ++i) {
                     ss = block[i + 1] & 255;
                     ++ftab[(c1 << 8) + ss];
                     c1 = ss;
                  }

                  for(i = 1; i <= 65536; ++i) {
                     ftab[i] += ftab[i - 1];
                  }

                  c1 = block[1] & 255;

                  for(i = 0; i < lastShadow; ++i) {
                     ss = block[i + 2] & 255;
                     fmap[--ftab[(c1 << 8) + ss]] = i;
                     c1 = ss;
                  }

                  fmap[--ftab[((block[lastShadow + 1] & 255) << 8) + (block[1] & 255)]] = lastShadow;
                  i = 256;

                  while(true) {
                     --i;
                     if (i < 0) {
                        i = 364;

                        int bbStart;
                        int bbSize;
                        int shifts;
                        int j;
                        int a2update;
                        while(i != 1) {
                           i /= 3;

                           for(ss = i; ss <= 255; ++ss) {
                              bbStart = runningOrder[ss];
                              bbSize = ftab[bbStart + 1 << 8] - ftab[bbStart << 8];
                              shifts = i - 1;
                              j = ss;

                              for(a2update = runningOrder[ss - i]; ftab[a2update + 1 << 8] - ftab[a2update << 8] > bbSize; a2update = runningOrder[j - i]) {
                                 runningOrder[j] = a2update;
                                 j -= i;
                                 if (j <= shifts) {
                                    break;
                                 }
                              }

                              runningOrder[j] = bbStart;
                           }
                        }

                        for(i = 0; i <= 255; ++i) {
                           ss = runningOrder[i];

                           for(bbStart = 0; bbStart <= 255; ++bbStart) {
                              bbSize = (ss << 8) + bbStart;
                              shifts = ftab[bbSize];
                              if ((shifts & 2097152) != 2097152) {
                                 j = shifts & -2097153;
                                 a2update = (ftab[bbSize + 1] & -2097153) - 1;
                                 if (a2update > j) {
                                    this.mainQSort3(dataShadow, j, a2update, 2, lastShadow);
                                    if (firstAttemptShadow && this.workDone > workLimitShadow) {
                                       return;
                                    }
                                 }

                                 ftab[bbSize] = shifts | 2097152;
                              }
                           }

                           for(bbStart = 0; bbStart <= 255; ++bbStart) {
                              copy[bbStart] = ftab[(bbStart << 8) + ss] & -2097153;
                           }

                           bbStart = ftab[ss << 8] & -2097153;

                           for(bbSize = ftab[ss + 1 << 8] & -2097153; bbStart < bbSize; ++bbStart) {
                              shifts = fmap[bbStart];
                              c1 = block[shifts] & 255;
                              if (!bigDone[c1]) {
                                 fmap[copy[c1]] = shifts == 0 ? lastShadow : shifts - 1;
                                 int var10002 = copy[c1]++;
                              }
                           }

                           bbStart = 256;

                           while(true) {
                              --bbStart;
                              if (bbStart < 0) {
                                 bigDone[ss] = true;
                                 if (i < 255) {
                                    bbStart = ftab[ss << 8] & -2097153;
                                    bbSize = (ftab[ss + 1 << 8] & -2097153) - bbStart;

                                    for(shifts = 0; bbSize >> shifts > 65534; ++shifts) {
                                    }

                                    for(j = 0; j < bbSize; ++j) {
                                       a2update = fmap[bbStart + j];
                                       char qVal = (char)(j >> shifts);
                                       quadrant[a2update] = qVal;
                                       if (a2update < 20) {
                                          quadrant[a2update + lastShadow + 1] = qVal;
                                       }
                                    }
                                 }
                                 break;
                              }

                              ftab[(bbStart << 8) + ss] |= 2097152;
                           }
                        }

                        return;
                     }

                     bigDone[i] = false;
                     runningOrder[i] = i;
                  }
               }

               quadrant[c1] = 0;
            }
         }

         ftab[c1] = 0;
      }
   }
}
