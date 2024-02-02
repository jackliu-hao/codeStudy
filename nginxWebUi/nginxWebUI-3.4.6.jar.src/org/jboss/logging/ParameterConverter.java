package org.jboss.logging;

import java.util.Locale;

@Deprecated
public interface ParameterConverter<I> {
  Object convert(Locale paramLocale, I paramI);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\ParameterConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */