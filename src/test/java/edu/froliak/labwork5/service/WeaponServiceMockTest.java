package edu.froliak.labwork5.service;

import edu.froliak.labwork5.model.Weapon;
import edu.froliak.labwork5.repository.WeaponRepository;
import edu.froliak.labwork5.request.WeaponCreateRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    private ArgumentCaptor<Weapon> argumentCaptor;

    private WeaponCreateRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new WeaponService(mockRepository);
    }
    @AfterEach
    void tearsDown(){

    }

    @DisplayName("Create new Item. Happy Path")
    @Test
    void whenInsertNewItemAndCodeNotExistsThenOk() {
        //given
        request = new WeaponCreateRequest("Till Lindemann", "Rammstein", "poet");
        Weapon weaponToCreate = Weapon.builder()
                .name(request.name())
                .code(request.code())
                .description(request.description())
                .build();
        given(mockRepository.existsByCode(request.code())).willReturn(false);
        // when
        underTest.create(request);
        // then
        then(mockRepository).should().save(argumentCaptor.capture());
        Weapon itemToSave = argumentCaptor.getValue();
        assertThat(itemToSave.getName()).isEqualTo(request.name());
        assertNotNull(itemToSave.getCreateDate());
        assertTrue(itemToSave.getCreateDate().isBefore(LocalDateTime.now()));
        assertTrue(itemToSave.getUpdateDates().isEmpty());
        verify(mockRepository).save(itemToSave);
        verify(mockRepository, times(1)).existsByCode(request.code());
        verify(mockRepository, times(1)).save(itemToSave);
    }

    //  @Test
    void update() {
    }
}