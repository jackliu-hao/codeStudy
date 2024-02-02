package cn.hutool.core.net;

import cn.hutool.core.codec.PercentCodec;

public class RFC3986 {
   public static final PercentCodec GEN_DELIMS = PercentCodec.of((CharSequence)":/?#[]@");
   public static final PercentCodec SUB_DELIMS = PercentCodec.of((CharSequence)"!$&'()*+,;=");
   public static final PercentCodec RESERVED;
   public static final PercentCodec UNRESERVED;
   public static final PercentCodec PCHAR;
   public static final PercentCodec SEGMENT;
   public static final PercentCodec SEGMENT_NZ_NC;
   public static final PercentCodec PATH;
   public static final PercentCodec QUERY;
   public static final PercentCodec FRAGMENT;
   public static final PercentCodec QUERY_PARAM_VALUE;
   public static final PercentCodec QUERY_PARAM_NAME;

   private static StringBuilder unreservedChars() {
      StringBuilder sb = new StringBuilder();

      char c;
      for(c = 'A'; c <= 'Z'; ++c) {
         sb.append(c);
      }

      for(c = 'a'; c <= 'z'; ++c) {
         sb.append(c);
      }

      for(c = '0'; c <= '9'; ++c) {
         sb.append(c);
      }

      sb.append("_.-~");
      return sb;
   }

   static {
      RESERVED = GEN_DELIMS.orNew(SUB_DELIMS);
      UNRESERVED = PercentCodec.of((CharSequence)unreservedChars());
      PCHAR = UNRESERVED.orNew(SUB_DELIMS).or(PercentCodec.of((CharSequence)":@"));
      SEGMENT = PCHAR;
      SEGMENT_NZ_NC = PercentCodec.of(SEGMENT).removeSafe(':');
      PATH = SEGMENT.orNew(PercentCodec.of((CharSequence)"/"));
      QUERY = PCHAR.orNew(PercentCodec.of((CharSequence)"/?"));
      FRAGMENT = QUERY;
      QUERY_PARAM_VALUE = PercentCodec.of(QUERY).removeSafe('&');
      QUERY_PARAM_NAME = PercentCodec.of(QUERY_PARAM_VALUE).removeSafe('=');
   }
}
