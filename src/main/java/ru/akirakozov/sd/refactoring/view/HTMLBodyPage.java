package ru.akirakozov.sd.refactoring.view;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a page from which we are only interested in the "body" contents.
 */
public abstract class HTMLBodyPage implements ResponsePage {
    private static final String htmlHeader = "<html><body>";
    private static final String htmlFooter = "</body></html>";

    @Override
    public @NotNull String getHTMLCode() {
        return String.format(
                "%s%n%s%n%s",
                htmlHeader,
                getBodyHTMLCode(),
                htmlFooter
        );
    }

    /**
     * Takes HTML code of this page that would be placed into the body of this page.
     * @return An HTML code of this page that would be placed into the body of this page.
     */
    public abstract @NotNull String getBodyHTMLCode();
}
