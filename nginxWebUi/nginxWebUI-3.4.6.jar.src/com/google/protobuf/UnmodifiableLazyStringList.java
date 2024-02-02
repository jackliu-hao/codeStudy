/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.AbstractList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import java.util.RandomAccess;
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
/*     */ public class UnmodifiableLazyStringList
/*     */   extends AbstractList<String>
/*     */   implements LazyStringList, RandomAccess
/*     */ {
/*     */   private final LazyStringList list;
/*     */   
/*     */   public UnmodifiableLazyStringList(LazyStringList list) {
/*  53 */     this.list = list;
/*     */   }
/*     */ 
/*     */   
/*     */   public String get(int index) {
/*  58 */     return this.list.get(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public Object getRaw(int index) {
/*  63 */     return this.list.getRaw(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public int size() {
/*  68 */     return this.list.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteString getByteString(int index) {
/*  73 */     return this.list.getByteString(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(ByteString element) {
/*  78 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(int index, ByteString element) {
/*  83 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAllByteString(Collection<? extends ByteString> element) {
/*  88 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getByteArray(int index) {
/*  93 */     return this.list.getByteArray(index);
/*     */   }
/*     */ 
/*     */   
/*     */   public void add(byte[] element) {
/*  98 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public void set(int index, byte[] element) {
/* 103 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean addAllByteArray(Collection<byte[]> element) {
/* 108 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public ListIterator<String> listIterator(final int index) {
/* 113 */     return new ListIterator<String>() {
/* 114 */         ListIterator<String> iter = UnmodifiableLazyStringList.this.list.listIterator(index);
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 118 */           return this.iter.hasNext();
/*     */         }
/*     */ 
/*     */         
/*     */         public String next() {
/* 123 */           return this.iter.next();
/*     */         }
/*     */ 
/*     */         
/*     */         public boolean hasPrevious() {
/* 128 */           return this.iter.hasPrevious();
/*     */         }
/*     */ 
/*     */         
/*     */         public String previous() {
/* 133 */           return this.iter.previous();
/*     */         }
/*     */ 
/*     */         
/*     */         public int nextIndex() {
/* 138 */           return this.iter.nextIndex();
/*     */         }
/*     */ 
/*     */         
/*     */         public int previousIndex() {
/* 143 */           return this.iter.previousIndex();
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 148 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public void set(String o) {
/* 153 */           throw new UnsupportedOperationException();
/*     */         }
/*     */ 
/*     */         
/*     */         public void add(String o) {
/* 158 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> iterator() {
/* 165 */     return new Iterator<String>() {
/* 166 */         Iterator<String> iter = UnmodifiableLazyStringList.this.list.iterator();
/*     */ 
/*     */         
/*     */         public boolean hasNext() {
/* 170 */           return this.iter.hasNext();
/*     */         }
/*     */ 
/*     */         
/*     */         public String next() {
/* 175 */           return this.iter.next();
/*     */         }
/*     */ 
/*     */         
/*     */         public void remove() {
/* 180 */           throw new UnsupportedOperationException();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public List<?> getUnderlyingElements() {
/* 188 */     return this.list.getUnderlyingElements();
/*     */   }
/*     */ 
/*     */   
/*     */   public void mergeFrom(LazyStringList other) {
/* 193 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<byte[]> asByteArrayList() {
/* 198 */     return (List)Collections.unmodifiableList((List)this.list.asByteArrayList());
/*     */   }
/*     */ 
/*     */   
/*     */   public List<ByteString> asByteStringList() {
/* 203 */     return Collections.unmodifiableList(this.list.asByteStringList());
/*     */   }
/*     */ 
/*     */   
/*     */   public LazyStringList getUnmodifiableView() {
/* 208 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\UnmodifiableLazyStringList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */