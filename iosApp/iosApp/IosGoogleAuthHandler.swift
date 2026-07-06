import Foundation
import UIKit
import Shared
import GoogleSignIn
import FirebaseAuth

class IosGoogleAuthHandler: NSObject, GoogleAuthHandler {
    func signIn(
        onSuccess: @escaping (String, String?) -> Void,
        onError: @escaping (KotlinException) -> Void
    ) {
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
              let rootViewController = windowScene.windows.first?.rootViewController else {
            onError(KotlinException(message: "Cannot find root view controller"))
            return
        }

        GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { signInResult, error in
            if let error = error {
                onError(KotlinException(message: error.localizedDescription))
                return
            }

            guard let result = signInResult else { return }

            result.user.refreshTokensIfNeeded { user, error in
                if let error = error {
                    onError(KotlinException(message: error.localizedDescription))
                    return
                }

                guard let idToken = user?.idToken?.tokenString else {
                    onError(KotlinException(message: "No ID token found"))
                    return
                }

                let accessToken = user?.accessToken.tokenString
                onSuccess(idToken, accessToken)
            }
        }
    }
}
