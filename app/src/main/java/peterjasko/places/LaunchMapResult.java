package peterjasko.places;

import android.net.Uri;

import java.util.List;


class LaunchMapResult {
    private Uri locationURI;

    public LaunchMapResult(Uri locationURI) {
        this.locationURI = locationURI;
    }

    public Uri getLocationUri() {
        return locationURI;
    }
}
