package freemarker.ext.jython;

import freemarker.template.utility.StringUtil;
import org.python.core.PySystemState;

class JythonVersionAdapterHolder {
   static final JythonVersionAdapter INSTANCE;

   private static RuntimeException adapterCreationException(Exception e) {
      return new RuntimeException("Unexpected exception when creating JythonVersionAdapter", e);
   }

   static {
      int version;
      try {
         version = StringUtil.versionStringToInt(PySystemState.class.getField("version").get((Object)null).toString());
      } catch (Exception var4) {
         throw new RuntimeException("Failed to get Jython version: " + var4);
      }

      ClassLoader cl = JythonVersionAdapter.class.getClassLoader();

      try {
         if (version >= 2005000) {
            INSTANCE = (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython25VersionAdapter").newInstance();
         } else if (version >= 2002000) {
            INSTANCE = (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython22VersionAdapter").newInstance();
         } else {
            INSTANCE = (JythonVersionAdapter)cl.loadClass("freemarker.ext.jython._Jython20And21VersionAdapter").newInstance();
         }

      } catch (IllegalAccessException | InstantiationException | ClassNotFoundException var3) {
         throw adapterCreationException(var3);
      }
   }
}
