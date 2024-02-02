/*     */ package org.h2.mvstore.tx;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.WriteBuffer;
/*     */ import org.h2.mvstore.type.BasicDataType;
/*     */ import org.h2.mvstore.type.DataType;
/*     */ import org.h2.mvstore.type.MetaType;
/*     */ import org.h2.mvstore.type.StatefulDataType;
/*     */ import org.h2.value.VersionedValue;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class VersionedValueType<T, D>
/*     */   extends BasicDataType<VersionedValue<T>>
/*     */   implements StatefulDataType<D>
/*     */ {
/*     */   private final DataType<T> valueType;
/*  24 */   private final Factory<D> factory = new Factory<>();
/*     */ 
/*     */   
/*     */   public VersionedValueType(DataType<T> paramDataType) {
/*  28 */     this.valueType = paramDataType;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public VersionedValue<T>[] createStorage(int paramInt) {
/*  34 */     return (VersionedValue<T>[])new VersionedValue[paramInt];
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory(VersionedValue<T> paramVersionedValue) {
/*  39 */     if (paramVersionedValue == null) return 0;
/*     */     
/*  41 */     int i = 48 + getValMemory((T)paramVersionedValue.getCurrentValue());
/*  42 */     if (paramVersionedValue.getOperationId() != 0L) {
/*  43 */       i += getValMemory((T)paramVersionedValue.getCommittedValue());
/*     */     }
/*  45 */     return i;
/*     */   }
/*     */   
/*     */   private int getValMemory(T paramT) {
/*  49 */     return (paramT == null) ? 0 : this.valueType.getMemory(paramT);
/*     */   }
/*     */ 
/*     */   
/*     */   public void read(ByteBuffer paramByteBuffer, Object paramObject, int paramInt) {
/*  54 */     if (paramByteBuffer.get() == 0) {
/*     */       
/*  56 */       for (byte b = 0; b < paramInt; b++) {
/*  57 */         ((VersionedValue[])cast(paramObject))[b] = VersionedValueCommitted.getInstance(this.valueType.read(paramByteBuffer));
/*     */       }
/*     */     } else {
/*     */       
/*  61 */       for (byte b = 0; b < paramInt; b++) {
/*  62 */         ((VersionedValue[])cast(paramObject))[b] = read(paramByteBuffer);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public VersionedValue<T> read(ByteBuffer paramByteBuffer) {
/*  69 */     long l = DataUtils.readVarLong(paramByteBuffer);
/*  70 */     if (l == 0L) {
/*  71 */       return VersionedValueCommitted.getInstance((T)this.valueType.read(paramByteBuffer));
/*     */     }
/*  73 */     byte b = paramByteBuffer.get();
/*  74 */     Object object1 = ((b & 0x1) != 0) ? this.valueType.read(paramByteBuffer) : null;
/*  75 */     Object object2 = ((b & 0x2) != 0) ? this.valueType.read(paramByteBuffer) : null;
/*  76 */     return VersionedValueUncommitted.getInstance(l, (T)object1, (T)object2);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(WriteBuffer paramWriteBuffer, Object paramObject, int paramInt) {
/*  82 */     boolean bool = true; byte b;
/*  83 */     for (b = 0; b < paramInt; b++) {
/*  84 */       VersionedValue versionedValue = ((VersionedValue[])cast(paramObject))[b];
/*  85 */       if (versionedValue.getOperationId() != 0L || versionedValue.getCurrentValue() == null) {
/*  86 */         bool = false;
/*     */       }
/*     */     } 
/*  89 */     if (bool) {
/*  90 */       paramWriteBuffer.put((byte)0);
/*  91 */       for (b = 0; b < paramInt; b++) {
/*  92 */         VersionedValue versionedValue = ((VersionedValue[])cast(paramObject))[b];
/*  93 */         this.valueType.write(paramWriteBuffer, versionedValue.getCurrentValue());
/*     */       }
/*     */     
/*     */     } else {
/*     */       
/*  98 */       paramWriteBuffer.put((byte)1);
/*  99 */       for (b = 0; b < paramInt; b++) {
/* 100 */         write(paramWriteBuffer, ((VersionedValue[])cast(paramObject))[b]);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void write(WriteBuffer paramWriteBuffer, VersionedValue<T> paramVersionedValue) {
/* 107 */     long l = paramVersionedValue.getOperationId();
/* 108 */     paramWriteBuffer.putVarLong(l);
/* 109 */     if (l == 0L) {
/* 110 */       this.valueType.write(paramWriteBuffer, paramVersionedValue.getCurrentValue());
/*     */     } else {
/* 112 */       Object object = paramVersionedValue.getCommittedValue();
/* 113 */       int i = ((paramVersionedValue.getCurrentValue() == null) ? 0 : 1) | ((object == null) ? 0 : 2);
/* 114 */       paramWriteBuffer.put((byte)i);
/* 115 */       if (paramVersionedValue.getCurrentValue() != null) {
/* 116 */         this.valueType.write(paramWriteBuffer, paramVersionedValue.getCurrentValue());
/*     */       }
/* 118 */       if (object != null) {
/* 119 */         this.valueType.write(paramWriteBuffer, object);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 127 */     if (paramObject == this)
/* 128 */       return true; 
/* 129 */     if (!(paramObject instanceof VersionedValueType)) {
/* 130 */       return false;
/*     */     }
/* 132 */     VersionedValueType versionedValueType = (VersionedValueType)paramObject;
/* 133 */     return this.valueType.equals(versionedValueType.valueType);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 138 */     return super.hashCode() ^ this.valueType.hashCode();
/*     */   }
/*     */ 
/*     */   
/*     */   public void save(WriteBuffer paramWriteBuffer, MetaType<D> paramMetaType) {
/* 143 */     paramMetaType.write(paramWriteBuffer, this.valueType);
/*     */   }
/*     */ 
/*     */   
/*     */   public int compare(VersionedValue<T> paramVersionedValue1, VersionedValue<T> paramVersionedValue2) {
/* 148 */     return this.valueType.compare(paramVersionedValue1.getCurrentValue(), paramVersionedValue2.getCurrentValue());
/*     */   }
/*     */ 
/*     */   
/*     */   public Factory<D> getFactory() {
/* 153 */     return this.factory;
/*     */   }
/*     */   
/*     */   public static final class Factory<D>
/*     */     implements StatefulDataType.Factory<D>
/*     */   {
/*     */     public DataType<?> create(ByteBuffer param1ByteBuffer, MetaType<D> param1MetaType, D param1D) {
/* 160 */       DataType<?> dataType = param1MetaType.read(param1ByteBuffer);
/* 161 */       return (DataType<?>)new VersionedValueType<>(dataType);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\tx\VersionedValueType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */