package cn.hutool.extra.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;

public abstract class AbstractTemplate implements Template {
   public void render(Map<?, ?> bindingMap, File file) {
      BufferedOutputStream out = null;

      try {
         out = FileUtil.getOutputStream(file);
         this.render(bindingMap, out);
      } finally {
         IoUtil.close(out);
      }

   }

   public String render(Map<?, ?> bindingMap) {
      StringWriter writer = new StringWriter();
      this.render(bindingMap, writer);
      return writer.toString();
   }
}
