package org.zerock.mreview.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.mreview.dto.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Log4j2
public class UploadController { // 브라우저는 업로드 처리 후에 JSON의 배열 형태로 결과를 전달받음(console로 확인 가능)

    @Value("${org.zerock.upload.path}") // application.properties의 변수
    private String uploadPath;

    @PostMapping("/uploadAjax") // MultipartFile 배열을 활용하여 동시에 여러 개의 파일 정보 처리해서 화면에서 여러 개의 파일을 동시에 업로드 가능
    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles){
        List<UploadResultDTO> resultDTOList = new ArrayList<>();
        
        for(MultipartFile uploadFile: uploadFiles){
            // 이미지 파일만 업로드 가능 ( MultipartFile에서 제공하는 getContentType()를 이용해서 image 파일인지 체크)
            if(uploadFile.getContentType().startsWith("image") == false){
                log.warn("this file is not image type");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 업로드 결과 반환(실패) => 403 Forbidden을 반환하도록
            }

            // 실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
            String originalName = uploadFile.getOriginalFilename();
            String fileName = originalName.substring(originalName.lastIndexOf("\\")+1);

            log.info("fileName: " + fileName);

            // 날짜 폴더 생성
            String folderPath = makeFolder();

            // UUID
            String uuid = UUID.randomUUID().toString();

            // 저장할 파일, 이름 중간에 "_"를 이용해서 구분
            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;

            Path savePath = Paths.get(saveName);

            try {
                // 원본 파일 저장
                uploadFile.transferTo(savePath); // 실제 이미지 저장
                
                // 썸네일 이름, 썸네일 파일 이름은 중간에 s_ 로 시작하도록
                String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator + "s_" + uuid + "_" + fileName;

                File thumbnailFile = new File(thumbnailSaveName);
                
                // 썸네일 생성, 가로 세로가 100px 사이즈의 썸네일 생성
                Thumbnailator.createThumbnail(savePath.toFile(), thumbnailFile, 100, 100);
                
                resultDTOList.add(new UploadResultDTO(fileName, uuid, folderPath)); // UploadResultDTO에 실제 경로들 변수로 저장
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(resultDTOList, HttpStatus.OK); // 업로드 결과 반환(성공)
    }

    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("//", File.separator);

        // make folder ---------
        File uploadPathFolder = new File(uploadPath, folderPath);

        if(uploadPathFolder.exists()==false){
            uploadPathFolder.mkdirs();
        }

        return folderPath;
    }

    // URL 인코딩된 파일 이름을 파라미터로 받아 해당 파일을 byte[]로 만들어서 브라우저로 전송
    // size 파리미터로 원본파일인지 썸네일인지 구분, size가 1이면 원본 파일 전송
    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName, String size){
        ResponseEntity<byte[]> result = null;

        try {
            String srcFileName = URLDecoder.decode(fileName,"UTF-8");

            log.info("file: " + srcFileName);

            File file = new File(uploadPath + File.separator + srcFileName);

            if(size != null && size.equals("1")){
                file = new File(file.getParent(), file.getName().substring(2));
            }

            log.info("file: " + file);

            HttpHeaders header = new HttpHeaders();

            // MIME 타입 처리 (파일의 확장자에 따라서 브라우저에 전송하는 MIME 타입이 달라져야 하는 문제를 probeContentType()을 이용해서 처리)
            header.add("Content-Type", Files.probeContentType(file.toPath()));

            // 파일 데이터 처리 (FileCopyUtils 이용해 처리)
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return result;
    }

    @PostMapping("/removeFile")
    public ResponseEntity<Boolean> removeFile(String fileName){ // 경로와 UUID가 포함된 파일 이름을 파라미터로 받아 삭제 결과를 Boolean으로 반환
        String srcFileName=null;
        
        try {
            srcFileName = URLDecoder.decode(fileName,"UTF-8");
            File file = new File(uploadPath + File.separator + srcFileName);
            boolean result = file.delete(); // 원본 삭제
            
            File thumbnail = new File(file.getParent(), "s_" + file.getName());
            
            result = thumbnail.delete(); // 썸네일 삭제
            
            return new ResponseEntity<>(result, HttpStatus.OK);
            
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
