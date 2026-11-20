package com.sofka.qa.web;

import com.sofka.qa.web.data.PurchaseData;
import com.sofka.qa.web.questions.CartContents;
import com.sofka.qa.web.tasks.AddProduct;
import com.sofka.qa.web.tasks.CheckoutAsGuest;
import com.sofka.qa.web.tasks.StartShopping;
import com.sofka.qa.web.ui.Store;
import net.serenitybdd.annotations.Managed;
import net.serenitybdd.annotations.Title;
import net.serenitybdd.annotations.WithTag;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.questions.Text;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SerenityRunner.class)
public class GuestPurchaseIT {
    @Managed
    WebDriver browser;

    @Test
    @Title("WEB-01 Compra de dos productos distintos como invitado")
    @WithTag("regression")
    public void guestBuysTwoAvailableProducts() {
        var data = PurchaseData.load();
        var buyer = Actor.named("Comprador invitado").whoCan(BrowseTheWeb.with(browser));
        buyer.attemptsTo(StartShopping.with(data));
        data.products().forEach(product -> buyer.attemptsTo(AddProduct.toCart(product, data)));
        buyer.attemptsTo(Open.url(data.route("checkout/cart")));
        buyer.should(seeThat("Productos y cantidades en el carrito",
                CartContents.forProducts(data.products()), equalTo(data.expectedCart())));
        buyer.attemptsTo(CheckoutAsGuest.with(data));
        buyer.should(seeThat("Confirmación exacta de la compra",
                Text.of(Store.SUCCESS_HEADING), equalTo("Your order has been placed!")));
    }
}
