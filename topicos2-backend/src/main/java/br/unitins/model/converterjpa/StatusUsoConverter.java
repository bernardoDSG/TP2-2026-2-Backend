package br.unitins.model.converterjpa;

import br.unitins.model.StatusUso;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class StatusUsoConverter implements AttributeConverter<StatusUso, Long>{
    
    @Override
    public Long convertToDatabaseColumn(StatusUso statusUso) {
        if (statusUso == null)
            return null;
        return statusUso.getId();
    }

    @Override
    public StatusUso convertToEntityAttribute(Long id) {
        if (id == null)
            return null;
        return StatusUso.valueOf(id);
    }
}
