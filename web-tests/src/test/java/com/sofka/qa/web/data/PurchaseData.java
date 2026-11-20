package com.sofka.qa.web.data;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public final class PurchaseData {
    private final Properties values;

    private PurchaseData(Properties values) {
        this.values = values;
    }

    public static PurchaseData load() {
        String environment = System.getProperty("environment", "demo");
        if (!environment.matches("[a-zA-Z0-9_-]+")) {
            throw new IllegalArgumentException("Nombre de ambiente inválido");
        }
        var values = new Properties();
        try (var input = PurchaseData.class.getResourceAsStream("/data/" + environment + ".properties")) {
            if (input == null) throw new IllegalArgumentException("Ambiente no definido: " + environment);
            values.load(input);
        } catch (IOException error) {
            throw new UncheckedIOException(error);
        }
        var data = new PurchaseData(values);
        if (data.products().stream().map(Product::id).distinct().count() != 2) {
            throw new IllegalArgumentException("Se requieren dos productos distintos");
        }
        return data;
    }

    public String value(String key) {
        String value = System.getProperty(key, values.getProperty(key));
        if (value == null || value.isBlank()) throw new IllegalArgumentException("Falta configuración: " + key);
        return value;
    }

    public List<Product> products() {
        return List.of(product("first"), product("second"));
    }

    private Product product(String position) {
        String prefix = "product." + position + ".";
        return new Product(value(prefix + "id"), value(prefix + "name"), Integer.parseInt(value(prefix + "quantity")));
    }

    public Map<String, Integer> expectedCart() {
        return products().stream().collect(Collectors.toMap(Product::name, Product::quantity));
    }

    public String route(String route) {
        return URI.create(value("web.base.url")).resolve("index.php?route=" + route).toString();
    }
}
