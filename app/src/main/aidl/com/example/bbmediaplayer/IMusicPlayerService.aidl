// IMusicPlayerService.aidl
package com.example.bbmediaplayer;

// Declare any non-default types here with import statements

interface IMusicPlayerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
            


     void openAudio(int position);

     void start();


     void pause();

     void stop();


     int getCurrentPosition();


     int getCurrentDuration();


     String getArtist();


     String getName();


     String getAudioPath();


     void pre();


     void next();


     void setPlayMode(int playmode);


     int getPlayMode();

     boolean isPlaying();

     void seekTo(int position);

     int getAudioSessionId();
}
