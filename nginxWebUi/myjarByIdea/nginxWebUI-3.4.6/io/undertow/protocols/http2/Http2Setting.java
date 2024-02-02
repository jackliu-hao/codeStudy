package io.undertow.protocols.http2;

public class Http2Setting {
   public static final int SETTINGS_HEADER_TABLE_SIZE = 1;
   public static final int SETTINGS_ENABLE_PUSH = 2;
   public static final int SETTINGS_MAX_CONCURRENT_STREAMS = 3;
   public static final int SETTINGS_INITIAL_WINDOW_SIZE = 4;
   public static final int SETTINGS_MAX_FRAME_SIZE = 5;
   public static final int SETTINGS_MAX_HEADER_LIST_SIZE = 6;
   private final int id;
   private final long value;

   Http2Setting(int id, long value) {
      this.id = id;
      this.value = value;
   }

   public int getId() {
      return this.id;
   }

   public long getValue() {
      return this.value;
   }
}
