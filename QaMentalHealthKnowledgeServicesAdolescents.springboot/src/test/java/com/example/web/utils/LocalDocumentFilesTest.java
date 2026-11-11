package com.example.web.utils;

import java.io.IOException;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class LocalDocumentFilesTest {
    @TempDir Path temporary;

    @Test void resolvesOldAndProxiedReferencesToTheSameLocalFile() throws Exception {
        Path base=Files.createDirectory(temporary.resolve("uploads"));
        Path dir=Files.createDirectory(base.resolve("folder"));
        Path file=Files.writeString(dir.resolve("知识 库.txt"),"source text");
        assertEquals(file,LocalDocumentFiles.resolve(base,"http://localhost:7406/folder/知识 库.txt","/api"));
        assertEquals(file,LocalDocumentFiles.resolve(base,"/api/folder/%E7%9F%A5%E8%AF%86%20%E5%BA%93.txt","/api"));
        assertEquals(file,LocalDocumentFiles.resolve(base,"https://example.test/api/folder/%E7%9F%A5%E8%AF%86%20%E5%BA%93.txt","https://example.test/api"));
        assertEquals(file,LocalDocumentFiles.resolve(base,file.toString(),"/api"));
    }

    @Test void rejectsTraversalAndOutsideFiles() throws Exception {
        Path base=Files.createDirectory(temporary.resolve("uploads"));
        Path secret=Files.writeString(temporary.resolve("secret.txt"),"private");
        for(String reference:new String[]{"../secret.txt","/api/../secret.txt",secret.toString(),"https://example.test/api/%2e%2e/secret.txt"})
            assertThrows(IOException.class,()->LocalDocumentFiles.resolve(base,reference,"/api"));
    }

    @Test void missingFileIsNotFetchedFromTheNetwork() {
        assertThrows(IOException.class,()->LocalDocumentFiles.resolve(temporary,"https://127.0.0.1:1/private.txt","/api"));
    }
}
