package freemarker.core;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;

public class _SettingEvaluationEnvironment {
   private static final ThreadLocal CURRENT = new ThreadLocal();
   private BeansWrapper objectWrapper;

   public static _SettingEvaluationEnvironment getCurrent() {
      Object r = CURRENT.get();
      return r != null ? (_SettingEvaluationEnvironment)r : new _SettingEvaluationEnvironment();
   }

   public static _SettingEvaluationEnvironment startScope() {
      Object previous = CURRENT.get();
      CURRENT.set(new _SettingEvaluationEnvironment());
      return (_SettingEvaluationEnvironment)previous;
   }

   public static void endScope(_SettingEvaluationEnvironment previous) {
      CURRENT.set(previous);
   }

   public BeansWrapper getObjectWrapper() {
      if (this.objectWrapper == null) {
         this.objectWrapper = new BeansWrapper(Configuration.VERSION_2_3_21);
      }

      return this.objectWrapper;
   }
}
