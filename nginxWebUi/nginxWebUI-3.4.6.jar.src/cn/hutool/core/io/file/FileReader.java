/*     */ package cn.hutool.core.io.file;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.LineHandler;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class FileReader
/*     */   extends FileWrapper
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   
/*     */   public static FileReader create(File file, Charset charset) {
/*  37 */     return new FileReader(file, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FileReader create(File file) {
/*  46 */     return new FileReader(file);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileReader(File file, Charset charset) {
/*  56 */     super(file, charset);
/*  57 */     checkFile();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileReader(File file, String charset) {
/*  66 */     this(file, CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileReader(String filePath, Charset charset) {
/*  75 */     this(FileUtil.file(filePath), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileReader(String filePath, String charset) {
/*  84 */     this(FileUtil.file(filePath), CharsetUtil.charset(charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileReader(File file) {
/*  93 */     this(file, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileReader(String filePath) {
/* 102 */     this(filePath, DEFAULT_CHARSET);
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
/*     */   public byte[] readBytes() throws IORuntimeException {
/* 114 */     long len = this.file.length();
/* 115 */     if (len >= 2147483647L) {
/* 116 */       throw new IORuntimeException("File is larger then max array size");
/*     */     }
/*     */     
/* 119 */     byte[] bytes = new byte[(int)len];
/* 120 */     FileInputStream in = null;
/*     */     
/*     */     try {
/* 123 */       in = new FileInputStream(this.file);
/* 124 */       int readLength = in.read(bytes);
/* 125 */       if (readLength < len) {
/* 126 */         throw new IOException(StrUtil.format("File length is [{}] but read [{}]!", new Object[] { Long.valueOf(len), Integer.valueOf(readLength) }));
/*     */       }
/* 128 */     } catch (Exception e) {
/* 129 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 131 */       IoUtil.close(in);
/*     */     } 
/*     */     
/* 134 */     return bytes;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String readString() throws IORuntimeException {
/* 144 */     return new String(readBytes(), this.charset);
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
/*     */   public <T extends java.util.Collection<String>> T readLines(T collection) throws IORuntimeException {
/* 156 */     BufferedReader reader = null;
/*     */     try {
/* 158 */       reader = FileUtil.getReader(this.file, this.charset);
/*     */       
/*     */       while (true) {
/* 161 */         String line = reader.readLine();
/* 162 */         if (line == null) {
/*     */           break;
/*     */         }
/* 165 */         collection.add(line);
/*     */       } 
/* 167 */       return collection;
/* 168 */     } catch (IOException e) {
/* 169 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 171 */       IoUtil.close(reader);
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
/*     */   public void readLines(LineHandler lineHandler) throws IORuntimeException {
/* 183 */     BufferedReader reader = null;
/*     */     try {
/* 185 */       reader = FileUtil.getReader(this.file, this.charset);
/* 186 */       IoUtil.readLines(reader, lineHandler);
/*     */     } finally {
/* 188 */       IoUtil.close(reader);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> readLines() throws IORuntimeException {
/* 199 */     return readLines(new ArrayList<>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T read(ReaderHandler<T> readerHandler) throws IORuntimeException {
/*     */     T result;
/* 211 */     BufferedReader reader = null;
/*     */     
/*     */     try {
/* 214 */       reader = FileUtil.getReader(this.file, this.charset);
/* 215 */       result = readerHandler.handle(reader);
/* 216 */     } catch (IOException e) {
/* 217 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 219 */       IoUtil.close(reader);
/*     */     } 
/* 221 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedReader getReader() throws IORuntimeException {
/* 231 */     return IoUtil.getReader(getInputStream(), this.charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BufferedInputStream getInputStream() throws IORuntimeException {
/*     */     try {
/* 242 */       return new BufferedInputStream(new FileInputStream(this.file));
/* 243 */     } catch (IOException e) {
/* 244 */       throw new IORuntimeException(e);
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
/*     */   public long writeToStream(OutputStream out) throws IORuntimeException {
/* 256 */     return writeToStream(out, false);
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
/*     */   public long writeToStream(OutputStream out, boolean isCloseOut) throws IORuntimeException {
/* 269 */     try (FileInputStream in = new FileInputStream(this.file)) {
/* 270 */       return IoUtil.copy(in, out);
/* 271 */     } catch (IOException e) {
/* 272 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 274 */       if (isCloseOut) {
/* 275 */         IoUtil.close(out);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void checkFile() throws IORuntimeException {
/* 299 */     if (false == this.file.exists()) {
/* 300 */       throw new IORuntimeException("File not exist: " + this.file);
/*     */     }
/* 302 */     if (false == this.file.isFile())
/* 303 */       throw new IORuntimeException("Not a file:" + this.file); 
/*     */   }
/*     */   
/*     */   public static interface ReaderHandler<T> {
/*     */     T handle(BufferedReader param1BufferedReader) throws IOException;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\file\FileReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */