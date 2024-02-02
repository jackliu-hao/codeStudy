package ch.qos.logback.core.spi;

public interface PropertyDefiner extends ContextAware {
   String getPropertyValue();
}
