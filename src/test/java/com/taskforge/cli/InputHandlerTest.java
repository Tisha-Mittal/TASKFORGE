package com.taskforge.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class InputHandlerTest {

    @Test
    void shouldReturnValidInteger() {

        Scanner scanner = new Scanner("5\n");

        InputHandler handler = new InputHandler(scanner);

        int result = handler.getMenuChoice();

        assertEquals(5, result);

        handler.close();
    }

    @Test
    void shouldReturnMinusOneForInvalidInput() {

        Scanner scanner = new Scanner("hello\n");

        InputHandler handler = new InputHandler(scanner);

        int result = handler.getMenuChoice();

        assertEquals(-1, result);

        handler.close();
    }
}