package com.example.mushroomhunter;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

/**
 *  This is the sound player class that is used to add
 *  sound to the game. There are three different sounds
 *  in the game to represent if the character has collected
 *  a mushroom, potion or has hit a bomb.
 */
public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 3;
    // Defines names for each of the sounds in the game
    private static SoundPool soundPool;
    private static int hitShroomSound;
    private static int hitPotionSound;
    private static int hitBombSound;

    public SoundPlayer(Context context) {

        // SoundPool is deprecated in API level 21. (Lollipop)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();

        } else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }
        // Links the audio file to an object in the game
        hitShroomSound = soundPool.load(context, R.raw.shroom, 1);
        hitPotionSound = soundPool.load(context, R.raw.potion, 1);
        hitBombSound = soundPool.load(context, R.raw.bomb, 1);
    }

    /**
     * The three different sounds in the game have to be set with a volume so they are not too loud
     * and that the game knows when to play the correct sound.
     */
    public void playHitShroomSound() {
        soundPool.play(hitShroomSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitPotionSound() {
        soundPool.play(hitPotionSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void playHitBombSound() {
        soundPool.play(hitBombSound, 1.0f, 1.0f, 1, 0, 1.0f);
    }
}