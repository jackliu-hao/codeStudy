package io.undertow.util;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public final class HeaderValues extends AbstractCollection<String> implements Deque<String>, List<String>, RandomAccess {
   private static final String[] NO_STRINGS = new String[0];
   final HttpString key;
   byte size;
   Object value;

   HeaderValues(HttpString key) {
      this.key = key;
   }

   public HttpString getHeaderName() {
      return this.key;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void clear() {
      byte size = this.size;
      if (size != 0) {
         this.clearInternal();
      }
   }

   private void clearInternal() {
      Object value = this.value;
      if (value instanceof String[]) {
         String[] strings = (String[])((String[])value);
         int len = strings.length;
         Arrays.fill(strings, 0, len, (Object)null);
      } else {
         this.value = null;
      }

      this.size = 0;
   }

   private int index(int idx) {
      assert idx >= 0;

      assert idx < this.size;

      int len = ((String[])((String[])this.value)).length;
      if (idx > len) {
         idx -= len;
      }

      return idx;
   }

   public ListIterator<String> listIterator() {
      return this.iterator(0, true);
   }

   public ListIterator<String> listIterator(int index) {
      return this.iterator(index, true);
   }

   public Iterator<String> iterator() {
      return this.iterator(0, true);
   }

   public Iterator<String> descendingIterator() {
      return this.iterator(0, false);
   }

   private ListIterator<String> iterator(final int start, final boolean forwards) {
      return new ListIterator<String>() {
         int idx = start;
         int returned = -1;

         public boolean hasNext() {
            return this.idx < HeaderValues.this.size;
         }

         public boolean hasPrevious() {
            return this.idx > 0;
         }

         public String next() {
            try {
               String next;
               int idx;
               if (forwards) {
                  idx = this.idx;
                  next = HeaderValues.this.get(idx);
                  this.returned = idx;
                  this.idx = idx + 1;
                  return next;
               } else {
                  idx = this.idx - 1;
                  next = HeaderValues.this.get(idx);
                  this.idx = this.returned = idx;
                  return next;
               }
            } catch (IndexOutOfBoundsException var3) {
               throw new NoSuchElementException();
            }
         }

         public int nextIndex() {
            return this.idx;
         }

         public String previous() {
            try {
               String prev;
               int idx;
               if (forwards) {
                  idx = this.idx - 1;
                  prev = HeaderValues.this.get(idx);
                  this.idx = this.returned = idx;
                  return prev;
               } else {
                  idx = this.idx;
                  prev = HeaderValues.this.get(idx);
                  this.returned = idx;
                  this.idx = idx + 1;
                  return prev;
               }
            } catch (IndexOutOfBoundsException var3) {
               throw new NoSuchElementException();
            }
         }

         public int previousIndex() {
            return this.idx - 1;
         }

         public void remove() {
            if (this.returned == -1) {
               throw new IllegalStateException();
            } else {
               HeaderValues.this.remove(this.returned);
               this.returned = -1;
            }
         }

         public void set(String headerValue) {
            if (this.returned == -1) {
               throw new IllegalStateException();
            } else {
               HeaderValues.this.set(this.returned, headerValue);
            }
         }

         public void add(String headerValue) {
            if (this.returned == -1) {
               throw new IllegalStateException();
            } else {
               int idx = this.idx;
               HeaderValues.this.add(idx, headerValue);
               this.idx = idx + 1;
               this.returned = -1;
            }
         }
      };
   }

   public boolean offerFirst(String headerValue) {
      int size = this.size;
      if (headerValue != null && size != 127) {
         Object value = this.value;
         if (value instanceof String[]) {
            String[] strings = (String[])((String[])value);
            int len = strings.length;
            if (size == len) {
               String[] newStrings = new String[len + 2];
               System.arraycopy(strings, 0, newStrings, 1, len);
               newStrings[0] = headerValue;
               this.value = newStrings;
            } else {
               System.arraycopy(strings, 0, strings, 1, strings.length - 1);
               strings[0] = headerValue;
            }

            this.size = (byte)(size + 1);
         } else if (size == 0) {
            this.value = headerValue;
            this.size = 1;
         } else {
            this.value = new String[]{headerValue, (String)value, null, null};
            this.size = 2;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean offerLast(String headerValue) {
      int size = this.size;
      if (headerValue != null && size != 127) {
         Object value = this.value;
         if (value instanceof String[]) {
            this.offerLastMultiValue(headerValue, size, (String[])((String[])value));
         } else if (size == 0) {
            this.value = headerValue;
            this.size = 1;
         } else {
            this.value = new String[]{(String)value, headerValue, null, null};
            this.size = 2;
         }

         return true;
      } else {
         return false;
      }
   }

   private void offerLastMultiValue(String headerValue, int size, String[] value) {
      int len = value.length;
      if (size == len) {
         String[] newStrings = new String[len + 2];
         System.arraycopy(value, 0, newStrings, 0, len);
         newStrings[len] = headerValue;
         this.value = newStrings;
      } else {
         value[size] = headerValue;
      }

      this.size = (byte)(size + 1);
   }

   private boolean offer(int idx, String headerValue) {
      int size = this.size;
      if (idx >= 0 && idx <= size && size != 127 && headerValue != null) {
         if (idx == 0) {
            return this.offerFirst(headerValue);
         } else if (idx == size) {
            return this.offerLast(headerValue);
         } else {
            assert size >= 2;

            Object value = this.value;

            assert value instanceof String[];

            String[] strings = (String[])((String[])value);
            int len = strings.length;
            if (size == len) {
               int newLen = len + 2;
               String[] newStrings = new String[newLen];
               System.arraycopy(value, 0, newStrings, 0, idx);
               System.arraycopy(value, idx, newStrings, idx + 1, len - idx);
               newStrings[idx] = headerValue;
               this.value = newStrings;
            } else {
               System.arraycopy(value, idx, value, idx + 1, len - idx);
               strings[idx] = headerValue;
            }

            this.size = (byte)(size + 1);
            return true;
         }
      } else {
         return false;
      }
   }

   public String pollFirst() {
      byte size = this.size;
      if (size == 0) {
         return null;
      } else {
         Object value = this.value;
         if (value instanceof String) {
            this.size = 0;
            this.value = null;
            return (String)value;
         } else {
            String[] strings = (String[])((String[])value);
            String ret = strings[0];
            System.arraycopy(strings, 1, strings, 0, strings.length - 1);
            this.size = (byte)(size - 1);
            return ret;
         }
      }
   }

   public String pollLast() {
      byte size = this.size;
      if (size == 0) {
         return null;
      } else {
         Object value = this.value;
         if (value instanceof String) {
            this.size = 0;
            this.value = null;
            return (String)value;
         } else {
            String[] strings = (String[])((String[])value);
            int idx = this.size = (byte)(size - 1);
            int len = strings.length;
            if (idx > len) {
               idx -= len;
            }

            String var6;
            try {
               var6 = strings[idx];
            } finally {
               strings[idx] = null;
            }

            return var6;
         }
      }
   }

   public String remove(int idx) {
      int size = this.size;
      if (idx >= 0 && idx < size) {
         if (idx == 0) {
            return this.removeFirst();
         } else if (idx == size - 1) {
            return this.removeLast();
         } else {
            assert size > 2;

            String[] value = (String[])((String[])this.value);
            int len = value.length;
            String ret = value[idx];
            System.arraycopy(value, idx + 1, value, idx, len - idx - 1);
            value[len - 1] = null;
            this.size = (byte)(size - 1);
            return ret;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public String get(int idx) {
      if (idx > this.size) {
         throw new IndexOutOfBoundsException();
      } else {
         Object value = this.value;

         assert value != null;

         if (value instanceof String) {
            assert this.size == 1;

            return (String)value;
         } else {
            String[] a = (String[])((String[])value);
            return a[this.index(idx)];
         }
      }
   }

   public int indexOf(Object o) {
      if (o != null && this.size != 0) {
         if (this.value instanceof String[]) {
            String[] list = (String[])((String[])this.value);
            int len = list.length;

            for(int i = 0; i < this.size; ++i) {
               if ((i > len ? list[i - len] : list[i]).equals(o)) {
                  return i;
               }
            }
         } else if (o.equals(this.value)) {
            return 0;
         }

         return -1;
      } else {
         return -1;
      }
   }

   public int lastIndexOf(Object o) {
      if (o != null && this.size != 0) {
         if (this.value instanceof String[]) {
            String[] list = (String[])((String[])this.value);
            int len = list.length;

            for(int i = this.size - 1; i >= 0; --i) {
               if ((i > len ? list[i - len] : list[i]).equals(o)) {
                  return i;
               }
            }
         } else if (o.equals(this.value)) {
            return 0;
         }

         return -1;
      } else {
         return -1;
      }
   }

   public String set(int index, String element) {
      if (element == null) {
         throw new IllegalArgumentException();
      } else {
         byte size = this.size;
         if (index >= 0 && index < size) {
            Object value = this.value;
            if (size == 1 && value instanceof String) {
               String var15;
               try {
                  var15 = (String)value;
               } finally {
                  this.value = element;
               }

               return var15;
            } else {
               String[] list = (String[])((String[])value);
               int i = this.index(index);

               String var7;
               try {
                  var7 = list[i];
               } finally {
                  list[i] = element;
               }

               return var7;
            }
         } else {
            throw new IndexOutOfBoundsException();
         }
      }
   }

   public boolean addAll(int index, Collection<? extends String> c) {
      int size = this.size;
      if (index >= 0 && index <= size) {
         Iterator<? extends String> iterator = c.iterator();

         boolean result;
         for(result = false; iterator.hasNext(); result |= this.offer(index, (String)iterator.next())) {
         }

         return result;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public List<String> subList(int fromIndex, int toIndex) {
      if (fromIndex >= 0 && toIndex <= this.size && fromIndex <= toIndex) {
         int len = toIndex - fromIndex;
         String[] strings = new String[len];

         for(int i = 0; i < len; ++i) {
            strings[i] = this.get(i + fromIndex);
         }

         return Arrays.asList(strings);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public String[] toArray() {
      int size = this.size;
      if (size == 0) {
         return NO_STRINGS;
      } else {
         Object v = this.value;
         if (v instanceof String) {
            return new String[]{(String)v};
         } else {
            String[] list = (String[])((String[])v);
            int len = list.length;
            if (size < len) {
               return (String[])Arrays.copyOfRange(list, 0, size);
            } else {
               String[] ret = (String[])Arrays.copyOfRange(list, 0, size);
               System.arraycopy(list, 0, ret, len, size - len);
               return ret;
            }
         }
      }
   }

   public <T> T[] toArray(T[] a) {
      int size = this.size;
      if (size == 0) {
         return a;
      } else {
         int inLen = a.length;
         Object[] target = inLen < size ? Arrays.copyOfRange(a, inLen, inLen + size) : a;
         Object v = this.value;
         if (v instanceof String) {
            target[0] = v;
         } else {
            System.arraycopy(v, 0, target, 0, size);
         }

         return (Object[])target;
      }
   }

   public void addFirst(String s) {
      if (s != null) {
         if (!this.offerFirst(s)) {
            throw new IllegalStateException();
         }
      }
   }

   public void addLast(String s) {
      if (s != null) {
         if (!this.offerLast(s)) {
            throw new IllegalStateException();
         }
      }
   }

   public void add(int index, String s) {
      if (s != null) {
         if (!this.offer(index, s)) {
            throw new IllegalStateException();
         }
      }
   }

   public boolean contains(Object o) {
      return this.indexOf(o) != -1;
   }

   public String peekFirst() {
      return this.size == 0 ? null : this.get(0);
   }

   public String peekLast() {
      return this.size == 0 ? null : this.get(this.size - 1);
   }

   public boolean removeFirstOccurrence(Object o) {
      int i = this.indexOf(o);
      return i != -1 && this.remove(i) != null;
   }

   public boolean removeLastOccurrence(Object o) {
      int i = this.lastIndexOf(o);
      return i != -1 && this.remove(i) != null;
   }

   public boolean add(String s) {
      this.addLast(s);
      return true;
   }

   public void push(String s) {
      this.addFirst(s);
   }

   public String pop() {
      return this.removeFirst();
   }

   public boolean offer(String s) {
      return this.offerLast(s);
   }

   public String poll() {
      return this.pollFirst();
   }

   public String peek() {
      return this.peekFirst();
   }

   public String remove() {
      return this.removeFirst();
   }

   public String removeFirst() {
      String s = this.pollFirst();
      if (s == null) {
         throw new NoSuchElementException();
      } else {
         return s;
      }
   }

   public String removeLast() {
      String s = this.pollLast();
      if (s == null) {
         throw new NoSuchElementException();
      } else {
         return s;
      }
   }

   public String getFirst() {
      String s = this.peekFirst();
      if (s == null) {
         throw new NoSuchElementException();
      } else {
         return s;
      }
   }

   public String getLast() {
      String s = this.peekLast();
      if (s == null) {
         throw new NoSuchElementException();
      } else {
         return s;
      }
   }

   public String element() {
      return this.getFirst();
   }

   public boolean remove(Object obj) {
      return this.removeFirstOccurrence(obj);
   }

   public boolean addAll(Collection<? extends String> c) {
      Iterator var2 = c.iterator();

      while(var2.hasNext()) {
         String s = (String)var2.next();
         this.add(s);
      }

      return !c.isEmpty();
   }
}
