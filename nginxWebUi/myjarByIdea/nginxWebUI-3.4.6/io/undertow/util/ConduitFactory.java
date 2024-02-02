package io.undertow.util;

import org.xnio.conduits.Conduit;

public interface ConduitFactory<C extends Conduit> {
   C create();
}
