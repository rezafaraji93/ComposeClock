# ComposeClock 🕐

A beautiful, animated analog clock built with Kotlin Multiplatform and Jetpack Compose Multiplatform. Features stunning visual effects that respond to time changes and platform-specific optimizations for Android, iOS, Desktop (JVM), and Web.

## 🎬 Demo Video

https://github.com/user-attachments/assets/composeclock-demo

*Watch the ComposeClock in action with all its stunning animations and effects!*

## ✨ Features

### 🎨 Animated Effects
- **Breathing Effect**: Gentle pulsing animation with color-shifting aurora-like glow
- **Aurora Effect**: Dynamic northern lights-inspired animated rings with sweeping gradients
- **Cosmic Stars**: Starfield animation for a space-themed experience
- **Water Ripples**: Fluid ripple effects triggered by time changes
- **None**: Clean, minimalist clock without effects

### 🎯 Interactive Controls
- Easy-to-use animation selector with intuitive button layout
- Real-time animation switching
- Smooth transitions between effects

### ⏰ Accurate Time Display
- Real-time analog clock with smooth second hand animation
- Platform-specific timezone handling
- Precise hour, minute, and second indicators
- Traditional 12-hour format with numbered hour markers

### 🚀 Multiplatform Support
- **Android**: Native Android app with Material Design 3
- **iOS**: Native iOS app with SwiftUI integration
- **Desktop**: Cross-platform desktop application (Windows, macOS, Linux)
- **Web**: Modern web application with WASM support

## 🛠️ Technical Stack

- **Kotlin Multiplatform** - Shared business logic across platforms
- **Jetpack Compose Multiplatform** - Modern declarative UI framework
- **Kotlinx DateTime** - Cross-platform date and time handling
- **Kotlinx Coroutines** - Asynchronous programming
- **Compose Animation** - Smooth, performant animations

## 📱 Platform Details

### Android
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Features**: Edge-to-edge display, Material Design 3 theming

### iOS
- **Framework**: Native iOS framework integration
- **Architecture**: ARM64 support for both device and simulator

### Desktop
- **JVM Target**: Java 11
- **Distributions**: DMG (macOS), MSI (Windows), DEB (Linux)
- **Features**: Native window management with Compose Desktop

### Web
- **JavaScript**: Modern browser support with ES6+
- **WASM**: WebAssembly for enhanced performance
- **Features**: Responsive design, modern web standards

## 🏗️ Project Structure

```
ComposeClock/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/          # Shared Kotlin code
│   │   │   ├── animation/       # Animation effects system
│   │   │   ├── model/          # Data models and enums
│   │   │   ├── ui/             # Compose UI components
│   │   │   └── App.kt          # Main application composable
│   │   ├── androidMain/        # Android-specific code
│   │   ├── iosMain/           # iOS-specific code
│   │   ├── jvmMain/           # Desktop-specific code
│   │   ├── webMain/           # Web-specific code
│   │   └── wasmJsMain/        # WebAssembly-specific code
│   └── build.gradle.kts       # Module build configuration
├── gradle/
│   └── libs.versions.toml     # Dependency version catalog
└── build.gradle.kts           # Root build configuration
```

## 🎮 Animation System

The project features a sophisticated animation system with the following components:

### AnimationManager
- Centralized animation effect management
- Effect registration and lifecycle management
- Draw coordination and timing control

### Animation Effects
Each effect implements the `AnimationEffect` interface:

- **BreathingEffect**: Organic pulsing with color transitions
- **AuroraEffect**: Multi-layered aurora simulation with gradient sweeps
- **CosmicStarsEffect**: Particle-based starfield animation
- **WaterRipplesEffect**: Fluid ripple propagation effects

### Animation Triggers
- Second-based animation triggers for time-synchronized effects
- Smooth spring animations for clock hands
- 60 FPS rendering for fluid motion

## 🚀 Getting Started

### Prerequisites
- **Kotlin**: 2.2.20+
- **Gradle**: 8.0+
- **Android Studio**: Arctic Fox+ (for Android development)
- **Xcode**: 14+ (for iOS development)
- **JDK**: 11+ (for desktop development)

### Building the Project

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ComposeClock
   ```

2. **Build for Android**
   ```bash
   ./gradlew :composeApp:assembleDebug
   ```

3. **Build for iOS**
   ```bash
   ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
   ```

4. **Build for Desktop**
   ```bash
   ./gradlew :composeApp:runDistributable
   ```

5. **Build for Web**
   ```bash
   ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
   ```

### Running the Application

#### Android
```bash
./gradlew :composeApp:installDebug
```

#### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and run on simulator or device.

#### Desktop
```bash
./gradlew :composeApp:run
```

#### Web
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```
Then open `http://localhost:8080` in your browser.

## 🎨 Customization

### Adding New Animation Effects

1. Create a new class implementing `AnimationEffect`:
   ```kotlin
   class CustomEffect : AnimationEffect {
       override val animationType = AnimationType.CUSTOM
       
       @Composable
       override fun initializeAnimations() {
           // Initialize your animations
       }
       
       override fun drawEffect(drawScope: DrawScope, center: Offset, radius: Float) {
           // Implement your drawing logic
       }
       
       override suspend fun triggerAnimation() {
           // Define animation trigger behavior
       }
   }
   ```

2. Add the new type to `AnimationType` enum
3. Register the effect in `App.kt`

### Styling the Clock

The analog clock appearance can be customized by modifying:
- Clock face colors and markers in `AnalogClock.kt`
- Animation selector styling in `AnimationSelector.kt`
- Material Design theme in `App.kt`

## 🔧 Development

### Hot Reload
The project supports Compose Hot Reload for faster development:
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun --continuous
```

### Testing
```bash
./gradlew test
```

### Code Style
The project follows Kotlin coding conventions and uses ktlint for code formatting.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🙏 Acknowledgments

- Jetpack Compose Multiplatform team for the excellent framework
- Kotlin Multiplatform team for cross-platform capabilities
- Material Design team for design guidelines and components

## 📞 Support

If you encounter any issues or have questions, please open an issue on GitHub or contact the maintainers.

---

**Made with ❤️ using Kotlin Multiplatform and Jetpack Compose**