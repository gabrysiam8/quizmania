package com.springboot.rest.quizmania.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.core.io.ClassPathResource;

public class TestUtils {

    public static String readFile(String filename) throws IOException {
        return Files.readString(Paths.get(new ClassPathResource(filename).getURI()));
    }
}
