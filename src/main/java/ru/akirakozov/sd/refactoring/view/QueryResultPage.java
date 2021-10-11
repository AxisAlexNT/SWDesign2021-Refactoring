package ru.akirakozov.sd.refactoring.view;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public class QueryResultPage extends HTMLBodyPage {
    private final @NotNull String queryHeader;
    private final @NotNull String queryResult;


    @Override
    public @NotNull String getBodyHTMLCode() {
        return String.format("%s%n%s", queryHeader, queryResult);
    }
}
