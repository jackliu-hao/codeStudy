package cn.hutool.http.useragent;

import cn.hutool.core.util.ReUtil;
import java.io.Serializable;
import java.util.regex.Pattern;

public class UserAgentInfo implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final String NameUnknown = "Unknown";
   private final String name;
   private final Pattern pattern;

   public UserAgentInfo(String name, String regex) {
      this(name, null == regex ? null : Pattern.compile(regex, 2));
   }

   public UserAgentInfo(String name, Pattern pattern) {
      this.name = name;
      this.pattern = pattern;
   }

   public String getName() {
      return this.name;
   }

   public Pattern getPattern() {
      return this.pattern;
   }

   public boolean isMatch(String content) {
      return ReUtil.contains((Pattern)this.pattern, content);
   }

   public boolean isUnknown() {
      return "Unknown".equals(this.name);
   }

   public int hashCode() {
      int prime = true;
      int result = 1;
      result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
      return result;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         UserAgentInfo other = (UserAgentInfo)obj;
         if (this.name == null) {
            return other.name == null;
         } else {
            return this.name.equals(other.name);
         }
      }
   }

   public String toString() {
      return this.name;
   }
}
