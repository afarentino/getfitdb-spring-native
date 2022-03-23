package com.github.afarentino.getfitdb;

import java.util.List;
import com.github.afarentino.getfitdb.model.ExerciseRecord;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * In Spring a RestController annotation is equivalent to writing a @Controller class which has
 * @ResponseBody annotated on every single method.
 *
 * Therefore by default RestControllers return JSON/XML, instead of HTML.
 */
@RestController
public class RecordController {
    private final RecordService recordService;

    public RecordController(RecordService service) {
        this.recordService = service;
    }

    @GetMapping("/api/records")
    public List<ExerciseRecord> records() {

        return recordService.findAll();

    }

}
