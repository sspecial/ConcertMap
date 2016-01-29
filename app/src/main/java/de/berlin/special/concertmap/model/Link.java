package de.berlin.special.concertmap.model;

/**
 * Created by Saeed on 29-Jan-16.
 */
public class Link {

    private final String provider;
    private final String URL;

    public Link(String provider, String URL) {
        this.provider = provider;
        this.URL = URL;
    }

    public String getProvider() {
        return provider;
    }

    public String getURL() {
        return URL;
    }
}
