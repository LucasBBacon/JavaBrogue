# Brogue: Java Core Refactor

## A strategic refactoring of the open-source Roguelike Brogue from C to modern Java.

### Project Overview

This project is an architectural study and implementation of a Roguelike game engine, aiming to port the logic of the class game *Brogue* (originally written in C) into a clean, testable, and Object-Oriented Java environment.

Unlike a direct line-by-line translation, this project re-imagines the codebase using modern software design patterns (MVC, Dependency Injection principles, and Strong Typing) while strictly adhering to the original game's mathematical behaviours (RNG seeding, color blending, and dungeon generation algorithms).

### Core Architecture

The project moves away from Brogue's original global state and `struct`-based approach towards a component-based architecture:
- **Model:** The `DungeonLevel`, `Grid`, and `Entity` classes hold the state. They are strictly data containers.
- **View:** The `ConsoleRenderer` and `MessageLog` handle visualisation of the state.
- **Controller:** The `GameManager` acts as the central brain, coordinating inputs, game turns, and system updates.
- **Systems:** Logic is decoupled into specific systems:
  - `FOVSystem`: Recursive Shadowcasting for Fog of War.
  - `AISystem`: Greedy pathfinding and state-based behaviour for monsters.
  - `DungeonGenerator`: Procedural generation using the "Room and Corridor" algorithm.

### Features Implemented

#### Combat and Gameplay
- **Turn-based loop:** A robust energy/turn system where enemies only act after the player.
- **Bump combat:** Seamless interaction where moving into an enemy triggers attacks.
- **Hit points and damage:** Functional health tracking and death states.
- **Message log:** A scrolling history of combat events and game notifications.

#### Artificial Intelligence
- **Hunger AI:** Monsters attack the player once visible.
- **State logic:** Enemies switch between Idle, Hunting, and Attacking states based on distance and Line of Sight.

#### World Generation
- **Procedural dungeon generation:** Creates unique layouts with connected rooms and hallways based on a seed.
- **Loot tables:** Automatically populates rooms with Gold and Consumables.
- **Brogue RNG:** A custom port of the original Linear Congruential Generator to ensure authentic probability distributions.

#### Field of View (FOV)
- **Recursive Shadowcasting:** Efficient algorithms to calculate visibility.
- **Fog of War:** Distinguishes between currently visible tiles, explored (memory) tiles, and the unknown.

#### Inventory System
- **Items:** Abstract hierarchy for items.
- **Inventory Management:** Fixed-capacity pack (like the original).
- **Interaction:** Consumable items that affect player stats.

#### Lighting and Color
- **BrogueColor:** A floating-point color system supporting HDR (High Dynamic Range) calculations, linear interpolation, and light blending.

### Getting Started

This project is built using **Gradle**.

#### Prerequisites
- Java 17 or higher
- Gradle (or use the included wrapper)

#### Running the Engine

Currently, the engine runs primarily through **Unit Tests** and **Console Simulations**, as the graphical frontend is decoupled.

To run the full suite of tests and see the console simulations:
```
./gradlew test
```

To see the visual output of the Dungeon Generator and FOV, check the output of `ConsoleRendererTest`.

### Directory Structure
```
src/main/java/lucas/games/brogue/backend/
├── entities/           # Actors (Player, Monster) and Items
├── generators/         # Procedural generation logic
├── systems/            # Logic engines (AI, FOV)
├── views/              # Text-based rendering and logs
├── BrogueColor.java    # Lighting math
├── DungeonLevel.java   # The grid data structure
└── GameManager.java    # The main game controller
```

### Credits
- **Original game:** *Brogue* by Brian Walker.
- **Community edition:** Maintained by tmewett.
- **Refactoring:** Built as an educational project to bridge C and Java game development paradigms.