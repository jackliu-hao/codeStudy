package javax.activation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDataSource implements DataSource {
   private File _file;
   private FileTypeMap typeMap;

   public FileDataSource(File file) {
      this._file = null;
      this.typeMap = null;
      this._file = file;
   }

   public FileDataSource(String name) {
      this(new File(name));
   }

   public InputStream getInputStream() throws IOException {
      return new FileInputStream(this._file);
   }

   public OutputStream getOutputStream() throws IOException {
      return new FileOutputStream(this._file);
   }

   public String getContentType() {
      return this.typeMap == null ? FileTypeMap.getDefaultFileTypeMap().getContentType(this._file) : this.typeMap.getContentType(this._file);
   }

   public String getName() {
      return this._file.getName();
   }

   public File getFile() {
      return this._file;
   }

   public void setFileTypeMap(FileTypeMap map) {
      this.typeMap = map;
   }
}
