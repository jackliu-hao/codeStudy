/*     */ package cn.hutool.core.builder;
/*     */ 
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import java.lang.reflect.AccessibleObject;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Modifier;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompareToBuilder
/*     */   implements Builder<Integer>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  63 */   private int comparison = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int reflectionCompare(Object lhs, Object rhs) {
/*  89 */     return reflectionCompare(lhs, rhs, false, null, new String[0]);
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
/*     */   public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients) {
/* 121 */     return reflectionCompare(lhs, rhs, compareTransients, null, new String[0]);
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
/*     */   public static int reflectionCompare(Object lhs, Object rhs, Collection<String> excludeFields) {
/* 154 */     return reflectionCompare(lhs, rhs, (String[])ArrayUtil.toArray(excludeFields, String.class));
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
/*     */   public static int reflectionCompare(Object lhs, Object rhs, String... excludeFields) {
/* 187 */     return reflectionCompare(lhs, rhs, false, null, excludeFields);
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
/*     */   public static int reflectionCompare(Object lhs, Object rhs, boolean compareTransients, Class<?> reflectUpToClass, String... excludeFields) {
/* 229 */     if (lhs == rhs) {
/* 230 */       return 0;
/*     */     }
/* 232 */     if (lhs == null || rhs == null) {
/* 233 */       throw new NullPointerException();
/*     */     }
/* 235 */     Class<?> lhsClazz = lhs.getClass();
/* 236 */     if (!lhsClazz.isInstance(rhs)) {
/* 237 */       throw new ClassCastException();
/*     */     }
/* 239 */     CompareToBuilder compareToBuilder = new CompareToBuilder();
/* 240 */     reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients, excludeFields);
/* 241 */     while (lhsClazz.getSuperclass() != null && lhsClazz != reflectUpToClass) {
/* 242 */       lhsClazz = lhsClazz.getSuperclass();
/* 243 */       reflectionAppend(lhs, rhs, lhsClazz, compareToBuilder, compareTransients, excludeFields);
/*     */     } 
/* 245 */     return compareToBuilder.toComparison();
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
/*     */   private static void reflectionAppend(Object lhs, Object rhs, Class<?> clazz, CompareToBuilder builder, boolean useTransients, String[] excludeFields) {
/* 267 */     Field[] fields = clazz.getDeclaredFields();
/* 268 */     AccessibleObject.setAccessible((AccessibleObject[])fields, true);
/* 269 */     for (int i = 0; i < fields.length && builder.comparison == 0; i++) {
/* 270 */       Field f = fields[i];
/* 271 */       if (false == ArrayUtil.contains((Object[])excludeFields, f.getName()) && f
/* 272 */         .getName().indexOf('$') == -1 && (useTransients || 
/* 273 */         !Modifier.isTransient(f.getModifiers())) && 
/* 274 */         !Modifier.isStatic(f.getModifiers())) {
/*     */         try {
/* 276 */           builder.append(f.get(lhs), f.get(rhs));
/* 277 */         } catch (IllegalAccessException e) {
/*     */ 
/*     */           
/* 280 */           throw new InternalError("Unexpected IllegalAccessException");
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
/*     */   public CompareToBuilder appendSuper(int superCompareTo) {
/* 296 */     if (this.comparison != 0) {
/* 297 */       return this;
/*     */     }
/* 299 */     this.comparison = superCompareTo;
/* 300 */     return this;
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
/*     */   public CompareToBuilder append(Object lhs, Object rhs) {
/* 324 */     return append(lhs, rhs, (Comparator<?>)null);
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
/*     */   public CompareToBuilder append(Object lhs, Object rhs, Comparator<?> comparator) {
/* 353 */     if (this.comparison != 0) {
/* 354 */       return this;
/*     */     }
/* 356 */     if (lhs == rhs) {
/* 357 */       return this;
/*     */     }
/* 359 */     if (lhs == null) {
/* 360 */       this.comparison = -1;
/* 361 */       return this;
/*     */     } 
/* 363 */     if (rhs == null) {
/* 364 */       this.comparison = 1;
/* 365 */       return this;
/*     */     } 
/* 367 */     if (lhs.getClass().isArray()) {
/*     */ 
/*     */ 
/*     */       
/* 371 */       if (lhs instanceof long[]) {
/* 372 */         append((long[])lhs, (long[])rhs);
/* 373 */       } else if (lhs instanceof int[]) {
/* 374 */         append((int[])lhs, (int[])rhs);
/* 375 */       } else if (lhs instanceof short[]) {
/* 376 */         append((short[])lhs, (short[])rhs);
/* 377 */       } else if (lhs instanceof char[]) {
/* 378 */         append((char[])lhs, (char[])rhs);
/* 379 */       } else if (lhs instanceof byte[]) {
/* 380 */         append((byte[])lhs, (byte[])rhs);
/* 381 */       } else if (lhs instanceof double[]) {
/* 382 */         append((double[])lhs, (double[])rhs);
/* 383 */       } else if (lhs instanceof float[]) {
/* 384 */         append((float[])lhs, (float[])rhs);
/* 385 */       } else if (lhs instanceof boolean[]) {
/* 386 */         append((boolean[])lhs, (boolean[])rhs);
/*     */       }
/*     */       else {
/*     */         
/* 390 */         append((Object[])lhs, (Object[])rhs, comparator);
/*     */       }
/*     */     
/*     */     }
/* 394 */     else if (comparator == null) {
/*     */       
/* 396 */       Comparable<Object> comparable = (Comparable<Object>)lhs;
/* 397 */       this.comparison = comparable.compareTo(rhs);
/*     */     } else {
/*     */       
/* 400 */       Comparator<Object> comparator2 = (Comparator)comparator;
/* 401 */       this.comparison = comparator2.compare(lhs, rhs);
/*     */     } 
/*     */     
/* 404 */     return this;
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
/*     */   public CompareToBuilder append(long lhs, long rhs) {
/* 417 */     if (this.comparison != 0) {
/* 418 */       return this;
/*     */     }
/* 420 */     this.comparison = Long.compare(lhs, rhs);
/* 421 */     return this;
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
/*     */   public CompareToBuilder append(int lhs, int rhs) {
/* 433 */     if (this.comparison != 0) {
/* 434 */       return this;
/*     */     }
/* 436 */     this.comparison = Integer.compare(lhs, rhs);
/* 437 */     return this;
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
/*     */   public CompareToBuilder append(short lhs, short rhs) {
/* 449 */     if (this.comparison != 0) {
/* 450 */       return this;
/*     */     }
/* 452 */     this.comparison = Short.compare(lhs, rhs);
/* 453 */     return this;
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
/*     */   public CompareToBuilder append(char lhs, char rhs) {
/* 465 */     if (this.comparison != 0) {
/* 466 */       return this;
/*     */     }
/* 468 */     this.comparison = Character.compare(lhs, rhs);
/* 469 */     return this;
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
/*     */   public CompareToBuilder append(byte lhs, byte rhs) {
/* 481 */     if (this.comparison != 0) {
/* 482 */       return this;
/*     */     }
/* 484 */     this.comparison = Byte.compare(lhs, rhs);
/* 485 */     return this;
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
/*     */   public CompareToBuilder append(double lhs, double rhs) {
/* 502 */     if (this.comparison != 0) {
/* 503 */       return this;
/*     */     }
/* 505 */     this.comparison = Double.compare(lhs, rhs);
/* 506 */     return this;
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
/*     */   public CompareToBuilder append(float lhs, float rhs) {
/* 523 */     if (this.comparison != 0) {
/* 524 */       return this;
/*     */     }
/* 526 */     this.comparison = Float.compare(lhs, rhs);
/* 527 */     return this;
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
/*     */   public CompareToBuilder append(boolean lhs, boolean rhs) {
/* 539 */     if (this.comparison != 0) {
/* 540 */       return this;
/*     */     }
/* 542 */     if (lhs == rhs) {
/* 543 */       return this;
/*     */     }
/* 545 */     if (!lhs) {
/* 546 */       this.comparison = -1;
/*     */     } else {
/* 548 */       this.comparison = 1;
/*     */     } 
/* 550 */     return this;
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
/*     */   public CompareToBuilder append(Object[] lhs, Object[] rhs) {
/* 575 */     return append(lhs, rhs, (Comparator<?>)null);
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
/*     */   public CompareToBuilder append(Object[] lhs, Object[] rhs, Comparator<?> comparator) {
/* 602 */     if (this.comparison != 0) {
/* 603 */       return this;
/*     */     }
/* 605 */     if (lhs == rhs) {
/* 606 */       return this;
/*     */     }
/* 608 */     if (lhs == null) {
/* 609 */       this.comparison = -1;
/* 610 */       return this;
/*     */     } 
/* 612 */     if (rhs == null) {
/* 613 */       this.comparison = 1;
/* 614 */       return this;
/*     */     } 
/* 616 */     if (lhs.length != rhs.length) {
/* 617 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 618 */       return this;
/*     */     } 
/* 620 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 621 */       append(lhs[i], rhs[i], comparator);
/*     */     }
/* 623 */     return this;
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
/*     */   public CompareToBuilder append(long[] lhs, long[] rhs) {
/* 642 */     if (this.comparison != 0) {
/* 643 */       return this;
/*     */     }
/* 645 */     if (lhs == rhs) {
/* 646 */       return this;
/*     */     }
/* 648 */     if (lhs == null) {
/* 649 */       this.comparison = -1;
/* 650 */       return this;
/*     */     } 
/* 652 */     if (rhs == null) {
/* 653 */       this.comparison = 1;
/* 654 */       return this;
/*     */     } 
/* 656 */     if (lhs.length != rhs.length) {
/* 657 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 658 */       return this;
/*     */     } 
/* 660 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 661 */       append(lhs[i], rhs[i]);
/*     */     }
/* 663 */     return this;
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
/*     */   public CompareToBuilder append(int[] lhs, int[] rhs) {
/* 682 */     if (this.comparison != 0) {
/* 683 */       return this;
/*     */     }
/* 685 */     if (lhs == rhs) {
/* 686 */       return this;
/*     */     }
/* 688 */     if (lhs == null) {
/* 689 */       this.comparison = -1;
/* 690 */       return this;
/*     */     } 
/* 692 */     if (rhs == null) {
/* 693 */       this.comparison = 1;
/* 694 */       return this;
/*     */     } 
/* 696 */     if (lhs.length != rhs.length) {
/* 697 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 698 */       return this;
/*     */     } 
/* 700 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 701 */       append(lhs[i], rhs[i]);
/*     */     }
/* 703 */     return this;
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
/*     */   public CompareToBuilder append(short[] lhs, short[] rhs) {
/* 722 */     if (this.comparison != 0) {
/* 723 */       return this;
/*     */     }
/* 725 */     if (lhs == rhs) {
/* 726 */       return this;
/*     */     }
/* 728 */     if (lhs == null) {
/* 729 */       this.comparison = -1;
/* 730 */       return this;
/*     */     } 
/* 732 */     if (rhs == null) {
/* 733 */       this.comparison = 1;
/* 734 */       return this;
/*     */     } 
/* 736 */     if (lhs.length != rhs.length) {
/* 737 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 738 */       return this;
/*     */     } 
/* 740 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 741 */       append(lhs[i], rhs[i]);
/*     */     }
/* 743 */     return this;
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
/*     */   public CompareToBuilder append(char[] lhs, char[] rhs) {
/* 762 */     if (this.comparison != 0) {
/* 763 */       return this;
/*     */     }
/* 765 */     if (lhs == rhs) {
/* 766 */       return this;
/*     */     }
/* 768 */     if (lhs == null) {
/* 769 */       this.comparison = -1;
/* 770 */       return this;
/*     */     } 
/* 772 */     if (rhs == null) {
/* 773 */       this.comparison = 1;
/* 774 */       return this;
/*     */     } 
/* 776 */     if (lhs.length != rhs.length) {
/* 777 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 778 */       return this;
/*     */     } 
/* 780 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 781 */       append(lhs[i], rhs[i]);
/*     */     }
/* 783 */     return this;
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
/*     */   public CompareToBuilder append(byte[] lhs, byte[] rhs) {
/* 802 */     if (this.comparison != 0) {
/* 803 */       return this;
/*     */     }
/* 805 */     if (lhs == rhs) {
/* 806 */       return this;
/*     */     }
/* 808 */     if (lhs == null) {
/* 809 */       this.comparison = -1;
/* 810 */       return this;
/*     */     } 
/* 812 */     if (rhs == null) {
/* 813 */       this.comparison = 1;
/* 814 */       return this;
/*     */     } 
/* 816 */     if (lhs.length != rhs.length) {
/* 817 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 818 */       return this;
/*     */     } 
/* 820 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 821 */       append(lhs[i], rhs[i]);
/*     */     }
/* 823 */     return this;
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
/*     */   public CompareToBuilder append(double[] lhs, double[] rhs) {
/* 842 */     if (this.comparison != 0) {
/* 843 */       return this;
/*     */     }
/* 845 */     if (lhs == rhs) {
/* 846 */       return this;
/*     */     }
/* 848 */     if (lhs == null) {
/* 849 */       this.comparison = -1;
/* 850 */       return this;
/*     */     } 
/* 852 */     if (rhs == null) {
/* 853 */       this.comparison = 1;
/* 854 */       return this;
/*     */     } 
/* 856 */     if (lhs.length != rhs.length) {
/* 857 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 858 */       return this;
/*     */     } 
/* 860 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 861 */       append(lhs[i], rhs[i]);
/*     */     }
/* 863 */     return this;
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
/*     */   public CompareToBuilder append(float[] lhs, float[] rhs) {
/* 882 */     if (this.comparison != 0) {
/* 883 */       return this;
/*     */     }
/* 885 */     if (lhs == rhs) {
/* 886 */       return this;
/*     */     }
/* 888 */     if (lhs == null) {
/* 889 */       this.comparison = -1;
/* 890 */       return this;
/*     */     } 
/* 892 */     if (rhs == null) {
/* 893 */       this.comparison = 1;
/* 894 */       return this;
/*     */     } 
/* 896 */     if (lhs.length != rhs.length) {
/* 897 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 898 */       return this;
/*     */     } 
/* 900 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 901 */       append(lhs[i], rhs[i]);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CompareToBuilder append(boolean[] lhs, boolean[] rhs) {
/* 922 */     if (this.comparison != 0) {
/* 923 */       return this;
/*     */     }
/* 925 */     if (lhs == rhs) {
/* 926 */       return this;
/*     */     }
/* 928 */     if (lhs == null) {
/* 929 */       this.comparison = -1;
/* 930 */       return this;
/*     */     } 
/* 932 */     if (rhs == null) {
/* 933 */       this.comparison = 1;
/* 934 */       return this;
/*     */     } 
/* 936 */     if (lhs.length != rhs.length) {
/* 937 */       this.comparison = (lhs.length < rhs.length) ? -1 : 1;
/* 938 */       return this;
/*     */     } 
/* 940 */     for (int i = 0; i < lhs.length && this.comparison == 0; i++) {
/* 941 */       append(lhs[i], rhs[i]);
/*     */     }
/* 943 */     return this;
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
/*     */   public int toComparison() {
/* 957 */     return this.comparison;
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
/*     */   public Integer build() {
/* 972 */     return Integer.valueOf(toComparison());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\builder\CompareToBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */