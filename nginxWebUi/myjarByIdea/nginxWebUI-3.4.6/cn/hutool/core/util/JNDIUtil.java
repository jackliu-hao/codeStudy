package cn.hutool.core.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.map.MapUtil;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class JNDIUtil {
   public static InitialDirContext createInitialDirContext(Map<String, String> environment) {
      try {
         return MapUtil.isEmpty(environment) ? new InitialDirContext() : new InitialDirContext((Hashtable)Convert.convert((Class)Hashtable.class, environment));
      } catch (NamingException var2) {
         throw new UtilException(var2);
      }
   }

   public static InitialContext createInitialContext(Map<String, String> environment) {
      try {
         return MapUtil.isEmpty(environment) ? new InitialContext() : new InitialContext((Hashtable)Convert.convert((Class)Hashtable.class, environment));
      } catch (NamingException var2) {
         throw new UtilException(var2);
      }
   }

   public static Attributes getAttributes(String uri, String... attrIds) {
      try {
         return createInitialDirContext((Map)null).getAttributes(uri, attrIds);
      } catch (NamingException var3) {
         throw new UtilException(var3);
      }
   }
}
