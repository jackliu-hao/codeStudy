package org.apache.http.client.protocol;

import java.io.IOException;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.DecompressingEntity;
import org.apache.http.client.entity.DeflateInputStreamFactory;
import org.apache.http.client.entity.GZIPInputStreamFactory;
import org.apache.http.client.entity.InputStreamFactory;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.protocol.HttpContext;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class ResponseContentEncoding implements HttpResponseInterceptor {
   public static final String UNCOMPRESSED = "http.client.response.uncompressed";
   private final Lookup<InputStreamFactory> decoderRegistry;
   private final boolean ignoreUnknown;

   public ResponseContentEncoding(Lookup<InputStreamFactory> decoderRegistry, boolean ignoreUnknown) {
      this.decoderRegistry = (Lookup)(decoderRegistry != null ? decoderRegistry : RegistryBuilder.create().register("gzip", GZIPInputStreamFactory.getInstance()).register("x-gzip", GZIPInputStreamFactory.getInstance()).register("deflate", DeflateInputStreamFactory.getInstance()).build());
      this.ignoreUnknown = ignoreUnknown;
   }

   public ResponseContentEncoding(boolean ignoreUnknown) {
      this((Lookup)null, ignoreUnknown);
   }

   public ResponseContentEncoding(Lookup<InputStreamFactory> decoderRegistry) {
      this(decoderRegistry, true);
   }

   public ResponseContentEncoding() {
      this((Lookup)null);
   }

   public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
      HttpEntity entity = response.getEntity();
      HttpClientContext clientContext = HttpClientContext.adapt(context);
      RequestConfig requestConfig = clientContext.getRequestConfig();
      if (requestConfig.isContentCompressionEnabled() && entity != null && entity.getContentLength() != 0L) {
         Header ceheader = entity.getContentEncoding();
         if (ceheader != null) {
            HeaderElement[] codecs = ceheader.getElements();
            HeaderElement[] arr$ = codecs;
            int len$ = codecs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
               HeaderElement codec = arr$[i$];
               String codecname = codec.getName().toLowerCase(Locale.ROOT);
               InputStreamFactory decoderFactory = (InputStreamFactory)this.decoderRegistry.lookup(codecname);
               if (decoderFactory != null) {
                  response.setEntity(new DecompressingEntity(response.getEntity(), decoderFactory));
                  response.removeHeaders("Content-Length");
                  response.removeHeaders("Content-Encoding");
                  response.removeHeaders("Content-MD5");
               } else if (!"identity".equals(codecname) && !this.ignoreUnknown) {
                  throw new HttpException("Unsupported Content-Encoding: " + codec.getName());
               }
            }
         }
      }

   }
}
