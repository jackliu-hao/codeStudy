package cn.hutool.extra.ssh;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.xfer.FileSystemFile;

public class SshjSftp extends AbstractFtp {
   private SSHClient ssh;
   private SFTPClient sftp;

   public SshjSftp(String sshHost) {
      this(new FtpConfig(sshHost, 22, (String)null, (String)null, CharsetUtil.CHARSET_UTF_8));
   }

   public SshjSftp(String sshHost, String sshUser, String sshPass) {
      this(new FtpConfig(sshHost, 22, sshUser, sshPass, CharsetUtil.CHARSET_UTF_8));
   }

   public SshjSftp(String sshHost, int sshPort, String sshUser, String sshPass) {
      this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, CharsetUtil.CHARSET_UTF_8));
   }

   public SshjSftp(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
      this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, charset));
   }

   protected SshjSftp(FtpConfig config) {
      super(config);
      this.init();
   }

   public void init() {
      this.ssh = new SSHClient();
      this.ssh.addHostKeyVerifier(new PromiscuousVerifier());

      try {
         this.ssh.connect(this.ftpConfig.getHost(), this.ftpConfig.getPort());
         this.ssh.authPassword(this.ftpConfig.getUser(), this.ftpConfig.getPassword());
         this.ssh.setRemoteCharset(this.ftpConfig.getCharset());
         this.sftp = this.ssh.newSFTPClient();
      } catch (IOException var2) {
         throw new FtpException("sftp 初始化失败.", var2);
      }
   }

   public AbstractFtp reconnectIfTimeout() {
      if (StrUtil.isBlank(this.ftpConfig.getHost())) {
         throw new FtpException("Host is blank!");
      } else {
         try {
            this.cd("/");
         } catch (FtpException var2) {
            this.close();
            this.init();
         }

         return this;
      }
   }

   public boolean cd(String directory) {
      String exec = String.format("cd %s", directory);
      this.command(exec);
      String pwd = this.pwd();
      return pwd.equals(directory);
   }

   public String pwd() {
      return this.command("pwd");
   }

   public boolean mkdir(String dir) {
      try {
         this.sftp.mkdir(dir);
      } catch (IOException var3) {
         throw new FtpException(var3);
      }

      return this.containsFile(dir);
   }

   public List<String> ls(String path) {
      List infoList;
      try {
         infoList = this.sftp.ls(path);
      } catch (IOException var4) {
         throw new FtpException(var4);
      }

      return CollUtil.isNotEmpty((Collection)infoList) ? CollUtil.map(infoList, RemoteResourceInfo::getName, true) : null;
   }

   public boolean delFile(String path) {
      try {
         this.sftp.rm(path);
         return !this.containsFile(path);
      } catch (IOException var3) {
         throw new FtpException(var3);
      }
   }

   public boolean delDir(String dirPath) {
      try {
         this.sftp.rmdir(dirPath);
         return !this.containsFile(dirPath);
      } catch (IOException var3) {
         throw new FtpException(var3);
      }
   }

   public boolean upload(String destPath, File file) {
      try {
         this.sftp.put(new FileSystemFile(file), destPath);
         return this.containsFile(destPath);
      } catch (IOException var4) {
         throw new FtpException(var4);
      }
   }

   public void download(String path, File outFile) {
      try {
         this.sftp.get(path, new FileSystemFile(outFile));
      } catch (IOException var4) {
         throw new FtpException(var4);
      }
   }

   public void recursiveDownloadFolder(String sourcePath, File destDir) {
      List<String> files = this.ls(sourcePath);
      if (files != null && !files.isEmpty()) {
         files.forEach((path) -> {
            this.download(sourcePath + "/" + path, destDir);
         });
      }

   }

   public void close() {
      try {
         this.sftp.close();
         this.ssh.disconnect();
      } catch (IOException var2) {
         throw new FtpException(var2);
      }
   }

   public boolean containsFile(String fileDir) {
      try {
         this.sftp.lstat(fileDir);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public String command(String exec) {
      Session session = null;

      String var5;
      try {
         session = this.ssh.startSession();
         Session.Command command = session.exec(exec);
         InputStream inputStream = command.getInputStream();
         var5 = IoUtil.read(inputStream, DEFAULT_CHARSET);
      } catch (Exception var9) {
         throw new FtpException(var9);
      } finally {
         IoUtil.close(session);
      }

      return var5;
   }
}
