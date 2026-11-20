package com.sofka.qa.web.ui;

import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public final class Store {
    public static final By CART_PRODUCT_LINK = By.cssSelector("td:nth-child(2) a");
    public static final By CART_QUANTITY = By.cssSelector("input[name^='quantity']");
    public static final Target CART_TOTAL = css("Resumen del carrito", "#cart-total");
    public static final Target PRODUCT_NAME = css("Nombre del producto", "#content .col-sm-4 h1");
    public static final Target CONTENT = css("Contenido de la tienda", "#content");
    public static final Target QUANTITY = css("Cantidad", "#input-quantity");
    public static final Target ADD = css("Agregar al carrito", "#button-cart");
    public static final Target ADDED = css("Confirmación de agregado", ".alert-success");
    public static final Target CART_ROWS = css("Filas del carrito", "#content form .table tbody tr");
    public static final Target CART_ERROR = css("Error del carrito", "#checkout-cart .alert-danger");
    public static final Target CHECKOUT = css("Ir al checkout", "#content .buttons a[href*='route=checkout/checkout']");
    public static final Target GUEST = css("Guest Checkout", "input[name='account'][value='guest']");
    public static final Target CONTINUE_ACCOUNT = css("Continuar como invitado", "#button-account");
    public static final Target COUNTRY = field("country");
    public static final Target REGION = field("zone");
    public static final Target SAME_ADDRESS = css("Misma dirección de envío", "input[name='shipping_address']");
    public static final Target CONTINUE_GUEST = css("Guardar datos del invitado", "#button-guest");
    public static final Target CONTINUE_SHIPPING = css("Continuar envío", "#button-shipping-method");
    public static final Target DEMO_PAYMENT = css("Pago de demostración contra entrega", "input[name='payment_method'][value='cod']");
    public static final Target CONTINUE_PAYMENT = css("Continuar pago", "#button-payment-method");
    public static final Target AGREE = css("Aceptar términos", "input[name='agree']");
    public static final Target CONFIRM = css("Confirmar orden", "#button-confirm");
    public static final Target CONFIRM_ROWS = css("Productos de la orden", "#collapse-checkout-confirm .table tbody tr");
    public static final Target SUCCESS_HEADING = css("Orden confirmada", "#content h1");

    private Store() { }

    public static Target field(String field) {
        return css("Dato del comprador: " + field, "#input-payment-" + field);
    }

    public static Target shipping(String method) {
        if (!method.matches("[a-zA-Z0-9_.-]+")) throw new IllegalArgumentException("Método de envío inválido");
        return css("Método de envío " + method, "input[name='shipping_method'][value='" + method + "']");
    }

    public static Target cartRow(String productId) {
        return Target.the("Fila del producto " + productId).locatedBy(
                "//div[@id='content']//form//tbody/tr[td/a[substring-before(concat(substring-after(@href,'product_id='),'&'),'&')='" + productId + "']]");
    }

    private static Target css(String name, String selector) {
        return Target.the(name).located(By.cssSelector(selector));
    }
}
