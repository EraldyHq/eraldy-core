package net.bytle.exception;

/**
 * Fighting against NULL.
 * Uses this instead of returning null
 */
public class NotLoggedInException extends RuntimeException {

  public NotLoggedInException() {
  }

}
