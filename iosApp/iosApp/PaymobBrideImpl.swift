// iosApp/PaymobBridgeImpl.swift
import Shared
import PaymobSDK
import UIKit

class PaymobBridgeImpl: NSObject, PaymobNativeBridge, PaymobSDKDelegate {
    private var paymobSDK: PaymobSDK?
    private var currentListener: PaymobListener?

    func startPayment(clientSecret: String, publicKey: String, listener: PaymobListener) {
        self.currentListener = listener
        self.paymobSDK = PaymobSDK()
        self.paymobSDK?.delegate = self

        guard let vc = currentViewController() else {
            listener.onFailure(msg: "Could not find root view controller")
            return
        }

        do {
            try paymobSDK?.presentPayVC(VC: vc, PublicKey: publicKey, ClientSecret: clientSecret, SavedBankCards: [])
        } catch {
            listener.onFailure(msg: error.localizedDescription)
        }
    }

    // MARK: - PaymobSDKDelegate

    func transactionAccepted(transactionDetails: [String : Any]) {
        let kotlinDict = KotlinMutableDictionary<NSString, NSString>()
        for (key, value) in transactionDetails {
            kotlinDict[key as NSString] = "\(value)" as NSString
        }
        currentListener?.onSuccess(payResponse: kotlinDict)
    }

    func transactionRejected() {
        currentListener?.onFailure(msg: "Transaction Rejected")
    }

    func transactionPending() {
        currentListener?.onPending()
    }

    private func currentViewController() -> UIViewController? {
        UIApplication.shared.connectedScenes
            .compactMap { ($0 as? UIWindowScene)?.keyWindow }
            .first?.rootViewController
    }
}