package com.example.mushroomhunter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * The main class for the game where values are set and everything is
 * defined so that the game plays corretly
 */
public class MainActivity extends AppCompatActivity {

    // Create frame
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;

    // Label the images
    private ImageView box, bomb, shroom, potion;
    private Drawable imageBoxRight, imageBoxLeft;

    // Define the size of the box
    private int boxSize;

    // Set each images position
    private float boxX, boxY;
    private float bombX, bombY;
    private float shroomX, shroomY;
    private float potionX, potionY;

    // Variable's relating to the score in the game
    private TextView scoreLabel, highScoreLabel;
    private int score, highScore, timeCount;
    private SharedPreferences settings;

    // Class
    private Timer timer;
    private Handler handler = new Handler();
    private SoundPlayer soundPlayer;

    // Status
    private boolean start_flg = false;
    private boolean action_flg = false;
    private boolean potion_flg = false;


    @Override
    /**
     * the onCreate method is used to save the state of the game so the
     * player can return where they left off
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundPlayer = new SoundPlayer(this);

        // Starts the game and begins displaying the objects
        gameFrame = findViewById(R.id.gameFrame);
        startLayout = findViewById(R.id.startLayout);
        box = findViewById(R.id.box);
        bomb = findViewById(R.id.bomb);
        shroom = findViewById(R.id.shroom);
        potion = findViewById(R.id.potion);
        scoreLabel = findViewById(R.id.scoreLabel);
        highScoreLabel = findViewById(R.id.highScoreLabel);

        // So when the character moves from left to right, the sprite also changes
        //direction
        imageBoxLeft = getResources().getDrawable(R.drawable.char_left);
        imageBoxRight = getResources().getDrawable(R.drawable.char_right);

        // High Score
        settings = getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore = settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : " + highScore);
    }

    /**
     * This method is there to get the objects in the game to move
     * by falling from the top of the game
     */
    public void changePos() {

        // Add timeCount
        timeCount += 20;

        // shroom
        shroomY += 12;

        // Sets the hit detector size of the mushroom
        float shroomCenterX = shroomX + shroom.getWidth() / 2;
        float shroomCenterY = shroomY + shroom.getHeight() / 2;

        // Adds ten to the score if a mushroom is touched
        if (hitCheck(shroomCenterX, shroomCenterY)) {
            shroomY = frameHeight + 100;
            score += 10;
            // Triggers the sound that a mushroom was touched
            soundPlayer.playHitShroomSound();
        }

        if (shroomY > frameHeight) {
            shroomY = -100;
            shroomX = (float) Math.floor(Math.random() * (frameWidth - shroom.getWidth()));
        }
        shroom.setX(shroomX);
        shroom.setY(shroomY);

        // potion
        if (!potion_flg && timeCount % 10000 == 0) {
            potion_flg = true;
            potionY = -20;
            potionX = (float) Math.floor(Math.random() * (frameWidth - potion.getWidth()));
        }

        if (potion_flg) {
            potionY += 20;

            // Sets the hit detector size of the mushroom
            float potionCenterX = potionX + potion.getWidth() / 2;
            float potionCenterY = potionY + potion.getWidth() / 2;

            // Checks that the potion was touched so score can be added
            if (hitCheck(potionCenterX, potionCenterY)) {
                potionY = frameHeight + 30;
                // Adds thirty to player score
                score += 30;
                // Enlarges the game framewidth if potion is touched
                if (initialFrameWidth > frameWidth * 110 / 100) {
                    frameWidth = frameWidth * 110 / 100;
                    changeFrameWidth(frameWidth);
                }
                // Triggers the sound that a potion was touched
                soundPlayer.playHitPotionSound();
            }

            if (potionY > frameHeight) potion_flg = false;
            potion.setX(potionX);
            potion.setY(potionY);
        }

        // Bomb
        bombY += 18;
        // Sets the size of the bomb so it can be registered as touched
        float bombCenterX = bombX + bomb.getWidth() / 2;
        float bombCenterY = bombY + bomb.getHeight() / 2;
        // Checks if the bomb was touched
        if (hitCheck(bombCenterX, bombCenterY)) {
            bombY = frameHeight + 100;

            // If the bomb was touched, the game frame closes
            frameWidth = frameWidth * 80 / 100;
            changeFrameWidth(frameWidth);
            // Triggers the bomb sound if hit
            soundPlayer.playHitBombSound();
            // Checks if the game frame has been shrunk enough to
            // end the game as the character cannot move
            if (frameWidth <= boxSize) {
                gameOver();
            }

        }
        // Creates a new size for the frame after bomb has been touched
        if (bombY > frameHeight) {
            bombY = -100;
            bombX = (float) Math.floor(Math.random() * (frameWidth - bomb.getWidth()));
        }

        bomb.setX(bombX);
        bomb.setY(bombY);

        // Move Box
        if (action_flg) {
            // Touching
            boxX += 14;
            box.setImageDrawable(imageBoxRight);
        } else {
            // Releasing
            boxX -= 14;
            box.setImageDrawable(imageBoxLeft);
        }

        // Check box position.
        if (boxX < 0) {
            boxX = 0;
            box.setImageDrawable(imageBoxRight);
        }
        if (frameWidth - boxSize < boxX) {
            boxX = frameWidth - boxSize;
            box.setImageDrawable(imageBoxLeft);
        }

        box.setX(boxX);

        // Adds the score achieved to the player to the screen
        scoreLabel.setText("Score : " + score);

    }

    /**
     * This method is used to check if the players character
     * has collected the mushrooms, potion or is hit by the
     * bomb.
     */
    public boolean hitCheck(float x, float y) {
        if (boxX <= x && x <= boxX + boxSize &&
                boxY <= y && y <= frameHeight) {
            return true;
        }
        return false;
    }

    public void changeFrameWidth(int frameWidth) {

        ViewGroup.LayoutParams params = gameFrame.getLayoutParams();
        params.width = frameWidth;
        gameFrame.setLayoutParams(params);
    }

    public void gameOver() {
        // Stop timer.
        timer.cancel();
        timer = null;
        start_flg = false;

        // Waits two seconds until showing the start menu
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);

        // Hides the falling objects when the game is not  being played
        startLayout.setVisibility(View.VISIBLE);
        box.setVisibility(View.INVISIBLE);
        bomb.setVisibility(View.INVISIBLE);
        shroom.setVisibility(View.INVISIBLE);
        potion.setVisibility(View.INVISIBLE);

        // If a new high score is achieved it will be added to the
        // high score chart
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score : " + highScore);

            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }
    }

    @Override
    /**
     *  To be called when the screen is touched to interact with
     *  the character and make it move.
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                action_flg = true;

            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                action_flg = false;

            }
        }
        return true;
    }

    /*
     * Creates the game so it can be played
     */
    public void startGame(View view) {
        start_flg = true;
        startLayout.setVisibility(View.INVISIBLE);

        if (frameHeight == 0) {
            frameHeight = gameFrame.getHeight();
            frameWidth = gameFrame.getWidth();
            initialFrameWidth = frameWidth;

            boxSize = box.getHeight();
            boxX = box.getX();
            boxY = box.getY();
        }

        frameWidth = initialFrameWidth;

        box.setX(0.0f);
        bomb.setY(3000.0f);
        shroom.setY(3000.0f);
        potion.setY(3000.0f);

        bombY = bomb.getY();
        shroomY = shroom.getY();
        potionY = potion.getY();

        box.setVisibility(View.VISIBLE);
        bomb.setVisibility(View.VISIBLE);
        shroom.setVisibility(View.VISIBLE);
        potion.setVisibility(View.VISIBLE);

        timeCount = 0;
        score = 0;
        scoreLabel.setText("Score : 0");


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }

    /**
     * This method is simply used to close the game
     */
    public void quitGame(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

}
