package freemarker.ext.servlet;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;

final class InitParamParser {
   static final String TEMPLATE_PATH_PREFIX_CLASS = "class://";
   static final String TEMPLATE_PATH_PREFIX_CLASSPATH = "classpath:";
   static final String TEMPLATE_PATH_PREFIX_FILE = "file://";
   static final String TEMPLATE_PATH_SETTINGS_BI_NAME = "settings";
   private static final Logger LOG = Logger.getLogger("freemarker.servlet");

   private InitParamParser() {
   }

   static TemplateLoader createTemplateLoader(String templatePath, Configuration cfg, Class classLoaderClass, ServletContext srvCtx) throws IOException {
      int settingAssignmentsStart = findTemplatePathSettingAssignmentsStart(templatePath);
      String pureTemplatePath = (settingAssignmentsStart == -1 ? templatePath : templatePath.substring(0, settingAssignmentsStart)).trim();
      Object templateLoader;
      String commaSepItems;
      if (pureTemplatePath.startsWith("class://")) {
         commaSepItems = pureTemplatePath.substring("class://".length());
         commaSepItems = normalizeToAbsolutePackagePath(commaSepItems);
         templateLoader = new ClassTemplateLoader(classLoaderClass, commaSepItems);
      } else if (pureTemplatePath.startsWith("classpath:")) {
         commaSepItems = pureTemplatePath.substring("classpath:".length());
         commaSepItems = normalizeToAbsolutePackagePath(commaSepItems);
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         if (classLoader == null) {
            LOG.warn("No Thread Context Class Loader was found. Falling back to the class loader of " + classLoaderClass.getName() + ".");
            classLoader = classLoaderClass.getClassLoader();
         }

         templateLoader = new ClassTemplateLoader(classLoader, commaSepItems);
      } else if (pureTemplatePath.startsWith("file://")) {
         commaSepItems = pureTemplatePath.substring("file://".length());
         templateLoader = new FileTemplateLoader(new File(commaSepItems));
      } else if (pureTemplatePath.startsWith("[") && cfg.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_22) {
         if (!pureTemplatePath.endsWith("]")) {
            throw new TemplatePathParsingException("Failed to parse template path; closing \"]\" is missing.");
         }

         commaSepItems = pureTemplatePath.substring(1, pureTemplatePath.length() - 1).trim();
         List listItems = parseCommaSeparatedTemplatePaths(commaSepItems);
         TemplateLoader[] templateLoaders = new TemplateLoader[listItems.size()];

         for(int i = 0; i < listItems.size(); ++i) {
            String pathItem = (String)listItems.get(i);
            templateLoaders[i] = createTemplateLoader(pathItem, cfg, classLoaderClass, srvCtx);
         }

         templateLoader = new MultiTemplateLoader(templateLoaders);
      } else {
         if (pureTemplatePath.startsWith("{") && cfg.getIncompatibleImprovements().intValue() >= _TemplateAPI.VERSION_INT_2_3_22) {
            throw new TemplatePathParsingException("Template paths starting with \"{\" are reseved for future purposes");
         }

         templateLoader = new WebappTemplateLoader(srvCtx, pureTemplatePath);
      }

      if (settingAssignmentsStart != -1) {
         try {
            int nextPos = _ObjectBuilderSettingEvaluator.configureBean(templatePath, templatePath.indexOf(40, settingAssignmentsStart) + 1, templateLoader, _SettingEvaluationEnvironment.getCurrent());
            if (nextPos != templatePath.length()) {
               throw new TemplatePathParsingException("Template path should end after the setting list in: " + templatePath);
            }
         } catch (Exception var12) {
            throw new TemplatePathParsingException("Failed to set properties in: " + templatePath, var12);
         }
      }

      return (TemplateLoader)templateLoader;
   }

   static String normalizeToAbsolutePackagePath(String path) {
      while(path.startsWith("/")) {
         path = path.substring(1);
      }

      return "/" + path;
   }

   static List parseCommaSeparatedList(String value) throws ParseException {
      List valuesList = new ArrayList();
      String[] values = StringUtil.split(value, ',');

      for(int i = 0; i < values.length; ++i) {
         String s = values[i].trim();
         if (s.length() != 0) {
            valuesList.add(s);
         } else if (i != values.length - 1) {
            throw new ParseException("Missing list item berfore a comma", -1);
         }
      }

      return valuesList;
   }

   static List parseCommaSeparatedPatterns(String value) throws ParseException {
      List values = parseCommaSeparatedList(value);
      List patterns = new ArrayList(values.size());

      for(int i = 0; i < values.size(); ++i) {
         patterns.add(Pattern.compile((String)values.get(i)));
      }

      return patterns;
   }

   static List parseCommaSeparatedTemplatePaths(String commaSepItems) {
      ArrayList listItems;
      int prevComaIdx;
      for(listItems = new ArrayList(); commaSepItems.length() != 0; commaSepItems = prevComaIdx != -1 ? commaSepItems.substring(0, prevComaIdx).trim() : "") {
         int itemSettingAssignmentsStart = findTemplatePathSettingAssignmentsStart(commaSepItems);
         int pureItemEnd = itemSettingAssignmentsStart != -1 ? itemSettingAssignmentsStart : commaSepItems.length();
         prevComaIdx = commaSepItems.lastIndexOf(44, pureItemEnd - 1);
         int itemStart = prevComaIdx != -1 ? prevComaIdx + 1 : 0;
         String item = commaSepItems.substring(itemStart).trim();
         if (item.length() != 0) {
            listItems.add(0, item);
         } else if (listItems.size() > 0) {
            throw new TemplatePathParsingException("Missing list item before a comma");
         }
      }

      return listItems;
   }

   static int findTemplatePathSettingAssignmentsStart(String s) {
      int pos;
      for(pos = s.length() - 1; pos >= 0 && Character.isWhitespace(s.charAt(pos)); --pos) {
      }

      if (pos >= 0 && s.charAt(pos) == ')') {
         --pos;
         int parLevel = 1;

         int biNameEnd;
         for(int mode = 0; parLevel > 0; --pos) {
            if (pos < 0) {
               return -1;
            }

            biNameEnd = s.charAt(pos);
            switch (mode) {
               case 0:
                  switch (biNameEnd) {
                     case 34:
                        mode = 2;
                     case 35:
                     case 36:
                     case 37:
                     case 38:
                     default:
                        continue;
                     case 39:
                        mode = 1;
                        continue;
                     case 40:
                        --parLevel;
                        continue;
                     case 41:
                        ++parLevel;
                        continue;
                  }
               case 1:
                  if (biNameEnd == 39 && (pos <= 0 || s.charAt(pos - 1) != '\\')) {
                     mode = 0;
                  }
                  break;
               case 2:
                  if (biNameEnd == 34 && (pos <= 0 || s.charAt(pos - 1) != '\\')) {
                     mode = 0;
                  }
            }
         }

         while(pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
            --pos;
         }

         for(biNameEnd = pos + 1; pos >= 0 && Character.isJavaIdentifierPart(s.charAt(pos)); --pos) {
         }

         int biNameStart = pos + 1;
         if (biNameStart == biNameEnd) {
            return -1;
         } else {
            String biName;
            for(biName = s.substring(biNameStart, biNameEnd); pos >= 0 && Character.isWhitespace(s.charAt(pos)); --pos) {
            }

            if (pos >= 0 && s.charAt(pos) == '?') {
               if (!biName.equals("settings")) {
                  throw new TemplatePathParsingException(StringUtil.jQuote(biName) + " is unexpected after the \"?\". Expected \"" + "settings" + "\".");
               } else {
                  return pos;
               }
            } else {
               return -1;
            }
         }
      } else {
         return -1;
      }
   }

   private static final class TemplatePathParsingException extends RuntimeException {
      public TemplatePathParsingException(String message, Throwable cause) {
         super(message, cause);
      }

      public TemplatePathParsingException(String message) {
         super(message);
      }
   }
}
