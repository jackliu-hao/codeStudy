/*     */ package cn.hutool.core.io;
/*     */ 
/*     */ import cn.hutool.core.io.resource.ResourceUtil;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.Manifest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ManifestUtil
/*     */ {
/*  22 */   private static final String[] MANIFEST_NAMES = new String[] { "Manifest.mf", "manifest.mf", "MANIFEST.MF" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Manifest getManifest(Class<?> cls) throws IORuntimeException {
/*     */     URLConnection connection;
/*  34 */     URL url = ResourceUtil.getResource(null, cls);
/*     */     
/*     */     try {
/*  37 */       connection = url.openConnection();
/*  38 */     } catch (IOException e) {
/*  39 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/*  42 */     if (connection instanceof JarURLConnection) {
/*  43 */       JarURLConnection conn = (JarURLConnection)connection;
/*  44 */       return getManifest(conn);
/*     */     } 
/*  46 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Manifest getManifest(File classpathItem) throws IORuntimeException {
/*  57 */     Manifest manifest = null;
/*     */     
/*  59 */     if (classpathItem.isFile()) {
/*  60 */       try (JarFile jarFile = new JarFile(classpathItem)) {
/*  61 */         manifest = getManifest(jarFile);
/*  62 */       } catch (IOException e) {
/*  63 */         throw new IORuntimeException(e);
/*     */       } 
/*     */     } else {
/*  66 */       File metaDir = new File(classpathItem, "META-INF");
/*  67 */       File manifestFile = null;
/*  68 */       if (metaDir.isDirectory()) {
/*  69 */         for (String name : MANIFEST_NAMES) {
/*  70 */           File mFile = new File(metaDir, name);
/*  71 */           if (mFile.isFile()) {
/*  72 */             manifestFile = mFile;
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       }
/*  77 */       if (null != manifestFile) {
/*  78 */         try (FileInputStream fis = new FileInputStream(manifestFile)) {
/*  79 */           manifest = new Manifest(fis);
/*  80 */         } catch (IOException e) {
/*  81 */           throw new IORuntimeException(e);
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/*  86 */     return manifest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Manifest getManifest(JarURLConnection connection) throws IORuntimeException {
/*     */     JarFile jarFile;
/*     */     try {
/*  99 */       jarFile = connection.getJarFile();
/* 100 */     } catch (IOException e) {
/* 101 */       throw new IORuntimeException(e);
/*     */     } 
/* 103 */     return getManifest(jarFile);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Manifest getManifest(JarFile jarFile) throws IORuntimeException {
/*     */     try {
/* 115 */       return jarFile.getManifest();
/* 116 */     } catch (IOException e) {
/* 117 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\ManifestUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */