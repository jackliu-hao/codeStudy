/*     */ package cn.hutool.core.io.file;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.copier.SrcToDestCopier;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.util.ArrayList;
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
/*     */ public class FileCopier
/*     */   extends SrcToDestCopier<File, FileCopier>
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private boolean isOverride;
/*     */   private boolean isCopyAttributes;
/*     */   private boolean isCopyContentIfDir;
/*     */   private boolean isOnlyCopyFile;
/*     */   
/*     */   public static FileCopier create(String srcPath, String destPath) {
/*  50 */     return new FileCopier(FileUtil.file(srcPath), FileUtil.file(destPath));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileCopier create(File src, File dest) {
/*  60 */     return new FileCopier(src, dest);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileCopier(File src, File dest) {
/*  71 */     this.src = src;
/*  72 */     this.dest = dest;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOverride() {
/*  82 */     return this.isOverride;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileCopier setOverride(boolean isOverride) {
/*  90 */     this.isOverride = isOverride;
/*  91 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCopyAttributes() {
/*  99 */     return this.isCopyAttributes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileCopier setCopyAttributes(boolean isCopyAttributes) {
/* 107 */     this.isCopyAttributes = isCopyAttributes;
/* 108 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isCopyContentIfDir() {
/* 116 */     return this.isCopyContentIfDir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileCopier setCopyContentIfDir(boolean isCopyContentIfDir) {
/* 125 */     this.isCopyContentIfDir = isCopyContentIfDir;
/* 126 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isOnlyCopyFile() {
/* 136 */     return this.isOnlyCopyFile;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileCopier setOnlyCopyFile(boolean isOnlyCopyFile) {
/* 147 */     this.isOnlyCopyFile = isOnlyCopyFile;
/* 148 */     return this;
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
/*     */   
/*     */   public File copy() throws IORuntimeException {
/* 170 */     File src = (File)this.src;
/* 171 */     File dest = (File)this.dest;
/*     */     
/* 173 */     Assert.notNull(src, "Source File is null !", new Object[0]);
/* 174 */     if (false == src.exists()) {
/* 175 */       throw new IORuntimeException("File not exist: " + src);
/*     */     }
/* 177 */     Assert.notNull(dest, "Destination File or directiory is null !", new Object[0]);
/* 178 */     if (FileUtil.equals(src, dest)) {
/* 179 */       throw new IORuntimeException("Files '{}' and '{}' are equal", new Object[] { src, dest });
/*     */     }
/*     */     
/* 182 */     if (src.isDirectory()) {
/* 183 */       if (dest.exists() && false == dest.isDirectory())
/*     */       {
/* 185 */         throw new IORuntimeException("Src is a directory but dest is a file!");
/*     */       }
/* 187 */       if (FileUtil.isSub(src, dest)) {
/* 188 */         throw new IORuntimeException("Dest is a sub directory of src !");
/*     */       }
/*     */       
/* 191 */       File subTarget = this.isCopyContentIfDir ? dest : FileUtil.mkdir(FileUtil.file(dest, src.getName()));
/* 192 */       internalCopyDirContent(src, subTarget);
/*     */     } else {
/* 194 */       internalCopyFile(src, dest);
/*     */     } 
/* 196 */     return dest;
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
/*     */   private void internalCopyDirContent(File src, File dest) throws IORuntimeException {
/* 209 */     if (null != this.copyFilter && false == this.copyFilter.accept(src)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 214 */     if (false == dest.exists()) {
/*     */ 
/*     */       
/* 217 */       dest.mkdirs();
/* 218 */     } else if (false == dest.isDirectory()) {
/* 219 */       throw new IORuntimeException(StrUtil.format("Src [{}] is a directory but dest [{}] is a file!", new Object[] { src.getPath(), dest.getPath() }));
/*     */     } 
/*     */     
/* 222 */     String[] files = src.list();
/* 223 */     if (ArrayUtil.isNotEmpty((Object[])files))
/*     */     {
/*     */       
/* 226 */       for (String file : files) {
/* 227 */         File srcFile = new File(src, file);
/* 228 */         File destFile = this.isOnlyCopyFile ? dest : new File(dest, file);
/*     */         
/* 230 */         if (srcFile.isDirectory()) {
/* 231 */           internalCopyDirContent(srcFile, destFile);
/*     */         } else {
/* 233 */           internalCopyFile(srcFile, destFile);
/*     */         } 
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
/*     */ 
/*     */   
/*     */   private void internalCopyFile(File src, File dest) throws IORuntimeException {
/* 252 */     if (null != this.copyFilter && false == this.copyFilter.accept(src)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 258 */     if (dest.exists()) {
/* 259 */       if (dest.isDirectory())
/*     */       {
/* 261 */         dest = new File(dest, src.getName());
/*     */       }
/*     */       
/* 264 */       if (dest.exists() && false == this.isOverride) {
/*     */         return;
/*     */       }
/*     */     }
/*     */     else {
/*     */       
/* 270 */       FileUtil.mkParentDirs(dest);
/*     */     } 
/*     */     
/* 273 */     ArrayList<CopyOption> optionList = new ArrayList<>(2);
/* 274 */     if (this.isOverride) {
/* 275 */       optionList.add(StandardCopyOption.REPLACE_EXISTING);
/*     */     }
/* 277 */     if (this.isCopyAttributes) {
/* 278 */       optionList.add(StandardCopyOption.COPY_ATTRIBUTES);
/*     */     }
/*     */     
/*     */     try {
/* 282 */       Files.copy(src.toPath(), dest.toPath(), optionList.<CopyOption>toArray(new CopyOption[0]));
/* 283 */     } catch (IOException e) {
/* 284 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\FileCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */