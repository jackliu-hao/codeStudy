package io.undertow.util;

import io.undertow.UndertowMessages;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PathTemplateMatcher<T> {
   private Map<String, Set<PathTemplateMatcher<T>.PathTemplateHolder>> pathTemplateMap = new CopyOnWriteMap();
   private volatile int[] lengths = new int[0];

   public PathMatchResult<T> match(String path) {
      String normalizedPath = "".equals(path) ? "/" : path;
      if (!normalizedPath.startsWith("/")) {
         normalizedPath = "/" + normalizedPath;
      }

      Map<String, String> params = new LinkedHashMap();
      int length = normalizedPath.length();
      int[] lengths = this.lengths;

      for(int i = 0; i < lengths.length; ++i) {
         int pathLength = lengths[i];
         if (pathLength == length) {
            Set<PathTemplateMatcher<T>.PathTemplateHolder> entry = (Set)this.pathTemplateMap.get(normalizedPath);
            if (entry != null) {
               PathMatchResult<T> res = this.handleStemMatch(entry, normalizedPath, params);
               if (res != null) {
                  return res;
               }
            }
         } else if (pathLength < length) {
            String part = normalizedPath.substring(0, pathLength);
            Set<PathTemplateMatcher<T>.PathTemplateHolder> entry = (Set)this.pathTemplateMap.get(part);
            if (entry != null) {
               PathMatchResult<T> res = this.handleStemMatch(entry, normalizedPath, params);
               if (res != null) {
                  return res;
               }
            }
         }
      }

      return null;
   }

   private PathMatchResult<T> handleStemMatch(Set<PathTemplateMatcher<T>.PathTemplateHolder> entry, String path, Map<String, String> params) {
      Iterator var4 = entry.iterator();

      while(var4.hasNext()) {
         PathTemplateMatcher<T>.PathTemplateHolder val = (PathTemplateHolder)var4.next();
         if (val.template.matches(path, params)) {
            return new PathMatchResult(params, val.template.getTemplateString(), val.value);
         }

         params.clear();
      }

      return null;
   }

   public synchronized PathTemplateMatcher<T> add(PathTemplate template, T value) {
      Set<PathTemplateMatcher<T>.PathTemplateHolder> values = (Set)this.pathTemplateMap.get(this.trimBase(template));
      TreeSet newValues;
      if (values == null) {
         newValues = new TreeSet();
      } else {
         newValues = new TreeSet(values);
      }

      PathTemplateMatcher<T>.PathTemplateHolder holder = new PathTemplateHolder(value, template);
      if (!newValues.contains(holder)) {
         newValues.add(holder);
         this.pathTemplateMap.put(this.trimBase(template), newValues);
         this.buildLengths();
         return this;
      } else {
         PathTemplate equivalent = null;
         Iterator var7 = newValues.iterator();

         while(var7.hasNext()) {
            PathTemplateMatcher<T>.PathTemplateHolder item = (PathTemplateHolder)var7.next();
            if (item.compareTo(holder) == 0) {
               equivalent = item.template;
               break;
            }
         }

         throw UndertowMessages.MESSAGES.matcherAlreadyContainsTemplate(template.getTemplateString(), equivalent.getTemplateString());
      }
   }

   private String trimBase(PathTemplate template) {
      String retval = template.getBase();
      if (template.getBase().endsWith("/") && !template.getParameterNames().isEmpty()) {
         return retval.substring(0, retval.length() - 1);
      } else {
         return retval.endsWith("*") ? retval.substring(0, retval.length() - 1) : retval;
      }
   }

   private void buildLengths() {
      Set<Integer> lengths = new TreeSet(new Comparator<Integer>() {
         public int compare(Integer o1, Integer o2) {
            return -o1.compareTo(o2);
         }
      });
      Iterator var2 = this.pathTemplateMap.keySet().iterator();

      while(var2.hasNext()) {
         String p = (String)var2.next();
         lengths.add(p.length());
      }

      int[] lengthArray = new int[lengths.size()];
      int pos = 0;

      int i;
      for(Iterator var4 = lengths.iterator(); var4.hasNext(); lengthArray[pos++] = i) {
         i = (Integer)var4.next();
      }

      this.lengths = lengthArray;
   }

   public synchronized PathTemplateMatcher<T> add(String pathTemplate, T value) {
      PathTemplate template = PathTemplate.create(pathTemplate);
      return this.add(template, value);
   }

   public synchronized PathTemplateMatcher<T> addAll(PathTemplateMatcher<T> pathTemplateMatcher) {
      Iterator var2 = pathTemplateMatcher.getPathTemplateMap().entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, Set<PathTemplateMatcher<T>.PathTemplateHolder>> entry = (Map.Entry)var2.next();
         Iterator var4 = ((Set)entry.getValue()).iterator();

         while(var4.hasNext()) {
            PathTemplateMatcher<T>.PathTemplateHolder pathTemplateHolder = (PathTemplateHolder)var4.next();
            this.add(pathTemplateHolder.template, pathTemplateHolder.value);
         }
      }

      return this;
   }

   Map<String, Set<PathTemplateMatcher<T>.PathTemplateHolder>> getPathTemplateMap() {
      return this.pathTemplateMap;
   }

   public Set<PathTemplate> getPathTemplates() {
      Set<PathTemplate> templates = new HashSet();
      Iterator var2 = this.pathTemplateMap.values().iterator();

      while(var2.hasNext()) {
         Set<PathTemplateMatcher<T>.PathTemplateHolder> holders = (Set)var2.next();
         Iterator var4 = holders.iterator();

         while(var4.hasNext()) {
            PathTemplateMatcher<T>.PathTemplateHolder holder = (PathTemplateHolder)var4.next();
            templates.add(holder.template);
         }
      }

      return templates;
   }

   public synchronized PathTemplateMatcher<T> remove(String pathTemplate) {
      PathTemplate template = PathTemplate.create(pathTemplate);
      return this.remove(template);
   }

   private synchronized PathTemplateMatcher<T> remove(PathTemplate template) {
      Set<PathTemplateMatcher<T>.PathTemplateHolder> values = (Set)this.pathTemplateMap.get(this.trimBase(template));
      if (values == null) {
         return this;
      } else {
         Set<PathTemplateMatcher<T>.PathTemplateHolder> newValues = new TreeSet(values);
         Iterator<PathTemplateMatcher<T>.PathTemplateHolder> it = newValues.iterator();

         while(it.hasNext()) {
            PathTemplateMatcher<T>.PathTemplateHolder next = (PathTemplateHolder)it.next();
            if (next.template.getTemplateString().equals(template.getTemplateString())) {
               it.remove();
               break;
            }
         }

         if (newValues.size() == 0) {
            this.pathTemplateMap.remove(this.trimBase(template));
         } else {
            this.pathTemplateMap.put(this.trimBase(template), newValues);
         }

         this.buildLengths();
         return this;
      }
   }

   public synchronized T get(String template) {
      PathTemplate pathTemplate = PathTemplate.create(template);
      Set<PathTemplateMatcher<T>.PathTemplateHolder> values = (Set)this.pathTemplateMap.get(this.trimBase(pathTemplate));
      if (values == null) {
         return null;
      } else {
         Iterator var4 = values.iterator();

         PathTemplateHolder next;
         do {
            if (!var4.hasNext()) {
               return null;
            }

            next = (PathTemplateHolder)var4.next();
         } while(!next.template.getTemplateString().equals(template));

         return next.value;
      }
   }

   private final class PathTemplateHolder implements Comparable<PathTemplateMatcher<T>.PathTemplateHolder> {
      final T value;
      final PathTemplate template;

      private PathTemplateHolder(T value, PathTemplate template) {
         this.value = value;
         this.template = template;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o == null) {
            return false;
         } else if (!PathTemplateHolder.class.equals(o.getClass())) {
            return false;
         } else {
            PathTemplateMatcher<T>.PathTemplateHolder that = (PathTemplateHolder)o;
            return this.template.equals(that.template);
         }
      }

      public int hashCode() {
         return this.template.hashCode();
      }

      public int compareTo(PathTemplateMatcher<T>.PathTemplateHolder o) {
         return this.template.compareTo(o.template);
      }

      // $FF: synthetic method
      PathTemplateHolder(Object x1, PathTemplate x2, Object x3) {
         this(x1, x2);
      }
   }

   public static class PathMatchResult<T> extends PathTemplateMatch {
      private final T value;

      public PathMatchResult(Map<String, String> parameters, String matchedTemplate, T value) {
         super(matchedTemplate, parameters);
         this.value = value;
      }

      public T getValue() {
         return this.value;
      }
   }
}
