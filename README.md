# EdgeSync — Real-Time IoT Protocol Benchmark & Analytics

**EdgeSync** is an Android application designed to benchmark and evaluate IoT synchronization protocols — **MQTT**, **HTTP Polling (REST)**, and **WebSockets** — under real-world conditions.

It simulates virtual IoT smart devices (e.g. smart plugs) and measures real-time performance metrics including latency, delivery reliability, and battery consumption profiles.

---

## 🚀 Key Features

- **Live Multi-Protocol Benchmarking:** Concurrent execution of MQTT, HTTP Polling, and WebSocket telemetry loops.
- **Interactive Dashboard:** Live latency sparklines, reliability percentages, and protocol comparison matrix.
- **Virtual Smart Devices:** Manage virtual IoT smart plugs with real-time toggle measurements.
- **Session History & Analytics:** Persistent benchmark session logging powered by **Room Database**.
- **Modern Jetpack Compose UI:** Material 3 design system with dynamic bottom navigation and adaptive layouts.
- **Android Splash Screen API:** Integrated `core-splashscreen` branding.

---

## 📊 Protocol Comparison Overview

| Metric | MQTT | HTTP Polling | WebSocket |
| :--- | :--- | :--- | :--- |
| **Architecture** | Publish / Subscribe | Request / Response | Full-Duplex |
| **Transport** | Persistent TCP | Transient HTTP / TCP | Persistent TCP |
| **Avg Latency** | ~10 - 30 ms (Ultra Low) | ~200+ ms (High) | ~20 - 50 ms (Low) |
| **Battery Impact** | Low | High | Medium |
| **Use Case** | Low-bandwidth IoT telemetry | Simple REST integrations | Web & mobile real-time streams |

---

## 🛠️ Architecture & Tech Stack

- **UI Framework:** Jetpack Compose (Material 3)
- **Navigation:** Jetpack Navigation Compose
- **Local Persistence:** Room Database with KSP annotation processing
- **Networking & Clients:**
  - HiveMQ MQTT Client (`com.hivemq:hivemq-mqtt-client`)
  - OkHttp 4 (`com.squareup.okhttp3:okhttp`) & Retrofit
- **State Management & Concurrency:** Kotlin Coroutines, StateFlow, ViewModel, `viewModelScope`
- **Build System:** Gradle Kotlin DSL (`build.gradle.kts`) with KSP (`com.google.devtools.ksp`)

---

## 📱 App Navigation Structure

1. **Benchmark Tab:** Real-time live execution dashboard with latency graphs and comparison table.
2. **Devices Tab:** Virtual smart plug management and individual connection statuses.
3. **Protocols Tab:** Detailed technical specifications and pros/cons for MQTT, HTTP, and WebSockets.
4. **History Tab:** Historical log of saved benchmark sessions with deletion option.

---

## ⚙️ Getting Started & Building

### Prerequisites
- **Android Studio** 2024.1+ (Ladybug / Jellyfish / Hedgehog)
- **JDK 17**
- **Android SDK 34** (Target API 34, Min API 24)

### Build Instructions
```bash
# Clone the repository
git clone https://github.com/nexis/EdgeSync.git
cd EdgeSync

# Build Debug APK
./gradlew assembleDebug

# Run Unit Tests & Sync
./gradlew test
```

---

## 📄 License
Licensed under the Apache License, Version 2.0.
