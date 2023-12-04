package com.example.stacker;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the difficulty level
        String difficulty = getIntent().getStringExtra("difficulty");
        BlockView blockView = new BlockView(this);

        // Set the difficulty from passed intent
        if (difficulty != null) {
            blockView.setDifficulty(difficulty);
        }

        setContentView(blockView);
    }}
