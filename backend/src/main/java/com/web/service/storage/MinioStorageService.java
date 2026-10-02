package com.web.service.storage;
 
import com.web.dto.StorageFileDTO;
import com.web.dto.StorageResourceDTO;
import com.web.exception.MyException;
import com.web.service.IStorageService;
import io.minio.*;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.storage.image.type",
        havingValue = "minio"
)
public class MinioStorageService implements IStorageService {


    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);
    private final MinioClient minioClient;
    private String bucket;
    private String urlPrefix;

    public MinioStorageService(MinioClient minioClient,
                                @Value("${minio.bucket}") String bucket,
                              @Value("${app.upload-url-prefix:/api/uploads}") String urlPrefix){
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.urlPrefix = removeTrailingSlash(urlPrefix);;

    }

    @Override
    public StorageFileDTO save(MultipartFile file, String folder) {
        if(file == null || file.isEmpty()){
            throw new IllegalArgumentException("File không được rỗng");
        }

        String safeFolder = normalizedKey(folder);
        String filename = generateFilename(file);
        String key = safeFolder + "/" + filename;
        String contentType = resolveContentType(file);

        try (InputStream inputStream = file.getInputStream()){
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(inputStream,
                            file.getSize(),
                            -1)
                    .contentType(contentType)
                    .build());
            return new StorageFileDTO(key,getPublicUrl(key),file.getSize(),contentType);
        }catch(Exception e){
            throw new RuntimeException(
                    "Không thể upload file lên MinIO",
                    e
            );
        }
    }

    @Override
    public Optional<StorageResourceDTO> load(String key) {
        String normalizedKey = normalizedKey(key);
        if(normalizedKey == null){
            return Optional.empty();
        }

        Optional<StorageResourceDTO> result = loadObject(normalizedKey);

        if(result.isPresent()){
            return result;
        }

        if(normalizedKey.startsWith("product/")){
            String oldKey = normalizedKey.substring("prduct/".length());
            return loadObject(oldKey);
        }

        return Optional.empty();
    }

    @Override
    public void delete(String key) {
        String normalizedKey = normalizedKey(key);
        if(normalizedKey == null){
            return;
        }
        try{
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(normalizedKey).build());
        }catch (Exception e){
            throw new RuntimeException("Không thể xoá Minio Object"+ normalizedKey);
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
        if(key.startsWith("products/")){
            key = "product/" + key.substring("products/".length());
        }

        key = key.replaceAll("^/+","");

        if(key.isBlank() || key.contains("..")){
            return null;
        }
        return key;


    }

    private String resolveContentType(MultipartFile file){
        String contentType = file.getContentType();
        return contentType != null && contentType.isBlank() ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE;

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
    private Optional<StorageResourceDTO> loadObject(String objectName){
        try{
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );

            GetObjectResponse stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName).build());

            InputStreamResource resource = new InputStreamResource(stream);
            MediaType mediaType = parseMediaType(stat.contentType());
            return Optional.of(new StorageResourceDTO(resource,mediaType,stat.size()));


        } catch (ErrorResponseException e) {
            String code = e.getStatusCode().toString();
            if("NoSuchKey".equals(code)|| "NoSuchObject".equals(code)|| "NoSuchFile".equals(code)){
                return Optional.empty();
            }
            log.error("Minio Eror bucketr={}, object = {}, code = {}",
                    bucket,
                    objectName,
                    code,
                    e);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Minio Eror bucketr={}, object = {}",
                    bucket,
                    objectName,
                    e);
            return Optional.empty();
        }
    }
}
