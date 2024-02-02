/*     */ package cn.hutool.core.io.file;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.PrintWriter;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Map;
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
/*     */ public class FileWriter
/*     */   extends FileWrapper
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static FileWriter create(File file, Charset charset) {
/*  38 */     return new FileWriter(file, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileWriter create(File file) {
/*  48 */     return new FileWriter(file);
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
/*     */   public FileWriter(File file, Charset charset) {
/*  60 */     super(file, charset);
/*  61 */     checkFile();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileWriter(File file, String charset) {
/*  71 */     this(file, CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileWriter(String filePath, Charset charset) {
/*  81 */     this(FileUtil.file(filePath), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileWriter(String filePath, String charset) {
/*  91 */     this(FileUtil.file(filePath), CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileWriter(File file) {
/* 101 */     this(file, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileWriter(String filePath) {
/* 111 */     this(filePath, DEFAULT_CHARSET);
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
/*     */   public File write(String content, boolean isAppend) throws IORuntimeException {
/* 124 */     BufferedWriter writer = null;
/*     */     try {
/* 126 */       writer = getWriter(isAppend);
/* 127 */       writer.write(content);
/* 128 */       writer.flush();
/* 129 */     } catch (IOException e) {
/* 130 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 132 */       IoUtil.close(writer);
/*     */     } 
/* 134 */     return this.file;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File write(String content) throws IORuntimeException {
/* 145 */     return write(content, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File append(String content) throws IORuntimeException {
/* 156 */     return write(content, true);
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
/*     */   public <T> File writeLines(Iterable<T> list) throws IORuntimeException {
/* 168 */     return writeLines(list, false);
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
/*     */   public <T> File appendLines(Iterable<T> list) throws IORuntimeException {
/* 180 */     return writeLines(list, true);
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
/*     */   public <T> File writeLines(Iterable<T> list, boolean isAppend) throws IORuntimeException {
/* 193 */     return writeLines(list, (LineSeparator)null, isAppend);
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
/*     */   public <T> File writeLines(Iterable<T> list, LineSeparator lineSeparator, boolean isAppend) throws IORuntimeException {
/* 208 */     try (PrintWriter writer = getPrintWriter(isAppend)) {
/* 209 */       boolean isFirst = true;
/* 210 */       for (T t : list) {
/* 211 */         if (null != t) {
/* 212 */           if (isFirst) {
/* 213 */             isFirst = false;
/* 214 */             if (isAppend && FileUtil.isNotEmpty(this.file))
/*     */             {
/* 216 */               printNewLine(writer, lineSeparator);
/*     */             }
/*     */           } else {
/* 219 */             printNewLine(writer, lineSeparator);
/*     */           } 
/* 221 */           writer.print(t);
/*     */           
/* 223 */           writer.flush();
/*     */         } 
/*     */       } 
/*     */     } 
/* 227 */     return this.file;
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
/*     */   public File writeMap(Map<?, ?> map, String kvSeparator, boolean isAppend) throws IORuntimeException {
/* 241 */     return writeMap(map, (LineSeparator)null, kvSeparator, isAppend);
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
/*     */   public File writeMap(Map<?, ?> map, LineSeparator lineSeparator, String kvSeparator, boolean isAppend) throws IORuntimeException {
/* 256 */     if (null == kvSeparator) {
/* 257 */       kvSeparator = " = ";
/*     */     }
/* 259 */     try (PrintWriter writer = getPrintWriter(isAppend)) {
/* 260 */       for (Map.Entry<?, ?> entry : map.entrySet()) {
/* 261 */         if (null != entry) {
/* 262 */           writer.print(StrUtil.format("{}{}{}", new Object[] { entry.getKey(), kvSeparator, entry.getValue() }));
/* 263 */           printNewLine(writer, lineSeparator);
/* 264 */           writer.flush();
/*     */         } 
/*     */       } 
/*     */     } 
/* 268 */     return this.file;
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
/*     */   public File write(byte[] data, int off, int len) throws IORuntimeException {
/* 281 */     return write(data, off, len, false);
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
/*     */   public File append(byte[] data, int off, int len) throws IORuntimeException {
/* 294 */     return write(data, off, len, true);
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
/*     */   public File write(byte[] data, int off, int len, boolean isAppend) throws IORuntimeException {
/* 308 */     try (FileOutputStream out = new FileOutputStream(FileUtil.touch(this.file), isAppend)) {
/* 309 */       out.write(data, off, len);
/* 310 */       out.flush();
/* 311 */     } catch (IOException e) {
/* 312 */       throw new IORuntimeException(e);
/*     */     } 
/* 314 */     return this.file;
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
/*     */   public File writeFromStream(InputStream in) throws IORuntimeException {
/* 326 */     return writeFromStream(in, true);
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
/*     */   public File writeFromStream(InputStream in, boolean isCloseIn) throws IORuntimeException {
/* 339 */     FileOutputStream out = null;
/*     */     try {
/* 341 */       out = new FileOutputStream(FileUtil.touch(this.file));
/* 342 */       IoUtil.copy(in, out);
/* 343 */     } catch (IOException e) {
/* 344 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 346 */       IoUtil.close(out);
/* 347 */       if (isCloseIn) {
/* 348 */         IoUtil.close(in);
/*     */       }
/*     */     } 
/* 351 */     return this.file;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedOutputStream getOutputStream() throws IORuntimeException {
/*     */     try {
/* 362 */       return new BufferedOutputStream(new FileOutputStream(FileUtil.touch(this.file)));
/* 363 */     } catch (IOException e) {
/* 364 */       throw new IORuntimeException(e);
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
/*     */   public BufferedWriter getWriter(boolean isAppend) throws IORuntimeException {
/*     */     try {
/* 377 */       return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileUtil.touch(this.file), isAppend), this.charset));
/* 378 */     } catch (Exception e) {
/* 379 */       throw new IORuntimeException(e);
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
/*     */   public PrintWriter getPrintWriter(boolean isAppend) throws IORuntimeException {
/* 391 */     return new PrintWriter(getWriter(isAppend));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkFile() throws IORuntimeException {
/* 400 */     Assert.notNull(this.file, "File to write content is null !", new Object[0]);
/* 401 */     if (this.file.exists() && false == this.file.isFile()) {
/* 402 */       throw new IORuntimeException("File [{}] is not a file !", new Object[] { this.file.getAbsoluteFile() });
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
/*     */   private void printNewLine(PrintWriter writer, LineSeparator lineSeparator) {
/* 414 */     if (null == lineSeparator) {
/*     */       
/* 416 */       writer.println();
/*     */     } else {
/*     */       
/* 419 */       writer.print(lineSeparator.getValue());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\FileWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */