package net.project.journalApp.service;

import net.project.journalApp.entity.UserEntry;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class UserArgumentProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        return Stream.of(
                Arguments.of(UserEntry.builder().username("Roop").password("Roop").build()),
                Arguments.of(UserEntry.builder().username("Omkar").password("Omkar").build())
        );
    }
}
