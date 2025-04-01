package net.bytle.type;

import net.bytle.exception.NullValueException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MediaTypeTest {

  @Test
  public void parsing() throws NullValueException {

    String validEmailValue = "text/plain; charset=utf-8";
    MediaType mediaType = MediaTypes.createFromMimeValue(validEmailValue);
    assertThat(mediaType.getExtension(),is("txt"));

  }

}
