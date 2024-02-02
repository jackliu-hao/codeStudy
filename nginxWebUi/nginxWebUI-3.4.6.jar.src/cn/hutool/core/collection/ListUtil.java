/*     */ package cn.hutool.core.collection;
/*     */ 
/*     */ import cn.hutool.core.comparator.PinyinComparator;
/*     */ import cn.hutool.core.comparator.PropertyComparator;
/*     */ import cn.hutool.core.lang.Matcher;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.PageUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.CopyOnWriteArrayList;
/*     */ import java.util.function.Consumer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ListUtil
/*     */ {
/*     */   public static <T> List<T> list(boolean isLinked) {
/*  37 */     return isLinked ? new LinkedList<>() : new ArrayList<>();
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
/*     */   @SafeVarargs
/*     */   public static <T> List<T> list(boolean isLinked, T... values) {
/*  51 */     if (ArrayUtil.isEmpty((Object[])values)) {
/*  52 */       return list(isLinked);
/*     */     }
/*  54 */     List<T> arrayList = isLinked ? new LinkedList<>() : new ArrayList<>(values.length);
/*  55 */     Collections.addAll(arrayList, values);
/*  56 */     return arrayList;
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
/*     */   public static <T> List<T> list(boolean isLinked, Collection<T> collection) {
/*  69 */     if (null == collection) {
/*  70 */       return list(isLinked);
/*     */     }
/*  72 */     return isLinked ? new LinkedList<>(collection) : new ArrayList<>(collection);
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
/*     */   public static <T> List<T> list(boolean isLinked, Iterable<T> iterable) {
/*  86 */     if (null == iterable) {
/*  87 */       return list(isLinked);
/*     */     }
/*  89 */     return list(isLinked, iterable.iterator());
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
/*     */   public static <T> List<T> list(boolean isLinked, Iterator<T> iter) {
/* 103 */     List<T> list = list(isLinked);
/* 104 */     if (null != iter) {
/* 105 */       while (iter.hasNext()) {
/* 106 */         list.add(iter.next());
/*     */       }
/*     */     }
/* 109 */     return list;
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
/*     */   public static <T> List<T> list(boolean isLinked, Enumeration<T> enumration) {
/* 123 */     List<T> list = list(isLinked);
/* 124 */     if (null != enumration) {
/* 125 */       while (enumration.hasMoreElements()) {
/* 126 */         list.add(enumration.nextElement());
/*     */       }
/*     */     }
/* 129 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @SafeVarargs
/*     */   public static <T> ArrayList<T> toList(T... values) {
/* 141 */     return (ArrayList<T>)list(false, values);
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
/*     */   @SafeVarargs
/*     */   public static <T> LinkedList<T> toLinkedList(T... values) {
/* 154 */     return (LinkedList<T>)list(true, values);
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
/*     */   @SafeVarargs
/*     */   public static <T> List<T> of(T... ts) {
/* 168 */     if (ArrayUtil.isEmpty((Object[])ts)) {
/* 169 */       return Collections.emptyList();
/*     */     }
/* 171 */     return Collections.unmodifiableList(toList(ts));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> CopyOnWriteArrayList<T> toCopyOnWriteArrayList(Collection<T> collection) {
/* 182 */     return (null == collection) ? new CopyOnWriteArrayList<>() : new CopyOnWriteArrayList<>(collection);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> ArrayList<T> toList(Collection<T> collection) {
/* 193 */     return (ArrayList<T>)list(false, collection);
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
/*     */   public static <T> ArrayList<T> toList(Iterable<T> iterable) {
/* 206 */     return (ArrayList<T>)list(false, iterable);
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
/*     */   public static <T> ArrayList<T> toList(Iterator<T> iterator) {
/* 219 */     return (ArrayList<T>)list(false, iterator);
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
/*     */   public static <T> ArrayList<T> toList(Enumeration<T> enumeration) {
/* 232 */     return (ArrayList<T>)list(false, enumeration);
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
/*     */   public static <T> List<T> page(int pageNo, int pageSize, List<T> list) {
/* 246 */     if (CollUtil.isEmpty(list)) {
/* 247 */       return new ArrayList<>(0);
/*     */     }
/*     */     
/* 250 */     int resultSize = list.size();
/*     */     
/* 252 */     if (resultSize <= pageSize) {
/* 253 */       if (pageNo < PageUtil.getFirstPageNo() + 1) {
/* 254 */         return unmodifiable(list);
/*     */       }
/*     */       
/* 257 */       return new ArrayList<>(0);
/*     */     } 
/*     */ 
/*     */     
/* 261 */     if ((pageNo - PageUtil.getFirstPageNo()) * pageSize > resultSize)
/*     */     {
/* 263 */       return new ArrayList<>(0);
/*     */     }
/*     */     
/* 266 */     int[] startEnd = PageUtil.transToStartEnd(pageNo, pageSize);
/* 267 */     if (startEnd[1] > resultSize) {
/* 268 */       startEnd[1] = resultSize;
/* 269 */       if (startEnd[0] > startEnd[1]) {
/* 270 */         return new ArrayList<>(0);
/*     */       }
/*     */     } 
/*     */     
/* 274 */     return sub(list, startEnd[0], startEnd[1]);
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
/*     */   public static <T> void page(List<T> list, int pageSize, Consumer<List<T>> pageListConsumer) {
/* 287 */     if (CollUtil.isEmpty(list) || pageSize <= 0) {
/*     */       return;
/*     */     }
/*     */     
/* 291 */     int total = list.size();
/* 292 */     int totalPage = PageUtil.totalPage(total, pageSize);
/* 293 */     for (int pageNo = PageUtil.getFirstPageNo(); pageNo < totalPage + PageUtil.getFirstPageNo(); pageNo++) {
/*     */       
/* 295 */       int[] startEnd = PageUtil.transToStartEnd(pageNo, pageSize);
/* 296 */       if (startEnd[1] > total) {
/* 297 */         startEnd[1] = total;
/*     */       }
/*     */ 
/*     */       
/* 301 */       pageListConsumer.accept(sub(list, startEnd[0], startEnd[1]));
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
/*     */   public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
/* 315 */     if (CollUtil.isEmpty(list)) {
/* 316 */       return list;
/*     */     }
/* 318 */     list.sort(c);
/* 319 */     return list;
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
/*     */   public static <T> List<T> sortByProperty(List<T> list, String property) {
/* 332 */     return sort(list, (Comparator<? super T>)new PropertyComparator(property));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> sortByPinyin(List<String> list) {
/* 343 */     return sort(list, (Comparator<? super String>)new PinyinComparator());
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
/*     */   public static <T> List<T> reverse(List<T> list) {
/* 355 */     Collections.reverse(list);
/* 356 */     return list;
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
/*     */   public static <T> List<T> reverseNew(List<T> list) {
/* 368 */     List<T> list2 = (List<T>)ObjectUtil.clone(list);
/* 369 */     if (null == list2)
/*     */     {
/* 371 */       list2 = new ArrayList<>(list);
/*     */     }
/* 373 */     return reverse(list2);
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
/*     */   public static <T> List<T> setOrAppend(List<T> list, int index, T element) {
/* 387 */     if (index < list.size()) {
/* 388 */       list.set(index, element);
/*     */     } else {
/* 390 */       list.add(element);
/*     */     } 
/* 392 */     return list;
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
/*     */   public static <T> List<T> sub(List<T> list, int start, int end) {
/* 405 */     return sub(list, start, end, 1);
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
/*     */   public static <T> List<T> sub(List<T> list, int start, int end, int step) {
/* 421 */     if (list == null) {
/* 422 */       return null;
/*     */     }
/*     */     
/* 425 */     if (list.isEmpty()) {
/* 426 */       return new ArrayList<>(0);
/*     */     }
/*     */     
/* 429 */     int size = list.size();
/* 430 */     if (start < 0) {
/* 431 */       start += size;
/*     */     }
/* 433 */     if (end < 0) {
/* 434 */       end += size;
/*     */     }
/* 436 */     if (start == size) {
/* 437 */       return new ArrayList<>(0);
/*     */     }
/* 439 */     if (start > end) {
/* 440 */       int tmp = start;
/* 441 */       start = end;
/* 442 */       end = tmp;
/*     */     } 
/* 444 */     if (end > size) {
/* 445 */       if (start >= size) {
/* 446 */         return new ArrayList<>(0);
/*     */       }
/* 448 */       end = size;
/*     */     } 
/*     */     
/* 451 */     if (step < 1) {
/* 452 */       step = 1;
/*     */     }
/*     */     
/* 455 */     List<T> result = new ArrayList<>(); int i;
/* 456 */     for (i = start; i < end; i += step) {
/* 457 */       result.add(list.get(i));
/*     */     }
/* 459 */     return result;
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
/*     */   public static <T> int lastIndexOf(List<T> list, Matcher<T> matcher) {
/* 473 */     if (null != list) {
/* 474 */       int size = list.size();
/* 475 */       if (size > 0) {
/* 476 */         for (int i = size - 1; i >= 0; i--) {
/* 477 */           if (null == matcher || matcher.match(list.get(i))) {
/* 478 */             return i;
/*     */           }
/*     */         } 
/*     */       }
/*     */     } 
/* 483 */     return -1;
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
/*     */   public static <T> int[] indexOfAll(List<T> list, Matcher<T> matcher) {
/* 496 */     return CollUtil.indexOfAll(list, matcher);
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
/*     */   public static <T> List<T> unmodifiable(List<T> list) {
/* 508 */     if (null == list) {
/* 509 */       return null;
/*     */     }
/* 511 */     return Collections.unmodifiableList(list);
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
/*     */   public static <T> List<T> empty() {
/* 523 */     return Collections.emptyList();
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
/*     */   public static <T> List<List<T>> partition(List<T> list, int size) {
/* 542 */     if (CollUtil.isEmpty(list)) {
/* 543 */       return empty();
/*     */     }
/*     */     
/* 546 */     return (list instanceof java.util.RandomAccess) ? new RandomAccessPartition<>(list, size) : new Partition<>(list, size);
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
/*     */   public static <T> List<List<T>> split(List<T> list, int size) {
/* 567 */     return partition(list, size);
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
/*     */   public static <T> List<List<T>> splitAvg(List<T> list, int limit) {
/* 588 */     if (CollUtil.isEmpty(list)) {
/* 589 */       return empty();
/*     */     }
/*     */     
/* 592 */     return (list instanceof java.util.RandomAccess) ? new RandomAccessAvgPartition<>(list, limit) : new AvgPartition<>(list, limit);
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
/*     */   public static <T> void swapTo(List<T> list, T element, Integer targetIndex) {
/* 609 */     if (CollUtil.isNotEmpty(list)) {
/* 610 */       int index = list.indexOf(element);
/* 611 */       if (index >= 0) {
/* 612 */         Collections.swap(list, index, targetIndex.intValue());
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
/*     */   public static <T> void swapElement(List<T> list, T element, T targetElement) {
/* 628 */     if (CollUtil.isNotEmpty(list)) {
/* 629 */       int targetIndex = list.indexOf(targetElement);
/* 630 */       if (targetIndex >= 0)
/* 631 */         swapTo(list, element, Integer.valueOf(targetIndex)); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\collection\ListUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */