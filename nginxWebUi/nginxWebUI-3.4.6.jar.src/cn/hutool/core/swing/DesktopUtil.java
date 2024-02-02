/*     */ package cn.hutool.core.swing;
/*     */ 
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.awt.Desktop;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DesktopUtil
/*     */ {
/*     */   public static Desktop getDsktop() {
/*  26 */     return Desktop.getDesktop();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void browse(String url) {
/*  35 */     browse(URLUtil.toURI(url));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void browse(URI uri) {
/*  45 */     Desktop dsktop = getDsktop();
/*     */     try {
/*  47 */       dsktop.browse(uri);
/*  48 */     } catch (IOException e) {
/*  49 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void open(File file) {
/*  59 */     Desktop dsktop = getDsktop();
/*     */     try {
/*  61 */       dsktop.open(file);
/*  62 */     } catch (IOException e) {
/*  63 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void edit(File file) {
/*  73 */     Desktop dsktop = getDsktop();
/*     */     try {
/*  75 */       dsktop.edit(file);
/*  76 */     } catch (IOException e) {
/*  77 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void print(File file) {
/*  87 */     Desktop dsktop = getDsktop();
/*     */     try {
/*  89 */       dsktop.print(file);
/*  90 */     } catch (IOException e) {
/*  91 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void mail(String mailAddress) {
/* 101 */     Desktop dsktop = getDsktop();
/*     */     try {
/* 103 */       dsktop.mail(URLUtil.toURI(mailAddress));
/* 104 */     } catch (IOException e) {
/* 105 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\DesktopUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */