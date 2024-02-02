package cn.hutool.extra.template;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

public interface Template {
   void render(Map<?, ?> var1, Writer var2);

   void render(Map<?, ?> var1, OutputStream var2);

   void render(Map<?, ?> var1, File var2);

   String render(Map<?, ?> var1);
}
