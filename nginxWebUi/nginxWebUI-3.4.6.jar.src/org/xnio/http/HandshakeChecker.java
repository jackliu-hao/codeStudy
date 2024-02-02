package org.xnio.http;

import java.io.IOException;
import java.util.Map;

public interface HandshakeChecker {
  void checkHandshake(Map<String, String> paramMap) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\http\HandshakeChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */