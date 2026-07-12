<p align="center">
  <!-- ============================================================
       STEP 1: Upload your cover/banner image to GitHub by dragging
       it into any GitHub Issue comment box. You will get a URL like:
       https://github.com/user-attachments/assets/xxxxx
       Then replace the src below with that URL.
       Recommended size: 1440x744 px
       ============================================================ -->
   <img width="1440" height="800" alt="Image" src="https://github.com/user-attachments/assets/acf18e07-7b1e-459b-8c94-4ca8141b088f" />
</p>

<p align="center">
  <b>Troves</b> — A modern, cross-platform eCommerce application built with Kotlin Multiplatform.
  <br><br>
  Troves delivers a seamless, native-like shopping experience on both <b>Android</b> and <b>iOS</b> from a single shared codebase. It integrates deeply with the <b>Shopify API</b> via Apollo GraphQL for a rich product catalog, offers multiple payment solutions including <b>Paymob</b> and <b>Shopify Checkout Sheet Kit</b>, and features an intelligent <b>AI Shopping Assistant</b> powered by Google Gemini. A dedicated <b>AI Recommendation Engine</b> — "<b>Your Troves</b>" — surfaces hyper-personalized product suggestions on the home screen, learning each user's taste in real time. From browsing and favoriting to map-based delivery and secure checkout, Troves is a full end-to-end shopping platform.
  <br><br>
</p>

---

# Troves Tech Stack
- Kotlin Multiplatform + Compose Multiplatform for Android & iOS
- Shopify API (via Apollo GraphQL & Ktor REST) for product catalog, cart, and orders
- Google Gemini + Cloudflare Workers for the AI Shopping Assistant & **Your Troves** recommendation engine
- Firebase Auth (GitLive KMP SDK) for cross-platform authentication
- Supabase Edge Functions / Webhooks for payment orchestration

---

# Troves Features

## Home

> The Home screen is the app's main landing surface. It provides a personalized, data-driven shopping feed with dynamic sections to guide users toward products and brands they will love.

<img width="1440" height="800" alt="Image" src="https://github.com/user-attachments/assets/aa9060c8-6295-4f52-91c4-1616c4e4d9d1" />
<br>

> [!Note]
> ### 🏠 Dynamic Feed Sections
> - **Ad Slider**: A full-width promotional banner carousel for campaigns and featured collections.
> - **Brands Rail**: Horizontally scrollable brand logos with real Shopify smart-collection imagery.
> - **Your Troves** ✨: AI-powered personalized product rail — see the dedicated section below.
> - **Categories Rail**: Visual category chips with real Shopify collection images for quick navigation.
> - **Trending Now**: A curated product rail showcasing the most popular items.
>
> ### ⚡ Performance & UX
> - All feed sections are fetched **concurrently** (`async/await`) so the screen loads in the time of the slowest source.
> - **Shimmer skeleton** loading states while data is being fetched.
> - Real Shopify images loaded via **Coil 3** with design-system drawable fallbacks.

---

## Your Troves — AI Recommendations ✨

> **Your Troves** is the intelligent heart of the home screen. Powered by **Google Gemini** via a **Cloudflare Worker** backend, it analyzes each user's behavior, favorites, browsing history, and purchase patterns to surface a hyper-personalized product feed — unique to every shopper.

<!-- IMAGES: screenshot of the "Your Troves" recommendation section on home -->
<img width="1440" height="800" alt="Image" src="https://github.com/user-attachments/assets/09f8c98a-2f44-4730-8c63-9773dc1d2302" /><br>

> [!Note]
> ### 🧠 AI-Powered Personalization
> - **Behavior-aware**: Recommendations adapt in real time based on what the user browses, likes, and purchases.
> - **Powered by Google Gemini**: The AI model evaluates product attributes, user history, and preferences to rank and select the most relevant items.
> - **Cloudflare Worker Backend**: A lightweight, serverless inference layer that keeps the recommendation pipeline fast and scalable — no cold-start latency for the user.
>
> ### 🎯 Discovery Experience
> - Displayed prominently on the Home screen under the **"Your Troves"** heading so it's the first personalized content a returning user sees.
> - Each suggested product card links directly to the full **Product Details** screen.
> - Refreshes automatically as the user's in-app activity evolves — no manual input required.
>
> ### 🔒 Privacy & Fallback
> - New or guest users see a curated default selection (e.g., trending or top-rated items) until enough signal is gathered.
> - All personalization logic runs server-side — no raw user data is sent to third-party model providers beyond anonymized behavioral signals.



## Product Details

> Explore rich product information, browse variants, manage your wishlist, and add to cart — all from a polished, native-feeling detail screen.

<!-- IMAGES: screenshots of product details + reviews screens -->
<img width="1600" height="1024" alt="Image" src="https://github.com/user-attachments/assets/8d5614b3-69c5-4d1d-90d0-9c7bb1dd8ae0" /><br>

> [!Note]
> ### 🛍️ Product Information
> - Full product title, description, images, and price display.
> - Variant selection (size, color, etc.) with real-time price updates.
> - Star ratings and customer reviews section.
>
> ### ❤️ Wishlist & Cart
> - Add or remove products from **Favorites** (wishlist) — requires authentication.
> - **Add to Cart** action with auth gate: guests are prompted to sign in to continue.

---

## Cart & Checkout

> A complete, multi-step checkout experience that supports multiple payment methods and integrates deeply with Shopify's order management.

<!-- IMAGES: screenshots of cart and checkout/payment screens -->
<img width="1600" height="800" alt="Image" src="https://github.com/user-attachments/assets/a9739d0b-fe37-4d68-bf51-801778792b2f" />
<br>

> [!Note]
> ### 🛒 Cart Management
> - Add, remove, and update quantities for items in the cart.
> - Cart is linked to the authenticated user's Shopify account via Apollo GraphQL.
> - Real-time cart total and item count updates.
>
> ### 💳 Versatile Payment Options
> - **Paymob Integration**: Card-based payments orchestrated via **Supabase Edge Functions / Webhooks**, which create the corresponding Shopify order upon successful payment.
> - **Shopify Checkout Sheet Kit**: Native payment sheet for both Android and iOS.
> - **Cash on Delivery (COD)**: Available as a payment option.
>
> ### 📍 Delivery Address
> - Select or add a delivery address using the integrated **Mapbox** map.
> - Automatic camera animation to the user's current location.
> - Address management from the user profile.

---

## AI Shopping Assistant

> An intelligent, conversational shopping assistant powered by **Google Gemini**, backed by a serverless **Cloudflare Worker**, to help users discover products and get shopping advice.

<!-- IMAGES: screenshot of the AI chat screen -->
<img width="1600" height="867" alt="Image" src="https://github.com/user-attachments/assets/ca9a416c-eac8-4da0-adc7-657eba2f59ab" /><br>

> [!Note]
> ### 🤖 Conversational Commerce
> - Chat with the AI assistant to get product recommendations, compare items, and get styling advice.
> - Powered by **Google Gemini** with a **Cloudflare Worker** backend for serverless inference.
> - Chat history persisted locally so conversations are preserved across sessions.
>
> ### 🔍 Smart Discovery
> - Ask the assistant questions in natural language (e.g., "Find me a red jacket under $50").
> - Responses guide users directly to relevant product detail screens.

---

## Unified Identity & Authentication

> Troves uses a **guest-first** approach. Users can browse freely without being forced to log in, only being prompted when they attempt a protected action.

<!-- IMAGES: screenshots of onboarding and login screens -->
<img width="1600" height="800" alt="Image" src="https://github.com/user-attachments/assets/e058c82c-296a-4e6d-86e1-05e7ff24df1f" /><br>

> [!Note]
> ### 🚀 Onboarding
> - First-launch **Onboarding** flow shown once per device, then skipped on subsequent launches.
> - Onboarding completion state persisted via **DataStore**.
>
> ### 🔐 Authentication & Account
> - **Login & Register**: Secure account access powered by **Firebase Auth** (GitLive KMP SDK).
> - **Guest Mode**: Browse the entire catalog without an account. Auth is only required for cart, checkout, favorites, and profile.
> - **Auth Gate**: A shared `requireAuth` mechanism — when a guest triggers a protected action, an alert prompts them to "Sign up to continue" or "Cancel".
> - **Logout**: Sign out from the current device.
>
> ### 👤 User Profile
> - View and manage personal information.
> - Manage saved delivery addresses.
> - Browse order history and track past purchases.
> - Access saved Favorites (wishlist).
>
> ### 🎨 Personalization
> - **Multi-language**: Arabic and English support.
> - **Multi-theme**: Dark & Light mode switching.

---

## Mobile App Structure

Application structure built with a modular Clean Architecture approach, enabling team scaling and clear separation of concerns.

### Modularization Structure with dependencies



```
          ┌──────────────────────────────────────────┐
          │                androidApp                │
          │           (Android Entry Point)          │
          └──────────────────┬───────────────────────┘
                             │ depends on
          ┌──────────────────▼───────────────────────┐
          │                  shared                  │
          │        (Wires all modules + Koin DI)     │
          └────┬──────────────┬────────────────┬─────┘
               │              │                │
   ┌───────────▼──┐  ┌────────▼───────┐  ┌────▼────────────────┐
   │ presintation │  │     data       │  │    designSystem      │
   │ (MVI/UI/Nav) │  │ (Ktor/Room/API)│  │  (Compose UI Kit)   │
   └───────┬──────┘  └────────┬───────┘  └─────────────────────┘
           │                  │
           └──────────┬───────┘
                      │ depends on
           ┌──────────▼───────┐
           │      domain      │
           │ (UseCases/Entities│
           │  /Repositories)  │
           └──────────────────┘
```

---

## Technologies

### Core
- [Kotlin Multiplatform (KMP)](https://kotlinlang.org/docs/multiplatform.html)
- [Kotlinx Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Kotlinx Serialization (JSON)](https://kotlinlang.org/docs/serialization.html)

### UI / Compose
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Compose Material 3](https://developer.android.com/jetpack/compose/designsystems/material3)
- [Lucide Icons for CMP](https://github.com/composables/icons-lucide-cmp)
- [Coil 3 (Image Loading + GIF)](https://coil-kt.github.io/coil/compose/)

### Navigation
- [Jetpack Navigation 3 (androidx.navigation3)](https://developer.android.com/guide/navigation/navigation-multiplatform)
- [Lifecycle ViewModel Navigation 3](https://developer.android.com/jetpack/androidx/releases/lifecycle)
- [Material3 Adaptive Navigation 3 (CMP)](https://developer.android.com/develop/ui/compose/layouts/adaptive)

### Dependency Injection
- [Koin (Core, Compose, ViewModel, Navigation3)](https://insert-koin.io/)

### Networking & API
- [Apollo GraphQL (Kotlin)](https://www.apollographql.com/docs/kotlin/) — Shopify Storefront API
- [Ktor Client (Android, Darwin)](https://ktor.io/docs/welcome.html) — REST API calls
- [Apollo Normalized Cache (SQLite)](https://www.apollographql.com/docs/kotlin/caching/normalized-cache)

### Database & Storage
- [Room (SQLite)](https://developer.android.com/training/data-storage/room)
- [AndroidX DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore)

### Authentication
- [Firebase Auth — GitLive KMP SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk)

### Maps & Location
- [Mapbox Maps SDK (Android)](https://www.mapbox.com/)
- [Mapbox Compose Extension](https://docs.mapbox.com/android/maps/guides/)
- [Google Play Services Location](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary)

### Payments
- [Paymob Android SDK](https://developers.paymob.com/) — Card payments via Supabase webhook orchestration
- [Shopify Checkout Sheet Kit (Android)](https://github.com/Shopify/checkout-sheet-kit-android) — Native checkout sheet

### AI & Backend
- [Google Gemini](https://ai.google.dev/) — AI Shopping Assistant model
- [Cloudflare Workers](https://workers.cloudflare.com/) — Serverless AI backend
- [Supabase Edge Functions](https://supabase.com/docs/guides/functions) — Payment webhook orchestration

### Logging & Utilities
- [Kermit (KMP Logging)](https://github.com/touchlab/Kermit)
- [BuildKonfig](https://github.com/yshrsmz/BuildKonfig) — Build-time config injection

---

## Requirements

Before you begin, ensure you have met the following requirements:

- **Android Studio**
    - [Download Android Studio](https://developer.android.com/studio) (latest stable recommended)
    - Kotlin Multiplatform plugin installed

- **Xcode** (for iOS development only, requires a Mac)
    - [Download Xcode](https://developer.apple.com/xcode/)

- **API Keys & Config Files** (see Installation)

## Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/YOUR_USERNAME/Troves.git
    cd Troves
    ```

2. Add your `google-services.json` (Android) to `androidApp/` and `GoogleService-Info.plist` (iOS) to `iosApp/` for Firebase Auth.

3. Add your API keys and secrets to `local.properties`:
    ```properties
    # Shopify
    SHOPIFY_STORE_DOMAIN=your-store.myshopify.com
    SHOPIFY_ACCESS_TOKEN=your_storefront_api_token

    # Mapbox
    MAPBOX_ACCESS_TOKEN=your_mapbox_token

    # Gemini / Cloudflare AI Backend
    GEMINI_API_KEY=your_gemini_api_key
    CLOUDFLARE_AI_WORKER_URL=https://your-worker.workers.dev

    # Paymob
    PAYMOB_API_KEY=your_paymob_api_key
    ```

4. Sync the project in Android Studio and run it.

### Running the Apps

**Android App:**
```bash
./gradlew :androidApp:assembleDebug
```
*Or simply run the `androidApp` configuration in Android Studio.*

**iOS App:**
Open the `iosApp` directory in Xcode and run, or use the `iosApp` run configuration in Android Studio (requires a Mac).

### Running Tests

```bash
# Android host tests (unit tests)
./gradlew :shared:testAndroidHostTest

# iOS simulator tests
./gradlew :shared:iosSimulatorArm64Test
```

---


## Contributors

<p align="center">
  <!-- Replace YOUR_USERNAME/Troves with your actual GitHub repository path -->
  <a href="https://github.com/YOUR_USERNAME/Troves/graphs/contributors">
    <a href="https://github.com/Troves-eCommerce/Troves/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=Troves-eCommerce/Troves" />
</a>
  </a>
</p>

---

## License

    Copyright 2025 Troves

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
