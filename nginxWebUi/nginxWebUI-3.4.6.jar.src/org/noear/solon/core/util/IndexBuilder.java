/*     */ package org.noear.solon.core.util;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IndexBuilder
/*     */ {
/*  19 */   private static final Map<String, Integer> map = new HashMap<>();
/*  20 */   private static final ArrayList<String> classStack = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int buildIndex(Class<?> clazz) {
/*  29 */     return buildIndex(clazz, Boolean.valueOf(true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static int buildIndex(Class<?> clazz, Boolean stackTop) {
/*  40 */     if (stackTop.booleanValue()) {
/*  41 */       classStack.clear();
/*     */       
/*  43 */       if (isLoopRelate(clazz, clazz.getName())) {
/*  44 */         String link = "";
/*  45 */         for (int i = 0; i < classStack.size(); i++) {
/*  46 */           link = link + (String)classStack.get(i);
/*  47 */           if (i != classStack.size() - 1) {
/*  48 */             link = link + " -> ";
/*     */           }
/*     */         } 
/*     */         
/*  52 */         throw new IllegalStateException("Dependency loops are not supported: " + link);
/*     */       } 
/*     */     } 
/*     */     
/*  56 */     if (map.get(clazz.getName()) != null) {
/*  57 */       return ((Integer)map.get(clazz.getName())).intValue();
/*     */     }
/*     */     
/*  60 */     List<Class<?>> clazzList = findRelateClass(clazz);
/*     */ 
/*     */     
/*  63 */     if (clazzList.size() == 0) {
/*  64 */       map.put(clazz.getName(), Integer.valueOf(0));
/*  65 */       return 0;
/*     */     } 
/*     */ 
/*     */     
/*  69 */     Integer maxIndex = null;
/*  70 */     for (Class<?> clazzRelate : clazzList) {
/*  71 */       Integer index = Integer.valueOf(buildIndex(clazzRelate, Boolean.valueOf(false)));
/*     */       
/*  73 */       if (maxIndex == null) {
/*  74 */         maxIndex = index; continue;
/*  75 */       }  if (maxIndex.intValue() < index.intValue()) {
/*  76 */         maxIndex = index;
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  81 */     map.put(clazz.getName(), Integer.valueOf(maxIndex.intValue() + 1));
/*  82 */     return maxIndex.intValue() + 1;
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
/*     */   private static List<Class<?>> findRelateClass(Class<?> clazz) {
/*  94 */     List<Class<?>> clazzList = new ArrayList<>();
/*  95 */     Field[] fields = clazz.getDeclaredFields();
/*  96 */     for (Field field : fields) {
/*  97 */       if (field.isAnnotationPresent((Class)Inject.class)) {
/*  98 */         Inject inject = field.<Inject>getAnnotation(Inject.class);
/*  99 */         if (!inject.value().contains("${"))
/*     */         {
/*     */ 
/*     */ 
/*     */           
/* 104 */           if (!clazz.equals(field.getType()))
/*     */           {
/*     */ 
/*     */ 
/*     */             
/* 109 */             clazzList.add(field.getType()); }  } 
/*     */       } 
/*     */     } 
/* 112 */     return clazzList;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isLoopRelate(Class<?> clazz, String topName) {
/* 122 */     classStack.add(clazz.getName());
/*     */ 
/*     */     
/* 125 */     List<Class<?>> clazzList = findRelateClass(clazz);
/*     */     
/* 127 */     for (Class<?> clazzRelate : clazzList) {
/* 128 */       if (clazzRelate.getName().equals(topName)) {
/* 129 */         classStack.add(clazzRelate.getName());
/* 130 */         return true;
/*     */       } 
/*     */     } 
/*     */     
/* 134 */     for (Class<?> clazzRelate : clazzList) {
/* 135 */       if (isLoopRelate(clazzRelate, topName)) {
/* 136 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 140 */     classStack.remove(clazz.getName());
/* 141 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\IndexBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */