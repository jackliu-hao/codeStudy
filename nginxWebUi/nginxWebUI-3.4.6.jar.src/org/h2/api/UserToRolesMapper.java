package org.h2.api;

import java.util.Collection;
import org.h2.security.auth.AuthenticationException;
import org.h2.security.auth.AuthenticationInfo;
import org.h2.security.auth.Configurable;

public interface UserToRolesMapper extends Configurable {
  Collection<String> mapUserToRoles(AuthenticationInfo paramAuthenticationInfo) throws AuthenticationException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\api\UserToRolesMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */