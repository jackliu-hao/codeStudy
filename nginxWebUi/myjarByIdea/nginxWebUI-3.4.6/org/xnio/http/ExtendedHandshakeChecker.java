package org.xnio.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ExtendedHandshakeChecker {
   void checkHandshakeExtended(Map<String, List<String>> var1) throws IOException;
}
