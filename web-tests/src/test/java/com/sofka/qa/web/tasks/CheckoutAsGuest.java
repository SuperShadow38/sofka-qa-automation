package com.sofka.qa.web.tasks;

import com.sofka.qa.web.data.PurchaseData;
import com.sofka.qa.web.ui.Store;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.SelectFromOptions;
import net.serenitybdd.screenplay.waits.WaitUntil;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;

import java.time.Duration;
import java.util.Map;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CheckoutAsGuest implements Task {
    private final PurchaseData data;

    public CheckoutAsGuest(PurchaseData data) { this.data = data; }

    public static CheckoutAsGuest with(PurchaseData data) {
        return Tasks.instrumented(CheckoutAsGuest.class, data);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Click.on(Store.CHECKOUT),
                WaitUntil.the(Store.GUEST, isClickable()).forNoMoreThan(20).seconds(),
                Click.on(Store.GUEST), Click.on(Store.CONTINUE_ACCOUNT),
                WaitUntil.the(Store.field("firstname"), isVisible()).forNoMoreThan(15).seconds());
        var fields = Map.of("firstname", "firstname", "lastname", "lastname", "email", "email",
                "telephone", "telephone", "address-1", "address", "city", "city", "postcode", "postcode");
        fields.forEach((field, key) -> actor.attemptsTo(Enter.theValue(data.value("buyer." + key)).into(Store.field(field))));
        actor.attemptsTo(SelectFromOptions.byVisibleText(data.value("buyer.country")).from(Store.COUNTRY));
        new WebDriverWait(BrowseTheWeb.as(actor).getDriver(), Duration.ofSeconds(15)).until(driver ->
                new Select(Store.REGION.resolveFor(actor)).getOptions().stream().anyMatch(option -> option.getText().equals(data.value("buyer.region"))));
        actor.attemptsTo(SelectFromOptions.byVisibleText(data.value("buyer.region")).from(Store.REGION));
        if (!Store.SAME_ADDRESS.resolveFor(actor).isSelected()) actor.attemptsTo(Click.on(Store.SAME_ADDRESS));
        actor.attemptsTo(Click.on(Store.CONTINUE_GUEST),
                WaitUntil.the(Store.shipping(data.value("shipping.method")), isClickable()).forNoMoreThan(20).seconds(),
                Click.on(Store.shipping(data.value("shipping.method"))), Click.on(Store.CONTINUE_SHIPPING),
                WaitUntil.the(Store.CONTINUE_PAYMENT, isVisible()).forNoMoreThan(20).seconds());
        assertTrue("BLOQUEADO: no hay pago de demostración contra entrega. No se autoriza un pago real.",
                Store.DEMO_PAYMENT.resolveAllFor(actor).stream().anyMatch(e -> e.isVisible()));
        actor.attemptsTo(Click.on(Store.DEMO_PAYMENT), Click.on(Store.AGREE), Click.on(Store.CONTINUE_PAYMENT),
                WaitUntil.the(Store.CONFIRM, isClickable()).forNoMoreThan(25).seconds());
        assertEquals("Dos productos en la confirmación", data.products().size(), Store.CONFIRM_ROWS.resolveAllFor(actor).size());
        String confirmation = Store.CONTENT.resolveFor(actor).getText();
        data.products().forEach(product -> assertTrue("Producto en la orden: " + product.name(), confirmation.contains(product.name())));
        Serenity.recordReportData().withTitle("Confirmación previa; pago Cash On Delivery").andContents(confirmation);
        actor.attemptsTo(Click.on(Store.CONFIRM),
                WaitUntil.the(Store.SUCCESS_HEADING, net.serenitybdd.screenplay.matchers.WebElementStateMatchers.containsText("Your order has been placed!"))
                        .forNoMoreThan(25).seconds());
    }
}
