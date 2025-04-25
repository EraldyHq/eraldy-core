package net.bytle.type;

import net.bytle.exception.InternalException;
import net.bytle.exception.NotAbsoluteException;
import net.bytle.exception.NullValueException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static net.bytle.type.MediaType.TEXT_TYPE;

/**
 * A collection of well known MediaType / Mime
 * and of static constructors
 */
public class MediaTypes {


  static Set<MediaType> standardizeDataTypes;


  static public MediaType BINARY_FILE = new MediaTypeAbs() {

    @Override
    public String getType() {
      return "application";
    }

    @Override
    public String getSubType() {
      return "octet-stream";
    }

    @Override
    public boolean isContainer() {
      return false;
    }

    @Override
    public String getExtension() {
      return "bin";
    }

  };

  // Ubuntu
  // https://stackoverflow.com/questions/18869772/mime-type-for-a-directory
  static public MediaType DIR = new MediaTypeAbs() {

    @Override
    public String getType() {
      return "inode";
    }

    @Override
    public String getSubType() {
      return "directory";
    }

    @Override
    public boolean isContainer() {
      return true;
    }

    @Override
    public String getExtension() {
      return "";
    }

  };

  static public MediaType TEXT_PLAIN = new MediaTypeText() {


    @Override
    public String getSubType() {
      return "plain";
    }

    @Override
    public String getExtension() {
      return MediaTypeExtension.TEXT_EXTENSION;
    }

  };

  // According to http://tools.ietf.org/html/rfc4180
  static public MediaType TEXT_CSV = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "csv";
    }

  };

  static public MediaType TEXT_HTML = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "html";
    }


  };

  static public MediaType TEXT_MD = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "md";
    }

  };
  /**
   * A type used to define relation (in memory data)
   * Tpc
   */
  static public MediaType SQL_RELATION = new MediaTypeAbs() {

    @Override
    public String getType() {
      return "sql";
    }

    @Override
    public String getSubType() {
      return "relation";
    }


  };
  /**
   * A sql file
   */
  static public MediaType TEXT_SQL = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "sql";
    }

  };

  static public MediaType TEXT_CSS = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "css";
    }


  };

  static public MediaType TEXT_JSON = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "json";
    }

  };
  static public MediaType TEXT_JSONL = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "jsonl";
    }

  };

  static public MediaType TEXT_YAML = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "yaml";
    }

    @Override
    public String getExtension() {
      return "yml";
    }
  };

  static public MediaType TEXT_JAVASCRIPT = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "javascript";
    }

    @Override
    public String getExtension() {
      return "js";
    }

  };

  static public MediaType TEXT_XML = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "xml";
    }

  };

  static public MediaType TEXT_EML = new MediaTypeText() {

    @Override
    public String getSubType() {
      return "eml";
    }

  };


  static {
    standardizeDataTypes = new HashSet<>();
    standardizeDataTypes.add(TEXT_EML);
    standardizeDataTypes.add(TEXT_CSS);
    standardizeDataTypes.add(TEXT_CSV);
    standardizeDataTypes.add(TEXT_HTML);
    standardizeDataTypes.add(DIR);
    standardizeDataTypes.add(TEXT_JAVASCRIPT);
    standardizeDataTypes.add(TEXT_JSONL);
    standardizeDataTypes.add(TEXT_JSON);
    standardizeDataTypes.add(TEXT_PLAIN);
    standardizeDataTypes.add(TEXT_MD);
    standardizeDataTypes.add(TEXT_SQL);
    standardizeDataTypes.add(TEXT_XML);
    standardizeDataTypes.add(TEXT_YAML);
  }

  /**
   * @param absolutePath an absolute path
   * @return the media Type
   * @throws NotAbsoluteException if the path is not absolute (important to see if this is a directory media type)
   */
  public static MediaType createFromPath(Path absolutePath) throws NotAbsoluteException {

    if (!absolutePath.isAbsolute()) {
      throw new NotAbsoluteException("The path (" + absolutePath + ") is not absolute, we can't determine it media type");
    }

    /**
     * If this is a directory
     */
    if (Files.isDirectory(absolutePath)) {
      return MediaTypes.DIR;
    }

    /**
     * File System based
     * They need to implement java.nio.file.spi.FileTypeDetector
     */
    String mediaTypeString;
    try {
      /**
       * May be implemented
       */
      mediaTypeString = Files.probeContentType(absolutePath);
      try {
        return createFromMediaTypeString(mediaTypeString);
      } catch (NullValueException e) {
        // mediaTypeString may be null if not detected
      }

    } catch (IOException e) {
      // Log is depend on the type module unfortunately
      // LoggerType.LOGGER.fine("Error while guessing the mime type of (" + path + ") via probeContent", e.getMessage());
    }


    /**
     * Name based
     */
    Path fileName = absolutePath.getFileName();
    if (fileName == null) {
      // file system may not have any name in the path for file
      // (ie http has no directory only file, but they may have no name. Example: https://example.com)
      throw new RuntimeException("The file (" + absolutePath + ") does not have any name");
    }

    /**
     * Extension based
     */
    String fullFileName = fileName.toString();
    int i = fullFileName.lastIndexOf('.');
    String extension;
    if (i != -1) {
      extension = fullFileName.substring(i + 1);
      try {
        return createFromExtension(extension);
      } catch (NullValueException e) {
        // could not happen
        throw new InternalException("This exception should not happen", e);
      }
    }


    /**
     * Name based
     */
    mediaTypeString = URLConnection.guessContentTypeFromName(fileName.toString());
    try {
      return createFromMediaTypeString(mediaTypeString);
    } catch (NullValueException e) {
      // null
    }


    if (!Files.notExists(absolutePath)) {
      return MediaTypes.BINARY_FILE;
    }


    /**
     * Open and guess content
     */

    /**
     * BufferedInputStream was chosen because it supports marks
     * Otherwise it does not work
     */
    try (InputStream is = new BufferedInputStream(Files.newInputStream(absolutePath))) {
      mediaTypeString = URLConnection.guessContentTypeFromStream(is);
      if (mediaTypeString != null) {
        return createFromMediaTypeString(mediaTypeString);
      }
    } catch (Exception e) {
      /**
       *
       * We may get an error it this is a http url and there is no basic authentication property
       * yet set
       */
      LoggerType.LOGGER.fine("Error while guessing the mime type of (" + absolutePath + ") via content reading", e.getMessage());

    }


    // Unknown
    return MediaTypes.BINARY_FILE;

  }

  /**
   * In a email content mime may be
   * text/plain; charset=utf-8
   */
  public static String getMediaTypeFromMimeType(String value) {

    int firstComma = value.indexOf(";");
    if (firstComma != -1) {
      return value.substring(0, firstComma);
    }
    return value;
  }

  /**
   * @param value a mime from an email
   * @return the media type without any character set
   */
  public static MediaType createFromMimeValue(String value) throws NullValueException {

    String mediaType = getMediaTypeFromMimeType(value);
    return createFromMediaTypeString(mediaType);

  }

  public static MediaType createFromExtension(String fileExtension) throws NullValueException {

    if (fileExtension == null) {
      throw new NullValueException();
    }
    return createFromMediaTypeString("application/" + fileExtension);

  }

  /**
   * @param mediaTypeString the media type string
   * @return a media type
   */
  public static MediaType createFromMediaTypeString(String mediaTypeString) throws NullValueException {

    if (mediaTypeString == null) {
      throw new NullValueException();
    }

    /**
     * Delete character set if any
     */
    mediaTypeString = getMediaTypeFromMimeType(mediaTypeString);

    /**
     * Processing
     */
    int endIndex = mediaTypeString.indexOf("/");
    mediaTypeString = mediaTypeString.toLowerCase(Locale.ROOT);

    String type;
    String subType;
    if (endIndex != -1) {
      type = mediaTypeString.substring(0, endIndex);
      subType = mediaTypeString.substring(endIndex + 1);
    } else {
      type = null;
      subType = mediaTypeString;
    }

    /**
     * Special case when the user enter text or txt
     */
    if (type == null && (subType.equalsIgnoreCase(TEXT_TYPE) || subType.equalsIgnoreCase("txt"))) {
      return TEXT_PLAIN;
    }

    MediaType mediaTypeObj = new MediaTypeAbs() {

      @Override
      public String getSubType() {
        return subType;
      }

      @Override
      public String getType() {
        return type;
      }

    };

    MediaType sameSubtype = null;

    for (MediaType mediaType : standardizeDataTypes) {
      if (mediaTypeString.equals(mediaType.toString())) {
        return mediaType;
      }
      if (
        mediaTypeObj.getSubType().equals(mediaType.getSubType()) ||
          mediaTypeObj.getExtension().equals(mediaType.getExtension())
      ) {
        sameSubtype = mediaType;
      }
    }

    if (sameSubtype != null) {
      return sameSubtype;
    }

    return mediaTypeObj;


  }

  /**
   * A function to be used in static construction variable
   *
   * @param s the media type
   * @return a Media Type
   */
  public static MediaType createFromMediaTypeNonNullString(String s) {
    try {
      return createFromMediaTypeString(s);
    } catch (NullValueException e) {
      throw new InternalException("This function should not be filled with a null value");
    }
  }


}
