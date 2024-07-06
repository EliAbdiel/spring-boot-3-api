package med.voll.api.domain.consulta.validaciones;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class HorarioDeAnticipacion implements ValidadorDeConsulta {

    public void validar(DatosAgendarConsulta datos) {

        var ahora = LocalDateTime.now();

        var horaDeConsulta = datos.fecha();

        var diferenciaDe30Min = Duration.between(ahora, horaDeConsulta).toMinutes() < 30;

        if (diferenciaDe30Min) {
            throw new ValidationException("La consulta debe programarce con al menos " +
                    "30 minutos de anticipación");
        }
    }
}
