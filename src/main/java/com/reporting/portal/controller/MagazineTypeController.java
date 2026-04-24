package com.reporting.portal.controller;

import com.reporting.portal.entity.MagazineType;
import com.reporting.portal.repository.MagazineTypeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/magazine/types", "/magazine/types"})
@CrossOrigin(origins = "http://65.0.71.13")
public class MagazineTypeController {

    private final MagazineTypeRepository magazineTypeRepository;

    public MagazineTypeController(MagazineTypeRepository magazineTypeRepository) {
        this.magazineTypeRepository = magazineTypeRepository;
    }

    @GetMapping
    public ResponseEntity<List<MagazineType>> getTypes() {
        return ResponseEntity.ok(magazineTypeRepository.findAll());
    }
}