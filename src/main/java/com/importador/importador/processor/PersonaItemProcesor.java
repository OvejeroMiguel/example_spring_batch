package com.importador.importador.processor;

import com.importador.importador.model.Persona;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class PersonaItemProcesor implements ItemProcessor<Persona, Persona> {
    private static final Logger LOG = LoggerFactory.getLogger(PersonaItemProcesor.class);

    @Override
    public Persona process(Persona persona) throws Exception {
        String primerNombre = persona.getPrimerNombre().toUpperCase();
        String segundoNombre = persona.getSegundoNombre().toUpperCase();
        String telefono = persona.getTelefono();

        Persona persona1 = new Persona(primerNombre, segundoNombre, telefono);

        LOG.info("Convirtiendo (" + persona + ") a (" + persona1 + ")");
        return persona1;
    }


}
