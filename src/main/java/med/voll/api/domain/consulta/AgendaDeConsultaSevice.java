package med.voll.api.domain.consulta;

import med.voll.api.domain.consulta.desafio.ValidadorCancelamientoDeConsulta;
import med.voll.api.domain.consulta.validaciones.ValidadorDeConsulta;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;
import med.voll.api.domain.paciente.PacienteRepository;
import med.voll.api.infra.errores.ValidacionDeIntegridad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgendaDeConsultaSevice {

    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private List<ValidadorDeConsulta> validadores;

    @Autowired
    private List<ValidadorCancelamientoDeConsulta> validadoresCancelamiento;

    public DatosDetalleConsulta agendar(DatosAgendarConsulta datos) {

        if (!pacienteRepository.findById(datos.idPaciente()).isPresent()) {
            throw new ValidacionDeIntegridad("Este id para el paciente no fue encontrado");
        }

        if (datos.idMedico() != null && !medicoRepository.existsById(datos.idMedico())) {
            throw new ValidacionDeIntegridad("Este id para el medico no fue encontrado");
        }

        validadores.forEach(v -> v.validar(datos));

        var paciente = pacienteRepository.findById(datos.idPaciente()).get();

        var medico = seleccionarMedico(datos);

        if (medico == null) {
            throw new ValidacionDeIntegridad("No existen medicos disponibles para este horario y especialidad");
        }

        var consulta = new Consulta(medico, paciente, datos.fecha());

        consultaRepository.save(consulta);

        return new DatosDetalleConsulta(consulta);
    }

    private Medico seleccionarMedico(DatosAgendarConsulta datos) {
        if (datos.idMedico() != null) {
            return medicoRepository.getReferenceById(datos.idMedico());
        }
        if (datos.especialidad()==null) {
            throw new ValidacionDeIntegridad("Debe seleccionar una especialidad para el medico");
        }
        return medicoRepository.seleccionarMedicoConEspecialidadEnFecha(datos.especialidad(), datos.fecha());
    }

    public void cancelar(DatosCancelamientoConsulta datos) {
        if (!consultaRepository.existsById(datos.idConsulta())) {
            throw new ValidacionDeIntegridad("Id de la consulta informada no existe");
        }

        validadoresCancelamiento.forEach(v -> v.validar(datos));

        var consulta = consultaRepository.getReferenceById(datos.idConsulta());
        consulta.cancelar(datos.motivo());
    }

    public Page<DatosDetalleConsulta> consultar(Pageable paginacion) {
        return consultaRepository.findAll(paginacion).map(DatosDetalleConsulta::new);
    }
}
