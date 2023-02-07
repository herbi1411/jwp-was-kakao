package webserver.response;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import webserver.FileType;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Response {
    private final StatusCode statusCode;
    private final Map<String, String> responseHeader;
    private final byte[] body;

    public static Response ok(byte[] body, FileType fileType) {
        Map<String, String> responseHeader = generateResponseHeader(body, fileType);
        return new Response(
                StatusCode.OK,
                responseHeader,
                body
        );
    }

    public static Response found(byte[] body, FileType fileType, String location) {
        Map<String, String> responseHeader = generateResponseHeader(body, fileType);
        responseHeader.put("Location", location);
        return new Response(
                StatusCode.FOUND,
                responseHeader,
                body
        );
    }

    private static Map<String, String> generateResponseHeader(byte[] body, FileType fileType) {
        Map<String, String> responseHeader = new LinkedHashMap<>();
        responseHeader.put("Content-Type", fileType.getContentType() + ";charset=utf-8");
        if (body.length > 0) {
            responseHeader.put("Content-Length", String.valueOf(body.length));
        }
        return responseHeader;
    }

    public void flush(DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 " + statusCode.getCode() + " " + statusCode.getMessage() + " \r\n");
        for (Map.Entry<String, String> entry : responseHeader.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + " \r\n");
        }
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
