package com.sofka.qa.web.tasks;

import com.sofka.qa.web.data.Product;
import com.sofka.qa.web.data.PurchaseData;
import com.sofka.qa.web.ui.Store;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.util.regex.Pattern;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AddProduct implements Task {
    private final Product product;
    private final PurchaseData data;

    public AddProduct(Product product, PurchaseData data) {
        this.product = product;
        this.data = data;
    }

    public static AddProduct toCart(Product product, PurchaseData data) {
        return Tasks.instrumented(AddProduct.class, product, data);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Open.url(data.route("product/product") + "&product_id=" + product.id()),
                WaitUntil.the(Store.PRODUCT_NAME, isVisible()).forNoMoreThan(20).seconds());
        assertEquals("Producto configurado", product.name(), Store.PRODUCT_NAME.resolveFor(actor).getText());
        var availability = Pattern.compile("Availability: ([^\\r\\n]+)").matcher(Store.CONTENT.resolveFor(actor).getText());
        assertTrue("No se pudo identificar el inventario", availability.find());
        String stock = availability.group(1).trim();
        boolean available = stock.equals("In Stock") || (stock.matches("\\d+") && Integer.parseInt(stock) >= product.quantity());
        assertTrue("Producto no disponible: " + product.name() + " / " + stock, available);
        actor.attemptsTo(Enter.theValue(String.valueOf(product.quantity())).into(Store.QUANTITY),
                Click.on(Store.ADD), WaitUntil.the(Store.ADDED, isVisible()).forNoMoreThan(15).seconds());
        assertTrue("El agregado debe mencionar el producto", Store.ADDED.resolveFor(actor).getText().contains(product.name()));
    }
}
