/*     */ package cn.hutool.extra.ftp;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class AbstractFtp
/*     */   implements Closeable
/*     */ {
/*  21 */   public static final Charset DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected FtpConfig ftpConfig;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractFtp(FtpConfig config) {
/*  32 */     this.ftpConfig = config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract AbstractFtp reconnectIfTimeout();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean cd(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean toParent() {
/*  58 */     return cd("..");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract String pwd();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDir(String dir) {
/*  76 */     return cd(dir);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean mkdir(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean exist(String path) {
/*     */     List<String> names;
/*  94 */     String fileName = FileUtil.getName(path);
/*  95 */     String dir = StrUtil.removeSuffix(path, fileName);
/*     */     
/*     */     try {
/*  98 */       names = ls(dir);
/*  99 */     } catch (FtpException ignore) {
/* 100 */       return false;
/*     */     } 
/* 102 */     return containsIgnoreCase(names, fileName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract List<String> ls(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean delFile(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean delDir(String paramString);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mkDirs(String dir) {
/* 135 */     String[] dirs = StrUtil.trim(dir).split("[\\\\/]+");
/*     */     
/* 137 */     String now = pwd();
/* 138 */     if (dirs.length > 0 && StrUtil.isEmpty(dirs[0]))
/*     */     {
/* 140 */       cd("/");
/*     */     }
/* 142 */     for (String s : dirs) {
/* 143 */       if (StrUtil.isNotEmpty(s)) {
/* 144 */         boolean exist = true;
/*     */         try {
/* 146 */           if (false == cd(s)) {
/* 147 */             exist = false;
/*     */           }
/* 149 */         } catch (FtpException e) {
/* 150 */           exist = false;
/*     */         } 
/* 152 */         if (false == exist) {
/*     */           
/* 154 */           mkdir(s);
/* 155 */           cd(s);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 160 */     cd(now);
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
/*     */   public abstract boolean upload(String paramString, File paramFile);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void download(String paramString, File paramFile);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void download(String path, File outFile, String tempFileSuffix) {
/* 192 */     if (StrUtil.isBlank(tempFileSuffix)) {
/* 193 */       tempFileSuffix = ".temp";
/*     */     } else {
/* 195 */       tempFileSuffix = StrUtil.addPrefixIfNot(tempFileSuffix, ".");
/*     */     } 
/*     */ 
/*     */     
/* 199 */     String fileName = outFile.isDirectory() ? FileUtil.getName(path) : outFile.getName();
/*     */     
/* 201 */     String tempFileName = fileName + tempFileSuffix;
/*     */ 
/*     */     
/* 204 */     outFile = new File(outFile.isDirectory() ? outFile : outFile.getParentFile(), tempFileName);
/*     */     try {
/* 206 */       download(path, outFile);
/*     */       
/* 208 */       FileUtil.rename(outFile, fileName, true);
/* 209 */     } catch (Throwable e) {
/*     */       
/* 211 */       FileUtil.del(outFile);
/* 212 */       throw new FtpException(e);
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
/*     */   public abstract void recursiveDownloadFolder(String paramString, File paramFile);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean containsIgnoreCase(List<String> names, String nameToFind) {
/* 235 */     if (CollUtil.isEmpty(names)) {
/* 236 */       return false;
/*     */     }
/* 238 */     if (StrUtil.isEmpty(nameToFind)) {
/* 239 */       return false;
/*     */     }
/* 241 */     for (String name : names) {
/* 242 */       if (nameToFind.equalsIgnoreCase(name)) {
/* 243 */         return true;
/*     */       }
/*     */     } 
/* 246 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ftp\AbstractFtp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */