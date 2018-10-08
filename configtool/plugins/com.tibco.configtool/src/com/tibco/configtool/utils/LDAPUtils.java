package com.tibco.configtool.utils;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import com.tibco.customwizard.action.ActionException;
import com.tibco.trinity.runtime.core.provider.identity.ssl.MakeSocketFactory;

public class LDAPUtils {
	public static void packBaseProperties(Properties props, String factory, String url, String username, String password, String authenticationtype,
			String timeout) {
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY, factory);
		props.setProperty(Context.PROVIDER_URL, url);
		props.setProperty(Context.SECURITY_AUTHENTICATION, authenticationtype);
		props.setProperty(Context.SECURITY_PRINCIPAL, username);
		props.setProperty(Context.SECURITY_CREDENTIALS, password);
		if (timeout != null) {
			props.setProperty("com.sun.jndi.ldap.connect.timeout", timeout);
		}
	}

	public static void packSSLProperties(Properties props, String socketFactoryClassName) {
		props.setProperty(Context.SECURITY_PROTOCOL, "ssl");
		props.setProperty("java.naming.ldap.factory.socket", socketFactoryClassName);
	}
	
	public static InitialLdapContext getInitialLdapContext(Properties props) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(MakeSocketFactory.getCombinedLoader(LDAPUtils.class.getClassLoader()));
			InitialLdapContext initialLdapContext = new InitialLdapContext(props, null);

			return initialLdapContext;
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}
	}
	
	public static void reconnect(InitialLdapContext ctx) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(MakeSocketFactory.getCombinedLoader(LDAPUtils.class.getClassLoader()));
			ctx.reconnect(null);
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}
	}
	
	public static void checkUser(InitialLdapContext ctx, String username, String password, String baseDN, String filter, boolean subtree) throws Exception {
		String userDN = getUserDN(ctx, username, baseDN, filter, subtree);

		try {
			ctx.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
			ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
			reconnect(ctx);
		} catch (Exception e) {
			throw new ActionException("The password [" + password + "] that you entered for the user [" + username
					+ "] is incorrect.");
		}
	}

	@SuppressWarnings("unchecked")
	private static String getUserDN(InitialLdapContext ctx, String username, String baseDN, String filter, boolean subtree) throws Exception {
		filter = filter.replace("{0}", username);

		SearchControls constraints = new SearchControls();
		if (subtree) {
			constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
		} else {
			constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		}

		NamingEnumeration en = ctx.search(baseDN, filter, constraints);
		if (en == null || !en.hasMoreElements()) {
			throw new ActionException("There is no user '" + username + "' entry obtained from an LDAP search for '(" + filter
					+ ")' under the user base DN of '" + baseDN + "'");
		}

		SearchResult searchResult = (SearchResult) en.nextElement();
		return searchResult.getName() + "," + baseDN;
	}

}
