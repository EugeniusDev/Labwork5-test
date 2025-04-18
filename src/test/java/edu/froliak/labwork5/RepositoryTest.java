package edu.froliak.labwork5;

/*
  @author eugen
  @project labwork5
  @class RepositoryTest
  @version 1.0.0
  @since 4/16/2025 - 12.24
*/

import edu.froliak.labwork5.model.Weapon;
import edu.froliak.labwork5.repository.WeaponRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class RepositoryTest {

    @Autowired
    WeaponRepository underTest;

    @BeforeAll
    void beforeAll() {}

    @BeforeEach
    void setUp() {
        Weapon assaultRifle = Weapon.builder()
                .id("1")
                .name("Assault Rifle")
                .description("00sample description00")
                .code("001")
                .build();
        Weapon tank = Weapon.builder()
                .id("2")
                .name("Tank")
                .description("00sample description00")
                .code("002")
                .build();
        Weapon fpvDrone = Weapon.builder()
                .id("3")
                .name("FPV drone")
                .description("00sample description00")
                .code("003")
                .build();
        underTest.saveAll(List.of(assaultRifle, tank, fpvDrone));
    }

    @AfterEach
    void tearDown() {
        List<Weapon> weaponsToDelete = underTest.findAll().stream()
                .filter(Weapon -> Weapon.getDescription().contains("00sample description00"))
                .toList();
        underTest.deleteAll(weaponsToDelete);
    }

    @AfterAll
    void afterAll() {}

    @Test
    void testSetShouldContain_3_Records_ToTest(){
        List<Weapon> weaponsToDelete = underTest.findAll().stream()
                .filter(Weapon -> Weapon.getDescription().contains("00sample description00"))
                .toList();
        assertEquals(3,weaponsToDelete.size());
    }

    @Test
    void whenThereAreTestWeaponsInDatabaseItShouldBeAbleToDetermineThemByDescription() {
        Weapon attackHelicopter = Weapon.builder()
                .name("Attack helicopter")
                .description("00sample description00")
                .code("0001")
                .build();
        Weapon spear = Weapon.builder()
                .name("Spear")
                .description("00sample description00")
                .code("0002")
                .build();
        underTest.saveAll(List.of(attackHelicopter, spear));

        List<Weapon> testWeaponsFromDatabase = underTest.findAll().stream()
                .filter(d -> d.getDescription().equals("00sample description00"))
                .toList();
        assertFalse(testWeaponsFromDatabase.isEmpty());
    }

    @Test
    void whenSearchingWeaponByCodeThenDatabaseShouldReturnIt() {
        Weapon spear = Weapon.builder()
                .name("Spear")
                .description("00sample description00")
                .code("0000")
                .build();
        underTest.save(spear);

        Weapon weaponFoundByCode = underTest.findAll().stream()
                .filter(d -> d.getCode().equals("0000"))
                .findFirst().orElse(null);
        assertNotNull(weaponFoundByCode);
        assertEquals(spear, weaponFoundByCode);
    }

    @Test
    void whenDatabaseHasNoWeaponWithSuchNameThenItShouldReturnEmptyList() {
        List<Weapon> notFound = underTest.findAll().stream()
                .filter(d -> d.getName().equals("WUNDERWAFFE"))
                .toList();
        assertTrue(notFound.isEmpty());
    }

    @Test
    void shouldUpdateWeaponByName() {
        Weapon sniperRifle = Weapon.builder()
                .name("Sniper rifle")
                .description("00sample description00")
                .code("005")
                .build();
        underTest.save(sniperRifle);

        sniperRifle.setName("Marksman rifle");
        underTest.save(sniperRifle);

        Weapon rifleFromDatabase = underTest.findById(sniperRifle.getId()).orElse(null);
        assertEquals("Marksman rifle", rifleFromDatabase.getName());
    }

    @Test
    void shouldUpdateMultipleFieldsOfWeapon() {
        Weapon initialWeapon = Weapon.builder()
                .name("Crossbow").code("CB-01").description("00sample description00").build();
        Weapon savedWeapon = underTest.save(initialWeapon);
        String weaponId = savedWeapon.getId();
        assertNotNull(weaponId);

        Weapon weaponToUpdate = underTest.findById(weaponId).orElseThrow();
        weaponToUpdate.setName("Heavy Crossbow");
        weaponToUpdate.setCode("HCB-02");
        underTest.save(weaponToUpdate);

        Weapon updatedWeapon = underTest.findById(weaponId).orElse(null);
        assertNotNull(updatedWeapon);
        assertEquals(weaponId, updatedWeapon.getId());
        assertEquals("Heavy Crossbow", updatedWeapon.getName());
        assertEquals("HCB-02", updatedWeapon.getCode());
    }

    @Test
    void shouldDeleteWeapon() {
        Weapon laser = Weapon.builder()
                .name("Laser")
                .description("00sample description00")
                .code("007")
                .build();
        underTest.save(laser);
        underTest.deleteById(laser.getId());
        assertFalse(underTest.findById(laser.getId()).isPresent());
    }

    @Test
    void shouldGiveNotEmptyIdForNewRecord() {
        Weapon reconnaissanceDrone = Weapon.builder()
                .name("Reconnaissance drone")
                .description("00sample description00")
                .code("004")
                .build();
        underTest.save(reconnaissanceDrone);
        Weapon weaponFromDb = underTest.findAll().stream()
                .filter(Weapon -> Weapon.getName().equals("Reconnaissance drone"))
                .findFirst().orElse(null);
        assertNotSame(weaponFromDb.getId(), reconnaissanceDrone.getId());
        assertNotNull(weaponFromDb);
        assertNotNull(weaponFromDb.getId());
        assertFalse(weaponFromDb.getId().isEmpty());
        assertEquals(24, weaponFromDb.getId().length());
    }

    @Test
    void weaponsIdShouldBeUnique() {
        Weapon attackHelicopter = Weapon.builder()
                .name("Attack helicopter")
                .description("00sample description00")
                .code("006")
                .build();
        Weapon spear = Weapon.builder()
                .name("Spear")
                .description("00sample description00")
                .code("007")
                .build();
        underTest.saveAll(List.of(attackHelicopter, spear));

        List<Weapon> weaponsFromDatabase = underTest.findAll().stream()
                .filter(d -> d.getDescription().equals("00sample description00"))
                .toList();
        assertNotEquals(weaponsFromDatabase.get(0).getId(), weaponsFromDatabase.get(1).getId());
    }

    @Test
    void whenWeaponIsSavedThroughRepositoryThenAuditMetadataFieldsShouldNotBeSet() {
        Weapon axe = Weapon.builder()
                .name("Axe")
                .description("00sample description00")
                .code("006")
                .build();
        underTest.save(axe);

        Weapon axeFromDatabase = underTest.findAll().stream()
                .filter(d -> d.getName().equals("Axe"))
                .findFirst().orElse(null);
        assertNotNull(axeFromDatabase);
        assertNull(axeFromDatabase.getCreatedDate(), "AuditMetadata fields should not be set");
    }
}
