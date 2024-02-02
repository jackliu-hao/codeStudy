/*     */ package com.sun.jna.platform;
/*     */ 
/*     */ import com.sun.jna.platform.win32.FlagEnum;
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
/*     */ public class EnumUtils
/*     */ {
/*     */   public static final int UNINITIALIZED = -1;
/*     */   
/*     */   public static <E extends Enum<E>> int toInteger(E val) {
/*  52 */     Enum[] arrayOfEnum = (Enum[])val.getClass().getEnumConstants();
/*     */     
/*  54 */     for (int idx = 0; idx < arrayOfEnum.length; idx++) {
/*  55 */       if (arrayOfEnum[idx] == val) {
/*  56 */         return idx;
/*     */       }
/*     */     } 
/*     */     
/*  60 */     throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <E extends Enum<E>> E fromInteger(int idx, Class<E> clazz) {
/*  70 */     if (idx == -1) {
/*  71 */       return null;
/*     */     }
/*  73 */     Enum[] arrayOfEnum = (Enum[])clazz.getEnumConstants();
/*  74 */     return (E)arrayOfEnum[idx];
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends FlagEnum> Set<T> setFromInteger(int flags, Class<T> clazz) {
/*  84 */     FlagEnum[] arrayOfFlagEnum = (FlagEnum[])clazz.getEnumConstants();
/*  85 */     Set<T> result = new HashSet<T>();
/*     */     
/*  87 */     for (FlagEnum flagEnum : arrayOfFlagEnum) {
/*     */       
/*  89 */       if ((flags & flagEnum.getFlag()) != 0)
/*     */       {
/*  91 */         result.add((T)flagEnum);
/*     */       }
/*     */     } 
/*     */     
/*  95 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T extends FlagEnum> int setToInteger(Set<T> set) {
/* 103 */     int sum = 0;
/*     */     
/* 105 */     for (FlagEnum flagEnum : set)
/*     */     {
/* 107 */       sum |= flagEnum.getFlag();
/*     */     }
/*     */     
/* 110 */     return sum;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\EnumUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */