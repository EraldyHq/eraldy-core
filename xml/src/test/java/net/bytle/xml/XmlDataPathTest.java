package net.bytle.xml;

import net.bytle.db.Tabular;
import net.bytle.db.fs.FsConnection;
import net.bytle.db.fs.FsDataPath;
import net.bytle.db.model.ColumnDef;
import net.bytle.db.spi.DataPath;
import net.bytle.db.spi.SelectException;
import net.bytle.db.spi.Tabulars;
import net.bytle.db.stream.SelectStream;
import net.bytle.db.uri.DataUri;
import net.bytle.timer.Timer;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;
import java.util.List;

public class XmlDataPathTest {


  @Test
  public void base() throws SelectException {
    try (Tabular tabular = Tabular.tabularWithCleanEnvironment()) {
      FsConnection resourceConnection = tabular.createRuntimeConnectionForResources(XmlDataPathTest.class, "wikipedia");
      FsDataPath dataPath = resourceConnection.getDataPath("bar.xml");
      Assert.assertTrue(dataPath instanceof XmlDataPath);
      Assert.assertEquals(1, dataPath.getOrCreateRelationDef().getColumnsSize());
      ColumnDef<Object> columnDef = dataPath.getOrCreateRelationDef().getColumnDef(1);
      Assert.assertEquals(XmlDataPath.XML_DEFAULT_HEADER_NAME, columnDef.getColumnName());
      Assert.assertEquals(Types.SQLXML, columnDef.getDataType().getTypeCode());
      Assert.assertEquals(1L, (long) dataPath.getCount());
      Tabulars.print(dataPath);
      try (SelectStream selectStream = dataPath.getSelectStream()) {
        selectStream.next();
        String output = selectStream.getString(1);
        Assert.assertEquals("<?xml version=\"1.1\"?>\n<bar>foo</bar>\n", output);
      }
    }
  }

  @Test
  public void httpXml() {
    try (Tabular tabular = Tabular.tabularWithCleanEnvironment()) {
      Timer timer = Timer.create("load").start();
      List<DataPath> dataPaths = tabular.select(DataUri.create().setPath("https://en.wikipedia.org/w/api.php?action=query&titles=SQL&format=xml&prop=description|categories"));
      Assert.assertEquals(1,dataPaths.size());
      DataPath dataPath = dataPaths.get(0);
      Assert.assertEquals(dataPath.getClass().getSimpleName(), XmlDataPath.class.getSimpleName());
      timer.stop();
      System.out.println(timer.getResponseTimeInString());
    }
  }
}
