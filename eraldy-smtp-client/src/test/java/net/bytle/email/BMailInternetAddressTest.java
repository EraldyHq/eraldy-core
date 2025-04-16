package net.bytle.email;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import net.bytle.type.DnsName;
import net.bytle.type.EmailAddress;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BMailInternetAddressTest {

  @Test
  public void basicEmailTest() throws AddressException {

    final String localPart = "CameronFrayne53";
    final String rootDomain = "example.com";
    final String domain = "blog." + rootDomain;
    BMailInternetAddress bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + domain);
    EmailAddress emailAddress = bmailInternetAddress.getEmailAddress();
    DnsName domainName = emailAddress.getDomainName();
    Assert.assertEquals("domain", domain, domainName.toStringWithoutRoot());
    Assert.assertEquals("local-part", localPart, emailAddress.getLocalPart());
    Assert.assertEquals("apex domain", rootDomain, domainName.getApexName().toStringWithoutRoot());
    List<String> parts = domainName.getLabels();
    Assert.assertEquals("3 domain parts", 3, parts.size());
    Assert.assertEquals("local part digit count", (Integer) 2, emailAddress.getLocalPartDigitCount());

  }

  @Test
  public void rootDomainTest() throws AddressException {

    final String localPart = "nico";
    final String apexDomain = "example.com";

    // RootDomain
    BMailInternetAddress bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + apexDomain);
    Assert.assertEquals("root domain", apexDomain, bmailInternetAddress.getEmailAddress().getDomainName().getApexName().toStringWithoutRoot());

    final String subDomain = "blog." + apexDomain;
    bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + subDomain);
    Assert.assertEquals("root domain from SubDomain", apexDomain, bmailInternetAddress.getEmailAddress().getDomainName().getApexName().toStringWithoutRoot());

    final String subSubDomain = "sub.blog." + apexDomain;
    bmailInternetAddress = BMailInternetAddress.of(localPart + "@" + subSubDomain);
    Assert.assertEquals("root domain from subSubDomain", apexDomain, bmailInternetAddress.getEmailAddress().getDomainName().getApexName().toStringWithoutRoot());


  }

  @Test
  public void internetAddressRfc822Test() throws AddressException {

    String address = "youpla@golo.com";
    String personal = "Foo Bar";
    String rfc822Type = "rfc822"; // fix value

    /**
     * Zero format
     */
    InternetAddress internetAddress = new InternetAddress(address);
    Assert.assertEquals(address, internetAddress.getAddress());
    Assert.assertNull(internetAddress.getPersonal());
    Assert.assertEquals(rfc822Type, internetAddress.getType());
    Assert.assertFalse(internetAddress.isGroup());
    Assert.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();

    /**
     * First format
     */
    String emailAddress = personal + " <" + address + "> (A comment)";
    internetAddress = new InternetAddress(emailAddress);
    Assert.assertEquals(address, internetAddress.getAddress());
    Assert.assertEquals(personal, internetAddress.getPersonal());
    Assert.assertEquals(rfc822Type, internetAddress.getType());
    Assert.assertFalse(internetAddress.isGroup());
    Assert.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();

    /**
     * Second format
     */
    emailAddress = " " + address + " (" + personal + ")";
    internetAddress = new InternetAddress(emailAddress);
    Assert.assertEquals(address, internetAddress.getAddress());
    Assert.assertEquals(personal, internetAddress.getPersonal());
    Assert.assertEquals(rfc822Type, internetAddress.getType());
    Assert.assertFalse(internetAddress.isGroup());
    Assert.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();


    /**
     * Third format
     */
    address = "user@[10.9.8.7]";
    internetAddress = new InternetAddress(address);
    Assert.assertEquals(address, internetAddress.getAddress());
    Assert.assertNull(internetAddress.getPersonal());
    Assert.assertEquals(rfc822Type, internetAddress.getType());
    Assert.assertFalse(internetAddress.isGroup());
    Assert.assertNull(internetAddress.getGroup(false));
    internetAddress.validate();

  }

  @Test
  public void internetAddressValidateRfc822() throws AddressException {

    InternetAddress internetAddress = new InternetAddress("youpla@golo.com");
    internetAddress.validate();

    internetAddress = new InternetAddress("youpla@golo");
    internetAddress.validate();


    try {
      new InternetAddress("@golo");
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assert.assertEquals("Missing local name", e.getMessage());
    }

    try {
      new InternetAddress("youpla@");
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assert.assertEquals("Missing domain", e.getMessage());
    }

    try {
      internetAddress = new InternetAddress("youpla");
      Assert.assertEquals("youpla", internetAddress.getAddress());
      Assert.assertNull(internetAddress.getPersonal());
      internetAddress.validate();
    } catch (AddressException e) {
      Assert.assertEquals("Missing final '@domain'", e.getMessage());
    }

  }

  @Test
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
    Assert.assertEquals("Address is the whole string", groupAddress, groupedInternetAddress.getAddress());
    Assert.assertNull("Personal is null", groupedInternetAddress.getPersonal());
    InternetAddress[] group = groupedInternetAddress.getGroup(false);
    Assert.assertTrue(groupedInternetAddress.isGroup());
    Assert.assertEquals(3, group.length);
    // first
    InternetAddress internetAddress1 = group[0];
    Assert.assertEquals("Foo@localhost", internetAddress1.getAddress());
    Assert.assertEquals("Foo", internetAddress1.getPersonal());
    internetAddress1.validate();
    // second
    InternetAddress internetAddress2 = group[1];
    Assert.assertEquals("bar@example.com", internetAddress2.getAddress());
    Assert.assertEquals("Name", internetAddress2.getPersonal());
    internetAddress2.validate();

    // third
    InternetAddress internetAddress3 = group[2];
    Assert.assertEquals("Barney", internetAddress3.getAddress());
    Assert.assertNull("Personal is null", internetAddress3.getPersonal());
    try {
      internetAddress3.validate();
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assert.assertEquals("Missing final '@domain'", e.getMessage());
    }

  }

  @Test
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
      Assert.assertEquals("Local address starts with dot", e.getMessage());
    }

    // no final domain
    // same with `abc`
    emailString = "com.";
    try {
      BMailInternetAddress.of(emailString);
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assert.assertEquals("Missing final '@domain'", e.getMessage());
    }

    // no tld
    emailString = "com@com";
    try {
      BMailInternetAddress.of(emailString);
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assert.assertEquals("The domain should have at minimal a TLD domain (.com, ...)", e.getMessage());
    }

    emailString = "user@[10.9.8.7]";
    try {
      BMailInternetAddress.of(emailString);
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assert.assertEquals("The domain should not start with a [", e.getMessage());
    }

    emailString = "user@10.9.8.7";
    try {
      BMailInternetAddress.of(emailString);
      throw new RuntimeException("Should throw");
    } catch (AddressException e) {
      Assert.assertEquals("A domain part should not be a number. The part (10) is a number.", e.getMessage());
    }


  }

  @Test
  public void bounceReversePathTest() throws AddressException {
    String domain = "forward.domain.net";
    String local = "SRS0=155e=FQ=gmail.com=gerardnico";
    BMailInternetAddress email = BMailInternetAddress.of("<" + local + "@" + domain + ">");
    Assert.assertEquals(domain, email.getEmailAddress().getDomainName().toStringWithoutRoot());
    Assert.assertEquals(local, email.getEmailAddress().getLocalPart());
  }



}
