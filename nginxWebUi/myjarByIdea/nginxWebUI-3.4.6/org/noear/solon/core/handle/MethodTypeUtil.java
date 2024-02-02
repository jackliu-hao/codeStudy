package org.noear.solon.core.handle;

import java.util.Set;
import java.util.function.Predicate;
import org.noear.solon.annotation.Delete;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Head;
import org.noear.solon.annotation.Http;
import org.noear.solon.annotation.Options;
import org.noear.solon.annotation.Patch;
import org.noear.solon.annotation.Post;
import org.noear.solon.annotation.Put;
import org.noear.solon.annotation.Socket;
import org.noear.solon.annotation.WebSocket;

public class MethodTypeUtil {
   public static Set<MethodType> findAndFill(Set<MethodType> list, Predicate<Class> checker) {
      if (checker.test(Get.class)) {
         list.add(MethodType.GET);
      }

      if (checker.test(Post.class)) {
         list.add(MethodType.POST);
      }

      if (checker.test(Put.class)) {
         list.add(MethodType.PUT);
      }

      if (checker.test(Patch.class)) {
         list.add(MethodType.PATCH);
      }

      if (checker.test(Delete.class)) {
         list.add(MethodType.DELETE);
      }

      if (checker.test(Head.class)) {
         list.add(MethodType.HEAD);
      }

      if (checker.test(Options.class)) {
         list.add(MethodType.OPTIONS);
      }

      if (checker.test(Http.class)) {
         list.add(MethodType.HTTP);
      }

      if (checker.test(Socket.class)) {
         list.add(MethodType.SOCKET);
      }

      if (checker.test(WebSocket.class)) {
         list.add(MethodType.WEBSOCKET);
      }

      return list;
   }
}
