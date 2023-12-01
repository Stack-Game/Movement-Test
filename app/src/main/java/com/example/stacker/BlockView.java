package com.example.stacker;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;


public class BlockView extends View {
    private static final int BLOCK_SIZE = 100;
    private static final int NUM_BLOCKS = 3;
    private static final int BLOCK_GAP = 10;
    private static final int DELAY_MILLIS = 50;


    private Paint paint;
    private List<int[]> rows;
    private int[] blockDirections = {1, 1, 1}; // Initial directions for each block (1 for right, -1 for left)
    private Handler handler;
    private boolean blocksMoving;


    public BlockView(Context context) {
        super(context);
        init();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            stopBlocks();
            addNewRow();
        }
        return super.onTouchEvent(event);
    }


    private void stopBlocks() {
        blocksMoving = false;
        handler.removeCallbacksAndMessages(null); // Remove any pending movements
    }


    private void addNewRow() {
        blocksMoving = true;


        // Freeze the current row
        int[] currentRow = rows.get(rows.size() - 1);
        rows.add(currentRow.clone()); // Add a new row with the same positions


        // Trigger a redraw
        invalidate();


        // Schedule the next movement
        moveBlocks();
    }


    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        rows = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());


        // Start with the initial row
        int[] initialRow = new int[NUM_BLOCKS];
        for (int i = 0; i < NUM_BLOCKS; i++) {
            initialRow[i] = i * (BLOCK_SIZE + BLOCK_GAP);
        }
        rows.add(initialRow);


        // Set initial direction for the blocks to move right
        for (int i = 0; i < NUM_BLOCKS; i++) {
            blockDirections[i] = 1;
        }


        moveBlocks();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (int[] row : rows) {
            for (int i = 0; i < NUM_BLOCKS; i++) {
                canvas.drawRect(
                        row[i],
                        getHeight() - BLOCK_SIZE - (BLOCK_SIZE + BLOCK_GAP) * (rows.indexOf(row)),
                        row[i] + BLOCK_SIZE,
                        getHeight() - (BLOCK_SIZE + BLOCK_GAP) * (rows.indexOf(row)),
                        paint
                );
            }
        }
    }


    private void moveBlocks() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int[] currentRow = rows.get(rows.size() - 1);

                for (int i = 0; i < NUM_BLOCKS; i++) {
                    currentRow[i] += blockDirections[i] * (BLOCK_SIZE + BLOCK_GAP);


                    if (currentRow[i] >= getWidth() - BLOCK_SIZE || currentRow[i] <= 0) {
                        blockDirections[i] = -blockDirections[i];


                    }
                }

                invalidate();
                moveBlocks();
            }
        }, DELAY_MILLIS);
    }
}
