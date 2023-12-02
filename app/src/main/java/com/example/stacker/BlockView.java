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
import java.util.List;



public class BlockView extends View {
    private static final int BLOCK_SIZE = 100;
    private static final int NUM_BLOCKS = 3;
    private static final int BLOCK_GAP = 10;
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


                    }
                }

                invalidate();
                moveBlocks();
            }
        }, DELAY_MILLIS);
    }
}