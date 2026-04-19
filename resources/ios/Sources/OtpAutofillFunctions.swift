import Foundation
import UIKit

enum OtpAutofillFunctions {

    static var isListening = false
    static var clipboardTimer: Timer?
    static var lastClipboardContent = ""

    // MARK: - OTP Extraction
    static func extractOtp(from text: String) -> String? {
        let pattern = #"\b(\d{4,8})\b"#
        guard let regex = try? NSRegularExpression(pattern: pattern),
              let match = regex.firstMatch(
                in: text,
                range: NSRange(text.startIndex..., in: text)
              ),
              let range = Range(match.range(at: 1), in: text)
        else { return nil }
        return String(text[range])
    }

    // MARK: - Clipboard Watcher
    static func startClipboardWatcher() {
        // Delay initial read by 1 second to avoid
        // immediate clipboard permission dialog
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) {
            lastClipboardContent = UIPasteboard.general.string ?? ""

            clipboardTimer = Timer.scheduledTimer(
                withTimeInterval: 1.0,
                repeats: true
            ) { _ in
                guard let current = UIPasteboard.general.string,
                    current != lastClipboardContent else { return }

                lastClipboardContent = current

                guard let otp = extractOtp(from: current) else { return }

                DispatchQueue.main.async {
                    LaravelBridge.shared.send?(
                        "Nativephp\\OtpAutofill\\Events\\OtpReceived",
                        [
                            "otp": otp,
                            "message": current,
                            "sender": "clipboard"
                        ]
                    )
                }
            }
        }
    }

    static func stopClipboardWatcher() {
        clipboardTimer?.invalidate()
        clipboardTimer = nil
    }

    // MARK: - Bridge Functions

    class StartListening: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            guard !OtpAutofillFunctions.isListening else {
                return BridgeResponse.success(data: ["status": "already_listening"])
            }

            OtpAutofillFunctions.isListening = true

            // Start clipboard watcher on main thread
            DispatchQueue.main.async {
                OtpAutofillFunctions.startClipboardWatcher()
            }

            return BridgeResponse.success(data: [
                "status": "listening",
                "methods": [
                    "system_autofill": "iOS will suggest OTP above keyboard automatically",
                    "clipboard": "Watching clipboard every 1 second for copied OTPs"
                ]
            ])
        }
    }

    class StopListening: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            OtpAutofillFunctions.isListening = false

            DispatchQueue.main.async {
                OtpAutofillFunctions.stopClipboardWatcher()
            }

            return BridgeResponse.success(data: ["status": "stopped"])
        }
    }

    class GetStatus: BridgeFunction {
        func execute(parameters: [String: Any]) throws -> [String: Any] {
            return BridgeResponse.success(data: [
                "status": OtpAutofillFunctions.isListening ? "listening" : "idle",
                "version": "1.0.0",
                "platform": "ios"
            ])
        }
    }
}