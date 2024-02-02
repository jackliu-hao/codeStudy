package cn.hutool.setting;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.SystemPropsUtil;
import cn.hutool.log.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SettingLoader {
   private static final Log log = Log.get();
   private static final char COMMENT_FLAG_PRE = '#';
   private char assignFlag;
   private String varRegex;
   private final Charset charset;
   private final boolean isUseVariable;
   private final GroupedMap groupedMap;

   public SettingLoader(GroupedMap groupedMap) {
      this(groupedMap, CharsetUtil.CHARSET_UTF_8, false);
   }

   public SettingLoader(GroupedMap groupedMap, Charset charset, boolean isUseVariable) {
      this.assignFlag = '=';
      this.varRegex = "\\$\\{(.*?)\\}";
      this.groupedMap = groupedMap;
      this.charset = charset;
      this.isUseVariable = isUseVariable;
   }

   public boolean load(Resource resource) {
      if (resource == null) {
         throw new NullPointerException("Null setting url define!");
      } else {
         log.debug("Load setting file [{}]", new Object[]{resource});
         InputStream settingStream = null;

         boolean var4;
         try {
            settingStream = resource.getStream();
            this.load(settingStream);
            return true;
         } catch (Exception var8) {
            log.error(var8, "Load setting error!", new Object[0]);
            var4 = false;
         } finally {
            IoUtil.close(settingStream);
         }

         return var4;
      }
   }

   public synchronized boolean load(InputStream settingStream) throws IOException {
      this.groupedMap.clear();
      BufferedReader reader = null;

      try {
         reader = IoUtil.getReader(settingStream, this.charset);
         String group = null;

         while(true) {
            String line = reader.readLine();
            if (line == null) {
               return true;
            }

            line = line.trim();
            if (!StrUtil.isBlank(line) && !StrUtil.startWith(line, '#')) {
               if (StrUtil.isSurround(line, '[', ']')) {
                  group = line.substring(1, line.length() - 1).trim();
               } else {
                  String[] keyValue = StrUtil.splitToArray(line, this.assignFlag, 2);
                  if (keyValue.length >= 2) {
                     String value = keyValue[1].trim();
                     if (this.isUseVariable) {
                        value = this.replaceVar(group, value);
                     }

                     this.groupedMap.put(group, keyValue[0].trim(), value);
                  }
               }
            }
         }
      } finally {
         IoUtil.close(reader);
      }
   }

   public void setVarRegex(String regex) {
      this.varRegex = regex;
   }

   public void setAssignFlag(char assignFlag) {
      this.assignFlag = assignFlag;
   }

   public void store(String absolutePath) {
      this.store(FileUtil.touch(absolutePath));
   }

   public void store(File file) {
      Assert.notNull(file, "File to store must be not null !");
      log.debug("Store Setting to [{}]...", new Object[]{file.getAbsolutePath()});
      PrintWriter writer = null;

      try {
         writer = FileUtil.getPrintWriter(file, this.charset, false);
         this.store(writer);
      } finally {
         IoUtil.close(writer);
      }

   }

   private synchronized void store(PrintWriter writer) {
      Iterator var2 = this.groupedMap.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry<String, LinkedHashMap<String, String>> groupEntry = (Map.Entry)var2.next();
         writer.println(StrUtil.format("{}{}{}", new Object[]{'[', groupEntry.getKey(), ']'}));
         Iterator var4 = ((LinkedHashMap)groupEntry.getValue()).entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var4.next();
            writer.println(StrUtil.format("{} {} {}", new Object[]{entry.getKey(), this.assignFlag, entry.getValue()}));
         }
      }

   }

   private String replaceVar(String group, String value) {
      Set<String> vars = (Set)ReUtil.findAll((String)this.varRegex, value, 0, new HashSet());
      Iterator var5 = vars.iterator();

      while(var5.hasNext()) {
         String var = (String)var5.next();
         String key = ReUtil.get((String)this.varRegex, var, 1);
         if (StrUtil.isNotBlank(key)) {
            String varValue = this.groupedMap.get(group, key);
            if (null == varValue) {
               List<String> groupAndKey = StrUtil.split(key, '.', 2);
               if (groupAndKey.size() > 1) {
                  varValue = this.groupedMap.get((String)groupAndKey.get(0), (String)groupAndKey.get(1));
               }
            }

            if (null == varValue) {
               varValue = SystemPropsUtil.get(key);
            }

            if (null != varValue) {
               value = value.replace(var, varValue);
            }
         }
      }

      return value;
   }
}
