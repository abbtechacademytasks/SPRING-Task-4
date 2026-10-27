package az.iktex.organization.structure.testders.response;

import java.math.BigDecimal;
import java.util.List;

public record BookResponse(Long id, String title, BigDecimal price, Integer pageCount, List<AuthorResponse> authors) {
}
