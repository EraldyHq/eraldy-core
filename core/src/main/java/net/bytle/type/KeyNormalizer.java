package net.bytle.type;

import net.bytle.log.Logs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A key name normalizer
 */
public class KeyNormalizer {


  private final String stringOrigin;


  /**
   * @param stringOrigin - the string to normalize
   */
  KeyNormalizer(String stringOrigin) {

    this.stringOrigin = stringOrigin;

  }

  /**
   * @param key - the name key to normalize
   *            This normalizer accepts all cases.
   *            It will split the key in words that are separated
   *            by separators characters (not letter or digit)
   *            by uppercase letter (if not preceded by another uppercase character to handle UPPER_SNAKE_CASE)
   *            The words can then be printed/normalized into a {@link KeyCase}
   */
  public static KeyNormalizer create(Object key) {
    return new KeyNormalizer(key.toString());
  }


  public String toCamelCase() {
    return this
      .toParts(this.stringOrigin)
      .stream()
      .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
      .collect(Collectors.joining());
  }


  /**
   * @return the parts of a string in lowercase
   */
  private List<String> toParts(String s) {

    List<String> parts = new ArrayList<>();
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
    return parts;
  }

  /**
   * @return a name in event case. ie camel case with a space between words
   */
  public String toHandleCase() {
    return this
      .toParts(this.stringOrigin)
      .stream()
      .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
      .collect(Collectors.joining(" "));
  }

  /**
   * @return the words in a Snake Case (ie user_count)
   */
  public String toSnakeCase() {
    return toSnakeCase(this.toParts(this.stringOrigin));
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
      .map(String::toLowerCase)
      .collect(Collectors.joining("_"));
  }

  /**
   * @return the words in a Upper Snake Case (ie USER_COUNT)
   */
  public String toUpperSnakeCase() {
    return toUpperSnakeCase(this.toParts(this.stringOrigin));
  }

  /**
   * @return the words in a Snake Case (ie user_count)
   * conforming to chapter 5.4 of ANSI 1992
   * Non-conforming letters are transformed into an underscore
   * The first character is transformed to `a` if it's not a latin letter
   */
  public String toSqlCase() {
    return toSnakeCase(this.toParts(this.toSqlName(this.stringOrigin)));
  }

  private String toSqlName(String sqlName) {
    char firstChar = sqlName.charAt(0);
    if (!String.valueOf(firstChar).matches("[a-zA-Z]")) {
      // throw new IllegalArgumentException("Name ("+sqlName+") is not valid for sql as it should start with a Latin letter (a-z, A-Z), not "+firstChar);
      firstChar = 'a';
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
  public String toSqlName() {
    return toSqlName(this.stringOrigin);
  }

  /**
   * @return the words in an Upper Snake Case (ie USER_COUNT)
   * Old case that conflicts with shouting.
   */
  @SuppressWarnings("unused")
  public String toUpperSqlCase() {
    return this.toSqlCase().toUpperCase();
  }


  @SuppressWarnings("unused")
  public String toCase(KeyCase keyCase) {
    switch (keyCase) {
      case HANDLE:
        return toHandleCase();
      case CAMEL:
        return toCamelCase();
      case HYPHEN:
        return toHyphenCase();
      case FILE:
        return toFileCase();
      case SNAKE:
        return toSnakeCase();
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
      .toParts(this.stringOrigin)
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
   * @return {@link #toHyphenCase()}
   */
  @Override
  public String toString() {
    return this.toHyphenCase();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    KeyNormalizer that = (KeyNormalizer) o;
    return Objects.equals(this.toParts(this.stringOrigin), that.toParts(that.stringOrigin));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.toParts(this.stringOrigin));
  }

  /**
   * @return only the first letter of each word concatenated
   * This is not posix compliant
   */
  public String toCliShortOptionName() {
    return this
      .toParts(this.stringOrigin)
      .stream()
      .map(s -> String.valueOf(s.charAt(0)).toLowerCase())
      .collect(Collectors.joining());
  }

}
