package cn.hutool.http.useragent;

public class UserAgentUtil {
   public static UserAgent parse(String userAgentString) {
      return UserAgentParser.parse(userAgentString);
   }
}
