package com.example.web.service;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DocumentSourcesTest {
    @Test void readsOnlyExplicitHttpsReferencesAndStopsAtTheNextSection() {
        String input="- [正文链接](https://ignored.test)\n## 参考来源\n"
                +"- [机构原文](https://example.test/original)\n"
                +"- [重复](https://example.test/original)\n"
                +"- [脚本](javascript:alert(1))\n- [非加密](http://example.test)\n"
                +"- [带凭据](https://user:password@example.test/private)\n"
                +"## 附录\n- [附录链接](https://appendix.test)";
        var actual=DocumentSources.parse(input);
        assertEquals(1,actual.size());assertEquals("机构原文",actual.get(0).title());
        assertEquals("https://example.test/original",actual.get(0).url());
    }
    @Test void neverInventsAReferenceForUnannotatedOrMissingDocuments() {
        assertTrue(DocumentSources.parse("普通演示资料，没有来源记录").isEmpty());
        assertTrue(new DocumentSources().read("https://127.0.0.1:1/missing.md").isEmpty());
    }
    @Test void shippedDocumentFourIncludesBothCheckedSourcesAndItsNature() throws Exception {
        String markdown=Files.readString(Path.of("external-resources/1000000040/心理健康自我保健知识.md"));
        assertTrue(markdown.contains("不是机构原文"));assertTrue(markdown.contains("未经临床专家审定"));
        var references=DocumentSources.parse(markdown);
        assertEquals(2,references.size());
        assertEquals("www.who.int",java.net.URI.create(references.get(0).url()).getHost());
        assertEquals("mhl.psych.ac.cn",java.net.URI.create(references.get(1).url()).getHost());
    }
}
