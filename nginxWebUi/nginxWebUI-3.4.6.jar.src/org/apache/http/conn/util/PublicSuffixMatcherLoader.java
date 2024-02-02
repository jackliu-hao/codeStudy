/*     */ package org.apache.http.conn.util;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.net.URL;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.Consts;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.util.Args;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public final class PublicSuffixMatcherLoader
/*     */ {
/*     */   private static volatile PublicSuffixMatcher DEFAULT_INSTANCE;
/*     */   
/*     */   private static PublicSuffixMatcher load(InputStream in) throws IOException {
/*  54 */     List<PublicSuffixList> lists = (new PublicSuffixListParser()).parseByType(new InputStreamReader(in, Consts.UTF_8));
/*     */     
/*  56 */     return new PublicSuffixMatcher(lists);
/*     */   }
/*     */   
/*     */   public static PublicSuffixMatcher load(URL url) throws IOException {
/*  60 */     Args.notNull(url, "URL");
/*  61 */     InputStream in = url.openStream();
/*     */     try {
/*  63 */       return load(in);
/*     */     } finally {
/*  65 */       in.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static PublicSuffixMatcher load(File file) throws IOException {
/*  70 */     Args.notNull(file, "File");
/*  71 */     InputStream in = new FileInputStream(file);
/*     */     try {
/*  73 */       return load(in);
/*     */     } finally {
/*  75 */       in.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static PublicSuffixMatcher getDefault() {
/*  82 */     if (DEFAULT_INSTANCE == null) {
/*  83 */       synchronized (PublicSuffixMatcherLoader.class) {
/*  84 */         if (DEFAULT_INSTANCE == null) {
/*  85 */           URL url = PublicSuffixMatcherLoader.class.getResource("/mozilla/public-suffix-list.txt");
/*     */           
/*  87 */           if (url != null) {
/*     */             try {
/*  89 */               DEFAULT_INSTANCE = load(url);
/*  90 */             } catch (IOException ex) {
/*     */               
/*  92 */               Log log = LogFactory.getLog(PublicSuffixMatcherLoader.class);
/*  93 */               if (log.isWarnEnabled()) {
/*  94 */                 log.warn("Failure loading public suffix list from default resource", ex);
/*     */               }
/*     */             } 
/*     */           } else {
/*  98 */             DEFAULT_INSTANCE = new PublicSuffixMatcher(DomainType.ICANN, Arrays.asList(new String[] { "com" }, ), null);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }
/* 103 */     return DEFAULT_INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\con\\util\PublicSuffixMatcherLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */