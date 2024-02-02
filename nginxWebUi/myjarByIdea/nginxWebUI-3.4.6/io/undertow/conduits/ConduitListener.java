package io.undertow.conduits;

import java.util.EventListener;
import org.xnio.conduits.Conduit;

public interface ConduitListener<T extends Conduit> extends EventListener {
   void handleEvent(T var1);
}
