import Shared
import UIKit
import PaymobSDK

final class PaymobBridgeImpl: NSObject, PaymobNativeBridge, PaymobSDKDelegate {
    private var sdk: PaymobSDK?
    private var listener: PaymobListener?

    func startPayment(clientSecret: String, publicKey: String, listener: PaymobListener) {
        self.listener = listener

        let sdk = PaymobSDK()
        sdk.delegate = self
        self.sdk = sdk

        do {
            try sdk.presentPayVC(
                VC: currentViewController(),
                PublicKey: publicKey,
                ClientSecret: clientSecret
            )
        } catch {
            listener.onFailure(msg: error.localizedDescription)
        }
    }

    func transactionRejected() {
        listener?.onFailure(msg: nil)
    }

    func transactionAccepted(transactionDetails: [String: Any]) {
        let payResponse = KotlinMutableDictionary<NSString, AnyObject>()
        transactionDetails.forEach { key, value in
            payResponse[key as NSString] = String(describing: value) as NSString
        }
        listener?.onSuccess(payResponse: payResponse)
    }

    func transactionPending() {
        listener?.onPending()
    }

    private func currentViewController() -> UIViewController {
        UIApplication.shared.connectedScenes
            .compactMap { ($0 as? UIWindowScene)?.keyWindow }
            .first?.rootViewController ?? UIViewController()
    }
}

