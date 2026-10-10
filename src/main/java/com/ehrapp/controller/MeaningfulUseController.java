package com.ehrapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ehrapp.model.StateRecord;
import com.ehrapp.service.MeaningfulUseService;

@RestController
public class MeaningfulUseController {
	
	private final MeaningfulUseService service;

    public MeaningfulUseController(
            MeaningfulUseService service) {
        this.service = service;
    }

    @GetMapping("/hospitals/meaningful-use/{year}")
    public List<StateRecord> getStates(@PathVariable int year) throws Exception {

        return service.getMeaningfulUse(year);
    }

}
