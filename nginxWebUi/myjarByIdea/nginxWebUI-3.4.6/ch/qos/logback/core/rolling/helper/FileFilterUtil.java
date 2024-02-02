package ch.qos.logback.core.rolling.helper;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFilterUtil {
   public static void sortFileArrayByName(File[] fileArray) {
      Arrays.sort(fileArray, new Comparator<File>() {
         public int compare(File o1, File o2) {
            String o1Name = o1.getName();
            String o2Name = o2.getName();
            return o1Name.compareTo(o2Name);
         }
      });
   }

   public static void reverseSortFileArrayByName(File[] fileArray) {
      Arrays.sort(fileArray, new Comparator<File>() {
         public int compare(File o1, File o2) {
            String o1Name = o1.getName();
            String o2Name = o2.getName();
            return o2Name.compareTo(o1Name);
         }
      });
   }

   public static String afterLastSlash(String sregex) {
      int i = sregex.lastIndexOf(47);
      return i == -1 ? sregex : sregex.substring(i + 1);
   }

   public static boolean isEmptyDirectory(File dir) {
      if (!dir.isDirectory()) {
         throw new IllegalArgumentException("[" + dir + "] must be a directory");
      } else {
         String[] filesInDir = dir.list();
         return filesInDir == null || filesInDir.length == 0;
      }
   }

   public static File[] filesInFolderMatchingStemRegex(File file, final String stemRegex) {
      if (file == null) {
         return new File[0];
      } else {
         return file.exists() && file.isDirectory() ? file.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
               return name.matches(stemRegex);
            }
         }) : new File[0];
      }
   }

   public static int findHighestCounter(File[] matchingFileArray, String stemRegex) {
      int max = Integer.MIN_VALUE;
      File[] var3 = matchingFileArray;
      int var4 = matchingFileArray.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         File aFile = var3[var5];
         int aCounter = extractCounter(aFile, stemRegex);
         if (max < aCounter) {
            max = aCounter;
         }
      }

      return max;
   }

   public static int extractCounter(File file, String stemRegex) {
      Pattern p = Pattern.compile(stemRegex);
      String lastFileName = file.getName();
      Matcher m = p.matcher(lastFileName);
      if (!m.matches()) {
         throw new IllegalStateException("The regex [" + stemRegex + "] should match [" + lastFileName + "]");
      } else {
         String counterAsStr = m.group(1);
         return new Integer(counterAsStr);
      }
   }

   public static String slashify(String in) {
      return in.replace('\\', '/');
   }

   public static void removeEmptyParentDirectories(File file, int recursivityCount) {
      if (recursivityCount < 3) {
         File parent = file.getParentFile();
         if (parent.isDirectory() && isEmptyDirectory(parent)) {
            parent.delete();
            removeEmptyParentDirectories(parent, recursivityCount + 1);
         }

      }
   }
}
