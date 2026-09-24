package com.youlai.system.service.impl.oss;

import com.youlai.system.model.dto.FileInfo;
import com.youlai.system.service.OssService;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

/** 本地文件存储实现，不依赖 MinIO、OSS 或外部网络。 */
@Service
@ConditionalOnProperty(value = "oss.type", havingValue = "local", matchIfMissing = true)
@ConfigurationProperties(prefix = "oss.local")
@Data
public class LocalFileOssService implements OssService {
    private String directory = "files";
    private Path root() { return Paths.get(directory).toAbsolutePath().normalize(); }

    @Override
    public FileInfo uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("上传文件不能为空");
        String originalName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot > 0 && dot < originalName.length() - 1) extension = originalName.substring(dot).replaceAll("[^A-Za-z0-9._-]", "");
        String relative = LocalDate.now() + "/" + UUID.randomUUID().toString().replace("-", "") + extension;
        Path target = root().resolve(relative).normalize();
        if (!target.startsWith(root())) throw new IllegalArgumentException("文件路径无效");
        try {
            Files.createDirectories(target.getParent());
            try (InputStream input = file.getInputStream()) { Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING); }
        } catch (IOException ex) { throw new IllegalStateException("文件上传失败", ex); }
        FileInfo info = new FileInfo();
        info.setName(relative.replace('\\', '/'));
        info.setUrl("/files/" + info.getName());
        return info;
    }

    @Override
    public boolean deleteFile(String filePath) {
        if (!StringUtils.hasText(filePath)) throw new IllegalArgumentException("删除文件路径不能为空");
        String relative = filePath.replace('\\', '/');
        if (relative.startsWith("/files/")) relative = relative.substring("/files/".length());
        else if (relative.startsWith("files/")) relative = relative.substring("files/".length());
        else throw new IllegalArgumentException("只能删除本地文件目录中的文件");
        Path target = root().resolve(relative).normalize();
        if (!target.startsWith(root())) throw new IllegalArgumentException("文件路径无效");
        try { return Files.deleteIfExists(target); }
        catch (IOException ex) { throw new IllegalStateException("文件删除失败", ex); }
    }
}
