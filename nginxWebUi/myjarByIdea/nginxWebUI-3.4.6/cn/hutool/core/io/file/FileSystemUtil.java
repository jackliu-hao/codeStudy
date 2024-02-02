package cn.hutool.core.io.file;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class FileSystemUtil {
   public static FileSystem create(String path) {
      try {
         return FileSystems.newFileSystem(Paths.get(path).toUri(), MapUtil.of("create", "true"));
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static FileSystem createZip(String path) {
      return createZip(path, (Charset)null);
   }

   public static FileSystem createZip(String path, Charset charset) {
      if (null == charset) {
         charset = CharsetUtil.CHARSET_UTF_8;
      }

      HashMap<String, String> env = new HashMap();
      env.put("create", "true");
      env.put("encoding", charset.name());

      try {
         return FileSystems.newFileSystem(URI.create("jar:" + Paths.get(path).toUri()), env);
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public static Path getRoot(FileSystem fileSystem) {
      return fileSystem.getPath("/");
   }
}
