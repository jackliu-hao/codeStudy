package io.undertow.util;

import io.undertow.UndertowMessages;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PathTemplate implements Comparable<PathTemplate> {
   private final String templateString;
   private final boolean template;
   private final String base;
   final List<Part> parts;
   private final Set<String> parameterNames;
   private final boolean trailingSlash;

   private PathTemplate(String templateString, boolean template, String base, List<Part> parts, Set<String> parameterNames, boolean trailingSlash) {
      this.templateString = templateString;
      this.template = template;
      this.base = base;
      this.parts = parts;
      this.parameterNames = Collections.unmodifiableSet(parameterNames);
      this.trailingSlash = trailingSlash;
   }

   public static PathTemplate create(String inputPath) {
      if (inputPath == null) {
         throw UndertowMessages.MESSAGES.pathMustBeSpecified();
      } else if (!inputPath.startsWith("/")) {
         return create("/" + inputPath);
      } else {
         String path = inputPath;
         int state = 0;
         String base = "";
         List<Part> parts = new ArrayList();
         int stringStart = 0;

         for(int i = 0; i < path.length(); ++i) {
            int c = path.charAt(i);
            Part part;
            switch (state) {
               case 0:
                  if (c == '/') {
                     state = 1;
                  } else if (c == '*') {
                     base = path.substring(0, i + 1);
                     stringStart = i;
                     state = 5;
                  } else {
                     state = 0;
                  }
                  break;
               case 1:
                  if (c == '{') {
                     base = path.substring(0, i);
                     stringStart = i + 1;
                     state = 2;
                  } else if (c == '*') {
                     base = path.substring(0, i + 1);
                     stringStart = i;
                     state = 5;
                  } else if (c != '/') {
                     state = 0;
                  }
                  break;
               case 2:
                  if (c == '}') {
                     part = new Part(true, path.substring(stringStart, i));
                     parts.add(part);
                     stringStart = i;
                     state = 3;
                  }
                  break;
               case 3:
                  if (c != '/') {
                     throw UndertowMessages.MESSAGES.couldNotParseUriTemplate(path, i);
                  }

                  state = 4;
                  break;
               case 4:
                  if (c == '{') {
                     stringStart = i + 1;
                     state = 2;
                  } else if (c != '/') {
                     stringStart = i;
                     state = 5;
                  }
                  break;
               case 5:
                  if (c == '/') {
                     part = new Part(false, path.substring(stringStart, i));
                     parts.add(part);
                     stringStart = i + 1;
                     state = 4;
                  }
            }
         }

         boolean trailingSlash = false;
         switch (state) {
            case 1:
               trailingSlash = true;
            case 0:
               base = path;
               break;
            case 2:
               throw UndertowMessages.MESSAGES.couldNotParseUriTemplate(path, path.length());
            case 3:
            default:
               break;
            case 4:
               trailingSlash = true;
               break;
            case 5:
               Part part = new Part(false, path.substring(stringStart));
               parts.add(part);
         }

         Set<String> templates = new HashSet();
         Iterator var13 = parts.iterator();

         while(var13.hasNext()) {
            Part part = (Part)var13.next();
            if (part.template) {
               templates.add(part.part);
            }
         }

         return new PathTemplate(path, state > 1 && !base.contains("*"), base, parts, templates, trailingSlash);
      }
   }

   public boolean matches(String path, Map<String, String> pathParameters) {
      int baseLength;
      if (!this.template && this.base.contains("*")) {
         baseLength = this.base.indexOf("*");
         String startBase = this.base.substring(0, baseLength);
         if (!path.startsWith(startBase)) {
            return false;
         } else {
            pathParameters.put("*", path.substring(baseLength, path.length()));
            return true;
         }
      } else if (!path.startsWith(this.base)) {
         return false;
      } else {
         baseLength = this.base.length();
         if (!this.template) {
            return path.length() == baseLength;
         } else if (this.trailingSlash && path.charAt(path.length() - 1) != '/') {
            return false;
         } else {
            int currentPartPosition = 0;
            Part current = (Part)this.parts.get(currentPartPosition);
            int stringStart = baseLength;

            int i;
            for(i = baseLength; i < path.length(); ++i) {
               char currentChar = path.charAt(i);
               if (currentChar == '?' || current.part.equals("*")) {
                  break;
               }

               if (currentChar == '/') {
                  String result = path.substring(stringStart, i);
                  if (current.template) {
                     pathParameters.put(current.part, result);
                  } else if (!result.equals(current.part)) {
                     pathParameters.clear();
                     return false;
                  }

                  ++currentPartPosition;
                  if (currentPartPosition == this.parts.size()) {
                     return i == path.length() - 1;
                  }

                  current = (Part)this.parts.get(currentPartPosition);
                  stringStart = i + 1;
               }
            }

            if (currentPartPosition + 1 != this.parts.size()) {
               pathParameters.clear();
               return false;
            } else {
               String result = path.substring(stringStart, i);
               if (current.part.equals("*")) {
                  pathParameters.put(current.part, path.substring(stringStart, path.length()));
                  return true;
               } else {
                  if (current.template) {
                     pathParameters.put(current.part, result);
                  } else if (!result.equals(current.part)) {
                     pathParameters.clear();
                     return false;
                  }

                  return true;
               }
            }
         }
      }
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof PathTemplate)) {
         return false;
      } else {
         PathTemplate that = (PathTemplate)o;
         return this.compareTo(that) == 0;
      }
   }

   public int hashCode() {
      int result = this.getTemplateString() != null ? this.getTemplateString().hashCode() : 0;
      result = 31 * result + (this.template ? 1 : 0);
      result = 31 * result + (this.getBase() != null ? this.getBase().hashCode() : 0);
      result = 31 * result + (this.parts != null ? this.parts.hashCode() : 0);
      result = 31 * result + (this.getParameterNames() != null ? this.getParameterNames().hashCode() : 0);
      return result;
   }

   public int compareTo(PathTemplate o) {
      if (this.template && !o.template) {
         return 1;
      } else if (o.template && !this.template) {
         return -1;
      } else {
         int res = this.base.compareTo(o.base);
         if (res > 0) {
            return -1;
         } else if (res < 0) {
            return 1;
         } else if (!this.template) {
            return 0;
         } else {
            int i;
            for(i = 0; this.parts.size() != i; ++i) {
               if (o.parts.size() == i) {
                  return -1;
               }

               Part thisPath = (Part)this.parts.get(i);
               Part otherPart = (Part)o.parts.get(i);
               if (thisPath.template && !otherPart.template) {
                  return 1;
               }

               if (!thisPath.template && otherPart.template) {
                  return -1;
               }

               if (!thisPath.template) {
                  int r = thisPath.part.compareTo(otherPart.part);
                  if (r != 0) {
                     return r;
                  }
               }
            }

            return o.parts.size() == i ? this.base.compareTo(o.base) : 1;
         }
      }
   }

   public String getBase() {
      return this.base;
   }

   public String getTemplateString() {
      return this.templateString;
   }

   public Set<String> getParameterNames() {
      return this.parameterNames;
   }

   public String toString() {
      return "PathTemplate{template=" + this.template + ", base='" + this.base + '\'' + ", parts=" + this.parts + '}';
   }

   private static class Part {
      final boolean template;
      final String part;

      private Part(boolean template, String part) {
         this.template = template;
         this.part = part;
      }

      public String toString() {
         return "Part{template=" + this.template + ", part='" + this.part + '\'' + '}';
      }

      // $FF: synthetic method
      Part(boolean x0, String x1, Object x2) {
         this(x0, x1);
      }
   }
}
