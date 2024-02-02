package io.undertow.util;

import java.util.ArrayList;
import java.util.List;

public class CanonicalPathUtils {
   private static final boolean DONT_CANONICALIZE_BACKSLASH = Boolean.parseBoolean("io.undertow.DONT_CANONICALIZE_BACKSLASH");
   static final int START = -1;
   static final int NORMAL = 0;
   static final int FIRST_SLASH = 1;
   static final int ONE_DOT = 2;
   static final int TWO_DOT = 3;
   static final int FIRST_BACKSLASH = 4;

   public static String canonicalize(String path) {
      return canonicalize(path, false);
   }

   public static String canonicalize(String path, boolean nullAllowed) {
      int state = -1;

      for(int i = path.length() - 1; i >= 0; --i) {
         char c = path.charAt(i);
         switch (c) {
            case '.':
               if (state != 1 && state != -1 && state != 4) {
                  if (state == 2) {
                     state = 3;
                  } else {
                     state = 0;
                  }
                  break;
               }

               state = 2;
               break;
            case '/':
               if (state == 1) {
                  return realCanonicalize(path, i + 1, 1, nullAllowed);
               }

               if (state == 2) {
                  return realCanonicalize(path, i + 2, 1, nullAllowed);
               }

               if (state == 3) {
                  return realCanonicalize(path, i + 3, 1, nullAllowed);
               }

               state = 1;
               break;
            case '\\':
               if (!DONT_CANONICALIZE_BACKSLASH) {
                  if (state == 4) {
                     return realCanonicalize(path, i + 1, 4, nullAllowed);
                  }

                  if (state == 2) {
                     return realCanonicalize(path, i + 2, 4, nullAllowed);
                  }

                  if (state == 3) {
                     return realCanonicalize(path, i + 3, 4, nullAllowed);
                  }

                  state = 4;
                  break;
               }
            default:
               state = 0;
         }
      }

      return path;
   }

   private static String realCanonicalize(String path, int lastDot, int initialState, boolean nullAllowed) {
      int state = initialState;
      int eatCount = 0;
      int tokenEnd = path.length();
      List<String> parts = new ArrayList();

      int c;
      for(int i = lastDot - 1; i >= 0; --i) {
         c = path.charAt(i);
         switch (state) {
            case 0:
               if (c == 47) {
                  state = 1;
                  if (eatCount > 0) {
                     --eatCount;
                     tokenEnd = i;
                  }
               } else if (c == 92 && !DONT_CANONICALIZE_BACKSLASH) {
                  state = 4;
                  if (eatCount > 0) {
                     --eatCount;
                     tokenEnd = i;
                  }
               }
               break;
            case 1:
               if (c == 46) {
                  state = 2;
               } else if (c == 47) {
                  if (eatCount > 0) {
                     --eatCount;
                     tokenEnd = i;
                  } else {
                     parts.add(path.substring(i + 1, tokenEnd));
                     tokenEnd = i;
                  }
               } else {
                  state = 0;
               }
               break;
            case 2:
               if (c == 46) {
                  state = 3;
               } else {
                  if (c == 47 || c == 92 && !DONT_CANONICALIZE_BACKSLASH) {
                     if (i + 2 != tokenEnd) {
                        parts.add(path.substring(i + 2, tokenEnd));
                     }

                     tokenEnd = i;
                     state = c == 47 ? 1 : 4;
                     continue;
                  }

                  state = 0;
               }
               break;
            case 3:
               if (c != 47 && (c != 92 || DONT_CANONICALIZE_BACKSLASH)) {
                  state = 0;
                  break;
               }

               if (i + 3 != tokenEnd) {
                  parts.add(path.substring(i + 3, tokenEnd));
               }

               tokenEnd = i;
               ++eatCount;
               state = c == 47 ? 1 : 4;
               break;
            case 4:
               if (c == 46) {
                  state = 2;
               } else if (c == 92) {
                  if (eatCount > 0) {
                     --eatCount;
                     tokenEnd = i;
                  } else {
                     parts.add(path.substring(i + 1, tokenEnd));
                     tokenEnd = i;
                  }
               } else {
                  state = 0;
               }
         }
      }

      if (eatCount > 0 && nullAllowed) {
         return null;
      } else {
         StringBuilder result = new StringBuilder();
         if (tokenEnd != 0) {
            result.append(path.substring(0, tokenEnd));
         }

         for(c = parts.size() - 1; c >= 0; --c) {
            result.append((String)parts.get(c));
         }

         if (result.length() == 0) {
            return "/";
         } else {
            return result.toString();
         }
      }
   }

   private CanonicalPathUtils() {
   }
}
