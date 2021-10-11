package ru.akirakozov.sd.refactoring.view;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public class SimpleTextPage implements ResponsePage {
    private final String pageText;

    @Override
    public @NotNull String getHTMLCode() {
        return pageText;
    }
}
