package com.pyshinskiy.videostreaming.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class HLSService {
    private static final Logger log = LoggerFactory.getLogger(HLSService.class);

    public void convertToHLS(String inputVideoPath, String outputFolderPath) throws IOException, InterruptedException {
        File outputFile = new File(outputFolderPath + "/1.m3u8");

        log.info("render started");
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg",
                "-i", inputVideoPath,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-hls_time", "30",
                "-hls_list_size", "4",
                "-hls_segment_filename", outputFolderPath + "\\segment_%03d.ts",
                outputFolderPath + "\\1.m3u8"
        );
        processBuilder.inheritIO();
//TODO не пишет в hls папку
        try {
            // Запускаем команду FFmpeg
            Process process = processBuilder.start();
            // Ждем, пока процесс завершится
            process.waitFor();
            // Выводим сообщение о завершении
            System.out.println("Конвертация завершена.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

}
}
