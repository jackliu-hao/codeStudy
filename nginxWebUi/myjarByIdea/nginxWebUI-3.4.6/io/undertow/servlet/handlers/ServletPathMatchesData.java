package io.undertow.servlet.handlers;

import io.undertow.UndertowMessages;
import io.undertow.util.SubstringMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class ServletPathMatchesData {
   private final Map<String, ServletPathMatch> exactPathMatches;
   private final SubstringMap<PathMatch> prefixMatches;
   private final Map<String, ServletChain> nameMatches;

   ServletPathMatchesData(Map<String, ServletChain> exactPathMatches, SubstringMap<PathMatch> prefixMatches, Map<String, ServletChain> nameMatches) {
      this.prefixMatches = prefixMatches;
      this.nameMatches = nameMatches;
      Map<String, ServletPathMatch> newExactPathMatches = new HashMap();
      Iterator var5 = exactPathMatches.entrySet().iterator();

      while(var5.hasNext()) {
         Map.Entry<String, ServletChain> entry = (Map.Entry)var5.next();
         newExactPathMatches.put(entry.getKey(), new ServletPathMatch((ServletChain)entry.getValue(), (String)entry.getKey(), ((ServletChain)entry.getValue()).isDefaultServletMapping()));
      }

      this.exactPathMatches = newExactPathMatches;
   }

   public ServletChain getServletHandlerByName(String name) {
      return (ServletChain)this.nameMatches.get(name);
   }

   public ServletPathMatch getServletHandlerByExactPath(String path) {
      return (ServletPathMatch)this.exactPathMatches.get(path);
   }

   public ServletPathMatch getServletHandlerByPath(String path) {
      ServletPathMatch exact = (ServletPathMatch)this.exactPathMatches.get(path);
      if (exact != null) {
         return exact;
      } else {
         SubstringMap.SubstringMatch<PathMatch> match = this.prefixMatches.get(path, path.length());
         if (match != null) {
            return this.handleMatch(path, (PathMatch)match.getValue(), path.lastIndexOf(46));
         } else {
            int extensionPos = -1;

            for(int i = path.length() - 1; i >= 0; --i) {
               char c = path.charAt(i);
               if (c == '/') {
                  match = this.prefixMatches.get(path, i);
                  if (match != null) {
                     return this.handleMatch(path, (PathMatch)match.getValue(), extensionPos);
                  }
               } else if (c == '.' && extensionPos == -1) {
                  extensionPos = i;
               }
            }

            throw UndertowMessages.MESSAGES.servletPathMatchFailed();
         }
      }
   }

   private ServletPathMatch handleMatch(String path, PathMatch match, int extensionPos) {
      if (extensionPos != -1 && !match.extensionMatches.isEmpty()) {
         String ext = path.substring(extensionPos + 1);
         ServletChain handler = (ServletChain)match.extensionMatches.get(ext);
         return handler != null ? new ServletPathMatch(handler, path, handler.getManagedServlet().getServletInfo().isRequireWelcomeFileMapping()) : new ServletPathMatch(match.defaultHandler, path, match.requireWelcomeFileMatch);
      } else {
         return new ServletPathMatch(match.defaultHandler, path, match.requireWelcomeFileMatch);
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   private static class PathMatch {
      private final Map<String, ServletChain> extensionMatches = new HashMap();
      private volatile ServletChain defaultHandler;
      private volatile boolean requireWelcomeFileMatch;

      PathMatch(ServletChain defaultHandler) {
         this.defaultHandler = defaultHandler;
      }
   }

   public static final class Builder {
      private final Map<String, ServletChain> exactPathMatches = new HashMap();
      private final SubstringMap<PathMatch> prefixMatches = new SubstringMap();
      private final Map<String, ServletChain> nameMatches = new HashMap();

      public void addExactMatch(String exactMatch, ServletChain match) {
         this.exactPathMatches.put(exactMatch, match);
      }

      public void addPrefixMatch(String prefix, ServletChain match, boolean requireWelcomeFileMatch) {
         SubstringMap.SubstringMatch<PathMatch> mt = this.prefixMatches.get(prefix);
         PathMatch m;
         if (mt == null) {
            this.prefixMatches.put(prefix, m = new PathMatch(match));
         } else {
            m = (PathMatch)mt.getValue();
         }

         m.defaultHandler = match;
         m.requireWelcomeFileMatch = requireWelcomeFileMatch;
      }

      public void addExtensionMatch(String prefix, String extension, ServletChain match) {
         SubstringMap.SubstringMatch<PathMatch> mt = this.prefixMatches.get(prefix);
         PathMatch m;
         if (mt == null) {
            this.prefixMatches.put(prefix, m = new PathMatch((ServletChain)null));
         } else {
            m = (PathMatch)mt.getValue();
         }

         m.extensionMatches.put(extension, match);
      }

      public void addNameMatch(String name, ServletChain match) {
         this.nameMatches.put(name, match);
      }

      public ServletPathMatchesData build() {
         return new ServletPathMatchesData(this.exactPathMatches, this.prefixMatches, this.nameMatches);
      }
   }
}
