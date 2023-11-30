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
    private static final int DELAY_MILLIS = 50; // Delay between movements in milliseconds


    private Paint paint;
    private int[] blockPositions;
    private int[] blockDirections = {1, 1, 1}; // Initial directions for each block (1 for right, -1 for left)
    private Handler handler;
    private boolean blocksMoving;


    public BlockView(Context context) {
        super(context);
        init();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        stopBlocks();
        return super.onTouchEvent(event);
    }


    public void stopBlocks() {
        blocksMoving = false;
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

