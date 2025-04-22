package com.autoblog.autoblog.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class ImageTransformer {

    private static final int BORDER_SIZE = 50; // 테두리 크기

    /**
     * 이미지를 변환하고 랜덤 색상의 테두리를 추가합니다.
     *
     * @param originalImage 원본 이미지 (BufferedImage)
     * @param outputDir     변환된 이미지 저장 디렉토리
     * @return 변환된 이미지 파일 경로
     * @throws IOException 파일 읽기/쓰기 오류
     */
    public String transformImage(BufferedImage originalImage, File outputDir) throws IOException {
        // 새 이미지 크기 계산 (테두리 포함)
        int newWidth = originalImage.getWidth() + BORDER_SIZE * 2;
        int newHeight = originalImage.getHeight() + BORDER_SIZE * 2;

        // 새 이미지 생성
        BufferedImage transformedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        // 랜덤 색상 생성
        Color borderColor = getRandomColor();

        // 그래픽 객체 생성
        Graphics2D g2d = transformedImage.createGraphics();

        // 배경(테두리) 색상 채우기
        g2d.setColor(borderColor);
        g2d.fillRect(0, 0, newWidth, newHeight);

        // 원본 이미지 그리기
        g2d.drawImage(originalImage, BORDER_SIZE, BORDER_SIZE, null);

        // 그래픽 리소스 해제
        g2d.dispose();

        // 출력 파일 이름 생성 (UUID 사용)
        String uniqueFileName = UUID.randomUUID().toString() + ".jpg";
        File outputFile = new File(outputDir, uniqueFileName);

        // 변환된 이미지 저장 (메타데이터 제거)
        ImageIO.write(transformedImage, "jpg", outputFile);

        return outputFile.getAbsolutePath();
    }

    /**
     * 여러 이미지를 변환하고 저장합니다.
     *
     * @param inputDir  원본 이미지 디렉토리
     * @param outputDir 변환된 이미지 저장 디렉토리
     * @throws IOException 파일 읽기/쓰기 오류
     */
    public void transformImagesInBatch(File inputDir, File outputDir) throws IOException {
        if (!inputDir.isDirectory()) {
            throw new IllegalArgumentException("Input path is not a directory: " + inputDir.getAbsolutePath());
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File[] imageFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(jpg|jpeg|png|bmp|gif)$"));
        if (imageFiles == null || imageFiles.length == 0) {
            System.out.println("No image files found in directory: " + inputDir.getAbsolutePath());
            return;
        }

        for (File imageFile : imageFiles) {
            BufferedImage originalImage = ImageIO.read(imageFile);
            String savedPath = transformImage(originalImage, outputDir);
            System.out.println("Transformed and saved: " + savedPath);
        }
    }

    /**
     * 랜덤 색상을 생성합니다.
     *
     * @return 랜덤 색상
     */
    private Color getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return new Color(red, green, blue);
    }
}