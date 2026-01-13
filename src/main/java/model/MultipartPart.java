package model;

public record MultipartPart (String name, String fileName, String contentType, byte[] data) {
    public boolean isFile() {
        return fileName != null;
    }
}
