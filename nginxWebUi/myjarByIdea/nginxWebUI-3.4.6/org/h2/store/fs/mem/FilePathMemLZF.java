package org.h2.store.fs.mem;

public class FilePathMemLZF extends FilePathMem {
   public FilePathMem getPath(String var1) {
      FilePathMemLZF var2 = new FilePathMemLZF();
      var2.name = getCanonicalPath(var1);
      return var2;
   }

   boolean compressed() {
      return true;
   }

   public String getScheme() {
      return "memLZF";
   }
}
