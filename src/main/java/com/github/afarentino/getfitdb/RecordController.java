package com.github.afarentino.getfitdb;

import com.github.afarentino.getfitdb.model.ExerciseRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * In Spring a RestController annotation is equivalent to writing a @Controller class which has
 * @ResponseBody annotated on every single method.
 *
 * Therefore by default RestControllers return JSON/XML, instead of HTML.
 */
@RestController
public class RecordController {
    private final RecordService recordService;

    @Autowired
    public RecordController(RecordService service) {
        this.recordService = service;
    }

    @GetMapping("/records")
    public List<ExerciseRecord> records() {
        return getList();
    }

    private List<ExerciseRecord> getList() {
        List<ExerciseRecord> list = recordService.findAll();
        return list;
    }
}
