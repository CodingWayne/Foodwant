package com.foodwant.foodwant.controller;

import com.foodwant.foodwant.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上傳與下載
 * @author JIA-YANG, LAI
 * @create 2022-10-09 下午 12:09
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${foodwant.path}")//使用EL表達式注入yml中的list
    private String basePath;

    /**
     * 文件上傳
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //因瀏覽器文件名為file需命名file來封裝，否則使用@RrquestPart("file") xxx來命名
        //file是一個臨時文件，存在C:\Users\wayne\AppData\Local\Temp\tomcat.....中
        //需要轉存到指定位置，否則本次請求結束後會刪除臨時文件
        log.info("上傳: {}",file.getOriginalFilename());

        //原始文件名稱
        String originalFilename = file.getOriginalFilename();//123.png
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));//.png

        //為了防止使用者上傳同名的圖片，使用UUID作為文件名
        String fileName = UUID.randomUUID().toString() + suffix;

        //確認basePath之目錄是否存在，無的話建立目錄
        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();//如父dir沒有的話也會建立
        }

        try {
            //將臨時文件喘存到指定位置
            //指定位置於Springboot配置文件中定義以便維護
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return R.success(fileName);//返回文件名給瀏覽器，以便傳送到server存到DB
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        FileInputStream fileInputStream = null;
        ServletOutputStream outputStream = null;
        try {
            //1.輸入流讀取文件內容
            fileInputStream = new FileInputStream(new File(basePath + name));

            //2.輸出流-將文件寫回瀏覽器
            outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while( (len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {

            try {
                fileInputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                outputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
