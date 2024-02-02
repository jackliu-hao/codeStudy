package org.noear.solon.web.staticfiles;

import java.net.URL;

public interface StaticRepository {
   URL find(String path) throws Exception;
}
