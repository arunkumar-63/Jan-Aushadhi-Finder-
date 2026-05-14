# Jan Aushadhi Finder 💊

**Jan Aushadhi Finder** is a Android application designed to help users find affordable generic medicines and locate nearby Jan Aushadhi Kendras (generic medicine stores) in India. It also offers tools to compare prices between branded and generic medicines, calculating potential savings over time.

---

## ✨ Features

- **🔎 Smart Search:** Fuzzy-friendly search for branded medicines, instantly showing you the generic salt equivalents.
- **💰 Savings Calculator:** Compare the prices between expensive branded medicines and their generic counterparts to see your monthly and yearly savings.
- **🗺️ Store Locator:** Integrated Google Maps SDK to simulate and locate Jan Aushadhi Kendras within a 10 km radius.
- **🔔 Refill Reminders:** Set up monthly reminders for medicine refills, backed by `AlarmManager` and stored locally.
- **💾 Offline-First:** Powered by Room Database, seeded with 550 dummy branded-to-generic medicine records so it works right out of the box.

## 🛠️ Tech Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel) with Repository Pattern
- **UI & Design:** XML layouts with ViewBinding, Material Design 3 components, `RecyclerView`
- **Database:** Room Database (Offline-first approach)
- **Mapping:** Google Maps SDK
- **Background Tasks:** `AlarmManager` & Notifications

## 🚀 Getting Started

### Prerequisites
- Android Studio (Electric Eel or newer recommended)
- A valid Google Maps API Key.

### Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yourusername/jan-aushadhi-finder.git
   ```

2. **Open the project** in Android Studio.

3. **Add Google Maps API Key:**
   Open `app/src/main/res/values/strings.xml` and replace the placeholder with your actual API key:
   ```xml
   <string name="google_maps_key">YOUR_GOOGLE_MAPS_API_KEY</string>
   ```

4. **Sync Gradle** and run the app on an emulator or physical device.

*Note: On the first launch, the app automatically seeds the local database with dummy records from `SeedData` so you can start testing immediately!*

## 📱 Main Screens

| Screen | Description |
| :--- | :--- |
| **Home** | Your dashboard showing monthly and yearly savings based on your selected generic medicines. |
| **Search** | Robust search mechanism with "Did You Mean" suggestions, stock simulation, and price comparisons. |
| **Stores** | An interactive map locating simulated Jan Aushadhi stores near your location. |
| **Reminders** | A dedicated section to manage monthly refill schedules, ensuring you never run out. |

## 📂 Project Structure

```text
app/src/main/java/com/janaushadhi/finder
 ├── data/          # Room entities, DAO interfaces, AppDatabase, and SeedData
 ├── repository/    # Data repositories and fuzzy string matching logic
 ├── viewmodel/     # Screen ViewModels and ViewModelProviders Factory
 ├── ui/
 │   ├── home/      # Home screen and savings summary
 │   ├── search/    # Medicine search interface and adapters
 │   ├── map/       # Google Maps store locator integration
 │   ├── reminder/  # Refill reminder management
 │   └── calculator/# Selected medicine summary adapters
 └── worker/        # AlarmManager scheduler and BroadcastReceivers for notifications
```

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check [issues page](https://github.com/yourusername/jan-aushadhi-finder/issues).

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).
