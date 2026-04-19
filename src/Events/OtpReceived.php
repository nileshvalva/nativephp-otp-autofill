<?php

namespace Nativephp\OtpAutofill\Events;

use Illuminate\Foundation\Events\Dispatchable;
use Illuminate\Queue\SerializesModels;

class OtpReceived
{
    use Dispatchable, SerializesModels;

    public function __construct(
        public string $otp,
        public string $message,
        public string $sender
    ) {}
}