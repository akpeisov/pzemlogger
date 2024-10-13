package kz.home.pzem.controller;

import kz.home.pzem.entity.PzemData;
import kz.home.pzem.repository.PzemDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/pzem")
public class PzemDataController {

    @Autowired
    private PzemDataRepository repository;

    @GetMapping("/data")
    public List<PzemData> getAllData() {
        return repository.findAll();
    }
}
