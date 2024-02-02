/*     */ package io.undertow.util;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import java.util.Comparator;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathTemplateMatcher<T>
/*     */ {
/*  45 */   private Map<String, Set<PathTemplateHolder>> pathTemplateMap = new CopyOnWriteMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  50 */   private volatile int[] lengths = new int[0];
/*     */   
/*     */   public PathMatchResult<T> match(String path) {
/*  53 */     String normalizedPath = "".equals(path) ? "/" : path;
/*  54 */     if (!normalizedPath.startsWith("/"))
/*  55 */       normalizedPath = "/" + normalizedPath; 
/*  56 */     Map<String, String> params = new LinkedHashMap<>();
/*  57 */     int length = normalizedPath.length();
/*  58 */     int[] lengths = this.lengths;
/*  59 */     for (int i = 0; i < lengths.length; i++) {
/*  60 */       int pathLength = lengths[i];
/*  61 */       if (pathLength == length) {
/*  62 */         Set<PathTemplateHolder> entry = this.pathTemplateMap.get(normalizedPath);
/*  63 */         if (entry != null) {
/*  64 */           PathMatchResult<T> res = handleStemMatch(entry, normalizedPath, params);
/*  65 */           if (res != null) {
/*  66 */             return res;
/*     */           }
/*     */         } 
/*  69 */       } else if (pathLength < length) {
/*  70 */         String part = normalizedPath.substring(0, pathLength);
/*  71 */         Set<PathTemplateHolder> entry = this.pathTemplateMap.get(part);
/*  72 */         if (entry != null) {
/*  73 */           PathMatchResult<T> res = handleStemMatch(entry, normalizedPath, params);
/*  74 */           if (res != null) {
/*  75 */             return res;
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*  80 */     return null;
/*     */   }
/*     */   
/*     */   private PathMatchResult<T> handleStemMatch(Set<PathTemplateHolder> entry, String path, Map<String, String> params) {
/*  84 */     for (PathTemplateHolder val : entry) {
/*  85 */       if (val.template.matches(path, params)) {
/*  86 */         return new PathMatchResult<>(params, val.template.getTemplateString(), val.value);
/*     */       }
/*  88 */       params.clear();
/*     */     } 
/*     */     
/*  91 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized PathTemplateMatcher<T> add(PathTemplate template, T value) {
/*  96 */     Set<PathTemplateHolder> newValues, values = this.pathTemplateMap.get(trimBase(template));
/*     */     
/*  98 */     if (values == null) {
/*  99 */       newValues = new TreeSet<>();
/*     */     } else {
/* 101 */       newValues = new TreeSet<>(values);
/*     */     } 
/* 103 */     PathTemplateHolder holder = new PathTemplateHolder(value, template);
/* 104 */     if (newValues.contains(holder)) {
/* 105 */       PathTemplate equivalent = null;
/* 106 */       for (PathTemplateHolder item : newValues) {
/* 107 */         if (item.compareTo(holder) == 0) {
/* 108 */           equivalent = item.template;
/*     */           break;
/*     */         } 
/*     */       } 
/* 112 */       throw UndertowMessages.MESSAGES.matcherAlreadyContainsTemplate(template.getTemplateString(), equivalent.getTemplateString());
/*     */     } 
/* 114 */     newValues.add(holder);
/* 115 */     this.pathTemplateMap.put(trimBase(template), newValues);
/* 116 */     buildLengths();
/* 117 */     return this;
/*     */   }
/*     */   
/*     */   private String trimBase(PathTemplate template) {
/* 121 */     String retval = template.getBase();
/*     */     
/* 123 */     if (template.getBase().endsWith("/") && !template.getParameterNames().isEmpty()) {
/* 124 */       return retval.substring(0, retval.length() - 1);
/*     */     }
/* 126 */     if (retval.endsWith("*")) {
/* 127 */       return retval.substring(0, retval.length() - 1);
/*     */     }
/* 129 */     return retval;
/*     */   }
/*     */   
/*     */   private void buildLengths() {
/* 133 */     Set<Integer> lengths = new TreeSet<>(new Comparator<Integer>()
/*     */         {
/*     */           public int compare(Integer o1, Integer o2) {
/* 136 */             return -o1.compareTo(o2);
/*     */           }
/*     */         });
/* 139 */     for (String p : this.pathTemplateMap.keySet()) {
/* 140 */       lengths.add(Integer.valueOf(p.length()));
/*     */     }
/*     */     
/* 143 */     int[] lengthArray = new int[lengths.size()];
/* 144 */     int pos = 0;
/* 145 */     for (Iterator<Integer> iterator = lengths.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
/* 146 */       lengthArray[pos++] = i; }
/*     */     
/* 148 */     this.lengths = lengthArray;
/*     */   }
/*     */   
/*     */   public synchronized PathTemplateMatcher<T> add(String pathTemplate, T value) {
/* 152 */     PathTemplate template = PathTemplate.create(pathTemplate);
/* 153 */     return add(template, value);
/*     */   }
/*     */   
/*     */   public synchronized PathTemplateMatcher<T> addAll(PathTemplateMatcher<T> pathTemplateMatcher) {
/* 157 */     for (Map.Entry<String, Set<PathTemplateHolder>> entry : (Iterable<Map.Entry<String, Set<PathTemplateHolder>>>)pathTemplateMatcher.getPathTemplateMap().entrySet()) {
/* 158 */       for (PathTemplateHolder pathTemplateHolder : entry.getValue()) {
/* 159 */         add(pathTemplateHolder.template, pathTemplateHolder.value);
/*     */       }
/*     */     } 
/* 162 */     return this;
/*     */   }
/*     */   
/*     */   Map<String, Set<PathTemplateHolder>> getPathTemplateMap() {
/* 166 */     return this.pathTemplateMap;
/*     */   }
/*     */   
/*     */   public Set<PathTemplate> getPathTemplates() {
/* 170 */     Set<PathTemplate> templates = new HashSet<>();
/* 171 */     for (Set<PathTemplateHolder> holders : this.pathTemplateMap.values()) {
/* 172 */       for (PathTemplateHolder holder : holders) {
/* 173 */         templates.add(holder.template);
/*     */       }
/*     */     } 
/* 176 */     return templates;
/*     */   }
/*     */   
/*     */   public synchronized PathTemplateMatcher<T> remove(String pathTemplate) {
/* 180 */     PathTemplate template = PathTemplate.create(pathTemplate);
/* 181 */     return remove(template);
/*     */   }
/*     */   
/*     */   private synchronized PathTemplateMatcher<T> remove(PathTemplate template) {
/* 185 */     Set<PathTemplateHolder> values = this.pathTemplateMap.get(trimBase(template));
/*     */     
/* 187 */     if (values == null) {
/* 188 */       return this;
/*     */     }
/* 190 */     Set<PathTemplateHolder> newValues = new TreeSet<>(values);
/*     */     
/* 192 */     Iterator<PathTemplateHolder> it = newValues.iterator();
/* 193 */     while (it.hasNext()) {
/* 194 */       PathTemplateHolder next = it.next();
/* 195 */       if (next.template.getTemplateString().equals(template.getTemplateString())) {
/* 196 */         it.remove();
/*     */         break;
/*     */       } 
/*     */     } 
/* 200 */     if (newValues.size() == 0) {
/* 201 */       this.pathTemplateMap.remove(trimBase(template));
/*     */     } else {
/* 203 */       this.pathTemplateMap.put(trimBase(template), newValues);
/*     */     } 
/* 205 */     buildLengths();
/* 206 */     return this;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized T get(String template) {
/* 211 */     PathTemplate pathTemplate = PathTemplate.create(template);
/* 212 */     Set<PathTemplateHolder> values = this.pathTemplateMap.get(trimBase(pathTemplate));
/* 213 */     if (values == null) {
/* 214 */       return null;
/*     */     }
/* 216 */     for (PathTemplateHolder next : values) {
/* 217 */       if (next.template.getTemplateString().equals(template)) {
/* 218 */         return next.value;
/*     */       }
/*     */     } 
/* 221 */     return null;
/*     */   }
/*     */   
/*     */   public static class PathMatchResult<T> extends PathTemplateMatch {
/*     */     private final T value;
/*     */     
/*     */     public PathMatchResult(Map<String, String> parameters, String matchedTemplate, T value) {
/* 228 */       super(matchedTemplate, parameters);
/* 229 */       this.value = value;
/*     */     }
/*     */     
/*     */     public T getValue() {
/* 233 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   private final class PathTemplateHolder implements Comparable<PathTemplateHolder> {
/*     */     final T value;
/*     */     final PathTemplate template;
/*     */     
/*     */     private PathTemplateHolder(T value, PathTemplate template) {
/* 242 */       this.value = value;
/* 243 */       this.template = template;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 248 */       if (this == o) return true; 
/* 249 */       if (o == null) return false; 
/* 250 */       if (!PathTemplateHolder.class.equals(o.getClass())) return false;
/*     */       
/* 252 */       PathTemplateHolder that = (PathTemplateHolder)o;
/* 253 */       return this.template.equals(that.template);
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 258 */       return this.template.hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public int compareTo(PathTemplateHolder o) {
/* 263 */       return this.template.compareTo(o.template);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\underto\\util\PathTemplateMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */