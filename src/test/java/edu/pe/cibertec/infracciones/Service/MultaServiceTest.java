package edu.pe.cibertec.infracciones.Service;

import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {

    @Mock
    private MultaRepository multaRepository;

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    @Captor
    private ArgumentCaptor<Multa> multaArgumentCaptor;

    @Test
    void transferirMulta_infractorBloqueado_noGuarda(){
        Infractor infractorA = new Infractor();
        infractorA.setId(1L);

        Infractor infractorB = new Infractor();
        infractorB.setId(2L);
        infractorB.setBloqueado(true);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);

        Multa multa = new Multa();
        multa.setId(1L);
        multa.setEstado(EstadoMulta.PENDIENTE);
        multa.setVehiculo(vehiculo);
        multa.setInfractor(infractorA);

        when(multaRepository.findById(1L))
                .thenReturn(Optional.of(multa));

        when(infractorRepository.findById(2L))
                .thenReturn(Optional.of(infractorB));
        assertThrows(InfractorBloqueadoException.class, () ->
                multaService.transferirMulta(1L, 2L));

        verify(multaRepository, never()).save(multaArgumentCaptor.capture());

        verify(multaRepository, times(1)).findById(1L);
    }
}
