package com.epam.dlab.automation.cloud.azure.oauth2;

import com.epam.dlab.automation.helper.ConfigPropertyValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.MicrosoftAzureActiveDirectoryApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class AzureActiveDirectoryExample {

    private static final Logger LOGGER = LogManager.getLogger(AzureActiveDirectoryExample.class);

    private static final String NETWORK_NAME = "Azure Active Directory";
    private static final String PROTECTED_RESOURCE_URL = "https://graph.windows.net/me?api-version=1.6";

    private AzureActiveDirectoryExample() {}

    public static void main(String... args) throws IOException, InterruptedException, ExecutionException {

        Path path = Paths.get(ConfigPropertyValue.getAzureAuthFileName());
        if (path.toFile().exists()) {

            AzureAuthFile azureAuthFile;
            try {
                azureAuthFile = new ObjectMapper().readValue(path.toFile(), AzureAuthFile.class);
            } catch (IOException e) {
                LOGGER.error("Cannot read Azure authentication file", e);
                throw e;
            }
            LOGGER.info("Configs from auth file are used");

            // Replace these with your client id and secret
            final OAuth20Service service = new ServiceBuilder(azureAuthFile.getClientId())
                    .apiSecret(azureAuthFile.getClientSecret())
                    .scope("openid")
                    .callback("http://www.example.com/oauth_callback/")
                    .build(MicrosoftAzureActiveDirectoryApi.instance());
            final Scanner in = new Scanner(System.in, "UTF-8");

            System.out.println("=== " + NETWORK_NAME + "'s OAuth Workflow ===");
            System.out.println();

            // Obtain the Authorization URL
            System.out.println("Fetching the Authorization URL...");
            final String authorizationUrl = service.getAuthorizationUrl();
            System.out.println("Got the Authorization URL!");
            System.out.println("Now go and authorize ScribeJava here:");
            System.out.println(authorizationUrl);
            System.out.println("And paste the authorization code here");
            System.out.print(">>");
            final String code = in.nextLine();
            System.out.println();

            // Trade the Request Token and Verfier for the Access Token
            System.out.println("Trading the Request Token for an Access Token...");
            final OAuth2AccessToken accessToken = service.getAccessToken(code);
            System.out.println("Got the Access Token!");
            System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
            System.out.println();

            // Now let's go and ask for a protected resource!
            System.out.println("Now we're going to access a protected resource...");
            final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
            service.signRequest(accessToken, request);
            final Response response = service.execute(request);
            System.out.println("Got it! Lets see what we found...");
            System.out.println();
            System.out.println(response.getCode());
            System.out.println(response.getBody());

            System.out.println();
            System.out.println("Thats it man! Go and build something awesome with ScribeJava! :)");
        }


    }
}
