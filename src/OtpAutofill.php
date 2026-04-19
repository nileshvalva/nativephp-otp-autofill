<?php

namespace Nativephp\OtpAutofill;

class OtpAutofill
{
    /**
     * Start listening for incoming OTP SMS messages
     */
    public function startListening(): ?object
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('OtpAutofill.StartListening', '{}');
            if ($result) {
                return json_decode($result)?->data ?? null;
            }
        }
        return null;
    }

    /**
     * Stop listening for OTP SMS messages
     */
    public function stopListening(): ?object
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('OtpAutofill.StopListening', '{}');
            if ($result) {
                return json_decode($result)?->data ?? null;
            }
        }
        return null;
    }

    /**
     * Get current listener status
     */
    public function getStatus(): ?object
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('OtpAutofill.GetStatus', '{}');
            if ($result) {
                return json_decode($result)?->data ?? null;
            }
        }
        return null;
    }
}