import SwiftUI

@main
struct iOSApp: App {
    init() {
            KoinKt.initKoin()
        }
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}