package az.iktex.organization.structure.testders.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@NullMarked
public class LanguageLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String language = request.getParameter("lang");

        if (language == null) {
            String acceptLanguage = request.getHeader("Accept-Language");
            if (acceptLanguage == null || acceptLanguage.isBlank()) {
                return Locale.forLanguageTag("az");
            }
            language = request.getLocale().getLanguage();
        }

        return switch (language.strip().toLowerCase(Locale.ROOT)) {
            case "az", "aze" -> Locale.forLanguageTag("az");
            case "en", "eng" -> Locale.ENGLISH;
            case "ru", "rus" -> Locale.forLanguageTag("ru");
            default -> Locale.forLanguageTag("az");
        };
    }

    @Override
    public void setLocale(
            HttpServletRequest request,
            @Nullable HttpServletResponse response,
            @Nullable Locale locale
    ) {
        throw new UnsupportedOperationException();
    }
}