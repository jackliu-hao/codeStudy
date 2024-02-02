package org.jboss.logging;

import java.util.Locale;

/** @deprecated */
@Deprecated
public interface ParameterConverter<I> {
   Object convert(Locale var1, I var2);
}
