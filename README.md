# FXcade: Game Manager for Blackjack and Snake

- **Course:** CS 151 (Object-Oriented Design), section 04
- **Instructor:** Telvin Zhong
- **Semester:** Fall 2025
- **Team members:** Marl Jonson, Sajid Kaiser, Toey Lui, Bush Nguyen
- **Video presentation demo:** https://drive.google.com/file/d/14qtYK6k2X9I7o4A-IBEsIcQ_bLMQGDHE/view

## Overview
FXcade is a game manager application built for CS 151 that integrates two classic games (blackjack and snake) into a unified JavaFX desktop interface. FXcade combines a JavaFX frontend, a Java backend, and Maven for dependency management and project organization. Users can create personal accounts, log in securely through a Caesar cipher–based password encryption system, and access a responsive main menu serving as the hub for game selection.

Blackjack and snake offer their own experiences. Blackjack includes persistent game-state saving so players can resume their session later. Both blackjack and snake maintain high score–tracking tied to user accounts. To ensure stable performance, FXcade is designed as an offline application.

## Design
The FXcade codebase is organized into three primary modules: manager, blackjack, and snake.

The manager module handles user account creation, login authentication, main menu navigation, and coordination between the other components. Passwords are encrypted using a Caesar shift before storage. The manager connects the UI elements of the main menu with the blackjack and snake game modules.

Both blackjack and snake follow a model–view–controller design pattern. This separation allowed us to work independently on UI and game logic without creating significant merge conflicts. Blackjack includes UI screens, player/dealer logic, card management, gameplay rules, a state-saving system for not-yet-finished games. Snake contains grid logic, movement and collision detection, and scoring. Both blackjack and snake maintain persistent high scores.

We incorporated JUnit tests in the test folder to verify aspects of the login, blackjack game, and snake game logic.

## Installation instructions
Prerequisites:
- Java 21 or higher
  - Our `pom.xml` sets `maven.compiler.release` to 21, so you must have JDK 21+ installed.
- Maven
  - Maven will automatically download JavaFX dependencies specified in ``pom.xml``.
- Git or GitHub Desktop

## Usage
1. Clone project from GitHub
2. Change directory to project folder (`FXcade-CS151-Fall-2025`)
3. Run `mvn clean javafx:run` in the terminal

## Contributions
**Marl Jonson (`marljonson`):**
- Initialized GitHub repository and structure
- Wrote frontend of blackjack game
- Connected blackjack game to main menu
- Managed music and sound effects

**Sajid Kaiser (`Hazelette`):**
- Wrote backend of blackjack game
- Debugged game logic for blackjack
- Wrote JUnit tests for snake game
- Debugged "freezing" issues on blackjack frontend

**Toey Lui (`toeyldev`):**
- Wrote `Main.java` and password encryption
- Designed UI/UX for sign-in and main menu
- Connected snake game to main menu
- Completed top bar and snake high score

**Bush Nguyen (`bush-nguyen`):**
- Wrote backend and frontend of snake game
