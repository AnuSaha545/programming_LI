package com.anu.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SourceReader {

    public static String read(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }
}