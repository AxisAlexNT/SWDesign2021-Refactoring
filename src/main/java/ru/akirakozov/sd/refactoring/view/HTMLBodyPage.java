package ru.akirakozov.sd.refactoring.view;

import org.jetbrains.annotations.NotNull;

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

    public abstract @NotNull String getBodyHTMLCode();
}
