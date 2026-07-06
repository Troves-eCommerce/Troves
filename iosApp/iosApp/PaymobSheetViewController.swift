
//  Created by Waheed on 05/07/2026.

import Foundation
import UIKit
import WebKit

final class PaymobSheetViewController: UIViewController {

    private let checkoutUrl: URL

    init(checkoutUrl: URL) {
        self.checkoutUrl = checkoutUrl
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder: NSCoder) {
        fatalError()
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        let webView = WKWebView(frame: view.bounds)
        webView.autoresizingMask = [.flexibleWidth, .flexibleHeight]

        view.addSubview(webView)

        webView.load(URLRequest(url: checkoutUrl))
    }
}
