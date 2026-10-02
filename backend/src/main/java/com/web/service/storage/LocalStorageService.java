/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.web.service.storage;

import com.web.dto.StorageFileDTO;
import com.web.dto.StorageResourceDTO;
import com.web.exception.MyException;
import com.web.service.IStorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ZZ
 */
@Slf4j
@Service
@ConditionalOnProperty(
        name = "app.storage.image.type",
        havingValue = "local",
        matchIfMissing  = true
)
public class LocalStorageService implements IStorageService {

    private final Path root;
    private final String urlPrefix;

    public LocalStorageService(@Value("${app.upload-dir}") String uploadDir,
                               @Value("${app.upload-url-prefix:/api/uploads}") String urlPrefix){
        this.root = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        this.urlPrefix = removeTrailingSlash(urlPrefix);

    }

    private static final Set<String> allowed = Set.of("image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");


    @Override
    public StorageFileDTO save(MultipartFile file,String folder){

        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("File không được rỗng");
        }
        String safeFolder = normalizedKey(folder);
        try{
            Path directory = root.resolve(safeFolder)
                    .normalize();
            validatePath(directory);
            Files.createDirectories(directory);

            String filename = generateFilename(file);

            Path target = directory.resolve(filename)
                    .normalize();

            validatePath(target);

            try(var inputStream = file.getInputStream()){
                Files.copy(inputStream,target,StandardCopyOption.REPLACE_EXISTING);
            }

            String key = safeFolder + "/"+ filename;
            return new StorageFileDTO(key,getPublicUrl(key),Files.size(target),resolveContentType(target));

        }catch(IOException e){
            throw  new RuntimeException("Không thể lưu file Local : ",e);
        }
    }

    @Override
    public Optional<StorageResourceDTO> load(String key){
        String normalizedKey = normalizedKey(key);
        if(normalizedKey == null){
            return Optional.empty();
        }
        try{
            Path file = root.resolve(normalizedKey)
                    .normalize();
            validatePath(file);
            if(!Files.isRegularFile(file)){
                return Optional.empty();
            }

            Resource resource = new FileSystemResource(file);
            String contentType = Files.probeContentType(file);
            MediaType mediaType = parseMediaType(contentType);

            return Optional.of(new StorageResourceDTO(resource,
                    mediaType,Files.size(file)));


        }catch (IOException e){
            log.warn(
                    "Không thể đọc local file key={}",
                    normalizedKey,
                    e
            );

            return Optional.empty();
        }

    }

    @Override
    public void delete(String key) {
        String normalizedKey = normalizedKey(key);

        if(normalizedKey == null){
            return;
        }
        try{
            Path file = root.resolve(normalizedKey)
                    .normalize();
            validatePath(file);
            Files.deleteIfExists(file);
        }catch (IOException e){
            throw  new RuntimeException("Không thể xoá file : "+normalizedKey,e);
        }


    }

    @Override
    public String getPublicUrl(String key) {
        String normalizedKey = normalizedKey(key);

        if(normalizedKey == null){
            return null;
        }
        return urlPrefix + "/" + normalizedKey;
    }

    private String generateFilename(MultipartFile file){
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String uuid = UUID.randomUUID().toString();
        if(extension == null || extension.isBlank()){
            return uuid;
        }
        return uuid + "." + extension.toLowerCase(Locale.ROOT);
    }
    private String normalizedKey(String value){
        if(value == null || value.isBlank()){
            return null;
        }
        String key = value.trim().replace("\\","/");

        if(key.startsWith("/api/files/")){
            key = key.substring("/api/files/".length());
        }
        if(key.startsWith("/uploads/")){
            key = key.substring("/uploads".length());
        }
//        if(key.startsWith("products/")){
//            key = "product/" + key.substring("products/".length());
//        }

        key = key.replaceAll("^/+","");

        if(key.isBlank() || key.contains("..")){
            return null;
        }
        return key;


    }
    private void validatePath(Path path){
        if(!path.startsWith(root)){
            throw new IllegalArgumentException("Storage path không hợp lệ");
        }

    }

    private String resolveContentType(Path file){
        try{
            String contentType = Files.probeContentType(file);

            return contentType != null ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private MediaType parseMediaType(String contentType){
        if(contentType == null || contentType.isBlank()){
            return MediaType.APPLICATION_OCTET_STREAM;
        }
        try{
            return MediaType.parseMediaType(contentType);
        }catch(Exception e){
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
    private String removeTrailingSlash(String value){
        if(value.endsWith("/")){
            return value.substring(0,value.length()-1);
        }
        return value;
    }

}
