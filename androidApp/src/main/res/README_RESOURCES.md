# Android Resources Guide

## Resource Directory Structure

In a Kotlin Multiplatform project, all Android-specific resources go in:
```
androidApp/src/main/res/
```

## Directory Structure

```
androidApp/src/main/res/
├── values/
│   ├── strings.xml          # String resources
│   ├── colors.xml           # Color definitions
│   └── themes.xml           # App themes
├── mipmap-*/                # App icons (different densities)
│   ├── ic_launcher.png     # Launcher icon
│   └── ic_launcher_round.png # Round launcher icon
├── drawable/                # Drawable resources
│   └── ic_launcher_foreground.xml
└── mipmap-anydpi-v26/       # Adaptive icons (API 26+)
    ├── ic_launcher.xml
    └── ic_launcher_round.xml
```

## Adding Resources

### 1. Strings
Edit `res/values/strings.xml` to add string resources:
```xml
<string name="my_string">My String Value</string>
```

Use in code:
```kotlin
context.getString(R.string.my_string)
```

### 2. Colors
Edit `res/values/colors.xml` to add colors:
```xml
<color name="my_color">#FF6200EE</color>
```

Use in code:
```kotlin
val color = ContextCompat.getColor(context, R.color.my_color)
```

### 3. Icons
- **Launcher Icons**: Place PNG files in `res/mipmap-*/` directories:
  - `mipmap-mdpi/` (48x48)
  - `mipmap-hdpi/` (72x72)
  - `mipmap-xhdpi/` (96x96)
  - `mipmap-xxhdpi/` (144x144)
  - `mipmap-xxxhdpi/` (192x192)

- **Adaptive Icons** (Android 8.0+): Use `mipmap-anydpi-v26/` with XML

### 4. Drawables
Place drawable resources in `res/drawable/`:
- Vector drawables (`.xml`)
- Bitmap drawables (`.png`, `.jpg`)

## Generating Icons

### Option 1: Android Studio
1. Right-click `res` → New → Image Asset
2. Choose "Launcher Icons (Adaptive and Legacy)"
3. Configure and generate

### Option 2: Online Tools
- [Android Asset Studio](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html)
- [App Icon Generator](https://www.appicon.co/)

### Option 3: Manual
1. Create icon images in different densities
2. Place them in corresponding `mipmap-*/` folders
3. Name them `ic_launcher.png` and `ic_launcher_round.png`

## Quick Setup

For a quick start, you can:
1. Use the default Material icons provided
2. Replace `ic_launcher_foreground.xml` with your custom icon
3. Update colors in `colors.xml` to match your brand

## Notes

- **DO NOT** put resources in the `shared` module - only Android-specific resources
- Resources are Android-only and won't be available in iOS
- Use string resources instead of hardcoded strings for localization support
- Material 3 theming is handled in Compose, but XML themes are still needed for the manifest

