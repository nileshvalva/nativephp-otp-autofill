<?php

namespace Nativephp\OtpAutofill;

use Illuminate\Support\ServiceProvider;
use Nativephp\OtpAutofill\Commands\CopyAssetsCommand;

class OtpAutofillServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->singleton(OtpAutofill::class, function () {
            return new OtpAutofill();
        });
    }

    public function boot(): void
    {
        // Register plugin hook commands
        if ($this->app->runningInConsole()) {
            $this->commands([
                CopyAssetsCommand::class,
            ]);
        }
    }
}