package com.example.controllers;

import com.example.models.Position;
import com.example.repository.PositionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/positions")
public class PositionController {
    @Autowired
    PositionRepository positionRepository;

    @GetMapping
    public List<Position> getAll(){
        return positionRepository.findAll();
    }

    @GetMapping(params = "name")
    public List<Position> getPositions(@RequestParam("name") String name){
        return positionRepository.findAllByNameStartingWith(name);
    }


    @PutMapping("{id}")
    public Position putPosition(@PathVariable("id") Position positionFromDb, @RequestBody Position position){
        BeanUtils.copyProperties(position, positionFromDb,"id");
        return positionRepository.save(positionFromDb);
    }

    @PostMapping
    public Position addPosition(@RequestBody Position position) {
        return positionRepository.save(position);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Position position) {
        positionRepository.delete(position);
    }
}
