package org.h2.store.fs.niomem;

public class FilePathNioMemLZF extends FilePathNioMem {
   boolean compressed() {
      return true;
   }

   public FilePathNioMem getPath(String var1) {
      if (!var1.startsWith(this.getScheme())) {
         throw new IllegalArgumentException(var1 + " doesn't start with " + this.getScheme());
      } else {
         int var2 = var1.indexOf(58);
         int var3 = var1.lastIndexOf(58);
         FilePathNioMemLZF var4 = new FilePathNioMemLZF();
         if (var2 != -1 && var2 != var3) {
            var4.compressLaterCachePercent = Float.parseFloat(var1.substring(var2 + 1, var3));
         }

         var4.name = getCanonicalPath(var1);
         return var4;
      }
   }

   protected boolean isRoot() {
      return this.name.lastIndexOf(58) == this.name.length() - 1;
   }

   public String getScheme() {
      return "nioMemLZF";
   }
}
