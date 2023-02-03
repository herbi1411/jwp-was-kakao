package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Request {
    private final List<String> requestLines;

    private Request(List<String> requestLines) {
        this.requestLines = requestLines;
    }

    public static Request parse(BufferedReader reader) throws IOException {
        List<String> requestLines = new ArrayList<>();
        String line;
        while (!Objects.equals(line = reader.readLine(), "")) {
            requestLines.add(line);
        }
        return new Request(requestLines);
    }

    public String getPath() {
        String[] firstRequestLine = requestLines.get(0).split(" ");
        return firstRequestLine[1];
    }
}
