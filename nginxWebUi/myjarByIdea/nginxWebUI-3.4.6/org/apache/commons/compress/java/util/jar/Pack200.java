package org.apache.commons.compress.java.util.jar;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.SortedMap;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import org.apache.commons.compress.harmony.archive.internal.nls.Messages;

public abstract class Pack200 {
   private static final String SYSTEM_PROPERTY_PACKER = "java.util.jar.Pack200.Packer";
   private static final String SYSTEM_PROPERTY_UNPACKER = "java.util.jar.Pack200.Unpacker";

   private Pack200() {
   }

   public static Packer newPacker() {
      return (Packer)AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            String className = System.getProperty("java.util.jar.Pack200.Packer", "org.apache.commons.compress.harmony.pack200.Pack200PackerAdapter");

            try {
               return ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
            } catch (Exception var3) {
               throw new Error(Messages.getString("archive.3E", (Object)className), var3);
            }
         }
      });
   }

   public static Unpacker newUnpacker() {
      return (Unpacker)AccessController.doPrivileged(new PrivilegedAction<Object>() {
         public Object run() {
            String className = System.getProperty("java.util.jar.Pack200.Unpacker", "org.apache.commons.compress.harmony.unpack200.Pack200UnpackerAdapter");

            try {
               return ClassLoader.getSystemClassLoader().loadClass(className).newInstance();
            } catch (Exception var3) {
               throw new Error(Messages.getString("archive.3E", (Object)className), var3);
            }
         }
      });
   }

   public interface Unpacker {
      String DEFLATE_HINT = "unpack.deflate.hint";
      String FALSE = "false";
      String KEEP = "keep";
      String PROGRESS = "unpack.progress";
      String TRUE = "true";

      SortedMap<String, String> properties();

      void unpack(InputStream var1, JarOutputStream var2) throws IOException;

      void unpack(File var1, JarOutputStream var2) throws IOException;

      void addPropertyChangeListener(PropertyChangeListener var1);

      void removePropertyChangeListener(PropertyChangeListener var1);
   }

   public interface Packer {
      String CLASS_ATTRIBUTE_PFX = "pack.class.attribute.";
      String CODE_ATTRIBUTE_PFX = "pack.code.attribute.";
      String DEFLATE_HINT = "pack.deflate.hint";
      String EFFORT = "pack.effort";
      String ERROR = "error";
      String FALSE = "false";
      String FIELD_ATTRIBUTE_PFX = "pack.field.attribute.";
      String KEEP = "keep";
      String KEEP_FILE_ORDER = "pack.keep.file.order";
      String LATEST = "latest";
      String METHOD_ATTRIBUTE_PFX = "pack.method.attribute.";
      String MODIFICATION_TIME = "pack.modification.time";
      String PASS = "pass";
      String PASS_FILE_PFX = "pack.pass.file.";
      String PROGRESS = "pack.progress";
      String SEGMENT_LIMIT = "pack.segment.limit";
      String STRIP = "strip";
      String TRUE = "true";
      String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute";

      SortedMap<String, String> properties();

      void pack(JarFile var1, OutputStream var2) throws IOException;

      void pack(JarInputStream var1, OutputStream var2) throws IOException;

      void addPropertyChangeListener(PropertyChangeListener var1);

      void removePropertyChangeListener(PropertyChangeListener var1);
   }
}
