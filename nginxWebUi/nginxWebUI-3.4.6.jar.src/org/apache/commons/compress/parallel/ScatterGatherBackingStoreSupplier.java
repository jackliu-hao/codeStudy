package org.apache.commons.compress.parallel;

import java.io.IOException;

public interface ScatterGatherBackingStoreSupplier {
  ScatterGatherBackingStore get() throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\parallel\ScatterGatherBackingStoreSupplier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */