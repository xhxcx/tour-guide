package tourGuide.model;

import java.util.UUID;

public class ProviderTourGuide {
    public final String name;
    public final double price;
    public final UUID tripId;

    public ProviderTourGuide(UUID tripId, String name, double price) {
        this.name = name;
        this.tripId = tripId;
        this.price = price;
    }
}
