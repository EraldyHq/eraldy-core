package net.bytle.niofs.http;

import java.util.HashSet;
import java.util.Set;

public class HttpRequestAttributes {


  public static HttpRequestAttribute<String> USER = new HttpRequestAttribute<>() {

    @Override
    public String getName() {
      return "user";
    }

    @Override
    public String getDescription() {
      return "Basic authentication user";
    }

  };
  public static HttpRequestAttribute<String> PASSWORD = new HttpRequestAttribute<>() {

    @Override
    public String getName() {
      return "content-type";
    }

    @Override
    public String getDescription() {
      return "Basic authentication password";
    }

  };

  public static final Set<HttpRequestAttribute<?>> ALL = new HashSet<>();
  static {
    ALL.add(PASSWORD);
    ALL.add(USER);
  }
}
