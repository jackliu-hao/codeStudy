/*     */ package cn.hutool.extra.ssh;
/*     */ 
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.extra.ftp.AbstractFtp;
/*     */ import cn.hutool.extra.ftp.FtpConfig;
/*     */ import cn.hutool.extra.ftp.FtpException;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.List;
/*     */ import net.schmizz.sshj.SSHClient;
/*     */ import net.schmizz.sshj.connection.channel.direct.Session;
/*     */ import net.schmizz.sshj.sftp.RemoteResourceInfo;
/*     */ import net.schmizz.sshj.sftp.SFTPClient;
/*     */ import net.schmizz.sshj.transport.verification.HostKeyVerifier;
/*     */ import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
/*     */ import net.schmizz.sshj.xfer.FileSystemFile;
/*     */ import net.schmizz.sshj.xfer.LocalDestFile;
/*     */ import net.schmizz.sshj.xfer.LocalSourceFile;
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
/*     */ public class SshjSftp
/*     */   extends AbstractFtp
/*     */ {
/*     */   private SSHClient ssh;
/*     */   private SFTPClient sftp;
/*     */   
/*     */   public SshjSftp(String sshHost) {
/*  45 */     this(new FtpConfig(sshHost, 22, null, null, CharsetUtil.CHARSET_UTF_8));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SshjSftp(String sshHost, String sshUser, String sshPass) {
/*  56 */     this(new FtpConfig(sshHost, 22, sshUser, sshPass, CharsetUtil.CHARSET_UTF_8));
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
/*     */   public SshjSftp(String sshHost, int sshPort, String sshUser, String sshPass) {
/*  68 */     this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, CharsetUtil.CHARSET_UTF_8));
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
/*     */   public SshjSftp(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
/*  81 */     this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, charset));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected SshjSftp(FtpConfig config) {
/*  91 */     super(config);
/*  92 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init() {
/* 102 */     this.ssh = new SSHClient();
/* 103 */     this.ssh.addHostKeyVerifier((HostKeyVerifier)new PromiscuousVerifier());
/*     */     try {
/* 105 */       this.ssh.connect(this.ftpConfig.getHost(), this.ftpConfig.getPort());
/* 106 */       this.ssh.authPassword(this.ftpConfig.getUser(), this.ftpConfig.getPassword());
/* 107 */       this.ssh.setRemoteCharset(this.ftpConfig.getCharset());
/* 108 */       this.sftp = this.ssh.newSFTPClient();
/* 109 */     } catch (IOException e) {
/* 110 */       throw new FtpException("sftp 初始化失败.", e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public AbstractFtp reconnectIfTimeout() {
/* 116 */     if (StrUtil.isBlank(this.ftpConfig.getHost())) {
/* 117 */       throw new FtpException("Host is blank!");
/*     */     }
/*     */     try {
/* 120 */       cd("/");
/* 121 */     } catch (FtpException e) {
/* 122 */       close();
/* 123 */       init();
/*     */     } 
/* 125 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean cd(String directory) {
/* 130 */     String exec = String.format("cd %s", new Object[] { directory });
/* 131 */     command(exec);
/* 132 */     String pwd = pwd();
/* 133 */     return pwd.equals(directory);
/*     */   }
/*     */ 
/*     */   
/*     */   public String pwd() {
/* 138 */     return command("pwd");
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean mkdir(String dir) {
/*     */     try {
/* 144 */       this.sftp.mkdir(dir);
/* 145 */     } catch (IOException e) {
/* 146 */       throw new FtpException(e);
/*     */     } 
/* 148 */     return containsFile(dir);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<String> ls(String path) {
/*     */     List<RemoteResourceInfo> infoList;
/*     */     try {
/* 155 */       infoList = this.sftp.ls(path);
/* 156 */     } catch (IOException e) {
/* 157 */       throw new FtpException(e);
/*     */     } 
/* 159 */     if (CollUtil.isNotEmpty(infoList)) {
/* 160 */       return CollUtil.map(infoList, RemoteResourceInfo::getName, true);
/*     */     }
/* 162 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean delFile(String path) {
/*     */     try {
/* 168 */       this.sftp.rm(path);
/* 169 */       return !containsFile(path);
/* 170 */     } catch (IOException e) {
/* 171 */       throw new FtpException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean delDir(String dirPath) {
/*     */     try {
/* 178 */       this.sftp.rmdir(dirPath);
/* 179 */       return !containsFile(dirPath);
/* 180 */     } catch (IOException e) {
/* 181 */       throw new FtpException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean upload(String destPath, File file) {
/*     */     try {
/* 188 */       this.sftp.put((LocalSourceFile)new FileSystemFile(file), destPath);
/* 189 */       return containsFile(destPath);
/* 190 */     } catch (IOException e) {
/* 191 */       throw new FtpException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void download(String path, File outFile) {
/*     */     try {
/* 198 */       this.sftp.get(path, (LocalDestFile)new FileSystemFile(outFile));
/* 199 */     } catch (IOException e) {
/* 200 */       throw new FtpException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void recursiveDownloadFolder(String sourcePath, File destDir) {
/* 206 */     List<String> files = ls(sourcePath);
/* 207 */     if (files != null && !files.isEmpty()) {
/* 208 */       files.forEach(path -> download(sourcePath + "/" + path, destDir));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/*     */     try {
/* 215 */       this.sftp.close();
/* 216 */       this.ssh.disconnect();
/* 217 */     } catch (IOException e) {
/* 218 */       throw new FtpException(e);
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
/*     */   public boolean containsFile(String fileDir) {
/*     */     try {
/* 232 */       this.sftp.lstat(fileDir);
/* 233 */       return true;
/* 234 */     } catch (IOException e) {
/* 235 */       return false;
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
/*     */   public String command(String exec) {
/* 249 */     Session session = null;
/*     */     try {
/* 251 */       session = this.ssh.startSession();
/* 252 */       Session.Command command = session.exec(exec);
/* 253 */       InputStream inputStream = command.getInputStream();
/* 254 */       return IoUtil.read(inputStream, DEFAULT_CHARSET);
/* 255 */     } catch (Exception e) {
/* 256 */       throw new FtpException(e);
/*     */     } finally {
/* 258 */       IoUtil.close((Closeable)session);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\SshjSftp.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */