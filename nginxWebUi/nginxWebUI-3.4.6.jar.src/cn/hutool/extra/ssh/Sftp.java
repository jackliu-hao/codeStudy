/*     */ package cn.hutool.extra.ssh;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.lang.Filter;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.ftp.AbstractFtp;
/*     */ import cn.hutool.extra.ftp.FtpConfig;
/*     */ import cn.hutool.extra.ftp.FtpException;
/*     */ import com.jcraft.jsch.Channel;
/*     */ import com.jcraft.jsch.ChannelSftp;
/*     */ import com.jcraft.jsch.Session;
/*     */ import com.jcraft.jsch.SftpATTRS;
/*     */ import com.jcraft.jsch.SftpException;
/*     */ import com.jcraft.jsch.SftpProgressMonitor;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Vector;
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
/*     */ public class Sftp
/*     */   extends AbstractFtp
/*     */ {
/*     */   private Session session;
/*     */   private ChannelSftp channel;
/*     */   
/*     */   public Sftp(String sshHost, int sshPort, String sshUser, String sshPass) {
/*  56 */     this(sshHost, sshPort, sshUser, sshPass, DEFAULT_CHARSET);
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
/*     */   public Sftp(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
/*  70 */     this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sftp(FtpConfig config) {
/*  80 */     super(config);
/*  81 */     init(config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sftp(Session session) {
/*  90 */     this(session, DEFAULT_CHARSET);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sftp(Session session, Charset charset) {
/* 101 */     super(FtpConfig.create().setCharset(charset));
/* 102 */     init(session, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sftp(ChannelSftp channel, Charset charset) {
/* 112 */     super(FtpConfig.create().setCharset(charset));
/* 113 */     init(channel, charset);
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
/*     */   public void init(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
/* 127 */     init(JschUtil.getSession(sshHost, sshPort, sshUser, sshPass), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init() {
/* 136 */     init(this.ftpConfig);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(FtpConfig config) {
/* 146 */     init(config.getHost(), config.getPort(), config.getUser(), config.getPassword(), config.getCharset());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(Session session, Charset charset) {
/* 156 */     this.session = session;
/* 157 */     init(JschUtil.openSftp(session, (int)this.ftpConfig.getConnectionTimeout()), charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(ChannelSftp channel, Charset charset) {
/* 167 */     this.ftpConfig.setCharset(charset);
/*     */     try {
/* 169 */       channel.setFilenameEncoding(charset.toString());
/* 170 */     } catch (SftpException e) {
/* 171 */       throw new JschRuntimeException(e);
/*     */     } 
/* 173 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */   
/*     */   public Sftp reconnectIfTimeout() {
/* 178 */     if (StrUtil.isBlank(this.ftpConfig.getHost())) {
/* 179 */       throw new FtpException("Host is blank!");
/*     */     }
/*     */     try {
/* 182 */       cd("/");
/* 183 */     } catch (FtpException e) {
/* 184 */       close();
/* 185 */       init();
/*     */     } 
/* 187 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelSftp getClient() {
/* 197 */     return this.channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String pwd() {
/*     */     try {
/* 208 */       return this.channel.pwd();
/* 209 */     } catch (SftpException e) {
/* 210 */       throw new JschRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String home() {
/*     */     try {
/* 222 */       return this.channel.getHome();
/* 223 */     } catch (SftpException e) {
/* 224 */       throw new JschRuntimeException(e);
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
/*     */   public List<String> ls(String path) {
/* 237 */     return ls(path, (Filter<ChannelSftp.LsEntry>)null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> lsDirs(String path) {
/* 248 */     return ls(path, t -> t.getAttrs().isDir());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<String> lsFiles(String path) {
/* 259 */     return ls(path, t -> (false == t.getAttrs().isDir()));
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
/*     */   public List<String> ls(String path, Filter<ChannelSftp.LsEntry> filter) {
/* 272 */     List<ChannelSftp.LsEntry> entries = lsEntries(path, filter);
/* 273 */     if (CollUtil.isEmpty(entries)) {
/* 274 */       return ListUtil.empty();
/*     */     }
/* 276 */     return CollUtil.map(entries, ChannelSftp.LsEntry::getFilename, true);
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
/*     */   public List<ChannelSftp.LsEntry> lsEntries(String path) {
/* 288 */     return lsEntries(path, (Filter<ChannelSftp.LsEntry>)null);
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
/*     */   public List<ChannelSftp.LsEntry> lsEntries(String path, Filter<ChannelSftp.LsEntry> filter) {
/* 301 */     List<ChannelSftp.LsEntry> entryList = new ArrayList<>();
/*     */     try {
/* 303 */       this.channel.ls(path, entry -> {
/*     */             String fileName = entry.getFilename();
/*     */             
/*     */             if (false == StrUtil.equals(".", fileName) && false == StrUtil.equals("..", fileName) && (null == filter || filter.accept(entry))) {
/*     */               entryList.add(entry);
/*     */             }
/*     */             
/*     */             return 0;
/*     */           });
/* 312 */     } catch (SftpException e) {
/* 313 */       if (false == StrUtil.startWithIgnoreCase(e.getMessage(), "No such file")) {
/* 314 */         throw new JschRuntimeException(e);
/*     */       }
/*     */     } 
/*     */     
/* 318 */     return entryList;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean mkdir(String dir) {
/* 323 */     if (isDir(dir))
/*     */     {
/* 325 */       return true;
/*     */     }
/*     */     try {
/* 328 */       this.channel.mkdir(dir);
/* 329 */       return true;
/* 330 */     } catch (SftpException e) {
/* 331 */       throw new JschRuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDir(String dir) {
/*     */     SftpATTRS sftpATTRS;
/*     */     try {
/* 339 */       sftpATTRS = this.channel.stat(dir);
/* 340 */     } catch (SftpException e) {
/* 341 */       String msg = e.getMessage();
/*     */       
/* 343 */       if (StrUtil.containsAnyIgnoreCase(msg, new CharSequence[] { "No such file", "does not exist"
/*     */           }))
/*     */       {
/* 346 */         return false;
/*     */       }
/* 348 */       throw new FtpException(e);
/*     */     } 
/* 350 */     return sftpATTRS.isDir();
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
/*     */   public synchronized boolean cd(String directory) throws FtpException {
/* 362 */     if (StrUtil.isBlank(directory))
/*     */     {
/* 364 */       return true;
/*     */     }
/*     */     try {
/* 367 */       this.channel.cd(directory.replace('\\', '/'));
/* 368 */       return true;
/* 369 */     } catch (SftpException e) {
/* 370 */       throw new FtpException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean delFile(String filePath) {
/*     */     try {
/* 382 */       this.channel.rm(filePath);
/* 383 */     } catch (SftpException e) {
/* 384 */       throw new JschRuntimeException(e);
/*     */     } 
/* 386 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean delDir(String dirPath) {
/*     */     Vector<ChannelSftp.LsEntry> list;
/* 398 */     if (false == cd(dirPath)) {
/* 399 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 404 */       list = this.channel.ls(this.channel.pwd());
/* 405 */     } catch (SftpException e) {
/* 406 */       throw new JschRuntimeException(e);
/*     */     } 
/*     */ 
/*     */     
/* 410 */     for (ChannelSftp.LsEntry entry : list) {
/* 411 */       String fileName = entry.getFilename();
/* 412 */       if (false == ".".equals(fileName) && false == "..".equals(fileName)) {
/* 413 */         if (entry.getAttrs().isDir()) {
/* 414 */           delDir(fileName); continue;
/*     */         } 
/* 416 */         delFile(fileName);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 421 */     if (false == cd("..")) {
/* 422 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 427 */       this.channel.rmdir(dirPath);
/* 428 */       return true;
/* 429 */     } catch (SftpException e) {
/* 430 */       throw new JschRuntimeException(e);
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
/*     */   public void syncUpload(File file, String remotePath) {
/* 442 */     if (false == FileUtil.exist(file)) {
/*     */       return;
/*     */     }
/* 445 */     if (file.isDirectory()) {
/* 446 */       File[] files = file.listFiles();
/* 447 */       if (files == null) {
/*     */         return;
/*     */       }
/* 450 */       for (File fileItem : files) {
/* 451 */         if (fileItem.isDirectory()) {
/* 452 */           String mkdir = FileUtil.normalize(remotePath + "/" + fileItem.getName());
/* 453 */           syncUpload(fileItem, mkdir);
/*     */         } else {
/* 455 */           syncUpload(fileItem, remotePath);
/*     */         } 
/*     */       } 
/*     */     } else {
/* 459 */       mkDirs(remotePath);
/* 460 */       upload(remotePath, file);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean upload(String destPath, File file) {
/* 466 */     put(FileUtil.getAbsolutePath(file), destPath);
/* 467 */     return true;
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
/*     */   public boolean upload(String destPath, String fileName, InputStream fileStream) {
/* 486 */     destPath = StrUtil.addSuffixIfNot(destPath, "/") + StrUtil.removePrefix(fileName, "/");
/* 487 */     put(fileStream, destPath, (SftpProgressMonitor)null, Mode.OVERWRITE);
/* 488 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Sftp put(String srcFilePath, String destPath) {
/* 499 */     return put(srcFilePath, destPath, Mode.OVERWRITE);
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
/*     */   public Sftp put(String srcFilePath, String destPath, Mode mode) {
/* 511 */     return put(srcFilePath, destPath, (SftpProgressMonitor)null, mode);
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
/*     */   public Sftp put(String srcFilePath, String destPath, SftpProgressMonitor monitor, Mode mode) {
/*     */     try {
/* 526 */       this.channel.put(srcFilePath, destPath, monitor, mode.ordinal());
/* 527 */     } catch (SftpException e) {
/* 528 */       throw new JschRuntimeException(e);
/*     */     } 
/* 530 */     return this;
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
/*     */   public Sftp put(InputStream srcStream, String destPath, SftpProgressMonitor monitor, Mode mode) {
/*     */     try {
/* 545 */       this.channel.put(srcStream, destPath, monitor, mode.ordinal());
/* 546 */     } catch (SftpException e) {
/* 547 */       throw new JschRuntimeException(e);
/*     */     } 
/* 549 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void download(String src, File destFile) {
/* 554 */     get(src, FileUtil.getAbsolutePath(destFile));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void download(String src, OutputStream out) {
/* 565 */     get(src, out);
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
/*     */   public void recursiveDownloadFolder(String sourcePath, File destDir) throws JschRuntimeException {
/* 579 */     for (ChannelSftp.LsEntry item : lsEntries(sourcePath)) {
/* 580 */       String fileName = item.getFilename();
/* 581 */       String srcFile = StrUtil.format("{}/{}", new Object[] { sourcePath, fileName });
/* 582 */       File destFile = FileUtil.file(destDir, fileName);
/*     */       
/* 584 */       if (false == item.getAttrs().isDir()) {
/*     */         
/* 586 */         if (false == FileUtil.exist(destFile) || item
/* 587 */           .getAttrs().getMTime() > destFile.lastModified() / 1000L) {
/* 588 */           download(srcFile, destFile);
/*     */         }
/*     */         continue;
/*     */       } 
/* 592 */       FileUtil.mkdir(destFile);
/* 593 */       recursiveDownloadFolder(srcFile, destFile);
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
/*     */   public Sftp get(String src, String dest) {
/*     */     try {
/* 608 */       this.channel.get(src, dest);
/* 609 */     } catch (SftpException e) {
/* 610 */       throw new JschRuntimeException(e);
/*     */     } 
/* 612 */     return this;
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
/*     */   public Sftp get(String src, OutputStream out) {
/*     */     try {
/* 625 */       this.channel.get(src, out);
/* 626 */     } catch (SftpException e) {
/* 627 */       throw new JschRuntimeException(e);
/*     */     } 
/* 629 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 634 */     JschUtil.close((Channel)this.channel);
/* 635 */     JschUtil.close(this.session);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 640 */     return "Sftp{host='" + this.ftpConfig
/* 641 */       .getHost() + '\'' + ", port=" + this.ftpConfig
/* 642 */       .getPort() + ", user='" + this.ftpConfig
/* 643 */       .getUser() + '\'' + '}';
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
/*     */   public enum Mode
/*     */   {
/* 656 */     OVERWRITE,
/*     */ 
/*     */ 
/*     */     
/* 660 */     RESUME,
/*     */ 
/*     */ 
/*     */     
/* 664 */     APPEND;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\Sftp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */