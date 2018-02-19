package com.epam.dlab.automation.cloud.azure.oauth2;

import com.github.scribejava.core.builder.api.ClientAuthenticationType;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;

/**
 * Microsoft Azure Active Directory Api
 *
 * @see <a href="https://docs.microsoft.com/en-us/azure/active-directory/develop/active-directory-protocols-oauth-code">
 * Understand the OAuth 2.0 authorization code flow in Azure AD | Microsoft Docs</a>
 * @see <a
 * href="https://docs.microsoft.com/en-us/azure/active-directory/develop/active-directory-devquickstarts-webapp-java">
 * Azure AD Java web app Getting Started | Microsoft Docs</a>
 * @see <a href="https://msdn.microsoft.com/en-us/library/azure/ad/graph/api/signed-in-user-operations">
 * Azure AD Graph API Operations on the Signed-in User</a>
 * @see <a href="https://portal.azure.com">https://portal.azure.com</a>
 */
public class AzureActiveDirectoryApi extends DefaultApi20 {

    private static final String MSFT_GRAPH_URL = "https://graph.windows.net";

    private static final String MSFT_LOGIN_URL = "https://login.microsoftonline.com";
    private static final String SLASH = "/";
    private static final String COMMON = "common";
    private static final String TOKEN_URI = "oauth2/token";
    private static final String AUTH_URI = "oauth2/authorize?";
    private static final String CLIENT_ID = "client_id=";
    private static final String REDIRECT_URI = "redirect_uri=";
    private static final String RESPONSE_TYPE = "response_type=";
    private static final String RESPONSE_MODE = "response_mode=";
    private static final String PROMPT = "prompt=";
    private String tenantId;

    public AzureActiveDirectoryApi(String tenantId) {
        this.tenantId = tenantId;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return MSFT_LOGIN_URL + SLASH + COMMON + SLASH + TOKEN_URI;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return MSFT_LOGIN_URL + SLASH + tenantId + SLASH + AUTH_URI;
    }

    @Override
    public AzureActiveDirectoryService createService(OAuthConfig config) {
        return new AzureActiveDirectoryService(this, config);
    }

    @Override
    public ClientAuthenticationType getClientAuthenticationType() {
        return ClientAuthenticationType.REQUEST_BODY;
    }
}