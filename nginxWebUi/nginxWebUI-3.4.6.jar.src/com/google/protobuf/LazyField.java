/*     */ package com.google.protobuf;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
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
/*     */ 
/*     */ 
/*     */ public class LazyField
/*     */   extends LazyFieldLite
/*     */ {
/*     */   private final MessageLite defaultInstance;
/*     */   
/*     */   public LazyField(MessageLite defaultInstance, ExtensionRegistryLite extensionRegistry, ByteString bytes) {
/*  56 */     super(extensionRegistry, bytes);
/*     */     
/*  58 */     this.defaultInstance = defaultInstance;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean containsDefaultInstance() {
/*  63 */     return (super.containsDefaultInstance() || this.value == this.defaultInstance);
/*     */   }
/*     */   
/*     */   public MessageLite getValue() {
/*  67 */     return getValue(this.defaultInstance);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  72 */     return getValue().hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  77 */     return getValue().equals(obj);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  82 */     return getValue().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static class LazyEntry<K>
/*     */     implements Map.Entry<K, Object>
/*     */   {
/*     */     private Map.Entry<K, LazyField> entry;
/*     */ 
/*     */ 
/*     */     
/*     */     private LazyEntry(Map.Entry<K, LazyField> entry) {
/*  95 */       this.entry = entry;
/*     */     }
/*     */ 
/*     */     
/*     */     public K getKey() {
/* 100 */       return this.entry.getKey();
/*     */     }
/*     */ 
/*     */     
/*     */     public Object getValue() {
/* 105 */       LazyField field = this.entry.getValue();
/* 106 */       if (field == null) {
/* 107 */         return null;
/*     */       }
/* 109 */       return field.getValue();
/*     */     }
/*     */     
/*     */     public LazyField getField() {
/* 113 */       return this.entry.getValue();
/*     */     }
/*     */ 
/*     */     
/*     */     public Object setValue(Object value) {
/* 118 */       if (!(value instanceof MessageLite)) {
/* 119 */         throw new IllegalArgumentException("LazyField now only used for MessageSet, and the value of MessageSet must be an instance of MessageLite");
/*     */       }
/*     */ 
/*     */       
/* 123 */       return ((LazyField)this.entry.getValue()).setValue((MessageLite)value);
/*     */     }
/*     */   }
/*     */   
/*     */   static class LazyIterator<K> implements Iterator<Map.Entry<K, Object>> {
/*     */     private Iterator<Map.Entry<K, Object>> iterator;
/*     */     
/*     */     public LazyIterator(Iterator<Map.Entry<K, Object>> iterator) {
/* 131 */       this.iterator = iterator;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean hasNext() {
/* 136 */       return this.iterator.hasNext();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Map.Entry<K, Object> next() {
/* 142 */       Map.Entry<K, ?> entry = this.iterator.next();
/* 143 */       if (entry.getValue() instanceof LazyField) {
/* 144 */         return new LazyField.LazyEntry<>(entry);
/*     */       }
/* 146 */       return (Map.Entry)entry;
/*     */     }
/*     */ 
/*     */     
/*     */     public void remove() {
/* 151 */       this.iterator.remove();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\LazyField.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */