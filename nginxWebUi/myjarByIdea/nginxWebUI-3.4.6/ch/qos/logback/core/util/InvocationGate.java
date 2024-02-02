package ch.qos.logback.core.util;

public interface InvocationGate {
   long TIME_UNAVAILABLE = -1L;

   boolean isTooSoon(long var1);
}
