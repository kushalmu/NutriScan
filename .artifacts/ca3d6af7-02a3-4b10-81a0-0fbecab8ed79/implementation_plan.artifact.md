# Fix Room Compilation Error with Kotlin 2.2.10

The project is failing to build because Room 2.6.1's compiler (using Kapt) does not support the Kotlin metadata version (2.2.0) produced by Kotlin 2.2.10. This results in a `java.lang.IllegalArgumentException` during the `:app:kaptDebugKotlin` task.

## Proposed Changes

To resolve this, I will upgrade Room to version 2.8.4 and migrate from Kapt to KSP. KSP is the recommended annotation processor for Room and provides better compatibility with modern Kotlin versions.

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/gunam/.gemini/antigravity-ide/scratch/NutriScan/gradle/libs.versions.toml)
- Update `room` version to `2.8.4`.
- Add the `ksp` plugin definition to the `[plugins]` section.

#### [MODIFY] [build.gradle.kts (root)](file:///C:/Users/gunam/.gemini/antigravity-ide/scratch/NutriScan/build.gradle.kts)
- Add the KSP plugin to the `plugins` block with `apply false`.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/gunam/.gemini/antigravity-ide/scratch/NutriScan/app/build.gradle.kts)
- Replace `id("kotlin-kapt")` with `alias(libs.plugins.ksp)`.
- Replace `kapt(libs.room.compiler)` with `ksp(libs.room.compiler)`.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify that the project builds successfully.
- Run Room-related unit tests if available.

### Manual Verification
- Verify that the generated Room code (DAO implementations, etc.) is correctly created in the `build/generated/ksp` directory.
