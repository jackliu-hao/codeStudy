package freemarker.core;

import freemarker.template.Configuration;
import freemarker.template.Version;
import freemarker.template.utility.DateUtil;

/** @deprecated */
@Deprecated
public class CommandLine {
   public static void main(String[] args) {
      Version ver = Configuration.getVersion();
      System.out.println();
      System.out.print("Apache FreeMarker version ");
      System.out.print(ver);
      if (!ver.toString().endsWith("Z") && ver.getBuildDate() != null) {
         System.out.print(" (built on ");
         System.out.print(DateUtil.dateToISO8601String(ver.getBuildDate(), true, true, true, 6, DateUtil.UTC, new DateUtil.TrivialDateToISO8601CalendarFactory()));
         System.out.print(")");
      }

      System.out.println();
      if (ver.isGAECompliant() != null) {
         System.out.print("Google App Engine complian variant: ");
         System.out.println(ver.isGAECompliant() ? "Yes" : "No");
      }

   }
}
