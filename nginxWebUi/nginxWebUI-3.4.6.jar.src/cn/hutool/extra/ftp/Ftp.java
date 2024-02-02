/*     */ package cn.hutool.extra.ftp;
/*     */ 
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.commons.net.ftp.FTPClient;
/*     */ import org.apache.commons.net.ftp.FTPClientConfig;
/*     */ import org.apache.commons.net.ftp.FTPFile;
/*     */ import org.apache.commons.net.ftp.FTPReply;
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
/*     */ public class Ftp
/*     */   extends AbstractFtp
/*     */ {
/*     */   public static final int DEFAULT_PORT = 21;
/*     */   private FTPClient client;
/*     */   private FtpMode mode;
/*     */   private boolean backToPwd;
/*     */   
/*     */   public Ftp(String host) {
/*  56 */     this(host, 21);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp(String host, int port) {
/*  66 */     this(host, port, "anonymous", "");
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
/*     */   public Ftp(String host, int port, String user, String password) {
/*  78 */     this(host, port, user, password, CharsetUtil.CHARSET_UTF_8);
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
/*     */   public Ftp(String host, int port, String user, String password, Charset charset) {
/*  91 */     this(host, port, user, password, charset, (String)null, (String)null);
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
/*     */   public Ftp(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey) {
/* 106 */     this(host, port, user, password, charset, serverLanguageCode, systemKey, (FtpMode)null);
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
/*     */   public Ftp(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey, FtpMode mode) {
/* 122 */     this(new FtpConfig(host, port, user, password, charset, serverLanguageCode, systemKey), mode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp(FtpConfig config, FtpMode mode) {
/* 132 */     super(config);
/* 133 */     this.mode = mode;
/* 134 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp(FTPClient client) {
/* 144 */     super(FtpConfig.create());
/* 145 */     this.client = client;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp init() {
/* 154 */     return init(this.ftpConfig, this.mode);
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
/*     */   public Ftp init(String host, int port, String user, String password) {
/* 167 */     return init(host, port, user, password, (FtpMode)null);
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
/*     */   public Ftp init(String host, int port, String user, String password, FtpMode mode) {
/* 181 */     return init(new FtpConfig(host, port, user, password, this.ftpConfig.getCharset(), null, null), mode);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp init(FtpConfig config, FtpMode mode) {
/* 192 */     FTPClient client = new FTPClient();
/*     */     
/* 194 */     client.setRemoteVerificationEnabled(false);
/*     */     
/* 196 */     Charset charset = config.getCharset();
/* 197 */     if (null != charset) {
/* 198 */       client.setControlEncoding(charset.toString());
/*     */     }
/* 200 */     client.setConnectTimeout((int)config.getConnectionTimeout());
/* 201 */     String systemKey = config.getSystemKey();
/* 202 */     if (StrUtil.isNotBlank(systemKey)) {
/* 203 */       FTPClientConfig conf = new FTPClientConfig(systemKey);
/* 204 */       String serverLanguageCode = config.getServerLanguageCode();
/* 205 */       if (StrUtil.isNotBlank(serverLanguageCode)) {
/* 206 */         conf.setServerLanguageCode(config.getServerLanguageCode());
/*     */       }
/* 208 */       client.configure(conf);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 214 */       client.connect(config.getHost(), config.getPort());
/* 215 */       client.setSoTimeout((int)config.getSoTimeout());
/*     */       
/* 217 */       client.login(config.getUser(), config.getPassword());
/* 218 */     } catch (IOException e) {
/* 219 */       throw new IORuntimeException(e);
/*     */     } 
/* 221 */     int replyCode = client.getReplyCode();
/* 222 */     if (false == FTPReply.isPositiveCompletion(replyCode)) {
/*     */       try {
/* 224 */         client.disconnect();
/* 225 */       } catch (IOException iOException) {}
/*     */ 
/*     */       
/* 228 */       throw new FtpException("Login failed for user [{}], reply code is: [{}]", new Object[] { config.getUser(), Integer.valueOf(replyCode) });
/*     */     } 
/* 230 */     this.client = client;
/* 231 */     if (mode != null) {
/* 232 */       setMode(mode);
/*     */     }
/* 234 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp setMode(FtpMode mode) {
/* 245 */     this.mode = mode;
/* 246 */     switch (mode) {
/*     */       case Active:
/* 248 */         this.client.enterLocalActiveMode();
/*     */         break;
/*     */       case Passive:
/* 251 */         this.client.enterLocalPassiveMode();
/*     */         break;
/*     */     } 
/* 254 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp setBackToPwd(boolean backToPwd) {
/* 265 */     this.backToPwd = backToPwd;
/* 266 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isBackToPwd() {
/* 275 */     return this.backToPwd;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Ftp reconnectIfTimeout() {
/* 285 */     String pwd = null;
/*     */     try {
/* 287 */       pwd = pwd();
/* 288 */     } catch (IORuntimeException iORuntimeException) {}
/*     */ 
/*     */ 
/*     */     
/* 292 */     if (pwd == null) {
/* 293 */       return init();
/*     */     }
/* 295 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean cd(String directory) {
/* 306 */     if (StrUtil.isBlank(directory))
/*     */     {
/* 308 */       return true;
/*     */     }
/*     */     
/*     */     try {
/* 312 */       return this.client.changeWorkingDirectory(directory);
/* 313 */     } catch (IOException e) {
/* 314 */       throw new IORuntimeException(e);
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
/*     */   public String pwd() {
/*     */     try {
/* 327 */       return this.client.printWorkingDirectory();
/* 328 */     } catch (IOException e) {
/* 329 */       throw new IORuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> ls(String path) {
/* 335 */     return ArrayUtil.map((Object[])lsFiles(path), FTPFile::getName);
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
/*     */   public List<FTPFile> lsFiles(String path, Filter<FTPFile> filter) {
/* 348 */     FTPFile[] ftpFiles = lsFiles(path);
/* 349 */     if (ArrayUtil.isEmpty((Object[])ftpFiles)) {
/* 350 */       return ListUtil.empty();
/*     */     }
/*     */     
/* 353 */     List<FTPFile> result = new ArrayList<>((ftpFiles.length - 2 <= 0) ? ftpFiles.length : (ftpFiles.length - 2));
/*     */     
/* 355 */     for (FTPFile ftpFile : ftpFiles) {
/* 356 */       String fileName = ftpFile.getName();
/* 357 */       if (false == StrUtil.equals(".", fileName) && false == StrUtil.equals("..", fileName) && (
/* 358 */         null == filter || filter.accept(ftpFile))) {
/* 359 */         result.add(ftpFile);
/*     */       }
/*     */     } 
/*     */     
/* 363 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FTPFile[] lsFiles(String path) throws FtpException, IORuntimeException {
/*     */     FTPFile[] ftpFiles;
/* 375 */     String pwd = null;
/* 376 */     if (StrUtil.isNotBlank(path)) {
/* 377 */       pwd = pwd();
/* 378 */       if (false == isDir(path)) {
/* 379 */         throw new FtpException("Change dir to [{}] error, maybe path not exist!", new Object[] { path });
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 385 */       ftpFiles = this.client.listFiles();
/* 386 */     } catch (IOException e) {
/* 387 */       throw new IORuntimeException(e);
/*     */     } finally {
/*     */       
/* 390 */       cd(pwd);
/*     */     } 
/*     */     
/* 393 */     return ftpFiles;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean mkdir(String dir) throws IORuntimeException {
/*     */     try {
/* 399 */       return this.client.makeDirectory(dir);
/* 400 */     } catch (IOException e) {
/* 401 */       throw new IORuntimeException(e);
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
/*     */   public int stat(String path) throws IORuntimeException {
/*     */     try {
/* 414 */       return this.client.stat(path);
/* 415 */     } catch (IOException e) {
/* 416 */       throw new IORuntimeException(e);
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
/*     */   public boolean existFile(String path) throws IORuntimeException {
/*     */     FTPFile[] ftpFileArr;
/*     */     try {
/* 430 */       ftpFileArr = this.client.listFiles(path);
/* 431 */     } catch (IOException e) {
/* 432 */       throw new IORuntimeException(e);
/*     */     } 
/* 434 */     return ArrayUtil.isNotEmpty((Object[])ftpFileArr);
/*     */   }
/*     */   
/*     */   public boolean delFile(String path) throws IORuntimeException {
/*     */     boolean isSuccess;
/* 439 */     String pwd = pwd();
/* 440 */     String fileName = FileUtil.getName(path);
/* 441 */     String dir = StrUtil.removeSuffix(path, fileName);
/* 442 */     if (false == isDir(dir)) {
/* 443 */       throw new FtpException("Change dir to [{}] error, maybe dir not exist!", new Object[] { path });
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 448 */       isSuccess = this.client.deleteFile(fileName);
/* 449 */     } catch (IOException e) {
/* 450 */       throw new IORuntimeException(e);
/*     */     } finally {
/*     */       
/* 453 */       cd(pwd);
/*     */     } 
/* 455 */     return isSuccess;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean delDir(String dirPath) throws IORuntimeException {
/*     */     FTPFile[] dirs;
/*     */     try {
/* 462 */       dirs = this.client.listFiles(dirPath);
/* 463 */     } catch (IOException e) {
/* 464 */       throw new IORuntimeException(e);
/*     */     } 
/*     */ 
/*     */     
/* 468 */     for (FTPFile ftpFile : dirs) {
/* 469 */       String name = ftpFile.getName();
/* 470 */       String childPath = StrUtil.format("{}/{}", new Object[] { dirPath, name });
/* 471 */       if (ftpFile.isDirectory()) {
/*     */         
/* 473 */         if (false == ".".equals(name) && false == "..".equals(name)) {
/* 474 */           delDir(childPath);
/*     */         }
/*     */       } else {
/* 477 */         delFile(childPath);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 483 */       return this.client.removeDirectory(dirPath);
/* 484 */     } catch (IOException e) {
/* 485 */       throw new IORuntimeException(e);
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
/*     */   public boolean upload(String destPath, File file) {
/* 504 */     Assert.notNull(file, "file to upload is null !", new Object[0]);
/* 505 */     return upload(destPath, file.getName(), file);
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
/*     */   public boolean upload(String destPath, String fileName, File file) throws IORuntimeException {
/* 524 */     try (InputStream in = FileUtil.getInputStream(file)) {
/* 525 */       return upload(destPath, fileName, in);
/* 526 */     } catch (IOException e) {
/* 527 */       throw new IORuntimeException(e);
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
/*     */   public boolean upload(String destPath, String fileName, InputStream fileStream) throws IORuntimeException {
/*     */     try {
/* 548 */       this.client.setFileType(2);
/* 549 */     } catch (IOException e) {
/* 550 */       throw new IORuntimeException(e);
/*     */     } 
/*     */     
/* 553 */     String pwd = null;
/* 554 */     if (this.backToPwd) {
/* 555 */       pwd = pwd();
/*     */     }
/*     */     
/* 558 */     if (StrUtil.isNotBlank(destPath)) {
/* 559 */       mkDirs(destPath);
/* 560 */       if (false == isDir(destPath)) {
/* 561 */         throw new FtpException("Change dir to [{}] error, maybe dir not exist!", new Object[] { destPath });
/*     */       }
/*     */     } 
/*     */     
/*     */     try {
/* 566 */       return this.client.storeFile(fileName, fileStream);
/* 567 */     } catch (IOException e) {
/* 568 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 570 */       if (this.backToPwd) {
/* 571 */         cd(pwd);
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
/*     */   public void download(String path, File outFile) {
/* 584 */     String fileName = FileUtil.getName(path);
/* 585 */     String dir = StrUtil.removeSuffix(path, fileName);
/* 586 */     download(dir, fileName, outFile);
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
/*     */   public void recursiveDownloadFolder(String sourcePath, File destDir) {
/* 600 */     for (FTPFile ftpFile : lsFiles(sourcePath, (Filter<FTPFile>)null)) {
/* 601 */       String fileName = ftpFile.getName();
/* 602 */       String srcFile = StrUtil.format("{}/{}", new Object[] { sourcePath, fileName });
/* 603 */       File destFile = FileUtil.file(destDir, fileName);
/*     */       
/* 605 */       if (false == ftpFile.isDirectory()) {
/*     */         
/* 607 */         if (false == FileUtil.exist(destFile) || ftpFile
/* 608 */           .getTimestamp().getTimeInMillis() > destFile.lastModified()) {
/* 609 */           download(srcFile, destFile);
/*     */         }
/*     */         continue;
/*     */       } 
/* 613 */       FileUtil.mkdir(destFile);
/* 614 */       recursiveDownloadFolder(srcFile, destFile);
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
/*     */   public void download(String path, String fileName, File outFile) throws IORuntimeException {
/* 628 */     if (outFile.isDirectory()) {
/* 629 */       outFile = new File(outFile, fileName);
/*     */     }
/* 631 */     if (false == outFile.exists()) {
/* 632 */       FileUtil.touch(outFile);
/*     */     }
/* 634 */     try (OutputStream out = FileUtil.getOutputStream(outFile)) {
/* 635 */       download(path, fileName, out);
/* 636 */     } catch (IOException e) {
/* 637 */       throw new IORuntimeException(e);
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
/*     */   public void download(String path, String fileName, OutputStream out) {
/* 649 */     download(path, fileName, out, (Charset)null);
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
/*     */   public void download(String path, String fileName, OutputStream out, Charset fileNameCharset) throws IORuntimeException {
/* 663 */     String pwd = null;
/* 664 */     if (this.backToPwd) {
/* 665 */       pwd = pwd();
/*     */     }
/*     */     
/* 668 */     if (false == isDir(path)) {
/* 669 */       throw new FtpException("Change dir to [{}] error, maybe dir not exist!", new Object[] { path });
/*     */     }
/*     */     
/* 672 */     if (null != fileNameCharset) {
/* 673 */       fileName = new String(fileName.getBytes(fileNameCharset), StandardCharsets.ISO_8859_1);
/*     */     }
/*     */     try {
/* 676 */       this.client.setFileType(2);
/* 677 */       this.client.retrieveFile(fileName, out);
/* 678 */     } catch (IOException e) {
/* 679 */       throw new IORuntimeException(e);
/*     */     } finally {
/* 681 */       if (this.backToPwd) {
/* 682 */         cd(pwd);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FTPClient getClient() {
/* 693 */     return this.client;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 698 */     if (null != this.client) {
/* 699 */       this.client.logout();
/* 700 */       if (this.client.isConnected()) {
/* 701 */         this.client.disconnect();
/*     */       }
/* 703 */       this.client = null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ftp\Ftp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */