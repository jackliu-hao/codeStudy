package ch.qos.logback.core.joran.spi;

import java.util.List;

public class ElementSelector extends ElementPath {
   public ElementSelector() {
   }

   public ElementSelector(List<String> list) {
      super(list);
   }

   public ElementSelector(String p) {
      super(p);
   }

   public boolean fullPathMatch(ElementPath path) {
      if (path.size() != this.size()) {
         return false;
      } else {
         int len = this.size();

         for(int i = 0; i < len; ++i) {
            if (!this.equalityCheck(this.get(i), path.get(i))) {
               return false;
            }
         }

         return true;
      }
   }

   public int getTailMatchLength(ElementPath p) {
      if (p == null) {
         return 0;
      } else {
         int lSize = this.partList.size();
         int rSize = p.partList.size();
         if (lSize != 0 && rSize != 0) {
            int minLen = lSize <= rSize ? lSize : rSize;
            int match = 0;

            for(int i = 1; i <= minLen; ++i) {
               String l = (String)this.partList.get(lSize - i);
               String r = (String)p.partList.get(rSize - i);
               if (!this.equalityCheck(l, r)) {
                  break;
               }

               ++match;
            }

            return match;
         } else {
            return 0;
         }
      }
   }

   public boolean isContainedIn(ElementPath p) {
      return p == null ? false : p.toStableString().contains(this.toStableString());
   }

   public int getPrefixMatchLength(ElementPath p) {
      if (p == null) {
         return 0;
      } else {
         int lSize = this.partList.size();
         int rSize = p.partList.size();
         if (lSize != 0 && rSize != 0) {
            int minLen = lSize <= rSize ? lSize : rSize;
            int match = 0;

            for(int i = 0; i < minLen; ++i) {
               String l = (String)this.partList.get(i);
               String r = (String)p.partList.get(i);
               if (!this.equalityCheck(l, r)) {
                  break;
               }

               ++match;
            }

            return match;
         } else {
            return 0;
         }
      }
   }

   private boolean equalityCheck(String x, String y) {
      return x.equalsIgnoreCase(y);
   }

   public boolean equals(Object o) {
      if (o != null && o instanceof ElementSelector) {
         ElementSelector r = (ElementSelector)o;
         if (r.size() != this.size()) {
            return false;
         } else {
            int len = this.size();

            for(int i = 0; i < len; ++i) {
               if (!this.equalityCheck(this.get(i), r.get(i))) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int hc = 0;
      int len = this.size();

      for(int i = 0; i < len; ++i) {
         hc ^= this.get(i).toLowerCase().hashCode();
      }

      return hc;
   }
}
