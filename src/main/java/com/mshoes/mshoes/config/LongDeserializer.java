package com.mshoes.mshoes.config;

import java.io.IOException;
import java.io.Serial;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class LongDeserializer extends StdDeserializer<Long> {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	public LongDeserializer() {
		this(null);
	}

	@Override
	public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		JsonNode node = p.getCodec().readTree(p);
		if (node == null || node.isNull()) {
			return null;
		}
		if (node.isNumber()) {
			return node.longValue();
		}
		throw new JsonParseException(p,
				"Cannot deserialize value of type `long` from Object value (token `JsonToken.START_OBJECT`)");

	}

	public LongDeserializer(Class<?> vc) {
		super(vc);
	}

}
