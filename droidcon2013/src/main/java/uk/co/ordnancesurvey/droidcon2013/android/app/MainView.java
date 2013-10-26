package uk.co.ordnancesurvey.droidcon2013.android.app;

import android.location.Location;

public interface MainView {

    public void displayMap();
    public void displayLocation(Location location);
    public void displayLocationDisabled();
    public void displaySearchBoard();
    public void displayTweetBoard();
    public void displayNavigationBoard();
    public void displayInformationBoard();
    public void displayAboutBoard();
    public void displayEulaBoard();

    public void closeView();
}
