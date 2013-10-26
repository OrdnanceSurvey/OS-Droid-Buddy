package uk.co.ordnancesurvey.droidcon2013.android.twitterintegration.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeoTweet {

    public static class Util {

        private static final String OS_HASHTAG = "#OSOpenSpaceSDK";
        private static final String GAP = " ";
        private static final Pattern PATTERN = Pattern.compile("(.*)" + GAP + OS_HASHTAG + GAP
                + "<(\\-?\\d+(\\.\\d+)?),\\s*(\\-?\\d+(\\.\\d+)?)>");

        public static String getHashtag() {
            return OS_HASHTAG;
        }

        public static String getAsString(String tweet, double lat, double lon) {
            return tweet + GAP + OS_HASHTAG + GAP + constructLocation(lat, lon);
        }

        public static GeoTweet getAsGeoTweet(String geotweet) {

            Matcher matcher = PATTERN.matcher(geotweet);
            if (matcher.find()) {
                String tweet = matcher.group(1);
                String latitudeString = matcher.group(2);
                String longitudeString = matcher.group(4);

                double latitude = Double.parseDouble(latitudeString);
                double longitude = Double.parseDouble(longitudeString);

                return new GeoTweet(tweet, latitude, longitude);
            }
            return null;
        }

        private static String constructLocation(double lat, double lon) {
            return "<" + lat + "," + lon + ">";
        }
    }

    final String mTweet;
    final double mLatitude;
    final double mLongitude;

    public GeoTweet(String tweet, double latitude, double longitude) {
        mTweet = tweet;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public String getTweet() {
        return mTweet;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String toString() {
        return Util.getAsString(mTweet, mLatitude, mLongitude);
    }
}
