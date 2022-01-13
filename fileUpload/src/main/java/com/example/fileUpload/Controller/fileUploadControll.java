package com.example.fileUpload.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.List;
import java.util.stream.Collectors;

import com.example.fileUpload.model.fileDetails;
import com.example.fileUpload.service.StorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Controller
public class fileUploadControll {
    @Autowired
    StorageService storageService;

    @GetMapping("/")
    public String getform() {
        return "uploadForm";
    }

    @PostMapping(value = "/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {
        String mssg = "";
        try {
            storageService.save(file);
            mssg = "uploaded the file succesfully : " + file.getOriginalFilename();
            model.addAttribute("message", mssg);
            return "uploadForm";
        } catch (Exception e) {
            mssg = "Could not upload the file " + file.getOriginalFilename();

            model.addAttribute("message", mssg);
            return "uploadForm";
        }
    }

    @GetMapping("/files")
    public String getListFiles(Model model) {
        List<fileDetails> fileDetails = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String dir = path.toString();
            String url = MvcUriComponentsBuilder
                    .fromMethodName(fileUploadControll.class, "getFile", path.getFileName().toString()).build()
                    .toString();
            return new fileDetails(filename, url, dir);
        }).collect(Collectors.toList());
        model.addAttribute("files", fileDetails);
        return "uploadform";
    }

    @GetMapping(value = "/pdf/{absolutePath:.+}")
    public ResponseEntity<InputStreamResource> getTermsConditions(@PathVariable String absolutePath)
            throws IOException {
        File file = storageService.load(absolutePath).getFile();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("content-disposition", "inline;name=" + absolutePath);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }

    @GetMapping("/files/{name:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String name) {
        Resource file = storageService.load(name);
        return ResponseEntity.ok().header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}