package org.noear.solon.core.message;

import org.noear.solon.annotation.Note;

public interface MessageFlag {
  @Note("容器包（用于二次编码）")
  public static final int container = 1;
  
  @Note("消息包")
  public static final int message = 10;
  
  @Note("心跳消息包")
  public static final int heartbeat = 11;
  
  @Note("握手消息包")
  public static final int handshake = 12;
  
  @Note("响应体消息包")
  public static final int response = 13;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\message\MessageFlag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */