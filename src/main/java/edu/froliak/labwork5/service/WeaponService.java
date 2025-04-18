package edu.froliak.labwork5.service;

import edu.froliak.labwork5.model.Weapon;
import edu.froliak.labwork5.repository.WeaponRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
  @author eugen
  @project labwork5
  @class WeaponService
  @version 1.0.0
  @since 4/13/2025 - 09.42
*/
@Service
@RequiredArgsConstructor
public class WeaponService {
    private final WeaponRepository weaponRepository;

    private List<Weapon> weapons = new ArrayList<>();
    {
        Weapon assaultRifle = Weapon.builder()
                .id("1")
                .name("Assault Rifle")
                .description("Infantry weapon")
                .code("001")
                .build();
        Weapon tank = Weapon.builder()
                .id("2")
                .name("Tank")
                .description("It is very heavy")
                .code("002")
                .build();
        Weapon fpvDrone = Weapon.builder()
                .id("3")
                .name("FPV drone")
                .description("Provider of rusoriz")
                .code("003")
                .build();
        weapons.add(assaultRifle);
        weapons.add(tank);
        weapons.add(fpvDrone);
    }

    @PostConstruct
    void init() {
        weaponRepository.deleteAll();
        weaponRepository.saveAll(weapons);
    }

    public Weapon create(Weapon weapon) { return weaponRepository.save(weapon); }
    public Weapon update(Weapon weapon) { return weaponRepository.save(weapon); }

    public List<Weapon> getAll() { return weaponRepository.findAll(); }
    public Weapon getById(String id) { return weaponRepository.findById(id).orElse(null); }
    
    public void delById(String id) { weaponRepository.deleteById(id); }
}
