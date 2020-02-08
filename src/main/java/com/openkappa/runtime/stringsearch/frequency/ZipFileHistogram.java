package com.openkappa.runtime.stringsearch.frequency;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileHistogram {

    public static void main(String... args) throws IOException {
        Path wd = Paths.get(System.getProperty("user.dir"));
        Path results = wd.resolve("results");
        if (!Files.exists(results)) {
            Files.createDirectory(results);
        }
        Path en = results.resolve("en");
        Path de = results.resolve("de");
        if (!Files.exists(en)) {
            Files.createDirectory(en);
        }
        if (!Files.exists(de)) {
            Files.createDirectory(de);
        }
        Path enInput = wd.resolve("src/data/text/eng-kjv2006_readaloud.zip");
        Path enOutput = en.resolve("bible");
        prepareOutput(enOutput);
        new ZipFileHistogram(enInput, enOutput).compute();
        Path deInput = wd.resolve("src/data/text/deu1912_readaloud.zip");
        Path deOutput = de.resolve("bible");
        prepareOutput(deOutput);
        new ZipFileHistogram(deInput, deOutput).compute();
    }

    private final int[] nibbleHistogram = new int[16];
    private final int[] base64Histogram = new int[64];
    private final int[] byteHistogram = new int[256];


    private static final char[] BASE64 = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    private static final char[] BASE16 = {
           '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    private final Path zipFile;
    private final Path output;

    public ZipFileHistogram(Path zipFile, Path output) {
        this.zipFile = zipFile;
        this.output = output;
    }


    public void compute() {
        byte[] buffer = new byte[1024];
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile.toFile()))) {
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.getName().endsWith(".txt")) {
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                        processBytes(buffer, length);
                    }
                }
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = Files.newBufferedWriter(output.resolve("base256.csv"))) {
            for (int i = 0; i < byteHistogram.length; ++i) {
                if (i == ',') {
                    writer.write( "comma," + byteHistogram[i]+ "\n");
                } if (i >= 0x20 && i <= 0x7e || i > 162) {
                    writer.write((char) i + "," + byteHistogram[i]+ "\n");
                }  else {
                    writer.write(String.format("\\0x%02x", i) + "," + byteHistogram[i]+ "\n");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = Files.newBufferedWriter(output.resolve("base64.csv"))) {
            for (int i = 0; i < base64Histogram.length; ++i) {
                writer.write(BASE64[i] + "," + base64Histogram[i]+ "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = Files.newBufferedWriter(output.resolve("base16.csv"))) {
            for (int i = 0; i < nibbleHistogram.length; ++i) {
                writer.write(BASE16[i] + "," + nibbleHistogram[i] + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processBytes(byte[] buffer, int length) {
        for (int i = 0; i + 2 < length; i += 3) {
            nibbleHistogram[buffer[i] & 0xF]++;
            nibbleHistogram[(buffer[i] & 0xF0) >>> 4]++;
            nibbleHistogram[buffer[i + 1] & 0xF]++;
            nibbleHistogram[(buffer[i + 1] & 0xF0) >>> 4]++;
            nibbleHistogram[buffer[i + 2] & 0xF]++;
            nibbleHistogram[(buffer[i + 2] & 0xF0) >>> 4]++;
            byteHistogram[buffer[i] & 0xFF]++;
            byteHistogram[buffer[i + 1] & 0xFF]++;
            byteHistogram[buffer[i + 2] & 0xFF]++;
            int word = (buffer[i] & 0xFF) << 16 | (buffer[i + 1] & 0xFF) << 8 | (buffer[i + 2] & 0xFF);
            base64Histogram[(word >>> 18) & 0x3F]++;
            base64Histogram[(word >>> 12) & 0x3F]++;
            base64Histogram[(word >>> 6) & 0x3F]++;
            base64Histogram[word & 0x3F]++;
        }
        // drop some at the end every now and then because who cares?
    }


    private static void prepareOutput(Path dir) {
        if (Files.exists(dir) && !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("not a dir");
        }
        try {
            if (!Files.exists(dir)) {
                Files.createDirectory(dir);
            } else {
                try (Stream<Path> files = Files.list(dir)) {
                    files.forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
