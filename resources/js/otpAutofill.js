/**
 * OtpAutofill Plugin for NativePHP Mobile
 *
 * @example
 * import { otpAutofill } from '@nativephp/otp-autofill';
 *
 * // Execute functionality
 * const result = await otpAutofill.execute({ option1: 'value' });
 *
 * // Get status
 * const status = await otpAutofill.getStatus();
 */

const baseUrl = '/_native/api/call';

/**
 * Internal bridge call function
 * @private
 */
async function bridgeCall(method, params = {}) {
    const response = await fetch(baseUrl, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]')?.content || ''
        },
        body: JSON.stringify({ method, params })
    });

    const result = await response.json();

    if (result.status === 'error') {
        throw new Error(result.message || 'Native call failed');
    }

    const nativeResponse = result.data;
    if (nativeResponse && nativeResponse.data !== undefined) {
        return nativeResponse.data;
    }

    return nativeResponse;
}

/**
 * Execute the plugin functionality
 * @param {Object} options - Options to pass to the native function
 * @returns {Promise<any>}
 */
export async function execute(options = {}) {
    return bridgeCall('OtpAutofill.Execute', options);
}

/**
 * Get the current status
 * @returns {Promise<Object>}
 */
export async function getStatus() {
    return bridgeCall('OtpAutofill.GetStatus');
}

/**
 * OtpAutofill namespace object
 */
export const otpAutofill = {
    execute,
    getStatus
};

export default otpAutofill;