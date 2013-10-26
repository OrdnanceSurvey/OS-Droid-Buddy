package uk.co.ordnancesurvey.droidcon2013.android.service.location;

import android.location.Location;

public interface LocationPresenter {
    void receivedLocation(Location location);
}
