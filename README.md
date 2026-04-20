# NativePHP OTP Autofill

[![Latest Version on Packagist](https://img.shields.io/packagist/v/nileshvalva/nativephp-otp-autofill.svg)](https://packagist.org/packages/nileshvalva/nativephp-otp-autofill)
[![License](https://img.shields.io/github/license/nileshvalva/nativephp-otp-autofill)](LICENSE)

Automatically detects and fills OTP codes from incoming SMS messages on **Android** and **iOS** for NativePHP Mobile apps.

## Features

- ✅ Android — reads SMS directly via BroadcastReceiver (silent, no user interaction)
- ✅ iOS — clipboard watcher + system AutoFill support
- ✅ Supports 4–8 digit OTP codes
- ✅ Fires a Laravel event when OTP is detected
- ✅ Works with any frontend (Blade, Livewire, Vue, Alpine)

## Requirements

- PHP 8.2+
- Laravel 10+
- NativePHP Mobile v3+

## Installation

```bash
composer require nileshvalva/nativephp-otp-autofill
```

Publish and register the plugin:

```bash
php artisan vendor:publish --tag=nativephp-plugins-provider
php artisan native:plugin:register nileshvalva/nativephp-otp-autofill
```

## Usage

### Start / Stop Listening

```php
use Nativephp\OtpAutofill\Facades\OtpAutofill;

// Start listening for OTP
OtpAutofill::startListening();

// Stop listening
OtpAutofill::stopListening();

// Get current status
$status = OtpAutofill::getStatus();
```

### Listen for OTP in PHP / Livewire

```php
use Nativephp\OtpAutofill\Events\OtpReceived;

// Standard Laravel event listener
Event::listen(OtpReceived::class, function ($event) {
    $otp     = $event->otp;
    $message = $event->message;
    $sender  = $event->sender;
});

// Livewire component
use Livewire\Attributes\On;

#[On('native:Nativephp\OtpAutofill\Events\OtpReceived')]
public function handleOtp($otp, $message, $sender)
{
    $this->otpCode = $otp;
}
```

### Listen for OTP in JavaScript

```js
document.addEventListener('native-event', (event) => {
    const detail = event.detail;
    if (detail?.event === 'Nativephp\\OtpAutofill\\Events\\OtpReceived') {
        const otp = detail.payload?.otp;
        const input = document.querySelector('input[autocomplete="one-time-code"]');
        if (input) {
            input.value = otp;
            input.dispatchEvent(new Event('input', { bubbles: true }));
        }
    }
});
```

### HTML Input

Add `autocomplete="one-time-code"` to your input for iOS system AutoFill support:

```html
<input
    type="tel"
    name="otp"
    autocomplete="one-time-code"
    maxlength="6"
    placeholder="------"
/>
```

## Platform Notes

### Android
- `RECEIVE_SMS` and `READ_SMS` permissions are declared automatically
- OTP is detected silently in the background — no user interaction needed

### iOS
- Uses **clipboard watcher** — checks clipboard every 1 second for OTP codes
- Uses **iOS system AutoFill** — iOS suggests OTP above keyboard automatically
- iOS 16+ shows a one-time "Allow Paste" dialog — this is standard iOS privacy behavior

## Testing

### Android Emulator
```bash
adb emu sms send 1234567890 "Your OTP is 847291. Valid for 10 minutes."
```

### iOS Simulator
```bash
xcrun simctl pbcopy booted "Your OTP is 847291. Valid for 10 minutes."
```

## Changelog

### v1.0.1 — Initial Release
- Android SMS detection via BroadcastReceiver
- iOS clipboard watcher support
- iOS system AutoFill support
- OTP extraction with regex (4–8 digits)
- Fires `OtpReceived` event to Laravel

## Author

**Nilesh Valva**
- GitHub: [@nileshvalva](https://github.com/nileshvalva)
- Packagist: [nileshvalva/nativephp-otp-autofill](https://packagist.org/packages/nileshvalva/nativephp-otp-autofill)

## License

MIT License. See [LICENSE](LICENSE) for details.