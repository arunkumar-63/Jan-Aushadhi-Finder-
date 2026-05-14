# Jan-Aushadhi Finder

Complete Kotlin Android app for finding affordable generic medicines and simulated nearby Jan Aushadhi Kendras.

## Stack

- Kotlin
- XML layouts with ViewBinding
- MVVM with Repository pattern
- Room Database
- RecyclerView
- Google Maps SDK
- AlarmManager refill reminders
- Material Design 3

## Setup

1. Open this folder in Android Studio.
2. Replace `YOUR_GOOGLE_MAPS_API_KEY` in `app/src/main/res/values/strings.xml`.
3. Sync Gradle and run the `app` configuration.

The medicine database is offline-first. On first launch, Room seeds 550 dummy branded-to-generic medicine records from `SeedData`.

## Main Screens

- Home: monthly and yearly savings calculator based on selected medicines.
- Search: fuzzy-friendly branded medicine search with generic salt, price comparison, stock simulation, and Did You Mean suggestions.
- Stores: Google Map with simulated Jan Aushadhi Kendra markers within 10 km.
- Reminders: monthly refill reminders stored in Room and scheduled with AlarmManager.

## Project Structure

```text
app/src/main/java/com/janaushadhi/finder
  data/          Room entities, DAO, database, seed data
  repository/    Repository and fuzzy matching logic
  viewmodel/     Screen ViewModels and factory
  ui/home/       Home and savings summary
  ui/search/     Medicine search screen and adapter
  ui/map/        Google Maps store locator
  ui/reminder/   Refill reminder screen and adapter
  ui/calculator/ Selected medicine adapter
  worker/        AlarmManager scheduler and notification receiver
```
