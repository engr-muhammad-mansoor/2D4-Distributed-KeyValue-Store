# 2D#4 - Distributed Key-Value Store Network

A Java-based distributed key-value store network implementation developed as part of the IN2011 Computer Networks coursework (2023/2024). This project implements a peer-to-peer network system where nodes can store and retrieve key-value pairs across a distributed network.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [Components](#components)
- [Protocol](#protocol)
- [License](#license)

## 🎯 Overview

2D#4 is a distributed storage system that allows nodes in a network to store and retrieve key-value pairs. The system consists of two types of nodes:

- **FullNode**: Persistent nodes that maintain key-value storage and handle network requests
- **TemporaryNode**: Client nodes that connect to the network to store or retrieve data

## ✨ Features

- **Distributed Storage**: Store and retrieve key-value pairs across a network of nodes
- **Network Discovery**: Nodes can discover and connect to each other
- **Thread-Safe Server**: FullNode uses multi-threaded architecture to handle concurrent connections
- **Command-Line Interface**: Easy-to-use CLI tools for interacting with the network
- **SHA-256 Hashing**: Secure hash-based identification using SHA-256 algorithm

## 🏗️ Architecture

The system follows a client-server architecture where:

- **FullNode** acts as a server that:
  - Listens for incoming connections
  - Maintains a local key-value store
  - Handles PUT, GET, and NOTIFY requests
  - Manages network topology through a network map

- **TemporaryNode** acts as a client that:
  - Connects to FullNodes in the network
  - Sends store/get requests
  - Retrieves data from the network

## 📁 Project Structure

```
2D#4/
├── src/
│   ├── FullNode.java           # Full node implementation (server)
│   ├── TemporaryNode.java      # Temporary node implementation (client)
│   ├── HashID.java             # SHA-256 hash computation utility
│   ├── CmdLineFullNode.java    # CLI tool to run a FullNode
│   ├── CmdLineStore.java       # CLI tool to store key-value pairs
│   └── CmdLineGet.java         # CLI tool to retrieve values
├── out/                        # Compiled class files
└── README.md                   # This file
```

## 📦 Requirements

- **Java Development Kit (JDK)**: Version 8 or higher
- **Java Runtime Environment (JRE)**: For running the compiled classes

## 🚀 Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/2D#4.git
cd 2D#4
```

2. Compile the Java source files:
```bash
javac src/*.java -d out/
```

## 💻 Usage

### Starting a FullNode

To start a FullNode server that other nodes can connect to:

```bash
java -cp out CmdLineFullNode <startingNodeName> <startingNodeAddress> <ipAddress> <portNumber>
```

**Example:**
```bash
java -cp out CmdLineFullNode Node1 localhost:12345 127.0.0.1 12345
```

### Storing a Key-Value Pair

To store a key-value pair in the network:

```bash
java -cp out CmdLineStore <startingNodeName> <startingNodeAddress> <key> <value>
```

**Example:**
```bash
java -cp out CmdLineStore Node1 localhost:12345 "myKey" "myValue"
```

### Retrieving a Value

To retrieve a value using its key:

```bash
java -cp out CmdLineGet <startingNodeName> <startingNodeAddress> <key>
```

**Example:**
```bash
java -cp out CmdLineGet Node1 localhost:12345 "myKey"
```

## 🔧 Components

### FullNode

The `FullNode` class implements the server-side functionality:

- **Key-Value Store**: In-memory HashMap for storing key-value pairs
- **Network Map**: Maintains information about connected nodes
- **Request Handlers**: Processes PUT, GET, and NOTIFY requests
- **Multi-threaded Server**: Handles multiple concurrent connections

**Key Methods:**
- `startServer()`: Starts the server and listens for connections
- `handleClient(Socket)`: Handles individual client connections
- `handlePutRequest()`: Processes store requests
- `handleGetRequest()`: Processes retrieval requests
- `handleNotifyRequest()`: Processes node notification requests
- `listen(String, int)`: Sets up server socket listening

### TemporaryNode

The `TemporaryNode` class implements the client-side functionality:

- **Network Connection**: Establishes connections to FullNodes
- **Store Operation**: Stores key-value pairs on the network
- **Get Operation**: Retrieves values from the network

**Key Methods:**
- `start(String, String)`: Connects to the network
- `store(String, String)`: Stores a key-value pair
- `get(String)`: Retrieves a value by key

### HashID

The `HashID` utility class provides SHA-256 hash computation:

- `computeHashID(String)`: Computes SHA-256 hash for strings ending with newline character

## 📡 Protocol

The system uses a text-based protocol for communication:

### START Message
- **Client → Server**: `START <version> <nodeType>`
- **Server → Client**: `START <version> <nodeType>`

### PUT Request
- **Client → Server**: 
  ```
  PUT? <numKeyLines> <numValueLines>
  <key lines>
  <value lines>
  ```
- **Server → Client**: `SUCCESS` or `END <error message>`

### GET Request
- **Client → Server**: 
  ```
  GET? <numKeyLines>
  <key lines>
  ```
- **Server → Client**: 
  - `VALUE <numValueLines>` followed by value lines (if found)
  - `NOPE` (if not found)

### NOTIFY Request
- **Client → Server**: 
  ```
  NOTIFY?
  <nodeName>:<nodeAddress>
  ```
- **Server → Client**: `NOTIFIED` or `END <error message>`

## 🎓 Course Information

This project was developed as part of:
- **Course**: IN2011 Computer Networks
- **Academic Year**: 2023/2024
- **Institution**: [Your Institution]

## 📝 Notes

- All keys and values must end with a newline character (`\n`)
- The system uses in-memory storage (data is not persisted to disk)
- FullNodes maintain a network map for node discovery
- The implementation supports concurrent connections through multi-threading

## 🤝 Contributing

This is a coursework project. If you're interested in extending or improving it, feel free to fork and submit pull requests!

## 📄 License

This project is provided as-is for educational purposes. Please refer to your institution's academic integrity policies regarding code sharing and usage.

---

**Developed with ❤️ for IN2011 Computer Networks Coursework**

