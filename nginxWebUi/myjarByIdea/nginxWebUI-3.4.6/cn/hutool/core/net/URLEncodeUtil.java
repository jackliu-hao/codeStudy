package cn.hutool.core.net;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;

public class URLEncodeUtil {
   public static String encodeAll(String url) {
      return encodeAll(url, CharsetUtil.CHARSET_UTF_8);
   }

   public static String encodeAll(String url, Charset charset) throws UtilException {
      return RFC3986.UNRESERVED.encode(url, charset);
   }

   public static String encode(String url) throws UtilException {
      return encode(url, CharsetUtil.CHARSET_UTF_8);
   }

   public static String encode(String url, Charset charset) {
      return RFC3986.PATH.encode(url, charset);
   }

   public static String encodeQuery(String url) throws UtilException {
      return encodeQuery(url, CharsetUtil.CHARSET_UTF_8);
   }

   public static String encodeQuery(String url, Charset charset) {
      return RFC3986.QUERY.encode(url, charset);
   }

   public static String encodePathSegment(String url) throws UtilException {
      return encodePathSegment(url, CharsetUtil.CHARSET_UTF_8);
   }

   public static String encodePathSegment(String url, Charset charset) {
      return StrUtil.isEmpty(url) ? url : RFC3986.SEGMENT.encode(url, charset);
   }

   public static String encodeFragment(String url) throws UtilException {
      return encodeFragment(url, CharsetUtil.CHARSET_UTF_8);
   }

   public static String encodeFragment(String url, Charset charset) {
      return StrUtil.isEmpty(url) ? url : RFC3986.FRAGMENT.encode(url, charset);
   }
}
