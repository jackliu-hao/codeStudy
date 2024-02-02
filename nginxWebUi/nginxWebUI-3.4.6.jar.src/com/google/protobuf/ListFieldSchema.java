/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
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
/*     */ abstract class ListFieldSchema
/*     */ {
/*     */   private ListFieldSchema() {}
/*     */   
/*  45 */   private static final ListFieldSchema FULL_INSTANCE = new ListFieldSchemaFull();
/*  46 */   private static final ListFieldSchema LITE_INSTANCE = new ListFieldSchemaLite();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static ListFieldSchema full() {
/*  55 */     return FULL_INSTANCE;
/*     */   } abstract <L> List<L> mutableListAt(Object paramObject, long paramLong);
/*     */   abstract void makeImmutableListAt(Object paramObject, long paramLong);
/*     */   static ListFieldSchema lite() {
/*  59 */     return LITE_INSTANCE;
/*     */   }
/*     */   
/*     */   abstract <L> void mergeListsAt(Object paramObject1, Object paramObject2, long paramLong);
/*     */   
/*     */   private static final class ListFieldSchemaFull
/*     */     extends ListFieldSchema {
/*  66 */     private static final Class<?> UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(Collections.emptyList()).getClass();
/*     */     private ListFieldSchemaFull() {}
/*     */     
/*     */     <L> List<L> mutableListAt(Object message, long offset) {
/*  70 */       return mutableListAt(message, offset, 10);
/*     */     }
/*     */ 
/*     */     
/*     */     void makeImmutableListAt(Object message, long offset) {
/*  75 */       List<?> list = (List)UnsafeUtil.getObject(message, offset);
/*  76 */       Object<?> immutable = null;
/*  77 */       if (list instanceof LazyStringList)
/*  78 */       { immutable = (Object<?>)((LazyStringList)list).getUnmodifiableView(); }
/*  79 */       else { if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
/*     */           return;
/*     */         }
/*  82 */         if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList) {
/*  83 */           if (((Internal.ProtobufList)list).isModifiable()) {
/*  84 */             ((Internal.ProtobufList)list).makeImmutable();
/*     */           }
/*     */           return;
/*     */         } 
/*  88 */         immutable = Collections.unmodifiableList(list); }
/*     */       
/*  90 */       UnsafeUtil.putObject(message, offset, immutable);
/*     */     }
/*     */ 
/*     */     
/*     */     private static <L> List<L> mutableListAt(Object message, long offset, int additionalCapacity) {
/*  95 */       List<L> list = getList(message, offset);
/*  96 */       if (list.isEmpty()) {
/*  97 */         if (list instanceof LazyStringList) {
/*  98 */           list = new LazyStringArrayList(additionalCapacity);
/*  99 */         } else if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList) {
/* 100 */           list = ((Internal.ProtobufList<L>)list).mutableCopyWithCapacity(additionalCapacity);
/*     */         } else {
/* 102 */           list = new ArrayList<>(additionalCapacity);
/*     */         } 
/* 104 */         UnsafeUtil.putObject(message, offset, list);
/* 105 */       } else if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
/* 106 */         ArrayList<L> newList = new ArrayList<>(list.size() + additionalCapacity);
/* 107 */         newList.addAll(list);
/* 108 */         list = newList;
/* 109 */         UnsafeUtil.putObject(message, offset, list);
/* 110 */       } else if (list instanceof UnmodifiableLazyStringList) {
/* 111 */         LazyStringArrayList newList = new LazyStringArrayList(list.size() + additionalCapacity);
/* 112 */         newList.addAll((UnmodifiableLazyStringList)list);
/* 113 */         list = newList;
/* 114 */         UnsafeUtil.putObject(message, offset, list);
/* 115 */       } else if (list instanceof PrimitiveNonBoxingCollection && list instanceof Internal.ProtobufList && 
/*     */         
/* 117 */         !((Internal.ProtobufList)list).isModifiable()) {
/* 118 */         list = ((Internal.ProtobufList<L>)list).mutableCopyWithCapacity(list.size() + additionalCapacity);
/* 119 */         UnsafeUtil.putObject(message, offset, list);
/*     */       } 
/* 121 */       return list;
/*     */     }
/*     */ 
/*     */     
/*     */     <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
/* 126 */       List<E> other = getList(otherMsg, offset);
/* 127 */       List<E> mine = mutableListAt(msg, offset, other.size());
/*     */       
/* 129 */       int size = mine.size();
/* 130 */       int otherSize = other.size();
/* 131 */       if (size > 0 && otherSize > 0) {
/* 132 */         mine.addAll(other);
/*     */       }
/*     */       
/* 135 */       List<E> merged = (size > 0) ? mine : other;
/* 136 */       UnsafeUtil.putObject(msg, offset, merged);
/*     */     }
/*     */ 
/*     */     
/*     */     static <E> List<E> getList(Object message, long offset) {
/* 141 */       return (List<E>)UnsafeUtil.getObject(message, offset);
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class ListFieldSchemaLite
/*     */     extends ListFieldSchema {
/*     */     private ListFieldSchemaLite() {}
/*     */     
/*     */     <L> List<L> mutableListAt(Object message, long offset) {
/* 150 */       Internal.ProtobufList<L> list = getProtobufList(message, offset);
/* 151 */       if (!list.isModifiable()) {
/* 152 */         int size = list.size();
/*     */         
/* 154 */         list = list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
/*     */         
/* 156 */         UnsafeUtil.putObject(message, offset, list);
/*     */       } 
/* 158 */       return list;
/*     */     }
/*     */ 
/*     */     
/*     */     void makeImmutableListAt(Object message, long offset) {
/* 163 */       Internal.ProtobufList<?> list = getProtobufList(message, offset);
/* 164 */       list.makeImmutable();
/*     */     }
/*     */ 
/*     */     
/*     */     <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
/* 169 */       Internal.ProtobufList<E> mine = getProtobufList(msg, offset);
/* 170 */       Internal.ProtobufList<E> other = getProtobufList(otherMsg, offset);
/*     */       
/* 172 */       int size = mine.size();
/* 173 */       int otherSize = other.size();
/* 174 */       if (size > 0 && otherSize > 0) {
/* 175 */         if (!mine.isModifiable()) {
/* 176 */           mine = mine.mutableCopyWithCapacity(size + otherSize);
/*     */         }
/* 178 */         mine.addAll(other);
/*     */       } 
/*     */       
/* 181 */       Internal.ProtobufList<E> merged = (size > 0) ? mine : other;
/* 182 */       UnsafeUtil.putObject(msg, offset, merged);
/*     */     }
/*     */ 
/*     */     
/*     */     static <E> Internal.ProtobufList<E> getProtobufList(Object message, long offset) {
/* 187 */       return (Internal.ProtobufList<E>)UnsafeUtil.getObject(message, offset);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ListFieldSchema.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */