package oshi.driver.unix.aix;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;

@ThreadSafe
public final class Who {
   private static final Pattern BOOT_FORMAT_AIX = Pattern.compile("\\D+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}).*");
   private static final DateTimeFormatter BOOT_DATE_FORMAT_AIX = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

   private Who() {
   }

   public static long queryBootTime() {
      String s = ExecutingCommand.getFirstAnswer("who -b");
      Matcher m = BOOT_FORMAT_AIX.matcher(s);
      if (m.matches()) {
         try {
            return LocalDateTime.parse(m.group(1) + " " + m.group(2), BOOT_DATE_FORMAT_AIX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
         } catch (NullPointerException | DateTimeParseException var3) {
         }
      }

      return 0L;
   }
}
