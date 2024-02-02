/*     */ package org.h2.mvstore.tx;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.MVMap;
/*     */ import org.h2.mvstore.WriteBuffer;
/*     */ import org.h2.mvstore.type.BasicDataType;
/*     */ import org.h2.value.VersionedValue;
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
/*     */ final class Record<K, V>
/*     */ {
/*  25 */   static final Record<?, ?> COMMIT_MARKER = new Record(-1, null, null);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final int mapId;
/*     */ 
/*     */ 
/*     */   
/*     */   final K key;
/*     */ 
/*     */ 
/*     */   
/*     */   final VersionedValue<V> oldValue;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Record(int paramInt, K paramK, VersionedValue<V> paramVersionedValue) {
/*  44 */     this.mapId = paramInt;
/*  45 */     this.key = paramK;
/*  46 */     this.oldValue = paramVersionedValue;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  51 */     return "mapId=" + this.mapId + ", key=" + this.key + ", value=" + this.oldValue;
/*     */   }
/*     */ 
/*     */   
/*     */   static final class Type<K, V>
/*     */     extends BasicDataType<Record<K, V>>
/*     */   {
/*     */     private final TransactionStore transactionStore;
/*     */     
/*     */     Type(TransactionStore param1TransactionStore) {
/*  61 */       this.transactionStore = param1TransactionStore;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getMemory(Record<K, V> param1Record) {
/*  66 */       int i = 52;
/*  67 */       if (param1Record.mapId >= 0) {
/*  68 */         MVMap<?, VersionedValue<?>> mVMap = this.transactionStore.getMap(param1Record.mapId);
/*  69 */         i += mVMap.getKeyType().getMemory(param1Record.key) + mVMap
/*  70 */           .getValueType().getMemory(param1Record.oldValue);
/*     */       } 
/*  72 */       return i;
/*     */     }
/*     */ 
/*     */     
/*     */     public int compare(Record<K, V> param1Record1, Record<K, V> param1Record2) {
/*  77 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(WriteBuffer param1WriteBuffer, Record<K, V> param1Record) {
/*  82 */       param1WriteBuffer.putVarInt(param1Record.mapId);
/*  83 */       if (param1Record.mapId >= 0) {
/*  84 */         MVMap<?, VersionedValue<?>> mVMap = this.transactionStore.getMap(param1Record.mapId);
/*  85 */         mVMap.getKeyType().write(param1WriteBuffer, param1Record.key);
/*  86 */         VersionedValue<V> versionedValue = param1Record.oldValue;
/*  87 */         if (versionedValue == null) {
/*  88 */           param1WriteBuffer.put((byte)0);
/*     */         } else {
/*  90 */           param1WriteBuffer.put((byte)1);
/*  91 */           mVMap.getValueType().write(param1WriteBuffer, versionedValue);
/*     */         } 
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Record<K, V> read(ByteBuffer param1ByteBuffer) {
/*  99 */       int i = DataUtils.readVarInt(param1ByteBuffer);
/* 100 */       if (i < 0) {
/* 101 */         return (Record)Record.COMMIT_MARKER;
/*     */       }
/* 103 */       MVMap<?, VersionedValue<?>> mVMap = this.transactionStore.getMap(i);
/* 104 */       Object object = mVMap.getKeyType().read(param1ByteBuffer);
/* 105 */       VersionedValue<V> versionedValue = null;
/* 106 */       if (param1ByteBuffer.get() == 1) {
/* 107 */         versionedValue = (VersionedValue)mVMap.getValueType().read(param1ByteBuffer);
/*     */       }
/* 109 */       return new Record<>(i, (K)object, versionedValue);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Record<K, V>[] createStorage(int param1Int) {
/* 115 */       return (Record<K, V>[])new Record[param1Int];
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\Record.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */