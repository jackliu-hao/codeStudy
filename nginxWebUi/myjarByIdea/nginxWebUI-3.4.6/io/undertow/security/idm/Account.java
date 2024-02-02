package io.undertow.security.idm;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

public interface Account extends Serializable {
   Principal getPrincipal();

   Set<String> getRoles();
}
