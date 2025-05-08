package net.bytle.type;

import net.bytle.exception.CastException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A key name normalizer
 */
@SuppressWarnings("unused")
public class KeyNormalizer implements Comparable<KeyNormalizer> {


  private final String stringOrigin;

  /**
   * A map of the string parsed and the parts in a list
   * We may parse for different string.
   * For instance, when we normalize for {@link #toSqlCase()}
   */
  private final Map<String, List<String>> partsByString = new HashMap<>();

  /**
   * @param name - the string to normalize
   *             Note that
   *             * environment variable may start with _
   *             * cli option and flag may start with -
   *             therefore they will be
   * @throws CastException if the name does have any letter or digit
   */
  KeyNormalizer(String name) throws CastException {

    this.stringOrigin = name;
    toParts(this.stringOrigin);

  }

  /**
   * @param key - the name key to normalize
   *            This normalizer accepts all cases.
   *            It will split the key in words that are separated
   *            by separators characters (not letter or digit)
   *            by uppercase letter (if not preceded by another uppercase character to handle UPPER_SNAKE_CASE)
   *            The words can then be printed/normalized into a {@link KeyCase}
   * @throws CastException when the key is null, does not have any letter or digit
   */
  public static KeyNormalizer create(Object key) throws CastException {
    if (key == null) {
      throw new CastException("The key should not be null");
    }
    if (key instanceof KeyNormalizer) {
      return (KeyNormalizer) key;
    }
    return new KeyNormalizer(key.toString());
  }

  /**
   * Same as {@link #create(Object)} but with a runtime exception
   * To use when the key is known in advance to have letters and digits
   */
  public static KeyNormalizer createSafe(Object key) {
    try {
      return create(key);
    } catch (CastException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    }
  }

  /**
   * @return the words in a camel case (ie UserCount)
   */
  public String toCamelCase() {
    return this
      .toPartsFromOriginalString()
      .stream()
      .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
      .collect(Collectors.joining());
  }

  /**
   * A utility class that does not throw
   *
   * @return the parts of the original string
   */
  private List<String> toPartsFromOriginalString() {
    try {
      return toParts(this.stringOrigin);
    } catch (CastException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * @param s - normally the {@link #stringOrigin} but it may be first process to
   *          normalize the string to a valid name before. Example: {@link #toSqlCase()}
   * @return the parts of a string in lowercase
   */
  private List<String> toParts(String s) throws CastException {

    List<String> parts = partsByString.get(s);
    if (parts != null) {
      return parts;
    }
    parts = new ArrayList<>();
    partsByString.put(s, parts);

    StringBuilder currentWord = new StringBuilder();
    /*
     * To handle UPPER SNAKE CASE
     * such as UPPER_SNAKE_CASE
     * We split on a UPPER case character only if the previous character is not
     */
    boolean previousCharacterIsNotUpperCase = false;

    for (char c : s.toCharArray()) {

      // Separator (ie whitespace, comma, dollar, underscore, ...)
      boolean isCharacterSeparator = Character.isWhitespace(c) || !Character.isLetterOrDigit(c);
      boolean currentCharacterIsUpperCase = Character.isUpperCase(c);
      /*
       * Separate on Uppercase if the previous character is not UPPER Case
       * For example: to separate UPPER_CASE key in 2 words UPPER and CASE
       */
      boolean separateOnCase = currentCharacterIsUpperCase && previousCharacterIsNotUpperCase;
      if (isCharacterSeparator || separateOnCase) {
        if (currentWord.length() > 0) {
          parts.add(currentWord.toString().toLowerCase());
          currentWord.setLength(0);
        }
      }
      /*
       * End
       */
      previousCharacterIsNotUpperCase = !currentCharacterIsUpperCase;
      if (isCharacterSeparator) {
        // we don't collect character separator
        continue;
      }
      currentWord.append(c);
    }

    if (currentWord.length() > 0) {
      parts.add(currentWord.toString().toLowerCase());
    }
    if (parts.isEmpty()) {
      throw new CastException("The key value (" + stringOrigin + ") after normalization is empty, It does not have any letter or digits.");
    }
    return parts;
  }

  /**
   * @return a name in event case. ie camel case with a space between words
   */
  public String toHandleCase() {
    return this
      .toPartsFromOriginalString()
      .stream()
      .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
      .collect(Collectors.joining(" "));
  }

  /**
   * @return the words in a Snake Case (ie user_count)
   */
  public String toSnakeCase() {
    return toSnakeCase(this.toPartsFromOriginalString());
  }

  private String toSnakeCase(List<String> parts) {
    return parts
      .stream()
      .map(String::toLowerCase)
      .collect(Collectors.joining("_"));
  }

  private String toUpperSnakeCase(List<String> parts) {
    return parts
      .stream()
      .map(String::toUpperCase)
      .collect(Collectors.joining("_"));
  }

  /**
   * @return the words in a Upper Snake Case (ie USER_COUNT)
   */
  public String toUpperSnakeCase() {
    return toUpperSnakeCase(this.toPartsFromOriginalString());
  }

  /**
   * Same as {@link #toSqlCase()} but without compile exception
   * Use it when you are sure that the name starts with a letter
   */
  public String toSqlCaseSafe() {

    try {
      return toSqlCase();
    } catch (CastException e) {
      throw new RuntimeException(e.getMessage(), e);
    }

  }

  /**
   * @return the words in a Snake Case (ie user_count)
   * conforming to chapter 5.4 of ANSI 1992
   * Non-conforming letters are transformed into an underscore
   * The first character is transformed to `a` if it's not a latin letter
   * @throws CastException if the name does not start with a letter
   */
  public String toSqlCase() throws CastException {

    return toSnakeCase(this.toParts(this.toSqlName()));

  }

  private String toSqlName(String sqlName) throws CastException {
    char firstChar = sqlName.charAt(0);
    if (!String.valueOf(firstChar).matches("[a-zA-Z]")) {
      throw new CastException("Name (" + sqlName + ") is not valid for sql as it should start with a Latin letter (a-z, A-Z), not " + firstChar);

    }
    // Replace non-conforming characters with underscores
    StringBuilder sanitized = new StringBuilder();
    sanitized.append(firstChar);

    for (int i = 1; i < sqlName.length(); i++) {
      char c = sqlName.charAt(i);
      // valid char
      if (String.valueOf(c).matches("[a-zA-Z0-9_]")) {
        sanitized.append(c);
      } else {
        sanitized.append('_');
      }
    }

    return sanitized.toString();
  }

  /**
   * @return normalize a string to a valid SQL Name
   */
  public String toSqlName() throws CastException {
    return toSqlName(this.stringOrigin);
  }

  /**
   * @return the words in an Upper Snake Case (ie USER_COUNT)
   * Old case that conflicts with shouting.
   */
  @SuppressWarnings("unused")
  public String toUpperSqlCase() throws CastException {
    return this.toSqlCase().toUpperCase();
  }


  public String toCaseSafe(KeyCase keyCase) {
    try {
      return toCase(keyCase);
    } catch (CastException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * @throws CastException if the name does not correspond to the case (ie only use for sql where the name should start with a letter)
   */
  public String toCase(KeyCase keyCase) throws CastException {
    switch (keyCase) {
      case HANDLE:
        return toHandleCase();
      case CAMEL:
        return toCamelCase();
      case HYPHEN:
      case KEBAB:
        return toHyphenCase();
      case FILE:
        return toFileCase();
      case SNAKE:
        return toSnakeCase();
      case SNAKE_UPPER:
        return toUpperSnakeCase();
      case SQL:
        return toSqlCase();
      default:
        throw new IllegalArgumentException("The word-case (" + keyCase + ") is unknown");
    }
  }


  /**
   * @return the words in a Hyphen Case (ie user-count)
   * Aeries of lowercase name separated by a minus (used by the command line and in HTML template variable)
   */
  public String toHyphenCase() {
    return this
      .toPartsFromOriginalString()
      .stream()
      .map(String::toLowerCase)
      .collect(Collectors.joining("-"));
  }

  /**
   * @return the long option name used in cli (ie {@link #toHyphenCase()}
   */
  public String toCliLongOptionName() {
    return toHyphenCase();
  }

  /**
   * @return a name that can be used as file name in the file system (ie the {@link #toHyphenCase()}
   */
  public String toFileCase() {
    return toHyphenCase();
  }

  /**
   * @return the string origin
   */
  @Override
  public String toString() {
    return this.stringOrigin;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    KeyNormalizer that = (KeyNormalizer) o;
    return Objects.equals(this.toPartsFromOriginalString(), that.toPartsFromOriginalString());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.toPartsFromOriginalString());
  }

  /**
   * @return only the first letter of each word concatenated
   * This is not posix compliant
   */
  public String toCliShortOptionName() {
    return this
      .toPartsFromOriginalString()
      .stream()
      .map(s -> String.valueOf(s.charAt(0)).toLowerCase())
      .collect(Collectors.joining());
  }

  /**
   * @return a css compliant name in {@link #toHyphenCase()}
   */
  public String toCssPropertyName() {
    return toHyphenCase();
  }

  /**
   * @return a standard case for java system property ie `user.count`
   * There is no default but this is the most used
   * Example: "web.environment", "vertx.web.environment"
   */
  public String toJavaSystemPropertyName() {
    return this
      .toPartsFromOriginalString()
      .stream()
      .map(String::toLowerCase)
      .collect(Collectors.joining("."));
  }

  /**
   * @return a html attribute name compliant case in {@link #toHyphenCase()}
   */
  public String toHtmlAttributeName() {
    return toHyphenCase();
  }

  /**
   * @return kebab case (ie user-count). Alias for {@link #toHyphenCase()}
   * The name comes from the similarity of the words to meat on a kebab skewer.
   * <a href="https://developer.mozilla.org/en-US/docs/Glossary/Kebab_case">...</a>
   */
  public String toKebabCase() {
    return toHyphenCase();
  }

  /**
   * @return env name (ie USER_NAME)
   */
  @SuppressWarnings("unused")
  public String toEnvName() {
    return this
      .toPartsFromOriginalString()
      .stream()
      .map(String::toUpperCase)
      .collect(Collectors.joining("_"));
  }

  /**
   * @return the words, parts of the name in lowercase to implement your own case
   */
  public List<String> getParts() {
    return toPartsFromOriginalString();
  }

  @Override
  public int compareTo(KeyNormalizer o) {
    return this.stringOrigin.compareTo(o.stringOrigin);
  }

}
