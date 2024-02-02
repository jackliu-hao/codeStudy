/*     */ package cn.hutool.core.stream;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Spliterators;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.UnaryOperator;
/*     */ import java.util.stream.Stream;
/*     */ import java.util.stream.StreamSupport;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StreamUtil
/*     */ {
/*     */   @SafeVarargs
/*     */   public static <T> Stream<T> of(T... array) {
/*  29 */     Assert.notNull(array, "Array must be not null!", new Object[0]);
/*  30 */     return Stream.of(array);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> Stream<T> of(Iterable<T> iterable) {
/*  41 */     return of(iterable, false);
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
/*     */   public static <T> Stream<T> of(Iterable<T> iterable, boolean parallel) {
/*  53 */     Assert.notNull(iterable, "Iterable must be not null!", new Object[0]);
/*  54 */     return StreamSupport.stream(
/*  55 */         Spliterators.spliterator(CollUtil.toCollection(iterable), 0), parallel);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Stream<String> of(File file) {
/*  66 */     return of(file, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Stream<String> of(Path path) {
/*  76 */     return of(path, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Stream<String> of(File file, Charset charset) {
/*  87 */     Assert.notNull(file, "File must be not null!", new Object[0]);
/*  88 */     return of(file.toPath(), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Stream<String> of(Path path, Charset charset) {
/*     */     try {
/* 100 */       return Files.lines(path, charset);
/* 101 */     } catch (IOException e) {
/* 102 */       throw new IORuntimeException(e);
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
/*     */   public static <T> Stream<T> of(T seed, UnaryOperator<T> elementCreator, int limit) {
/* 116 */     return Stream.<T>iterate(seed, elementCreator).limit(limit);
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
/*     */   public static <T> String join(Stream<T> stream, CharSequence delimiter) {
/* 128 */     return stream.collect(CollectorUtil.joining(delimiter));
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
/*     */   public static <T> String join(Stream<T> stream, CharSequence delimiter, Function<T, ? extends CharSequence> toStringFunc) {
/* 142 */     return stream.collect(CollectorUtil.joining(delimiter, toStringFunc));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\stream\StreamUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */