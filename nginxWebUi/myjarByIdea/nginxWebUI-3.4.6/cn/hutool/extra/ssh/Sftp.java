package cn.hutool.extra.ssh;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.ftp.AbstractFtp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class Sftp extends AbstractFtp {
   private Session session;
   private ChannelSftp channel;

   public Sftp(String sshHost, int sshPort, String sshUser, String sshPass) {
      this(sshHost, sshPort, sshUser, sshPass, DEFAULT_CHARSET);
   }

   public Sftp(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
      this(new FtpConfig(sshHost, sshPort, sshUser, sshPass, charset));
   }

   public Sftp(FtpConfig config) {
      super(config);
      this.init(config);
   }

   public Sftp(Session session) {
      this(session, DEFAULT_CHARSET);
   }

   public Sftp(Session session, Charset charset) {
      super(FtpConfig.create().setCharset(charset));
      this.init(session, charset);
   }

   public Sftp(ChannelSftp channel, Charset charset) {
      super(FtpConfig.create().setCharset(charset));
      this.init(channel, charset);
   }

   public void init(String sshHost, int sshPort, String sshUser, String sshPass, Charset charset) {
      this.init(JschUtil.getSession(sshHost, sshPort, sshUser, sshPass), charset);
   }

   public void init() {
      this.init(this.ftpConfig);
   }

   public void init(FtpConfig config) {
      this.init(config.getHost(), config.getPort(), config.getUser(), config.getPassword(), config.getCharset());
   }

   public void init(Session session, Charset charset) {
      this.session = session;
      this.init(JschUtil.openSftp(session, (int)this.ftpConfig.getConnectionTimeout()), charset);
   }

   public void init(ChannelSftp channel, Charset charset) {
      this.ftpConfig.setCharset(charset);

      try {
         channel.setFilenameEncoding(charset.toString());
      } catch (SftpException var4) {
         throw new JschRuntimeException(var4);
      }

      this.channel = channel;
   }

   public Sftp reconnectIfTimeout() {
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

   public ChannelSftp getClient() {
      return this.channel;
   }

   public String pwd() {
      try {
         return this.channel.pwd();
      } catch (SftpException var2) {
         throw new JschRuntimeException(var2);
      }
   }

   public String home() {
      try {
         return this.channel.getHome();
      } catch (SftpException var2) {
         throw new JschRuntimeException(var2);
      }
   }

   public List<String> ls(String path) {
      return this.ls(path, (Filter)null);
   }

   public List<String> lsDirs(String path) {
      return this.ls(path, (t) -> {
         return t.getAttrs().isDir();
      });
   }

   public List<String> lsFiles(String path) {
      return this.ls(path, (t) -> {
         return !t.getAttrs().isDir();
      });
   }

   public List<String> ls(String path, Filter<ChannelSftp.LsEntry> filter) {
      List<ChannelSftp.LsEntry> entries = this.lsEntries(path, filter);
      return CollUtil.isEmpty((Collection)entries) ? ListUtil.empty() : CollUtil.map(entries, ChannelSftp.LsEntry::getFilename, true);
   }

   public List<ChannelSftp.LsEntry> lsEntries(String path) {
      return this.lsEntries(path, (Filter)null);
   }

   public List<ChannelSftp.LsEntry> lsEntries(String path, Filter<ChannelSftp.LsEntry> filter) {
      List<ChannelSftp.LsEntry> entryList = new ArrayList();

      try {
         this.channel.ls(path, (entry) -> {
            String fileName = entry.getFilename();
            if (!StrUtil.equals(".", fileName) && !StrUtil.equals("..", fileName) && (null == filter || filter.accept(entry))) {
               entryList.add(entry);
            }

            return 0;
         });
      } catch (SftpException var5) {
         if (!StrUtil.startWithIgnoreCase(var5.getMessage(), "No such file")) {
            throw new JschRuntimeException(var5);
         }
      }

      return entryList;
   }

   public boolean mkdir(String dir) {
      if (this.isDir(dir)) {
         return true;
      } else {
         try {
            this.channel.mkdir(dir);
            return true;
         } catch (SftpException var3) {
            throw new JschRuntimeException(var3);
         }
      }
   }

   public boolean isDir(String dir) {
      SftpATTRS sftpATTRS;
      try {
         sftpATTRS = this.channel.stat(dir);
      } catch (SftpException var5) {
         String msg = var5.getMessage();
         if (StrUtil.containsAnyIgnoreCase(msg, new CharSequence[]{"No such file", "does not exist"})) {
            return false;
         }

         throw new FtpException(var5);
      }

      return sftpATTRS.isDir();
   }

   public synchronized boolean cd(String directory) throws FtpException {
      if (StrUtil.isBlank(directory)) {
         return true;
      } else {
         try {
            this.channel.cd(directory.replace('\\', '/'));
            return true;
         } catch (SftpException var3) {
            throw new FtpException(var3);
         }
      }
   }

   public boolean delFile(String filePath) {
      try {
         this.channel.rm(filePath);
         return true;
      } catch (SftpException var3) {
         throw new JschRuntimeException(var3);
      }
   }

   public boolean delDir(String dirPath) {
      if (!this.cd(dirPath)) {
         return false;
      } else {
         Vector list;
         try {
            list = this.channel.ls(this.channel.pwd());
         } catch (SftpException var7) {
            throw new JschRuntimeException(var7);
         }

         Iterator var4 = list.iterator();

         while(var4.hasNext()) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry)var4.next();
            String fileName = entry.getFilename();
            if (!".".equals(fileName) && !"..".equals(fileName)) {
               if (entry.getAttrs().isDir()) {
                  this.delDir(fileName);
               } else {
                  this.delFile(fileName);
               }
            }
         }

         if (!this.cd("..")) {
            return false;
         } else {
            try {
               this.channel.rmdir(dirPath);
               return true;
            } catch (SftpException var6) {
               throw new JschRuntimeException(var6);
            }
         }
      }
   }

   public void syncUpload(File file, String remotePath) {
      if (FileUtil.exist(file)) {
         if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
               return;
            }

            File[] var4 = files;
            int var5 = files.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               File fileItem = var4[var6];
               if (fileItem.isDirectory()) {
                  String mkdir = FileUtil.normalize(remotePath + "/" + fileItem.getName());
                  this.syncUpload(fileItem, mkdir);
               } else {
                  this.syncUpload(fileItem, remotePath);
               }
            }
         } else {
            this.mkDirs(remotePath);
            this.upload(remotePath, file);
         }

      }
   }

   public boolean upload(String destPath, File file) {
      this.put(FileUtil.getAbsolutePath(file), destPath);
      return true;
   }

   public boolean upload(String destPath, String fileName, InputStream fileStream) {
      destPath = StrUtil.addSuffixIfNot(destPath, "/") + StrUtil.removePrefix(fileName, "/");
      this.put((InputStream)fileStream, destPath, (SftpProgressMonitor)null, Sftp.Mode.OVERWRITE);
      return true;
   }

   public Sftp put(String srcFilePath, String destPath) {
      return this.put(srcFilePath, destPath, Sftp.Mode.OVERWRITE);
   }

   public Sftp put(String srcFilePath, String destPath, Mode mode) {
      return this.put((String)srcFilePath, destPath, (SftpProgressMonitor)null, mode);
   }

   public Sftp put(String srcFilePath, String destPath, SftpProgressMonitor monitor, Mode mode) {
      try {
         this.channel.put(srcFilePath, destPath, monitor, mode.ordinal());
         return this;
      } catch (SftpException var6) {
         throw new JschRuntimeException(var6);
      }
   }

   public Sftp put(InputStream srcStream, String destPath, SftpProgressMonitor monitor, Mode mode) {
      try {
         this.channel.put(srcStream, destPath, monitor, mode.ordinal());
         return this;
      } catch (SftpException var6) {
         throw new JschRuntimeException(var6);
      }
   }

   public void download(String src, File destFile) {
      this.get(src, FileUtil.getAbsolutePath(destFile));
   }

   public void download(String src, OutputStream out) {
      this.get(src, out);
   }

   public void recursiveDownloadFolder(String sourcePath, File destDir) throws JschRuntimeException {
      Iterator var6 = this.lsEntries(sourcePath).iterator();

      while(true) {
         String srcFile;
         File destFile;
         ChannelSftp.LsEntry item;
         label24:
         do {
            while(var6.hasNext()) {
               item = (ChannelSftp.LsEntry)var6.next();
               String fileName = item.getFilename();
               srcFile = StrUtil.format("{}/{}", new Object[]{sourcePath, fileName});
               destFile = FileUtil.file(destDir, fileName);
               if (!item.getAttrs().isDir()) {
                  continue label24;
               }

               FileUtil.mkdir(destFile);
               this.recursiveDownloadFolder(srcFile, destFile);
            }

            return;
         } while(FileUtil.exist(destFile) && (long)item.getAttrs().getMTime() <= destFile.lastModified() / 1000L);

         this.download(srcFile, destFile);
      }
   }

   public Sftp get(String src, String dest) {
      try {
         this.channel.get(src, dest);
         return this;
      } catch (SftpException var4) {
         throw new JschRuntimeException(var4);
      }
   }

   public Sftp get(String src, OutputStream out) {
      try {
         this.channel.get(src, out);
         return this;
      } catch (SftpException var4) {
         throw new JschRuntimeException(var4);
      }
   }

   public void close() {
      JschUtil.close((Channel)this.channel);
      JschUtil.close(this.session);
   }

   public String toString() {
      return "Sftp{host='" + this.ftpConfig.getHost() + '\'' + ", port=" + this.ftpConfig.getPort() + ", user='" + this.ftpConfig.getUser() + '\'' + '}';
   }

   public static enum Mode {
      OVERWRITE,
      RESUME,
      APPEND;
   }
}
