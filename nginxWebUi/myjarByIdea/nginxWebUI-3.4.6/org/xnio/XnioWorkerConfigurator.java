package org.xnio;

import java.io.IOException;

public interface XnioWorkerConfigurator {
   XnioWorker createWorker() throws IOException;
}
