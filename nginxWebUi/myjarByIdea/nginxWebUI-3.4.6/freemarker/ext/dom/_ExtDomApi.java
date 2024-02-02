package freemarker.ext.dom;

import freemarker.core.Environment;

public final class _ExtDomApi {
   private _ExtDomApi() {
   }

   public static boolean isXMLNameLike(String name) {
      return DomStringUtil.isXMLNameLike(name);
   }

   public static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
      return DomStringUtil.matchesName(qname, nodeName, nsURI, env);
   }
}
