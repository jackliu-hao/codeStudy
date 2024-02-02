package cn.hutool.core.io.resource;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

public class MultiFileResource extends MultiResource {
   private static final long serialVersionUID = 1L;

   public MultiFileResource(Collection<File> files) {
      super();
      this.add(files);
   }

   public MultiFileResource(File... files) {
      super();
      this.add(files);
   }

   public MultiFileResource add(File... files) {
      File[] var2 = files;
      int var3 = files.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         File file = var2[var4];
         this.add((Resource)(new FileResource(file)));
      }

      return this;
   }

   public MultiFileResource add(Collection<File> files) {
      Iterator var2 = files.iterator();

      while(var2.hasNext()) {
         File file = (File)var2.next();
         this.add((Resource)(new FileResource(file)));
      }

      return this;
   }

   public MultiFileResource add(Resource resource) {
      return (MultiFileResource)super.add(resource);
   }
}
