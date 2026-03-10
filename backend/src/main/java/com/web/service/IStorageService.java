/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service;

import com.web.dto.StorageFileDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ZZ
 */
public interface IStorageService {
    StorageFileDTO save(MultipartFile file, String folder);
    void delete(String key);
    String getPublicUrl(String key);
    
}
