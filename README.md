# Android Totem Application

Native Android kiosk scaffold for the visitor-facing Entraide Nationale totem.

Planned architecture:

- Kotlin + Jetpack Compose.
- MVVM and Repository pattern.
- Public API client for `/api/public/snapshot` and `/api/public/changes`.
- Room local cache for offline content.
- Disk media cache for attract slides and center/service images.
- Map provider abstraction so MapLibre, OSM, or Google Maps can be swapped later.
- Kiosk controller for immersive mode, keep-screen-awake, idle timeout, and return-to-home behavior.

This app must never embed institution content directly in screens. Screens render models supplied by repositories.

