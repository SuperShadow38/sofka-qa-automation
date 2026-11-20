package com.sofka.qa.web.questions;

import com.sofka.qa.web.data.Product;
import com.sofka.qa.web.ui.Store;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.waits.WaitUntil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CartContents implements Question<Map<String, Integer>> {
    private final List<Product> products;

    private CartContents(List<Product> products) { this.products = List.copyOf(products); }

    public static CartContents forProducts(List<Product> products) { return new CartContents(products); }

    @Override
    public Map<String, Integer> answeredBy(Actor actor) {
        actor.attemptsTo(WaitUntil.the(Store.CART_ROWS, isVisible()).forNoMoreThan(15).seconds());
        assertFalse("El carrito no debe presentar errores de disponibilidad", Store.CART_ERROR.resolveAllFor(actor).stream().anyMatch(e -> e.isVisible()));
        assertEquals("Exactamente dos productos en el carrito", products.size(), Store.CART_ROWS.resolveAllFor(actor).size());
        Map<String, Integer> result = new LinkedHashMap<>();
        for (var product : products) {
            var row = Store.cartRow(product.id()).resolveFor(actor);
            assertFalse("Producto sin stock en el carrito: " + product.name(), row.getText().contains("***"));
            String name = row.findElement(Store.CART_PRODUCT_LINK).getText();
            int quantity = Integer.parseInt(row.findElement(Store.CART_QUANTITY).getAttribute("value"));
            result.put(name, quantity);
        }
        return result;
    }
}
