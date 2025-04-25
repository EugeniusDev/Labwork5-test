package edu.froliak.labwork5.service;

import edu.froliak.labwork5.model.Weapon;
import edu.froliak.labwork5.repository.WeaponRepository;
import edu.froliak.labwork5.request.WeaponCreateRequest;
import edu.froliak.labwork5.request.WeaponUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/*
  @author eugen
  @project labwork5
  @class WeaponServiceTest
  @version 1.0.0
  @since 4/18/2025 - 12.23
*/

@DataMongoTest
@Import(WeaponService.class)
class WeaponServiceTest {

    @Autowired
    private WeaponRepository weaponRepository;

    @Autowired
    private WeaponService underTest;

    @BeforeEach
    void setUp() {
    }

    // @AfterEach
    void tearsDown(){
        weaponRepository.deleteAll();
    }


    @Test
    void whenCreateCalledWithValidRequest_ThenWeaponIsReturned() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "T01", "Test Description");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon).isNotNull();
    }

    @Test
    void whenCreateCalled_ThenNonNullIdIsGenerated() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "T01", "Test Description");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon.getId()).isNotNull().isNotEmpty();
    }

    @Test
    void whenCreateCalled_ThenNameIsMappedCorrectly() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeaponName", "T01", "Test Description");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon.getName()).isEqualTo("TestWeaponName");
    }

    @Test
    void whenCreateCalled_ThenCodeIsMappedCorrectly() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "TestCode01", "Test Description");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon.getCode()).isEqualTo("TestCode01");
    }

    @Test
    void whenCreateCalled_ThenDescriptionIsMappedCorrectly() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "T01", "TestDesc123");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon.getDescription()).isEqualTo("TestDesc123");
    }

    @Test
    void whenCreateCalled_ThenCreateDateIsSet() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "T01", "Test Description");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon.getCreateDate()).isNotNull();
    }

    @Test
    void whenCreateCalled_ThenCreateDateIsRecent() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "T01", "Test Description");
        LocalDateTime beforeCreation = LocalDateTime.now().minusSeconds(1);
        Weapon createdWeapon = underTest.create(request);
        LocalDateTime afterCreation = LocalDateTime.now().plusSeconds(1);
        assertThat(createdWeapon.getCreateDate()).isBetween(beforeCreation, afterCreation);
    }

    @Test
    void whenCreateCalled_ThenUpdateDatesListIsInitialized() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "T01", "Test Description");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon.getUpdateDates()).isNotNull();
    }

    @Test
    void whenCreateCalled_ThenUpdateDatesListIsEmpty() {
        WeaponCreateRequest request = new WeaponCreateRequest("TestWeapon", "T01", "Test Description");
        Weapon createdWeapon = underTest.create(request);
        assertThat(createdWeapon.getUpdateDates()).isEmpty();
    }

    @Test
    void whenCreateCalled_ThenWeaponIsPersisted() {
        weaponRepository.deleteAll();
        WeaponCreateRequest request = new WeaponCreateRequest("PersistTest", "P01", "Check Repo");
        Weapon createdWeapon = underTest.create(request);
        Optional<Weapon> found = weaponRepository.findById(createdWeapon.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("PersistTest");
    }


    private Weapon createAndSaveWeaponForUpdate(String name, String code) {
        weaponRepository.deleteAll();
        Weapon weapon = Weapon.builder()
                .name(name)
                .code(code)
                .description("Initial Description")
                .createDate(LocalDateTime.now().minusMinutes(5))
                .updateDates(new ArrayList<>())
                .build();
        return weaponRepository.save(weapon);
    }

    @Test
    void whenUpdateCalledWithExistingId_ThenWeaponIsReturned() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest1", "UT1");
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "Updated Name", "U01", "Updated Desc");
        Weapon updatedWeapon = underTest.update(request);
        assertThat(updatedWeapon).isNotNull();
    }

    @Test
    void whenUpdateCalledWithExistingId_ThenIdRemainsUnchanged() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest2", "UT2");
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "Updated Name", "U01", "Updated Desc");
        Weapon updatedWeapon = underTest.update(request);
        assertThat(updatedWeapon.getId()).isEqualTo(existingWeapon.getId());
    }

    @Test
    void whenUpdateCalledWithExistingId_ThenNameIsUpdated() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest3", "UT3");
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "NewName123", "U01", "Updated Desc");
        Weapon updatedWeapon = underTest.update(request);
        assertThat(updatedWeapon.getName()).isEqualTo("NewName123");
        assertThat(weaponRepository.findById(existingWeapon.getId()).get().getName()).isEqualTo("NewName123");
    }

    @Test
    void whenUpdateCalledWithExistingId_ThenCodeIsUpdated() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest4", "UT4");
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "Updated Name", "NewCodeABC", "Updated Desc");
        Weapon updatedWeapon = underTest.update(request);
        assertThat(updatedWeapon.getCode()).isEqualTo("NewCodeABC");
    }

    @Test
    void whenUpdateCalledWithExistingId_ThenDescriptionIsUpdated() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest5", "UT5");
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "Updated Name", "U01", "New Description XYZ");
        Weapon updatedWeapon = underTest.update(request);
        assertThat(updatedWeapon.getDescription()).isEqualTo("New Description XYZ");
    }

    @Test
    void whenUpdateCalledWithExistingId_ThenUpdateDatesListIsNotNull() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest7", "UT7");
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "Updated Name", "U01", "Updated Desc");
        Weapon updatedWeapon = underTest.update(request);
        assertThat(updatedWeapon.getUpdateDates()).isNotNull();
    }

    @Test
    void whenUpdateCalledWithExistingId_ThenUpdateDatesListContainsOneEntry() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest8", "UT8");
        assertThat(existingWeapon.getUpdateDates()).isNotNull().isEmpty();
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "Updated Name", "U01", "Updated Desc");
        Weapon updatedWeapon = underTest.update(request);
        assertThat(updatedWeapon.getUpdateDates()).hasSize(1);
    }

    @Test
    void whenUpdateCalled_ThenAddedUpdateDateIsRecent() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest9", "UT9");
        WeaponUpdateRequest request = new WeaponUpdateRequest(existingWeapon.getId(), "Updated Name", "U01", "Updated Desc");
        LocalDateTime beforeUpdate = LocalDateTime.now().minusSeconds(1);
        Weapon updatedWeapon = underTest.update(request);
        LocalDateTime afterUpdate = LocalDateTime.now().plusSeconds(1);
        assertThat(updatedWeapon.getUpdateDates()).isNotEmpty();
        assertThat(updatedWeapon.getUpdateDates().get(updatedWeapon.getUpdateDates().size() - 1))
                .isBetween(beforeUpdate, afterUpdate);
    }

    @Test
    void whenUpdateCalledMultipleTimes_ThenUpdateDatesListGrows() {
        Weapon existingWeapon = createAndSaveWeaponForUpdate("UpdateTest10", "UT10");
        WeaponUpdateRequest request1 = new WeaponUpdateRequest(existingWeapon.getId(), "First Update", "U01", "Desc 1");
        WeaponUpdateRequest request2 = new WeaponUpdateRequest(existingWeapon.getId(), "Second Update", "U02", "Desc 2");

        Weapon firstUpdate = underTest.update(request1);
        try { Thread.sleep(5); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        Weapon secondUpdate = underTest.update(request2);

        assertThat(firstUpdate.getUpdateDates()).hasSize(1);
        assertThat(secondUpdate.getUpdateDates()).hasSize(2);

        Weapon finalWeapon = weaponRepository.findById(existingWeapon.getId()).orElseThrow();
        assertThat(finalWeapon.getUpdateDates()).hasSize(2);
        assertThat(finalWeapon.getName()).isEqualTo("Second Update");
    }

    @Test
    void whenUpdateCalledWithNonExistentId_ThenReturnsNull() {
        String nonExistentId = "non-existent-id-very-unique-12345";
        weaponRepository.deleteById(nonExistentId);

        WeaponUpdateRequest request = new WeaponUpdateRequest(nonExistentId, "Ghost Name", "G01", "Ghost Desc");
        Weapon result = underTest.update(request);
        assertThat(result).isNull();
    }

    @Test
    void whenUpdateCalledWithNonExistentId_ThenRepositoryIsNotUpdated() {
        weaponRepository.deleteAll();
        createAndSaveWeaponForUpdate("BaseData", "BD1");
        long initialCount = weaponRepository.count();
        String nonExistentId = "non-existent-id-really-unique-12345";
        weaponRepository.deleteById(nonExistentId);

        WeaponUpdateRequest request = new WeaponUpdateRequest(nonExistentId, "Ghost Name", "G01", "Ghost Desc");
        underTest.update(request);

        long finalCount = weaponRepository.count();
        assertThat(finalCount).isEqualTo(initialCount);
        assertThat(weaponRepository.findById(nonExistentId)).isNotPresent();
    }


    @Test
    void whenGetAllCalled_ThenReturnsAllWeapons() {
        weaponRepository.deleteAll();
        Weapon w1 = weaponRepository.save(Weapon.builder().name("Gun").code("G1").build());
        Weapon w2 = weaponRepository.save(Weapon.builder().name("Sword").code("S1").build());

        List<Weapon> weapons = underTest.getAll();
        assertThat(weapons).hasSize(2);
        assertThat(weapons).extracting(Weapon::getId).containsExactlyInAnyOrder(w1.getId(), w2.getId());
    }

    @Test
    void whenGetAllCalled_AndRepositoryIsEmpty_ThenReturnsEmptyList() {
        weaponRepository.deleteAll();
        assertThat(weaponRepository.count()).isZero();
        List<Weapon> weapons = underTest.getAll();
        assertThat(weapons).isNotNull().isEmpty();
    }

    @Test
    void whenGetByIdCalledWithExistingId_ThenReturnsWeapon() {
        weaponRepository.deleteAll();
        Weapon savedWeapon = weaponRepository.save(Weapon.builder().name("Laser").code("L1").description("Pew pew").build());
        String existingId = savedWeapon.getId();

        Weapon foundWeapon = underTest.getById(existingId);
        assertThat(foundWeapon).isNotNull();
        assertThat(foundWeapon.getId()).isEqualTo(existingId);
        assertThat(foundWeapon.getName()).isEqualTo("Laser");
        assertThat(foundWeapon.getCode()).isEqualTo("L1");
    }

    @Test
    void whenGetByIdCalledWithNonExistentId_ThenReturnsNull() {
        String nonExistentId = "non-existent-id-truly-unique-54321";
        weaponRepository.deleteById(nonExistentId);

        Weapon foundWeapon = underTest.getById(nonExistentId);
        assertThat(foundWeapon).isNull();
    }

    @Test
    void whenDelByIdCalledWithExistingId_ThenWeaponIsRemoved() {
        weaponRepository.deleteAll();
        Weapon savedWeapon = weaponRepository.save(Weapon.builder().name("To Delete").code("D1").build());
        String existingId = savedWeapon.getId();
        assertThat(weaponRepository.existsById(existingId)).isTrue();

        underTest.delById(existingId);
        assertThat(weaponRepository.existsById(existingId)).isFalse();
    }

    @Test
    void whenDelByIdCalledWithNonExistentId_ThenNoErrorAndCountUnchanged() {
        weaponRepository.deleteAll();
        weaponRepository.save(Weapon.builder().name("Some Weapon").code("S1").build());
        long countBefore = weaponRepository.count();
        String nonExistentId = "non-existent-id-verifiably-unique-98765";
        weaponRepository.deleteById(nonExistentId);
        assertThat(weaponRepository.existsById(nonExistentId)).isFalse();

        assertDoesNotThrow(() -> {
            underTest.delById(nonExistentId);
        });

        long countAfter = weaponRepository.count();
        assertThat(countAfter).isEqualTo(countBefore);
    }
}