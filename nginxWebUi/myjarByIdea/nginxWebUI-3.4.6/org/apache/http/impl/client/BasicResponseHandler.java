package org.apache.http.impl.client;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicResponseHandler extends AbstractResponseHandler<String> {
   public String handleEntity(HttpEntity entity) throws IOException {
      return EntityUtils.toString(entity);
   }

   public String handleResponse(HttpResponse response) throws HttpResponseException, IOException {
      return (String)super.handleResponse(response);
   }
}
