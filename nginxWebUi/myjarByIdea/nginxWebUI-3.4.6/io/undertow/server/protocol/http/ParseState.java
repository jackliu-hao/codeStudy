package io.undertow.server.protocol.http;

import io.undertow.util.HttpString;

class ParseState {
   public static final int VERB = 0;
   public static final int PATH = 1;
   public static final int PATH_PARAMETERS = 2;
   public static final int QUERY_PARAMETERS = 3;
   public static final int VERSION = 4;
   public static final int AFTER_VERSION = 5;
   public static final int HEADER = 6;
   public static final int HEADER_VALUE = 7;
   public static final int PARSE_COMPLETE = 8;
   int state;
   int parseState = 0;
   HttpString current;
   byte[] currentBytes;
   int pos = 0;
   boolean urlDecodeRequired = false;
   final StringBuilder stringBuilder = new StringBuilder();
   final StringBuilder canonicalPath = new StringBuilder();
   byte leftOver;
   HttpString nextHeader;
   String nextQueryParam;
   int mapCount;
   final StringBuilder decodeBuffer = new StringBuilder();
   final CacheMap<HttpString, String> headerValuesCache;

   ParseState(int cacheSize) {
      if (cacheSize <= 0) {
         this.headerValuesCache = null;
      } else {
         this.headerValuesCache = new CacheMap(cacheSize);
      }

   }

   public boolean isComplete() {
      return this.state == 8;
   }

   public final void parseComplete() {
      this.state = 8;
   }

   public void reset() {
      this.state = 0;
      this.parseState = 0;
      this.current = null;
      this.currentBytes = null;
      this.pos = 0;
      this.leftOver = 0;
      this.urlDecodeRequired = false;
      this.stringBuilder.setLength(0);
      this.nextHeader = null;
      this.nextQueryParam = null;
      this.mapCount = 0;
   }
}
