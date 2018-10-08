/**
 * 
 */
package com.tibco.ert.model.core;

/**
 * @author keqiang
 * 
 */
public class AdminAccessConfig {
	private String protocol = "http";
	
    private String host;

    private int port;

    private String username;

    private String password;

    private String dbPassword;

    public AdminAccessConfig(String host, int port, String username, String password) {
        this(host, port, username, password, "");
    }

    public AdminAccessConfig(String host, int port, String username, String password, String dbPassword) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.dbPassword = dbPassword;
    }

    /**
     * get the host of admin configuration
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * get the password of admin configuration
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * get the port of admin configuration
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * get the username of admin configuration
     * @return username
     */
    public String getUsername() {
        return username;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
     * get the admin url of admin configuration
     * @return admin url
     */
    public String getAdminUrl() {
        return protocol + "://" + host + ":" + port + "/amxadministrator";
    }
}
