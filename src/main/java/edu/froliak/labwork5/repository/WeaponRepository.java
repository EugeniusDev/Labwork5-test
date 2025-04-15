package edu.froliak.labwork5.repository;

import edu.froliak.labwork5.model.Weapon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/*
  @author eugen
  @project labwork5
  @class WeaponRepository
  @version 1.0.0
  @since 4/13/2025 - 09.40
*/
@Repository
public interface WeaponRepository extends MongoRepository<Weapon, String> {
}
