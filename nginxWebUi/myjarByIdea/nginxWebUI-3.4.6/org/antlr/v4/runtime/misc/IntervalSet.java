package org.antlr.v4.runtime.misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;

public class IntervalSet implements IntSet {
   public static final IntervalSet COMPLETE_CHAR_SET = of(0, 65534);
   public static final IntervalSet EMPTY_SET;
   protected List<Interval> intervals;
   protected boolean readonly;

   public IntervalSet(List<Interval> intervals) {
      this.intervals = intervals;
   }

   public IntervalSet(IntervalSet set) {
      this();
      this.addAll(set);
   }

   public IntervalSet(int... els) {
      if (els == null) {
         this.intervals = new ArrayList(2);
      } else {
         this.intervals = new ArrayList(els.length);
         int[] arr$ = els;
         int len$ = els.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            int e = arr$[i$];
            this.add(e);
         }
      }

   }

   public static IntervalSet of(int a) {
      IntervalSet s = new IntervalSet(new int[0]);
      s.add(a);
      return s;
   }

   public static IntervalSet of(int a, int b) {
      IntervalSet s = new IntervalSet(new int[0]);
      s.add(a, b);
      return s;
   }

   public void clear() {
      if (this.readonly) {
         throw new IllegalStateException("can't alter readonly IntervalSet");
      } else {
         this.intervals.clear();
      }
   }

   public void add(int el) {
      if (this.readonly) {
         throw new IllegalStateException("can't alter readonly IntervalSet");
      } else {
         this.add(el, el);
      }
   }

   public void add(int a, int b) {
      this.add(Interval.of(a, b));
   }

   protected void add(Interval addition) {
      if (this.readonly) {
         throw new IllegalStateException("can't alter readonly IntervalSet");
      } else if (addition.b >= addition.a) {
         ListIterator<Interval> iter = this.intervals.listIterator();

         Interval r;
         do {
            if (!iter.hasNext()) {
               this.intervals.add(addition);
               return;
            }

            r = (Interval)iter.next();
            if (addition.equals(r)) {
               return;
            }

            if (addition.adjacent(r) || !addition.disjoint(r)) {
               Interval bigger = addition.union(r);
               iter.set(bigger);

               while(iter.hasNext()) {
                  Interval next = (Interval)iter.next();
                  if (!bigger.adjacent(next) && bigger.disjoint(next)) {
                     break;
                  }

                  iter.remove();
                  iter.previous();
                  iter.set(bigger.union(next));
                  iter.next();
               }

               return;
            }
         } while(!addition.startsBeforeDisjoint(r));

         iter.previous();
         iter.add(addition);
      }
   }

   public static IntervalSet or(IntervalSet[] sets) {
      IntervalSet r = new IntervalSet(new int[0]);
      IntervalSet[] arr$ = sets;
      int len$ = sets.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         IntervalSet s = arr$[i$];
         r.addAll(s);
      }

      return r;
   }

   public IntervalSet addAll(IntSet set) {
      if (set == null) {
         return this;
      } else {
         int n;
         if (set instanceof IntervalSet) {
            IntervalSet other = (IntervalSet)set;
            n = other.intervals.size();

            for(int i = 0; i < n; ++i) {
               Interval I = (Interval)other.intervals.get(i);
               this.add(I.a, I.b);
            }
         } else {
            Iterator i$ = set.toList().iterator();

            while(i$.hasNext()) {
               n = (Integer)i$.next();
               this.add(n);
            }
         }

         return this;
      }
   }

   public IntervalSet complement(int minElement, int maxElement) {
      return this.complement(of(minElement, maxElement));
   }

   public IntervalSet complement(IntSet vocabulary) {
      if (vocabulary != null && !vocabulary.isNil()) {
         IntervalSet vocabularyIS;
         if (vocabulary instanceof IntervalSet) {
            vocabularyIS = (IntervalSet)vocabulary;
         } else {
            vocabularyIS = new IntervalSet(new int[0]);
            vocabularyIS.addAll(vocabulary);
         }

         return vocabularyIS.subtract(this);
      } else {
         return null;
      }
   }

   public IntervalSet subtract(IntSet a) {
      if (a != null && !a.isNil()) {
         if (a instanceof IntervalSet) {
            return subtract(this, (IntervalSet)a);
         } else {
            IntervalSet other = new IntervalSet(new int[0]);
            other.addAll(a);
            return subtract(this, other);
         }
      } else {
         return new IntervalSet(this);
      }
   }

   public static IntervalSet subtract(IntervalSet left, IntervalSet right) {
      if (left != null && !left.isNil()) {
         IntervalSet result = new IntervalSet(left);
         if (right != null && !right.isNil()) {
            int resultI = 0;
            int rightI = 0;

            while(resultI < result.intervals.size() && rightI < right.intervals.size()) {
               Interval resultInterval = (Interval)result.intervals.get(resultI);
               Interval rightInterval = (Interval)right.intervals.get(rightI);
               if (rightInterval.b < resultInterval.a) {
                  ++rightI;
               } else if (rightInterval.a > resultInterval.b) {
                  ++resultI;
               } else {
                  Interval beforeCurrent = null;
                  Interval afterCurrent = null;
                  if (rightInterval.a > resultInterval.a) {
                     beforeCurrent = new Interval(resultInterval.a, rightInterval.a - 1);
                  }

                  if (rightInterval.b < resultInterval.b) {
                     afterCurrent = new Interval(rightInterval.b + 1, resultInterval.b);
                  }

                  if (beforeCurrent != null) {
                     if (afterCurrent != null) {
                        result.intervals.set(resultI, beforeCurrent);
                        result.intervals.add(resultI + 1, afterCurrent);
                        ++resultI;
                        ++rightI;
                     } else {
                        result.intervals.set(resultI, beforeCurrent);
                        ++resultI;
                     }
                  } else if (afterCurrent != null) {
                     result.intervals.set(resultI, afterCurrent);
                     ++rightI;
                  } else {
                     result.intervals.remove(resultI);
                  }
               }
            }

            return result;
         } else {
            return result;
         }
      } else {
         return new IntervalSet(new int[0]);
      }
   }

   public IntervalSet or(IntSet a) {
      IntervalSet o = new IntervalSet(new int[0]);
      o.addAll(this);
      o.addAll(a);
      return o;
   }

   public IntervalSet and(IntSet other) {
      if (other == null) {
         return null;
      } else {
         List<Interval> myIntervals = this.intervals;
         List<Interval> theirIntervals = ((IntervalSet)other).intervals;
         IntervalSet intersection = null;
         int mySize = myIntervals.size();
         int theirSize = theirIntervals.size();
         int i = 0;
         int j = 0;

         while(i < mySize && j < theirSize) {
            Interval mine = (Interval)myIntervals.get(i);
            Interval theirs = (Interval)theirIntervals.get(j);
            if (mine.startsBeforeDisjoint(theirs)) {
               ++i;
            } else if (theirs.startsBeforeDisjoint(mine)) {
               ++j;
            } else if (mine.properlyContains(theirs)) {
               if (intersection == null) {
                  intersection = new IntervalSet(new int[0]);
               }

               intersection.add(mine.intersection(theirs));
               ++j;
            } else if (theirs.properlyContains(mine)) {
               if (intersection == null) {
                  intersection = new IntervalSet(new int[0]);
               }

               intersection.add(mine.intersection(theirs));
               ++i;
            } else if (!mine.disjoint(theirs)) {
               if (intersection == null) {
                  intersection = new IntervalSet(new int[0]);
               }

               intersection.add(mine.intersection(theirs));
               if (mine.startsAfterNonDisjoint(theirs)) {
                  ++j;
               } else if (theirs.startsAfterNonDisjoint(mine)) {
                  ++i;
               }
            }
         }

         return intersection == null ? new IntervalSet(new int[0]) : intersection;
      }
   }

   public boolean contains(int el) {
      int n = this.intervals.size();

      for(int i = 0; i < n; ++i) {
         Interval I = (Interval)this.intervals.get(i);
         int a = I.a;
         int b = I.b;
         if (el < a) {
            break;
         }

         if (el >= a && el <= b) {
            return true;
         }
      }

      return false;
   }

   public boolean isNil() {
      return this.intervals == null || this.intervals.isEmpty();
   }

   public int getSingleElement() {
      if (this.intervals != null && this.intervals.size() == 1) {
         Interval I = (Interval)this.intervals.get(0);
         if (I.a == I.b) {
            return I.a;
         }
      }

      return 0;
   }

   public int getMaxElement() {
      if (this.isNil()) {
         return 0;
      } else {
         Interval last = (Interval)this.intervals.get(this.intervals.size() - 1);
         return last.b;
      }
   }

   public int getMinElement() {
      return this.isNil() ? 0 : ((Interval)this.intervals.get(0)).a;
   }

   public List<Interval> getIntervals() {
      return this.intervals;
   }

   public int hashCode() {
      int hash = MurmurHash.initialize();

      Interval I;
      for(Iterator i$ = this.intervals.iterator(); i$.hasNext(); hash = MurmurHash.update(hash, I.b)) {
         I = (Interval)i$.next();
         hash = MurmurHash.update(hash, I.a);
      }

      hash = MurmurHash.finish(hash, this.intervals.size() * 2);
      return hash;
   }

   public boolean equals(Object obj) {
      if (obj != null && obj instanceof IntervalSet) {
         IntervalSet other = (IntervalSet)obj;
         return this.intervals.equals(other.intervals);
      } else {
         return false;
      }
   }

   public String toString() {
      return this.toString(false);
   }

   public String toString(boolean elemAreChar) {
      StringBuilder buf = new StringBuilder();
      if (this.intervals != null && !this.intervals.isEmpty()) {
         if (this.size() > 1) {
            buf.append("{");
         }

         Iterator<Interval> iter = this.intervals.iterator();

         while(iter.hasNext()) {
            Interval I = (Interval)iter.next();
            int a = I.a;
            int b = I.b;
            if (a == b) {
               if (a == -1) {
                  buf.append("<EOF>");
               } else if (elemAreChar) {
                  buf.append("'").append((char)a).append("'");
               } else {
                  buf.append(a);
               }
            } else if (elemAreChar) {
               buf.append("'").append((char)a).append("'..'").append((char)b).append("'");
            } else {
               buf.append(a).append("..").append(b);
            }

            if (iter.hasNext()) {
               buf.append(", ");
            }
         }

         if (this.size() > 1) {
            buf.append("}");
         }

         return buf.toString();
      } else {
         return "{}";
      }
   }

   /** @deprecated */
   @Deprecated
   public String toString(String[] tokenNames) {
      return this.toString(VocabularyImpl.fromTokenNames(tokenNames));
   }

   public String toString(Vocabulary vocabulary) {
      StringBuilder buf = new StringBuilder();
      if (this.intervals != null && !this.intervals.isEmpty()) {
         if (this.size() > 1) {
            buf.append("{");
         }

         Iterator<Interval> iter = this.intervals.iterator();

         while(iter.hasNext()) {
            Interval I = (Interval)iter.next();
            int a = I.a;
            int b = I.b;
            if (a == b) {
               buf.append(this.elementName(vocabulary, a));
            } else {
               for(int i = a; i <= b; ++i) {
                  if (i > a) {
                     buf.append(", ");
                  }

                  buf.append(this.elementName(vocabulary, i));
               }
            }

            if (iter.hasNext()) {
               buf.append(", ");
            }
         }

         if (this.size() > 1) {
            buf.append("}");
         }

         return buf.toString();
      } else {
         return "{}";
      }
   }

   /** @deprecated */
   @Deprecated
   protected String elementName(String[] tokenNames, int a) {
      return this.elementName(VocabularyImpl.fromTokenNames(tokenNames), a);
   }

   protected String elementName(Vocabulary vocabulary, int a) {
      if (a == -1) {
         return "<EOF>";
      } else {
         return a == -2 ? "<EPSILON>" : vocabulary.getDisplayName(a);
      }
   }

   public int size() {
      int n = 0;
      int numIntervals = this.intervals.size();
      if (numIntervals == 1) {
         Interval firstInterval = (Interval)this.intervals.get(0);
         return firstInterval.b - firstInterval.a + 1;
      } else {
         for(int i = 0; i < numIntervals; ++i) {
            Interval I = (Interval)this.intervals.get(i);
            n += I.b - I.a + 1;
         }

         return n;
      }
   }

   public IntegerList toIntegerList() {
      IntegerList values = new IntegerList(this.size());
      int n = this.intervals.size();

      for(int i = 0; i < n; ++i) {
         Interval I = (Interval)this.intervals.get(i);
         int a = I.a;
         int b = I.b;

         for(int v = a; v <= b; ++v) {
            values.add(v);
         }
      }

      return values;
   }

   public List<Integer> toList() {
      List<Integer> values = new ArrayList();
      int n = this.intervals.size();

      for(int i = 0; i < n; ++i) {
         Interval I = (Interval)this.intervals.get(i);
         int a = I.a;
         int b = I.b;

         for(int v = a; v <= b; ++v) {
            values.add(v);
         }
      }

      return values;
   }

   public Set<Integer> toSet() {
      Set<Integer> s = new HashSet();
      Iterator i$ = this.intervals.iterator();

      while(i$.hasNext()) {
         Interval I = (Interval)i$.next();
         int a = I.a;
         int b = I.b;

         for(int v = a; v <= b; ++v) {
            s.add(v);
         }
      }

      return s;
   }

   public int get(int i) {
      int n = this.intervals.size();
      int index = 0;

      for(int j = 0; j < n; ++j) {
         Interval I = (Interval)this.intervals.get(j);
         int a = I.a;
         int b = I.b;

         for(int v = a; v <= b; ++v) {
            if (index == i) {
               return v;
            }

            ++index;
         }
      }

      return -1;
   }

   public int[] toArray() {
      return this.toIntegerList().toArray();
   }

   public void remove(int el) {
      if (this.readonly) {
         throw new IllegalStateException("can't alter readonly IntervalSet");
      } else {
         int n = this.intervals.size();

         for(int i = 0; i < n; ++i) {
            Interval I = (Interval)this.intervals.get(i);
            int a = I.a;
            int b = I.b;
            if (el < a) {
               break;
            }

            if (el == a && el == b) {
               this.intervals.remove(i);
               break;
            }

            if (el == a) {
               ++I.a;
               break;
            }

            if (el == b) {
               --I.b;
               break;
            }

            if (el > a && el < b) {
               int oldb = I.b;
               I.b = el - 1;
               this.add(el + 1, oldb);
            }
         }

      }
   }

   public boolean isReadonly() {
      return this.readonly;
   }

   public void setReadonly(boolean readonly) {
      if (this.readonly && !readonly) {
         throw new IllegalStateException("can't alter readonly IntervalSet");
      } else {
         this.readonly = readonly;
      }
   }

   static {
      COMPLETE_CHAR_SET.setReadonly(true);
      EMPTY_SET = new IntervalSet(new int[0]);
      EMPTY_SET.setReadonly(true);
   }
}
