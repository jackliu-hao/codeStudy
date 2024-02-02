package cn.hutool.core.net.url;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.RFC3986;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class UrlPath {
   private List<String> segments;
   private boolean withEngTag;

   public static UrlPath of(CharSequence pathStr, Charset charset) {
      UrlPath urlPath = new UrlPath();
      urlPath.parse(pathStr, charset);
      return urlPath;
   }

   public UrlPath setWithEndTag(boolean withEngTag) {
      this.withEngTag = withEngTag;
      return this;
   }

   public List<String> getSegments() {
      return this.segments;
   }

   public String getSegment(int index) {
      return null != this.segments && index < this.segments.size() ? (String)this.segments.get(index) : null;
   }

   public UrlPath add(CharSequence segment) {
      this.addInternal(fixPath(segment), false);
      return this;
   }

   public UrlPath addBefore(CharSequence segment) {
      this.addInternal(fixPath(segment), true);
      return this;
   }

   public UrlPath parse(CharSequence path, Charset charset) {
      if (StrUtil.isNotEmpty(path)) {
         if (StrUtil.endWith(path, '/')) {
            this.withEngTag = true;
         }

         CharSequence path = fixPath(path);
         if (StrUtil.isNotEmpty(path)) {
            List<String> split = StrUtil.split(path, '/');
            Iterator var4 = split.iterator();

            while(var4.hasNext()) {
               String seg = (String)var4.next();
               this.addInternal(URLDecoder.decodeForPath(seg, charset), false);
            }
         }
      }

      return this;
   }

   public String build(Charset charset) {
      return this.build(charset, true);
   }

   public String build(Charset charset, boolean encodePercent) {
      if (CollUtil.isEmpty((Collection)this.segments)) {
         return "";
      } else {
         char[] safeChars = encodePercent ? null : new char[]{'%'};
         StringBuilder builder = new StringBuilder();
         Iterator var5 = this.segments.iterator();

         while(var5.hasNext()) {
            String segment = (String)var5.next();
            if (builder.length() == 0) {
               builder.append('/').append(RFC3986.SEGMENT_NZ_NC.encode(segment, charset, safeChars));
            } else {
               builder.append('/').append(RFC3986.SEGMENT.encode(segment, charset, safeChars));
            }
         }

         if (StrUtil.isEmpty(builder)) {
            builder.append('/');
         } else if (this.withEngTag && !StrUtil.endWith(builder, '/')) {
            builder.append('/');
         }

         return builder.toString();
      }
   }

   public String toString() {
      return this.build((Charset)null);
   }

   private void addInternal(CharSequence segment, boolean before) {
      if (this.segments == null) {
         this.segments = new LinkedList();
      }

      String seg = StrUtil.str(segment);
      if (before) {
         this.segments.add(0, seg);
      } else {
         this.segments.add(seg);
      }

   }

   private static String fixPath(CharSequence path) {
      Assert.notNull(path, "Path segment must be not null!");
      if ("/".contentEquals(path)) {
         return "";
      } else {
         String segmentStr = StrUtil.trim(path);
         segmentStr = StrUtil.removePrefix(segmentStr, "/");
         segmentStr = StrUtil.removeSuffix(segmentStr, "/");
         segmentStr = StrUtil.trim(segmentStr);
         return segmentStr;
      }
   }
}
