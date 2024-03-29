package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface SoundCard {
   String getDriverVersion();

   String getName();

   String getCodec();
}
