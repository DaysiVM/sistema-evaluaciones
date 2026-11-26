package com.universidad.tests.dto.request;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;

/** Conserva el tipo string del contrato sin convertir numeros o booleanos en credenciales. */
public final class TextoSolicitudDeserializer extends StdScalarDeserializer<String> {

    public TextoSolicitudDeserializer() {
        super(String.class);
    }

    @Override
    public String deserialize(JsonParser parser, DeserializationContext contexto)
            throws IOException {
        if (!parser.hasToken(JsonToken.VALUE_STRING)) {
            return contexto.reportInputMismatch(String.class, "El campo debe ser texto");
        }
        return parser.getText();
    }
}
