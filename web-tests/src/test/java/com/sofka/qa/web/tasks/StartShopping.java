package com.sofka.qa.web.tasks;

import com.sofka.qa.web.data.PurchaseData;
import com.sofka.qa.web.ui.Store;
import net.serenitybdd.core.Serenity;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.net.URI;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StartShopping implements Task {
    private final PurchaseData data;

    public StartShopping(PurchaseData data) { this.data = data; }

    public static StartShopping with(PurchaseData data) {
        return Tasks.instrumented(StartShopping.class, data);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.url(data.value("web.base.url")),
                WaitUntil.the(Store.CART_TOTAL, isVisible()).forNoMoreThan(20).seconds());
        String effectiveUrl = BrowseTheWeb.as(actor).getDriver().getCurrentUrl();
        Serenity.recordReportData().withTitle("URL efectiva").andContents(effectiveUrl);
        assertEquals("Host esperado", URI.create(data.value("web.base.url")).getHost(), URI.create(effectiveUrl).getHost());
        assertTrue("Se esperaba HTTP o HTTPS", effectiveUrl.matches("https?://.*"));
        assertTrue("La sesión debe comenzar con carrito vacío", Store.CART_TOTAL.resolveFor(actor).getText().startsWith("0 item(s)"));
    }
}
