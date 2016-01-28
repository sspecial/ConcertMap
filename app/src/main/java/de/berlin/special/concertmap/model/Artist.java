package de.berlin.special.concertmap.model;

/**
 * Created by Saeed on 28-Jan-16.
 */
public class Artist {

    private final int id;
    private final String name;
    private final String image;

    public Artist(int id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
