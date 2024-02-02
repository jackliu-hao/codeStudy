package org.xnio.http;

import java.io.IOException;
import java.util.Map;

public interface HandshakeChecker {
   void checkHandshake(Map<String, String> var1) throws IOException;
}
