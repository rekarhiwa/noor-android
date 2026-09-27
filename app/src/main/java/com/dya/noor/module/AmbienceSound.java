package com.dya.noor.module;

public class AmbienceSound {
    private final String name;
    private final String displayName;
    private final String videoAsset;
    private final String audioAsset;

    public AmbienceSound(String name, String displayName, String videoAsset, String audioAsset) {
        this.name = name;
        this.displayName = displayName;
        this.videoAsset = videoAsset;
        this.audioAsset = audioAsset;
    }

    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public String getVideoAsset() { return videoAsset; }
    public String getAudioAsset() { return audioAsset; }
}