package ch.qos.logback.classic.pattern;

public class ClassNameOnlyAbbreviator implements Abbreviator {
   public String abbreviate(String fqClassName) {
      int lastIndex = fqClassName.lastIndexOf(46);
      return lastIndex != -1 ? fqClassName.substring(lastIndex + 1, fqClassName.length()) : fqClassName;
   }
}
