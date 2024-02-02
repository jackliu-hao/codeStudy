/*     */ package org.wildfly.common.flags;
/*     */ 
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.function.Consumer;
/*     */ import org.wildfly.common.Assert;
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
/*     */ public abstract class Flags<E extends Enum<E>, This extends Flags<E, This>>
/*     */   extends AbstractSet<E>
/*     */   implements SortedSet<E>
/*     */ {
/*     */   final int bits;
/*     */   
/*     */   protected Flags(int bits) {
/*  52 */     this.bits = bits;
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
/*     */   public final int size() {
/* 101 */     return Integer.bitCount(this.bits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final E first() {
/* 110 */     int bits = this.bits;
/* 111 */     if (bits == 0) throw new NoSuchElementException(); 
/* 112 */     return itemOf(Integer.numberOfTrailingZeros(Integer.lowestOneBit(bits)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final E last() {
/* 121 */     int bits = this.bits;
/* 122 */     if (bits == 0) throw new NoSuchElementException(); 
/* 123 */     return itemOf(Integer.numberOfTrailingZeros(Integer.highestOneBit(bits)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Comparator<? super E> comparator() {
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 141 */     return (this.bits == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This headSet(E toElement) {
/* 151 */     Assert.checkNotNullParam("toElement", toElement);
/* 152 */     return value(this.bits & bitOf((Enum<?>)toElement) - 1);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This tailSet(E fromElement) {
/* 162 */     Assert.checkNotNullParam("fromElement", fromElement);
/* 163 */     return value(this.bits & (bitOf((Enum<?>)fromElement) - 1 ^ 0xFFFFFFFF));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This subSet(E fromElement, E toElement) {
/* 174 */     Assert.checkNotNullParam("fromElement", fromElement);
/* 175 */     Assert.checkNotNullParam("toElement", toElement);
/* 176 */     return value(this.bits & bitOf((Enum<?>)toElement) - 1 & (bitOf((Enum<?>)fromElement) - 1 ^ 0xFFFFFFFF));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Object[] toArray() {
/* 185 */     int b = this.bits;
/* 186 */     Object[] array = new Object[Integer.bitCount(b)];
/* 187 */     int idx = 0;
/* 188 */     while (Integer.bitCount(b) > 0) {
/* 189 */       int lob = Integer.lowestOneBit(b);
/* 190 */       array[idx + 1] = itemOf(Integer.numberOfTrailingZeros(lob));
/* 191 */       b ^= lob;
/*     */     } 
/* 193 */     return array;
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
/*     */   public final <T> T[] toArray(T[] array) {
/* 205 */     int b = this.bits;
/* 206 */     int size = Integer.bitCount(b);
/* 207 */     if (size > array.length) {
/* 208 */       array = Arrays.copyOf(array, size);
/*     */     }
/* 210 */     int idx = 0;
/* 211 */     while (Integer.bitCount(b) > 0) {
/* 212 */       int lob = Integer.lowestOneBit(b);
/* 213 */       array[idx + 1] = (T)itemOf(Integer.numberOfTrailingZeros(lob));
/* 214 */       b ^= lob;
/*     */     } 
/* 216 */     return array;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean contains(E flag) {
/* 226 */     return (flag != null && (this.bits & bitOf((Enum<?>)flag)) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean contains(Object o) {
/* 236 */     return contains(castItemOrNull(o));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean containsAll(Collection<?> c) {
/* 246 */     if (c.getClass() == getClass()) {
/* 247 */       return containsAll(castThis(c));
/*     */     }
/* 249 */     for (Object o : c) {
/* 250 */       if (!contains(o)) return false; 
/*     */     } 
/* 252 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean containsAll(This other) {
/* 263 */     int otherBits = ((Flags)other).bits;
/* 264 */     return ((this.bits & otherBits) == otherBits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean containsAll(E flag1, E flag2) {
/* 275 */     return (contains(flag1) && contains(flag2));
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
/*     */   public final boolean containsAll(E flag1, E flag2, E flag3) {
/* 287 */     return (containsAll(flag1, flag2) && contains(flag3));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean containsAny(This other) {
/* 297 */     return (other != null && (this.bits & ((Flags)other).bits) != 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean containsAny(E flag1, E flag2) {
/* 308 */     return (contains(flag1) || contains(flag2));
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
/*     */   public final boolean containsAny(E flag1, E flag2, E flag3) {
/* 320 */     return (containsAny(flag1, flag2) || contains(flag3));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This complement() {
/* 329 */     return value(this.bits ^ 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This with(E flag) {
/* 339 */     return (flag == null) ? this_() : value(this.bits | bitOf((Enum<?>)flag));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This with(E flag1, E flag2) {
/* 350 */     return with(flag1).with(flag2);
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
/*     */   public final This with(E flag1, E flag2, E flag3) {
/* 362 */     return with(flag1, flag2).with(flag3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public final This with(E... flags) {
/* 373 */     if (flags == null) return this_(); 
/* 374 */     int b = this.bits;
/* 375 */     for (E flag : flags) {
/* 376 */       if (flag != null) b |= bitOf((Enum<?>)flag); 
/*     */     } 
/* 378 */     return value(b);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This with(This other) {
/* 388 */     return (other == null) ? this_() : value(this.bits | ((Flags)other).bits);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This without(E flag) {
/* 398 */     return (flag == null) ? this_() : value(this.bits & (bitOf((Enum<?>)flag) ^ 0xFFFFFFFF));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final This without(This other) {
/* 408 */     return (other == null) ? this_() : value(this.bits & (((Flags)other).bits ^ 0xFFFFFFFF));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean equals(Object o) {
/* 418 */     return (o == this || (o instanceof Set && equals((Set)o)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean equals(Set<?> o) {
/* 428 */     return (o == this || (o.containsAll(this) && containsAll(o)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final boolean equals(This o) {
/* 438 */     return (o == this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int hashCode() {
/* 447 */     int hc = 0;
/* 448 */     int b = this.bits;
/* 449 */     while (b != 0) {
/* 450 */       int lob = Integer.lowestOneBit(b);
/* 451 */       hc += itemOf(Integer.numberOfTrailingZeros(lob)).hashCode();
/* 452 */       b ^= lob;
/*     */     } 
/* 454 */     return hc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Iterator<E> iterator() {
/* 463 */     return new Iterator<E>() {
/* 464 */         int b = Flags.this.bits;
/*     */         
/*     */         public boolean hasNext() {
/* 467 */           return (this.b != 0);
/*     */         }
/*     */         
/*     */         public E next() {
/* 471 */           int b = this.b;
/* 472 */           if (b == 0) throw new NoSuchElementException(); 
/* 473 */           int lob = Integer.lowestOneBit(b);
/* 474 */           E item = (E)Flags.this.itemOf(Integer.numberOfTrailingZeros(lob));
/* 475 */           this.b = b ^ lob;
/* 476 */           return item;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final Iterator<E> descendingIterator() {
/* 487 */     return new Iterator<E>() {
/* 488 */         int b = Flags.this.bits;
/*     */         
/*     */         public boolean hasNext() {
/* 491 */           return (this.b != 0);
/*     */         }
/*     */         
/*     */         public E next() {
/* 495 */           int b = this.b;
/* 496 */           if (b == 0) throw new NoSuchElementException(); 
/* 497 */           int hob = Integer.highestOneBit(b);
/* 498 */           E item = (E)Flags.this.itemOf(Integer.numberOfTrailingZeros(hob));
/* 499 */           this.b = b ^ hob;
/* 500 */           return item;
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void forEach(Consumer<? super E> action) {
/* 511 */     Assert.checkNotNullParam("action", action);
/* 512 */     int b = this.bits;
/* 513 */     while (b != 0) {
/* 514 */       int lob = Integer.lowestOneBit(b);
/* 515 */       action.accept(itemOf(Integer.numberOfTrailingZeros(lob)));
/* 516 */       b ^= lob;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final String toString() {
/* 526 */     StringBuilder buf = new StringBuilder();
/* 527 */     buf.append(getClass().getSimpleName()).append('[');
/*     */     
/* 529 */     int bits = this.bits;
/* 530 */     if (bits != 0) {
/* 531 */       int lob = Integer.lowestOneBit(bits);
/* 532 */       buf.append(itemOf(Integer.numberOfTrailingZeros(lob)));
/* 533 */       bits ^= lob;
/* 534 */       while (bits != 0) {
/* 535 */         buf.append(' ');
/* 536 */         lob = Integer.lowestOneBit(bits);
/* 537 */         buf.append(itemOf(Integer.numberOfTrailingZeros(lob)));
/* 538 */         bits ^= lob;
/*     */       } 
/*     */     } 
/* 541 */     buf.append(']');
/* 542 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private static int bitOf(Enum<?> item) {
/* 546 */     return 1 << item.ordinal();
/*     */   }
/*     */   
/*     */   protected abstract This value(int paramInt);
/*     */   
/*     */   protected abstract This this_();
/*     */   
/*     */   protected abstract E itemOf(int paramInt);
/*     */   
/*     */   protected abstract E castItemOrNull(Object paramObject);
/*     */   
/*     */   protected abstract This castThis(Object paramObject);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\flags\Flags.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */