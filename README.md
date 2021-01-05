# gaconsentmanager-android-plugin
Appodeal Consent Manager for Godot

API Compatible with [iOS module](https://github.com/PoqXert/gaconsentmanager-ios-module).

## Setup

If not already done, make sure you have enabled and successfully set up Android Custom Builds. Grab the``GAConsentManager`` plugin binary and config from the releases page and put both into res://android/plugins. The plugin should now show up in the Android export settings, where you can enable it.

## Usage
To use the ``GAConsentManager`` API you first have to get the ``GAConsentManager`` singleton:
```gdscript
var _consent_manager

func _ready():
  if Engine.has_singleton("GAConsentManager"):
    _consent_manager = Engine.get_singeton("GAConsentManager")
```
### Regulations
```gdscript
enum Regulation {
  UNKNOWN = 0,
  NONE = 1,
  GDPR = 2,
  CCPA = 3,
}
```
### Statuses
```gdscript
enum Status {
  UNKNOWN = 0,
  NON_PERSONALIZED = 1,
  PARTLY_PERSONALIZED = 2,
  PERSONALIZED = 3,
}
```
### Methods
#### Synchronize
```gdscript
# Synchronize Consent Manager SDK
func synchronize(app_key: String) -> void
```
```gdscript
# Sunchronize Consent Manager SDK with params
func sunchronizeWithParams(app_key: String, params: Dictionary) -> void
```
#### Consent Dialog
```gdscript
# Should a consent dialog be displayed?
func shouldShowConsentDialog() -> bool
```
```gdscript
# Load Consent Dialog
func loadConsentDialog() -> void
```
```gdscript
# Is the dialog ready for display?
func isConsentDialogReady() -> bool
```
```gdscript
# Show Consent Dialog
func showConsentDialog() -> void
```
```gdscript
# Is the consent dialog shown?
func isConsentDialogPresenting() -> bool
```
#### Other
```gdscript
# Get regulation
func getRegulation() -> int
```
```gdscript
# Get status
func getStatus() -> int
```
```gdscript
# Get consent
func getConsent() -> bool
```
```gdscript
# Get IAB string
func getIABConsentString() -> String
```
```gdscript
# Get consent for vendor
func hasConsentForVendor(bundle: String) -> bool
```
```gdscript
# Enable IAB storage
func enableIABStorage() -> void
```
### Signals
#### Sunchronize
```gdscript
# Emit when Consent Manager SDK synchronized
signal synchronized()
```
```gdscript
# Emit when Consent Manager SDK failed to synchronize
signal synchronize_failed(error: String)
```
#### Consent Dialog
```gdscript
# Emit when Consent Dialog loaded
signal dialog_loaded()
```
```gdscript
# Emit when Consent Dialog shown
signal dialog_shown()
```
```gdscript
# Emit when Consent Dialog failed to show
signal dialog_failed()
```
```gdscript
# Emit when Consent Dialog closed
signal dialog_closed()
```
