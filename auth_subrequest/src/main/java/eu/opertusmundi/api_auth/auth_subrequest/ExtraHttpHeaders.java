package eu.opertusmundi.api_auth.auth_subrequest;

/**
 * The set of extra (x-*) HTTP headers: 
 *  (a) sent to us as part of the auth subrequest, or
 *  (b) sent by us as part of the response 
 */
class ExtraHttpHeaders
{
    //                  
    // request headers
    //
    
    static final String AUTH_REQUEST_REDIRECT_HEADER_NAME = "x-auth-request-redirect"; 
    
    static final String REQUEST_ID_HEADER_NAME = "x-request-id";
    
    static final String ORIG_URL_HEADER_NAME = "x-original-url";
    
    static final String ORIG_METHOD_HEADER_NAME = "x-original-method";
    
    //
    // response headers
    //
    
    static final String REMOTE_USER_HEADER_NAME = "x-remote-user";
    
    static final String ERROR_MESSAGE_HEADER_NAME = "x-error-message";
}
