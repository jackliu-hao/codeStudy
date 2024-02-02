package cn.hutool.extra.ssh;

public enum ChannelType {
   SESSION("session"),
   SHELL("shell"),
   EXEC("exec"),
   X11("x11"),
   AGENT_FORWARDING("auth-agent@openssh.com"),
   DIRECT_TCPIP("direct-tcpip"),
   FORWARDED_TCPIP("forwarded-tcpip"),
   SFTP("sftp"),
   SUBSYSTEM("subsystem");

   private final String value;

   private ChannelType(String value) {
      this.value = value;
   }

   public String getValue() {
      return this.value;
   }
}
