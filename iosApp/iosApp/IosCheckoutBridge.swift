import Foundation
import UIKit
import Shared
import ShopifyCheckoutSheetKit

final class IosCheckoutBridge: NSObject, PresintationIosCheckoutBridge {

    private var eventDelegate: CheckoutBridgeDelegate?

    func present(
        checkoutUrl: String,
        onCompleted: @escaping (PresintationCheckoutCompletedEvent) -> Void,
        onFailed: @escaping (String) -> Void,
        onCanceled: @escaping () -> Void
    ) {
        guard let url = URL(string: checkoutUrl) else {
            onFailed("Invalid checkout URL")
            return
        }
        guard let rootViewController = Self.topViewController() else {
            onFailed("Cannot find root view controller")
            return
        }

        let delegate = CheckoutBridgeDelegate(
            onCompleted: onCompleted,
            onFailed: onFailed,
            onCanceled: onCanceled
        )
        eventDelegate = delegate

        ShopifyCheckoutSheetKit.present(
            checkout: url,
            from: rootViewController,
            delegate: delegate
        )
    }

    private static func topViewController() -> UIViewController? {
        guard let scene = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .first else { return nil }
        let keyWindow = scene.windows.first(where: { $0.isKeyWindow }) ?? scene.windows.first
        var controller = keyWindow?.rootViewController
        while let presented = controller?.presentedViewController {
            controller = presented
        }
        return controller
    }
}

final class CheckoutBridgeDelegate: NSObject, CheckoutDelegate {

    private let onCompleted: (PresintationCheckoutCompletedEvent) -> Void
    private let onFailed: (String) -> Void
    private let onCanceled: () -> Void

    init(
        onCompleted: @escaping (PresintationCheckoutCompletedEvent) -> Void,
        onFailed: @escaping (String) -> Void,
        onCanceled: @escaping () -> Void
    ) {
        self.onCompleted = onCompleted
        self.onFailed = onFailed
        self.onCanceled = onCanceled
    }

    func checkoutDidComplete(event: ShopifyCheckoutSheetKit.CheckoutCompletedEvent) {
        onCompleted(CheckoutEventMapper.toDomain(event))
    }

    func checkoutDidCancel() {
        onCanceled()
    }

    func checkoutDidFail(error: ShopifyCheckoutSheetKit.CheckoutError) {
        onFailed(error.localizedDescription)
    }

    func checkoutDidClickLink(url: URL) {
        UIApplication.shared.open(url)
    }
}

enum CheckoutEventMapper {

    static func toDomain(
        _ event: ShopifyCheckoutSheetKit.CheckoutCompletedEvent
    ) -> PresintationCheckoutCompletedEvent {
        PresintationCheckoutCompletedEvent(orderDetails: orderDetails(event.orderDetails))
    }

    private static func orderDetails(
        _ order: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.OrderDetails
    ) -> PresintationOrderDetails {
        PresintationOrderDetails(
            billingAddress: order.billingAddress.map(address),
            cart: cartInfo(order.cart),
            deliveries: order.deliveries.map(deliveryInfo),
            email: order.email,
            id: order.id,
            paymentMethods: order.paymentMethods.map(paymentMethod),
            phone: order.phone
        )
    }

    private static func cartInfo(
        _ cart: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.CartInfo
    ) -> PresintationCartInfo {
        PresintationCartInfo(
            lines: cart.lines.map(cartLine),
            price: price(cart.price),
            token: cart.token
        )
    }

    private static func cartLine(
        _ line: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.CartLine
    ) -> PresintationCartLine {
        PresintationCartLine(
            discounts: line.discounts?.map(discount),
            image: line.image.map(cartLineImage),
            merchandiseId: line.merchandiseId,
            price: money(line.price),
            productId: line.productId,
            quantity: Int32(line.quantity),
            title: line.title
        )
    }

    private static func cartLineImage(
        _ image: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.CartLineImage
    ) -> PresintationCartLineImage {
        PresintationCartLineImage(
            altText: image.altText,
            lg: image.lg,
            md: image.md,
            sm: image.sm
        )
    }

    private static func price(
        _ price: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.Price
    ) -> PresintationPrice {
        PresintationPrice(
            discounts: price.discounts?.map(discount),
            shipping: price.shipping.map(money),
            subtotal: price.subtotal.map(money),
            taxes: price.taxes.map(money),
            total: price.total.map(money)
        )
    }

    private static func money(
        _ money: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.Money
    ) -> PresintationMoneyV2 {
        PresintationMoneyV2(
            amount: boxed(money.amount),
            currencyCode: money.currencyCode
        )
    }

    private static func address(
        _ address: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.Address
    ) -> PresintationAddress {
        PresintationAddress(
            address1: address.address1,
            address2: address.address2,
            city: address.city,
            countryCode: address.countryCode,
            firstName: address.firstName,
            lastName: address.lastName,
            name: address.name,
            phone: address.phone,
            postalCode: address.postalCode,
            referenceId: address.referenceId,
            zoneCode: address.zoneCode
        )
    }

    private static func deliveryInfo(
        _ delivery: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.DeliveryInfo
    ) -> PresintationDeliveryInfo {
        PresintationDeliveryInfo(
            details: deliveryDetails(delivery.details),
            method: delivery.method
        )
    }

    private static func deliveryDetails(
        _ details: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.DeliveryDetails
    ) -> PresintationDeliveryDetails {
        PresintationDeliveryDetails(
            additionalInfo: details.additionalInfo,
            location: details.location.map(address),
            name: details.name
        )
    }

    private static func paymentMethod(
        _ method: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.PaymentMethod
    ) -> PresintationPaymentMethod {
        PresintationPaymentMethod(
            details: method.details,
            type: method.type
        )
    }

    private static func discount(
        _ discount: ShopifyCheckoutSheetKit.CheckoutCompletedEvent.Discount
    ) -> PresintationDiscount {
        PresintationDiscount(
            amount: discount.amount.map(money),
            applicationType: discount.applicationType,
            title: discount.title,
            value: boxed(discount.value),
            valueType: discount.valueType
        )
    }

    private static func boxed(_ value: Double?) -> KotlinDouble? {
        guard let value = value else { return nil }
        return KotlinDouble(value)
    }
}
