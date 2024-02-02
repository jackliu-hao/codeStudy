package org.codehaus.plexus.util;

import java.io.File;
import java.util.StringTokenizer;

public class PathTool {
   public static final String getRelativePath(String basedir, String filename) {
      basedir = uppercaseDrive(basedir);
      filename = uppercaseDrive(filename);
      if (basedir != null && basedir.length() != 0 && filename != null && filename.length() != 0 && filename.startsWith(basedir)) {
         String separator = determineSeparator(filename);
         basedir = StringUtils.chompLast(basedir, separator);
         filename = StringUtils.chompLast(filename, separator);
         String relativeFilename = filename.substring(basedir.length());
         return determineRelativePath(relativeFilename, separator);
      } else {
         return "";
      }
   }

   public static final String getRelativePath(String filename) {
      filename = uppercaseDrive(filename);
      if (filename != null && filename.length() != 0) {
         String separator = determineSeparator(filename);
         filename = StringUtils.chompLast(filename, separator);
         if (!filename.startsWith(separator)) {
            filename = separator + filename;
         }

         return determineRelativePath(filename, separator);
      } else {
         return "";
      }
   }

   public static final String getDirectoryComponent(String filename) {
      if (filename != null && filename.length() != 0) {
         String separator = determineSeparator(filename);
         String directory = StringUtils.chomp(filename, separator);
         return filename.equals(directory) ? "." : directory;
      } else {
         return "";
      }
   }

   public static final String calculateLink(String link, String relativePath) {
      if (link.startsWith("/site/")) {
         return link.substring(5);
      } else if (link.startsWith("/absolute/")) {
         return link.substring(10);
      } else if (link.indexOf(":") >= 0) {
         return link;
      } else if (relativePath.equals(".")) {
         return link.startsWith("/") ? link.substring(1) : link;
      } else if (relativePath.endsWith("/") && link.startsWith("/")) {
         return relativePath + "." + link.substring(1);
      } else {
         return !relativePath.endsWith("/") && !link.startsWith("/") ? relativePath + "/" + link : relativePath + link;
      }
   }

   public static final String getRelativeWebPath(String oldPath, String newPath) {
      if (!StringUtils.isEmpty(oldPath) && !StringUtils.isEmpty(newPath)) {
         String resultPath = buildRelativePath(newPath, oldPath, '/');
         return newPath.endsWith("/") && !resultPath.endsWith("/") ? resultPath + "/" : resultPath;
      } else {
         return "";
      }
   }

   public static final String getRelativeFilePath(String oldPath, String newPath) {
      if (!StringUtils.isEmpty(oldPath) && !StringUtils.isEmpty(newPath)) {
         String fromPath = (new File(oldPath)).getPath();
         String toPath = (new File(newPath)).getPath();
         if (toPath.matches("^\\[a-zA-Z]:")) {
            toPath = toPath.substring(1);
         }

         if (fromPath.matches("^\\[a-zA-Z]:")) {
            fromPath = fromPath.substring(1);
         }

         if (fromPath.startsWith(":", 1)) {
            fromPath = Character.toLowerCase(fromPath.charAt(0)) + fromPath.substring(1);
         }

         if (toPath.startsWith(":", 1)) {
            toPath = Character.toLowerCase(toPath.charAt(0)) + toPath.substring(1);
         }

         if (toPath.startsWith(":", 1) && fromPath.startsWith(":", 1) && !toPath.substring(0, 1).equals(fromPath.substring(0, 1))) {
            return null;
         } else if ((!toPath.startsWith(":", 1) || fromPath.startsWith(":", 1)) && (toPath.startsWith(":", 1) || !fromPath.startsWith(":", 1))) {
            String resultPath = buildRelativePath(toPath, fromPath, File.separatorChar);
            return newPath.endsWith(File.separator) && !resultPath.endsWith(File.separator) ? resultPath + File.separator : resultPath;
         } else {
            return null;
         }
      } else {
         return "";
      }
   }

   private static final String determineRelativePath(String filename, String separator) {
      if (filename.length() == 0) {
         return "";
      } else {
         int slashCount = StringUtils.countMatches(filename, separator) - 1;
         if (slashCount <= 0) {
            return ".";
         } else {
            StringBuffer sb = new StringBuffer();

            for(int i = 0; i < slashCount; ++i) {
               sb.append("../");
            }

            return StringUtils.chop(sb.toString());
         }
      }
   }

   private static final String determineSeparator(String filename) {
      int forwardCount = StringUtils.countMatches(filename, "/");
      int backwardCount = StringUtils.countMatches(filename, "\\");
      return forwardCount >= backwardCount ? "/" : "\\";
   }

   static final String uppercaseDrive(String path) {
      if (path == null) {
         return null;
      } else {
         if (path.length() >= 2 && path.charAt(1) == ':') {
            path = Character.toUpperCase(path.charAt(0)) + path.substring(1);
         }

         return path;
      }
   }

   private static final String buildRelativePath(String toPath, String fromPath, char separatorChar) {
      StringTokenizer toTokeniser = new StringTokenizer(toPath, String.valueOf(separatorChar));
      StringTokenizer fromTokeniser = new StringTokenizer(fromPath, String.valueOf(separatorChar));

      int count;
      for(count = 0; toTokeniser.hasMoreTokens() && fromTokeniser.hasMoreTokens(); ++count) {
         if (separatorChar == '\\') {
            if (!fromTokeniser.nextToken().equalsIgnoreCase(toTokeniser.nextToken())) {
               break;
            }
         } else if (!fromTokeniser.nextToken().equals(toTokeniser.nextToken())) {
            break;
         }
      }

      toTokeniser = new StringTokenizer(toPath, String.valueOf(separatorChar));
      fromTokeniser = new StringTokenizer(fromPath, String.valueOf(separatorChar));

      while(count-- > 0) {
         fromTokeniser.nextToken();
         toTokeniser.nextToken();
      }

      String relativePath = "";

      while(fromTokeniser.hasMoreTokens()) {
         fromTokeniser.nextToken();
         relativePath = relativePath + "..";
         if (fromTokeniser.hasMoreTokens()) {
            relativePath = relativePath + separatorChar;
         }
      }

      if (relativePath.length() != 0 && toTokeniser.hasMoreTokens()) {
         relativePath = relativePath + separatorChar;
      }

      while(toTokeniser.hasMoreTokens()) {
         relativePath = relativePath + toTokeniser.nextToken();
         if (toTokeniser.hasMoreTokens()) {
            relativePath = relativePath + separatorChar;
         }
      }

      return relativePath;
   }
}
