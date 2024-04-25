package com.pyshinskiy.videostreaming.controller;

import com.pyshinskiy.videostreaming.service.HLSService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/hls")
public class HLSVideoController {

    private final HLSService hlsService;
    private final ResourceLoader resourceLoader;

    public HLSVideoController(HLSService hlsService, ResourceLoader resourceLoader) {
        this.hlsService = hlsService;
        this.resourceLoader = resourceLoader;
    }

    @GetMapping("/streaming/{uuid}")
    @ResponseBody
    public ResponseEntity<MultiValueMap<String, Object>> streamVideo(@PathVariable UUID uuid) throws IOException, InterruptedException {
        String inputVideoPath = resourceLoader.getResource("classpath:videos/"+ uuid + "/1.mp4").getFile().getAbsolutePath();
        String outputFolderPath = resourceLoader.getResource("classpath:videos/hls").getFile().getAbsolutePath();
        hlsService.convertToHLS(inputVideoPath, outputFolderPath);

        File hlsFolder = new File(outputFolderPath);
        File hlsFile = new File(hlsFolder, "1.m3u8");

        // Проверяем, что файл плейлиста существует
        if (!hlsFile.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Создаем MultiValueMap для хранения всех ресурсов
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // Добавляем плейлист HLS в MultiValueMap
        body.add("playlist", new FileSystemResource(hlsFile));

        // Получаем список всех файлов в папке сегментов видео
        File[] segmentFiles = hlsFolder.listFiles((dir, name) -> name.matches("segment_\\d+.ts"));

        // Добавляем каждый сегмент видео в MultiValueMap
        for (File segmentFile : segmentFiles) {
            body.add("segments", new FileSystemResource(segmentFile));
        }

        // Возвращаем MultiValueMap в качестве тела ответа
        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_MIXED)
                .body(body);
    }
}
