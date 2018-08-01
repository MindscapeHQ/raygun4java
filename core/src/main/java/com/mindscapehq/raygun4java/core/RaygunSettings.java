package com.mindscapehq.raygun4java.core;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class RaygunSettings {
    private static RaygunSettings raygunSettings;
    private static final String defaultApiEndPoint = "https://api.raygun.io/entries";
    private Proxy proxy;
    private Integer connectTimeout;

    private RaygunSettings() {
    }

    public static RaygunSettings getSettings() {
        if (RaygunSettings.raygunSettings == null) {
            RaygunSettings.raygunSettings = new RaygunSettings();
        }

        return RaygunSettings.raygunSettings;
    }

    public String getApiEndPoint() {
        return this.defaultApiEndPoint;
    }

    /**
     * The proxy class used when communicating with the Raygun server, this is instantiated with setHttpProxy
     *
     * @return The Proxy instance held and used to communicate with the Raygun API
     */
    public Proxy getHttpProxy() {
        return this.proxy;
    }

    /**
     * Set your proxy information, if your proxy server requires authentication set a
     * default Authenticator in your code:
     * <p>
     * Authenticator authenticator = new Authenticator() {
     * <p>
     * public PasswordAuthentication getPasswordAuthentication() {
     * return (new PasswordAuthentication("user",
     * "password".toCharArray()));
     * }
     * };
     * Authenticator.setDefault(authenticator);
     * <p>
     * This will allow different proxy authentication credentials to be used for different
     * target urls.
     *
     * @param host The host name
     * @param port The TCP port
     */
    public void setHttpProxy(String host, int port) {
        if (host == null) {
            this.proxy = null;
        } else {
            this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        }
    }

    /*
     * Sets HttpURLConnection.setConnectTimeout()
     * Sets a specified timeout value in milliseconds. A timeout of zero is interpreted as an infinite timeout.
     */
    public void setConnectTimeout(Integer ms) {
        this.connectTimeout = ms;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }
}
