import SwiftUI
import FirebaseCore
import FirebaseAuth
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
        }
    }
}