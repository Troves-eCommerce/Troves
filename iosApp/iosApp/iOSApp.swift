import SwiftUI
import FirebaseCore
import FirebaseAuth
import GoogleSignIn
import Shared

@main
struct iOSApp: App {
    
    init() {
        FirebaseApp.configure()
        configureGoogleSignIn()
        
        KoinInitKt.doInitKoin()
        PaymobBridgeHolder.shared.bridge = PaymobBridgeImpl()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
            }
        }
    }

    private func configureGoogleSignIn() {
        guard let clientID = FirebaseApp.app()?.options.clientID else {
            assertionFailure("Missing Firebase client ID for Google Sign-In configuration.")
            return
        }

        GIDSignIn.sharedInstance.configuration = GIDConfiguration(clientID: clientID)
    }
}
