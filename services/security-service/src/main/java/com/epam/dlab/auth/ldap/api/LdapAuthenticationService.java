package com.epam.dlab.auth.ldap.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.directory.api.ldap.model.cursor.SearchCursor;
import org.apache.directory.api.ldap.model.message.SearchRequest;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapConnectionConfig;
import org.apache.directory.ldap.client.api.LdapConnectionPool;
import org.apache.directory.ldap.client.api.ValidatingPoolableLdapConnectionFactory;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.auth.ldap.SecurityServiceConfiguration;
import com.epam.dlab.auth.ldap.core.Request;
import com.epam.dlab.auth.ldap.core.ReturnableConnection;
import com.epam.dlab.auth.ldap.core.filter.SearchResultProcessor;
import com.epam.dlab.auth.rest.AbstractAuthenticationService;
import com.epam.dlab.auth.rest.AuthorizedUsers;
import com.epam.dlab.auth.rest.ExpirableContainer;
import com.epam.dlab.auth.script.ScriptHolder;
import com.epam.dlab.auth.script.SearchResultToDictionaryMapper;
import com.epam.dlab.dto.UserCredentialDTO;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LdapAuthenticationService extends AbstractAuthenticationService<SecurityServiceConfiguration> {

	private final LdapConnectionConfig connConfig;
	private final List<Request> requests;
	private final String bindTemplate;
	private final LdapConnectionPool usersPool;
	private final LdapConnectionPool searchPool;
	private final ExpirableContainer<Map<String, Object>> filteredDictionaries = new ExpirableContainer<>();
	private final ScriptHolder script = new ScriptHolder();

	public LdapAuthenticationService(SecurityServiceConfiguration config) {
		super(config);
		this.connConfig = config.getLdapConnectionConfig();
		this.requests = config.getLdapSearch();
		this.bindTemplate = config.getLdapBindTemplate();
		PoolableObjectFactory<LdapConnection> userPoolFactory = new ValidatingPoolableLdapConnectionFactory(connConfig);
		this.usersPool = new LdapConnectionPool(userPoolFactory);
		PoolableObjectFactory<LdapConnection> searchPoolFactory = new ValidatingPoolableLdapConnectionFactory(
				connConfig);
		this.searchPool = new LdapConnectionPool(searchPoolFactory);
	}

	@Override
	@POST
	@Path("/login")
	public String login(UserCredentialDTO credential) {
		String username = credential.getUsername();
		String password = credential.getPassword();
		String accessToken = credential.getAccessToken();
		log.debug("validating username:{} password:****** token:{}", username, accessToken);
		UserInfo ui;

		if (this.isAccessTokenAvailable(accessToken)) {
			return accessToken;
		} else {
			try (ReturnableConnection userRCon = new ReturnableConnection(usersPool)) {
				LdapConnection userCon = userRCon.getConnection();
				// just confirm user exists
				String bind = String.format(bindTemplate, username);
				userCon.bind(bind, password);
				userCon.unBind();
				ui = new UserInfo(username, "******");
				log.debug("user '{}' identified. fetching data...", username);
				try (ReturnableConnection searchRCon = new ReturnableConnection(searchPool)) {
					LdapConnection searchCon = searchRCon.getConnection();
					Map<String, Object> conextTree = new HashMap<>();
					for (Request req : requests) {
						if (req == null) {
							continue;
						}
						SearchResultProcessor proc = req.getSearchResultProcessor();
						SearchRequest sr = req.buildSearchRequest(new HashMap<String, Object>() {
							private static final long serialVersionUID = 1L;
							{
								put(Pattern.quote("${username}"), username);
							}
						});
						String filter = sr.getFilter().toString();
						Map<String, Object> contextMap = filteredDictionaries.get(filter);
						SearchResultToDictionaryMapper mapper = new SearchResultToDictionaryMapper(req.getName(),
								conextTree);
						if (contextMap == null) {
							log.debug("Retrieving new branch {} for {}", req.getName(), filter);
							try (SearchCursor cursor = searchCon.search(sr)) {
								contextMap = mapper.transformSearchResult(cursor);
							}
							if (req.isCache()) {
								filteredDictionaries.put(filter, contextMap, req.getExpirationTimeMsec());
							}
						} else {
							log.debug("Restoring old branch {} for {}: {}", req.getName(), filter, contextMap);
							mapper.getBranch().putAll(contextMap);
						}
						if (proc != null) {
							log.debug("Executing: {}", proc.getLanguage());
							ui = script.evalOnce(req.getName(), proc.getLanguage(), proc.getCode()).apply(ui,
									conextTree);
						}

					}
				}

			} catch (Exception e) {
				log.error("LDAP error", e);
				throw new WebApplicationException(e);
			}
			String token = getRandomToken();
			rememberUserInfo(token, ui);
			return token;
		}
	}

	@Override
	@POST
	@Path("/getuserinfo")
	public UserInfo getUserInfo(String access_token) {
		UserInfo ui = AuthorizedUsers.getInstance().getUserInfo(access_token);
		log.debug("Authorized {} {}", access_token, ui);
		return ui;
	}

	@Override
	@POST
	@Path("/logout")
	public Response logout(String access_token) {
		UserInfo ui = this.forgetAccessToken(access_token);
		log.debug("Logged out {} {}", access_token, ui);
		return Response.ok().build();
	}
}