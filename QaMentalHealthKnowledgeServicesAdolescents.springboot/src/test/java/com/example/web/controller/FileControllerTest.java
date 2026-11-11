package com.example.web.controller;

import com.example.web.exception.CustomException;
import com.example.web.utils.LocalDocumentFiles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.*;

class FileControllerTest {
    private MockMultipartFile file(String name) { return new MockMultipartFile("file",name,"text/plain","sample document".getBytes()); }

    @Test void uploadedChineseFileCanBeResolvedFromProductionUrl() throws Exception {
        FileController controller=new FileController();ReflectionTestUtils.setField(controller,"serverIp","/api");
        var result=controller.batchUpload(new MultipartFile[]{file("知识 库.txt")},null).get(0);
        Path saved=LocalDocumentFiles.resolve(result.getUrl(),"/api");
        try {
            assertTrue(result.getUrl().startsWith("/api/"));assertFalse(result.getUrl().contains(" "));
            assertEquals("sample document",Files.readString(saved));
        } finally {Files.deleteIfExists(saved);Files.deleteIfExists(saved.getParent());}
    }

    @Test void rejectsTraversalAndExecutableContentBeforeWriting() {
        FileController controller=new FileController();
        for(String name:List.of("../secret.txt","..\\secret.txt","page.html","image.svg","script.js","script.bat","a\n.txt"))
            assertThrows(CustomException.class,()->controller.batchUpload(new MultipartFile[]{file(name)},null));
    }

    @Test void rejectsEmptyAndOversizedBatches() {
        FileController controller=new FileController();
        assertThrows(CustomException.class,()->controller.batchUpload(new MultipartFile[]{},null));
        MultipartFile[] files=new MultipartFile[11];java.util.Arrays.fill(files,file("file.txt"));
        assertThrows(CustomException.class,()->controller.batchUpload(files,null));
        assertThrows(CustomException.class,()->controller.batchUpload(new MultipartFile[]{new MockMultipartFile("file",new byte[0])},null));
    }
}
