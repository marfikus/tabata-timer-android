package com.github.marfikus.tabatatimer;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;


public class SoundPlayer {
    private final SoundPool soundPool;
    private final AssetManager assetManager;
    private final int soundDing, soundTada;
    public final String SOUND_DING_FILENAME = "ding.mp3";
    public final String SOUND_TADA_FILENAME = "tada.mp3";

    public SoundPlayer(AssetManager _assetManager) {
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        assetManager = _assetManager;
        soundDing = loadSound(SOUND_DING_FILENAME);
        soundTada = loadSound(SOUND_TADA_FILENAME);
    }

    public void playDing() {
        playSound(soundDing);
    }

    public void playTada() {
        playSound(soundTada);
    }

    private int loadSound(String fileName) {
        AssetFileDescriptor afd;
        try {
            afd = assetManager.openFd(fileName);
        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(this, getString(R.string.open_file_error) + fileName + "'", Toast.LENGTH_SHORT).show();
            return -1;
        }
        return soundPool.load(afd, 1);
    }

    private void playSound(int sound) {
        if (sound > 0)
            soundPool.play(sound, 1, 1, 1, 0, 1);
    }
}
