package cn.hutool.extra.template.engine.thymeleaf;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.template.AbstractTemplate;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

public class ThymeleafTemplate extends AbstractTemplate implements Serializable {
   private static final long serialVersionUID = 781284916568562509L;
   private final TemplateEngine engine;
   private final String template;
   private final Charset charset;

   public static ThymeleafTemplate wrap(TemplateEngine engine, String template, Charset charset) {
      return null == engine ? null : new ThymeleafTemplate(engine, template, charset);
   }

   public ThymeleafTemplate(TemplateEngine engine, String template, Charset charset) {
      this.engine = engine;
      this.template = template;
      this.charset = (Charset)ObjectUtil.defaultIfNull(charset, (Object)CharsetUtil.CHARSET_UTF_8);
   }

   public void render(Map<?, ?> bindingMap, Writer writer) {
      Map<String, Object> map = (Map)Convert.convert((TypeReference)(new TypeReference<Map<String, Object>>() {
      }), bindingMap);
      Context context = new Context(Locale.getDefault(), map);
      this.engine.process(this.template, context, writer);
   }

   public void render(Map<?, ?> bindingMap, OutputStream out) {
      this.render(bindingMap, (Writer)IoUtil.getWriter(out, this.charset));
   }
}
