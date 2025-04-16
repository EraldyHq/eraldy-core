package net.bytle.email;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import net.bytle.type.DnsName;
import net.bytle.type.EmailAddress;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BMailInternetAddressTest {

  @Test()
  public void basicEmailTest() throws AddressException {

    final String localPart = "CameronFrayne53";
    final String rootDomain = "example.com";
    final String domain = "blog." + rootDomain;
    BMailInternetAddress bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + domain);
    EmailAddress emailAddress = bmailInternetAddress.getEmailAddress();
    DnsName domainName = emailAddress.getDomainName();
    Assertions.assertEquals(domain, domainName.toStringWithoutRoot(), "domain");
    Assertions.assertEquals(localPart, emailAddress.getLocalPart(), "local-part");
    Assertions.assertEquals(rootDomain, domainName.getApexName().toStringWithoutRoot(), "apex domain");
    List<String> parts = domainName.getLabels();
    Assertions.assertEquals(3, parts.size(), "3 domain parts");
    Assertions.assertEquals((Integer) 2, emailAddress.getLocalPartDigitCount(), "local part digit count");

  }

  @Test()
  public void rootDomainTest() throws AddressException {

    final String localPart = "nico";
    final String apexDomain = "example.com";

    // RootDomain
    BMailInternetAddress bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + apexDomain);
    Assertions.assertEquals(apexDomain, bmailInternetAddress.getEmailAddress().getDomainName().getApexName().toStringWithoutRoot(), "root domain");

    final String subDomain = "blog." + apexDomain;
    bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + subDomain);
    Assertions.assertEquals(apexDomain, bmailInternetAddress.getEmailAddress().getDomainName().getApexName().toStringWithoutRoot(), "root domain from SubDomain");

    final String subSubDomain = "sub.blog." + apexDomain;
    bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + subSubDomain);
    Assertions.assertEquals(apexDomain, bmailInternetAddress.getEmailAddress().getDomainName().getApexName().toStringWithoutRoot(), "root domain from subSubDomain");


  }

  @Test()
  public void internetAddressRfc822Test() throws AddressException {

    String address = "youpla@golo.com";
    String personal = "Foo Bar";
    String rfc822Type = "rfc822"; // fix value

    /**
     * Zero format
     */
    InternetAddress internetAddress = new InternetAddress(address);
    Assertions.assertEquals(address, internetAddress.getAddress());
    Assertions.assertNull(internetAddress.getPersonal());
    Assertions.assertEquals(rfc822Type, internetAddress.getType());
    Assertions.assertFalse(internetAddress.isGroup());
    Assertions.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();

    /**
     * First format
     */
    String emailAddress = personal + " <" + address + "> (A comment)";
    internetAddress = new InternetAddress(emailAddress);
    Assertions.assertEquals(address, internetAddress.getAddress());
    Assertions.assertEquals(personal, internetAddress.getPersonal());
    Assertions.assertEquals(rfc822Type, internetAddress.getType());
    Assertions.assertFalse(internetAddress.isGroup());
    Assertions.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();

    /**
     * Second format
     */
    emailAddress = " " + address + " (" + personal + ")";
    internetAddress = new InternetAddress(emailAddress);
    Assertions.assertEquals(address, internetAddress.getAddress());
    Assertions.assertEquals(personal, internetAddress.getPersonal());
    Assertions.assertEquals(rfc822Type, internetAddress.getType());
    Assertions.assertFalse(internetAddress.isGroup());
    Assertions.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();


    /**
     * Third format
     */
    address = "user@[10.9.8.7]";
    internetAddress = new InternetAddress(address);
    Assertions.assertEquals(address, internetAddress.getAddress());
    Assertions.assertNull(internetAddress.getPersonal());
    Assertions.assertEquals(rfc822Type, internetAddress.getType());
    Assertions.assertFalse(internetAddress.isGroup());
    Assertions.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();

  }

  @Test()
  public void internetAddressValidateRfc822() throws AddressException {

    InternetAddress internetAddress = new InternetAddress("youpla@golo.com");
    internetAddress.validate();

    internetAddress = new InternetAddress("youpla@golo");
    internetAddress.validate();


    try {
      new InternetAddress("@golo");
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assertions.assertEquals("Missing local name", e.getMessage());
    }

    try {
      new InternetAddress("youpla@");
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assertions.assertEquals("Missing domain", e.getMessage());
    }

    try {
      internetAddress = new InternetAddress("youpla");
      Assertions.assertEquals("youpla", internetAddress.getAddress());
      Assertions.assertNull(internetAddress.getPersonal());
      internetAddress.validate();
    } catch (AddressException e) {
      Assertions.assertEquals("Missing final '@domain'", e.getMessage());
    }

  }

  @Test()
  public void internetAddressGroupRfc822Test() throws AddressException {
    /**
     * Email fields are Strings using the common formats for email with or without real name
     * <p>
     * username@example.com
     * username@example.com (Firstname Lastname)
     * Firstname Lastname <username@example.com>
     */
    String groupName = "my group";
    //String groupAddress = groupName + ":foo@bar," + emailAddress + ";";
    String groupAddress = groupName + ": \"Foo\" <Foo@localhost> (A comment), bar@example.com (Name), Barney;";
    InternetAddress groupedInternetAddress = new InternetAddress(groupAddress);
    Assertions.assertEquals(groupAddress, groupedInternetAddress.getAddress(), "Address is the whole string");
    Assertions.assertNull(groupedInternetAddress.getPersonal(), "Personal is null");
    InternetAddress[] group = groupedInternetAddress.getGroup(false);
    Assertions.assertTrue(groupedInternetAddress.isGroup());
    Assertions.assertEquals(3, group.length);
    // first
    InternetAddress internetAddress1 = group[0];
    Assertions.assertEquals("Foo@localhost", internetAddress1.getAddress());
    Assertions.assertEquals("Foo", internetAddress1.getPersonal());
    internetAddress1.validate();
    // second
    InternetAddress internetAddress2 = group[1];
    Assertions.assertEquals("bar@example.com", internetAddress2.getAddress());
    Assertions.assertEquals("Name", internetAddress2.getPersonal());
    internetAddress2.validate();

    // third
    InternetAddress internetAddress3 = group[2];
    Assertions.assertEquals("Barney", internetAddress3.getAddress());
    Assertions.assertNull(internetAddress3.getPersonal(), "Personal is null");
    try {
      internetAddress3.validate();
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assertions.assertEquals("Missing final '@domain'", e.getMessage());
    }

  }

  @Test()
  public void externalEmailValidation() {

    // no tld
    String emailString = "com@com.com";

    try {
      BMailInternetAddress.of(emailString);
    } catch (AddressException e) {
      throw new RuntimeException("Should not throw");
    }

    // Address should not start with a dot
    emailString = ".";
    try {
      BMailInternetAddress.of(emailString);
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assertions.assertEquals("Local address starts with dot", e.getMessage());
    }

    // no final domain
    // same with `abc`
    emailString = "com.";
    String finalEmailString = emailString;
    Assertions.assertThrows(
      AddressException.class,
      ()->BMailInternetAddress.of(finalEmailString),
      "Missing final '@domain'"
    );



    // no tld
    emailString = "com@com";
    try {
      BMailInternetAddress.of(emailString);
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assertions.assertEquals("The domain should have at minimal a TLD domain (.com, ...)", e.getMessage());
    }

    emailString = "user@[10.9.8.7]";
    try {
      BMailInternetAddress.of(emailString);
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assertions.assertEquals("The domain should not start with a [", e.getMessage());
    }

    emailString = "user@10.9.8.7";
    String finalEmailString1 = emailString;
    Assertions.assertThrows(
      AddressException.class,
      ()->BMailInternetAddress.of(finalEmailString1),
      "A domain part should not be a number. The part (10) is a number."
    );



  }

  @Test()
  public void bounceReversePathTest() throws AddressException {
    String domain = "forward.domain.net";
    String local = "SRS0=155e=FQ=gmail.com=gerardnico";
    BMailInternetAddress email = BMailInternetAddress.of("<" + local + "@" + domain + ">");
    Assertions.assertEquals(domain, email.getEmailAddress().getDomainName().toStringWithoutRoot());
    Assertions.assertEquals(local, email.getEmailAddress().getLocalPart());
  }



}
