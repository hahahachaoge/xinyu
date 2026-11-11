package com.example.web.service;

import com.example.web.utils.LocalDocumentFiles;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Read explicitly declared references from local Markdown; never infer an origin from related pages. */
@Service
public class DocumentSources {
    @Value("${server.ip:http://localhost:7406}") private String publicBase;
    private static final Pattern HEADING = Pattern.compile("(?m)^##[ \\t]+参考来源[ \\t]*\\r?$");
    private static final Pattern LINK = Pattern.compile("(?m)^-[ \\t]+\\[([^\\]\\r\\n]{1,200})\\]\\((https://[^\\s)]+)\\)[ \\t]*\\r?$");
    public record Reference(String title, String url) { }

    public List<Reference> read(String fileUrl) {
        try {
            var file=LocalDocumentFiles.resolve(fileUrl,publicBase);
            String name=file.getFileName().toString().toLowerCase(Locale.ROOT);
            if ((!name.endsWith(".md") && !name.endsWith(".txt")) || Files.size(file)>128*1024) return List.of();
            return parse(Files.readString(file,StandardCharsets.UTF_8));
        } catch(Exception unavailable) {return List.of();}
    }

    public static List<Reference> parse(String markdown) {
        if(markdown==null) return List.of();
        var heading=HEADING.matcher(markdown);
        if(!heading.find()) return List.of();
        String section=markdown.substring(heading.end());
        var next=Pattern.compile("(?m)^#{1,2}[ \\t]+").matcher(section);
        if(next.find()) section=section.substring(0,next.start());
        var links=LINK.matcher(section);Map<String,Reference> references=new LinkedHashMap<>();
        while(links.find() && references.size()<10) {
            try {
                URI uri=URI.create(links.group(2));
                if("https".equals(uri.getScheme()) && uri.getHost()!=null && uri.getUserInfo()==null)
                    references.putIfAbsent(uri.toString(),new Reference(links.group(1),uri.toString()));
            } catch(IllegalArgumentException ignored) { }
        }
        return List.copyOf(references.values());
    }
}
