# CLAUDE.md — Scamazon Android Frontend

## Project Overview
- **Platform:** Android (Kotlin + Jetpack Compose)
- **Architecture:** MVVM — Screen → ViewModel → Repository → Retrofit Service
- **State management:** `StateFlow` + `collectAsStateWithLifecycle`
- **DI:** Manual DI via `ViewModelFactory`
- **Auth:** JWT tokens stored via `TokenManager`, injected by `AuthInterceptor` in `RetrofitClient`
- **Real-time:** Microsoft SignalR (`SignalRManager`) for chat and order notifications
- **Push notifications:** Firebase Cloud Messaging (`ScamazonFirebaseService`)
- **Images:** Coil (`AsyncImage`)
- **Maps:** Google Maps Compose (`maps-compose`)
- **Backend:** ASP.NET Core 8 REST API — user does **NOT** maintain backend code

## Focus: Frontend Only
User only works on the Android frontend. Do **not** suggest changes to backend code. All API integration is done via Retrofit services in `data/remote/`.

## Key File Locations
```
Scamazon-Frontend/app/src/main/java/com/example/scamazon_frontend/
├── core/
│   ├── firebase/ScamazonFirebaseService.kt   ← FCM push service
│   ├── network/RetrofitClient.kt             ← HTTP + JWT interceptor
│   ├── network/SignalRManager.kt             ← WebSocket real-time
│   └── utils/
│       ├── TokenManager.kt                   ← JWT storage
│       ├── CartBadgeNotificationHelper.kt    ← Cart badge notification
│       └── CartCountManager.kt               ← Global cart count StateFlow
├── data/
│   ├── models/                               ← DTOs per feature
│   ├── remote/                               ← Retrofit API interfaces
│   └── repository/                           ← Data layer
├── di/ViewModelFactory.kt                    ← Manual DI
└── ui/
    ├── components/                           ← Reusable Composables
    ├── navigation/NavGraph.kt                ← All routes
    ├── screens/                              ← One folder per feature
    └── theme/                                ← Colors, Typography, Shapes, Dimens
```

## Coding Rules
- **Always use `collectAsStateWithLifecycle`**, not `collectAsState` for ViewModel flows (lifecycle-aware).
- **All network calls go through Repository**, never call Retrofit services directly from ViewModel.
- **Loading/Error/Success states** must use `Resource<T>` sealed class — never raw nullable.
- **Use `LafyuuXxx` design system components** (LafyuuTextField, LafyuuPrimaryButton, etc.) — do not use plain Material3 components where a Lafyuu variant exists.
- **Cart count** is a global singleton (`CartCountManager.cartCount: StateFlow<Int>`). Update it after any add/remove/cart change so the badge stays in sync.
- **Do not hardcode strings** visible to users — keep them in Composable parameters or a future strings.xml.
- **Do not add address field to LoginScreen** — address is only for profile/checkout.

## Known Issues to Fix (from requirements review)

### HIGH PRIORITY
1. **Missing Filter UI in ProductListScreen** — `SortFilterBar` only exposes Sort. The API (`ProductQueryRequestDto`) already supports `brandId`, `minPrice`, `maxPrice`, `minRating` but there is no Filter bottom sheet in the UI. Need to add a Filter button next to Sort that opens a FilterBottomSheet.
2. **No "Clear Cart" button** — `CartScreen` allows removing items one by one but has no bulk "Clear All" action. Requirement explicitly asks for clearing the entire cart.
3. **Address field missing from RegisterScreen** — Requirement says registration can optionally include address. Currently only name/email/phone/password.

### MEDIUM PRIORITY
4. **Hardcoded shipping fee** — `CartScreen` shows `$40.00` hardcoded. Should come from API or a configurable constant.
5. **Hardcoded notification badge** — `HomeScreen` passes `notificationBadge = 3` hardcoded to `LafyuuMainAppBar`. Should observe unread notification count from `NotificationViewModel`.
6. **"See All" navigation in HomeScreen** — Flash Sale, Mega Sale, and Category "See All" clicks have empty lambdas (`{ }`). Should navigate to `ProductListScreen` with appropriate filter params.
7. **Cart badge trigger** — `CartBadgeNotificationHelper.showCartBadge()` needs to be called from `MainActivity`'s `onStop`/`onPause` lifecycle, and `cancelCartBadge()` in `onResume`. Verify this wiring exists.

### LOW PRIORITY
8. **Username auto-generated from email** — `RegisterScreen` derives username as `email.substringBefore("@")`. User cannot choose their own username. Consider adding an explicit username field or clarifying this is intentional.
9. **Settings screen** — Placeholder only. Not required by grading rubric.
10. **Offers screen** — Placeholder only. Not required by grading rubric.

## Requirements Checklist (Final Project Grading)

| # | Feature | Weight | Status | Notes |
|---|---------|--------|--------|-------|
| 1 | Authentication (Sign Up/Login) | 10% | ✅ Done | Forgot password + OTP also implemented |
| 2 | List of Products | 15% | ⚠️ Partial | Sort ✅, Filter ❌ (brand/price/rating filter UI missing) |
| 3 | Product Details | 15% | ✅ Done | Images, specs, quantity, add to cart, reviews |
| 4 | Product Cart | 15% | ⚠️ Partial | Item remove ✅, quantity adjust ✅, clear entire cart ❌ |
| 5 | Billing | 10% | ✅ Done | COD + VNPay, order success screen |
| 6 | Notification (Cart Badge) | 15% | ✅ Done | NotificationCompat + CartBadgeNotificationHelper |
| 7 | Map Screen | 10% | ✅ Done | Google Maps, store marker, directions |
| 8 | Chat Screen | 10% | ✅ Done | SignalR real-time, image support |

**Estimated score: ~85–90% if filter UI and clear-cart are fixed.**

## Architecture Patterns to Follow
When adding new features:
1. Add DTOs in `data/models/{feature}/`
2. Add Retrofit interface in `data/remote/{Feature}Service.kt`
3. Register service in `RetrofitClient` (or `ViewModelFactory`)
4. Add repository in `data/repository/{Feature}Repository.kt`
5. Add ViewModel in `ui/screens/{feature}/{Feature}ViewModel.kt`
6. Add Screen Composable in `ui/screens/{feature}/{Feature}Screen.kt`
7. Register route in `ui/navigation/Screen.kt` and `NavGraph.kt`

## Do Not
- Do not use `LiveData` — project uses `StateFlow` throughout
- Do not use XML layouts — project is 100% Jetpack Compose
- Do not call `viewModel.someMethod()` inside Composable body directly — always use `LaunchedEffect` or event handlers
- Do not modify `RetrofitClient.BASE_URL` without confirming with user — it points to the remote API server
- Do not add Hilt/Koin — project uses manual DI intentionally
- Do not break existing `LafyuuXxx` component contracts — other screens depend on them
