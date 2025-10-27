# IsKahoot - Setup and Development Guide

## 📋 Table of Contents
1. [Project Overview](#project-overview)
2. [Prerequisites](#prerequisites)
3. [Project Structure](#project-structure)
4. [Building the Project](#building-the-project)
5. [Running the Application](#running-the-application)
6. [Development Workflow](#development-workflow)
7. [Architecture Overview](#architecture-overview)

---

## 🎯 Project Overview

**IsKahoot** is a distributed, concurrent quiz game similar to Kahoot!, developed as a project for the PCD (Concurrent and Distributed Programming) course.

**Key Features:**
- Client-Server architecture
- Multiple concurrent game rooms
- Teams of 2 players
- Two question types: Individual and Team
- Custom synchronization mechanisms (Semaphore, Barrier, ThreadPool)
- Real-time scoreboard updates

**Programming Language:** Java 17+

---

## 📦 Prerequisites

### Required Software:
1. **Java Development Kit (JDK) 17 or higher**
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify installation: `java -version`

2. **Apache Maven 3.6+**
   - Download: https://maven.apache.org/download.cgi
   - Verify installation: `mvn -version`

3. **IntelliJ IDEA** (Recommended) or any Java IDE
   - Download: https://www.jetbrains.com/idea/download/

### Optional:
- Git for version control
- Terminal/Command Prompt for running commands

---

## 📁 Project Structure

```
PCD/
├── src/
│   ├── main/
│   │   ├── java/com/iskahoot/
│   │   │   ├── server/              # Server-side code
│   │   │   │   ├── Server.java      # Main server class
│   │   │   │   ├── ServerTUI.java   # Text UI for server admin
│   │   │   │   ├── Room.java        # Game room management
│   │   │   │   ├── GameState.java   # Game state per room
│   │   │   │   └── PlayerHandler.java # Client connection handler
│   │   │   │
│   │   │   ├── client/              # Client-side code
│   │   │   │   ├── Client.java      # Main client class
│   │   │   │   └── ClientGUI.java   # GUI (Phase 1)
│   │   │   │
│   │   │   ├── common/              # Shared code
│   │   │   │   ├── models/          # Data models
│   │   │   │   │   ├── Question.java
│   │   │   │   │   ├── Quiz.java
│   │   │   │   │   ├── Player.java
│   │   │   │   │   └── Team.java
│   │   │   │   └── messages/        # Network messages
│   │   │   │       ├── Message.java
│   │   │   │       ├── JoinRequest.java
│   │   │   │       ├── AnswerSubmission.java
│   │   │   │       └── ScoreboardData.java
│   │   │   │
│   │   │   ├── sync/                # Custom synchronization (Phase 6)
│   │   │   │   ├── ModifiedSemaphore.java
│   │   │   │   ├── ModifiedBarrier.java
│   │   │   │   └── CustomThreadPool.java (Optional)
│   │   │   │
│   │   │   └── utils/               # Utilities
│   │   │       └── QuestionLoader.java # JSON parser
│   │   │
│   │   └── resources/
│   │       └── questions.json       # Quiz questions
│   │
│   └── test/java/                   # Unit tests
│
├── pom.xml                          # Maven configuration
├── README.md                        # Project specification
├── SETUP.md                         # This file
└── .gitignore                       # Git ignore rules
```

---

## 🔨 Building the Project

### Using Maven (Command Line):

1. **Navigate to project directory:**
   ```bash
   cd C:/Users/limar/IdeaProjects/PCD
   ```

2. **Clean and compile:**
   ```bash
   mvn clean compile
   ```

3. **Run tests:**
   ```bash
   mvn test
   ```

4. **Package (create JAR files):**
   ```bash
   mvn package
   ```
   This creates:
   - `target/iskahoot-game-1.0-SNAPSHOT-server.jar` (Server)
   - `target/iskahoot-game-1.0-SNAPSHOT-client.jar` (Client)

### Using IntelliJ IDEA:

1. **Open Project:**
   - File → Open → Select `PCD` folder
   - IntelliJ will auto-detect Maven project

2. **Build:**
   - Build → Build Project (Ctrl+F9)

3. **Run:**
   - Right-click on `Server.java` → Run 'Server.main()'
   - Right-click on `Client.java` → Run 'Client.main()'

---

## 🚀 Running the Application

### Step 1: Start the Server

**Option A - Using Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.iskahoot.server.Server"
```

**Option B - Using compiled JAR:**
```bash
java -jar target/iskahoot-game-1.0-SNAPSHOT-server.jar
```

**Option C - Using IntelliJ:**
- Right-click `Server.java` → Run

**Server will start on port 8080 by default.**

### Step 2: Create a Game Room

Once the server is running, use the TUI (Text User Interface):

```
> create 2
```
This creates a room for 2 teams (4 players total).

The server will display a **room code** (e.g., "1234").

### Step 3: Start Clients

You need to start 4 clients (2 teams × 2 players).

**Command format:**
```bash
java -cp target/classes com.iskahoot.client.Client <IP> <PORT> <RoomCode> <TeamCode> <Username>
```

**Example - Team A:**
```bash
# Player 1
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_A Alice

# Player 2
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_A Bob
```

**Example - Team B:**
```bash
# Player 3
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_B Charlie

# Player 4
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_B Diana
```

### Step 4: Game Starts Automatically

Once all 4 players connect, the game starts automatically!

---

## 🔄 Development Workflow

### Phase 1-3 (Due Nov 11-14) - Intermediate Delivery:
1. ✅ **Phase 1:** Implement ClientGUI.java
2. ✅ **Phase 2:** Complete GameState.java structure
3. ✅ **Phase 3:** Test QuestionLoader.java (already working!)

### Phase 4-7 (Due Dec 15):
4. **Phase 4:** Complete server-client connection logic
5. **Phase 5:** Implement message exchange
6. **Phase 6:** Implement custom Semaphore and Barrier
7. **Phase 7:** Complete game cycle and end game logic

### Phase 8 (Optional - Extra Credit):
8. **Phase 8:** Implement CustomThreadPool

---

## 🏗️ Architecture Overview

### How It Works:

```
┌─────────────────────────────────────────────────────────────┐
│                         SERVER                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  ServerTUI (Admin Interface)                         │  │
│  │  - create <teams>                                    │  │
│  │  - list                                              │  │
│  │  - status <code>                                     │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   Room 1234  │  │   Room 5678  │  │   Room 9012  │    │
│  │              │  │              │  │              │    │
│  │  GameState   │  │  GameState   │  │  GameState   │    │
│  │  Teams       │  │  Teams       │  │  Teams       │    │
│  │  Players     │  │  Players     │  │  Players     │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│                                                             │
│  ┌────────────────────────────────────────────────────┐   │
│  │  PlayerHandler (1 thread per connected player)     │   │
│  └────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                            ↕ TCP Sockets
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTS                              │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │ Client 1 │  │ Client 2 │  │ Client 3 │  │ Client 4 │  │
│  │ (Alice)  │  │  (Bob)   │  │(Charlie) │  │ (Diana)  │  │
│  │ TEAM_A   │  │ TEAM_A   │  │ TEAM_B   │  │ TEAM_B   │  │
│  │          │  │          │  │          │  │          │  │
│  │   GUI    │  │   GUI    │  │   GUI    │  │   GUI    │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Communication Flow:

1. **Server starts** → Loads questions.json
2. **Admin creates room** → Server generates room code
3. **Clients connect** → Send JoinRequest with room/team/username
4. **Server validates** → Check room exists, team not full, username unique
5. **All players connected** → Game starts automatically
6. **Each round:**
   - Server broadcasts Question
   - Clients display question + timer
   - Players submit answers
   - Server processes (Semaphore/Barrier)
   - Server broadcasts Scoreboard
7. **Game ends** → Final scores sent to all clients

### Network Protocol:

All communication uses **Java Object Serialization** over TCP sockets.

**Message Types:**
- `JOIN_ROOM` → Client requests to join
- `JOIN_ACCEPTED` / `JOIN_REJECTED` → Server response
- `QUESTION_BROADCAST` → Server sends question
- `SUBMIT_ANSWER` → Client sends answer
- `SCOREBOARD_UPDATE` → Server sends scores
- `GAME_ENDED` → Server signals game over

---

## 🎓 Key Concepts

### Custom Synchronization (Phase 6):

**⚠️ CRITICAL:** You MUST implement these from scratch using only `wait()`, `notify()`, and `notifyAll()`. NO Java standard library synchronization!

1. **ModifiedSemaphore** (Individual Questions):
   - First 2 correct answers get 2x points
   - Timeout after 30 seconds
   - Returns score multiplier on acquire()

2. **ModifiedBarrier** (Team Questions):
   - Waits for all team members
   - All correct → 2x team points
   - Any wrong → best individual score only

3. **CustomThreadPool** (Optional):
   - Limit to 5 concurrent games
   - Queue additional games

---

## 📝 Next Steps

1. **Test the current setup:**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.iskahoot.server.Server"
   ```

2. **Start Phase 1:** Implement the GUI in `ClientGUI.java`

3. **Test JSON loading:**
   - Questions are already in `src/main/resources/questions.json`
   - QuestionLoader is ready to use

4. **Read the project specification** in `README.md`

---

## 🆘 Troubleshooting

**Problem:** Maven not found
- **Solution:** Install Maven and add to PATH

**Problem:** Java version mismatch
- **Solution:** Ensure JDK 17+ is installed and JAVA_HOME is set

**Problem:** Port 8080 already in use
- **Solution:** Change port in Server.java or kill process using port

**Problem:** Connection refused
- **Solution:** Ensure server is running before starting clients

---

Good luck with your project! 🚀

