package com.cym.utils;

import cn.hutool.core.io.LineHandler;
import cn.hutool.core.io.file.Tailer;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpirationListener;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.noear.solon.annotation.Component;

@Component
public class BLogFileTailer {
   public Map<String, Tailer> tailerMap;
   public Map<String, Vector<String>> lineMap;

   public BLogFileTailer() {
      this.tailerMap = ExpiringMap.builder().expiration(20L, TimeUnit.SECONDS).expirationPolicy(ExpirationPolicy.ACCESSED).expirationListener(new ExpirationListener<String, Tailer>() {
         public void expired(String guid, Tailer tailer) {
            tailer.stop();
            tailer = null;
         }
      }).build();
      this.lineMap = ExpiringMap.builder().expiration(20L, TimeUnit.SECONDS).expirationPolicy(ExpirationPolicy.ACCESSED).build();
   }

   public String run(final String guid, String path) {
      if (this.tailerMap.get(guid) == null) {
         Tailer tailer = new Tailer(new File(path), new LineHandler() {
            public void handle(String line) {
               if (BLogFileTailer.this.lineMap.get(guid) == null) {
                  BLogFileTailer.this.lineMap.put(guid, new Vector());
               }

               ((Vector)BLogFileTailer.this.lineMap.get(guid)).add("<div>" + line + "</div>");
            }
         }, 50);
         tailer.start(true);
         this.tailerMap.put(guid, tailer);
      }

      List<String> list = (List)this.lineMap.get(guid);
      if (list != null && list.size() > 0) {
         while(list.size() > 500) {
            list.remove(0);
         }

         return StrUtil.join("", list);
      } else {
         return "";
      }
   }
}
