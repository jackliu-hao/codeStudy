/*     */ package cn.hutool.core.builder;
/*     */ 
/*     */ import cn.hutool.core.lang.Pair;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Collection;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class EqualsBuilder
/*     */   implements Builder<Boolean>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  53 */   private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY = new ThreadLocal<>();
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
/*     */   static Set<Pair<IDKey, IDKey>> getRegistry() {
/*  65 */     return REGISTRY.get();
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
/*     */   static Pair<IDKey, IDKey> getRegisterPair(Object lhs, Object rhs) {
/*  78 */     IDKey left = new IDKey(lhs);
/*  79 */     IDKey right = new IDKey(rhs);
/*  80 */     return new Pair(left, right);
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static boolean isRegistered(Object lhs, Object rhs) {
/*  97 */     Set<Pair<IDKey, IDKey>> registry = getRegistry();
/*  98 */     Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
/*  99 */     Pair<IDKey, IDKey> swappedPair = new Pair(pair.getKey(), pair.getValue());
/*     */     
/* 101 */     return (registry != null && (registry
/* 102 */       .contains(pair) || registry.contains(swappedPair)));
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
/*     */   static void register(Object lhs, Object rhs) {
/* 115 */     synchronized (EqualsBuilder.class) {
/* 116 */       if (getRegistry() == null) {
/* 117 */         REGISTRY.set(new HashSet<>());
/*     */       }
/*     */     } 
/*     */     
/* 121 */     Set<Pair<IDKey, IDKey>> registry = getRegistry();
/* 122 */     Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
/* 123 */     registry.add(pair);
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
/*     */ 
/*     */ 
/*     */   
/*     */   static void unregister(Object lhs, Object rhs) {
/* 139 */     Set<Pair<IDKey, IDKey>> registry = getRegistry();
/* 140 */     if (registry != null) {
/* 141 */       Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
/* 142 */       registry.remove(pair);
/* 143 */       synchronized (EqualsBuilder.class) {
/*     */         
/* 145 */         registry = getRegistry();
/* 146 */         if (registry != null && registry.isEmpty()) {
/* 147 */           REGISTRY.remove();
/*     */         }
/*     */       } 
/*     */     } 
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
/*     */   
/*     */   private boolean isEquals = true;
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
/*     */   public static boolean reflectionEquals(Object lhs, Object rhs, Collection<String> excludeFields) {
/* 176 */     return reflectionEquals(lhs, rhs, (String[])ArrayUtil.toArray(excludeFields, String.class));
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
/*     */   public static boolean reflectionEquals(Object lhs, Object rhs, String... excludeFields) {
/* 188 */     return reflectionEquals(lhs, rhs, false, null, excludeFields);
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
/*     */   public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients) {
/* 213 */     return reflectionEquals(lhs, rhs, testTransients, null, new String[0]);
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
/*     */   public static boolean reflectionEquals(Object lhs, Object rhs, boolean testTransients, Class<?> reflectUpToClass, String... excludeFields) {
/*     */     Class<?> testClass;
/* 245 */     if (lhs == rhs) {
/* 246 */       return true;
/*     */     }
/* 248 */     if (lhs == null || rhs == null) {
/* 249 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 255 */     Class<?> lhsClass = lhs.getClass();
/* 256 */     Class<?> rhsClass = rhs.getClass();
/*     */     
/* 258 */     if (lhsClass.isInstance(rhs)) {
/* 259 */       testClass = lhsClass;
/* 260 */       if (!rhsClass.isInstance(lhs))
/*     */       {
/* 262 */         testClass = rhsClass;
/*     */       }
/* 264 */     } else if (rhsClass.isInstance(lhs)) {
/* 265 */       testClass = rhsClass;
/* 266 */       if (!lhsClass.isInstance(rhs))
/*     */       {
/* 268 */         testClass = lhsClass;
/*     */       }
/*     */     } else {
/*     */       
/* 272 */       return false;
/*     */     } 
/* 274 */     EqualsBuilder equalsBuilder = new EqualsBuilder();
/*     */     try {
/* 276 */       if (testClass.isArray()) {
/* 277 */         equalsBuilder.append(lhs, rhs);
/*     */       } else {
/* 279 */         reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
/* 280 */         while (testClass.getSuperclass() != null && testClass != reflectUpToClass) {
/* 281 */           testClass = testClass.getSuperclass();
/* 282 */           reflectionAppend(lhs, rhs, testClass, equalsBuilder, testTransients, excludeFields);
/*     */         } 
/*     */       } 
/* 285 */     } catch (IllegalArgumentException e) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 291 */       return false;
/*     */     } 
/* 293 */     return equalsBuilder.isEquals();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void reflectionAppend(Object lhs, Object rhs, Class<?> clazz, EqualsBuilder builder, boolean useTransients, String[] excludeFields) {
/* 315 */     if (isRegistered(lhs, rhs)) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 320 */       register(lhs, rhs);
/* 321 */       Field[] fields = clazz.getDeclaredFields();
/* 322 */       AccessibleObject.setAccessible((AccessibleObject[])fields, true);
/* 323 */       for (int i = 0; i < fields.length && builder.isEquals; i++) {
/* 324 */         Field f = fields[i];
/* 325 */         if (false == ArrayUtil.contains((Object[])excludeFields, f.getName()) && f
/* 326 */           .getName().indexOf('$') == -1 && (useTransients || 
/* 327 */           !Modifier.isTransient(f.getModifiers())) && 
/* 328 */           !Modifier.isStatic(f.getModifiers())) {
/*     */           try {
/* 330 */             builder.append(f.get(lhs), f.get(rhs));
/* 331 */           } catch (IllegalAccessException e) {
/*     */ 
/*     */             
/* 334 */             throw new InternalError("Unexpected IllegalAccessException");
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } finally {
/* 339 */       unregister(lhs, rhs);
/*     */     } 
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
/*     */   public EqualsBuilder appendSuper(boolean superEquals) {
/* 353 */     if (!this.isEquals) {
/* 354 */       return this;
/*     */     }
/* 356 */     this.isEquals = superEquals;
/* 357 */     return this;
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
/*     */   
/*     */   public EqualsBuilder append(Object lhs, Object rhs) {
/* 371 */     if (!this.isEquals) {
/* 372 */       return this;
/*     */     }
/* 374 */     if (lhs == rhs) {
/* 375 */       return this;
/*     */     }
/* 377 */     if (lhs == null || rhs == null) {
/* 378 */       return setEquals(false);
/*     */     }
/* 380 */     if (ArrayUtil.isArray(lhs))
/*     */     {
/* 382 */       return setEquals(ArrayUtil.equals(lhs, rhs));
/*     */     }
/*     */ 
/*     */     
/* 386 */     return setEquals(lhs.equals(rhs));
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
/*     */   public EqualsBuilder append(long lhs, long rhs) {
/* 399 */     if (!this.isEquals) {
/* 400 */       return this;
/*     */     }
/* 402 */     this.isEquals = (lhs == rhs);
/* 403 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EqualsBuilder append(int lhs, int rhs) {
/* 414 */     if (!this.isEquals) {
/* 415 */       return this;
/*     */     }
/* 417 */     this.isEquals = (lhs == rhs);
/* 418 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EqualsBuilder append(short lhs, short rhs) {
/* 429 */     if (!this.isEquals) {
/* 430 */       return this;
/*     */     }
/* 432 */     this.isEquals = (lhs == rhs);
/* 433 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EqualsBuilder append(char lhs, char rhs) {
/* 444 */     if (!this.isEquals) {
/* 445 */       return this;
/*     */     }
/* 447 */     this.isEquals = (lhs == rhs);
/* 448 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EqualsBuilder append(byte lhs, byte rhs) {
/* 459 */     if (!this.isEquals) {
/* 460 */       return this;
/*     */     }
/* 462 */     this.isEquals = (lhs == rhs);
/* 463 */     return this;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EqualsBuilder append(double lhs, double rhs) {
/* 480 */     if (!this.isEquals) {
/* 481 */       return this;
/*     */     }
/* 483 */     return append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EqualsBuilder append(float lhs, float rhs) {
/* 500 */     if (!this.isEquals) {
/* 501 */       return this;
/*     */     }
/* 503 */     return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EqualsBuilder append(boolean lhs, boolean rhs) {
/* 514 */     if (!this.isEquals) {
/* 515 */       return this;
/*     */     }
/* 517 */     this.isEquals = (lhs == rhs);
/* 518 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEquals() {
/* 528 */     return this.isEquals;
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
/*     */   public Boolean build() {
/* 541 */     return Boolean.valueOf(isEquals());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected EqualsBuilder setEquals(boolean isEquals) {
/* 551 */     this.isEquals = isEquals;
/* 552 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {
/* 561 */     this.isEquals = true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\builder\EqualsBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */