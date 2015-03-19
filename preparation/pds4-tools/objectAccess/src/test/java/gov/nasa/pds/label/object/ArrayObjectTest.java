package gov.nasa.pds.label.object;

import static org.fest.reflect.core.Reflection.field;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import gov.nasa.arc.pds.xml.generated.Array;
import gov.nasa.arc.pds.xml.generated.Array2D;
import gov.nasa.arc.pds.xml.generated.AxisArray;
import gov.nasa.arc.pds.xml.generated.ElementArray;
import gov.nasa.arc.pds.xml.generated.FileSize;
import gov.nasa.arc.pds.xml.generated.Offset;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ArrayObjectTest {

	@Test
	public void test2DDouble() throws IOException, InstantiationException, IllegalAccessException {
		int[] expectedDimensions = {2, 3};
		File tempFile = createDoubleArray(expectedDimensions);
		gov.nasa.arc.pds.xml.generated.File fileObj = createFileObject(tempFile);
		Array arrayObj = createArrayObject(Array2D.class, expectedDimensions, "IEEE754MSBDouble");

		ArrayObject array = new ArrayObject(tempFile.getParentFile(), fileObj, arrayObj, 0);
		checkDimensions(array, expectedDimensions);

		assertEquals(array.getElementSize(), Double.SIZE / Byte.SIZE);
		assertEquals(array.getOffset(), 0);
		assertEquals(array.getSize(), tempFile.length());
		assertFalse(array.isImage());

		// Access the array element by element.
		int expected = 1;
		for (int i=0; i < 2; ++i) {
			for (int j=0; j < 3; ++j) {
				assertEquals(array.getDouble(i, j), (double) expected);
				assertEquals(array.getInt(i, j), expected);
				assertEquals(array.getLong(i, j), expected);
				++expected;
			}
		}

		// Get the entire array.
		double[][] actual = array.getElements2D();
		assertEquals(actual.length, 2);

		expected = 1;
		for (int i=0; i < 2; ++i) {
			assertEquals(actual[i].length, 3);
			for (int j=0; j < 3; ++j) {
				assertEquals(actual[i][j], (double) expected);
				++expected;
			}
		}
	}

	@Test
	public void test3DDouble() throws IOException, InstantiationException, IllegalAccessException {
		int[] expectedDimensions = {2, 3, 4};
		File tempFile = createDoubleArray(expectedDimensions);
		gov.nasa.arc.pds.xml.generated.File fileObj = createFileObject(tempFile);
		Array arrayObj = createArrayObject(Array2D.class, expectedDimensions, "IEEE754MSBDouble");

		ArrayObject array = new ArrayObject(tempFile.getParentFile(), fileObj, arrayObj, 0);
		checkDimensions(array, expectedDimensions);

		assertEquals(array.getElementSize(), Double.SIZE / Byte.SIZE);
		assertEquals(array.getOffset(), 0);
		assertEquals(array.getSize(), tempFile.length());
		assertFalse(array.isImage());

		// Access the array element by element.
		int expected = 1;
		for (int i=0; i < 2; ++i) {
			for (int j=0; j < 3; ++j) {
				for (int k=0; k < 4; ++k) {
					assertEquals(array.getDouble(i, j, k), (double) expected);
					assertEquals(array.getInt(i, j, k), expected);
					assertEquals(array.getLong(i, j, k), expected);

					int[] position = {i, j, k};
					assertEquals(array.getDouble(position), (double) expected);
					assertEquals(array.getInt(position), expected);
					assertEquals(array.getLong(position), expected);

					++expected;
				}
			}
		}

		// Get the entire array.
		double[][][] actual = array.getElements3D();
		assertEquals(actual.length, 2);

		expected = 1;
		for (int i=0; i < 2; ++i) {
			assertEquals(actual[i].length, 3);
			for (int j=0; j < 3; ++j) {
				assertEquals(actual[i][j].length, 4);
				for (int k=0; k < 4; ++k) {
					assertEquals(actual[i][j][k], (double) expected);
					++expected;
				}
			}
		}
	}

	@Test(dataProvider="BadIndicesTests", expectedExceptions={ArrayIndexOutOfBoundsException.class, IllegalArgumentException.class})
	public void testBadIndices(
			int[] position
	) throws IOException, InstantiationException, IllegalAccessException {
		int[] expectedDimensions = {2, 3};
		File tempFile = createDoubleArray(expectedDimensions);
		gov.nasa.arc.pds.xml.generated.File fileObj = createFileObject(tempFile);
		Array arrayObj = createArrayObject(Array2D.class, expectedDimensions, "IEEE754MSBDouble");
		ArrayObject array = new ArrayObject(tempFile.getParentFile(), fileObj, arrayObj, 0);

		@SuppressWarnings("unused")
		double value = array.getDouble(position);
	}

	@SuppressWarnings("unused")
	@DataProvider(name="BadIndicesTests")
	private Object[][] getBadIndicesTests() {
		return new Object[][] {
				{ new int[] {3, 1} }, // Index greater than allowable
				{ new int[] {1, -1} }, // Index less than allowable
				{ new int[] {1, 2, 3} }, // Too many indices
				{ new int[] {1} }, // Too few indices
		};
	}

	private File createDoubleArray(int[] dimensions) throws IOException {
		File tempFile = File.createTempFile("test", "dat");
		RandomAccessFile f = new RandomAccessFile(tempFile, "rw");
		MappedByteBuffer byteBuf = f.getChannel().map(MapMode.READ_WRITE, 0, countElements(dimensions) * Double.SIZE/Byte.SIZE);
		byteBuf.order(ByteOrder.BIG_ENDIAN);
		DoubleBuffer buf = byteBuf.asDoubleBuffer();

		writeData(buf, dimensions, 0, new SequenceGenerator());

		byteBuf.force();
		f.close();

		return tempFile;
	}

	private int countElements(int[] dimensions) {
		int count = 1;
		for (int i=0; i < dimensions.length; ++i) {
			count *= dimensions[i];
		}
		return count;
	}

	private void writeData(DoubleBuffer buf, int[] dimensions, int curIndex, DataGenerator generator) {
		for (int i=0; i < dimensions[curIndex]; ++i) {
			if (curIndex == dimensions.length-1) {
				buf.put(generator.next());
			} else {
				writeData(buf, dimensions, curIndex+1, generator);
			}
		}
	}

	private gov.nasa.arc.pds.xml.generated.File createFileObject(File f) {
		gov.nasa.arc.pds.xml.generated.File fileObj = new gov.nasa.arc.pds.xml.generated.File();
		fileObj.setFileName(f.getName());
		FileSize size = new FileSize();
		size.setValue((int) f.length());
		fileObj.setFileSize(size);
		return fileObj;
	}

	private Array createArrayObject(Class<? extends Array> clazz, int[] dimensions, String elementType) throws InstantiationException, IllegalAccessException {
		Array arrayObj = clazz.newInstance();
		arrayObj.setAxes(dimensions.length);
		Offset offset = new Offset();
		offset.setValue(0);
		arrayObj.setOffset(offset);

		List<AxisArray> axes = new ArrayList<AxisArray>();
		for (int i=0; i < dimensions.length; ++i) {
			AxisArray axis = new AxisArray();
			axis.setAxisName("Axis" + (i+1));
			axis.setElements(dimensions[i]);
			axes.add(axis);
		}

		field("axisArraies").ofType(List.class).in(arrayObj).set(axes);

		ElementArray elArray = new ElementArray();
		elArray.setDataType(elementType);
		elArray.setScalingFactor(1.0);
		elArray.setValueOffset(0.0);
		arrayObj.setElementArray(elArray);

		return arrayObj;
	}

	private void checkDimensions(ArrayObject array, int[] expectedDimensions) {
		assertEquals(array.getAxes(), expectedDimensions.length);
		int[] actualDimensions = array.getDimensions();
		assertEquals(actualDimensions.length, expectedDimensions.length);
		for (int i=0; i < expectedDimensions.length; ++i) {
			assertEquals(actualDimensions[i], expectedDimensions[i],
					"Dimension " + i + " does not match: " + actualDimensions[i] + "!=" + expectedDimensions[i]);
		}
	}

	private interface DataGenerator {

		double next();

	}

	private static class SequenceGenerator implements DataGenerator {

		private int curValue = 0;

		@Override
		public double next() {
			return ++curValue;
		}

	}
}
