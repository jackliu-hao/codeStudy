package cn.hutool.extra.ftp;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class Ftp extends AbstractFtp {
   public static final int DEFAULT_PORT = 21;
   private FTPClient client;
   private FtpMode mode;
   private boolean backToPwd;

   public Ftp(String host) {
      this(host, 21);
   }

   public Ftp(String host, int port) {
      this(host, port, "anonymous", "");
   }

   public Ftp(String host, int port, String user, String password) {
      this(host, port, user, password, CharsetUtil.CHARSET_UTF_8);
   }

   public Ftp(String host, int port, String user, String password, Charset charset) {
      this(host, port, user, password, charset, (String)null, (String)null);
   }

   public Ftp(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey) {
      this(host, port, user, password, charset, serverLanguageCode, systemKey, (FtpMode)null);
   }

   public Ftp(String host, int port, String user, String password, Charset charset, String serverLanguageCode, String systemKey, FtpMode mode) {
      this(new FtpConfig(host, port, user, password, charset, serverLanguageCode, systemKey), mode);
   }

   public Ftp(FtpConfig config, FtpMode mode) {
      super(config);
      this.mode = mode;
      this.init();
   }

   public Ftp(FTPClient client) {
      super(FtpConfig.create());
      this.client = client;
   }

   public Ftp init() {
      return this.init(this.ftpConfig, this.mode);
   }

   public Ftp init(String host, int port, String user, String password) {
      return this.init(host, port, user, password, (FtpMode)null);
   }

   public Ftp init(String host, int port, String user, String password, FtpMode mode) {
      return this.init(new FtpConfig(host, port, user, password, this.ftpConfig.getCharset(), (String)null, (String)null), mode);
   }

   public Ftp init(FtpConfig config, FtpMode mode) {
      FTPClient client = new FTPClient();
      client.setRemoteVerificationEnabled(false);
      Charset charset = config.getCharset();
      if (null != charset) {
         client.setControlEncoding(charset.toString());
      }

      client.setConnectTimeout((int)config.getConnectionTimeout());
      String systemKey = config.getSystemKey();
      if (StrUtil.isNotBlank(systemKey)) {
         FTPClientConfig conf = new FTPClientConfig(systemKey);
         String serverLanguageCode = config.getServerLanguageCode();
         if (StrUtil.isNotBlank(serverLanguageCode)) {
            conf.setServerLanguageCode(config.getServerLanguageCode());
         }

         client.configure(conf);
      }

      try {
         client.connect(config.getHost(), config.getPort());
         client.setSoTimeout((int)config.getSoTimeout());
         client.login(config.getUser(), config.getPassword());
      } catch (IOException var9) {
         throw new IORuntimeException(var9);
      }

      int replyCode = client.getReplyCode();
      if (!FTPReply.isPositiveCompletion(replyCode)) {
         try {
            client.disconnect();
         } catch (IOException var8) {
         }

         throw new FtpException("Login failed for user [{}], reply code is: [{}]", new Object[]{config.getUser(), replyCode});
      } else {
         this.client = client;
         if (mode != null) {
            this.setMode(mode);
         }

         return this;
      }
   }

   public Ftp setMode(FtpMode mode) {
      this.mode = mode;
      switch (mode) {
         case Active:
            this.client.enterLocalActiveMode();
            break;
         case Passive:
            this.client.enterLocalPassiveMode();
      }

      return this;
   }

   public Ftp setBackToPwd(boolean backToPwd) {
      this.backToPwd = backToPwd;
      return this;
   }

   public boolean isBackToPwd() {
      return this.backToPwd;
   }

   public Ftp reconnectIfTimeout() {
      String pwd = null;

      try {
         pwd = this.pwd();
      } catch (IORuntimeException var3) {
      }

      return pwd == null ? this.init() : this;
   }

   public synchronized boolean cd(String directory) {
      if (StrUtil.isBlank(directory)) {
         return true;
      } else {
         try {
            return this.client.changeWorkingDirectory(directory);
         } catch (IOException var3) {
            throw new IORuntimeException(var3);
         }
      }
   }

   public String pwd() {
      try {
         return this.client.printWorkingDirectory();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public List<String> ls(String path) {
      return ArrayUtil.map(this.lsFiles(path), FTPFile::getName);
   }

   public List<FTPFile> lsFiles(String path, Filter<FTPFile> filter) {
      FTPFile[] ftpFiles = this.lsFiles(path);
      if (ArrayUtil.isEmpty((Object[])ftpFiles)) {
         return ListUtil.empty();
      } else {
         List<FTPFile> result = new ArrayList(ftpFiles.length - 2 <= 0 ? ftpFiles.length : ftpFiles.length - 2);
         FTPFile[] var6 = ftpFiles;
         int var7 = ftpFiles.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            FTPFile ftpFile = var6[var8];
            String fileName = ftpFile.getName();
            if (!StrUtil.equals(".", fileName) && !StrUtil.equals("..", fileName) && (null == filter || filter.accept(ftpFile))) {
               result.add(ftpFile);
            }
         }

         return result;
      }
   }

   public FTPFile[] lsFiles(String path) throws FtpException, IORuntimeException {
      String pwd = null;
      if (StrUtil.isNotBlank(path)) {
         pwd = this.pwd();
         if (!this.isDir(path)) {
            throw new FtpException("Change dir to [{}] error, maybe path not exist!", new Object[]{path});
         }
      }

      FTPFile[] ftpFiles;
      try {
         ftpFiles = this.client.listFiles();
      } catch (IOException var8) {
         throw new IORuntimeException(var8);
      } finally {
         this.cd(pwd);
      }

      return ftpFiles;
   }

   public boolean mkdir(String dir) throws IORuntimeException {
      try {
         return this.client.makeDirectory(dir);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public int stat(String path) throws IORuntimeException {
      try {
         return this.client.stat(path);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public boolean existFile(String path) throws IORuntimeException {
      FTPFile[] ftpFileArr;
      try {
         ftpFileArr = this.client.listFiles(path);
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }

      return ArrayUtil.isNotEmpty((Object[])ftpFileArr);
   }

   public boolean delFile(String path) throws IORuntimeException {
      String pwd = this.pwd();
      String fileName = FileUtil.getName(path);
      String dir = StrUtil.removeSuffix(path, fileName);
      if (!this.isDir(dir)) {
         throw new FtpException("Change dir to [{}] error, maybe dir not exist!", new Object[]{path});
      } else {
         boolean isSuccess;
         try {
            isSuccess = this.client.deleteFile(fileName);
         } catch (IOException var10) {
            throw new IORuntimeException(var10);
         } finally {
            this.cd(pwd);
         }

         return isSuccess;
      }
   }

   public boolean delDir(String dirPath) throws IORuntimeException {
      FTPFile[] dirs;
      try {
         dirs = this.client.listFiles(dirPath);
      } catch (IOException var10) {
         throw new IORuntimeException(var10);
      }

      FTPFile[] var5 = dirs;
      int var6 = dirs.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         FTPFile ftpFile = var5[var7];
         String name = ftpFile.getName();
         String childPath = StrUtil.format("{}/{}", new Object[]{dirPath, name});
         if (ftpFile.isDirectory()) {
            if (!".".equals(name) && !"..".equals(name)) {
               this.delDir(childPath);
            }
         } else {
            this.delFile(childPath);
         }
      }

      try {
         return this.client.removeDirectory(dirPath);
      } catch (IOException var9) {
         throw new IORuntimeException(var9);
      }
   }

   public boolean upload(String destPath, File file) {
      Assert.notNull(file, "file to upload is null !");
      return this.upload(destPath, file.getName(), file);
   }

   public boolean upload(String destPath, String fileName, File file) throws IORuntimeException {
      try {
         InputStream in = FileUtil.getInputStream(file);
         Throwable var5 = null;

         boolean var6;
         try {
            var6 = this.upload(destPath, fileName, (InputStream)in);
         } catch (Throwable var16) {
            var5 = var16;
            throw var16;
         } finally {
            if (in != null) {
               if (var5 != null) {
                  try {
                     in.close();
                  } catch (Throwable var15) {
                     var5.addSuppressed(var15);
                  }
               } else {
                  in.close();
               }
            }

         }

         return var6;
      } catch (IOException var18) {
         throw new IORuntimeException(var18);
      }
   }

   public boolean upload(String destPath, String fileName, InputStream fileStream) throws IORuntimeException {
      try {
         this.client.setFileType(2);
      } catch (IOException var11) {
         throw new IORuntimeException(var11);
      }

      String pwd = null;
      if (this.backToPwd) {
         pwd = this.pwd();
      }

      if (StrUtil.isNotBlank(destPath)) {
         this.mkDirs(destPath);
         if (!this.isDir(destPath)) {
            throw new FtpException("Change dir to [{}] error, maybe dir not exist!", new Object[]{destPath});
         }
      }

      boolean var5;
      try {
         var5 = this.client.storeFile(fileName, fileStream);
      } catch (IOException var10) {
         throw new IORuntimeException(var10);
      } finally {
         if (this.backToPwd) {
            this.cd(pwd);
         }

      }

      return var5;
   }

   public void download(String path, File outFile) {
      String fileName = FileUtil.getName(path);
      String dir = StrUtil.removeSuffix(path, fileName);
      this.download(dir, fileName, outFile);
   }

   public void recursiveDownloadFolder(String sourcePath, File destDir) {
      Iterator var6 = this.lsFiles(sourcePath, (Filter)null).iterator();

      while(true) {
         String srcFile;
         File destFile;
         FTPFile ftpFile;
         label24:
         do {
            while(var6.hasNext()) {
               ftpFile = (FTPFile)var6.next();
               String fileName = ftpFile.getName();
               srcFile = StrUtil.format("{}/{}", new Object[]{sourcePath, fileName});
               destFile = FileUtil.file(destDir, fileName);
               if (!ftpFile.isDirectory()) {
                  continue label24;
               }

               FileUtil.mkdir(destFile);
               this.recursiveDownloadFolder(srcFile, destFile);
            }

            return;
         } while(FileUtil.exist(destFile) && ftpFile.getTimestamp().getTimeInMillis() <= destFile.lastModified());

         this.download(srcFile, destFile);
      }
   }

   public void download(String path, String fileName, File outFile) throws IORuntimeException {
      if (outFile.isDirectory()) {
         outFile = new File(outFile, fileName);
      }

      if (!outFile.exists()) {
         FileUtil.touch(outFile);
      }

      try {
         OutputStream out = FileUtil.getOutputStream(outFile);
         Throwable var5 = null;

         try {
            this.download(path, fileName, (OutputStream)out);
         } catch (Throwable var15) {
            var5 = var15;
            throw var15;
         } finally {
            if (out != null) {
               if (var5 != null) {
                  try {
                     out.close();
                  } catch (Throwable var14) {
                     var5.addSuppressed(var14);
                  }
               } else {
                  out.close();
               }
            }

         }

      } catch (IOException var17) {
         throw new IORuntimeException(var17);
      }
   }

   public void download(String path, String fileName, OutputStream out) {
      this.download(path, fileName, out, (Charset)null);
   }

   public void download(String path, String fileName, OutputStream out, Charset fileNameCharset) throws IORuntimeException {
      String pwd = null;
      if (this.backToPwd) {
         pwd = this.pwd();
      }

      if (!this.isDir(path)) {
         throw new FtpException("Change dir to [{}] error, maybe dir not exist!", new Object[]{path});
      } else {
         if (null != fileNameCharset) {
            fileName = new String(fileName.getBytes(fileNameCharset), StandardCharsets.ISO_8859_1);
         }

         try {
            this.client.setFileType(2);
            this.client.retrieveFile(fileName, out);
         } catch (IOException var10) {
            throw new IORuntimeException(var10);
         } finally {
            if (this.backToPwd) {
               this.cd(pwd);
            }

         }

      }
   }

   public FTPClient getClient() {
      return this.client;
   }

   public void close() throws IOException {
      if (null != this.client) {
         this.client.logout();
         if (this.client.isConnected()) {
            this.client.disconnect();
         }

         this.client = null;
      }

   }
}
