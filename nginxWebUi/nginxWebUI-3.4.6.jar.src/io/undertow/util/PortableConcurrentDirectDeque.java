/*      */ package io.undertow.util;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Deque;
/*      */ import java.util.Iterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class PortableConcurrentDirectDeque<E>
/*      */   extends ConcurrentDirectDeque<E>
/*      */   implements Deque<E>, Serializable
/*      */ {
/*      */   private static final long serialVersionUID = 876323262645176354L;
/*      */   private volatile transient Node<E> head;
/*      */   private volatile transient Node<E> tail;
/*  272 */   private static final AtomicReferenceFieldUpdater<PortableConcurrentDirectDeque, Node> headUpdater = AtomicReferenceFieldUpdater.newUpdater(PortableConcurrentDirectDeque.class, Node.class, "head");
/*  273 */   private static final AtomicReferenceFieldUpdater<PortableConcurrentDirectDeque, Node> tailUpdater = AtomicReferenceFieldUpdater.newUpdater(PortableConcurrentDirectDeque.class, Node.class, "tail");
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   Node<E> prevTerminator() {
/*  279 */     return (Node)PREV_TERMINATOR;
/*      */   }
/*      */ 
/*      */   
/*      */   Node<E> nextTerminator() {
/*  284 */     return (Node)NEXT_TERMINATOR;
/*      */   }
/*      */   
/*      */   static final class Node<E> {
/*  288 */     private static final AtomicReferenceFieldUpdater<Node, Node> prevUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "prev");
/*  289 */     private static final AtomicReferenceFieldUpdater<Node, Node> nextUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Node.class, "next");
/*  290 */     private static final AtomicReferenceFieldUpdater<Node, Object> itemUpdater = AtomicReferenceFieldUpdater.newUpdater(Node.class, Object.class, "item");
/*      */ 
/*      */     
/*      */     volatile Node<E> prev;
/*      */ 
/*      */     
/*      */     volatile E item;
/*      */     
/*      */     volatile Node<E> next;
/*      */ 
/*      */     
/*      */     Node() {}
/*      */ 
/*      */     
/*      */     Node(E item) {
/*  305 */       this.item = item;
/*      */     }
/*      */     
/*      */     boolean casItem(E cmp, E val) {
/*  309 */       return itemUpdater.compareAndSet(this, cmp, val);
/*      */     }
/*      */     
/*      */     void lazySetNext(Node<E> val) {
/*  313 */       this.next = val;
/*      */     }
/*      */     
/*      */     boolean casNext(Node<E> cmp, Node<E> val) {
/*  317 */       return nextUpdater.compareAndSet(this, cmp, val);
/*      */     }
/*      */     
/*      */     void lazySetPrev(Node<E> val) {
/*  321 */       this.prev = val;
/*      */     }
/*      */     
/*      */     boolean casPrev(Node<E> cmp, Node<E> val) {
/*  325 */       return prevUpdater.compareAndSet(this, cmp, val);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private Node linkFirst(E e) {
/*      */     Node<E> h, p;
/*  333 */     checkNotNull(e);
/*  334 */     Node<E> newNode = new Node<>(e);
/*      */ 
/*      */     
/*      */     label22: while (true) {
/*  338 */       p = h = this.head; while (true) {
/*  339 */         Node<E> q; if ((q = p.prev) != null && (q = (p = q).prev) != null) {
/*      */ 
/*      */ 
/*      */           
/*  343 */           p = (h != (h = this.head)) ? h : q; continue;
/*  344 */         }  if (p.next == p) {
/*      */           continue label22;
/*      */         }
/*      */         
/*  348 */         newNode.lazySetNext(p);
/*  349 */         if (p.casPrev(null, newNode))
/*      */           break; 
/*      */       }  break;
/*      */     } 
/*  353 */     if (p != h)
/*  354 */       casHead(h, newNode); 
/*  355 */     return newNode;
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
/*  366 */     checkNotNull(e);
/*  367 */     Node<E> newNode = new Node<>(e);
/*      */ 
/*      */     
/*      */     label22: while (true) {
/*  371 */       p = t = this.tail; while (true) {
/*  372 */         Node<E> q; if ((q = p.next) != null && (q = (p = q).next) != null) {
/*      */ 
/*      */ 
/*      */           
/*  376 */           p = (t != (t = this.tail)) ? t : q; continue;
/*  377 */         }  if (p.prev == p) {
/*      */           continue label22;
/*      */         }
/*      */         
/*  381 */         newNode.lazySetPrev(p);
/*  382 */         if (p.casNext(null, newNode))
/*      */           break; 
/*      */       }  break;
/*      */     } 
/*  386 */     if (p != t)
/*  387 */       casTail(t, newNode); 
/*  388 */     return newNode;
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
/*  401 */     Node<E> prev = x.prev;
/*  402 */     Node<E> next = x.next;
/*  403 */     if (prev == null) {
/*  404 */       unlinkFirst(x, next);
/*  405 */     } else if (next == null) {
/*  406 */       unlinkLast(x, prev);
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
/*  429 */       int hops = 1;
/*      */       
/*      */       Node<E> p;
/*  432 */       for (p = prev;; hops++) {
/*  433 */         if (p.item != null) {
/*  434 */           activePred = p;
/*  435 */           isFirst = false;
/*      */           break;
/*      */         } 
/*  438 */         Node<E> q = p.prev;
/*  439 */         if (q == null) {
/*  440 */           if (p.next == p)
/*      */             return; 
/*  442 */           activePred = p;
/*  443 */           isFirst = true;
/*      */           break;
/*      */         } 
/*  446 */         if (p == q) {
/*      */           return;
/*      */         }
/*  449 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  453 */       for (p = next;; hops++) {
/*  454 */         if (p.item != null) {
/*  455 */           activeSucc = p;
/*  456 */           isLast = false;
/*      */           break;
/*      */         } 
/*  459 */         Node<E> q = p.next;
/*  460 */         if (q == null) {
/*  461 */           if (p.prev == p)
/*      */             return; 
/*  463 */           activeSucc = p;
/*  464 */           isLast = true;
/*      */           break;
/*      */         } 
/*  467 */         if (p == q) {
/*      */           return;
/*      */         }
/*  470 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  474 */       if (hops < 2 && (isFirst || isLast)) {
/*      */         return;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  481 */       skipDeletedSuccessors(activePred);
/*  482 */       skipDeletedPredecessors(activeSucc);
/*      */ 
/*      */       
/*  485 */       if ((isFirst || isLast) && activePred.next == activeSucc && activeSucc.prev == activePred && (isFirst ? (activePred.prev == null) : (activePred.item != null)) && (isLast ? (activeSucc.next == null) : (activeSucc.item != null))) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  493 */         updateHead();
/*  494 */         updateTail();
/*      */ 
/*      */         
/*  497 */         x.lazySetPrev(isFirst ? prevTerminator() : x);
/*  498 */         x.lazySetNext(isLast ? nextTerminator() : x);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void unlinkFirst(Node<E> first, Node<E> next) {
/*  507 */     Node<E> o = null, p = next; while (true) {
/*  508 */       Node<E> q; if (p.item != null || (q = p.next) == null) {
/*  509 */         if (o != null && p.prev != p && first.casNext(next, p)) {
/*  510 */           skipDeletedPredecessors(p);
/*  511 */           if (first.prev == null && (p.next == null || p.item != null) && p.prev == first) {
/*      */ 
/*      */ 
/*      */             
/*  515 */             updateHead();
/*  516 */             updateTail();
/*      */ 
/*      */             
/*  519 */             o.lazySetNext(o);
/*  520 */             o.lazySetPrev(prevTerminator());
/*      */           } 
/*      */         } 
/*      */         return;
/*      */       } 
/*  525 */       if (p == q) {
/*      */         return;
/*      */       }
/*  528 */       o = p;
/*  529 */       p = q;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void unlinkLast(Node<E> last, Node<E> prev) {
/*  538 */     Node<E> o = null, p = prev; while (true) {
/*  539 */       Node<E> q; if (p.item != null || (q = p.prev) == null) {
/*  540 */         if (o != null && p.next != p && last.casPrev(prev, p)) {
/*  541 */           skipDeletedSuccessors(p);
/*  542 */           if (last.next == null && (p.prev == null || p.item != null) && p.next == last) {
/*      */ 
/*      */ 
/*      */             
/*  546 */             updateHead();
/*  547 */             updateTail();
/*      */ 
/*      */             
/*  550 */             o.lazySetPrev(o);
/*  551 */             o.lazySetNext(nextTerminator());
/*      */           } 
/*      */         } 
/*      */         return;
/*      */       } 
/*  556 */       if (p == q) {
/*      */         return;
/*      */       }
/*  559 */       o = p;
/*  560 */       p = q;
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
/*  576 */     label19: while ((h = this.head).item == null && (p = h.prev) != null) {
/*      */       while (true) {
/*  578 */         Node<E> q; if ((q = p.prev) == null || (q = (p = q).prev) == null) {
/*      */ 
/*      */ 
/*      */           
/*  582 */           if (casHead(h, p)) {
/*      */             return;
/*      */           }
/*      */           continue label19;
/*      */         } 
/*  587 */         if (h != this.head) {
/*      */           continue label19;
/*      */         }
/*  590 */         p = q;
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
/*  606 */     label19: while ((t = this.tail).item == null && (p = t.next) != null) {
/*      */       while (true) {
/*  608 */         Node<E> q; if ((q = p.next) == null || (q = (p = q).next) == null) {
/*      */ 
/*      */ 
/*      */           
/*  612 */           if (casTail(t, p)) {
/*      */             return;
/*      */           }
/*      */           continue label19;
/*      */         } 
/*  617 */         if (t != this.tail) {
/*      */           continue label19;
/*      */         }
/*  620 */         p = q;
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private void skipDeletedPredecessors(Node<E> x) {
/*      */     label21: do {
/*  628 */       Node<E> prev = x.prev;
/*  629 */       Node<E> p = prev;
/*      */ 
/*      */       
/*  632 */       while (p.item == null) {
/*      */         
/*  634 */         Node<E> q = p.prev;
/*  635 */         if (q == null) {
/*  636 */           if (p.next == p)
/*      */             continue label21; 
/*      */           break;
/*      */         } 
/*  640 */         if (p == q) {
/*      */           continue label21;
/*      */         }
/*  643 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  647 */       if (prev == p || x.casPrev(prev, p)) {
/*      */         return;
/*      */       }
/*  650 */     } while (x.item != null || x.next == null);
/*      */   }
/*      */ 
/*      */   
/*      */   private void skipDeletedSuccessors(Node<E> x) {
/*      */     label21: do {
/*  656 */       Node<E> next = x.next;
/*  657 */       Node<E> p = next;
/*      */ 
/*      */       
/*  660 */       while (p.item == null) {
/*      */         
/*  662 */         Node<E> q = p.next;
/*  663 */         if (q == null) {
/*  664 */           if (p.prev == p)
/*      */             continue label21; 
/*      */           break;
/*      */         } 
/*  668 */         if (p == q) {
/*      */           continue label21;
/*      */         }
/*  671 */         p = q;
/*      */       } 
/*      */ 
/*      */       
/*  675 */       if (next == p || x.casNext(next, p)) {
/*      */         return;
/*      */       }
/*  678 */     } while (x.item != null || x.prev == null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Node<E> succ(Node<E> p) {
/*  688 */     Node<E> q = p.next;
/*  689 */     return (p == q) ? first() : q;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   final Node<E> pred(Node<E> p) {
/*  698 */     Node<E> q = p.prev;
/*  699 */     return (p == q) ? last() : q;
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
/*  711 */       p = h = this.head; Node<E> q;
/*  712 */       while ((q = p.prev) != null && (q = (p = q).prev) != null)
/*      */       {
/*      */ 
/*      */         
/*  716 */         p = (h != (h = this.head)) ? h : q; } 
/*  717 */     } while (p != h && 
/*      */ 
/*      */       
/*  720 */       !casHead(h, p));
/*  721 */     return p;
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
/*  736 */       p = t = this.tail; Node<E> q;
/*  737 */       while ((q = p.next) != null && (q = (p = q).next) != null)
/*      */       {
/*      */ 
/*      */         
/*  741 */         p = (t != (t = this.tail)) ? t : q; } 
/*  742 */     } while (p != t && 
/*      */ 
/*      */       
/*  745 */       !casTail(t, p));
/*  746 */     return p;
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
/*  760 */     if (v == null) {
/*  761 */       throw new NullPointerException();
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
/*  772 */     if (v == null)
/*  773 */       throw new NoSuchElementException(); 
/*  774 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private ArrayList<E> toArrayList() {
/*  784 */     ArrayList<E> list = new ArrayList<>();
/*  785 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/*  786 */       E item = p.item;
/*  787 */       if (item != null)
/*  788 */         list.add(item); 
/*      */     } 
/*  790 */     return list;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public PortableConcurrentDirectDeque() {
/*  797 */     this.head = this.tail = new Node<>(null);
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
/*      */   public PortableConcurrentDirectDeque(Collection<? extends E> c) {
/*  811 */     Node<E> h = null, t = null;
/*  812 */     for (E e : c) {
/*  813 */       checkNotNull(e);
/*  814 */       Node<E> newNode = new Node<>(e);
/*  815 */       if (h == null) {
/*  816 */         h = t = newNode; continue;
/*      */       } 
/*  818 */       t.lazySetNext(newNode);
/*  819 */       newNode.lazySetPrev(t);
/*  820 */       t = newNode;
/*      */     } 
/*      */     
/*  823 */     initHeadTail(h, t);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void initHeadTail(Node<E> h, Node<E> t) {
/*  830 */     if (h == t) {
/*  831 */       if (h == null) {
/*  832 */         h = t = new Node<>(null);
/*      */       } else {
/*      */         
/*  835 */         Node<E> newNode = new Node<>(null);
/*  836 */         t.lazySetNext(newNode);
/*  837 */         newNode.lazySetPrev(t);
/*  838 */         t = newNode;
/*      */       } 
/*      */     }
/*  841 */     this.head = h;
/*  842 */     this.tail = t;
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
/*  853 */     linkFirst(e);
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
/*  866 */     linkLast(e);
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
/*  877 */     linkFirst(e);
/*  878 */     return true;
/*      */   }
/*      */   
/*      */   public Object offerFirstAndReturnToken(E e) {
/*  882 */     return linkFirst(e);
/*      */   }
/*      */   
/*      */   public Object offerLastAndReturnToken(E e) {
/*  886 */     return linkLast(e);
/*      */   }
/*      */   
/*      */   public void removeToken(Object token) {
/*  890 */     if (!(token instanceof Node)) {
/*  891 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/*  894 */     Node<E> node = (Node)token;
/*  895 */     while (!node.casItem(node.item, null));
/*  896 */     unlink(node);
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
/*  909 */     linkLast(e);
/*  910 */     return true;
/*      */   }
/*      */   
/*      */   public E peekFirst() {
/*  914 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/*  915 */       E item = p.item;
/*  916 */       if (item != null)
/*  917 */         return item; 
/*      */     } 
/*  919 */     return null;
/*      */   }
/*      */   
/*      */   public E peekLast() {
/*  923 */     for (Node<E> p = last(); p != null; p = pred(p)) {
/*  924 */       E item = p.item;
/*  925 */       if (item != null)
/*  926 */         return item; 
/*      */     } 
/*  928 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E getFirst() {
/*  935 */     return screenNullResult(peekFirst());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E getLast() {
/*  942 */     return screenNullResult(peekLast());
/*      */   }
/*      */   
/*      */   public E pollFirst() {
/*  946 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/*  947 */       E item = p.item;
/*  948 */       if (item != null && p.casItem(item, null)) {
/*  949 */         unlink(p);
/*  950 */         return item;
/*      */       } 
/*      */     } 
/*  953 */     return null;
/*      */   }
/*      */   
/*      */   public E pollLast() {
/*  957 */     for (Node<E> p = last(); p != null; p = pred(p)) {
/*  958 */       E item = p.item;
/*  959 */       if (item != null && p.casItem(item, null)) {
/*  960 */         unlink(p);
/*  961 */         return item;
/*      */       } 
/*      */     } 
/*  964 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E removeFirst() {
/*  971 */     return screenNullResult(pollFirst());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public E removeLast() {
/*  978 */     return screenNullResult(pollLast());
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
/*  991 */     return offerLast(e);
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
/* 1003 */     return offerLast(e);
/*      */   }
/*      */   
/*      */   public E poll() {
/* 1007 */     return pollFirst();
/*      */   }
/*      */   
/*      */   public E remove() {
/* 1011 */     return removeFirst();
/*      */   }
/*      */   
/*      */   public E peek() {
/* 1015 */     return peekFirst();
/*      */   }
/*      */   
/*      */   public E element() {
/* 1019 */     return getFirst();
/*      */   }
/*      */   
/*      */   public void push(E e) {
/* 1023 */     addFirst(e);
/*      */   }
/*      */   
/*      */   public E pop() {
/* 1027 */     return removeFirst();
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
/* 1040 */     checkNotNull(o);
/* 1041 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1042 */       E item = p.item;
/* 1043 */       if (item != null && o.equals(item) && p.casItem(item, null)) {
/* 1044 */         unlink(p);
/* 1045 */         return true;
/*      */       } 
/*      */     } 
/* 1048 */     return false;
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
/* 1061 */     checkNotNull(o);
/* 1062 */     for (Node<E> p = last(); p != null; p = pred(p)) {
/* 1063 */       E item = p.item;
/* 1064 */       if (item != null && o.equals(item) && p.casItem(item, null)) {
/* 1065 */         unlink(p);
/* 1066 */         return true;
/*      */       } 
/*      */     } 
/* 1069 */     return false;
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
/* 1080 */     if (o == null) return false; 
/* 1081 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1082 */       E item = p.item;
/* 1083 */       if (item != null && o.equals(item))
/* 1084 */         return true; 
/*      */     } 
/* 1086 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public boolean isEmpty() {
/* 1095 */     return (peekFirst() == null);
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
/* 1115 */     int count = 0;
/* 1116 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1117 */       if (p.item != null)
/*      */       {
/* 1119 */         if (++count == Integer.MAX_VALUE)
/*      */           break;  } 
/* 1121 */     }  return count;
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
/* 1134 */     return removeFirstOccurrence(o);
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
/* 1150 */     if (c == this)
/*      */     {
/* 1152 */       throw new IllegalArgumentException();
/*      */     }
/*      */     
/* 1155 */     Node<E> beginningOfTheEnd = null, last = null;
/* 1156 */     for (E e : c) {
/* 1157 */       checkNotNull(e);
/* 1158 */       Node<E> newNode = new Node<>(e);
/* 1159 */       if (beginningOfTheEnd == null) {
/* 1160 */         beginningOfTheEnd = last = newNode; continue;
/*      */       } 
/* 1162 */       last.lazySetNext(newNode);
/* 1163 */       newNode.lazySetPrev(last);
/* 1164 */       last = newNode;
/*      */     } 
/*      */     
/* 1167 */     if (beginningOfTheEnd == null) {
/* 1168 */       return false;
/*      */     }
/*      */ 
/*      */     
/*      */     label36: while (true) {
/* 1173 */       Node<E> p = t = this.tail; while (true) {
/* 1174 */         Node<E> q; if ((q = p.next) != null && (q = (p = q).next) != null) {
/*      */ 
/*      */ 
/*      */           
/* 1178 */           p = (t != (t = this.tail)) ? t : q; continue;
/* 1179 */         }  if (p.prev == p) {
/*      */           continue label36;
/*      */         }
/*      */         
/* 1183 */         beginningOfTheEnd.lazySetPrev(p);
/* 1184 */         if (p.casNext(null, beginningOfTheEnd))
/*      */           break; 
/*      */       }  break;
/* 1187 */     }  if (!casTail(t, last)) {
/*      */ 
/*      */       
/* 1190 */       t = this.tail;
/* 1191 */       if (last.next == null)
/* 1192 */         casTail(t, last); 
/*      */     } 
/* 1194 */     return true;
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
/* 1205 */     while (pollFirst() != null);
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
/* 1222 */     return toArrayList().toArray();
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
/* 1263 */     return toArrayList().toArray(a);
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
/*      */   public Iterator<E> iterator() {
/* 1280 */     return new Itr();
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
/*      */   public Iterator<E> descendingIterator() {
/* 1298 */     return new DescendingItr();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private abstract class AbstractItr
/*      */     implements Iterator<E>
/*      */   {
/*      */     private PortableConcurrentDirectDeque.Node<E> nextNode;
/*      */ 
/*      */     
/*      */     private E nextItem;
/*      */ 
/*      */     
/*      */     private PortableConcurrentDirectDeque.Node<E> lastRet;
/*      */ 
/*      */ 
/*      */     
/*      */     abstract PortableConcurrentDirectDeque.Node<E> startNode();
/*      */ 
/*      */ 
/*      */     
/*      */     abstract PortableConcurrentDirectDeque.Node<E> nextNode(PortableConcurrentDirectDeque.Node<E> param1Node);
/*      */ 
/*      */ 
/*      */     
/*      */     AbstractItr() {
/* 1325 */       advance();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private void advance() {
/* 1333 */       this.lastRet = this.nextNode;
/*      */       
/* 1335 */       PortableConcurrentDirectDeque.Node<E> p = (this.nextNode == null) ? startNode() : nextNode(this.nextNode);
/* 1336 */       for (;; p = nextNode(p)) {
/* 1337 */         if (p == null) {
/*      */           
/* 1339 */           this.nextNode = null;
/* 1340 */           this.nextItem = null;
/*      */           break;
/*      */         } 
/* 1343 */         E item = p.item;
/* 1344 */         if (item != null) {
/* 1345 */           this.nextNode = p;
/* 1346 */           this.nextItem = item;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/*      */     public boolean hasNext() {
/* 1353 */       return (this.nextItem != null);
/*      */     }
/*      */     
/*      */     public E next() {
/* 1357 */       E item = this.nextItem;
/* 1358 */       if (item == null) throw new NoSuchElementException(); 
/* 1359 */       advance();
/* 1360 */       return item;
/*      */     }
/*      */     
/*      */     public void remove() {
/* 1364 */       PortableConcurrentDirectDeque.Node<E> l = this.lastRet;
/* 1365 */       if (l == null) throw new IllegalStateException(); 
/* 1366 */       l.item = null;
/* 1367 */       PortableConcurrentDirectDeque.this.unlink(l);
/* 1368 */       this.lastRet = null;
/*      */     } }
/*      */   
/*      */   private class Itr extends AbstractItr {
/*      */     private Itr() {}
/*      */     
/*      */     PortableConcurrentDirectDeque.Node<E> startNode() {
/* 1375 */       return PortableConcurrentDirectDeque.this.first();
/*      */     }
/*      */     
/*      */     PortableConcurrentDirectDeque.Node<E> nextNode(PortableConcurrentDirectDeque.Node<E> p) {
/* 1379 */       return PortableConcurrentDirectDeque.this.succ(p);
/*      */     }
/*      */   }
/*      */   
/*      */   private class DescendingItr
/*      */     extends AbstractItr {
/*      */     private DescendingItr() {}
/*      */     
/*      */     PortableConcurrentDirectDeque.Node<E> startNode() {
/* 1388 */       return PortableConcurrentDirectDeque.this.last();
/*      */     }
/*      */     
/*      */     PortableConcurrentDirectDeque.Node<E> nextNode(PortableConcurrentDirectDeque.Node<E> p) {
/* 1392 */       return PortableConcurrentDirectDeque.this.pred(p);
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
/*      */   private void writeObject(ObjectOutputStream s) throws IOException {
/* 1406 */     s.defaultWriteObject();
/*      */ 
/*      */     
/* 1409 */     for (Node<E> p = first(); p != null; p = succ(p)) {
/* 1410 */       E item = p.item;
/* 1411 */       if (item != null) {
/* 1412 */         s.writeObject(item);
/*      */       }
/*      */     } 
/*      */     
/* 1416 */     s.writeObject(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
/* 1424 */     s.defaultReadObject();
/*      */ 
/*      */     
/* 1427 */     Node<E> h = null, t = null;
/*      */     Object item;
/* 1429 */     while ((item = s.readObject()) != null) {
/*      */       
/* 1431 */       Node<E> newNode = new Node<>((E)item);
/* 1432 */       if (h == null) {
/* 1433 */         h = t = newNode; continue;
/*      */       } 
/* 1435 */       t.lazySetNext(newNode);
/* 1436 */       newNode.lazySetPrev(t);
/* 1437 */       t = newNode;
/*      */     } 
/*      */     
/* 1440 */     initHeadTail(h, t);
/*      */   }
/*      */   
/*      */   private boolean casHead(Node<E> cmp, Node<E> val) {
/* 1444 */     return headUpdater.compareAndSet(this, cmp, val);
/*      */   }
/*      */   
/*      */   private boolean casTail(Node<E> cmp, Node<E> val) {
/* 1448 */     return tailUpdater.compareAndSet(this, cmp, val);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/* 1454 */   private static final Node<Object> PREV_TERMINATOR = new Node(); static {
/* 1455 */     PREV_TERMINATOR.next = PREV_TERMINATOR;
/* 1456 */   } private static final Node<Object> NEXT_TERMINATOR = new Node(); static {
/* 1457 */     NEXT_TERMINATOR.prev = NEXT_TERMINATOR;
/*      */   }
/*      */   
/*      */   private static final int HOPS = 2;
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PortableConcurrentDirectDeque.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */