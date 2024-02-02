package org.noear.solon.core.message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.noear.solon.core.util.PathAnalyzer;
import org.noear.solon.core.util.PathUtil;

public class ListenerHolder implements Listener {
   private Listener listener;
   private PathAnalyzer pathAnalyzer;
   private List<String> pathKeys;

   public ListenerHolder(String path, Listener listener) {
      this.listener = listener;
      if (path != null && path.indexOf("{") >= 0) {
         path = PathUtil.mergePath((String)null, path);
         this.pathKeys = new ArrayList();
         Matcher pm = PathUtil.pathKeyExpr.matcher(path);

         while(pm.find()) {
            this.pathKeys.add(pm.group(1));
         }

         if (this.pathKeys.size() > 0) {
            this.pathAnalyzer = PathAnalyzer.get(path);
         }
      }

   }

   public void onOpen(Session s) {
      if (this.pathAnalyzer != null) {
         Matcher pm = this.pathAnalyzer.matcher(s.pathNew());
         if (pm.find()) {
            int i = 0;

            for(int len = this.pathKeys.size(); i < len; ++i) {
               s.paramSet((String)this.pathKeys.get(i), pm.group(i + 1));
            }
         }
      }

      this.listener.onOpen(s);
   }

   public void onMessage(Session s, Message m) throws IOException {
      this.listener.onMessage(s, m);
   }

   public void onClose(Session s) {
      this.listener.onClose(s);
   }

   public void onError(Session s, Throwable e) {
      this.listener.onError(s, e);
   }
}
