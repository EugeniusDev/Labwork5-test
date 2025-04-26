package edu.froliak.labwork5.service;

import edu.froliak.labwork5.model.Weapon;
import edu.froliak.labwork5.repository.WeaponRepository;
import edu.froliak.labwork5.request.WeaponCreateRequest;
import edu.froliak.labwork5.request.WeaponUpdateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

/*
  @author eugen
  @project labwork5
  @class WeaponServiceMockTest
  @version 1.0.0
  @since 4/26/2025 - 07.48
*/

@SpringBootTest
class WeaponServiceMockTest {

    @Mock
    private WeaponRepository mockRepository;

    private WeaponService underTest;

    @Captor
    private ArgumentCaptor<Weapon> weaponArgumentCaptor;

    private WeaponCreateRequest sampleCreateRequest;
    private WeaponUpdateRequest sampleUpdateRequest;
    private Weapon existingWeapon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new WeaponService(mockRepository);

        sampleCreateRequest = new WeaponCreateRequest("TestWeapon", "CODE123", "Test Desc");

        existingWeapon = Weapon.builder()
                .id("existing-id-abc")
                .name("Old Name")
                .code("OLDCODE")
                .description("Old Desc")
                .createDate(LocalDateTime.now().minusDays(1))
                .updateDates(new ArrayList<>())
                .build();
        sampleUpdateRequest = new WeaponUpdateRequest(
                existingWeapon.getId(),
                "New Name",
                "NEWCODE",
                "New Desc"
        );
    }

    @AfterEach
    void tearsDown(){
    }

    // Create
    @DisplayName("CREATE: Should call existsByCode with correct code")
    @Test
    void create_shouldCallExistsByCode() {
        given(mockRepository.existsByCode(sampleCreateRequest.code())).willReturn(false);
        given(mockRepository.save(any(Weapon.class))).willReturn(new Weapon());

        underTest.create(sampleCreateRequest);

        then(mockRepository).should().existsByCode(sampleCreateRequest.code());
    }

    @DisplayName("CREATE: Should call save when code does not exist")
    @Test
    void create_shouldCallSaveWhenCodeNotExists() {
        given(mockRepository.existsByCode(sampleCreateRequest.code())).willReturn(false);
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation -> invocation.getArgument(0));

        underTest.create(sampleCreateRequest);

        then(mockRepository).should().save(weaponArgumentCaptor.capture());
        Weapon savedWeapon = weaponArgumentCaptor.getValue();
        assertThat(savedWeapon).isNotNull();
    }

    @DisplayName("CREATE: Should map request fields correctly to saved weapon")
    @Test
    void create_shouldMapRequestFieldsToSavedWeapon() {
        given(mockRepository.existsByCode(sampleCreateRequest.code())).willReturn(false);
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation -> invocation.getArgument(0));

        underTest.create(sampleCreateRequest);

        then(mockRepository).should().save(weaponArgumentCaptor.capture());
        Weapon savedWeapon = weaponArgumentCaptor.getValue();
        assertThat(savedWeapon.getName()).isEqualTo(sampleCreateRequest.name());
        assertThat(savedWeapon.getCode()).isEqualTo(sampleCreateRequest.code());
        assertThat(savedWeapon.getDescription()).isEqualTo(sampleCreateRequest.description());
    }

    @DisplayName("CREATE: Should set createDate and empty updateDates on saved weapon")
    @Test
    void create_shouldSetTimestampsOnSavedWeapon() {
        given(mockRepository.existsByCode(sampleCreateRequest.code())).willReturn(false);
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation -> invocation.getArgument(0));

        underTest.create(sampleCreateRequest);

        then(mockRepository).should().save(weaponArgumentCaptor.capture());
        Weapon savedWeapon = weaponArgumentCaptor.getValue();
        assertThat(savedWeapon.getCreateDate()).isNotNull();
        assertThat(savedWeapon.getUpdateDates()).isNotNull().isEmpty();
    }

    @DisplayName("CREATE: Should return created weapon when code does not exist")
    @Test
    void create_shouldReturnCreatedWeaponWhenCodeNotExists() {
        given(mockRepository.existsByCode(sampleCreateRequest.code())).willReturn(false);
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation -> {
            Weapon weaponToSave = invocation.getArgument(0);
            weaponToSave.setId("generated-id");
            return weaponToSave;
        });

        Weapon result = underTest.create(sampleCreateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("generated-id");
        assertThat(result.getName()).isEqualTo(sampleCreateRequest.name());
    }


    @DisplayName("CREATE: Should return null when code already exists (Wrong path)")
    @Test
    void create_shouldReturnNullWhenCodeExists() {
        given(mockRepository.existsByCode(sampleCreateRequest.code())).willReturn(true);

        Weapon result = underTest.create(sampleCreateRequest);

        assertNull(result);
    }

    @DisplayName("CREATE: Should not call save when code already exists (Wrong path)")
    @Test
    void create_shouldNotCallSaveWhenCodeExists() {
        given(mockRepository.existsByCode(sampleCreateRequest.code())).willReturn(true);

        underTest.create(sampleCreateRequest);

        then(mockRepository).should(never()).save(any(Weapon.class));
    }

    // Update
    @DisplayName("UPDATE: Should call findById with correct ID")
    @Test
    void update_shouldCallFindById() {
        given(mockRepository.findById(sampleUpdateRequest.id())).willReturn(Optional.of(existingWeapon));
        given(mockRepository.save(any(Weapon.class))).willReturn(new Weapon());

        underTest.update(sampleUpdateRequest);

        then(mockRepository).should().findById(sampleUpdateRequest.id());
    }

    @DisplayName("UPDATE: Should call save when weapon is found")
    @Test
    void update_shouldCallSaveWhenWeaponFound() {
        given(mockRepository.findById(sampleUpdateRequest.id())).willReturn(Optional.of(existingWeapon));
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation -> invocation.getArgument(0));

        underTest.update(sampleUpdateRequest);

        then(mockRepository).should().save(any(Weapon.class));
    }

    @DisplayName("UPDATE: Should map request fields correctly to updated weapon")
    @Test
    void update_shouldMapRequestFieldsToUpdatedWeapon() {
        given(mockRepository.findById(sampleUpdateRequest.id())).willReturn(Optional.of(existingWeapon));
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation -> invocation.getArgument(0));

        underTest.update(sampleUpdateRequest);

        then(mockRepository).should().save(weaponArgumentCaptor.capture());
        Weapon savedWeapon = weaponArgumentCaptor.getValue();
        assertThat(savedWeapon.getId()).isEqualTo(sampleUpdateRequest.id());
        assertThat(savedWeapon.getName()).isEqualTo(sampleUpdateRequest.name());
        assertThat(savedWeapon.getCode()).isEqualTo(sampleUpdateRequest.code());
        assertThat(savedWeapon.getDescription()).isEqualTo(sampleUpdateRequest.description());
    }

    @DisplayName("UPDATE: Should preserve original createDate and add updateDate")
    @Test
    void update_shouldPreserveCreateDateAndAddUpdateDate() {
        given(mockRepository.findById(sampleUpdateRequest.id())).willReturn(Optional.of(existingWeapon));
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation -> invocation.getArgument(0));
        LocalDateTime originalCreateDate = existingWeapon.getCreateDate();
        int initialUpdateCount = existingWeapon.getUpdateDates().size();

        underTest.update(sampleUpdateRequest);

        then(mockRepository).should().save(weaponArgumentCaptor.capture());
        Weapon savedWeapon = weaponArgumentCaptor.getValue();
        assertThat(savedWeapon.getCreateDate()).isEqualTo(originalCreateDate);
        assertThat(savedWeapon.getUpdateDates()).hasSize(initialUpdateCount + 1);
        assertThat(savedWeapon.getUpdateDates().get(initialUpdateCount)).isNotNull();
    }

    @DisplayName("UPDATE: Should return updated weapon when weapon is found")
    @Test
    void update_shouldReturnUpdatedWeaponWhenFound() {
        given(mockRepository.findById(sampleUpdateRequest.id())).willReturn(Optional.of(existingWeapon));
        given(mockRepository.save(any(Weapon.class))).willAnswer(invocation ->
                invocation.<Weapon>getArgument(0));

        Weapon result = underTest.update(sampleUpdateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(sampleUpdateRequest.id());
        assertThat(result.getName()).isEqualTo(sampleUpdateRequest.name());
    }

    @DisplayName("UPDATE: Should return null when weapon is not found (Wrong path)")
    @Test
    void update_shouldReturnNullWhenWeaponNotFound() {
        given(mockRepository.findById(sampleUpdateRequest.id())).willReturn(Optional.empty());

        Weapon result = underTest.update(sampleUpdateRequest);

        assertNull(result);
    }

    @DisplayName("UPDATE: Should not call save when weapon is not found (Wrong path)")
    @Test
    void update_shouldNotCallSaveWhenWeaponNotFound() {
        given(mockRepository.findById(sampleUpdateRequest.id())).willReturn(Optional.empty());

        underTest.update(sampleUpdateRequest);

        then(mockRepository).should(never()).save(any(Weapon.class));
    }
}