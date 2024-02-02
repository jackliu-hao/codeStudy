package org.xnio.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExtendedHandshakeChecker {
  void checkHandshakeExtended(Map<String, List<String>> paramMap) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\http\ExtendedHandshakeChecker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */