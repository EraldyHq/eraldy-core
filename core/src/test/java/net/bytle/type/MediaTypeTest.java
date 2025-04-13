package net.bytle.type;

import net.bytle.exception.NullValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MediaTypeTest {

    @Test
    public void parsing() throws NullValueException {

        String validEmailValue = "text/plain; charset=utf-8";
        MediaType mediaType = MediaTypes.createFromMimeValue(validEmailValue);
        Assertions.assertEquals("txt", mediaType.getExtension());

    }

}
