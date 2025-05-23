package net.bytle.type;


/**
 * The media type defines the content type:
 * * for a file system (web): mime type (ie csv, ...)
 * * for a memory system: list / queue / gen
 * * for a relational system: table, view, query
 * <p></p>
 * This is an interface to be able to create enum
 * <p></p>
 * When checking for equality, you need to check the equality on the `toString`
 * method if the enum class are not the same
 *
 * <p>
 * The name was taken from the term `Internet media type`
 * from the mime specification.
 * <a href="http://www.iana.org/assignments/media-types/media-types.xhtml">MediaType</a>
 */
public interface MediaType {

  String TEXT_TYPE = "text";

  /**
   * @return the format (plain, jpeg, mpeg, ...)
   * This is generally also the file extension
   */
  String getSubType();

  /**
   * @return the top-level type (text, image, video, audio, application)
   * This is a file type category
   */
  String getType();

  /**
   * @return true if this is a container of object (directory, schema, catalog, ...)
   * ie an object without any content
   */
  boolean isContainer();

  /**
   * @return the file extension (by default the {@link #getSubType() subtype}
   */
  String getExtension();


  /**
   * Utility
   * @return true if this media type is a text file
   */
  default Boolean isText() {

    return getType().equals(TEXT_TYPE);

  }


}
