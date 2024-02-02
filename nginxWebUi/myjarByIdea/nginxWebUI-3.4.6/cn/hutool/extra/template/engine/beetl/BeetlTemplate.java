package cn.hutool.extra.template.engine.beetl;

import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import org.beetl.core.Template;

public class BeetlTemplate extends AbstractTemplate implements Serializable {
   private static final long serialVersionUID = -8157926902932567280L;
   private final Template rawTemplate;

   public static BeetlTemplate wrap(Template beetlTemplate) {
      return null == beetlTemplate ? null : new BeetlTemplate(beetlTemplate);
   }

   public BeetlTemplate(Template beetlTemplate) {
      this.rawTemplate = beetlTemplate;
   }

   public void render(Map<?, ?> bindingMap, Writer writer) {
      this.rawTemplate.binding(bindingMap);
      this.rawTemplate.renderTo(writer);
   }

   public void render(Map<?, ?> bindingMap, OutputStream out) {
      this.rawTemplate.binding(bindingMap);
      this.rawTemplate.renderTo(out);
   }
}
