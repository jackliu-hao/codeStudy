/*     */ package cn.hutool.core.img;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontFormatException;
/*     */ import java.awt.FontMetrics;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FontUtil
/*     */ {
/*     */   public static Font createFont() {
/*  28 */     return new Font(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Font createSansSerifFont(int size) {
/*  38 */     return createFont("SansSerif", size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Font createFont(String name, int size) {
/*  49 */     return new Font(name, 0, size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Font createFont(File fontFile) {
/*     */     try {
/*  61 */       return Font.createFont(0, fontFile);
/*  62 */     } catch (FontFormatException e) {
/*     */       
/*     */       try {
/*  65 */         return Font.createFont(1, fontFile);
/*  66 */       } catch (Exception e1) {
/*  67 */         throw new UtilException(e);
/*     */       } 
/*  69 */     } catch (IOException e) {
/*  70 */       throw new IORuntimeException(e);
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
/*     */   public static Font createFont(InputStream fontStream) {
/*     */     try {
/*  83 */       return Font.createFont(0, fontStream);
/*  84 */     } catch (FontFormatException e) {
/*     */       
/*     */       try {
/*  87 */         return Font.createFont(1, fontStream);
/*  88 */       } catch (Exception e1) {
/*  89 */         throw new UtilException(e1);
/*     */       } 
/*  91 */     } catch (IOException e) {
/*  92 */       throw new IORuntimeException(e);
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
/*     */   public static Dimension getDimension(FontMetrics metrics, String str) {
/* 104 */     int width = metrics.stringWidth(str);
/* 105 */     int height = metrics.getAscent() - metrics.getLeading() - metrics.getDescent();
/*     */     
/* 107 */     return new Dimension(width, height);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\FontUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */