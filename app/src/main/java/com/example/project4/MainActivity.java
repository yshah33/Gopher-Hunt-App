package com.example.project4;

import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// Main activity class
public class MainActivity extends AppCompatActivity {

    // Handler for UI updates
    private Handler uiHandler;

    // Constants for identifying players and permissions
    private final int player1 = 1, player2 = 2;
    private final int PERMISSIONS_REQUEST = 3;

    Button start, stop1, stop2;            // start and stop buttons

    // Variables to store gopher location and player moves
    int gopherX, gopherY;
    private List<int[]> playerMoves1 = new ArrayList<>();
    private List<int[]> playerMoves2 = new ArrayList<>();

    int moveCount1 = 0, moveCount2 = 0;                 // count the guesses for player

    // TextViews for displaying game results and feedback  for moves
    TextView result, result1, result2;

    // GridViews for displaying player grids
    GridView playerGrid1, playerGrid2;

    // Worker threads for player actions
    Thread workerThread1, workerThread2;

    // onCreate method called when activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomePage();          // call welcomePage method
    }

    // Method to initialize welcome page UI
    public void welcomePage () {
        // Initialize buttons
        start = findViewById(R.id.start_button);
        stop1 = findViewById(R.id.stop_button);

        // Set click listeners for start buttons
        start.setOnClickListener(v -> {
            // Check if permission is granted
            int permissionCheck = ContextCompat.checkSelfPermission(this, "com.example.project4.permission.GAME_PLAYER");
            if (PackageManager.PERMISSION_GRANTED == permissionCheck) {
                setContentView(R.layout.game_layout);
                // Start the game
                gameStart();
            }
            // If permission denied, request permission
            else if (PackageManager.PERMISSION_DENIED == permissionCheck) {
                ActivityCompat.requestPermissions( this,
                        new String[]{"com.example.project4.permission.GAME_PLAYER"}, PERMISSIONS_REQUEST);
            }
        });

        // Set click listeners for stop1 buttons
        stop1.setOnClickListener(v -> {
            // Check if the game has started before stopping
            // Display the toast message accordingly
            if (workerThread1 == null && workerThread2 == null) {
                Toast.makeText(this, "Game not Started!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Game Stopped!", Toast.LENGTH_SHORT).show();
            }
            gameStop();
        });
    }

    // Method to handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                // permission was granted, start the game
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    gameStart();
                }
            }
        }
    }

    // Method to reset game state
    public void restart () {
        moveCount1 = 0;
        moveCount2 = 0;

        playerMoves1.clear();
        playerMoves2.clear();
    }

    // Method to randomly generate gopher location
    public void generateGopherLocation () {
        Random random = new Random();
        gopherX = random.nextInt(10);
        gopherY = random.nextInt(10);
    }

    // Method to start the game
    private void gameStart() {
        restart();
        Toast.makeText(this, "Game Started!", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.game_layout);
        generateGopherLocation();

        // Initialize player grids and stop2 button
        playerGrid1 = findViewById(R.id.gridView1);
        playerGrid2 = findViewById(R.id.gridView2);
        stop2 = findViewById(R.id.stop);

        // Initialize player results and win result
        result = findViewById(R.id.winner);
        result1 = findViewById(R.id.result1);
        result2 = findViewById(R.id.result2);

        // Initialize player
        playerGrid1.setAdapter(new GridAdapter(this, gopherX, gopherY, playerMoves1));
        playerGrid2.setAdapter(new GridAdapter(this, gopherX, gopherY, playerMoves2));

        // Set click listener for stop2 button
        stop2.setOnClickListener(v -> {
            Toast.makeText(this, "Game Stopped, Go back to play again!", Toast.LENGTH_SHORT).show();
            gameStop();
        });

        uiHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull android.os.Message msg) {
                // Handle messages received by the UI thread here
                switch (msg.what) {
                    // player1 code
                    case player1:
                        moveCount1++;
                        // Add guessed location and move number to the list
                        playerMoves1.add(new int[]{msg.arg1, msg.arg2, moveCount1});
                        // Display the feedback for player 1
                        result1.setText(String.format("Move: %d - %s", moveCount1, elevateResult(msg.arg1, msg.arg2)));
                        // Check if the player 1 won the game, call stop method
                        if (Objects.equals(elevateResult(msg.arg1, msg.arg2), "Success")) {
                            result.setText("Player 1 win the game!!");
                            Toast.makeText(MainActivity.this, "Game Stopped, Go back to play again!", Toast.LENGTH_SHORT).show();
                            gameStop();
                        }
                        playerGrid1.setAdapter(new GridAdapter(MainActivity.this, gopherX, gopherY, playerMoves1));
                        break;
                    // player2 code
                    case player2:
                        moveCount2++;
                        // Add guessed location and move number to the list
                        playerMoves2.add(new int[]{msg.arg1, msg.arg2, moveCount2});
                        // Display the feedback for player 2
                        result2.setText(String.format("Move: %d - %s", moveCount2, elevateResult(msg.arg1, msg.arg2)));
                        // Check if the player 2 won the game, call stop method
                        if (Objects.equals(elevateResult(msg.arg1, msg.arg2), "Success")) {
                            result.setText("Player 2 win the game!!");
                            Toast.makeText(MainActivity.this, "Game Stopped, Go back to play again!", Toast.LENGTH_SHORT).show();
                            gameStop();
                        }
                        playerGrid2.setAdapter(new GridAdapter(MainActivity.this, gopherX, gopherY, playerMoves2));
                        break;
                }
                return true;
            }
        });

        // Initialize and start the worker threads
        workerThread1 = new workerThread(uiHandler, player1);
        workerThread1.start();

        workerThread2 = new workerThread(uiHandler, player2);
        workerThread2.start();
    }

    // Method to evaluate the result of player's move
    public String elevateResult (int x, int y) {
        if (x == gopherX && y == gopherY) {
            return "Success";
        }
        else if (Math.abs(x - gopherX) <= 1 && Math.abs(y - gopherY) <= 1) {
            return "Near Miss";
        }
        else if (Math.abs(x - gopherX) <= 2 && Math.abs(y - gopherY) <= 2) {
            return "Close Guess";
        }
        return "Complete Miss";
    }

    // Method to stop the game and terminate the worker thread
    private void gameStop() {
        // the UI thread should send a message to the worker threads indicating that they
        // should terminate themselves
        if (workerThread1 != null) {
            workerThread1.interrupt();
            ((workerThread) workerThread1).stopThread();
        }
        if (workerThread2 != null) {
            workerThread2.interrupt();
            ((workerThread) workerThread2).stopThread();
        }
    }

    // Method for the back pressed to change the screen to welcome page
    @Override
    public void onBackPressed() {
        if (playerGrid1.getVisibility() == View.VISIBLE && playerGrid2.getVisibility() == View.VISIBLE) {
            setContentView(R.layout.activity_main);
            gameStop();
            welcomePage();
        } else {
            super.onBackPressed();
        }
    }

}