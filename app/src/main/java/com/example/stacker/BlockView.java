package com.example.stacker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.content.Intent;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockView extends View {
    private static final int BLOCK_SIZE = 170;
    private static final int NUM_BLOCKS = 3;
    private static final int BLOCK_GAP = 10;
    private static final double INITIAL_DELAY_MILLIS = 500.0; // Initial delay
    private double currentDelayMillis = INITIAL_DELAY_MILLIS; // Current delay
    private double accelerationFactor = 1; // Factor to increase speed
    private Paint paint;
    private List<int[]> rows;
    private int[] blockDirections = {1, 1, 1}; // Initial directions for each block (1 for right, -1 for left)
    private Handler handler;
    private String[] blockColors = {"#563635", "#78C091", "#81F0E5", "#5B6057", "#6E9075"};
    private int randColor = new Random().nextInt(blockColors.length);
    private Paint textPaint;
    private Paint backgroundPaint;
    public void setDifficulty(String difficulty) {
        switch (difficulty) {
            case "Easy":
                this.accelerationFactor = 1.1; // Easy difficulty
                break;
            case "Medium":
                this.accelerationFactor = 1.2; // Medium difficulty
                break;
            case "Hard":
                this.accelerationFactor = 1.3; // Hard difficulty
                break;
            default:
                this.accelerationFactor = 1.0; // Default to Easy if there's an error
                break;
        }
    }

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
        }
        return super.onTouchEvent(event);
    }

    private void stopBlocks() {
        handler.removeCallbacksAndMessages(null); // Remove any pending movements
    }

    private void addNewRow() {

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

            //Condition Check: If no more blocks exist, call lost game method
            if (newRowList.size() == 0) {
                showGameOverLosePopup();
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

        textPaint = new Paint();
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        for (int[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                int right = Math.min(row[i] + BLOCK_SIZE, getWidth());
                canvas.drawRect(
                        row[i],
                        getHeight() - BLOCK_SIZE - (BLOCK_SIZE + BLOCK_GAP) * (rows.indexOf(row)),
                        right,
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

                boolean changeDirection = false;
                boolean reachedTop = false;

                for (int i = 0; i < currentRow.length; i++) {
                    // Check if any block is about to go off the screen on the right
                    if (currentRow[i] + blockDirections[i] * (BLOCK_SIZE + BLOCK_GAP) >= getWidth() - BLOCK_SIZE) {
                        changeDirection = true;
                        break;
                    } else if (currentRow[i] + blockDirections[i] * (BLOCK_SIZE + BLOCK_GAP) < 0) {
                        // Check if any block is about to go off the screen on the left
                        changeDirection = true;
                        break;
                    }

                    // Check if any block has reached or passed the top of the screen
                    if (getHeight() - (BLOCK_SIZE + BLOCK_GAP) * (rows.indexOf(currentRow)) <= 0) {
                        reachedTop = true;
                        break;
                    }
                }

                //Condition check, if the blocks reach top of display, show game win popup
                if (reachedTop) {
                    showGameOverWinPopup();
                    return; // Exit the method to prevent further execution
                }

                if (changeDirection) {
                    for (int i = 0; i < blockDirections.length; i++) {
                        // Change direction for all blocks
                        blockDirections[i] = -blockDirections[i];
                    }
                }

                for (int i = 0; i < currentRow.length; i++) {
                    currentRow[i] += blockDirections[i] * (BLOCK_SIZE + BLOCK_GAP);
                }

                invalidate();
                moveBlocks();
            }
        }, (long) currentDelayMillis);
    }

    private void showGameOverLosePopup() {
        // Intent to navigate to the Game Over screen
        Intent intent = new Intent(getContext(), GameOverActivity.class);
        getContext().startActivity(intent);
    }
    private void showGameOverWinPopup() {
        // Intent to navigate to the You Win screen
        Intent intent = new Intent(getContext(), YouWin.class);
        getContext().startActivity(intent);
    }
    private void resetGame() {
        // Reset game state, clear rows, reset speed, etc.
        rows.clear();
        currentDelayMillis = INITIAL_DELAY_MILLIS;
        init();
        invalidate();
    }
}