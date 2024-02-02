/*     */ package org.noear.solon.core.wrap;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.lang.reflect.Parameter;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.function.BiConsumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ import org.noear.solon.core.handle.UploadedFile;
/*     */ import org.noear.solon.core.util.ConvertUtil;
/*     */ 
/*     */ public class ClassWrap {
/*  21 */   private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap<>();
/*     */   private final Class<?> _clz;
/*     */   private final Method[] methods;
/*     */   private final List<FieldWrap> fieldWraps;
/*     */   
/*     */   public static ClassWrap get(Class<?> clz) {
/*  27 */     ClassWrap cw = cached.get(clz);
/*  28 */     if (cw == null) {
/*  29 */       synchronized (clz) {
/*  30 */         cw = cached.get(clz);
/*  31 */         if (cw == null) {
/*  32 */           cw = new ClassWrap(clz);
/*  33 */           cached.put(clz, cw);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*  38 */     return cw;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final Map<String, FieldWrap> fieldAllWrapsMap;
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
/*  56 */     this._clz = clz;
/*  57 */     this._recordable = true;
/*     */ 
/*     */     
/*  60 */     this.methods = clz.getDeclaredMethods();
/*     */ 
/*     */ 
/*     */     
/*  64 */     this.fieldWraps = new ArrayList<>();
/*  65 */     this.fieldAllWrapsMap = new LinkedHashMap<>();
/*     */ 
/*     */     
/*  68 */     doScanAllFields(clz, this.fieldAllWrapsMap::containsKey, this.fieldAllWrapsMap::put);
/*     */ 
/*     */     
/*  71 */     for (Field f : clz.getDeclaredFields()) {
/*  72 */       FieldWrap fw = this.fieldAllWrapsMap.get(f.getName());
/*  73 */       if (fw != null) {
/*  74 */         this.fieldWraps.add(fw);
/*     */       }
/*     */     } 
/*     */     
/*  78 */     if (this.fieldWraps.size() == 0) {
/*  79 */       this._recordable = false;
/*     */     }
/*     */     
/*  82 */     if (this._recordable) {
/*     */       
/*  84 */       this._recordConstructor = clz.getConstructors()[0];
/*  85 */       this._recordParams = this._recordConstructor.getParameters();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Class<?> clz() {
/*  90 */     return this._clz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, FieldWrap> getFieldAllWraps() {
/*  97 */     return Collections.unmodifiableMap(this.fieldAllWrapsMap);
/*     */   }
/*     */   
/*     */   public FieldWrap getFieldWrap(String field) {
/* 101 */     return this.fieldAllWrapsMap.get(field);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method[] getMethods() {
/* 108 */     return this.methods;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean recordable() {
/* 116 */     return this._recordable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Constructor recordConstructor() {
/* 123 */     return this._recordConstructor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Parameter[] recordParams() {
/* 130 */     return this._recordParams;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T newBy(Properties data) {
/*     */     try {
/* 140 */       Constructor<?> constructor = clz().getConstructor(new Class[] { Properties.class });
/* 141 */       if (constructor != null) {
/* 142 */         return (T)constructor.newInstance(new Object[] { data });
/*     */       }
/* 144 */     } catch (Throwable throwable) {}
/*     */ 
/*     */     
/* 147 */     return newBy(data::getProperty);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T newBy(Function<String, String> data) {
/* 152 */     return newBy(data, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T newBy(Function<String, String> data, Context ctx) {
/*     */     try {
/* 164 */       if (recordable()) {
/*     */         
/* 166 */         Parameter[] argsP = recordParams();
/* 167 */         Object[] argsV = new Object[argsP.length];
/*     */         
/* 169 */         for (int i = 0; i < argsP.length; i++) {
/* 170 */           Parameter p = argsP[i];
/* 171 */           String key = p.getName();
/* 172 */           String val0 = data.apply(key);
/*     */           
/* 174 */           if (val0 != null) {
/*     */             
/* 176 */             Object val = ConvertUtil.to(p, p.getType(), key, val0, ctx);
/* 177 */             argsV[i] = val;
/*     */           }
/* 179 */           else if (p.getType() == UploadedFile.class) {
/* 180 */             argsV[i] = ctx.file(key);
/*     */           } else {
/* 182 */             argsV[i] = null;
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 187 */         Object object = recordConstructor().newInstance(argsV);
/* 188 */         return (T)object;
/*     */       } 
/* 190 */       Object obj = clz().newInstance();
/*     */       
/* 192 */       doFill(obj, data, ctx);
/*     */       
/* 194 */       return (T)obj;
/*     */     }
/* 196 */     catch (RuntimeException ex) {
/* 197 */       throw ex;
/* 198 */     } catch (Throwable ex) {
/* 199 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void fill(Object bean, Function<String, String> data) {
/*     */     try {
/* 210 */       doFill(bean, data, null);
/* 211 */     } catch (RuntimeException ex) {
/* 212 */       throw ex;
/* 213 */     } catch (Throwable ex) {
/* 214 */       throw new RuntimeException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doFill(Object bean, Function<String, String> data, Context ctx) throws Exception {
/* 225 */     for (Map.Entry<String, FieldWrap> kv : this.fieldAllWrapsMap.entrySet()) {
/* 226 */       String key = kv.getKey();
/* 227 */       String val0 = data.apply(key);
/*     */       
/* 229 */       FieldWrap fw = kv.getValue();
/*     */       
/* 231 */       if (val0 != null) {
/*     */         
/* 233 */         Object val = ConvertUtil.to(fw.field, fw.type, key, val0, ctx);
/* 234 */         fw.setValue(bean, val); continue;
/*     */       } 
/* 236 */       if (ctx != null && fw.type == UploadedFile.class) {
/* 237 */         UploadedFile file1 = ctx.file(key);
/* 238 */         if (file1 != null) {
/* 239 */           fw.setValue(bean, file1);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doScanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
/* 250 */     if (clz == null) {
/*     */       return;
/*     */     }
/*     */     
/* 254 */     for (Field f : clz.getDeclaredFields()) {
/* 255 */       int mod = f.getModifiers();
/*     */       
/* 257 */       if (!Modifier.isStatic(mod) && 
/* 258 */         !Modifier.isTransient(mod))
/*     */       {
/* 260 */         if (!checker.test(f.getName())) {
/* 261 */           this._recordable &= Modifier.isFinal(mod);
/*     */           
/* 263 */           consumer.accept(f.getName(), new FieldWrap(this._clz, f, Modifier.isFinal(mod)));
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 268 */     Class<?> sup = clz.getSuperclass();
/* 269 */     if (sup != Object.class)
/* 270 */       doScanAllFields(sup, checker, consumer); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\wrap\ClassWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */