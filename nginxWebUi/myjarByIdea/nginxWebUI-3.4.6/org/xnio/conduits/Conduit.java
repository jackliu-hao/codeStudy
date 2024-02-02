package org.xnio.conduits;

import org.xnio.XnioWorker;

public interface Conduit {
   XnioWorker getWorker();
}
