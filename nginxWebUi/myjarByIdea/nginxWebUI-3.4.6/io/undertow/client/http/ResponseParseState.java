package io.undertow.client.http;

import io.undertow.util.HttpString;

class ResponseParseState {
   public static final int VERSION = 0;
   public static final int STATUS_CODE = 1;
   public static final int REASON_PHRASE = 2;
   public static final int AFTER_REASON_PHRASE = 3;
   public static final int HEADER = 4;
   public static final int HEADER_VALUE = 5;
   public static final int PARSE_COMPLETE = 6;
   int state;
   int parseState = 0;
   HttpString current;
   byte[] currentBytes;
   int pos = 0;
   final StringBuilder stringBuilder = new StringBuilder();
   byte leftOver;
   HttpString nextHeader;

   public boolean isComplete() {
      return this.state == 6;
   }

   public final void parseComplete() {
      this.state = 6;
   }
}
