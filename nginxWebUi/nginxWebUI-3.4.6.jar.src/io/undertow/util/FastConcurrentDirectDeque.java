/*      */ package io.undertow.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.reflect.Field;
/*      */ import java.security.AccessController;
/*      */ import java.security.PrivilegedAction;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Deque;
/*      */ import java.util.Iterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Spliterator;
/*      */ import java.util.Spliterators;
/*      */ import java.util.function.Consumer;
/*      */ import sun.misc.Unsafe;
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
/*      */ public class FastConcurrentDirectDeque<E>
/*      */   extends ConcurrentDirectDeque<E>
/*      */   implements Deque<E>, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 876323262645176354L;
/*      */   private volatile transient Node<E> head;
/*      */   private volatile transient Node<E> tail;
/*      */   
/*      */   Node<E> prevTerminator() {
/*  284 */     return (Node)PREV_TERMINATOR;
/*      */   }
/*      */ 
/*      */   
/*      */   Node<E> nextTerminator() {
/*  289 */     return (Node)NEXT_TERMINATOR;
/*      */   }
/*      */   
/*      */   static final class Node<E>
/*      */   {
/*      */     volatile Node<E> prev;
/*      */     volatile E item;
/*      */     volatile Node<E> next;
/*      */     private static final Unsafe UNSAFE;
/*      */     private static final long prevOffset;
/*      */     private static final long itemOffset;
/*      */     private static final long nextOffset;
/*      */     
/*      */     Node() {}
/*      */     
/*      */     Node(E item) {
/*  305 */       UNSAFE.putObject(this, itemOffset, item);
/*      */     }
/*      */     
/*      */     boolean casItem(E cmp, E val) {
/*  309 */       return UNSAFE.compareAndSwapObject(this, itemOffset, cmp, val);
/*      */     }
/*      */     
/*      */     void lazySetNext(Node<E> val) {
/*  313 */       UNSAFE.putOrderedObject(this, nextOffset, val);
/*      */     }
/*      */     
/*      */     boolean casNext(Node<E> cmp, Node<E> val) {
/*  317 */       return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
/*      */     }
/*      */     
/*      */     void lazySetPrev(Node<E> val) {
/*  321 */       UNSAFE.putOrderedObject(this, prevOffset, val);
/*      */     }
/*      */     
/*      */     boolean casPrev(Node<E> cmp, Node<E> val) {
/*  325 */       return UNSAFE.compareAndSwapObject(this, prevOffset, cmp, val);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     static {
/*      */       try {
/*  337 */         UNSAFE = getUnsafe();
/*  338 */         Class<?> k = Node.class;
/*      */         
/*  340 */         prevOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("prev"));
/*      */         
/*  342 */         itemOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("item"));
/*      */         
/*  344 */         nextOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("next"));
/*  345 */       } catch (Exception e) {
/*  346 */         throw new Error(e);
/*      */       } 
/*      */     }
/*      */     
/*      */     private static Unsafe getUnsafe() {
/*  351 */       if (System.getSecurityManager() != null) {
/*  352 */         return AccessController.<Unsafe>doPrivileged(new PrivilegedAction<Unsafe>() {
/*      */               public Unsafe run() {
/*  354 */                 return FastConcurrentDirectDeque.getUnsafe0();
/*      */               }
/*      */             });
/*      */       }
/*  358 */       return FastConcurrentDirectDeque.getUnsafe0();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Node linkFirst(E e) {
/*      */     Node<E> h, p;
/*  366 */     checkNotNull(e);
/*  367 */     Node<E> newNode = new Node<>(e);
/*      */ 
/*      */     
/*      */     label22: while (true) {
/*  371 */       p = h = this.head; while (true) {
/*  372 */         Node<E> q; if ((q = p.prev) != null && (q = (p = q).prev) != null) {
/*      */ 
/*      */ 
/*      */           
/*  376 */           p = (h != (h = this.head)) ? h : q; continue;
/*  377 */         }  if (p.next == p) {
/*      */           continue label22;
/*      */         }
/*      */         
/*  381 */         newNode.lazySetNext(p);
/*  382 */         if (p.casPrev(null, newNode))
/*      */           break; 
/*      */       }  break;
/*      */     } 
/*  386 */     if (p != h)
/*  387 */       casHead(h, newNode); 
/*  388 */     return newNode;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private Node linkLast(E e) {
/*      */     Node<E> t, p;
/*  399 */     checkNotNull(e);
/*  400 */     Node<E> newNode = new Node<>(e);
/*      */ 
/*      */     
/*      */     label22: while (true) {
/*  404 */       p = t = this.tail; while (true) {
/*  405 */         Node<E> q; if ((q = p.next) != null && (q = (p = q).next) != null) {
/*      */ 
/*      */ 
/*      */           
/*  409 */           p = (t != (t = this.tail)) ? t : q; continue;
/*  410 */         }  if (p.prev == p) {
/*      */           continue label22;
/*      */         }
/*      */         
/*  414 */         newNode.lazySetPrev(p);
/*  415 */         if (p.casNext(null, newNode))
/*      */           break; 
/*      */       }  break;
/*      */     } 
/*  419 */     if (p != t)
/*  420 */       casTail(t, newNode); 
/*  421 */     return newNode;
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
/*      */   void unlink(Node<E> x) {
/*  434 */     Node<E> prev = x.prev;
/*  435 */     Node<E> next = x.next;
/*  436 */     if (prev == null) {
/*  437 */       unlinkFirst(x, next);
/*  438 */     } else if (next == null) {
/*  439 */       unlinkLast(x, prev);
/*      */     } else {
/*      */       Node<E> activePred, activeSucc;
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
/*      */       boolean isFirst, isLast;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  462 */       int hops = 1;
/*      */       
/*      */       Node<E> p;
/*  465 */       for (p = prev;; hops++) {
/*  466 */         if (p.item != null) {
/*  467 */           activePred = p;
/*  468 */           isFirst = false;
/*      */           break;
/*      */         } 
/*  471 */         Node<E> q = p.prev;
/*  472 */         if (q == null) {
/*  473 */           if (p.next == p)
/*      */             return; 
/*  475 */           activePred = p;
/*  476 */           isFirst = true;
/*      */           break;
/*      */         } 
/*  479 */         if (p == q) {
/*      */           return;
/*      */         }
/*  482 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  486 */       for (p = next;; hops++) {
/*  487 */         if (p.item != null) {
/*  488 */           activeSucc = p;
/*  489 */           isLast = false;
/*      */           break;
/*      */         } 
/*  492 */         Node<E> q = p.next;
/*  493 */         if (q == null) {
/*  494 */           if (p.prev == p)
/*      */             return; 
/*  496 */           activeSucc = p;
/*  497 */           isLast = true;
/*      */           break;
/*      */         } 
/*  500 */         if (p == q) {
/*      */           return;
/*      */         }
/*  503 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  507 */       if (hops < 2 && (isFirst || isLast)) {
/*      */         return;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  514 */       skipDeletedSuccessors(activePred);
/*  515 */       skipDeletedPredecessors(activeSucc);
/*      */ 
/*      */       
/*  518 */       if ((isFirst || isLast) && activePred.next == activeSucc && activeSucc.prev == activePred && (isFirst ? (activePred.prev == null) : (activePred.item != null)) && (isLast ? (activeSucc.next == null) : (activeSucc.item != null))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  526 */         updateHead();
/*  527 */         updateTail();
/*      */ 
/*      */         
/*  530 */         x.lazySetPrev(isFirst ? prevTerminator() : x);
/*  531 */         x.lazySetNext(isLast ? nextTerminator() : x);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void unlinkFirst(Node<E> first, Node<E> next) {
/*  540 */     Node<E> o = null, p = next; while (true) {
/*  541 */       Node<E> q; if (p.item != null || (q = p.next) == null) {
/*  542 */         if (o != null && p.prev != p && first.casNext(next, p)) {
/*  543 */           skipDeletedPredecessors(p);
/*  544 */           if (first.prev == null && (p.next == null || p.item != null) && p.prev == first) {
/*      */ 
/*      */ 
/*      */             
/*  548 */             updateHead();
/*  549 */             updateTail();
/*      */ 
/*      */             
/*  552 */             o.lazySetNext(o);
/*  553 */             o.lazySetPrev(prevTerminator());
/*      */           } 
/*      */         } 
/*      */         return;
/*      */       } 
/*  558 */       if (p == q) {
/*      */         return;
/*      */       }
/*  561 */       o = p;
/*  562 */       p = q;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void unlinkLast(Node<E> last, Node<E> prev) {
/*  571 */     Node<E> o = null, p = prev; while (true) {
/*  572 */       Node<E> q; if (p.item != null || (q = p.prev) == null) {
/*  573 */         if (o != null && p.next != p && last.casPrev(prev, p)) {
/*  574 */           skipDeletedSuccessors(p);
/*  575 */           if (last.next == null && (p.prev == null || p.item != null) && p.next == last) {
/*      */ 
/*      */ 
/*      */             
/*  579 */             updateHead();
/*  580 */             updateTail();
/*      */ 
/*      */             
/*  583 */             o.lazySetPrev(o);
/*  584 */             o.lazySetNext(nextTerminator());
/*      */           } 
/*      */         } 
/*      */         return;
/*      */       } 
/*  589 */       if (p == q) {
/*      */         return;
/*      */       }
/*  592 */       o = p;
/*  593 */       p = q;
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
/*      */   private void updateHead() {
/*      */     Node<E> h;
/*      */     Node<E> p;
/*  609 */     label19: while ((h = this.head).item == null && (p = h.prev) != null) {
/*      */       while (true) {
/*  611 */         Node<E> q; if ((q = p.prev) == null || (q = (p = q).prev) == null) {
/*      */ 
/*      */ 
/*      */           
/*  615 */           if (casHead(h, p)) {
/*      */             return;
/*      */           }
/*      */           continue label19;
/*      */         } 
/*  620 */         if (h != this.head) {
/*      */           continue label19;
/*      */         }
/*  623 */         p = q;
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
/*      */   private void updateTail() {
/*      */     Node<E> t;
/*      */     Node<E> p;
/*  639 */     label19: while ((t = this.tail).item == null && (p = t.next) != null) {
/*      */       while (true) {
/*  641 */         Node<E> q; if ((q = p.next) == null || (q = (p = q).next) == null) {
/*      */ 
/*      */ 
/*      */           
/*  645 */           if (casTail(t, p)) {
/*      */             return;
/*      */           }
/*      */           continue label19;
/*      */         } 
/*  650 */         if (t != this.tail) {
/*      */           continue label19;
/*      */         }
/*  653 */         p = q;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void skipDeletedPredecessors(Node<E> x) {
/*      */     label21: do {
/*  661 */       Node<E> prev = x.prev;
/*  662 */       Node<E> p = prev;
/*      */ 
/*      */       
/*  665 */       while (p.item == null) {
/*      */         
/*  667 */         Node<E> q = p.prev;
/*  668 */         if (q == null) {
/*  669 */           if (p.next == p)
/*      */             continue label21; 
/*      */           break;
/*      */         } 
/*  673 */         if (p == q) {
/*      */           continue label21;
/*      */         }
/*  676 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  680 */       if (prev == p || x.casPrev(prev, p)) {
/*      */         return;
/*      */       }
/*  683 */     } while (x.item != null || x.next == null);
/*      */   }
/*      */ 
/*      */   
/*      */   private void skipDeletedSuccessors(Node<E> x) {
/*      */     label21: do {
/*  689 */       Node<E> next = x.next;
/*  690 */       Node<E> p = next;
/*      */ 
/*      */       
/*  693 */       while (p.item == null) {
/*      */         
/*  695 */         Node<E> q = p.next;
/*  696 */         if (q == null) {
/*  697 */           if (p.prev == p)
/*      */             continue label21; 
/*      */           break;
/*      */         } 
/*  701 */         if (p == q) {
/*      */           continue label21;
/*      */         }
/*  704 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  708 */       if (next == p || x.casNext(next, p)) {
/*      */         return;
/*      */       }
/*  711 */     } while (x.item != null || x.prev == null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Node<E> succ(Node<E> p) {
/*  721 */     Node<E> q = p.next;
/*  722 */     return (p == q) ? first() : q;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Node<E> pred(Node<E> p) {
/*  731 */     Node<E> q = p.prev;
/*  732 */     return (p == q) ? last() : q;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Node<E> first() {
/*      */     Node<E> h;
/*      */     Node<E> p;
/*      */     do {
/*  744 */       p = h = this.head; Node<E> q;
/*  745 */       while ((q = p.prev) != null && (q = (p = q).prev) != null)
/*      */       {
/*      */ 
/*      */         
/*  749 */         p = (h != (h = this.head)) ? h : q; } 
/*  750 */     } while (p != h && 
/*      */ 
/*      */       
/*  753 */       !casHead(h, p));
/*  754 */     return p;
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
/*      */   Node<E> last() {
/*      */     Node<E> t;
/*      */     Node<E> p;
/*      */     do {
/*  769 */       p = t = this.tail; Node<E> q;
/*  770 */       while ((q = p.next) != null && (q = (p = q).next) != null)
/*      */       {
/*      */ 
/*      */         
/*  774 */         p = (t != (t = this.tail)) ? t : q; } 
/*  775 */     } while (p != t && 
/*      */ 
/*      */       
/*  778 */       !casTail(t, p));
/*  779 */     return p;
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
/*      */   private static void checkNotNull(Object v) {
/*  793 */     if (v == null) {
/*  794 */       throw new NullPointerException();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private E screenNullResult(E v) {
/*  805 */     if (v == null)
/*  806 */       throw new NoSuchElementException(); 
/*  807 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ArrayList<E> toArrayList() {
/*  817 */     ArrayList<E> list = new ArrayList<>();
/*  818 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/*  819 */       E item = p.item;
/*  820 */       if (item != null)
/*  821 */         list.add(item); 
/*      */     } 
/*  823 */     return list;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public FastConcurrentDirectDeque() {
/*  830 */     this.head = this.tail = new Node<>(null);
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
/*      */   public FastConcurrentDirectDeque(Collection<? extends E> c) {
/*  844 */     Node<E> h = null, t = null;
/*  845 */     for (E e : c) {
/*  846 */       checkNotNull(e);
/*  847 */       Node<E> newNode = new Node<>(e);
/*  848 */       if (h == null) {
/*  849 */         h = t = newNode; continue;
/*      */       } 
/*  851 */       t.lazySetNext(newNode);
/*  852 */       newNode.lazySetPrev(t);
/*  853 */       t = newNode;
/*      */     } 
/*      */     
/*  856 */     initHeadTail(h, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initHeadTail(Node<E> h, Node<E> t) {
/*  863 */     if (h == t) {
/*  864 */       if (h == null) {
/*  865 */         h = t = new Node<>(null);
/*      */       } else {
/*      */         
/*  868 */         Node<E> newNode = new Node<>(null);
/*  869 */         t.lazySetNext(newNode);
/*  870 */         newNode.lazySetPrev(t);
/*  871 */         t = newNode;
/*      */       } 
/*      */     }
/*  874 */     this.head = h;
/*  875 */     this.tail = t;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void addFirst(E e) {
/*  886 */     linkFirst(e);
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
/*      */   public void addLast(E e) {
/*  899 */     linkLast(e);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean offerFirst(E e) {
/*  910 */     linkFirst(e);
/*  911 */     return true;
/*      */   }
/*      */   
/*      */   public Object offerFirstAndReturnToken(E e) {
/*  915 */     return linkFirst(e);
/*      */   }
/*      */   
/*      */   public Object offerLastAndReturnToken(E e) {
/*  919 */     return linkLast(e);
/*      */   }
/*      */   
/*      */   public void removeToken(Object token) {
/*  923 */     if (!(token instanceof Node)) {
/*  924 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*  927 */     Node<E> node = (Node)token;
/*  928 */     while (!node.casItem(node.item, null));
/*  929 */     unlink(node);
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
/*      */   public boolean offerLast(E e) {
/*  942 */     linkLast(e);
/*  943 */     return true;
/*      */   }
/*      */   
/*      */   public E peekFirst() {
/*  947 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/*  948 */       E item = p.item;
/*  949 */       if (item != null)
/*  950 */         return item; 
/*      */     } 
/*  952 */     return null;
/*      */   }
/*      */   
/*      */   public E peekLast() {
/*  956 */     for (Node<E> p = last(); p != null; p = pred(p)) {
/*  957 */       E item = p.item;
/*  958 */       if (item != null)
/*  959 */         return item; 
/*      */     } 
/*  961 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E getFirst() {
/*  968 */     return screenNullResult(peekFirst());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E getLast() {
/*  975 */     return screenNullResult(peekLast());
/*      */   }
/*      */   
/*      */   public E pollFirst() {
/*  979 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/*  980 */       E item = p.item;
/*  981 */       if (item != null && p.casItem(item, null)) {
/*  982 */         unlink(p);
/*  983 */         return item;
/*      */       } 
/*      */     } 
/*  986 */     return null;
/*      */   }
/*      */   
/*      */   public E pollLast() {
/*  990 */     for (Node<E> p = last(); p != null; p = pred(p)) {
/*  991 */       E item = p.item;
/*  992 */       if (item != null && p.casItem(item, null)) {
/*  993 */         unlink(p);
/*  994 */         return item;
/*      */       } 
/*      */     } 
/*  997 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E removeFirst() {
/* 1004 */     return screenNullResult(pollFirst());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E removeLast() {
/* 1011 */     return screenNullResult(pollLast());
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
/*      */   public boolean offer(E e) {
/* 1024 */     return offerLast(e);
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
/*      */   public boolean add(E e) {
/* 1036 */     return offerLast(e);
/*      */   }
/*      */   
/*      */   public E poll() {
/* 1040 */     return pollFirst();
/*      */   }
/*      */   
/*      */   public E peek() {
/* 1044 */     return peekFirst();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E remove() {
/* 1051 */     return removeFirst();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E pop() {
/* 1058 */     return removeFirst();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E element() {
/* 1065 */     return getFirst();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void push(E e) {
/* 1072 */     addFirst(e);
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
/*      */   public boolean removeFirstOccurrence(Object o) {
/* 1085 */     checkNotNull(o);
/* 1086 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1087 */       E item = p.item;
/* 1088 */       if (item != null && o.equals(item) && p.casItem(item, null)) {
/* 1089 */         unlink(p);
/* 1090 */         return true;
/*      */       } 
/*      */     } 
/* 1093 */     return false;
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
/*      */   public boolean removeLastOccurrence(Object o) {
/* 1106 */     checkNotNull(o);
/* 1107 */     for (Node<E> p = last(); p != null; p = pred(p)) {
/* 1108 */       E item = p.item;
/* 1109 */       if (item != null && o.equals(item) && p.casItem(item, null)) {
/* 1110 */         unlink(p);
/* 1111 */         return true;
/*      */       } 
/*      */     } 
/* 1114 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean contains(Object o) {
/* 1125 */     if (o == null) return false; 
/* 1126 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1127 */       E item = p.item;
/* 1128 */       if (item != null && o.equals(item))
/* 1129 */         return true; 
/*      */     } 
/* 1131 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/* 1140 */     return (peekFirst() == null);
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
/*      */   public int size() {
/* 1160 */     int count = 0;
/* 1161 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1162 */       if (p.item != null)
/*      */       {
/* 1164 */         if (++count == Integer.MAX_VALUE)
/*      */           break;  } 
/* 1166 */     }  return count;
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
/*      */   public boolean remove(Object o) {
/* 1179 */     return removeFirstOccurrence(o);
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
/*      */   public boolean addAll(Collection<? extends E> c) {
/*      */     Node<E> t;
/* 1195 */     if (c == this)
/*      */     {
/* 1197 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/* 1200 */     Node<E> beginningOfTheEnd = null, last = null;
/* 1201 */     for (E e : c) {
/* 1202 */       checkNotNull(e);
/* 1203 */       Node<E> newNode = new Node<>(e);
/* 1204 */       if (beginningOfTheEnd == null) {
/* 1205 */         beginningOfTheEnd = last = newNode; continue;
/*      */       } 
/* 1207 */       last.lazySetNext(newNode);
/* 1208 */       newNode.lazySetPrev(last);
/* 1209 */       last = newNode;
/*      */     } 
/*      */     
/* 1212 */     if (beginningOfTheEnd == null) {
/* 1213 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     label36: while (true) {
/* 1218 */       Node<E> p = t = this.tail; while (true) {
/* 1219 */         Node<E> q; if ((q = p.next) != null && (q = (p = q).next) != null) {
/*      */ 
/*      */ 
/*      */           
/* 1223 */           p = (t != (t = this.tail)) ? t : q; continue;
/* 1224 */         }  if (p.prev == p) {
/*      */           continue label36;
/*      */         }
/*      */         
/* 1228 */         beginningOfTheEnd.lazySetPrev(p);
/* 1229 */         if (p.casNext(null, beginningOfTheEnd))
/*      */           break; 
/*      */       }  break;
/* 1232 */     }  if (!casTail(t, last)) {
/*      */ 
/*      */       
/* 1235 */       t = this.tail;
/* 1236 */       if (last.next == null)
/* 1237 */         casTail(t, last); 
/*      */     } 
/* 1239 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void clear() {
/* 1250 */     while (pollFirst() != null);
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
/*      */   public Object[] toArray() {
/* 1267 */     return toArrayList().toArray();
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
/*      */   public <T> T[] toArray(T[] a) {
/* 1308 */     return toArrayList().toArray(a);
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
/*      */   public Iterator<E> iterator() {
/* 1321 */     return new Itr();
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
/*      */   public Iterator<E> descendingIterator() {
/* 1335 */     return new DescendingItr();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private abstract class AbstractItr
/*      */     implements Iterator<E>
/*      */   {
/*      */     private FastConcurrentDirectDeque.Node<E> nextNode;
/*      */ 
/*      */     
/*      */     private E nextItem;
/*      */ 
/*      */     
/*      */     private FastConcurrentDirectDeque.Node<E> lastRet;
/*      */ 
/*      */ 
/*      */     
/*      */     abstract FastConcurrentDirectDeque.Node<E> startNode();
/*      */ 
/*      */ 
/*      */     
/*      */     abstract FastConcurrentDirectDeque.Node<E> nextNode(FastConcurrentDirectDeque.Node<E> param1Node);
/*      */ 
/*      */ 
/*      */     
/*      */     AbstractItr() {
/* 1362 */       advance();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void advance() {
/* 1370 */       this.lastRet = this.nextNode;
/*      */       
/* 1372 */       FastConcurrentDirectDeque.Node<E> p = (this.nextNode == null) ? startNode() : nextNode(this.nextNode);
/* 1373 */       for (;; p = nextNode(p)) {
/* 1374 */         if (p == null) {
/*      */           
/* 1376 */           this.nextNode = null;
/* 1377 */           this.nextItem = null;
/*      */           break;
/*      */         } 
/* 1380 */         E item = p.item;
/* 1381 */         if (item != null) {
/* 1382 */           this.nextNode = p;
/* 1383 */           this.nextItem = item;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     public boolean hasNext() {
/* 1390 */       return (this.nextItem != null);
/*      */     }
/*      */     
/*      */     public E next() {
/* 1394 */       E item = this.nextItem;
/* 1395 */       if (item == null) throw new NoSuchElementException(); 
/* 1396 */       advance();
/* 1397 */       return item;
/*      */     }
/*      */     
/*      */     public void remove() {
/* 1401 */       FastConcurrentDirectDeque.Node<E> l = this.lastRet;
/* 1402 */       if (l == null) throw new IllegalStateException(); 
/* 1403 */       l.item = null;
/* 1404 */       FastConcurrentDirectDeque.this.unlink(l);
/* 1405 */       this.lastRet = null;
/*      */     }
/*      */   }
/*      */   
/*      */   private class Itr
/*      */     extends AbstractItr
/*      */   {
/*      */     private Itr() {}
/*      */     
/*      */     FastConcurrentDirectDeque.Node<E> startNode() {
/* 1415 */       return FastConcurrentDirectDeque.this.first();
/*      */     }
/*      */     
/*      */     FastConcurrentDirectDeque.Node<E> nextNode(FastConcurrentDirectDeque.Node<E> p) {
/* 1419 */       return FastConcurrentDirectDeque.this.succ(p);
/*      */     }
/*      */   }
/*      */   
/*      */   private class DescendingItr
/*      */     extends AbstractItr
/*      */   {
/*      */     private DescendingItr() {}
/*      */     
/*      */     FastConcurrentDirectDeque.Node<E> startNode() {
/* 1429 */       return FastConcurrentDirectDeque.this.last();
/*      */     }
/*      */     
/*      */     FastConcurrentDirectDeque.Node<E> nextNode(FastConcurrentDirectDeque.Node<E> p) {
/* 1433 */       return FastConcurrentDirectDeque.this.pred(p);
/*      */     }
/*      */   }
/*      */   
/*      */   static final class CLDSpliterator<E> implements Spliterator<E> {
/*      */     static final int MAX_BATCH = 33554432;
/*      */     final FastConcurrentDirectDeque<E> queue;
/*      */     FastConcurrentDirectDeque.Node<E> current;
/*      */     int batch;
/*      */     boolean exhausted;
/*      */     
/*      */     CLDSpliterator(FastConcurrentDirectDeque<E> queue) {
/* 1445 */       this.queue = queue;
/*      */     }
/*      */ 
/*      */     
/*      */     public Spliterator<E> trySplit() {
/* 1450 */       FastConcurrentDirectDeque<E> q = this.queue;
/* 1451 */       int b = this.batch;
/* 1452 */       int n = (b <= 0) ? 1 : ((b >= 33554432) ? 33554432 : (b + 1)); FastConcurrentDirectDeque.Node<E> p;
/* 1453 */       if (!this.exhausted && ((p = this.current) != null || (
/* 1454 */         p = q.first()) != null)) {
/* 1455 */         if (p.item == null && p == (p = p.next))
/* 1456 */           this.current = p = q.first(); 
/* 1457 */         if (p != null && p.next != null) {
/* 1458 */           Object[] a = new Object[n];
/* 1459 */           int i = 0;
/*      */           do {
/* 1461 */             a[i] = p.item; if (p.item != null)
/* 1462 */               i++; 
/* 1463 */             if (p != (p = p.next))
/* 1464 */               continue;  p = q.first();
/* 1465 */           } while (p != null && i < n);
/* 1466 */           if ((this.current = p) == null)
/* 1467 */             this.exhausted = true; 
/* 1468 */           if (i > 0) {
/* 1469 */             this.batch = i;
/* 1470 */             return 
/* 1471 */               Spliterators.spliterator(a, 0, i, 4368);
/*      */           } 
/*      */         } 
/*      */       } 
/*      */       
/* 1476 */       return null;
/*      */     }
/*      */ 
/*      */     
/*      */     public void forEachRemaining(Consumer<? super E> action) {
/* 1481 */       if (action == null) throw new NullPointerException(); 
/* 1482 */       FastConcurrentDirectDeque<E> q = this.queue; FastConcurrentDirectDeque.Node<E> p;
/* 1483 */       if (!this.exhausted && ((p = this.current) != null || (
/* 1484 */         p = q.first()) != null)) {
/* 1485 */         this.exhausted = true;
/*      */         do {
/* 1487 */           E e = p.item;
/* 1488 */           if (p == (p = p.next))
/* 1489 */             p = q.first(); 
/* 1490 */           if (e == null)
/* 1491 */             continue;  action.accept(e);
/* 1492 */         } while (p != null);
/*      */       } 
/*      */     }
/*      */ 
/*      */     
/*      */     public boolean tryAdvance(Consumer<? super E> action) {
/* 1498 */       if (action == null) throw new NullPointerException(); 
/* 1499 */       FastConcurrentDirectDeque<E> q = this.queue; FastConcurrentDirectDeque.Node<E> p;
/* 1500 */       if (!this.exhausted && ((p = this.current) != null || (
/* 1501 */         p = q.first()) != null)) {
/*      */         E e;
/*      */         do {
/* 1504 */           e = p.item;
/* 1505 */           if (p != (p = p.next))
/* 1506 */             continue;  p = q.first();
/* 1507 */         } while (e == null && p != null);
/* 1508 */         if ((this.current = p) == null)
/* 1509 */           this.exhausted = true; 
/* 1510 */         if (e != null) {
/* 1511 */           action.accept(e);
/* 1512 */           return true;
/*      */         } 
/*      */       } 
/* 1515 */       return false;
/*      */     }
/*      */     
/*      */     public long estimateSize() {
/* 1519 */       return Long.MAX_VALUE;
/*      */     }
/*      */     
/*      */     public int characteristics() {
/* 1523 */       return 4368;
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
/*      */   public Spliterator<E> spliterator() {
/* 1545 */     return new CLDSpliterator<>(this);
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
/*      */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 1560 */     s.defaultWriteObject();
/*      */ 
/*      */     
/* 1563 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1564 */       E item = p.item;
/* 1565 */       if (item != null) {
/* 1566 */         s.writeObject(item);
/*      */       }
/*      */     } 
/*      */     
/* 1570 */     s.writeObject(null);
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
/*      */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 1582 */     s.defaultReadObject();
/*      */ 
/*      */     
/* 1585 */     Node<E> h = null, t = null;
/*      */     Object item;
/* 1587 */     while ((item = s.readObject()) != null) {
/*      */       
/* 1589 */       Node<E> newNode = new Node<>((E)item);
/* 1590 */       if (h == null) {
/* 1591 */         h = t = newNode; continue;
/*      */       } 
/* 1593 */       t.lazySetNext(newNode);
/* 1594 */       newNode.lazySetPrev(t);
/* 1595 */       t = newNode;
/*      */     } 
/*      */     
/* 1598 */     initHeadTail(h, t);
/*      */   }
/*      */   
/*      */   private boolean casHead(Node<E> cmp, Node<E> val) {
/* 1602 */     return UNSAFE.compareAndSwapObject(this, headOffset, cmp, val);
/*      */   }
/*      */   
/*      */   private boolean casTail(Node<E> cmp, Node<E> val) {
/* 1606 */     return UNSAFE.compareAndSwapObject(this, tailOffset, cmp, val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1615 */   private static final Node<Object> PREV_TERMINATOR = new Node(); static {
/* 1616 */     PREV_TERMINATOR.next = PREV_TERMINATOR;
/* 1617 */   } private static final Node<Object> NEXT_TERMINATOR = new Node(); private static final int HOPS = 2; private static final Unsafe UNSAFE; static {
/* 1618 */     NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
/*      */     try {
/* 1620 */       UNSAFE = getUnsafe();
/* 1621 */       Class<?> k = FastConcurrentDirectDeque.class;
/*      */       
/* 1623 */       headOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("head"));
/*      */       
/* 1625 */       tailOffset = UNSAFE.objectFieldOffset(k.getDeclaredField("tail"));
/* 1626 */     } catch (Exception e) {
/* 1627 */       throw new Error(e);
/*      */     } 
/*      */   }
/*      */   private static final long headOffset; private static final long tailOffset;
/*      */   private static Unsafe getUnsafe() {
/* 1632 */     if (System.getSecurityManager() != null) {
/* 1633 */       return AccessController.<Unsafe>doPrivileged(new PrivilegedAction<Unsafe>() {
/*      */             public Unsafe run() {
/* 1635 */               return FastConcurrentDirectDeque.getUnsafe0();
/*      */             }
/*      */           });
/*      */     }
/* 1639 */     return getUnsafe0();
/*      */   }
/*      */   
/*      */   private static Unsafe getUnsafe0() {
/*      */     try {
/* 1644 */       Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
/* 1645 */       theUnsafe.setAccessible(true);
/* 1646 */       return (Unsafe)theUnsafe.get(null);
/* 1647 */     } catch (Throwable t) {
/* 1648 */       throw new RuntimeException("JDK did not allow accessing unsafe", t);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\FastConcurrentDirectDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */