/*     */ package com.fasterxml.jackson.annotation;
/*     */ 
/*     */ import java.util.UUID;
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
/*     */ public class ObjectIdGenerators
/*     */ {
/*     */   private static abstract class Base<T>
/*     */     extends ObjectIdGenerator<T>
/*     */   {
/*     */     protected final Class<?> _scope;
/*     */     
/*     */     protected Base(Class<?> scope) {
/*  32 */       this._scope = scope;
/*     */     }
/*     */ 
/*     */     
/*     */     public final Class<?> getScope() {
/*  37 */       return this._scope;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean canUseFor(ObjectIdGenerator<?> gen) {
/*  42 */       return (gen.getClass() == getClass() && gen.getScope() == this._scope);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public abstract T generateId(Object param1Object);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class None
/*     */     extends ObjectIdGenerator<Object> {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static abstract class PropertyGenerator
/*     */     extends Base<Object>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected PropertyGenerator(Class<?> scope) {
/*  75 */       super(scope);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class IntSequenceGenerator
/*     */     extends Base<Integer>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     
/*     */     protected transient int _nextValue;
/*     */     
/*     */     public IntSequenceGenerator() {
/*  88 */       this(Object.class, -1);
/*     */     } public IntSequenceGenerator(Class<?> scope, int fv) {
/*  90 */       super(scope);
/*  91 */       this._nextValue = fv;
/*     */     }
/*     */     protected int initialValue() {
/*  94 */       return 1;
/*     */     }
/*     */     
/*     */     public ObjectIdGenerator<Integer> forScope(Class<?> scope) {
/*  98 */       return (this._scope == scope) ? this : new IntSequenceGenerator(scope, this._nextValue);
/*     */     }
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator<Integer> newForSerialization(Object context) {
/* 103 */       return new IntSequenceGenerator(this._scope, initialValue());
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator.IdKey key(Object key) {
/* 109 */       if (key == null) {
/* 110 */         return null;
/*     */       }
/* 112 */       return new ObjectIdGenerator.IdKey(getClass(), this._scope, key);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Integer generateId(Object forPojo) {
/* 118 */       if (forPojo == null) {
/* 119 */         return null;
/*     */       }
/* 121 */       int id = this._nextValue;
/* 122 */       this._nextValue++;
/* 123 */       return Integer.valueOf(id);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class UUIDGenerator
/*     */     extends Base<UUID>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public UUIDGenerator() {
/* 140 */       this(Object.class);
/*     */     } private UUIDGenerator(Class<?> scope) {
/* 142 */       super(Object.class);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator<UUID> forScope(Class<?> scope) {
/* 150 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator<UUID> newForSerialization(Object context) {
/* 158 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public UUID generateId(Object forPojo) {
/* 163 */       return UUID.randomUUID();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator.IdKey key(Object key) {
/* 169 */       if (key == null) {
/* 170 */         return null;
/*     */       }
/* 172 */       return new ObjectIdGenerator.IdKey(getClass(), null, key);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean canUseFor(ObjectIdGenerator<?> gen) {
/* 180 */       return (gen.getClass() == getClass());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final class StringIdGenerator
/*     */     extends Base<String>
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public StringIdGenerator() {
/* 201 */       this(Object.class);
/*     */     } private StringIdGenerator(Class<?> scope) {
/* 203 */       super(Object.class);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator<String> forScope(Class<?> scope) {
/* 209 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator<String> newForSerialization(Object context) {
/* 215 */       return this;
/*     */     }
/*     */ 
/*     */     
/*     */     public String generateId(Object forPojo) {
/* 220 */       return UUID.randomUUID().toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public ObjectIdGenerator.IdKey key(Object key) {
/* 225 */       if (key == null) {
/* 226 */         return null;
/*     */       }
/* 228 */       return new ObjectIdGenerator.IdKey(getClass(), null, key);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean canUseFor(ObjectIdGenerator<?> gen) {
/* 234 */       return gen instanceof StringIdGenerator;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\ObjectIdGenerators.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */