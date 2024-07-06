package med.voll.api.domain.consulta.validaciones;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import med.voll.api.domain.medico.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicoActivo implements ValidadorDeConsulta {

    @Autowired
    private MedicoRepository medicoRepository;

    public void validar(DatosAgendarConsulta datos) {

        if (datos.idMedico() == null) {
            return;
        }

        var medicoActivo = medicoRepository.findActivoById(datos.idMedico());

        if (!medicoActivo) {
            throw new ValidationException("No se puede permitir programar citas con medicos " +
                    "inactivos en el sistema");
        }
    }
}
