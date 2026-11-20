package com.sofka.qa.web.data;

public record Product(String id, String name, int quantity) {
    public Product {
        if (!id.matches("\\d+") || name.isBlank() || quantity < 1) {
            throw new IllegalArgumentException("Producto inválido: " + id);
        }
    }
}
