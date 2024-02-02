package oshi.hardware.platform.linux;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import oshi.annotation.concurrent.Immutable;
import oshi.driver.linux.Dmidecode;
import oshi.driver.linux.Sysfs;
import oshi.hardware.common.AbstractFirmware;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@Immutable
final class LinuxFirmware extends AbstractFirmware {
   private static final DateTimeFormatter VCGEN_FORMATTER;
   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
   private final Supplier<String> description = Memoizer.memoize(this::queryDescription);
   private final Supplier<String> version = Memoizer.memoize(this::queryVersion);
   private final Supplier<String> releaseDate = Memoizer.memoize(this::queryReleaseDate);
   private final Supplier<String> name = Memoizer.memoize(this::queryName);
   private final Supplier<VcGenCmdStrings> vcGenCmd = Memoizer.memoize(LinuxFirmware::queryVcGenCmd);
   private final Supplier<Pair<String, String>> biosNameRev = Memoizer.memoize(Dmidecode::queryBiosNameRev);

   public String getManufacturer() {
      return (String)this.manufacturer.get();
   }

   public String getDescription() {
      return (String)this.description.get();
   }

   public String getVersion() {
      return (String)this.version.get();
   }

   public String getReleaseDate() {
      return (String)this.releaseDate.get();
   }

   public String getName() {
      return (String)this.name.get();
   }

   private String queryManufacturer() {
      String result = null;
      return (result = Sysfs.queryBiosVendor()) == null && (result = ((VcGenCmdStrings)this.vcGenCmd.get()).manufacturer) == null ? "unknown" : result;
   }

   private String queryDescription() {
      String result = null;
      return (result = Sysfs.queryBiosDescription()) == null && (result = ((VcGenCmdStrings)this.vcGenCmd.get()).description) == null ? "unknown" : result;
   }

   private String queryVersion() {
      String result = null;
      return (result = Sysfs.queryBiosVersion((String)((Pair)this.biosNameRev.get()).getB())) == null && (result = ((VcGenCmdStrings)this.vcGenCmd.get()).version) == null ? "unknown" : result;
   }

   private String queryReleaseDate() {
      String result = null;
      return (result = Sysfs.queryBiosReleaseDate()) == null && (result = ((VcGenCmdStrings)this.vcGenCmd.get()).releaseDate) == null ? "unknown" : result;
   }

   private String queryName() {
      String result = null;
      return (result = (String)((Pair)this.biosNameRev.get()).getA()) == null && (result = ((VcGenCmdStrings)this.vcGenCmd.get()).name) == null ? "unknown" : result;
   }

   private static VcGenCmdStrings queryVcGenCmd() {
      String vcReleaseDate = null;
      String vcManufacturer = null;
      String vcVersion = null;
      List<String> vcgencmd = ExecutingCommand.runNative("vcgencmd version");
      if (vcgencmd.size() >= 3) {
         try {
            vcReleaseDate = DateTimeFormatter.ISO_LOCAL_DATE.format(VCGEN_FORMATTER.parse((CharSequence)vcgencmd.get(0)));
         } catch (DateTimeParseException var5) {
            vcReleaseDate = "unknown";
         }

         String[] copyright = ParseUtil.whitespaces.split((CharSequence)vcgencmd.get(1));
         vcManufacturer = copyright[copyright.length - 1];
         vcVersion = ((String)vcgencmd.get(2)).replace("version ", "");
         return new VcGenCmdStrings(vcReleaseDate, vcManufacturer, vcVersion, "RPi", "Bootloader");
      } else {
         return new VcGenCmdStrings((String)null, (String)null, (String)null, (String)null, (String)null);
      }
   }

   static {
      VCGEN_FORMATTER = DateTimeFormatter.ofPattern("MMM d uuuu HH:mm:ss", Locale.ENGLISH);
   }

   private static final class VcGenCmdStrings {
      private final String releaseDate;
      private final String manufacturer;
      private final String version;
      private final String name;
      private final String description;

      private VcGenCmdStrings(String releaseDate, String manufacturer, String version, String name, String description) {
         this.releaseDate = releaseDate;
         this.manufacturer = manufacturer;
         this.version = version;
         this.name = name;
         this.description = description;
      }

      // $FF: synthetic method
      VcGenCmdStrings(String x0, String x1, String x2, String x3, String x4, Object x5) {
         this(x0, x1, x2, x3, x4);
      }
   }
}
