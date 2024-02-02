/*     */ package cn.hutool.core.comparator;
/*     */ 
/*     */ import cn.hutool.core.lang.Chain;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.BitSet;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
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
/*     */ public class ComparatorChain<E>
/*     */   implements Chain<Comparator<E>, ComparatorChain<E>>, Comparator<E>, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = -2426725788913962429L;
/*     */   private final List<Comparator<E>> chain;
/*     */   private final BitSet orderingBits;
/*     */   private boolean lock = false;
/*     */   
/*     */   public static <E> ComparatorChain<E> of(Comparator<E> comparator) {
/*  49 */     return of(comparator, false);
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
/*     */   public static <E> ComparatorChain<E> of(Comparator<E> comparator, boolean reverse) {
/*  62 */     return new ComparatorChain<>(comparator, reverse);
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
/*     */   @SafeVarargs
/*     */   public static <E> ComparatorChain<E> of(Comparator<E>... comparators) {
/*  75 */     return of(Arrays.asList(comparators));
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
/*     */   public static <E> ComparatorChain<E> of(List<Comparator<E>> comparators) {
/*  87 */     return new ComparatorChain<>(comparators);
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
/*     */   public static <E> ComparatorChain<E> of(List<Comparator<E>> comparators, BitSet bits) {
/* 100 */     return new ComparatorChain<>(comparators, bits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain() {
/* 108 */     this(new ArrayList<>(), new BitSet());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain(Comparator<E> comparator) {
/* 117 */     this(comparator, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain(Comparator<E> comparator, boolean reverse) {
/* 127 */     this.chain = new ArrayList<>(1);
/* 128 */     this.chain.add(comparator);
/* 129 */     this.orderingBits = new BitSet(1);
/* 130 */     if (reverse == true) {
/* 131 */       this.orderingBits.set(0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain(List<Comparator<E>> list) {
/* 142 */     this(list, new BitSet(list.size()));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain(List<Comparator<E>> list, BitSet bits) {
/* 153 */     this.chain = list;
/* 154 */     this.orderingBits = bits;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain<E> addComparator(Comparator<E> comparator) {
/* 164 */     return addComparator(comparator, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain<E> addComparator(Comparator<E> comparator, boolean reverse) {
/* 175 */     checkLocked();
/*     */     
/* 177 */     this.chain.add(comparator);
/* 178 */     if (reverse == true) {
/* 179 */       this.orderingBits.set(this.chain.size() - 1);
/*     */     }
/* 181 */     return this;
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
/*     */   public ComparatorChain<E> setComparator(int index, Comparator<E> comparator) throws IndexOutOfBoundsException {
/* 193 */     return setComparator(index, comparator, false);
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
/*     */   public ComparatorChain<E> setComparator(int index, Comparator<E> comparator, boolean reverse) {
/* 205 */     checkLocked();
/*     */     
/* 207 */     this.chain.set(index, comparator);
/* 208 */     if (reverse == true) {
/* 209 */       this.orderingBits.set(index);
/*     */     } else {
/* 211 */       this.orderingBits.clear(index);
/*     */     } 
/* 213 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain<E> setForwardSort(int index) {
/* 223 */     checkLocked();
/* 224 */     this.orderingBits.clear(index);
/* 225 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComparatorChain<E> setReverseSort(int index) {
/* 235 */     checkLocked();
/* 236 */     this.orderingBits.set(index);
/* 237 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 246 */     return this.chain.size();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isLocked() {
/* 255 */     return this.lock;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<Comparator<E>> iterator() {
/* 260 */     return this.chain.iterator();
/*     */   }
/*     */ 
/*     */   
/*     */   public ComparatorChain<E> addChain(Comparator<E> element) {
/* 265 */     return addComparator(element);
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
/*     */   public int compare(E o1, E o2) throws UnsupportedOperationException {
/* 279 */     if (!this.lock) {
/* 280 */       checkChainIntegrity();
/* 281 */       this.lock = true;
/*     */     } 
/*     */     
/* 284 */     Iterator<Comparator<E>> comparators = this.chain.iterator();
/*     */ 
/*     */     
/* 287 */     for (int comparatorIndex = 0; comparators.hasNext(); comparatorIndex++) {
/* 288 */       Comparator<? super E> comparator = comparators.next();
/* 289 */       int retval = comparator.compare(o1, o2);
/* 290 */       if (retval != 0) {
/*     */         
/* 292 */         if (true == this.orderingBits.get(comparatorIndex)) {
/* 293 */           retval = (retval > 0) ? -1 : 1;
/*     */         }
/* 295 */         return retval;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 300 */     return 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 305 */     int hash = 0;
/* 306 */     if (null != this.chain) {
/* 307 */       hash ^= this.chain.hashCode();
/*     */     }
/* 309 */     if (null != this.orderingBits) {
/* 310 */       hash ^= this.orderingBits.hashCode();
/*     */     }
/* 312 */     return hash;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object object) {
/* 317 */     if (this == object) {
/* 318 */       return true;
/*     */     }
/* 320 */     if (null == object) {
/* 321 */       return false;
/*     */     }
/* 323 */     if (object.getClass().equals(getClass())) {
/* 324 */       ComparatorChain<?> otherChain = (ComparatorChain)object;
/*     */       
/* 326 */       return (Objects.equals(this.orderingBits, otherChain.orderingBits) && this.chain
/* 327 */         .equals(otherChain.chain));
/*     */     } 
/* 329 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkLocked() {
/* 340 */     if (this.lock == true) {
/* 341 */       throw new UnsupportedOperationException("Comparator ordering cannot be changed after the first comparison is performed");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkChainIntegrity() {
/* 351 */     if (this.chain.size() == 0)
/* 352 */       throw new UnsupportedOperationException("ComparatorChains must contain at least one Comparator"); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\ComparatorChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */