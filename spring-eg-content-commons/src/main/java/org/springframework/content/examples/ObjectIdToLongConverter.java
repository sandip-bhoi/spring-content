package org.springframework.content.examples;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;

public class ObjectIdToLongConverter implements Converter<ObjectId, Long> {

	@Override
	public Long convert(ObjectId source) {
		return Long.parseLong(Long.toString(source.getTime()) + 
							  Integer.toString(source.getMachine()) + 
							  Integer.toString(source.getInc()));
	}

}
