package com.example.vkrfull.controller;

import com.example.vkrfull.model.Exercise;
import com.example.vkrfull.service.ExerciseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin
public class ExerciseController {

    private final ExerciseServiceImpl exerciseService;

    @Autowired
    public ExerciseController(ExerciseServiceImpl exerciseService) {
        this.exerciseService = exerciseService;
    }

    @GetMapping(value = "/pytest5")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> pytest() {
        try {
            String gr = "start = 'FOR ' var '_:=_' intnumb ' TO ' intnumb [' BY ' intnumb ] ' DO' \n"+
                    "var = id [ '_[_' indexes '_]_' ] \n"+
                    "indexes = index { '_,_' index } \n"+
                    "index = id | intnumb \n"+
                    "intnumb = '0' | ['-'] digit { digitzero } \n"+
                    "id = alpha { alpha | digitzero } ";
            String gr2 = "start = 'FOR ' var '_:=_' intnumb ' TO ' intnumb [' BY ' intnumb ] ' DO' \\nvar = id [ '_[_' indexes '_]_' ] \\nindexes = index { '_,_' index } \\nindex = id | intnumb \\nintnumb = '0' | ['-'] digit { digitzero } \\nid = alpha { alpha | digitzero } ";
            String gr3 = "start/=/\'FOR/\'/var/\'_:=_\'/intnumb/\'/TO/\'/intnumb/[\'/BY/\'/intnumb/]/\'/DO\'///"+
                    "var/=/id/[/\'_[_\'/indexes/\'_]_\'/]///"+
                    "indexes/=/index/{/\'_,_\'/index/}///"+
                    "index/=/id/|/intnumb///"+
                    "intnumb/=/\'0\'/|/[\'-\']/digit/{/digitzero/}///"+
                    "id/=/alpha/{/alpha/|/digitzero/}/";
            String ex = gr2;
            ex = ex.replaceAll(" \\\\n", "///");
            ex = ex.replaceAll(" ", "/");
            String s = null;
            Process p = Runtime.getRuntime().exec("python src/main/java/com/example/vkrfull/controller/maintestjava.py "+ex);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                System.out.println("RESPONSE: "+s);
                exerciseService.parsePythonResponse(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println("ERROR: " +s);
            }

        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/exercises")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> create(@RequestBody Exercise exercise) {
        log.debug("exerciseBody '{}'", exercise);
        exerciseService.create(exercise);
        log.info("new entity is created");
        return new ResponseEntity<>(HttpStatus.CREATED);
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
