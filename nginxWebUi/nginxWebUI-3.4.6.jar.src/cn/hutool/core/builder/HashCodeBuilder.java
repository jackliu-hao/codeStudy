/*     */ package cn.hutool.core.builder;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HashCodeBuilder
/*     */   implements Builder<Integer>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final int DEFAULT_INITIAL_VALUE = 17;
/*     */   private static final int DEFAULT_MULTIPLIER_VALUE = 37;
/* 106 */   private static final ThreadLocal<Set<IDKey>> REGISTRY = new ThreadLocal<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final int iConstant;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int iTotal;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Set<IDKey> getRegistry() {
/* 134 */     return REGISTRY.get();
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
/*     */   private static boolean isRegistered(Object value) {
/* 149 */     Set<IDKey> registry = getRegistry();
/* 150 */     return (registry != null && registry.contains(new IDKey(value)));
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
/*     */   private static void reflectionAppend(Object object, Class<?> clazz, HashCodeBuilder builder, boolean useTransients, String[] excludeFields) {
/* 171 */     if (isRegistered(object)) {
/*     */       return;
/*     */     }
/*     */     try {
/* 175 */       register(object);
/* 176 */       Field[] fields = clazz.getDeclaredFields();
/* 177 */       AccessibleObject.setAccessible((AccessibleObject[])fields, true);
/* 178 */       for (Field field : fields) {
/* 179 */         if (false == ArrayUtil.contains((Object[])excludeFields, field.getName()) && field
/* 180 */           .getName().indexOf('$') == -1 && (useTransients || 
/* 181 */           !Modifier.isTransient(field.getModifiers())) && 
/* 182 */           !Modifier.isStatic(field.getModifiers())) {
/*     */           try {
/* 184 */             Object fieldValue = field.get(object);
/* 185 */             builder.append(fieldValue);
/* 186 */           } catch (IllegalAccessException e) {
/*     */ 
/*     */             
/* 189 */             throw new InternalError("Unexpected IllegalAccessException");
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } finally {
/* 194 */       unregister(object);
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
/*     */   public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object) {
/* 237 */     return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, false, null, new String[0]);
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
/*     */   public static int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, Object object, boolean testTransients) {
/* 282 */     return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, testTransients, null, new String[0]);
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
/*     */   public static <T> int reflectionHashCode(int initialNonZeroOddNumber, int multiplierNonZeroOddNumber, T object, boolean testTransients, Class<? super T> reflectUpToClass, String... excludeFields) {
/* 336 */     if (object == null) {
/* 337 */       throw new IllegalArgumentException("The object to build a hash code for must not be null");
/*     */     }
/* 339 */     HashCodeBuilder builder = new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber);
/* 340 */     Class<?> clazz = object.getClass();
/* 341 */     reflectionAppend(object, clazz, builder, testTransients, excludeFields);
/* 342 */     while (clazz.getSuperclass() != null && clazz != reflectUpToClass) {
/* 343 */       clazz = clazz.getSuperclass();
/* 344 */       reflectionAppend(object, clazz, builder, testTransients, excludeFields);
/*     */     } 
/* 346 */     return builder.toHashCode();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int reflectionHashCode(Object object, boolean testTransients) {
/* 383 */     return reflectionHashCode(17, 37, object, testTransients, null, new String[0]);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int reflectionHashCode(Object object, Collection<String> excludeFields) {
/* 421 */     return reflectionHashCode(object, (String[])ArrayUtil.toArray(excludeFields, String.class));
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int reflectionHashCode(Object object, String... excludeFields) {
/* 460 */     return reflectionHashCode(17, 37, object, false, null, excludeFields);
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
/*     */   static void register(Object value) {
/* 473 */     synchronized (HashCodeBuilder.class) {
/* 474 */       if (getRegistry() == null) {
/* 475 */         REGISTRY.set(new HashSet<>());
/*     */       }
/*     */     } 
/* 478 */     getRegistry().add(new IDKey(value));
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
/*     */   static void unregister(Object value) {
/* 494 */     Set<IDKey> registry = getRegistry();
/* 495 */     if (registry != null) {
/* 496 */       registry.remove(new IDKey(value));
/* 497 */       synchronized (HashCodeBuilder.class) {
/*     */         
/* 499 */         registry = getRegistry();
/* 500 */         if (registry != null && registry.isEmpty()) {
/* 501 */           REGISTRY.remove();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HashCodeBuilder() {
/* 523 */     this.iConstant = 37;
/* 524 */     this.iTotal = 17;
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
/*     */   public HashCodeBuilder(int initialOddNumber, int multiplierOddNumber) {
/* 545 */     Assert.isTrue((initialOddNumber % 2 != 0), "HashCodeBuilder requires an odd initial value", new Object[0]);
/* 546 */     Assert.isTrue((multiplierOddNumber % 2 != 0), "HashCodeBuilder requires an odd multiplier", new Object[0]);
/* 547 */     this.iConstant = multiplierOddNumber;
/* 548 */     this.iTotal = initialOddNumber;
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
/*     */   public HashCodeBuilder append(boolean value) {
/* 573 */     this.iTotal = this.iTotal * this.iConstant + (value ? 0 : 1);
/* 574 */     return this;
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
/*     */   public HashCodeBuilder append(boolean[] array) {
/* 587 */     if (array == null) {
/* 588 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 590 */       for (boolean element : array) {
/* 591 */         append(element);
/*     */       }
/*     */     } 
/* 594 */     return this;
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
/*     */   public HashCodeBuilder append(byte value) {
/* 609 */     this.iTotal = this.iTotal * this.iConstant + value;
/* 610 */     return this;
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
/*     */   public HashCodeBuilder append(byte[] array) {
/* 625 */     if (array == null) {
/* 626 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 628 */       for (byte element : array) {
/* 629 */         append(element);
/*     */       }
/*     */     } 
/* 632 */     return this;
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
/*     */   public HashCodeBuilder append(char value) {
/* 645 */     this.iTotal = this.iTotal * this.iConstant + value;
/* 646 */     return this;
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
/*     */   public HashCodeBuilder append(char[] array) {
/* 659 */     if (array == null) {
/* 660 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 662 */       for (char element : array) {
/* 663 */         append(element);
/*     */       }
/*     */     } 
/* 666 */     return this;
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
/*     */   public HashCodeBuilder append(double value) {
/* 679 */     return append(Double.doubleToLongBits(value));
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
/*     */   public HashCodeBuilder append(double[] array) {
/* 692 */     if (array == null) {
/* 693 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 695 */       for (double element : array) {
/* 696 */         append(element);
/*     */       }
/*     */     } 
/* 699 */     return this;
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
/*     */   public HashCodeBuilder append(float value) {
/* 712 */     this.iTotal = this.iTotal * this.iConstant + Float.floatToIntBits(value);
/* 713 */     return this;
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
/*     */   public HashCodeBuilder append(float[] array) {
/* 726 */     if (array == null) {
/* 727 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 729 */       for (float element : array) {
/* 730 */         append(element);
/*     */       }
/*     */     } 
/* 733 */     return this;
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
/*     */   public HashCodeBuilder append(int value) {
/* 746 */     this.iTotal = this.iTotal * this.iConstant + value;
/* 747 */     return this;
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
/*     */   public HashCodeBuilder append(int[] array) {
/* 760 */     if (array == null) {
/* 761 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 763 */       for (int element : array) {
/* 764 */         append(element);
/*     */       }
/*     */     } 
/* 767 */     return this;
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
/*     */   public HashCodeBuilder append(long value) {
/* 784 */     this.iTotal = this.iTotal * this.iConstant + (int)(value ^ value >> 32L);
/* 785 */     return this;
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
/*     */   public HashCodeBuilder append(long[] array) {
/* 798 */     if (array == null) {
/* 799 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 801 */       for (long element : array) {
/* 802 */         append(element);
/*     */       }
/*     */     } 
/* 805 */     return this;
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
/*     */   public HashCodeBuilder append(Object object) {
/* 818 */     if (object == null) {
/* 819 */       this.iTotal *= this.iConstant;
/*     */     
/*     */     }
/* 822 */     else if (object.getClass().isArray()) {
/*     */ 
/*     */       
/* 825 */       if (object instanceof long[]) {
/* 826 */         append((long[])object);
/* 827 */       } else if (object instanceof int[]) {
/* 828 */         append((int[])object);
/* 829 */       } else if (object instanceof short[]) {
/* 830 */         append((short[])object);
/* 831 */       } else if (object instanceof char[]) {
/* 832 */         append((char[])object);
/* 833 */       } else if (object instanceof byte[]) {
/* 834 */         append((byte[])object);
/* 835 */       } else if (object instanceof double[]) {
/* 836 */         append((double[])object);
/* 837 */       } else if (object instanceof float[]) {
/* 838 */         append((float[])object);
/* 839 */       } else if (object instanceof boolean[]) {
/* 840 */         append((boolean[])object);
/*     */       } else {
/*     */         
/* 843 */         append((Object[])object);
/*     */       } 
/*     */     } else {
/* 846 */       this.iTotal = this.iTotal * this.iConstant + object.hashCode();
/*     */     } 
/*     */     
/* 849 */     return this;
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
/*     */   public HashCodeBuilder append(Object[] array) {
/* 862 */     if (array == null) {
/* 863 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 865 */       for (Object element : array) {
/* 866 */         append(element);
/*     */       }
/*     */     } 
/* 869 */     return this;
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
/*     */   public HashCodeBuilder append(short value) {
/* 882 */     this.iTotal = this.iTotal * this.iConstant + value;
/* 883 */     return this;
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
/*     */   public HashCodeBuilder append(short[] array) {
/* 896 */     if (array == null) {
/* 897 */       this.iTotal *= this.iConstant;
/*     */     } else {
/* 899 */       for (short element : array) {
/* 900 */         append(element);
/*     */       }
/*     */     } 
/* 903 */     return this;
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
/*     */   public HashCodeBuilder appendSuper(int superHashCode) {
/* 917 */     this.iTotal = this.iTotal * this.iConstant + superHashCode;
/* 918 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int toHashCode() {
/* 929 */     return this.iTotal;
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
/*     */   public Integer build() {
/* 941 */     return Integer.valueOf(toHashCode());
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
/*     */   public int hashCode() {
/* 955 */     return toHashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\builder\HashCodeBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */