package org.noear.solon.core.message;

import org.noear.solon.annotation.Note;

public interface MessageFlag {
   @Note("容器包（用于二次编码）")
   int container = 1;
   @Note("消息包")
   int message = 10;
   @Note("心跳消息包")
   int heartbeat = 11;
   @Note("握手消息包")
   int handshake = 12;
   @Note("响应体消息包")
   int response = 13;
}
