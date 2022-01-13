package com.example.fileUpload;

import javax.annotation.Resource;

import com.example.fileUpload.service.StorageService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class FileUploadApplication implements CommandLineRunner {
	@Resource
	StorageService storageService;

	public static void main(String[] args) {
		SpringApplication.run(FileUploadApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// when run delete all files
		// storageService.deleteAll();
		// storageService.init();
	}

}
