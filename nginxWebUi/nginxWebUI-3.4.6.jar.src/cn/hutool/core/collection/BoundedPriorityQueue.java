/*    */ package cn.hutool.core.collection;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Comparator;
/*    */ import java.util.Iterator;
/*    */ import java.util.PriorityQueue;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BoundedPriorityQueue<E>
/*    */   extends PriorityQueue<E>
/*    */ {
/*    */   private static final long serialVersionUID = 3794348988671694820L;
/*    */   private final int capacity;
/*    */   private final Comparator<? super E> comparator;
/*    */   
/*    */   public BoundedPriorityQueue(int capacity) {
/* 24 */     this(capacity, (Comparator<? super E>)null);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BoundedPriorityQueue(int capacity, Comparator<? super E> comparator) {
/* 33 */     super(capacity, (o1, o2) -> {
/*    */           int cResult;
/*    */           
/*    */           if (comparator != null) {
/*    */             cResult = comparator.compare(o1, o2);
/*    */           } else {
/*    */             Comparable<E> o1c = (Comparable<E>)o1;
/*    */             
/*    */             cResult = o1c.compareTo((E)o2);
/*    */           } 
/*    */           return -cResult;
/*    */         });
/* 45 */     this.capacity = capacity;
/* 46 */     this.comparator = comparator;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean offer(E e) {
/* 56 */     if (size() >= this.capacity) {
/* 57 */       E head = peek();
/* 58 */       if (comparator().compare(e, head) <= 0) {
/* 59 */         return true;
/*    */       }
/*    */       
/* 62 */       poll();
/*    */     } 
/* 64 */     return super.offer(e);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean addAll(E[] c) {
/* 74 */     return addAll(Arrays.asList(c));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ArrayList<E> toList() {
/* 81 */     ArrayList<E> list = new ArrayList<>(this);
/* 82 */     list.sort(this.comparator);
/* 83 */     return list;
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<E> iterator() {
/* 88 */     return toList().iterator();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\BoundedPriorityQueue.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */