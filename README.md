<div align="center">

[![ConnTest](./.github/ConnTestLogo.svg)](#ConnTest)

</div>

# ConnTest

**ConnTest** is a lightweight tool for testing and analyzing network communication. It can operate as a **client** or **server**, and displays all incoming and outgoing messages in a simple text-based chat interface.

---

## How to use

1. Download the latest release
2. Run `ConnTest.exe` or if it is a jar file, run `java -jar ConnTest.jar`

### Server Mode
- Enter a port number
- Click **Start** to begin listening

### Client Mode
- Enter the server **IP address** and **port**
- Click **Connect** to establish connection

> ⚠️ **Note**:  
> Closing the application window (X) will **not** terminate the app.  
> You must right-click the **tray icon** and choose **Exit** to quit the application completely.

---

## Building

### Requirements
- Java **21** or later
- Git

```bash
git clone https://github.com/Suchti18/ConnTest.git
```
```bash
mvn clean package
```

---

## License

[Unlicense](https://unlicense.org)