package org.jboss.threads;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ThreadNameInfo {
   private final long globalThreadSequenceNum;
   private final long perFactoryThreadSequenceNum;
   private final long factorySequenceNum;
   private static final Pattern searchPattern = Pattern.compile("([^%]+)|%.");

   ThreadNameInfo(long globalThreadSequenceNum, long perFactoryThreadSequenceNum, long factorySequenceNum) {
      this.globalThreadSequenceNum = globalThreadSequenceNum;
      this.perFactoryThreadSequenceNum = perFactoryThreadSequenceNum;
      this.factorySequenceNum = factorySequenceNum;
   }

   public long getGlobalThreadSequenceNum() {
      return this.globalThreadSequenceNum;
   }

   public long getPerFactoryThreadSequenceNum() {
      return this.perFactoryThreadSequenceNum;
   }

   public long getFactorySequenceNum() {
      return this.factorySequenceNum;
   }

   public String format(Thread thread, String formatString) {
      StringBuilder builder = new StringBuilder(formatString.length() * 5);
      ThreadGroup group = thread.getThreadGroup();
      Matcher matcher = searchPattern.matcher(formatString);

      while(matcher.find()) {
         if (matcher.group(1) != null) {
            builder.append(matcher.group());
         } else {
            switch (matcher.group().charAt(1)) {
               case '%':
                  builder.append('%');
                  break;
               case 'G':
                  if (group != null) {
                     builder.append(group.getName());
                  }
                  break;
               case 'f':
                  builder.append(this.factorySequenceNum);
                  break;
               case 'g':
                  builder.append(this.globalThreadSequenceNum);
                  break;
               case 'i':
                  builder.append(thread.getId());
                  break;
               case 'p':
                  if (group != null) {
                     appendGroupPath(group, builder);
                  }
                  break;
               case 't':
                  builder.append(this.perFactoryThreadSequenceNum);
            }
         }
      }

      return builder.toString();
   }

   private static void appendGroupPath(ThreadGroup group, StringBuilder builder) {
      ThreadGroup parent = group.getParent();
      if (parent != null) {
         appendGroupPath(parent, builder);
         builder.append(':');
      }

      builder.append(group.getName());
   }
}
