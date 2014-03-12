package com.mobi.bright.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class DataStorage {
  // ------------------------------ FIELDS ------------------------------
  public static final String GAME_PREFERENCES = "MobiFusionPreferences";
  public static final String SIZE = "SIZE";
  public static final String BRIGHTNESS = "BRIGHTNESS";
  private static final String VIDEO_DURATION = "VIDEO_DURATION";
  private static final String VIDEO_POSITION = "VIDEO_POSITION";
  private static final String AUDIO_POSITION = "AUDIO_POSITION";
  SharedPreferences sharedPreferences;

// --------------------------- CONSTRUCTORS ---------------------------

  public DataStorage(Activity activity) {
    this.sharedPreferences = activity.getSharedPreferences(GAME_PREFERENCES,
                                                           Context.MODE_PRIVATE);
  }

// -------------------------- OTHER METHODS --------------------------

  public int getBrightness() {
    return sharedPreferences.getInt(BRIGHTNESS,
                                    -1);
  }

  public void setBrightness(int brightness) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(BRIGHTNESS,
                  brightness);
    editor.commit();
  }

  public float getSize() {
    return sharedPreferences.getFloat(SIZE,
                                      1.0f);
  }

  public void setSize(float size) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putFloat(SIZE,
                    size);
    editor.commit();
  }

  public int getVideoDuration(String url) {
    return sharedPreferences.getInt(VIDEO_DURATION + url,
                                    -1);
  }

  public int getVideoPosition(String url) {
    return sharedPreferences.getInt(VIDEO_POSITION + url,
                                    0);
  }

  public int getAudioPosition(String url) {
    return sharedPreferences.getInt(AUDIO_POSITION + url,
                                    0);
  }

  public void setVideoDuration(String url,
                               int seconds) {
    SharedPreferences.Editor editor = sharedPreferences.edit();

    if (seconds > 0) {
      seconds--;
    }

    editor.putInt(VIDEO_DURATION + url,
                  seconds);
    editor.commit();
  }

  public void setVideoPosition(String url,
                               int milliSeconds) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(VIDEO_POSITION + url,
                  milliSeconds);

    editor.commit();
  }

  public void setAudioPosition(String url,
                               int milliSeconds) {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(AUDIO_POSITION + url,
                  milliSeconds);
    editor.commit();
  }
}
