package edu.froliak.labwork5.controller;

import edu.froliak.labwork5.model.Weapon;
import edu.froliak.labwork5.service.WeaponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
  @author eugen
  @project labwork5
  @class WeaponRestController
  @version 1.0.0
  @since 4/15/2025 - 09.22
*/
@RestController
@RequestMapping("api/v1/weapons/")
@RequiredArgsConstructor
public class WeaponRestController {
    private final WeaponService weaponService;

    @GetMapping
    public List<Weapon> showAll() {
        return weaponService.getAll();
    }

    @GetMapping("{id}")
    public Weapon showOneById(@PathVariable String id) {
        return weaponService.getById(id);
    }

    @PostMapping
    public Weapon insert(@RequestBody Weapon weapon) {
        return weaponService.create(weapon);
    }

    @PutMapping
    public Weapon edit(@RequestBody Weapon weapon) {
        return weaponService.update(weapon);
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        weaponService.delById(id);
    }
}
