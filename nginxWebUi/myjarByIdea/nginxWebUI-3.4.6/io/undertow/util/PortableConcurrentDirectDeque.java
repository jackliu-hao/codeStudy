package io.undertow.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class PortableConcurrentDirectDeque<E> extends ConcurrentDirectDeque<E> implements Deque<E>, Serializable {
   private static final long serialVersionUID = 876323262645176354L;
   private transient volatile Node<E> head;
   private transient volatile Node<E> tail;
   private static final AtomicReferenceFieldUpdater<PortableConcurrentDirectDeque, Node> headUpdater = AtomicReferenceFieldUpdater.newUpdater(PortableConcurrentDirectDeque.class, Node.class, "head");
   private static final AtomicReferenceFieldUpdater<PortableConcurrentDirectDeque, Node> tailUpdater = AtomicReferenceFieldUpdater.newUpdater(PortableConcurrentDirectDeque.class, Node.class, "tail");
   private static final Node<Object> PREV_TERMINATOR = new Node();
   private static final Node<Object> NEXT_TERMINATOR;
   private static final int HOPS = 2;

   Node<E> prevTerminator() {
      return PREV_TERMINATOR;
   }

   Node<E> nextTerminator() {
      return NEXT_TERMINATOR;
   }

   private Node linkFirst(E e) {
      checkNotNull(e);
      Node<E> newNode = new Node(e);

      label34:
      while(true) {
         Node<E> h = this.head;
         Node<E> p = h;

         while(true) {
            while(true) {
               Node q;
               if ((q = p.prev) != null) {
                  p = q;
                  if ((q = q.prev) != null) {
                     p = h != (h = this.head) ? h : q;
                     continue;
                  }
               }

               if (p.next == p) {
                  continue label34;
               }

               newNode.lazySetNext(p);
               if (p.casPrev((Node)null, newNode)) {
                  if (p != h) {
                     this.casHead(h, newNode);
                  }

                  return newNode;
               }
            }
         }
      }
   }

   private Node linkLast(E e) {
      checkNotNull(e);
      Node<E> newNode = new Node(e);

      label34:
      while(true) {
         Node<E> t = this.tail;
         Node<E> p = t;

         while(true) {
            while(true) {
               Node q;
               if ((q = p.next) != null) {
                  p = q;
                  if ((q = q.next) != null) {
                     p = t != (t = this.tail) ? t : q;
                     continue;
                  }
               }

               if (p.prev == p) {
                  continue label34;
               }

               newNode.lazySetPrev(p);
               if (p.casNext((Node)null, newNode)) {
                  if (p != t) {
                     this.casTail(t, newNode);
                  }

                  return newNode;
               }
            }
         }
      }
   }

   void unlink(Node<E> x) {
      Node<E> prev = x.prev;
      Node<E> next = x.next;
      if (prev == null) {
         this.unlinkFirst(x, next);
      } else if (next == null) {
         this.unlinkLast(x, prev);
      } else {
         int hops = 1;
         Node<E> p = prev;

         Node activePred;
         boolean isFirst;
         Node q;
         while(true) {
            if (p.item != null) {
               activePred = p;
               isFirst = false;
               break;
            }

            q = p.prev;
            if (q == null) {
               if (p.next == p) {
                  return;
               }

               activePred = p;
               isFirst = true;
               break;
            }

            if (p == q) {
               return;
            }

            p = q;
            ++hops;
         }

         p = next;

         Node activeSucc;
         boolean isLast;
         while(true) {
            if (p.item != null) {
               activeSucc = p;
               isLast = false;
               break;
            }

            q = p.next;
            if (q == null) {
               if (p.prev == p) {
                  return;
               }

               activeSucc = p;
               isLast = true;
               break;
            }

            if (p == q) {
               return;
            }

            p = q;
            ++hops;
         }

         if (hops < 2 && (isFirst || isLast)) {
            return;
         }

         this.skipDeletedSuccessors(activePred);
         this.skipDeletedPredecessors(activeSucc);
         if ((isFirst || isLast) && activePred.next == activeSucc && activeSucc.prev == activePred) {
            if (isFirst) {
               if (activePred.prev != null) {
                  return;
               }
            } else if (activePred.item == null) {
               return;
            }

            if (isLast) {
               if (activeSucc.next != null) {
                  return;
               }
            } else if (activeSucc.item == null) {
               return;
            }

            this.updateHead();
            this.updateTail();
            x.lazySetPrev(isFirst ? this.prevTerminator() : x);
            x.lazySetNext(isLast ? this.nextTerminator() : x);
         }
      }

   }

   private void unlinkFirst(Node<E> first, Node<E> next) {
      Node<E> o = null;

      Node p;
      Node q;
      for(p = next; p.item == null && (q = p.next) != null; p = q) {
         if (p == q) {
            return;
         }

         o = p;
      }

      if (o != null && p.prev != p && first.casNext(next, p)) {
         this.skipDeletedPredecessors(p);
         if (first.prev == null && (p.next == null || p.item != null) && p.prev == first) {
            this.updateHead();
            this.updateTail();
            o.lazySetNext(o);
            o.lazySetPrev(this.prevTerminator());
         }
      }

   }

   private void unlinkLast(Node<E> last, Node<E> prev) {
      Node<E> o = null;

      Node p;
      Node q;
      for(p = prev; p.item == null && (q = p.prev) != null; p = q) {
         if (p == q) {
            return;
         }

         o = p;
      }

      if (o != null && p.next != p && last.casPrev(prev, p)) {
         this.skipDeletedSuccessors(p);
         if (last.next == null && (p.prev == null || p.item != null) && p.next == last) {
            this.updateHead();
            this.updateTail();
            o.lazySetPrev(o);
            o.lazySetNext(this.nextTerminator());
         }
      }

   }

   private void updateHead() {
      label28:
      while(true) {
         Node h;
         Node p;
         if ((h = this.head).item == null && (p = h.prev) != null) {
            Node q;
            while((q = p.prev) != null) {
               p = q;
               if ((q = q.prev) == null) {
                  break;
               }

               if (h != this.head) {
                  continue label28;
               }

               p = q;
            }

            if (!this.casHead(h, p)) {
               continue;
            }

            return;
         }

         return;
      }
   }

   private void updateTail() {
      label28:
      while(true) {
         Node t;
         Node p;
         if ((t = this.tail).item == null && (p = t.next) != null) {
            Node q;
            while((q = p.next) != null) {
               p = q;
               if ((q = q.next) == null) {
                  break;
               }

               if (t != this.tail) {
                  continue label28;
               }

               p = q;
            }

            if (!this.casTail(t, p)) {
               continue;
            }

            return;
         }

         return;
      }
   }

   private void skipDeletedPredecessors(Node<E> x) {
      do {
         Node<E> prev = x.prev;
         Node<E> p = prev;

         while(true) {
            if (p.item == null) {
               Node<E> q = p.prev;
               if (q != null) {
                  if (p != q) {
                     p = q;
                     continue;
                  }
                  break;
               }

               if (p.next == p) {
                  break;
               }
            }

            if (prev != p && !x.casPrev(prev, p)) {
               break;
            }

            return;
         }
      } while(x.item != null || x.next == null);

   }

   private void skipDeletedSuccessors(Node<E> x) {
      do {
         Node<E> next = x.next;
         Node<E> p = next;

         while(true) {
            if (p.item == null) {
               Node<E> q = p.next;
               if (q != null) {
                  if (p != q) {
                     p = q;
                     continue;
                  }
                  break;
               }

               if (p.prev == p) {
                  break;
               }
            }

            if (next != p && !x.casNext(next, p)) {
               break;
            }

            return;
         }
      } while(x.item != null || x.prev == null);

   }

   final Node<E> succ(Node<E> p) {
      Node<E> q = p.next;
      return p == q ? this.first() : q;
   }

   final Node<E> pred(Node<E> p) {
      Node<E> q = p.prev;
      return p == q ? this.last() : q;
   }

   Node<E> first() {
      Node h;
      Node p;
      do {
         h = this.head;

         Node q;
         for(p = h; (q = p.prev) != null; p = h != (h = this.head) ? h : q) {
            p = q;
            if ((q = q.prev) == null) {
               break;
            }
         }
      } while(p != h && !this.casHead(h, p));

      return p;
   }

   Node<E> last() {
      Node t;
      Node p;
      do {
         t = this.tail;

         Node q;
         for(p = t; (q = p.next) != null; p = t != (t = this.tail) ? t : q) {
            p = q;
            if ((q = q.next) == null) {
               break;
            }
         }
      } while(p != t && !this.casTail(t, p));

      return p;
   }

   private static void checkNotNull(Object v) {
      if (v == null) {
         throw new NullPointerException();
      }
   }

   private E screenNullResult(E v) {
      if (v == null) {
         throw new NoSuchElementException();
      } else {
         return v;
      }
   }

   private ArrayList<E> toArrayList() {
      ArrayList<E> list = new ArrayList();

      for(Node<E> p = this.first(); p != null; p = this.succ(p)) {
         E item = p.item;
         if (item != null) {
            list.add(item);
         }
      }

      return list;
   }

   public PortableConcurrentDirectDeque() {
      this.head = this.tail = new Node((Object)null);
   }

   public PortableConcurrentDirectDeque(Collection<? extends E> c) {
      Node<E> h = null;
      Node<E> t = null;
      Iterator var4 = c.iterator();

      while(var4.hasNext()) {
         E e = var4.next();
         checkNotNull(e);
         Node<E> newNode = new Node(e);
         if (h == null) {
            t = newNode;
            h = newNode;
         } else {
            t.lazySetNext(newNode);
            newNode.lazySetPrev(t);
            t = newNode;
         }
      }

      this.initHeadTail(h, t);
   }

   private void initHeadTail(Node<E> h, Node<E> t) {
      if (h == t) {
         if (h == null) {
            h = t = new Node((Object)null);
         } else {
            Node<E> newNode = new Node((Object)null);
            t.lazySetNext(newNode);
            newNode.lazySetPrev(t);
            t = newNode;
         }
      }

      this.head = h;
      this.tail = t;
   }

   public void addFirst(E e) {
      this.linkFirst(e);
   }

   public void addLast(E e) {
      this.linkLast(e);
   }

   public boolean offerFirst(E e) {
      this.linkFirst(e);
      return true;
   }

   public Object offerFirstAndReturnToken(E e) {
      return this.linkFirst(e);
   }

   public Object offerLastAndReturnToken(E e) {
      return this.linkLast(e);
   }

   public void removeToken(Object token) {
      if (!(token instanceof Node)) {
         throw new IllegalArgumentException();
      } else {
         Node node = (Node)((Node)token);

         while(!node.casItem(node.item, (Object)null)) {
         }

         this.unlink(node);
      }
   }

   public boolean offerLast(E e) {
      this.linkLast(e);
      return true;
   }

   public E peekFirst() {
      for(Node<E> p = this.first(); p != null; p = this.succ(p)) {
         E item = p.item;
         if (item != null) {
            return item;
         }
      }

      return null;
   }

   public E peekLast() {
      for(Node<E> p = this.last(); p != null; p = this.pred(p)) {
         E item = p.item;
         if (item != null) {
            return item;
         }
      }

      return null;
   }

   public E getFirst() {
      return this.screenNullResult(this.peekFirst());
   }

   public E getLast() {
      return this.screenNullResult(this.peekLast());
   }

   public E pollFirst() {
      for(Node<E> p = this.first(); p != null; p = this.succ(p)) {
         E item = p.item;
         if (item != null && p.casItem(item, (Object)null)) {
            this.unlink(p);
            return item;
         }
      }

      return null;
   }

   public E pollLast() {
      for(Node<E> p = this.last(); p != null; p = this.pred(p)) {
         E item = p.item;
         if (item != null && p.casItem(item, (Object)null)) {
            this.unlink(p);
            return item;
         }
      }

      return null;
   }

   public E removeFirst() {
      return this.screenNullResult(this.pollFirst());
   }

   public E removeLast() {
      return this.screenNullResult(this.pollLast());
   }

   public boolean offer(E e) {
      return this.offerLast(e);
   }

   public boolean add(E e) {
      return this.offerLast(e);
   }

   public E poll() {
      return this.pollFirst();
   }

   public E remove() {
      return this.removeFirst();
   }

   public E peek() {
      return this.peekFirst();
   }

   public E element() {
      return this.getFirst();
   }

   public void push(E e) {
      this.addFirst(e);
   }

   public E pop() {
      return this.removeFirst();
   }

   public boolean removeFirstOccurrence(Object o) {
      checkNotNull(o);

      for(Node<E> p = this.first(); p != null; p = this.succ(p)) {
         E item = p.item;
         if (item != null && o.equals(item) && p.casItem(item, (Object)null)) {
            this.unlink(p);
            return true;
         }
      }

      return false;
   }

   public boolean removeLastOccurrence(Object o) {
      checkNotNull(o);

      for(Node<E> p = this.last(); p != null; p = this.pred(p)) {
         E item = p.item;
         if (item != null && o.equals(item) && p.casItem(item, (Object)null)) {
            this.unlink(p);
            return true;
         }
      }

      return false;
   }

   public boolean contains(Object o) {
      if (o == null) {
         return false;
      } else {
         for(Node<E> p = this.first(); p != null; p = this.succ(p)) {
            E item = p.item;
            if (item != null && o.equals(item)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean isEmpty() {
      return this.peekFirst() == null;
   }

   public int size() {
      int count = 0;

      for(Node<E> p = this.first(); p != null; p = this.succ(p)) {
         if (p.item != null) {
            ++count;
            if (count == Integer.MAX_VALUE) {
               break;
            }
         }
      }

      return count;
   }

   public boolean remove(Object o) {
      return this.removeFirstOccurrence(o);
   }

   public boolean addAll(Collection<? extends E> c) {
      if (c == this) {
         throw new IllegalArgumentException();
      } else {
         Node<E> beginningOfTheEnd = null;
         Node<E> last = null;
         Iterator var4 = c.iterator();

         Node q;
         while(var4.hasNext()) {
            E e = var4.next();
            checkNotNull(e);
            q = new Node(e);
            if (beginningOfTheEnd == null) {
               last = q;
               beginningOfTheEnd = q;
            } else {
               last.lazySetNext(q);
               q.lazySetPrev(last);
               last = q;
            }
         }

         if (beginningOfTheEnd == null) {
            return false;
         } else {
            label49:
            while(true) {
               Node<E> t = this.tail;
               Node<E> p = t;

               while(true) {
                  while(true) {
                     if ((q = p.next) != null) {
                        p = q;
                        if ((q = q.next) != null) {
                           p = t != (t = this.tail) ? t : q;
                           continue;
                        }
                     }

                     if (p.prev == p) {
                        continue label49;
                     }

                     beginningOfTheEnd.lazySetPrev(p);
                     if (p.casNext((Node)null, beginningOfTheEnd)) {
                        if (!this.casTail(t, last)) {
                           t = this.tail;
                           if (last.next == null) {
                              this.casTail(t, last);
                           }
                        }

                        return true;
                     }
                  }
               }
            }
         }
      }
   }

   public void clear() {
      while(this.pollFirst() != null) {
      }

   }

   public Object[] toArray() {
      return this.toArrayList().toArray();
   }

   public <T> T[] toArray(T[] a) {
      return this.toArrayList().toArray(a);
   }

   public Iterator<E> iterator() {
      return new Itr();
   }

   public Iterator<E> descendingIterator() {
      return new DescendingItr();
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for(Node<E> p = this.first(); p != null; p = this.succ(p)) {
         E item = p.item;
         if (item != null) {
            s.writeObject(item);
         }
      }

      s.writeObject((Object)null);
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      Node<E> h = null;
      Node<E> t = null;

      Object item;
      while((item = s.readObject()) != null) {
         Node<E> newNode = new Node(item);
         if (h == null) {
            t = newNode;
            h = newNode;
         } else {
            t.lazySetNext(newNode);
            newNode.lazySetPrev(t);
            t = newNode;
         }
      }

      this.initHeadTail(h, t);
   }

   private boolean casHead(Node<E> cmp, Node<E> val) {
      return headUpdater.compareAndSet(this, cmp, val);
   }

   private boolean casTail(Node<E> cmp, Node<E> val) {
      return tailUpdater.compareAndSet(this, cmp, val);
   }

   static {
      PREV_TERMINATOR.next = PREV_TERMINATOR;
      NEXT_TERMINATOR = new Node();
      NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
   }

   private class DescendingItr extends PortableConcurrentDirectDeque<E>.AbstractItr {
      private DescendingItr() {
         super();
      }

      Node<E> startNode() {
         return PortableConcurrentDirectDeque.this.last();
      }

      Node<E> nextNode(Node<E> p) {
         return PortableConcurrentDirectDeque.this.pred(p);
      }

      // $FF: synthetic method
      DescendingItr(Object x1) {
         this();
      }
   }

   private class Itr extends PortableConcurrentDirectDeque<E>.AbstractItr {
      private Itr() {
         super();
      }

      Node<E> startNode() {
         return PortableConcurrentDirectDeque.this.first();
      }

      Node<E> nextNode(Node<E> p) {
         return PortableConcurrentDirectDeque.this.succ(p);
      }

      // $FF: synthetic method
      Itr(Object x1) {
         this();
      }
   }

   private abstract class AbstractItr implements Iterator<E> {
      private Node<E> nextNode;
      private E nextItem;
      private Node<E> lastRet;

      abstract Node<E> startNode();

      abstract Node<E> nextNode(Node<E> var1);

      AbstractItr() {
         this.advance();
      }

      private void advance() {
         this.lastRet = this.nextNode;
         Node<E> p = this.nextNode == null ? this.startNode() : this.nextNode(this.nextNode);

         while(true) {
            if (p == null) {
               this.nextNode = null;
               this.nextItem = null;
               break;
            }

            E item = p.item;
            if (item != null) {
               this.nextNode = p;
               this.nextItem = item;
               break;
            }

            p = this.nextNode(p);
         }

      }

      public boolean hasNext() {
         return this.nextItem != null;
      }

      public E next() {
         E item = this.nextItem;
         if (item == null) {
            throw new NoSuchElementException();
         } else {
            this.advance();
            return item;
         }
      }

      public void remove() {
         Node<E> l = this.lastRet;
         if (l == null) {
            throw new IllegalStateException();
         } else {
            l.item = null;
            PortableConcurrentDirectDeque.this.unlink(l);
            this.lastRet = null;
         }
      }
   }

   static final class Node<E> {
      private static final AtomicReferenceFieldUpdater<Node, Node> prevUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "prev");
      private static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
      private static final AtomicReferenceFieldUpdater<Node, Object> itemUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Object.class, "item");
      volatile Node<E> prev;
      volatile E item;
      volatile Node<E> next;

      Node() {
      }

      Node(E item) {
         this.item = item;
      }

      boolean casItem(E cmp, E val) {
         return itemUpdater.compareAndSet(this, cmp, val);
      }

      void lazySetNext(Node<E> val) {
         this.next = val;
      }

      boolean casNext(Node<E> cmp, Node<E> val) {
         return nextUpdater.compareAndSet(this, cmp, val);
      }

      void lazySetPrev(Node<E> val) {
         this.prev = val;
      }

      boolean casPrev(Node<E> cmp, Node<E> val) {
         return prevUpdater.compareAndSet(this, cmp, val);
      }
   }
}
