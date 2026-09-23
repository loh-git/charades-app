# Ultimate Charades — Guess It!

A native Android party game: phone-on-forehead charades with tilt controls, built in
Kotlin and Jetpack Compose.


## Gallery

![Cyberpunk Neon theme](https://github.com/loh-git/charades-app/raw/master/assets/cyberpunk-neon.jpg)
![Sunset Arcade theme](https://github.com/loh-git/charades-app/raw/master/assets/sunset-arcade.jpg)
![Terminal theme](https://github.com/loh-git/charades-app/raw/master/assets/terminal.jpg)
![Pop-Art theme](https://github.com/loh-git/charades-app/raw/master/assets/pop-art.jpg)


## What it is

Hold the phone on your forehead, your team acts out the word on screen, you tilt down to pass
and tilt up when you get it. It's the classic "Heads Up!"-style party game — no accounts, no
backend, no internet connection needed. Everything runs and persists entirely on-device.

## Features

- **Classic & Party modes** — solo rounds, or set up named teams and let the app hand off turns
  with a running scoreboard and final standings.
- **16 built-in decks, 675+ words** — movies split by genre (horror, action, romance, historical,
  cult classics), animals, sports, music (instruments, plus pop/rock/legendary musicians), food,
  famous actors, and everyday objects — curated specifically for what's actually easy to act out.
- **Custom deck builder** — write your own word lists under custom categories for in-jokes or
  niche topics no built-in deck covers.
- **Tilt-based gameplay** driven by the accelerometer, with a tap-to-play fallback for devices or
  situations where tilt doesn't work well.
- **Four fully distinct visual themes** — Sunset Arcade, Matrix Terminal, Pop Art, and Cyberpunk
  Neon — each reskins colors, borders, shadows, and glow effects through one shared theming
  system (down to monospace typography for Matrix Terminal), not just a palette swap.
- **Lifetime stats** — rounds played, correct/passed totals, best score, and longest streak,
  tracked locally.
- **Synthesized audio feedback** — the correct/incorrect/countdown sounds are raw PCM waveforms
  generated in code (sine and triangle synthesis), not bundled audio files.
- **Ads that stay out of the way** — a single interstitial, gated to only ever appear between
  rounds, never mid-game.

## Tech stack

- **Kotlin** + **Jetpack Compose** (Material 3) for the whole UI, MVVM throughout
  (`ViewModel` + `StateFlow`)
- **Jetpack DataStore (Preferences)** for all persistence — decks, settings, stats, favorites —
  no SQL, no server
- **Navigation Compose** for screen flow
- **kotlinx.serialization** for custom deck storage
- **AdMob + Google UMP** for monetization and consent, isolated behind a small `AdProvider`
  interface so the rest of the app never touches the vendor SDK directly
- Min SDK 26, target SDK 35

## Architecture notes

No backend, no accounts, no network calls beyond the ad SDK's own traffic — every feature
(custom decks, stats, party mode) works fully offline by design. Theming is a
`CompositionLocal`-based `ExtendedColors` layer on top of Material 3's `ColorScheme`, which lets
each theme override colors, borders, shadows, and glow effects independently instead of screens
branching on which theme is active.

## Running it

```bash
git clone <repo-url>
cd charades-app
./gradlew assembleDebug
```

The repo ships with Google's public test AdMob IDs so it builds and runs out of the box — a real
AdMob app ID/ad unit ID is only needed for an actual release build.
