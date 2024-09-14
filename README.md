# Gopher Hunting

## Overview

Chicago Gopher Hunt is an interactive Android game where two players compete to locate a hidden gopher on a 10x10 grid. Players take turns making guesses, and the game provides real-time feedback on their proximity to the gopher. The game is designed with multithreading to ensure smooth and concurrent gameplay, enhancing the user experience.

## Features

- **Two-Player Mode**: Players compete to find the hidden gopher first.
- **Random Gopher Location**: Ensures a unique game each time.
- **Move Feedback**: Provides hints based on guess proximity (Success, Near Miss, Close Guess, Complete Miss).
- **Permission Handling**: Requests necessary permissions for gameplay.
- **Thread Management**: Utilizes separate threads for player actions.

## Unique Aspects

### Multithreading

The game's multithreading architecture is a standout feature that ensures seamless and efficient gameplay. Here's a detailed look at how multithreading is implemented and its significance:

#### Player Threads

Each player operates on a separate thread, enabling concurrent actions and smooth updates without blocking the main UI thread. This approach leverages Android's `Handler` and `Thread` classes to manage inter-thread communication and ensure responsive gameplay.

1. **Thread Initialization**:
   Each player is assigned a worker thread that runs concurrently with the main thread. This setup allows the game to handle player actions in parallel, enhancing performance and responsiveness.

2. **Message Handling**:
   The worker threads generate random guesses and communicate with the main thread using `Handler` messages. This mechanism ensures that the UI updates are performed on the main thread, adhering to Android's threading model.

#### UI Thread Interaction

The main thread, which handles the UI, receives messages from the player threads and updates the game state accordingly. This design ensures that the UI remains responsive while the game logic is processed in the background.

- **UI Updates**:
  The main thread processes messages from the player threads to update the game grid and provide feedback on player moves. This separation of concerns allows for a fluid user experience without UI lag.

### Feedback Mechanism

The game provides feedback based on the proximity of player guesses to the gopher's location. This feature helps players refine their guesses and adds a strategic element to the game.

- **Move Evaluation**:
  The feedback is categorized into four levels: Success, Near Miss, Close Guess, and Complete Miss. This categorization aids players in making more informed guesses.

