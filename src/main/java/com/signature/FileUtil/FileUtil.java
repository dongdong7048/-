package com.signature.FileUtil;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.util.Objects;

public class FileUtil {
    /**
     * MultipartFile型別轉File型別，檔名同被轉換檔名稱一致，如有需要可以拓展方法。
     */
    public static File MultipartFileToFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }
        // 獲取InoutString
        InputStream inputStream = multipartFile.getInputStream();
        // 建立檔案
        File toFile = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        // 寫入檔案
        OutputStream outputStream = new FileOutputStream(toFile);
        byte[] buffer = new byte[8192];
        int bytesRead = 0;
        while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.close();
        inputStream.close();
        return toFile;
    }



    /**
     * 將File檔案轉化為Base64位元組碼
     */
    public static String encodeBase64File(File file) throws IOException {
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        int read = inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

    /**
     * 將base64字元儲存文字檔案
     */
    public static void decoderBase64File(String base64Code, String targetPath) throws IOException {
        byte[] buffer = new BASE64Decoder().decodeBuffer(base64Code);
        FileOutputStream out = new FileOutputStream(targetPath);
        out.write(buffer);
        out.close();
    }

    /**
     * 下載檔案至瀏覽器預設位置
     */
    public static ResponseEntity<InputStreamResource> downLoadFile(InputStream in, String fileName) throws IOException {
        byte[] testBytes = new byte[in.available()];
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(testBytes.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(in));
    }
}
