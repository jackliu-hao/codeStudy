/*     */ package org.xnio;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.RandomAccess;
/*     */ import org.xnio._private.Messages;
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
/*     */ public final class Sequence<T>
/*     */   extends AbstractList<T>
/*     */   implements List<T>, RandomAccess, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 3042164316147742903L;
/*     */   private final Object[] values;
/*  43 */   private static final Object[] empty = new Object[0];
/*     */   
/*     */   private Sequence(Object[] values) {
/*  46 */     Object[] realValues = (Object[])values.clone();
/*  47 */     this.values = realValues;
/*  48 */     for (int i = 0, length = realValues.length; i < length; i++) {
/*  49 */       if (realValues[i] == null) {
/*  50 */         throw Messages.msg.nullArrayIndex("option", i);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*  55 */   private static final Sequence<?> EMPTY = new Sequence(empty);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Sequence<T> of(T... members) {
/*  65 */     if (members.length == 0) {
/*  66 */       return empty();
/*     */     }
/*  68 */     return new Sequence<>((Object[])members);
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
/*     */   public static <T> Sequence<T> of(Collection<T> members) {
/*  80 */     if (members instanceof Sequence) {
/*  81 */       return (Sequence<T>)members;
/*     */     }
/*  83 */     Object[] objects = members.toArray();
/*  84 */     if (objects.length == 0) {
/*  85 */       return empty();
/*     */     }
/*  87 */     return new Sequence<>(objects);
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
/*     */   public <N> Sequence<N> cast(Class<N> newType) throws ClassCastException {
/* 100 */     for (Object value : this.values) {
/* 101 */       newType.cast(value);
/*     */     }
/* 103 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Sequence<T> empty() {
/* 114 */     return (Sequence)EMPTY;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<T> iterator() {
/* 124 */     return Arrays.<T>asList((T[])this.values).iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int size() {
/* 133 */     return this.values.length;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEmpty() {
/* 142 */     return (this.values.length == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] toArray() {
/* 151 */     return (Object[])this.values.clone();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public T get(int index) {
/* 162 */     return (T)this.values[index];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object other) {
/* 172 */     return (other instanceof Sequence && equals((Sequence)other));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Sequence<?> other) {
/* 182 */     return (this == other || (other != null && Arrays.equals(this.values, other.values)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 191 */     return Arrays.hashCode(this.values);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\Sequence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */