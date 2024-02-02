/*     */ package cn.hutool.setting;
/*     */ 
/*     */ import cn.hutool.core.collection.CollectionUtil;
/*     */ import cn.hutool.core.collection.ListUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.URLUtil;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
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
/*     */ public class GroupedSet
/*     */   extends HashMap<String, LinkedHashSet<String>>
/*     */ {
/*     */   private static final long serialVersionUID = -8430706353275835496L;
/*     */   private static final String COMMENT_FLAG_PRE = "#";
/*  51 */   private static final char[] GROUP_SURROUND = new char[] { '[', ']' };
/*     */ 
/*     */ 
/*     */   
/*     */   private Charset charset;
/*     */ 
/*     */ 
/*     */   
/*     */   private URL groupedSetUrl;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedSet(Charset charset) {
/*  65 */     this.charset = charset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedSet(String pathBaseClassLoader, Charset charset) {
/*  75 */     if (null == pathBaseClassLoader) {
/*  76 */       pathBaseClassLoader = "";
/*     */     }
/*     */     
/*  79 */     URL url = URLUtil.getURL(pathBaseClassLoader);
/*  80 */     if (url == null) {
/*  81 */       throw new RuntimeException(StrUtil.format("Can not find GroupSet file: [{}]", new Object[] { pathBaseClassLoader }));
/*     */     }
/*  83 */     init(url, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedSet(File configFile, Charset charset) {
/*  93 */     if (configFile == null) {
/*  94 */       throw new RuntimeException("Null GroupSet file!");
/*     */     }
/*  96 */     URL url = URLUtil.getURL(configFile);
/*  97 */     init(url, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedSet(String path, Class<?> clazz, Charset charset) {
/* 108 */     URL url = URLUtil.getURL(path, clazz);
/* 109 */     if (url == null) {
/* 110 */       throw new RuntimeException(StrUtil.format("Can not find GroupSet file: [{}]", new Object[] { path }));
/*     */     }
/* 112 */     init(url, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedSet(URL url, Charset charset) {
/* 122 */     if (url == null) {
/* 123 */       throw new RuntimeException("Null url define!");
/*     */     }
/* 125 */     init(url, charset);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public GroupedSet(String pathBaseClassLoader) {
/* 134 */     this(pathBaseClassLoader, CharsetUtil.CHARSET_UTF_8);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean init(URL groupedSetUrl, Charset charset) {
/* 146 */     if (groupedSetUrl == null) {
/* 147 */       throw new RuntimeException("Null GroupSet url or charset define!");
/*     */     }
/* 149 */     this.charset = charset;
/* 150 */     this.groupedSetUrl = groupedSetUrl;
/*     */     
/* 152 */     return load(groupedSetUrl);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean load(URL groupedSetUrl) {
/* 162 */     if (groupedSetUrl == null) {
/* 163 */       throw new RuntimeException("Null GroupSet url define!");
/*     */     }
/*     */     
/* 166 */     InputStream settingStream = null;
/*     */     try {
/* 168 */       settingStream = groupedSetUrl.openStream();
/* 169 */       load(settingStream);
/* 170 */     } catch (IOException e) {
/*     */       
/* 172 */       return false;
/*     */     } finally {
/* 174 */       IoUtil.close(settingStream);
/*     */     } 
/* 176 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reload() {
/* 183 */     load(this.groupedSetUrl);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean load(InputStream settingStream) throws IOException {
/* 194 */     clear();
/* 195 */     BufferedReader reader = null;
/*     */     try {
/* 197 */       reader = IoUtil.getReader(settingStream, this.charset);
/*     */ 
/*     */       
/* 200 */       LinkedHashSet<String> valueSet = null;
/*     */       
/*     */       while (true) {
/* 203 */         String line = reader.readLine();
/* 204 */         if (line == null) {
/*     */           break;
/*     */         }
/* 207 */         line = line.trim();
/*     */         
/* 209 */         if (StrUtil.isBlank(line) || line.startsWith("#")) {
/*     */           continue;
/*     */         }
/* 212 */         if (line.startsWith("\\#"))
/*     */         {
/* 214 */           line = line.substring(1);
/*     */         }
/*     */ 
/*     */         
/* 218 */         if (line.charAt(0) == GROUP_SURROUND[0] && line.charAt(line.length() - 1) == GROUP_SURROUND[1]) {
/*     */           
/* 220 */           String group = line.substring(1, line.length() - 1).trim();
/* 221 */           valueSet = (LinkedHashSet<String>)get(group);
/* 222 */           if (null == valueSet) {
/* 223 */             valueSet = new LinkedHashSet<>();
/*     */           }
/* 225 */           put((K)group, (V)valueSet);
/*     */           
/*     */           continue;
/*     */         } 
/*     */         
/* 230 */         if (null == valueSet) {
/*     */           
/* 232 */           valueSet = new LinkedHashSet<>();
/* 233 */           put((K)"", (V)valueSet);
/*     */         } 
/* 235 */         valueSet.add(line);
/*     */       } 
/*     */     } finally {
/* 238 */       IoUtil.close(reader);
/*     */     } 
/* 240 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPath() {
/* 247 */     return this.groupedSetUrl.getPath();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<String> getGroups() {
/* 254 */     return (Set)keySet();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LinkedHashSet<String> getValues(String group) {
/* 264 */     if (group == null) {
/* 265 */       group = "";
/*     */     }
/* 267 */     return (LinkedHashSet<String>)get(group);
/*     */   }
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
/*     */   public boolean contains(String group, String value, String... otherValues) {
/* 280 */     if (ArrayUtil.isNotEmpty((Object[])otherValues)) {
/*     */       
/* 282 */       List<String> valueList = ListUtil.toList((Object[])otherValues);
/* 283 */       valueList.add(value);
/* 284 */       return contains(group, valueList);
/*     */     } 
/*     */     
/* 287 */     LinkedHashSet<String> valueSet = getValues(group);
/* 288 */     if (CollectionUtil.isEmpty(valueSet)) {
/* 289 */       return false;
/*     */     }
/*     */     
/* 292 */     return valueSet.contains(value);
/*     */   }
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
/*     */   public boolean contains(String group, Collection<String> values) {
/* 305 */     LinkedHashSet<String> valueSet = getValues(group);
/* 306 */     if (CollectionUtil.isEmpty(values) || CollectionUtil.isEmpty(valueSet)) {
/* 307 */       return false;
/*     */     }
/*     */     
/* 310 */     return valueSet.containsAll(values);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\GroupedSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */