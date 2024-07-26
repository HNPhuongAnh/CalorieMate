package com.testing;
public class DietMethod {
    private String name;
    private String description;
    private String imageUrl;
    private String details;

    public DietMethod(String name, String description, String imageUrl, String details) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.details = details;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDetails() {
        return details;
    }
}
