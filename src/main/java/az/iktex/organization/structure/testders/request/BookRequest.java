package az.iktex.organization.structure.testders.request;

import java.math.BigDecimal;

public record BookRequest(String title, BigDecimal price, Integer pageCount) {
}
