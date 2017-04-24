package peterjasko.places;

import java.util.List;

public class GetLocationsResultEvent {
    private List<Place> places;

    public GetLocationsResultEvent(List<Place> places) {
        this.places = places;
    }

    public List<Place> getPlaces() {
        return places;
    }
}
