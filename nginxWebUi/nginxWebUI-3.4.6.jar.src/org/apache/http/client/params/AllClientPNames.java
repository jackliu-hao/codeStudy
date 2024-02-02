package org.apache.http.client.params;

import org.apache.http.auth.params.AuthPNames;
import org.apache.http.conn.params.ConnConnectionPNames;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.params.CookieSpecPNames;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

@Deprecated
public interface AllClientPNames extends CoreConnectionPNames, CoreProtocolPNames, ClientPNames, AuthPNames, CookieSpecPNames, ConnConnectionPNames, ConnManagerPNames, ConnRoutePNames {}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\params\AllClientPNames.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */