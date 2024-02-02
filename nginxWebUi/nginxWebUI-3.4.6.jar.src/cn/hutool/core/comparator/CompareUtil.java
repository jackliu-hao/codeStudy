/*     */ package cn.hutool.core.comparator;
/*     */ 
/*     */ import java.util.Comparator;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Function;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CompareUtil
/*     */ {
/*     */   public static <E extends Comparable<? super E>> Comparator<E> naturalComparator() {
/*  23 */     return ComparableComparator.INSTANCE;
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
/*     */   public static <T> int compare(T c1, T c2, Comparator<T> comparator) {
/*  43 */     if (null == comparator) {
/*  44 */       return compare((Comparable)c1, (Comparable)c2);
/*     */     }
/*  46 */     return comparator.compare(c1, c2);
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
/*     */   public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
/*  59 */     return compare(c1, c2, false);
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
/*     */   public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean isNullGreater) {
/*  73 */     if (c1 == c2)
/*  74 */       return 0; 
/*  75 */     if (c1 == null)
/*  76 */       return isNullGreater ? 1 : -1; 
/*  77 */     if (c2 == null) {
/*  78 */       return isNullGreater ? -1 : 1;
/*     */     }
/*  80 */     return c1.compareTo(c2);
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
/*     */   public static <T> int compare(T o1, T o2, boolean isNullGreater) {
/* 101 */     if (o1 == o2)
/* 102 */       return 0; 
/* 103 */     if (null == o1)
/* 104 */       return isNullGreater ? 1 : -1; 
/* 105 */     if (null == o2) {
/* 106 */       return isNullGreater ? -1 : 1;
/*     */     }
/*     */     
/* 109 */     if (o1 instanceof Comparable && o2 instanceof Comparable)
/*     */     {
/* 111 */       return ((Comparable<T>)o1).compareTo(o2);
/*     */     }
/*     */     
/* 114 */     if (o1.equals(o2)) {
/* 115 */       return 0;
/*     */     }
/*     */     
/* 118 */     int result = Integer.compare(o1.hashCode(), o2.hashCode());
/* 119 */     if (0 == result) {
/* 120 */       result = compare(o1.toString(), o2.toString());
/*     */     }
/*     */     
/* 123 */     return result;
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
/*     */   public static <T> Comparator<T> comparingPinyin(Function<T, String> keyExtractor) {
/* 135 */     return comparingPinyin(keyExtractor, false);
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
/*     */   public static <T> Comparator<T> comparingPinyin(Function<T, String> keyExtractor, boolean reverse) {
/* 148 */     Objects.requireNonNull(keyExtractor);
/* 149 */     PinyinComparator pinyinComparator = new PinyinComparator();
/* 150 */     if (reverse) {
/* 151 */       return (o1, o2) -> pinyinComparator.compare(keyExtractor.apply(o2), keyExtractor.apply(o1));
/*     */     }
/* 153 */     return (o1, o2) -> pinyinComparator.compare(keyExtractor.apply(o1), keyExtractor.apply(o2));
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
/*     */   public static <T, U> Comparator<T> comparingIndexed(Function<? super T, ? extends U> keyExtractor, U... objs) {
/* 169 */     return comparingIndexed(keyExtractor, false, objs);
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
/*     */   public static <T, U> Comparator<T> comparingIndexed(Function<? super T, ? extends U> keyExtractor, boolean atEndIfMiss, U... objs) {
/* 186 */     Objects.requireNonNull(keyExtractor);
/* 187 */     IndexedComparator<U> indexedComparator = new IndexedComparator<>(atEndIfMiss, objs);
/* 188 */     return (o1, o2) -> indexedComparator.compare(keyExtractor.apply(o1), keyExtractor.apply(o2));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\comparator\CompareUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */