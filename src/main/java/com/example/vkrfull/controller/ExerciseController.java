package com.example.vkrfull.controller;

import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.model.FileData;
import com.example.vkrfull.service.ExerciseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@CrossOrigin
public class ExerciseController {

    @Value("${upload.path}")
    private String uploadPath;

    @Autowired
    private HttpServletRequest request;

    private final ExerciseServiceImpl exerciseService;

    @Autowired
    public ExerciseController(ExerciseServiceImpl exerciseService) {
        this.exerciseService = exerciseService;
    }

//    @GetMapping(value = "/pytest5")
////    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<?> pytest() {
//        try {
//            String gr = "start = 'FOR ' var '_:=_' intnumb ' TO ' intnumb [' BY ' intnumb ] ' DO' \n"+
//                    "var = id [ '_[_' indexes '_]_' ] \n"+
//                    "indexes = index { '_,_' index } \n"+
//                    "index = id | intnumb \n"+
//                    "intnumb = '0' | ['-'] digit { digitzero } \n"+
//                    "id = alpha { alpha | digitzero } ";
//            String gr2 = "start = 'FOR ' var '_:=_' intnumb ' TO ' intnumb [' BY ' intnumb ] ' DO' \\nvar = id [ '_[_' indexes '_]_' ] \\nindexes = index { '_,_' index } \\nindex = id | intnumb \\nintnumb = '0' | ['-'] digit { digitzero } \\nid = alpha { alpha | digitzero } ";
//            String gr3 = "start/=/\'FOR/\'/var/\'_:=_\'/intnumb/\'/TO/\'/intnumb/[\'/BY/\'/intnumb/]/\'/DO\'///"+
//                    "var/=/id/[/\'_[_\'/indexes/\'_]_\'/]///"+
//                    "indexes/=/index/{/\'_,_\'/index/}///"+
//                    "index/=/id/|/intnumb///"+
//                    "intnumb/=/\'0\'/|/[\'-\']/digit/{/digitzero/}///"+
//                    "id/=/alpha/{/alpha/|/digitzero/}/";
//            String ex = gr2;
//            ex = ex.replaceAll(" \\\\n", "///");
//            ex = ex.replaceAll(" ", "/");
//            String s = null;
//            Process p = Runtime.getRuntime().exec("python src/main/java/com/example/vkrfull/controller/maintestjava.py "+ex);
//            BufferedReader stdInput = new BufferedReader(new
//                    InputStreamReader(p.getInputStream()));
//            BufferedReader stdError = new BufferedReader(new
//                    InputStreamReader(p.getErrorStream()));
//
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println("RESPONSE: "+s);
//                exerciseService.parsePythonResponse(s);
//            }
//            while ((s = stdError.readLine()) != null) {
//                System.out.println("ERROR: " +s);
//            }
//
//        }
//        catch (IOException e) {
//            System.out.println("exception happened - here's what I know: ");
//            e.printStackTrace();
//        }
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }

    @PostMapping(value = "/exercises/files/save")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> savePDF(@RequestParam("file") MultipartFile file,
                                     @RequestHeader("exerciseId") String id) throws IOException {
        final Exercise exercise = exerciseService.get(Integer.parseInt(id));
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            String uuidFile = UUID.randomUUID().toString();
            String resultFilename = uuidFile + "." + file.getOriginalFilename();
            String filePath = request.getServletContext().getRealPath("/");//доделать
            file.transferTo(new File(uploadPath + "/" + resultFilename));
            exercise.setFileName(resultFilename);
        }
        exerciseService.update(exercise, Integer.parseInt(id));
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
    public ResponseEntity<Exercise> get(@PathVariable(name = "id") int id) {
        log.debug("id '{}'", id);
        final Exercise exercise = exerciseService.get(id);
//        exercise.setExercise(exercise.getExercise().replaceAll("\\\\n", "&#13;&#10;"));
        log.debug("exercise '{}'", exercise);
        return new ResponseEntity<>(exercise, HttpStatus.OK);
    }

    @PutMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(@PathVariable(name = "id") int id,
                                    @RequestBody Exercise exercise) {
        log.debug("exerciseBody '{}'", exercise);
        exerciseService.update(exercise, id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable(name = "id") int id) {
        exerciseService.delete(id);
        log.debug("id '{}'", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
