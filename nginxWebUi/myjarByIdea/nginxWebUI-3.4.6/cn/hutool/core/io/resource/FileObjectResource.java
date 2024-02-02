package cn.hutool.core.io.resource;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import javax.tools.FileObject;

public class FileObjectResource implements Resource {
   private final FileObject fileObject;

   public FileObjectResource(FileObject fileObject) {
      this.fileObject = fileObject;
   }

   public FileObject getFileObject() {
      return this.fileObject;
   }

   public String getName() {
      return this.fileObject.getName();
   }

   public URL getUrl() {
      try {
         return this.fileObject.toUri().toURL();
      } catch (MalformedURLException var2) {
         return null;
      }
   }

   public InputStream getStream() {
      try {
         return this.fileObject.openInputStream();
      } catch (IOException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public BufferedReader getReader(Charset charset) {
      try {
         return IoUtil.getReader(this.fileObject.openReader(false));
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }
}
