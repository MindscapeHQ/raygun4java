package com.mindscapehq.raygun4java.core;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class RaygunSettings {

    private RaygunSettings() {
    }

    private static RaygunSettings raygunSettings;

    public static synchronized RaygunSettings GetSettings() {

        if (RaygunSettings.raygunSettings == null) {
            RaygunSettings.raygunSettings = new RaygunSettings();
        }

        return RaygunSettings.raygunSettings;
    }

    private final String defaultApiEndPoint = "https://api.raygun.io/entries";

    public String getApiEndPoint() {
        return this.defaultApiEndPoint;
    }

    private Proxy proxy;

    /**
     * The proxy class used when communicating with the Raygun server, this is instantiated with setHttpProxy
     *
     * @return The Proxy instance held and used to communicate with the Raygun API
     */
    public Proxy getProxy() {
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
        this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    }

}
