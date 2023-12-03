package com.example.stacker;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.Random;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;

import java.util.List;




public class BlockView extends View {
    private static final int BLOCK_SIZE = 170;
    private static final int NUM_BLOCKS = 3;
    private static final int BLOCK_GAP = 10;

    private static final double INITIAL_DELAY_MILLIS = 500.0; // Initial delay
    private double currentDelayMillis = INITIAL_DELAY_MILLIS; // Current delay
    private double accelerationFactor = 1.2; // Factor to increase speed
    private Paint paint;

    private static final int DELAY_MILLIS = 50;


    private Paint paint;
    private Paint textPaint;

    private Paint backgroundPaint;

    private List<int[]> rows;
    private int[] blockDirections = {1, 1, 1}; // Initial directions for each block (1 for right, -1 for left)
    private Handler handler;

    private String[] blockColors = {"#563635", "#78C091", "#81F0E5", "#5B6057", "#6E9075"};

    private int randColor = new Random().nextInt(blockColors.length);


    private boolean blocksMoving;
    private boolean reachedTop = false;

    private int numRows = 0;
    private int score = 0;

    public BlockView(Context context) {
        super(context);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            stopBlocks();
            addNewRow();
            updateDelay();

        if (event.getAction() == MotionEvent.ACTION_DOWN && !reachedTop) {
            stopBlocks();
            addNewRow();
        }

        if(numRows == 19){
            stopBlocks();


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
        if (!rows.isEmpty()) {
            int[] currentRow = rows.get(rows.size() - 1);
            List<Integer> newRowList = new ArrayList<>();

            // If there's more than one row, check the row beneath
            if (rows.size() > 1) {
                int[] previousRow = rows.get(rows.size() - 2);

                for (int i = 0; i < currentRow.length; i++) {
                    // Check if there's a block directly underneath in the previous row
                    for (int j = 0; j < previousRow.length; j++) {
                        if (currentRow[i] == previousRow[j]) {
                            newRowList.add(currentRow[i]);  // Add the block to the new row
                            break;
                        }
                    }
                }

            } else {
                // If it's the first row, just clone it
                for (int i = 0; i < NUM_BLOCKS; i++) {
                    newRowList.add(currentRow[i]);
                }
            }

            // Convert the list of blocks in the new row to an array
            int[] newRow = new int[newRowList.size()];
            for (int i = 0; i < newRowList.size(); i++) {
                newRow[i] = newRowList.get(i);
            }

            // Replace the current row with the new row
            rows.set(rows.size() - 1, newRow);

            // Add a new row for the next round
            rows.add(Arrays.copyOf(newRow, newRow.length));

            // Trigger a redraw
            invalidate();

            // Schedule the next movement
            moveBlocks();
        }
    }

    private void updateDelay() {
        // Exponentially increase the speed
        currentDelayMillis = currentDelayMillis / accelerationFactor;
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



    private void addNewRow() {
        blocksMoving = true;
        score++;

        // Freeze the current row
        int[] currentRow = rows.get(rows.size() - 1);
        rows.add(currentRow.clone()); // Add a new row with the same positions
        numRows++;
        if(numRows == 19){
            reachedTop = true;
        }



        // Trigger a redraw
        invalidate();


        // Schedule the next movement
        moveBlocks();
    }


    private void init() {
        paint = new Paint();
        paint.setColor(Color.parseColor(blockColors[randColor]));

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
            for (int i = 0; i < row.length; i++) {
                // Ensure that the right side of the collision display is within the display bounds
                int right = Math.min(row[i] + BLOCK_SIZE, getWidth());
                canvas.drawRect(
                        row[i],
                        getHeight() - BLOCK_SIZE - (BLOCK_SIZE + BLOCK_GAP) * (rows.indexOf(row)),
                        right,

        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

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



        textPaint.setTextSize(60);
        textPaint.setColor(Color.WHITE);
        canvas.drawText("Score: " + score, canvas.getWidth()/20, canvas.getHeight()/20, textPaint);

        if(reachedTop){
            canvas.drawRect(0,0, getWidth(), getHeight(), paint);
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(100);
            canvas.drawText("Game Over", canvas.getWidth()/4, canvas.getHeight()/2, textPaint);
            canvas.drawText("Score: " + score, canvas.getWidth()/4, 7 * canvas.getHeight()/12, textPaint);
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


                for (int i = 0; i < currentRow.length; i++) {
                    currentRow[i] += blockDirections[i] * (BLOCK_SIZE + BLOCK_GAP);


                    if (currentRow[i] >= getWidth() - (BLOCK_SIZE + BLOCK_GAP) || currentRow[i] <= 0) {
                        blockDirections[i] = -blockDirections[i];
                    }
                }
                invalidate();
                moveBlocks();
            }
        }, (long) currentDelayMillis);
    }
}
}