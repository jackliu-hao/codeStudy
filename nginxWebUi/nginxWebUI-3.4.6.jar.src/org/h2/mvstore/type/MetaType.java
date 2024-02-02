/*     */ package org.h2.mvstore.type;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.mvstore.WriteBuffer;
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
/*     */ public final class MetaType<D>
/*     */   extends BasicDataType<DataType<?>>
/*     */ {
/*     */   private final D database;
/*     */   private final Thread.UncaughtExceptionHandler exceptionHandler;
/*  27 */   private final Map<String, Object> cache = new HashMap<>();
/*     */   
/*     */   public MetaType(D paramD, Thread.UncaughtExceptionHandler paramUncaughtExceptionHandler) {
/*  30 */     this.database = paramD;
/*  31 */     this.exceptionHandler = paramUncaughtExceptionHandler;
/*     */   }
/*     */ 
/*     */   
/*     */   public int compare(DataType<?> paramDataType1, DataType<?> paramDataType2) {
/*  36 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory(DataType<?> paramDataType) {
/*  41 */     return 24;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void write(WriteBuffer paramWriteBuffer, DataType<?> paramDataType) {
/*  47 */     Class<?> clazz = paramDataType.getClass();
/*  48 */     StatefulDataType statefulDataType = null;
/*  49 */     if (paramDataType instanceof StatefulDataType) {
/*  50 */       statefulDataType = (StatefulDataType)paramDataType;
/*  51 */       StatefulDataType.Factory factory = statefulDataType.getFactory();
/*  52 */       if (factory != null) {
/*  53 */         clazz = factory.getClass();
/*     */       }
/*     */     } 
/*  56 */     String str = clazz.getName();
/*  57 */     int i = str.length();
/*  58 */     paramWriteBuffer.putVarInt(i)
/*  59 */       .putStringData(str, i);
/*  60 */     if (statefulDataType != null) {
/*  61 */       statefulDataType.save(paramWriteBuffer, this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DataType<?> read(ByteBuffer paramByteBuffer) {
/*  68 */     int i = DataUtils.readVarInt(paramByteBuffer);
/*  69 */     String str = DataUtils.readString(paramByteBuffer, i); try {
/*     */       StatefulDataType.Factory factory;
/*  71 */       Object object = this.cache.get(str);
/*  72 */       if (object != null) {
/*  73 */         if (object instanceof StatefulDataType.Factory) {
/*  74 */           return ((StatefulDataType.Factory<D>)object).create(paramByteBuffer, this, this.database);
/*     */         }
/*  76 */         return (DataType)object;
/*     */       } 
/*  78 */       Class<?> clazz = Class.forName(str);
/*  79 */       boolean bool = false;
/*     */       
/*     */       try {
/*  82 */         factory = (StatefulDataType.Factory)clazz.getDeclaredField("INSTANCE").get(null);
/*  83 */         bool = true;
/*  84 */       } catch (ReflectiveOperationException|NullPointerException reflectiveOperationException) {
/*  85 */         factory = (StatefulDataType.Factory)clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*     */       } 
/*  87 */       if (factory instanceof StatefulDataType.Factory) {
/*  88 */         StatefulDataType.Factory<D> factory1 = factory;
/*  89 */         this.cache.put(str, factory1);
/*  90 */         return factory1.create(paramByteBuffer, this, this.database);
/*     */       } 
/*  92 */       if (bool) {
/*  93 */         this.cache.put(str, factory);
/*     */       }
/*  95 */       return (DataType)factory;
/*  96 */     } catch (ReflectiveOperationException|SecurityException|IllegalArgumentException reflectiveOperationException) {
/*  97 */       if (this.exceptionHandler != null) {
/*  98 */         this.exceptionHandler.uncaughtException(Thread.currentThread(), reflectiveOperationException);
/*     */       }
/* 100 */       throw new RuntimeException(reflectiveOperationException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public DataType<?>[] createStorage(int paramInt) {
/* 106 */     return (DataType<?>[])new DataType[paramInt];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\type\MetaType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */