package com.video.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 汤垚平
 * @version 1.0
 */
public class FFMpegTest {

    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        super();
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String inputPath, String outputPath) throws IOException {

        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(inputPath);
        command.add(outputPath);

        for (String s :command) {
            System.out.println(s);
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process start = processBuilder.start();

//        在进行操作时会产生许多临时文件，有可能卡住进程，因此需要将其以流的形式释放
        InputStream errorStream = start.getErrorStream();
        InputStreamReader isr = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(isr);

        String line = "";
        while((line = br.readLine()) != null) {
        }
        if(br != null) br.close();
        if(isr != null) isr.close();
        if(errorStream != null) isr.close();

    }

    public static void main(String[] args) {

        FFMpegTest ffMpeg = new FFMpegTest("E:\\后端文件\\ffmpeg-release-essentials\\" +
                "ffmpeg-4.3.1-2021-01-01-essentials_build\\bin\\ffmpeg.exe");
        try {
            ffMpeg.convertor("input.mp4","output.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
