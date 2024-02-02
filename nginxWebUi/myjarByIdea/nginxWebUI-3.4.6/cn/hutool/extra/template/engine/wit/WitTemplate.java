package cn.hutool.extra.template.engine.wit;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import org.febit.wit.Template;

public class WitTemplate extends AbstractTemplate implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Template rawTemplate;

   public static WitTemplate wrap(Template witTemplate) {
      return null == witTemplate ? null : new WitTemplate(witTemplate);
   }

   public WitTemplate(Template witTemplate) {
      this.rawTemplate = witTemplate;
   }

   public void render(Map<?, ?> bindingMap, Writer writer) {
      Map<String, Object> map = (Map)Convert.convert((TypeReference)(new TypeReference<Map<String, Object>>() {
      }), bindingMap);
      this.rawTemplate.merge(map, writer);
   }

   public void render(Map<?, ?> bindingMap, OutputStream out) {
      Map<String, Object> map = (Map)Convert.convert((TypeReference)(new TypeReference<Map<String, Object>>() {
      }), bindingMap);
      this.rawTemplate.merge(map, out);
   }
}
