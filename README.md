# FXcade: Game Manager

- **Course:** CS 151 (Object-Oriented Design)
- **Instructor:** Telvin Zhong
- **Semester:** Fall 2025
- **Team members:** Marl Jonson, Sajid Kaiser, Toey Lui, Bush Nguyen

## Overview
**FXcade** is a game manager application featuring the popular games blackjack and snake. It is an an offline console application built on a JavaFX frontend and a Java backend. Our game manager uses a Caesar shift for encryption

## Design
Our code is largely split into 3 categories: (1) manager, (2) blackjack, and (3) snake.

## Installation instructions
Prerequisites:
- Java
  - Our `pom.xml` file is set to match the user's existing SDK
- Maven
- Git or GitHub Desktop installed

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

**Bush Nguyen (`bush-nguyen`):**
- Wrote backend and frontend of snake game
