import UIKit
import SwiftUI
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Self.Context) -> UIViewController {
        let authHandler = IosGoogleAuthHandler()
        let checkoutBridge = IosCheckoutBridge()
        return MainViewControllerKt.MainViewController(
            googleAuthHandler: authHandler,
            checkoutBridge: checkoutBridge
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Self.Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
