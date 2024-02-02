package cn.hutool.extra.ftp;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Closeable;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractFtp implements Closeable {
   public static final Charset DEFAULT_CHARSET;
   protected FtpConfig ftpConfig;

   protected AbstractFtp(FtpConfig config) {
      this.ftpConfig = config;
   }

   public abstract AbstractFtp reconnectIfTimeout();

   public abstract boolean cd(String var1);

   public boolean toParent() {
      return this.cd("..");
   }

   public abstract String pwd();

   public boolean isDir(String dir) {
      return this.cd(dir);
   }

   public abstract boolean mkdir(String var1);

   public boolean exist(String path) {
      String fileName = FileUtil.getName(path);
      String dir = StrUtil.removeSuffix(path, fileName);

      List names;
      try {
         names = this.ls(dir);
      } catch (FtpException var6) {
         return false;
      }

      return containsIgnoreCase(names, fileName);
   }

   public abstract List<String> ls(String var1);

   public abstract boolean delFile(String var1);

   public abstract boolean delDir(String var1);

   public void mkDirs(String dir) {
      String[] dirs = StrUtil.trim(dir).split("[\\\\/]+");
      String now = this.pwd();
      if (dirs.length > 0 && StrUtil.isEmpty(dirs[0])) {
         this.cd("/");
      }

      String[] var4 = dirs;
      int var5 = dirs.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         if (StrUtil.isNotEmpty(s)) {
            boolean exist = true;

            try {
               if (!this.cd(s)) {
                  exist = false;
               }
            } catch (FtpException var10) {
               exist = false;
            }

            if (!exist) {
               this.mkdir(s);
               this.cd(s);
            }
         }
      }

      this.cd(now);
   }

   public abstract boolean upload(String var1, File var2);

   public abstract void download(String var1, File var2);

   public void download(String path, File outFile, String tempFileSuffix) {
      if (StrUtil.isBlank(tempFileSuffix)) {
         tempFileSuffix = ".temp";
      } else {
         tempFileSuffix = StrUtil.addPrefixIfNot(tempFileSuffix, ".");
      }

      String fileName = outFile.isDirectory() ? FileUtil.getName(path) : outFile.getName();
      String tempFileName = fileName + tempFileSuffix;
      outFile = new File(outFile.isDirectory() ? outFile : outFile.getParentFile(), tempFileName);

      try {
         this.download(path, outFile);
         FileUtil.rename(outFile, fileName, true);
      } catch (Throwable var7) {
         FileUtil.del(outFile);
         throw new FtpException(var7);
      }
   }

   public abstract void recursiveDownloadFolder(String var1, File var2);

   private static boolean containsIgnoreCase(List<String> names, String nameToFind) {
      if (CollUtil.isEmpty((Collection)names)) {
         return false;
      } else if (StrUtil.isEmpty(nameToFind)) {
         return false;
      } else {
         Iterator var2 = names.iterator();

         String name;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            name = (String)var2.next();
         } while(!nameToFind.equalsIgnoreCase(name));

         return true;
      }
   }

   static {
      DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
   }
}
