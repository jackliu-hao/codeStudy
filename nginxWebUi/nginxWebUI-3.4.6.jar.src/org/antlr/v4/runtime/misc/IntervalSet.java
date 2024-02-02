/*     */ package org.antlr.v4.runtime.misc;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Set;
/*     */ import org.antlr.v4.runtime.Vocabulary;
/*     */ import org.antlr.v4.runtime.VocabularyImpl;
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
/*     */ public class IntervalSet
/*     */   implements IntSet
/*     */ {
/*  57 */   public static final IntervalSet COMPLETE_CHAR_SET = of(0, 65534);
/*     */   static {
/*  59 */     COMPLETE_CHAR_SET.setReadonly(true);
/*     */   }
/*     */   
/*  62 */   public static final IntervalSet EMPTY_SET = new IntervalSet(new int[0]);
/*     */   static {
/*  64 */     EMPTY_SET.setReadonly(true);
/*     */   }
/*     */ 
/*     */   
/*     */   protected List<Interval> intervals;
/*     */   
/*     */   protected boolean readonly;
/*     */   
/*     */   public IntervalSet(List<Interval> intervals) {
/*  73 */     this.intervals = intervals;
/*     */   }
/*     */   
/*     */   public IntervalSet(IntervalSet set) {
/*  77 */     this(new int[0]);
/*  78 */     addAll(set);
/*     */   }
/*     */   
/*     */   public IntervalSet(int... els) {
/*  82 */     if (els == null) {
/*  83 */       this.intervals = new ArrayList<Interval>(2);
/*     */     } else {
/*     */       
/*  86 */       this.intervals = new ArrayList<Interval>(els.length);
/*  87 */       for (int e : els) add(e);
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public static IntervalSet of(int a) {
/*  94 */     IntervalSet s = new IntervalSet(new int[0]);
/*  95 */     s.add(a);
/*  96 */     return s;
/*     */   }
/*     */ 
/*     */   
/*     */   public static IntervalSet of(int a, int b) {
/* 101 */     IntervalSet s = new IntervalSet(new int[0]);
/* 102 */     s.add(a, b);
/* 103 */     return s;
/*     */   }
/*     */   
/*     */   public void clear() {
/* 107 */     if (this.readonly) throw new IllegalStateException("can't alter readonly IntervalSet"); 
/* 108 */     this.intervals.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(int el) {
/* 116 */     if (this.readonly) throw new IllegalStateException("can't alter readonly IntervalSet"); 
/* 117 */     add(el, el);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(int a, int b) {
/* 128 */     add(Interval.of(a, b));
/*     */   }
/*     */ 
/*     */   
/*     */   protected void add(Interval addition) {
/* 133 */     if (this.readonly) throw new IllegalStateException("can't alter readonly IntervalSet");
/*     */     
/* 135 */     if (addition.b < addition.a) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 140 */     for (ListIterator<Interval> iter = this.intervals.listIterator(); iter.hasNext(); ) {
/* 141 */       Interval r = iter.next();
/* 142 */       if (addition.equals(r)) {
/*     */         return;
/*     */       }
/* 145 */       if (addition.adjacent(r) || !addition.disjoint(r)) {
/*     */         
/* 147 */         Interval bigger = addition.union(r);
/* 148 */         iter.set(bigger);
/*     */ 
/*     */         
/* 151 */         while (iter.hasNext()) {
/* 152 */           Interval next = iter.next();
/* 153 */           if (!bigger.adjacent(next) && bigger.disjoint(next)) {
/*     */             break;
/*     */           }
/*     */ 
/*     */           
/* 158 */           iter.remove();
/* 159 */           iter.previous();
/* 160 */           iter.set(bigger.union(next));
/* 161 */           iter.next();
/*     */         } 
/*     */         return;
/*     */       } 
/* 165 */       if (addition.startsBeforeDisjoint(r)) {
/*     */         
/* 167 */         iter.previous();
/* 168 */         iter.add(addition);
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */     
/* 175 */     this.intervals.add(addition);
/*     */   }
/*     */ 
/*     */   
/*     */   public static IntervalSet or(IntervalSet[] sets) {
/* 180 */     IntervalSet r = new IntervalSet(new int[0]);
/* 181 */     for (IntervalSet s : sets) r.addAll(s); 
/* 182 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public IntervalSet addAll(IntSet set) {
/* 187 */     if (set == null) {
/* 188 */       return this;
/*     */     }
/*     */     
/* 191 */     if (set instanceof IntervalSet) {
/* 192 */       IntervalSet other = (IntervalSet)set;
/*     */       
/* 194 */       int n = other.intervals.size();
/* 195 */       for (int i = 0; i < n; i++) {
/* 196 */         Interval I = other.intervals.get(i);
/* 197 */         add(I.a, I.b);
/*     */       } 
/*     */     } else {
/*     */       
/* 201 */       for (Iterator<Integer> i$ = set.toList().iterator(); i$.hasNext(); ) { int value = ((Integer)i$.next()).intValue();
/* 202 */         add(value); }
/*     */     
/*     */     } 
/*     */     
/* 206 */     return this;
/*     */   }
/*     */   
/*     */   public IntervalSet complement(int minElement, int maxElement) {
/* 210 */     return complement(of(minElement, maxElement));
/*     */   }
/*     */ 
/*     */   
/*     */   public IntervalSet complement(IntSet vocabulary) {
/*     */     IntervalSet vocabularyIS;
/* 216 */     if (vocabulary == null || vocabulary.isNil()) {
/* 217 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 221 */     if (vocabulary instanceof IntervalSet) {
/* 222 */       vocabularyIS = (IntervalSet)vocabulary;
/*     */     } else {
/*     */       
/* 225 */       vocabularyIS = new IntervalSet(new int[0]);
/* 226 */       vocabularyIS.addAll(vocabulary);
/*     */     } 
/*     */     
/* 229 */     return vocabularyIS.subtract(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public IntervalSet subtract(IntSet a) {
/* 234 */     if (a == null || a.isNil()) {
/* 235 */       return new IntervalSet(this);
/*     */     }
/*     */     
/* 238 */     if (a instanceof IntervalSet) {
/* 239 */       return subtract(this, (IntervalSet)a);
/*     */     }
/*     */     
/* 242 */     IntervalSet other = new IntervalSet(new int[0]);
/* 243 */     other.addAll(a);
/* 244 */     return subtract(this, other);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IntervalSet subtract(IntervalSet left, IntervalSet right) {
/* 254 */     if (left == null || left.isNil()) {
/* 255 */       return new IntervalSet(new int[0]);
/*     */     }
/*     */     
/* 258 */     IntervalSet result = new IntervalSet(left);
/* 259 */     if (right == null || right.isNil())
/*     */     {
/* 261 */       return result;
/*     */     }
/*     */     
/* 264 */     int resultI = 0;
/* 265 */     int rightI = 0;
/* 266 */     while (resultI < result.intervals.size() && rightI < right.intervals.size()) {
/* 267 */       Interval resultInterval = result.intervals.get(resultI);
/* 268 */       Interval rightInterval = right.intervals.get(rightI);
/*     */ 
/*     */ 
/*     */       
/* 272 */       if (rightInterval.b < resultInterval.a) {
/* 273 */         rightI++;
/*     */         
/*     */         continue;
/*     */       } 
/* 277 */       if (rightInterval.a > resultInterval.b) {
/* 278 */         resultI++;
/*     */         
/*     */         continue;
/*     */       } 
/* 282 */       Interval beforeCurrent = null;
/* 283 */       Interval afterCurrent = null;
/* 284 */       if (rightInterval.a > resultInterval.a) {
/* 285 */         beforeCurrent = new Interval(resultInterval.a, rightInterval.a - 1);
/*     */       }
/*     */       
/* 288 */       if (rightInterval.b < resultInterval.b) {
/* 289 */         afterCurrent = new Interval(rightInterval.b + 1, resultInterval.b);
/*     */       }
/*     */       
/* 292 */       if (beforeCurrent != null) {
/* 293 */         if (afterCurrent != null) {
/*     */           
/* 295 */           result.intervals.set(resultI, beforeCurrent);
/* 296 */           result.intervals.add(resultI + 1, afterCurrent);
/* 297 */           resultI++;
/* 298 */           rightI++;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 303 */         result.intervals.set(resultI, beforeCurrent);
/* 304 */         resultI++;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 309 */       if (afterCurrent != null) {
/*     */         
/* 311 */         result.intervals.set(resultI, afterCurrent);
/* 312 */         rightI++;
/*     */         
/*     */         continue;
/*     */       } 
/*     */       
/* 317 */       result.intervals.remove(resultI);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 326 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public IntervalSet or(IntSet a) {
/* 331 */     IntervalSet o = new IntervalSet(new int[0]);
/* 332 */     o.addAll(this);
/* 333 */     o.addAll(a);
/* 334 */     return o;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public IntervalSet and(IntSet other) {
/* 340 */     if (other == null) {
/* 341 */       return null;
/*     */     }
/*     */     
/* 344 */     List<Interval> myIntervals = this.intervals;
/* 345 */     List<Interval> theirIntervals = ((IntervalSet)other).intervals;
/* 346 */     IntervalSet intersection = null;
/* 347 */     int mySize = myIntervals.size();
/* 348 */     int theirSize = theirIntervals.size();
/* 349 */     int i = 0;
/* 350 */     int j = 0;
/*     */     
/* 352 */     while (i < mySize && j < theirSize) {
/* 353 */       Interval mine = myIntervals.get(i);
/* 354 */       Interval theirs = theirIntervals.get(j);
/*     */       
/* 356 */       if (mine.startsBeforeDisjoint(theirs)) {
/*     */         
/* 358 */         i++; continue;
/*     */       } 
/* 360 */       if (theirs.startsBeforeDisjoint(mine)) {
/*     */         
/* 362 */         j++; continue;
/*     */       } 
/* 364 */       if (mine.properlyContains(theirs)) {
/*     */         
/* 366 */         if (intersection == null) {
/* 367 */           intersection = new IntervalSet(new int[0]);
/*     */         }
/* 369 */         intersection.add(mine.intersection(theirs));
/* 370 */         j++; continue;
/*     */       } 
/* 372 */       if (theirs.properlyContains(mine)) {
/*     */         
/* 374 */         if (intersection == null) {
/* 375 */           intersection = new IntervalSet(new int[0]);
/*     */         }
/* 377 */         intersection.add(mine.intersection(theirs));
/* 378 */         i++; continue;
/*     */       } 
/* 380 */       if (!mine.disjoint(theirs)) {
/*     */         
/* 382 */         if (intersection == null) {
/* 383 */           intersection = new IntervalSet(new int[0]);
/*     */         }
/* 385 */         intersection.add(mine.intersection(theirs));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 393 */         if (mine.startsAfterNonDisjoint(theirs)) {
/* 394 */           j++; continue;
/*     */         } 
/* 396 */         if (theirs.startsAfterNonDisjoint(mine)) {
/* 397 */           i++;
/*     */         }
/*     */       } 
/*     */     } 
/* 401 */     if (intersection == null) {
/* 402 */       return new IntervalSet(new int[0]);
/*     */     }
/* 404 */     return intersection;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(int el) {
/* 410 */     int n = this.intervals.size();
/* 411 */     for (int i = 0; i < n; i++) {
/* 412 */       Interval I = this.intervals.get(i);
/* 413 */       int a = I.a;
/* 414 */       int b = I.b;
/* 415 */       if (el < a) {
/*     */         break;
/*     */       }
/* 418 */       if (el >= a && el <= b) {
/* 419 */         return true;
/*     */       }
/*     */     } 
/* 422 */     return false;
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
/*     */   public boolean isNil() {
/* 440 */     return (this.intervals == null || this.intervals.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSingleElement() {
/* 446 */     if (this.intervals != null && this.intervals.size() == 1) {
/* 447 */       Interval I = this.intervals.get(0);
/* 448 */       if (I.a == I.b) {
/* 449 */         return I.a;
/*     */       }
/*     */     } 
/* 452 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxElement() {
/* 462 */     if (isNil()) {
/* 463 */       return 0;
/*     */     }
/* 465 */     Interval last = this.intervals.get(this.intervals.size() - 1);
/* 466 */     return last.b;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMinElement() {
/* 476 */     if (isNil()) {
/* 477 */       return 0;
/*     */     }
/*     */     
/* 480 */     return ((Interval)this.intervals.get(0)).a;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Interval> getIntervals() {
/* 485 */     return this.intervals;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 490 */     int hash = MurmurHash.initialize();
/* 491 */     for (Interval I : this.intervals) {
/* 492 */       hash = MurmurHash.update(hash, I.a);
/* 493 */       hash = MurmurHash.update(hash, I.b);
/*     */     } 
/*     */     
/* 496 */     hash = MurmurHash.finish(hash, this.intervals.size() * 2);
/* 497 */     return hash;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 507 */     if (obj == null || !(obj instanceof IntervalSet)) {
/* 508 */       return false;
/*     */     }
/* 510 */     IntervalSet other = (IntervalSet)obj;
/* 511 */     return this.intervals.equals(other.intervals);
/*     */   }
/*     */   
/*     */   public String toString() {
/* 515 */     return toString(false);
/*     */   }
/*     */   public String toString(boolean elemAreChar) {
/* 518 */     StringBuilder buf = new StringBuilder();
/* 519 */     if (this.intervals == null || this.intervals.isEmpty()) {
/* 520 */       return "{}";
/*     */     }
/* 522 */     if (size() > 1) {
/* 523 */       buf.append("{");
/*     */     }
/* 525 */     Iterator<Interval> iter = this.intervals.iterator();
/* 526 */     while (iter.hasNext()) {
/* 527 */       Interval I = iter.next();
/* 528 */       int a = I.a;
/* 529 */       int b = I.b;
/* 530 */       if (a == b)
/* 531 */       { if (a == -1) { buf.append("<EOF>"); }
/* 532 */         else if (elemAreChar) { buf.append("'").append((char)a).append("'"); }
/* 533 */         else { buf.append(a); }
/*     */         
/*     */          }
/* 536 */       else if (elemAreChar) { buf.append("'").append((char)a).append("'..'").append((char)b).append("'"); }
/* 537 */       else { buf.append(a).append("..").append(b); }
/*     */       
/* 539 */       if (iter.hasNext()) {
/* 540 */         buf.append(", ");
/*     */       }
/*     */     } 
/* 543 */     if (size() > 1) {
/* 544 */       buf.append("}");
/*     */     }
/* 546 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public String toString(String[] tokenNames) {
/* 554 */     return toString(VocabularyImpl.fromTokenNames(tokenNames));
/*     */   }
/*     */   
/*     */   public String toString(Vocabulary vocabulary) {
/* 558 */     StringBuilder buf = new StringBuilder();
/* 559 */     if (this.intervals == null || this.intervals.isEmpty()) {
/* 560 */       return "{}";
/*     */     }
/* 562 */     if (size() > 1) {
/* 563 */       buf.append("{");
/*     */     }
/* 565 */     Iterator<Interval> iter = this.intervals.iterator();
/* 566 */     while (iter.hasNext()) {
/* 567 */       Interval I = iter.next();
/* 568 */       int a = I.a;
/* 569 */       int b = I.b;
/* 570 */       if (a == b) {
/* 571 */         buf.append(elementName(vocabulary, a));
/*     */       } else {
/*     */         
/* 574 */         for (int i = a; i <= b; i++) {
/* 575 */           if (i > a) buf.append(", "); 
/* 576 */           buf.append(elementName(vocabulary, i));
/*     */         } 
/*     */       } 
/* 579 */       if (iter.hasNext()) {
/* 580 */         buf.append(", ");
/*     */       }
/*     */     } 
/* 583 */     if (size() > 1) {
/* 584 */       buf.append("}");
/*     */     }
/* 586 */     return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected String elementName(String[] tokenNames, int a) {
/* 594 */     return elementName(VocabularyImpl.fromTokenNames(tokenNames), a);
/*     */   }
/*     */ 
/*     */   
/*     */   protected String elementName(Vocabulary vocabulary, int a) {
/* 599 */     if (a == -1) {
/* 600 */       return "<EOF>";
/*     */     }
/* 602 */     if (a == -2) {
/* 603 */       return "<EPSILON>";
/*     */     }
/*     */     
/* 606 */     return vocabulary.getDisplayName(a);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 612 */     int n = 0;
/* 613 */     int numIntervals = this.intervals.size();
/* 614 */     if (numIntervals == 1) {
/* 615 */       Interval firstInterval = this.intervals.get(0);
/* 616 */       return firstInterval.b - firstInterval.a + 1;
/*     */     } 
/* 618 */     for (int i = 0; i < numIntervals; i++) {
/* 619 */       Interval I = this.intervals.get(i);
/* 620 */       n += I.b - I.a + 1;
/*     */     } 
/* 622 */     return n;
/*     */   }
/*     */   
/*     */   public IntegerList toIntegerList() {
/* 626 */     IntegerList values = new IntegerList(size());
/* 627 */     int n = this.intervals.size();
/* 628 */     for (int i = 0; i < n; i++) {
/* 629 */       Interval I = this.intervals.get(i);
/* 630 */       int a = I.a;
/* 631 */       int b = I.b;
/* 632 */       for (int v = a; v <= b; v++) {
/* 633 */         values.add(v);
/*     */       }
/*     */     } 
/* 636 */     return values;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Integer> toList() {
/* 641 */     List<Integer> values = new ArrayList<Integer>();
/* 642 */     int n = this.intervals.size();
/* 643 */     for (int i = 0; i < n; i++) {
/* 644 */       Interval I = this.intervals.get(i);
/* 645 */       int a = I.a;
/* 646 */       int b = I.b;
/* 647 */       for (int v = a; v <= b; v++) {
/* 648 */         values.add(Integer.valueOf(v));
/*     */       }
/*     */     } 
/* 651 */     return values;
/*     */   }
/*     */   
/*     */   public Set<Integer> toSet() {
/* 655 */     Set<Integer> s = new HashSet<Integer>();
/* 656 */     for (Interval I : this.intervals) {
/* 657 */       int a = I.a;
/* 658 */       int b = I.b;
/* 659 */       for (int v = a; v <= b; v++) {
/* 660 */         s.add(Integer.valueOf(v));
/*     */       }
/*     */     } 
/* 663 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int get(int i) {
/* 671 */     int n = this.intervals.size();
/* 672 */     int index = 0;
/* 673 */     for (int j = 0; j < n; j++) {
/* 674 */       Interval I = this.intervals.get(j);
/* 675 */       int a = I.a;
/* 676 */       int b = I.b;
/* 677 */       for (int v = a; v <= b; v++) {
/* 678 */         if (index == i) {
/* 679 */           return v;
/*     */         }
/* 681 */         index++;
/*     */       } 
/*     */     } 
/* 684 */     return -1;
/*     */   }
/*     */   
/*     */   public int[] toArray() {
/* 688 */     return toIntegerList().toArray();
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(int el) {
/* 693 */     if (this.readonly) throw new IllegalStateException("can't alter readonly IntervalSet"); 
/* 694 */     int n = this.intervals.size();
/* 695 */     for (int i = 0; i < n; i++) {
/* 696 */       Interval I = this.intervals.get(i);
/* 697 */       int a = I.a;
/* 698 */       int b = I.b;
/* 699 */       if (el < a) {
/*     */         break;
/*     */       }
/*     */       
/* 703 */       if (el == a && el == b) {
/* 704 */         this.intervals.remove(i);
/*     */         
/*     */         break;
/*     */       } 
/* 708 */       if (el == a) {
/* 709 */         I.a++;
/*     */         
/*     */         break;
/*     */       } 
/* 713 */       if (el == b) {
/* 714 */         I.b--;
/*     */         
/*     */         break;
/*     */       } 
/* 718 */       if (el > a && el < b) {
/* 719 */         int oldb = I.b;
/* 720 */         I.b = el - 1;
/* 721 */         add(el + 1, oldb);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isReadonly() {
/* 727 */     return this.readonly;
/*     */   }
/*     */   
/*     */   public void setReadonly(boolean readonly) {
/* 731 */     if (this.readonly && !readonly) throw new IllegalStateException("can't alter readonly IntervalSet"); 
/* 732 */     this.readonly = readonly;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\misc\IntervalSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */