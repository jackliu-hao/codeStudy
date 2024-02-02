package freemarker.cache;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.utility.StringUtil;

public abstract class TemplateNameFormat {
   public static final TemplateNameFormat DEFAULT_2_3_0 = new Default020300();
   public static final TemplateNameFormat DEFAULT_2_4_0 = new Default020400();

   private TemplateNameFormat() {
   }

   abstract String toRootBasedName(String var1, String var2) throws MalformedTemplateNameException;

   abstract String normalizeRootBasedName(String var1) throws MalformedTemplateNameException;

   abstract String rootBasedNameToAbsoluteName(String var1) throws MalformedTemplateNameException;

   private static void checkNameHasNoNullCharacter(String name) throws MalformedTemplateNameException {
      if (name.indexOf(0) != -1) {
         throw new MalformedTemplateNameException(name, "Null character (\\u0000) in the name; possible attack attempt");
      }
   }

   private static MalformedTemplateNameException newRootLeavingException(String name) {
      return new MalformedTemplateNameException(name, "Backing out from the root directory is not allowed");
   }

   // $FF: synthetic method
   TemplateNameFormat(Object x0) {
      this();
   }

   private static final class Default020400 extends TemplateNameFormat {
      private Default020400() {
         super(null);
      }

      String toRootBasedName(String baseName, String targetName) {
         if (this.findSchemeSectionEnd(targetName) != 0) {
            return targetName;
         } else if (targetName.startsWith("/")) {
            String targetNameAsRelative = targetName.substring(1);
            int schemeSectionEnd = this.findSchemeSectionEnd(baseName);
            return schemeSectionEnd == 0 ? targetNameAsRelative : baseName.substring(0, schemeSectionEnd) + targetNameAsRelative;
         } else {
            if (!baseName.endsWith("/")) {
               int baseEnd = baseName.lastIndexOf("/") + 1;
               if (baseEnd == 0) {
                  baseEnd = this.findSchemeSectionEnd(baseName);
               }

               baseName = baseName.substring(0, baseEnd);
            }

            return baseName + targetName;
         }
      }

      String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
         TemplateNameFormat.checkNameHasNoNullCharacter(name);
         if (name.indexOf(92) != -1) {
            throw new MalformedTemplateNameException(name, "Backslash (\"\\\") is not allowed in template names. Use slash (\"/\") instead.");
         } else {
            int schemeSectionEnd = this.findSchemeSectionEnd(name);
            String scheme;
            String path;
            if (schemeSectionEnd == 0) {
               scheme = null;
               path = name;
            } else {
               scheme = name.substring(0, schemeSectionEnd);
               path = name.substring(schemeSectionEnd);
            }

            if (path.indexOf(58) != -1) {
               throw new MalformedTemplateNameException(name, "The ':' character can only be used after the scheme name (if there's any), not in the path part");
            } else {
               path = this.removeRedundantSlashes(path);
               path = this.removeDotSteps(path);
               path = this.resolveDotDotSteps(path, name);
               path = this.removeRedundantStarSteps(path);
               return scheme == null ? path : scheme + path;
            }
         }
      }

      private int findSchemeSectionEnd(String name) {
         int schemeColonIdx = name.indexOf(":");
         if (schemeColonIdx != -1 && name.lastIndexOf(47, schemeColonIdx - 1) == -1) {
            return schemeColonIdx + 2 < name.length() && name.charAt(schemeColonIdx + 1) == '/' && name.charAt(schemeColonIdx + 2) == '/' ? schemeColonIdx + 3 : schemeColonIdx + 1;
         } else {
            return 0;
         }
      }

      private String removeRedundantSlashes(String path) {
         String prevName;
         do {
            prevName = path;
            path = StringUtil.replace(path, "//", "/");
         } while(prevName != path);

         return path.startsWith("/") ? path.substring(1) : path;
      }

      private String removeDotSteps(String path) {
         int nextFromIdx = path.length() - 1;

         while(true) {
            int dotIdx;
            boolean slashRight;
            while(true) {
               do {
                  dotIdx = path.lastIndexOf(46, nextFromIdx);
                  if (dotIdx < 0) {
                     return path;
                  }

                  nextFromIdx = dotIdx - 1;
               } while(dotIdx != 0 && path.charAt(dotIdx - 1) != '/');

               if (dotIdx + 1 == path.length()) {
                  slashRight = false;
                  break;
               }

               if (path.charAt(dotIdx + 1) == '/') {
                  slashRight = true;
                  break;
               }
            }

            if (slashRight) {
               path = path.substring(0, dotIdx) + path.substring(dotIdx + 2);
            } else {
               path = path.substring(0, path.length() - 1);
            }
         }
      }

      private String resolveDotDotSteps(String path, String name) throws MalformedTemplateNameException {
         int nextFromIdx = 0;

         while(true) {
            int dotDotIdx = path.indexOf("..", nextFromIdx);
            if (dotDotIdx < 0) {
               return path;
            }

            if (dotDotIdx == 0) {
               throw TemplateNameFormat.newRootLeavingException(name);
            }

            if (path.charAt(dotDotIdx - 1) != '/') {
               nextFromIdx = dotDotIdx + 3;
            } else {
               boolean slashRight;
               if (dotDotIdx + 2 == path.length()) {
                  slashRight = false;
               } else {
                  if (path.charAt(dotDotIdx + 2) != '/') {
                     nextFromIdx = dotDotIdx + 3;
                     continue;
                  }

                  slashRight = true;
               }

               boolean skippedStarStep = false;
               int searchSlashBacwardsFrom = dotDotIdx - 2;

               int previousSlashIdx;
               while(true) {
                  if (searchSlashBacwardsFrom == -1) {
                     throw TemplateNameFormat.newRootLeavingException(name);
                  }

                  previousSlashIdx = path.lastIndexOf(47, searchSlashBacwardsFrom);
                  if (previousSlashIdx == -1) {
                     if (searchSlashBacwardsFrom == 0 && path.charAt(0) == '*') {
                        throw TemplateNameFormat.newRootLeavingException(name);
                     }
                     break;
                  }

                  if (path.charAt(previousSlashIdx + 1) != '*' || path.charAt(previousSlashIdx + 2) != '/') {
                     break;
                  }

                  skippedStarStep = true;
                  searchSlashBacwardsFrom = previousSlashIdx - 1;
               }

               path = path.substring(0, previousSlashIdx + 1) + (skippedStarStep ? "*/" : "") + path.substring(dotDotIdx + (slashRight ? 3 : 2));
               nextFromIdx = previousSlashIdx + 1;
            }
         }
      }

      private String removeRedundantStarSteps(String path) {
         while(true) {
            int supiciousIdx = path.indexOf("*/*");
            if (supiciousIdx != -1) {
               if ((supiciousIdx == 0 || path.charAt(supiciousIdx - 1) == '/') && (supiciousIdx + 3 == path.length() || path.charAt(supiciousIdx + 3) == '/')) {
                  path = path.substring(0, supiciousIdx) + path.substring(supiciousIdx + 2);
               }

               if (path != path) {
                  continue;
               }
            }

            if (path.startsWith("*")) {
               if (path.length() == 1) {
                  path = "";
               } else if (path.charAt(1) == '/') {
                  path = path.substring(2);
               }
            }

            return path;
         }
      }

      String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
         if (this.findSchemeSectionEnd(name) != 0) {
            return name;
         } else {
            return !name.startsWith("/") ? "/" + name : name;
         }
      }

      public String toString() {
         return "TemplateNameFormat.DEFAULT_2_4_0";
      }

      // $FF: synthetic method
      Default020400(Object x0) {
         this();
      }
   }

   private static final class Default020300 extends TemplateNameFormat {
      private Default020300() {
         super(null);
      }

      String toRootBasedName(String baseName, String targetName) {
         if (targetName.indexOf("://") > 0) {
            return targetName;
         } else if (targetName.startsWith("/")) {
            int schemeSepIdx = baseName.indexOf("://");
            return schemeSepIdx > 0 ? baseName.substring(0, schemeSepIdx + 2) + targetName : targetName.substring(1);
         } else {
            if (!baseName.endsWith("/")) {
               baseName = baseName.substring(0, baseName.lastIndexOf("/") + 1);
            }

            return baseName + targetName;
         }
      }

      String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
         TemplateNameFormat.checkNameHasNoNullCharacter(name);
         String path = name;

         while(true) {
            int currentDirPathLoc = path.indexOf("/../");
            if (currentDirPathLoc == 0) {
               throw TemplateNameFormat.newRootLeavingException(name);
            }

            if (currentDirPathLoc == -1) {
               if (path.startsWith("../")) {
                  throw TemplateNameFormat.newRootLeavingException(name);
               }

               while(true) {
                  currentDirPathLoc = path.indexOf("/./");
                  if (currentDirPathLoc == -1) {
                     if (path.startsWith("./")) {
                        path = path.substring("./".length());
                     }

                     if (path.length() > 1 && path.charAt(0) == '/') {
                        path = path.substring(1);
                     }

                     return path;
                  }

                  path = path.substring(0, currentDirPathLoc) + path.substring(currentDirPathLoc + "/./".length() - 1);
               }
            }

            int previousSlashLoc = path.lastIndexOf(47, currentDirPathLoc - 1);
            path = path.substring(0, previousSlashLoc + 1) + path.substring(currentDirPathLoc + "/../".length());
         }
      }

      String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
         if (name.indexOf("://") > 0) {
            return name;
         } else {
            return !name.startsWith("/") ? "/" + name : name;
         }
      }

      public String toString() {
         return "TemplateNameFormat.DEFAULT_2_3_0";
      }

      // $FF: synthetic method
      Default020300(Object x0) {
         this();
      }
   }
}
