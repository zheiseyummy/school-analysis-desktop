package com.youlai.system.controller;

import com.youlai.system.common.result.Result;
import com.youlai.system.service.LocalDatabaseBackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Path;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/local-backup")
@RequiredArgsConstructor
public class LocalBackupController {
    private final LocalDatabaseBackupService backupService;

    @PostMapping
    public Result<Map<String, String>> backup() {
        Path path = backupService.backup();
        return Result.success(Map.of("path", path == null ? "" : path.toString()));
    }

    @GetMapping
    public Result<java.util.List<String>> list() { return Result.success(backupService.listBackups()); }

    @PostMapping("/restore")
    public Result<Void> restore(@RequestParam String fileName) { backupService.restore(fileName); return Result.success(); }
}
