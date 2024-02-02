package freemarker.ext.dom;

import freemarker.core.Environment;

final class DomStringUtil {
   private DomStringUtil() {
   }

   static boolean isXMLNameLike(String name) {
      return isXMLNameLike(name, 0);
   }

   static boolean isXMLNameLike(String name, int firstCharIdx) {
      int ln = name.length();

      for(int i = firstCharIdx; i < ln; ++i) {
         char c = name.charAt(i);
         if (i == firstCharIdx && (c == '-' || c == '.' || Character.isDigit(c))) {
            return false;
         }

         if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '.') {
            if (c != ':') {
               return false;
            }

            if (i + 1 < ln && name.charAt(i + 1) == ':') {
               return false;
            }
         }
      }

      return true;
   }

   static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
      String defaultNS = env.getDefaultNS();
      if (defaultNS != null && defaultNS.equals(nsURI)) {
         return qname.equals(nodeName) || qname.equals("D:" + nodeName);
      } else if ("".equals(nsURI)) {
         if (defaultNS != null) {
            return qname.equals("N:" + nodeName);
         } else {
            return qname.equals(nodeName) || qname.equals("N:" + nodeName);
         }
      } else {
         String prefix = env.getPrefixForNamespace(nsURI);
         return prefix == null ? false : qname.equals(prefix + ":" + nodeName);
      }
   }
}
