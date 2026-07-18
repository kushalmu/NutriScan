# Add Preview for NutritionResultCard

This plan outlines the steps to add a `@Preview` composable for the `NutritionResultCard` in `ScannerScreen.kt`.

## Proposed Changes

### [ScannerScreen.kt](file:///C:/Users/gunam/.gemini/antigravity-ide/scratch/NutriScan/app/src/main/java/com/example/nutriscan/ui/scanner/ScannerScreen.kt)

#### [MODIFY] [ScannerScreen.kt](file:///C:/Users/gunam/.gemini/antigravity-ide/scratch/NutriScan/app/src/main/java/com/example/nutriscan/ui/scanner/ScannerScreen.kt)
- Add `import androidx.compose.ui.tooling.preview.Preview` if missing.
- Add `PreviewNutritionResultCard` at the bottom of the file.
- Use `NutriScanTheme` for the preview.
- Create a sample `NutritionInfo` object for the preview.

## Verification Plan

### Manual Verification
- Render the `PreviewNutritionResultCard` using `render_compose_preview`.
- Check for any compilation errors using `analyze_file`.
