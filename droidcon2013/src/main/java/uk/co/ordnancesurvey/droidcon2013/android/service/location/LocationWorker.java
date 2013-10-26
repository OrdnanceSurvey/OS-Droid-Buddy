package uk.co.ordnancesurvey.droidcon2013.android.service.location;

public interface LocationWorker {
    void findLocation();
    boolean isGpsAvailable();
    boolean isNetworkAvailable();
    void setPresenter(LocationPresenter presenter);
    void terminate();
}
