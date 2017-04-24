package peterjasko.places;

import android.content.Intent;
import android.net.Uri;


class SharePlaceResult {
    private Intent sendIntent;

    public SharePlaceResult(Intent SendIntet) {
        this.sendIntent = SendIntet;
    }

    public Intent getShareIntent() {
        return sendIntent;
    }
}
