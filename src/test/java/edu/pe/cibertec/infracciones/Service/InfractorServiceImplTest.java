package edu.pe.cibertec.infracciones.Service;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InfractorServiceImplTest {

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @Test
    void calcularDeudaTest(){

        Infractor infractor = new Infractor();
        infractor.setId(1L);

        Multa m1 = new Multa();
        m1.setEstado(EstadoMulta.PENDIENTE);
        m1.setMonto(200.00);

        Multa m2 = new Multa();
        m2.setEstado(EstadoMulta.VENCIDA);
        m2.setMonto(300.00);

        List<Multa> multas = List.of(m1, m2);

        when(infractorRepository.findById(1L))
                .thenReturn(Optional.of(infractor));

        when(multaRepository.findByInfractor_Id(1L))
                .thenReturn(multas);

        Double deuda = infractorService.calcularDeuda(1L);

        assertEquals(545.0, deuda);

    }

    @Test
    void desasignarVehiculo_sinMultasPendientes(){
        Infractor infractor = new Infractor();
        infractor.setId(1L);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1L);

        infractor.setVehiculos(new ArrayList<>(List.of(vehiculo)));

        when(infractorRepository.findById(1L))
                .thenReturn(Optional.of(infractor));

        when(vehiculoRepository.findById(1L))
                .thenReturn(Optional.of(vehiculo));

        when(multaRepository.existsByVehiculoIdAndEstado(1L, EstadoMulta.PENDIENTE))
                .thenReturn(false);

        infractorService.desasignarVehiculo(1L, 1L);

        assertFalse(infractor.getVehiculos().contains(vehiculo));
        verify(infractorRepository).save(infractor);

    }


}
