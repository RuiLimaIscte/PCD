# IsKahoot - Quick Start Guide

## 🚀 Get Started in 5 Minutes

### Step 1: Build the Project
```bash
cd C:/Users/limar/IdeaProjects/PCD
mvn clean compile
```

### Step 2: Start the Server
**Terminal 1:**
```bash
mvn exec:java -Dexec.mainClass="com.iskahoot.server.Server"
```

You should see:
```
╔════════════════════════════════════════╗
║     IsKahoot Server Started            ║
╚════════════════════════════════════════╝
Server listening on port: 8080
```

### Step 3: Create a Room
In the server terminal, type:
```
> create 2
```

The server will respond with a room code (e.g., "1234"). **Remember this code!**

### Step 4: Start 4 Clients (2 Teams)

**Terminal 2 - Alice (Team A):**
```bash
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_A Alice
```

**Terminal 3 - Bob (Team A):**
```bash
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_A Bob
```

**Terminal 4 - Charlie (Team B):**
```bash
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_B Charlie
```

**Terminal 5 - Diana (Team B):**
```bash
java -cp target/classes com.iskahoot.client.Client localhost 8080 1234 TEAM_B Diana
```

### Step 5: Game Starts!
Once all 4 players connect, the game starts automatically.

---

## 📋 Server Commands

| Command | Description | Example |
|---------|-------------|---------|
| `create <teams>` | Create new room | `create 2` |
| `list` | List all active rooms | `list` |
| `status <code>` | Show room details | `status 1234` |
| `help` | Show commands | `help` |
| `exit` | Stop server | `exit` |

---

## 🎮 Client Command Format

```bash
java -cp target/classes com.iskahoot.client.Client <IP> <PORT> <RoomCode> <TeamCode> <Username>
```

**Parameters:**
- `IP`: Server IP address (use `localhost` for local testing)
- `PORT`: Server port (default: `8080`)
- `RoomCode`: 4-digit room code from server
- `TeamCode`: Your team identifier (e.g., `TEAM_A`, `TEAM_B`)
- `Username`: Your unique username (must be unique across all players)

---

## 🔧 Using IntelliJ IDEA

### Run Server:
1. Open `src/main/java/com/iskahoot/server/Server.java`
2. Right-click → Run 'Server.main()'

### Run Client:
1. Open `src/main/java/com/iskahoot/client/Client.java`
2. Right-click → Modify Run Configuration
3. Add Program Arguments: `localhost 8080 1234 TEAM_A Alice`
4. Click Run

### Run Multiple Clients:
1. Run → Edit Configurations
2. Select Client configuration
3. Check "Allow multiple instances"
4. Create separate configurations for each player with different arguments

---

## 🐛 Common Issues

### "Connection refused"
- **Cause:** Server not running
- **Fix:** Start the server first

### "Room not found"
- **Cause:** Wrong room code or room doesn't exist
- **Fix:** Create room first, use correct code

### "Username already in use"
- **Cause:** Another player is using the same username
- **Fix:** Choose a different username

### "Team is full"
- **Cause:** Team already has 2 players
- **Fix:** Join a different team or create new room

### "Port 8080 already in use"
- **Cause:** Another process is using port 8080
- **Fix:** Kill the process or change port in Server.java

---

## 📁 Project Files Overview

```
Key Files to Work On:
├── ClientGUI.java          ← Phase 1: Implement GUI
├── GameState.java          ← Phase 2: Complete game state
├── QuestionLoader.java     ← Phase 3: Already done! ✓
├── Server.java             ← Phase 4: Connection logic
├── PlayerHandler.java      ← Phase 5: Message handling
├── ModifiedSemaphore.java  ← Phase 6: Custom semaphore
├── ModifiedBarrier.java    ← Phase 6: Custom barrier
└── CustomThreadPool.java   ← Phase 8: Optional
```

---

## 🎯 Next Steps for Development

### For Intermediate Delivery (Nov 11-14):

**Phase 1 - GUI:**
- [ ] Edit `ClientGUI.java`
- [ ] Add question display panel
- [ ] Add 4 answer buttons
- [ ] Add timer label (countdown from 30)
- [ ] Add scoreboard panel

**Phase 2 - GameState:**
- [ ] Complete `GameState.java`
- [ ] Add answer collection logic
- [ ] Add score calculation methods

**Phase 3 - JSON:**
- [x] Already working! Test it:
```java
Quiz quiz = QuestionLoader.loadFromResources("questions.json");
System.out.println(quiz.getName());
System.out.println(quiz.getQuestions().size());
```

---

## 📚 Useful Resources

- **Project Spec:** `README.md`
- **Detailed Setup:** `SETUP.md`
- **This Guide:** `QUICKSTART.md`
- **Maven Docs:** https://maven.apache.org/guides/
- **Gson Tutorial:** https://github.com/google/gson

---

## 💡 Tips

1. **Test incrementally** - Don't wait until everything is done
2. **Use print statements** - Debug with `System.out.println()`
3. **Test with 1 team first** - Easier to debug with 2 players
4. **Read the spec carefully** - All requirements are in README.md
5. **Ask for help early** - Don't wait until the deadline

---

## ✅ Verification Checklist

Before intermediate delivery (Nov 11-14):
- [ ] Server starts without errors
- [ ] Can create rooms via TUI
- [ ] Clients can connect to server
- [ ] Questions load from JSON
- [ ] GUI displays (even if basic)

Before final delivery (Dec 15):
- [ ] Full game cycle works
- [ ] Custom Semaphore implemented
- [ ] Custom Barrier implemented
- [ ] Scoreboard updates correctly
- [ ] Game ends properly
- [ ] Multiple rooms work simultaneously

---

Good luck! 🎓

