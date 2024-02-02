/*     */ package cn.hutool.setting;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.Resource;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.CharsetUtil;
/*     */ import cn.hutool.core.util.ReUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.SystemPropsUtil;
/*     */ import cn.hutool.log.Log;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintWriter;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SettingLoader
/*     */ {
/*  33 */   private static final Log log = Log.get();
/*     */ 
/*     */   
/*     */   private static final char COMMENT_FLAG_PRE = '#';
/*     */   
/*  38 */   private char assignFlag = '=';
/*     */   
/*  40 */   private String varRegex = "\\$\\{(.*?)\\}";
/*     */ 
/*     */ 
/*     */   
/*     */   private final Charset charset;
/*     */ 
/*     */   
/*     */   private final boolean isUseVariable;
/*     */ 
/*     */   
/*     */   private final GroupedMap groupedMap;
/*     */ 
/*     */ 
/*     */   
/*     */   public SettingLoader(GroupedMap groupedMap) {
/*  55 */     this(groupedMap, CharsetUtil.CHARSET_UTF_8, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SettingLoader(GroupedMap groupedMap, Charset charset, boolean isUseVariable) {
/*  66 */     this.groupedMap = groupedMap;
/*  67 */     this.charset = charset;
/*  68 */     this.isUseVariable = isUseVariable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean load(Resource resource) {
/*  78 */     if (resource == null) {
/*  79 */       throw new NullPointerException("Null setting url define!");
/*     */     }
/*  81 */     log.debug("Load setting file [{}]", new Object[] { resource });
/*  82 */     InputStream settingStream = null;
/*     */     try {
/*  84 */       settingStream = resource.getStream();
/*  85 */       load(settingStream);
/*  86 */     } catch (Exception e) {
/*  87 */       log.error(e, "Load setting error!", new Object[0]);
/*  88 */       return false;
/*     */     } finally {
/*  90 */       IoUtil.close(settingStream);
/*     */     } 
/*  92 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized boolean load(InputStream settingStream) throws IOException {
/* 103 */     this.groupedMap.clear();
/* 104 */     BufferedReader reader = null;
/*     */     try {
/* 106 */       reader = IoUtil.getReader(settingStream, this.charset);
/*     */       
/* 108 */       String group = null;
/*     */ 
/*     */       
/*     */       while (true) {
/* 112 */         String line = reader.readLine();
/* 113 */         if (line == null) {
/*     */           break;
/*     */         }
/* 116 */         line = line.trim();
/*     */         
/* 118 */         if (StrUtil.isBlank(line) || StrUtil.startWith(line, '#')) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */         
/* 123 */         if (StrUtil.isSurround(line, '[', ']')) {
/* 124 */           group = line.substring(1, line.length() - 1).trim();
/*     */           
/*     */           continue;
/*     */         } 
/* 128 */         String[] keyValue = StrUtil.splitToArray(line, this.assignFlag, 2);
/*     */         
/* 130 */         if (keyValue.length < 2) {
/*     */           continue;
/*     */         }
/*     */         
/* 134 */         String value = keyValue[1].trim();
/*     */         
/* 136 */         if (this.isUseVariable) {
/* 137 */           value = replaceVar(group, value);
/*     */         }
/* 139 */         this.groupedMap.put(group, keyValue[0].trim(), value);
/*     */       } 
/*     */     } finally {
/* 142 */       IoUtil.close(reader);
/*     */     } 
/* 144 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setVarRegex(String regex) {
/* 154 */     this.varRegex = regex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAssignFlag(char assignFlag) {
/* 164 */     this.assignFlag = assignFlag;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void store(String absolutePath) {
/* 174 */     store(FileUtil.touch(absolutePath));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void store(File file) {
/* 185 */     Assert.notNull(file, "File to store must be not null !", new Object[0]);
/* 186 */     log.debug("Store Setting to [{}]...", new Object[] { file.getAbsolutePath() });
/* 187 */     PrintWriter writer = null;
/*     */     try {
/* 189 */       writer = FileUtil.getPrintWriter(file, this.charset, false);
/* 190 */       store(writer);
/*     */     } finally {
/* 192 */       IoUtil.close(writer);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized void store(PrintWriter writer) {
/* 202 */     for (Map.Entry<String, LinkedHashMap<String, String>> groupEntry : this.groupedMap.entrySet()) {
/* 203 */       writer.println(StrUtil.format("{}{}{}", new Object[] { Character.valueOf('['), groupEntry.getKey(), Character.valueOf(']') }));
/* 204 */       for (Map.Entry<String, String> entry : (Iterable<Map.Entry<String, String>>)((LinkedHashMap)groupEntry.getValue()).entrySet()) {
/* 205 */         writer.println(StrUtil.format("{} {} {}", new Object[] { entry.getKey(), Character.valueOf(this.assignFlag), entry.getValue() }));
/*     */       } 
/*     */     } 
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
/*     */   private String replaceVar(String group, String value) {
/* 220 */     Set<String> vars = (Set<String>)ReUtil.findAll(this.varRegex, value, 0, new HashSet());
/*     */     
/* 222 */     for (String var : vars) {
/* 223 */       String key = ReUtil.get(this.varRegex, var, 1);
/* 224 */       if (StrUtil.isNotBlank(key)) {
/*     */         
/* 226 */         String varValue = this.groupedMap.get(group, key);
/*     */         
/* 228 */         if (null == varValue) {
/* 229 */           List<String> groupAndKey = StrUtil.split(key, '.', 2);
/* 230 */           if (groupAndKey.size() > 1) {
/* 231 */             varValue = this.groupedMap.get(groupAndKey.get(0), groupAndKey.get(1));
/*     */           }
/*     */         } 
/*     */         
/* 235 */         if (null == varValue) {
/* 236 */           varValue = SystemPropsUtil.get(key);
/*     */         }
/*     */         
/* 239 */         if (null != varValue)
/*     */         {
/* 241 */           value = value.replace(var, varValue);
/*     */         }
/*     */       } 
/*     */     } 
/* 245 */     return value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\setting\SettingLoader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */