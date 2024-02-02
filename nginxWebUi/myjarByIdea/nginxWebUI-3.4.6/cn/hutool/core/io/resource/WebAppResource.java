package cn.hutool.core.io.resource;

import cn.hutool.core.io.FileUtil;
import java.io.File;

public class WebAppResource extends FileResource {
   private static final long serialVersionUID = 1L;

   public WebAppResource(String path) {
      super(new File(FileUtil.getWebRoot(), path));
   }
}
