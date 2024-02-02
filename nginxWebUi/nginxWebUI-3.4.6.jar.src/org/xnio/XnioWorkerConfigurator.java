package org.xnio;

import java.io.IOException;

public interface XnioWorkerConfigurator {
  XnioWorker createWorker() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\XnioWorkerConfigurator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */