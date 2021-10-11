package ru.akirakozov.sd.refactoring.view;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a page that we can take an HTML code of.
 */
public interface ResponsePage {
    /**
     * Takes an HTML code of this page.
     * @return An HTML code of this page.
     */
    @NotNull String getHTMLCode();
}
