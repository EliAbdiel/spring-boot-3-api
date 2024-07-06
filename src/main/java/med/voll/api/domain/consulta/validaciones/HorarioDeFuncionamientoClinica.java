package med.voll.api.domain.consulta.validaciones;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;

@Component
public class HorarioDeFuncionamientoClinica implements ValidadorDeConsulta {

    public void validar(DatosAgendarConsulta datos) {

        var domingo = DayOfWeek.SUNDAY.equals(datos.fecha());

        var antesDeApertura = datos.fecha().getHour() < 7;

        var despuesDeCierre = datos.fecha().getHour() > 19;

        if (domingo || antesDeApertura || despuesDeCierre) {
            throw new ValidationException("El horario de atención de la clinica es de " +
                    "lunes a sabado de 7:00 a 19:00 horas");
        }
    }
}
