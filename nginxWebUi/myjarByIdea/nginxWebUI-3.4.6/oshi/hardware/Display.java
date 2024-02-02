package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface Display {
   byte[] getEdid();
}
