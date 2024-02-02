/*     */ package org.noear.snack.core.exts;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Parameter;
/*     */ import java.util.Collection;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Predicate;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassWrap
/*     */ {
/*  18 */   private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap<>();
/*     */   
/*     */   private final Class<?> _clz;
/*     */   private final Collection<FieldWrap> _fieldAllWraps;
/*     */   
/*     */   public static ClassWrap get(Class<?> clz) {
/*  24 */     ClassWrap cw = cached.get(clz);
/*  25 */     if (cw == null) {
/*  26 */       cw = new ClassWrap(clz);
/*  27 */       ClassWrap l = cached.putIfAbsent(clz, cw);
/*  28 */       if (l != null) {
/*  29 */         cw = l;
/*     */       }
/*     */     } 
/*  32 */     return cw;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean _recordable;
/*     */ 
/*     */   
/*     */   private Constructor _recordConstructor;
/*     */ 
/*     */   
/*     */   private Parameter[] _recordParams;
/*     */ 
/*     */   
/*     */   protected ClassWrap(Class<?> clz) {
/*  47 */     this._clz = clz;
/*  48 */     this._recordable = true;
/*     */     
/*  50 */     Map<String, FieldWrap> map = new LinkedHashMap<>();
/*  51 */     scanAllFields(clz, map::containsKey, map::put);
/*     */     
/*  53 */     this._fieldAllWraps = map.values();
/*     */     
/*  55 */     if (this._fieldAllWraps.size() == 0) {
/*  56 */       this._recordable = false;
/*     */     }
/*     */     
/*  59 */     if (this._recordable) {
/*     */       
/*  61 */       this._recordConstructor = clz.getConstructors()[0];
/*  62 */       this._recordParams = this._recordConstructor.getParameters();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Class<?> clz() {
/*  67 */     return this._clz;
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<FieldWrap> fieldAllWraps() {
/*  72 */     return this._fieldAllWraps;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean recordable() {
/*  78 */     return this._recordable;
/*     */   }
/*     */   
/*     */   public Constructor recordConstructor() {
/*  82 */     return this._recordConstructor;
/*     */   }
/*     */   
/*     */   public Parameter[] recordParams() {
/*  86 */     return this._recordParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void scanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
/*  93 */     if (clz == null) {
/*     */       return;
/*     */     }
/*     */     
/*  97 */     for (Field f : clz.getDeclaredFields()) {
/*  98 */       int mod = f.getModifiers();
/*     */       
/* 100 */       if (!Modifier.isStatic(mod) && 
/* 101 */         !Modifier.isTransient(mod) && 
/* 102 */         !checker.test(f.getName())) {
/* 103 */         this._recordable &= Modifier.isFinal(mod);
/* 104 */         consumer.accept(f.getName(), new FieldWrap(clz, f, Modifier.isFinal(mod)));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 109 */     Class<?> sup = clz.getSuperclass();
/* 110 */     if (sup != Object.class)
/* 111 */       scanAllFields(sup, checker, consumer); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\exts\ClassWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */