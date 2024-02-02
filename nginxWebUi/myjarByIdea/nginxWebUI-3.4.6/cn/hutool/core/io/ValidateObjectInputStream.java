package cn.hutool.core.io;

import cn.hutool.core.collection.CollUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ValidateObjectInputStream extends ObjectInputStream {
   private Set<String> whiteClassSet;
   private Set<String> blackClassSet;

   public ValidateObjectInputStream(InputStream inputStream, Class<?>... acceptClasses) throws IOException {
      super(inputStream);
      this.accept(acceptClasses);
   }

   public void refuse(Class<?>... refuseClasses) {
      if (null == this.blackClassSet) {
         this.blackClassSet = new HashSet();
      }

      Class[] var2 = refuseClasses;
      int var3 = refuseClasses.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class<?> acceptClass = var2[var4];
         this.blackClassSet.add(acceptClass.getName());
      }

   }

   public void accept(Class<?>... acceptClasses) {
      if (null == this.whiteClassSet) {
         this.whiteClassSet = new HashSet();
      }

      Class[] var2 = acceptClasses;
      int var3 = acceptClasses.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class<?> acceptClass = var2[var4];
         this.whiteClassSet.add(acceptClass.getName());
      }

   }

   protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      this.validateClassName(desc.getName());
      return super.resolveClass(desc);
   }

   private void validateClassName(String className) throws InvalidClassException {
      if (CollUtil.isNotEmpty((Collection)this.blackClassSet) && this.blackClassSet.contains(className)) {
         throw new InvalidClassException("Unauthorized deserialization attempt by black list", className);
      } else if (!CollUtil.isEmpty((Collection)this.whiteClassSet)) {
         if (!className.startsWith("java.")) {
            if (!this.whiteClassSet.contains(className)) {
               throw new InvalidClassException("Unauthorized deserialization attempt", className);
            }
         }
      }
   }
}
