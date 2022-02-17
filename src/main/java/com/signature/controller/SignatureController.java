package com.signature.controller;


import com.signature.entity.ActionName;

import com.signature.service.HistoryService;
import com.signature.service.SignatureService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;

import java.io.*;
import java.net.URLEncoder;


@Slf4j // lombok套件，引入Slf4j，直接可使用log功能
@Controller
public class SignatureController {

    //本機桌面路徑
    public static final String DESKTOP_PATH = FileSystemView.getFileSystemView().getHomeDirectory().toString();

    //本機下載資料夾路徑
    public static final String LOCAL_DOWNLOAD_DIRECTORY_PATH = System.getProperty("user.home") + "\\Downloads";

    //當前工作目錄
    public static final String PROJECT_PATH = System.getProperty("user.dir");


    @Autowired
    HistoryService historyService;

    @Autowired
    SignatureService signatureService;

    @Autowired
    ActionName actionName;


    /**
     * 簽章系統首頁
     * 2022年2月16日
     *
     * @return String
     */
    @RequestMapping(value = {"/index", "/"})
    public String index() {
        return "mainPage";
    }


    /***
     *
     * 開啟SignaturePad簽章畫面
     * 2020年12月28日
     *
     */
    @RequestMapping("/openSignaturePad")
    public String openSignaturePad() {
        return "signature_pad";
    }

    @RequestMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //檔案的儲存名稱
        String filename = request.getParameter("filename");
        filename = new String(filename.getBytes("iso8859-1"), "utf-8");
        File file = new File(PROJECT_PATH, filename);
        if (!file.exists()) {
            System.out.println("下載資源不存在");
        } else {
            String mimeType = request.getServletContext().getMimeType(filename);
            response.setContentType(mimeType);
            filename = URLEncoder.encode(filename, "utf-8");
            response.setHeader("content-disposition", "attachment;filename=" + filename);
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            int len = -1;
            byte[] b = new byte[1024];
            while ((len = fis.read(b)) != -1) {
                os.write(b, 0, len);
                os.flush();
            }
            os.close();
            fis.close();
        }
    }

    /***
     * 將簽章儲存成png檔，依照商業邏輯合併pdf檔並記錄資訊於db
     * 2020年12月28日
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/save")
    @ResponseBody
    public String save(@RequestParam("signature.png") MultipartFile multipartFile) throws Exception {
        //簽名檔先儲存在當前工作目錄下
        File file = new File(PROJECT_PATH, multipartFile.getName());
        multipartFile.transferTo(file);
        return multipartFile.getName();
    }


    /**
     * 亂數取得6位整數
     * 2020年12月28日
     *
     * @return int
     */
    private int get7RamdomInteger() {
        return (int) ((Math.random() * 9 + 1) * 1000000);//(int)(Math.random()*8998)+1000+1;
    }


    /***
     * 將當前工作目錄下產出的暫存簽名png檔全部刪除
     * 2021年2月16日
     *
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteTempImages")
    public void deleteTempImage() throws Exception {
        File file = new File(PROJECT_PATH + "\\signature.png");
        if (file.exists()) {
            file.delete();
        }
    }

}
