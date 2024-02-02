package org.noear.solon.view.freemarker.tags;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.io.IOException;
import java.util.Map;
import org.noear.solon.Utils;
import org.noear.solon.auth.AuthUtil;
import org.noear.solon.auth.annotation.Logical;
import org.noear.solon.core.NvMap;

public class AuthRolesTag implements TemplateDirectiveModel {
   public void execute(Environment env, Map map, TemplateModel[] templateModels, TemplateDirectiveBody body) throws TemplateException, IOException {
      NvMap mapExt = new NvMap(map);
      String nameStr = (String)mapExt.get("name");
      String logicalStr = (String)mapExt.get("logical");
      if (!Utils.isEmpty(nameStr)) {
         String[] names = nameStr.split(",");
         if (names.length != 0) {
            if (AuthUtil.verifyRoles(names, Logical.of(logicalStr))) {
               body.render(env.getOut());
            }

         }
      }
   }
}
