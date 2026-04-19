# OtpAutofill Plugin for NativePHP Mobile

Automatically detects and fills OTP codes from incoming SMS messages on Android and iOS

## Installation

```bash
composer require nativephp/otp-autofill
```

## Usage

```php
use Nativephp\OtpAutofill\Facades\OtpAutofill;

// Execute functionality
$result = OtpAutofill::execute(['option1' => 'value']);

// Get status
$status = OtpAutofill::getStatus();
```

## Listening for Events

```php
use Livewire\Attributes\On;

#[On('native:Nativephp\OtpAutofill\Events\OtpAutofillCompleted')]
public function handleOtpAutofillCompleted($result, $id = null)
{
    // Handle the event
}
```

## License

MIT