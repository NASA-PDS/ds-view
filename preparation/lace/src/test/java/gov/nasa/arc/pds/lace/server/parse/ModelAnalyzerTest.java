package gov.nasa.arc.pds.lace.server.parse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import gov.nasa.arc.pds.lace.server.LabelContentsServiceImpl;
import gov.nasa.arc.pds.lace.shared.Container;
import gov.nasa.arc.pds.lace.shared.InsertOption;
import gov.nasa.arc.pds.lace.shared.InsertionPoint;
import gov.nasa.arc.pds.lace.shared.LabelItem;
import gov.nasa.arc.pds.lace.shared.LabelItemType;
import gov.nasa.arc.pds.lace.shared.SimpleItem;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class ModelAnalyzerTest {

	private static final File SCHEMA_FILE = new File("src/test/resources/model-analyzer-schema.xsd");
	private static final String SCHEMA_NAMESPACE = "http://arc.nasa.gov/pds4/model-analyzer";

	private static final File PDS4_0300a_SCHEMA_FILE = new File("src/main/resources/gov/nasa/arc/pds/lace/server/PDS4_OPS_0300a.xsd");
	private static final String PDS4_0300a_SCHEMA_NAMESPACE = "http://pds.nasa.gov/pds4/pds/v03";

	private ModelAnalyzer analyzer;

	@BeforeMethod
	public void init() {
		analyzer = new ModelAnalyzer(SCHEMA_FILE.toURI());
	}

	@Test(dataProvider="SimpleTests")
	public void testSimple(String rootElementName) {
		Container container = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE);
		assertEquals(container.getContents().size(), 1);

		LabelItem item = container.getContents().get(0);
		assertTrue(item instanceof SimpleItem);

		LabelItemType type = ((SimpleItem) item).getType();
		assertEquals(type.getElementName(), "SimpleValue");
		assertEquals(type.getMinOccurrences(), 1);
		assertEquals(type.getMaxOccurrences(), 1);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="SimpleTests")
	private Object[][] getSimpleTests() {
		return new Object[][] {
				// root element name
				{ "Simple1" },
				{ "Simple2" },
				{ "Simple3" },
				{ "Simple4" },
		};
	}

	@Test(dataProvider="MiddlingTests")
	public void testMiddling(String rootElementName) {
		Container root = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 1);

		LabelItem item = root.getContents().get(0);
		assertTrue(item instanceof Container);

		Container child = (Container) item;
		LabelItemType type = child.getType();
		assertEquals(type.getElementName(), "Container");
		assertEquals(type.getMinOccurrences(), 1);
		assertEquals(type.getMaxOccurrences(), 1);

		assertEquals(child.getContents().size(), 1);
		item = child.getContents().get(0);
		assertTrue(item instanceof SimpleItem);

		type = ((SimpleItem) item).getType();
		assertEquals(type.getElementName(), "SimpleValue");
		assertEquals(type.getMinOccurrences(), 1);
		assertEquals(type.getMaxOccurrences(), 1);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="MiddlingTests")
	private Object[][] getMiddlingTests() {
		return new Object[][] {
				// root element name
				{ "Middling1" },
				{ "Middling2" },
				{ "Middling3" },
		};
	}

	@Test(dataProvider="InsPointTests")
	public void testInsertionPoints(
			String rootElementName,
			int minOccurrences,
			int maxOccurrences,
			int usedOccurrences,
			String[] expectedAlternatives
	) {
		Container root = analyzer.getContainerForElement(rootElementName, SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 1);

		LabelItem item = root.getContents().get(0);
		assertTrue(item instanceof InsertionPoint);
		
		List<InsertOption> alternatives = ((InsertionPoint) item).getAlternatives();
		assertEquals(alternatives.size(), 1);
		
		assertEquals(alternatives.get(0).getMaxOccurrences(), maxOccurrences);
		assertEquals(alternatives.get(0).getMinOccurrences(), minOccurrences);
		assertEquals(alternatives.get(0).getUsedOccurrences(), usedOccurrences);
		assertEquals(alternatives.get(0).getTypes().size(), expectedAlternatives.length);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="InsPointTests")
	private Object[][] getInsPointTests() {
		return new Object[][] {
				//root element, min, max, used, expected insert options
				{ "InsPoint1", 0,  1, 0, new String[] {"Container"} },
				{ "InsPoint2", 0, -1, 0, new String[] {"Container"} },
				{ "InsPoint3", 1, -1, 0, new String[] {"Container"} },
				{ "InsPoint4", 1, -1, 0, new String[] {"Value"} },
				{ "InsPoint5", 0, -1, 0, new String[] {"Value"} },
				{ "InsPoint6", 0, -1, 0, new String[] {"Value1", "Value2"} },
				{ "InsPoint7", 1,  1, 0, new String[] {"Value1", "Value2"} },
		};
	}

	@Test
	public void testReusingSimpleTypes() {
		Container root = analyzer.getContainerForElement("Common1", SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 3);

		LabelItemType rootItem1Type = ((SimpleItem) root.getContents().get(0)).getType();
		LabelItemType rootItem2Type = ((SimpleItem) root.getContents().get(1)).getType();
		LabelItemType rootItem3Type = ((InsertionPoint) root.getContents().get(2)).getAlternatives().get(0).getTypes().get(0);

		assertEquals(rootItem1Type.getElementName(), "Value1");
		assertEquals(rootItem2Type.getElementName(), "Value2");
		assertEquals(rootItem3Type.getElementName(), "Value3");
		
		assertEquals(rootItem1Type.getMinOccurrences(), 1);
		assertEquals(rootItem1Type.getMaxOccurrences(), 1);
		assertEquals(rootItem2Type.getMinOccurrences(), 0);
		assertEquals(rootItem2Type.getMaxOccurrences(), 1);
		assertEquals(rootItem3Type.getMinOccurrences(), 1);
		assertEquals(rootItem3Type.getMaxOccurrences(), -1);
	}

	@Test
	public void testReusingContainerTypes() {
		Container root = analyzer.getContainerForElement("Common2", SCHEMA_NAMESPACE);
		List<LabelItem> contents1 = root.getContents();
		assertEquals(contents1.size(), 4);

		LabelItemType rootItem1Type = ((Container) contents1.get(0)).getType();
		LabelItemType rootItem2Type = (((InsertionPoint) contents1.get(1)).getAlternatives().get(0)).getTypes().get(0);
		LabelItemType rootItem3Type = (((InsertionPoint) contents1.get(2)).getAlternatives().get(0)).getTypes().get(0);
		LabelItemType rootItem4Type = (((InsertionPoint) contents1.get(3)).getAlternatives().get(0)).getTypes().get(0);
		
		assertEquals(rootItem1Type.getElementName(), "Container1");
		assertEquals(rootItem2Type.getElementName(), "Container2");
		assertEquals(rootItem3Type.getElementName(), "Container3");
		assertEquals(rootItem4Type.getElementName(), "Container4");
		
		assertEquals(rootItem1Type.getMinOccurrences(), 1);
		assertEquals(rootItem1Type.getMaxOccurrences(), 1);		
		assertEquals(rootItem2Type.getMinOccurrences(), 0);
		assertEquals(rootItem2Type.getMaxOccurrences(), 1);		
		assertEquals(rootItem3Type.getMinOccurrences(), 1);
		assertEquals(rootItem3Type.getMaxOccurrences(), -1);
		assertEquals(rootItem4Type.getMinOccurrences(), 0);
		assertEquals(rootItem4Type.getMaxOccurrences(), -1);
	}

	@Test
	public void testRepeatingContainers() {
		Container root = analyzer.getContainerForElement("Repeating1", SCHEMA_NAMESPACE);
		assertEquals(root.getContents().size(), 1);
		
		Container item1 = (Container) root.getContents().get(0);
		assertEquals(item1.getType().getElementName(), "Container");
		
		InsertionPoint insPoint1 = (InsertionPoint) item1.getContents().get(0);
		List<InsertOption> alternatives = insPoint1.getAlternatives();
		assertEquals(alternatives.size(), 1);
		
		List<LabelItemType> types = alternatives.get(0).getTypes();		
		assertEquals(types.size(), 2);
		assertEquals(types.get(0).getElementName(), "SimpleValue");
		assertEquals(types.get(1).getElementName(), "Container");
	}
	
	private boolean oldAndNewModelMatch;

	@Test(enabled=false)
	public void testOldAndNewModelCreation() throws ClassCastException, SAXException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		oldAndNewModelMatch = true;

		LabelContentsServiceImpl oldService = new LabelContentsServiceImpl(PDS4_0300a_SCHEMA_FILE.toURI(), PDS4_0300a_SCHEMA_NAMESPACE);
		ModelAnalyzer newService = new ModelAnalyzer(PDS4_0300a_SCHEMA_FILE.toURI());

		Container oldResult = oldService.getRootContainer("Product_Observational");
		Container newResult = newService.getContainerForElement("Product_Observational", PDS4_0300a_SCHEMA_NAMESPACE);
		newService.expandInsertionPoints(newResult);

		compareContainers(oldResult, newResult, "/");

		if (!oldAndNewModelMatch) {
			System.out.println("----- Old -----");
			showItemTree(oldResult, 0);
			System.out.println("----- New -----");
			showItemTree(newResult, 0);
		}
		assertTrue(oldAndNewModelMatch, "Old and new models do not match");
	}

	private void compareContainers(Container c1, Container c2, String path) {
		compareTypes(c1.getType(), c2.getType(), path);

		String newPath = path + c1.getType().getElementName() + "/";
		compareFacet(newPath, "content length", c1.getContents().size(), c2.getContents().size());

		Iterator<LabelItem> it1 = c1.getContents().iterator();
		Iterator<LabelItem> it2 = c2.getContents().iterator();
		int index = 0;
		while (it1.hasNext() || it2.hasNext()) {
			LabelItem item1 = (it1.hasNext() ? it1.next() : null);
			LabelItem item2 = (it2.hasNext() ? it2.next() : null);

			if (item1 == null) {
				System.out.println(newPath + ": missing from old: " + item2.toString());
				oldAndNewModelMatch = false;
			} else if (item2 == null) {
				System.out.println(newPath + ": missing from new: " + item1.toString());
				oldAndNewModelMatch = false;
			} else if (item1.getClass() != item2.getClass()) {
				System.out.println(newPath + ": at index " + index + ", type mismatch: " + item1.toString() + " and " + item2.toString());
			} else {
				compareItems(item1, item2, newPath);
			}
			++index;
		}
	}

	private void compareTypes(LabelItemType type1, LabelItemType type2, String path) {
		compareFacet(path, "element names", type1.getElementName(), type2.getElementName());
		compareFacet(path, "minOccurs", type1.getMinOccurrences(), type2.getMinOccurrences());
		compareFacet(path, "maxOccurs", type1.getMaxOccurrences(), type2.getMaxOccurrences());
	}

	private void compareItems(LabelItem item1, LabelItem item2, String path) {
		// We can assume item1 and item2 are the same class.
		if (item1 instanceof SimpleItem) {
			compareSimpleItems((SimpleItem) item1, (SimpleItem) item2, path);
		} else if (item1 instanceof Container) {
			compareContainers((Container) item1, (Container) item2, path);
		} else if (item1 instanceof InsertionPoint) {
			compareInsertionPoints((InsertionPoint) item1, (InsertionPoint) item2, path);
		}
	}

	private void compareSimpleItems(SimpleItem item1, SimpleItem item2, String path) {
		compareTypes(item1.getType(), item2.getType(), path);
	}

	private void compareInsertionPoints(InsertionPoint item1, InsertionPoint item2, String path) {
		/*List<LabelItemType> alt1 = item1.getAlternatives();
		List<LabelItemType> alt2 = item2.getAlternatives();
		compareFacet(path, "number of alternatives", alt1.size(), alt2.size());

		Set<String> onlyInOld = new HashSet<String>();
		for (LabelItemType type : alt1) {
			onlyInOld.add(type.getElementName());
		}
		for (LabelItemType type : alt2) {
			onlyInOld.remove(type.getElementName());
		}

		Set<String> onlyInNew = new HashSet<String>();
		for (LabelItemType type : alt2) {
			onlyInNew.add(type.getElementName());
		}
		for (LabelItemType type : alt1) {
			onlyInNew.remove(type.getElementName());
		}

		if (onlyInOld.size() > 0) {
			System.out.print(path + ": missing alternatives from new: " + onlyInOld.toString());
			System.out.println();
			oldAndNewModelMatch = false;
		}
		if (onlyInNew.size() > 0) {
			System.out.print(path + ": missing alternatives from old: " + onlyInNew.toString());
			System.out.println();
			oldAndNewModelMatch = false;
		}*/
	}

	private void compareFacet(String path, String facetName, String a, String b) {
		if (!a.equals(b)) {
			System.out.println(path + ": " + facetName + " mismatch [" + a + " != " + b + "]");
			oldAndNewModelMatch = false;
		}
	}

	private void compareFacet(String path, String facetName, int a, int b) {
		if (a != b) {
			System.out.println(path + ": " + facetName + " mismatch [" + Integer.toString(a) + " != " + Integer.toString(b) + "]");
			oldAndNewModelMatch = false;
		}
	}

	private void showItemTree(LabelItem item, int depth) {
		System.out.println(getIndent(depth) + item.toString());
		if (item instanceof Container) {
			for (LabelItem child : ((Container) item).getContents()) {
				showItemTree(child, depth+1);
			}
		}
	}

	private String getIndent(int depth) {
		StringBuilder builder = new StringBuilder();

		for (int i=0; i < depth; ++i) {
			builder.append("   ");
		}

		return builder.toString();
	}

}
