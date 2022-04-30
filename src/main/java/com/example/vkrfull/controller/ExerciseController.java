package com.example.vkrfull.controller;

import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.model.FileData;
import com.example.vkrfull.service.ExerciseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
public class ExerciseController {

    @Autowired
    private HttpServletRequest request;

    private final ExerciseServiceImpl exerciseService;

    @Autowired
    public ExerciseController(ExerciseServiceImpl exerciseService) {
        this.exerciseService = exerciseService;
    }

    @PostMapping(value = "/exercises/files/save")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> savePDF(@RequestParam("file") MultipartFile file,
                                     @RequestHeader("exerciseId") String id) throws IOException {
        final Exercise exercise = exerciseService.get(UUID.fromString(id));
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            Path path = Paths.get("src/files");
            String pathString = path.toAbsolutePath().toString().replace("\\", "/");
            File uploadDir = new File(pathString);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String resultFilename = file.getOriginalFilename();
            file.transferTo(new File(pathString + "/" + resultFilename));
            exercise.setFileName(resultFilename);
        }
        exerciseService.update(exercise, UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/exercises/files/{filename}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "filename")
                                                             String filename) throws IOException {
        Resource file = exerciseService.download(filename);
        Path path = file.getFile()
                .toPath();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @GetMapping(value = "/exercises/files")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public List<FileData> list() {
        return exerciseService.list();
    }

    @PostMapping(value = "/exercises")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Exercise exercise) throws IOException {
        log.debug("exerciseBody '{}'", exercise);
        Exercise savedExercise = exerciseService.create(exercise);
        log.info("new exercise is created");
        return new ResponseEntity<>(savedExercise, HttpStatus.CREATED);
    }

    @GetMapping(value = "/exercises")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Exercise>> getAll() {
        final List<Exercise> exercises = exerciseService.getAll();
        log.info("get entity");
        return new ResponseEntity<>(exercises, HttpStatus.OK);
    }

    @GetMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Exercise> get(@PathVariable(name = "id") UUID id) {
        log.debug("id '{}'", id);
        final Exercise exercise = exerciseService.get(id);
        log.debug("exercise '{}'", exercise);
        return new ResponseEntity<>(exercise, HttpStatus.OK);
    }

    @PutMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable(name = "id") UUID id,
                                    @RequestBody Exercise exercise) {
        log.debug("exerciseBody '{}'", exercise);
        exerciseService.update(exercise, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") UUID id) {
        exerciseService.delete(id);
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
