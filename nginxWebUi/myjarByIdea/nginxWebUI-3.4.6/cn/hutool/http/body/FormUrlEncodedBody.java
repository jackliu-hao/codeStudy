package cn.hutool.http.body;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;
import java.util.Map;

public class FormUrlEncodedBody extends BytesBody {
   public static FormUrlEncodedBody create(Map<String, Object> form, Charset charset) {
      return new FormUrlEncodedBody(form, charset);
   }

   public FormUrlEncodedBody(Map<String, Object> form, Charset charset) {
      super(StrUtil.bytes(UrlQuery.of(form, true).build(charset), charset));
   }
}
