package org.xnio;

public interface XnioProvider {
   Xnio getInstance();

   String getName();
}
