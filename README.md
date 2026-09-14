# Nixsum

Native mobile app for nixsum.co, built with Flutter.

## Overview

This is a "web-to-app" wrapper: the app loads https://nixsum.co inside an embedded WebView with configurable header, side drawer navigation, splash screen, theme, ads, push notifications and deep links.

## Configuration

App behaviour is defined at build time in a single offline config file:

```
assets/nixsum.json
```

Edit it before every build — no backend needed. It controls:

- app name, logo, base URL and language
- navigation style (side drawer / bottom nav / full screen / tabs)
- header icons, splash screen, walkthrough, exit popup
- theme (Default / Custom / Gradient) and colors
- ads (AdMob or Facebook, or none) and their IDs
- share content and About Us social links

## Rebranding

Identity is wired in these places:

- Dart package: `nixsum` (`pubspec.yaml` + `package:nixsum/` imports)
- Android: `co.nixsum.app` (namespace + applicationId, `android/app/build.gradle`)
- iOS/macOS bundle id: `co.nixsum.app`
- App display name: "Nixsum" (Android label, iOS `Info.plist`, macOS copyright)
- Brand color: `#161f2c`
- Method channels: `nixsum/channel` and `nixsum/events`
- Firebase: `android/app/google-services.json` + `ios/Runner/GoogleService-Info.plist`

## Building

- Android: `flutter build apk --release` or `flutter build appbundle --release`
- The GitHub Actions workflow builds both APK and AAB and keeps the release keystore in secrets.

## Push notifications

Push currently runs through OneSignal. `main.dart` seeds `OneSignal.initialize` with the configured one-signal ID from `nixsum.json`. iOS requires the OneSignalNotificationServiceExtension target. Firebase Messaging + Analytics are linked but intentionally not configured (no `google-services.json` collection) — see the Firebase console to re-register the `co.nixsum.app` package id if you enable them.