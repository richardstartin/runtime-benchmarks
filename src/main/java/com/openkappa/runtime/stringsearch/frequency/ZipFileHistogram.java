package com.openkappa.runtime.stringsearch.frequency;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipFileHistogram {

    private static final Map<String, String> ZIPS = Map.of(
            "en", "eng-kjv2006_readaloud.zip",
            "de", "deu1912_readaloud.zip",
            "zh", "cmn-ncvt_readaloud.zip",
            "ru", "russyn_readaloud.zip",
            "sh", "srp1865_readaloud.zip"
    );

    public static void main(String... args) throws IOException {
        Path wd = Paths.get(System.getProperty("user.dir"));
        Path results = wd.resolve("results");
        if (!Files.exists(results)) {
            Files.createDirectory(results);
        }
        for (var pair : ZIPS.entrySet()) {
            Path path = results.resolve(pair.getKey());
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            Path input = wd.resolve("src/data/text").resolve(pair.getValue());
            Path output = path.resolve("bible");
            prepareOutput(output);
            new ZipFileHistogram(input, output).compute();
        }
    }

    private final int[] nibbleHistogram = new int[16];
    private final int[] base64Histogram = new int[64];
    private final int[] byteHistogram = new int[256];
    private final int[] pairHistogram = new int[1 << 16];
    private final int[] base64PairHistogram = new int[64 * 64];
    private byte lastByte;
    private int lastBase64;
    private boolean first = true;


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
            StringBuilder sb = new StringBuilder(5);
            for (int i = 0; i < byteHistogram.length; ++i) {
                sb.append(i & 0xFF)
                        .append(',')
                        .append(byteHistogram[i])
                        .append('\n');
                writer.write(sb.toString());
                sb.setLength(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = Files.newBufferedWriter(output.resolve("pairs.csv"))) {
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < pairHistogram.length; ++i) {
                sb.append(i >>> 8)
                        .append(',')
                        .append(i & 0xFF)
                        .append(',')
                        .append(pairHistogram[i])
                        .append('\n');
                writer.write(sb.toString());
                sb.setLength(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Writer writer = Files.newBufferedWriter(output.resolve("base64pairs.csv"))) {
            StringBuilder sb = new StringBuilder(10);
            for (int i = 0; i < base64PairHistogram.length; ++i) {
                sb.append(i >>> 6)
                        .append(',')
                        .append(i & 0x3F)
                        .append(',')
                        .append(base64PairHistogram[i])
                        .append('\n');
                writer.write(sb.toString());
                sb.setLength(0);
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
        int i = 0;
        for (; i + 2 < length; i += 3) {
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
            int p1 = (word >>> 18) & 0x3F;
            int p2 = (word >>> 12) & 0x3F;
            int p3 = (word >>> 6) & 0x3F;
            int p4 = word & 0x3F;
            base64Histogram[p1]++;
            base64Histogram[p2]++;
            base64Histogram[p3]++;
            base64Histogram[p4]++;
            if (!first) {
                base64PairHistogram[(lastBase64 << 6) | p1]++;
                pairHistogram[((lastByte & 0xFF) << 8) | (buffer[i] & 0xFF)]++;
            } else {
                first = false;
            }
            pairHistogram[((buffer[i] & 0xFF) << 8) | (buffer[i+1] & 0xFF)]++;
            pairHistogram[((buffer[i+1] & 0xFF) << 8) | (buffer[i+2] & 0xFF)]++;
            lastByte = buffer[i+2];
            base64PairHistogram[(p1 << 6) | p2]++;
            base64PairHistogram[(p2 << 6) | p3]++;
            base64PairHistogram[(p3 << 6) | p4]++;
            lastBase64 = p4;
        }
        for (; i < length; ++i) {
            nibbleHistogram[buffer[i] & 0xF]++;
            nibbleHistogram[(buffer[i] & 0xF0) >>> 4]++;
            byteHistogram[buffer[i] & 0xFF]++;
            pairHistogram[((lastByte & 0xFF) << 8) | (buffer[i] & 0xFF)]++;
            lastByte = buffer[i];
            // drop some base 64 at the end every now and then because who cares?
        }
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
