package org.noear.solon.socketd;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import org.noear.solon.core.message.Message;

public class RequestManager {
   public static int REQUEST_AND_RESPONSE_TIMEOUT_SECONDS = 30;
   private static Map<String, CompletableFuture<Message>> requests = new ConcurrentHashMap();

   public static void register(Message message, CompletableFuture<Message> future) {
      requests.putIfAbsent(message.key(), future);
   }

   public static CompletableFuture<Message> get(String key) {
      return (CompletableFuture)requests.get(key);
   }

   public static void remove(String key) {
      requests.remove(key);
   }
}
