import SwiftUI
import FirebaseCore
import FirebaseAuth
import GoogleSignIn
import Shared

@main
struct iOSApp: App {
    
    init() {
        FirebaseApp.configure()
        
        KoinInitKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    GIDSignIn.sharedInstance.handle(url)
                }
        }
    }
}