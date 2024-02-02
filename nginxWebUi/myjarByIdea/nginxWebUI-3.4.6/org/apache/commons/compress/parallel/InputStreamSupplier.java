package org.apache.commons.compress.parallel;

import java.io.InputStream;

public interface InputStreamSupplier {
   InputStream get();
}
