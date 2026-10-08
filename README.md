# WinKey — a small library for a handy keyer

This project brings Java and WinKey together. The `winkey-lib` module models
the WinKey protocol and provides an interface for communicating with a WinKey
device. The project is still growing, but its Maven structure is ready for
more modules as things evolve.

## 🛠️ Build

You’ll need **JDK 21**. No separate Maven installation is required — the Maven
Wrapper is included.

From the project root, run:

```bash
./mvnw clean verify
```

On Windows, use `mvnw.cmd`:

```bat
mvnw.cmd clean verify
```

This builds the aggregator project and its modules, then runs the tests. The
library JAR will be at
`winkey-lib/target/winkey-lib-0.1.0-SNAPSHOT.jar`.

## 🧱 Architecture

The repository is a Maven multi-module project:

```text
.
├── pom.xml       # Aggregator POM: shared versions and build configuration
└── winkey-lib/   # Java library for WinKey
```

- **Aggregator (`winkey-pom`)**: Has `pom` packaging and manages shared
  dependencies, plugins, and the project modules.
- **`winkey-lib`**: Contains the WinKey model — including commands,
  configuration, and responses — along with transport interfaces and a serial
  transport implementation. Serial communication is handled by jSerialComm.

In short: the model describes *what* is sent to or received from WinKey, while
the transport takes care of *how* those bytes travel to and from the device. 📡