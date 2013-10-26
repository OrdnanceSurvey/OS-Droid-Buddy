OS Droid Buddy
-------

Be in the know at this year’s Droidcon event with OS Droidbuddy, using Ordnance Survey’s overlay mapping you will be able to tweet your thoughts, views and location all from this fantastic application, built using our OS Android SDK, just an example of some of the fantastic applications you could be building this year with OS.

Key Features:

* An Overview map of the UK, defaulting to the Droidcon 2013 location
* See where you are in comparison to the Droidcon event on the map
* View latest OS / Droidcon related Tweets
* Send an OS Droidcon tweet from App
* View tweets made by other visitor in a visual geographical context

The Android OS OpenSpace SDK will enable you to create free and commercial mobile applications with our data. It provides access to a number of mapping layers and gazetteer lookups and has a similar API to Google Maps, so that moving from Google mapping to Ordnance Survey mapping is simple.

![Splash-ScreenShot](https://github.com/OrdnanceSurvey/OS-Droid-Buddy/raw/master/srcsht-splash.png "Screenshot of splash screen")
![Map-ScreenShot](https://github.com/OrdnanceSurvey/OS-Droid-Buddy/raw/master/srcsht-map.png "Screenshot of map")

### Installation

## Build
This project uses maven.  You must install two maven dependencies manually because they are not available in maven central.

1. [Android SDK and Support library](https://github.com/mosabua/maven-android-sdk-deployer)

2. The OS Open Space SDK, see sdk/README.txt.

## API Keys

Please ensure that you:

1. replace the 'tile_source_key' string resource value with an API key obtained from [Ordnance Survey](https://openspaceregister.ordnancesurvey.co.uk/osmapapi/register.do)

2. replace 'twitter_public_key' and 'twitter_public_secret' with your twitter app credials, see the [twitter dev site](https://dev.twitter.com/) for further details.


### OpenSpace Android SDK

The mapping component in this demonstration is powered by the [OpenSpace Android SDK][1], available subject to the BSD licence terms.

### Credits

This application uses [Twitter4j][3] library released under Apache License 2.0.

### License

This project is available under the [OGL (Open Government Licence)][2].

Graphics, data and other information other than source code is © Crown Copyright and/or database right 2013 Ordnance Survey.

[1]: https://github.com/OrdnanceSurvey/openspace-android-sdk
[2]: http://www.nationalarchives.gov.uk/doc/open-government-licence/version/2/
[3]: http://twitter4j.org/en/
