# FXcade: Game Manager for Blackjack and Snake

- **Course:** CS 151 (Object-Oriented Design), section 04
- **Instructor:** Telvin Zhong
- **Semester:** Fall 2025
- **Team members:** Marl Jonson, Sajid Kaiser, Toey Lui, Bush Nguyen
- **Video presentation demo:** https://drive.google.com/file/d/14qtYK6k2X9I7o4A-IBEsIcQ_bLMQGDHE/view

## Overview
**FXcade** is a game manager application featuring the popular games blackjack and snake. It is an offline console application built on a JavaFX frontend and a Java backend using Maven. Our game manager uses a Caesar shift for log-in password encryption.

## Design
Our code is largely split into 3 categories: (1) manager, (2) blackjack, and (3) snake. The two games are organized in a model–view–controller fashion such that the logic and UI are separated for each game; this enabled us to work on such components separately. The main menu UI incorporates a user system and account manager in the `manager` folder; the blackjack UI incorporates a controller and game logic backend; the snake UI incorporates another controller and game logic backend. We incorporated several JUnit tests in the `test` folder.

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
