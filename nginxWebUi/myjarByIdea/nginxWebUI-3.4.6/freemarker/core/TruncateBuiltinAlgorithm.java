package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

public abstract class TruncateBuiltinAlgorithm {
   public abstract TemplateModel truncateM(String var1, int var2, TemplateModel var3, Integer var4, Environment var5) throws TemplateException;

   public abstract TemplateScalarModel truncate(String var1, int var2, TemplateScalarModel var3, Integer var4, Environment var5) throws TemplateException;

   public abstract TemplateScalarModel truncateW(String var1, int var2, TemplateScalarModel var3, Integer var4, Environment var5) throws TemplateException;

   public abstract TemplateModel truncateWM(String var1, int var2, TemplateModel var3, Integer var4, Environment var5) throws TemplateException;

   public abstract TemplateScalarModel truncateC(String var1, int var2, TemplateScalarModel var3, Integer var4, Environment var5) throws TemplateException;

   public abstract TemplateModel truncateCM(String var1, int var2, TemplateModel var3, Integer var4, Environment var5) throws TemplateException;
}
