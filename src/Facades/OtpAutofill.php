<?php

namespace Nativephp\OtpAutofill\Facades;

use Illuminate\Support\Facades\Facade;

/**
 * @method static mixed execute(array $options = [])
 * @method static object|null getStatus()
 *
 * @see \Nativephp\OtpAutofill\OtpAutofill
 */
class OtpAutofill extends Facade
{
    protected static function getFacadeAccessor(): string
    {
        return \Nativephp\OtpAutofill\OtpAutofill::class;
    }
}