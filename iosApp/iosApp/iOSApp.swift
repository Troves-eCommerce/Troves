import SwiftUI
import FirebaseCore
import FirebaseAuth
import shared

@main
struct iOSApp: App {
    
    init() {
        // 1. تهيئة Firebase أولاً
        FirebaseApp.configure()
        
        // 2. تهيئة Koin الخاصة بمشروعك
        KoinInitKt.initKoin() 
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}