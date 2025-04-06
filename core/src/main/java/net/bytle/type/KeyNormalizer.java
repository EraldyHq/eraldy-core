package net.bytle.type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A key name normalizer
 */
public class KeyNormalizer {


  /**
   * The parts of the name normalized to lowercase in order to support Key equality
   */
  private final List<String> parts = new ArrayList<>();


  /**
   * @param stringOrigin - the string to normalize
   */
  KeyNormalizer(String stringOrigin) {


    StringBuilder currentWord = new StringBuilder();

    /*
     * To handle UPPER SNAKE CASE
     * such as UPPER_SNAKE_CASE
     * We split on a UPPER case character only if the previous character is not
     */
    boolean previousCharacterIsNotUpperCase = false;
    for (char c : stringOrigin.toCharArray()) {

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

  }

  /**
   *
   * @param key - the name key to normalize
   * This normalizer accepts all cases.
   * It will split the key in words that are separated
   *            by separators characters (not letter or digit)
   *            by uppercase letter (if not preceded by another uppercase character to handle UPPER_SNAKE_CASE)
   * The words can then be printed/normalized into a {@link KeyCase}
   */
  public static KeyNormalizer create(Object key) {
    return new KeyNormalizer(key.toString());
  }


  public String toCamelCase() {
    return this.parts.stream()
      .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
      .collect(Collectors.joining());
  }

  /**
   *
   * @return a name in event case. ie camel case with a space between words
   */
  public String toHandleCase() {
    return this.parts.stream()
      .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
      .collect(Collectors.joining(" "));
  }

  /**
   * @return the words in a Snake Case (ie user_count)
   */
  public String toSnakeCase() {
    return this.parts.stream()
      .map(String::toLowerCase)
      .collect(Collectors.joining("_"));
  }

  /**
   * @return the words in a Upper Snake Case (ie USER_COUNT)
   */
  public String toUpperSnakeCase() {
    return this.parts.stream()
      .map(String::toLowerCase)
      .collect(Collectors.joining("_"));
  }

  /**
   * @return the words in a Snake Case (ie user_count)
   */
  public String toSqlCase() {
    return this.toSnakeCase();
  }

  /**
   * @return the words in an Upper Snake Case (ie USER_COUNT)
   * Old case that conflicts with shouting.
   */
  @SuppressWarnings("unused")
  public String toUpperSqlCase() {
    return this.toUpperSnakeCase();
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
    return this.parts.stream()
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
   *
   * @return a name that can be used as file name in the file system (ie the {@link #toHyphenCase()}
   */
  public String toFileCase() {
    return toHyphenCase();
  }

  /**
   *
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
    return Objects.equals(parts, that.parts);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(parts);
  }

}
