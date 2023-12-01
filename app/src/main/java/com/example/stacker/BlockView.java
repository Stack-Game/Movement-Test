package com.example.stacker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;


public class BlockView extends View {
    private static final int BLOCK_SIZE = 100;


    private static final int NUM_BLOCKS = 3;
    private static final int BLOCK_GAP = 10; // Gap between blocks
    private static final int DELAY_MILLIS = 100; // Delay between movements in milliseconds

    private static final int DELAY_BLOCK_MILLIS = 3000;


    private Paint paint;

    private int score = 0;
    private int[] blockPositions;
    private int stackUnit = 1;
    private int[] blockDirections = {1, 1, 1}; // Initial directions for each block (1 for right, -1 for left)
    private Handler handler;
    private boolean blocksMoving;

    private boolean gameOver;

    private boolean top;
    private boolean missedBlock;


    public BlockView(Context context) {
        super(context);
        init();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pause();
        return super.onTouchEvent(event);
    }

    // creates a new block
    public void newBlocks(Canvas canvas){
        // every 3 seconds, new blocks
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (int i = 0; i < NUM_BLOCKS; i++) {
                    canvas.drawRect(
                            blockPositions[i],
                            getHeight() - BLOCK_SIZE * stackUnit,
                            blockPositions[i] + BLOCK_SIZE,
                            getHeight(),
                            paint
                    );
                }

                // Trigger a redraw after each movement
                invalidate();


                // Schedule the next movement
                stackUnit++;
                newBlocks(canvas);
            }
            }, DELAY_BLOCK_MILLIS);
        }


    // keeps track of whether the game is over
    public void gameStatus(){

    }

    // keeps track of the game score
    public void keepScore(){

    }


    public void pause() {
        if(blocksMoving){
            blocksMoving = false;
        } else {
            blocksMoving = true;
        }
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        blockPositions = new int[NUM_BLOCKS];
        blocksMoving = true;
        handler = new Handler(Looper.getMainLooper());


        for (int i = 0; i < NUM_BLOCKS; i++) {
            blockPositions[i] = i * (BLOCK_SIZE + BLOCK_GAP);
        }


        // Start the movement when the view is initialized
        moveBlocks();
        newBlocks(new Canvas());
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (int i = 0; i < NUM_BLOCKS; i++) {
            canvas.drawRect(
                    blockPositions[i],
                    getHeight() - BLOCK_SIZE,
                    blockPositions[i] + BLOCK_SIZE,
                    getHeight(),
                    paint
            );
        }


        invalidate(); // Redraw the view
    }


    private void moveBlocks() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Update block positions based on their individual directions
                if (blocksMoving) {
                    for (int i = 0; i < NUM_BLOCKS; i++) {
                        blockPositions[i] += blockDirections[i] * (BLOCK_SIZE + BLOCK_GAP);


                        // Check if the block reaches the screen edges and update direction individually
                        if (blockPositions[i] >= getWidth() - BLOCK_SIZE || blockPositions[i] <= 0) {
                            blockDirections[i] = -blockDirections[i];
                        }
                    }
                }
                // Trigger a redraw after each movement
                invalidate();


                // Schedule the next movement
                moveBlocks();
            }
        }, DELAY_MILLIS);
    }
}

