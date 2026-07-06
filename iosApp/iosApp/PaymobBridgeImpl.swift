import Shared
import UIKit
import PaymobSDK

final class PaymobBridgeImpl: NSObject, PaymobNativeBridge, PaymobSDKDelegate {
    private var sdk: PaymobSDK?
    private var listener: PaymobListener?

    override init() {
        super.init()
    }

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
        let topController = UIApplication.shared.connectedScenes
            .compactMap { ($0 as? UIWindowScene)?.keyWindow }
            .first?.rootViewController ?? UIViewController()
        
        let proxy = PresentationProxyViewController(actualPresentingViewController: topController)
        return proxy
    }
}

private final class PresentationProxyViewController: UIViewController {
    let actualPresentingViewController: UIViewController
    
    init(actualPresentingViewController: UIViewController) {
        self.actualPresentingViewController = actualPresentingViewController
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func present(_ viewControllerToPresent: UIViewController, animated flag: Bool, completion: (() -> Void)? = nil) {
        if #available(iOS 15.0, *) {
            viewControllerToPresent.modalPresentationStyle = .pageSheet
            if let sheet = viewControllerToPresent.sheetPresentationController {
                sheet.detents = [.medium(), .large()]
                sheet.prefersGrabberVisible = true
                sheet.prefersScrollingExpandsWhenScrolledToEdge = true
                sheet.preferredCornerRadius = 24
            }
        } else {
            viewControllerToPresent.modalPresentationStyle = .pageSheet
        }
        
        actualPresentingViewController.present(viewControllerToPresent, animated: flag, completion: completion)
        var runCount = 0
        Timer.scheduledTimer(withTimeInterval: 0.05, repeats: true) { [weak viewControllerToPresent] timer in
            guard let vc = viewControllerToPresent else {
                timer.invalidate()
                return
            }
            
            vc.view.backgroundColor = .clear
            if let nav = vc as? UINavigationController {
                nav.view.backgroundColor = .clear
                if let topVC = nav.topViewController {
                    topVC.view.backgroundColor = .clear
                    if let firstSubview = topVC.view.subviews.first, firstSubview.bounds == topVC.view.bounds {
                        firstSubview.backgroundColor = .clear
                    }
                }
            }
            
            runCount += 1
            if runCount > 20 {
                timer.invalidate()
            }
        }
    }
}

