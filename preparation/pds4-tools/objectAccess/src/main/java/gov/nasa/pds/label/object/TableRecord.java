package gov.nasa.pds.label.object;

/**
 * Defines an object for table records that can read the field values
 * or write values to the fields given a field name or field index.
 */
public interface TableRecord {

	/**
	 * Gets the integer index of a field with the given name (1-relative).
	 *
	 * @param name the name of a field
	 * @return the integer index of a field (1-relative)
	 */
	int findColumn(String name);

	/**
	 * Gets the short value of a field given the index.
	 *
	 * @param index the column index (1-relative)
	 * @return a short value
	 * @throws IllegalArgumentException If cannot convert the number to short
	 */
	short getShort(int index);

	/**
	 * Gets the short value of a field given the name.
	 *
	 * @param name the field name
	 * @return a short value
	 */
	short getShort(String name);

	/**
	 * Gets the byte value of a field given the index.
	 *
	 * @param index the field index (1-relative)
	 * @return a byte value
	 * @throws IllegalArgumentException If cannot convert the number to byte
	 */
	byte getByte(int index);

	/**
	 * Gets the byte value of a field given the name.
	 *
	 * @param name the field name
	 * @return a byte value
	 */
	byte getByte(String name);

	/**
	 * Gets the long value of a field given the index.
	 *
	 * <p>It throws NumberFormatException if the number is out of range
	 * when converting from ASCII_Integer to long.</p>
	 *
	 * @param index the field index (1-relative)
	 * @return a long value
	 * @throws IllegalArgumentException If cannot convert the number to long
	 * @throws NumberFormatException If the number is out of range for a long
	 */
	long getLong(int index);

	/**
	 * Gets the long value of a field given the name.
	 *
	 * @param name the field name
	 * @return a long value
	 */
	long getLong(String name);

	/**
	 * Gets the integer value of a field given the name.
	 *
	 * @param name the field name
	 * @return an integer value
	 */
	int getInt(String name);

	/**
	 * Gets the integer value of a field given the index.
	 *
	 * <p>It throws NumberFormatException if the number is out of range
	 *  when converting from ASCII_Integer to int.</p>
	 *
	 * @param index the field index (1-relative)
	 * @return an integer value
	 * @throws IllegalArgumentException If cannot convert the number to int
	 * @throws NumberFormatException If the number is out of range for an int
	 */
	int getInt(int index);

	/**
	 * Gets the double value of a field given the name.
	 *
	 * @param name the field name
	 * @return a double value
	 */
	double getDouble(String name);

	/**
	 * Gets the double value of a field given the index.
	 *
	 * <p> If the number is out of range when converting from ASCII_Real to double,
	 * the method returns Double.POSITIVE_INFINITY or Double.NEGATIVE_INFINITY.</p>
	 *
	 * @param index the field index (1-relative)
	 * @return a double value
	 * @throws IllegalArgumentException If cannot convert the number to double
	 */
	double getDouble(int index);

	/**
	 * Gets the float value of a field given the name.
	 *
	 * @param name the field name
	 * @return a float value
	 */
	float getFloat(String name);

	/**
	 * Gets the float value of a field given the index.
	 *
	 * <p> If the number is out of range when converting from ASCII_Real to float,
	 * the method returns Float.POSITIVE_INFINITY or Float.NEGATIVE_INFINITY.</p>
	 *
	 * @param index the field index (1-relative)
	 * @return a float value
	 * @throws IllegalArgumentException If cannot convert the number to float
	 */
	float getFloat(int index);

	/**
	 * Gets the string value of a field given the name.
	 *
	 * @param name the field name
	 * @return a String value
	 */
	String getString(String name);

	/**
	 * Gets the string value of a field given the index.
	 *
	 * @param index the field index (1-relative)
	 * @return a String value
	 * @throws IllegalArgumentException If data type is not supported
	 */
	String getString(int index);

	/**
	 * Sets a string value to a field given the index. In a fixed-width text file,
	 * numeric values are right justified and non-numeric values are left justified.
	 *
	 * @param index the field index (1-relative)
	 * @param value a string value
	 * @throws IllegalArgumentException If the size of the value is greater than the field length
	 */
	void setString(int index, String value);

	/**
	 * Sets a string value to a field given the name. In a fixed-width text file,
	 * numeric values are right justified and non-numeric values are left justified.
	 *
	 * @param name  the field name
	 * @param value a string value
	 * @throws IllegalArgumentException If the size of the value is greater than field length
	 */
	void setString(String name, String value);

	/**
	 * Sets a String value to the record at the current offset.
	 * Use this method to write a field delimiter in fixed-width text file.
	 *
	 * @param value a String value
	 */
	void setString(String value);

	/**
	 * Sets four bytes containing the integer value to a field given the index.
     *
	 * @param index the field index (1-relative)
	 * @param value an integer value
	 */
	void setInt(int index, int value);

	/**
	 * Sets four bytes containing the integer value to a field given the name.
     *
	 * @param name the field name
	 * @param value an integer value
	 */
	void setInt(String name, int value);

	/**
	 * Sets eight bytes containing the double value to a field given the index.
	 *
	 * @param index the field index (1-relative)
	 * @param value a double value
	 */
	void setDouble(int index, double value);

	/**
	 * Sets eight bytes containing the double value to a field given the name.
	 *
	 * @param name the field name
	 * @param value a double value
	 */
	void setDouble(String name, double value);

	/**
	 * Sets four bytes containing the float value to a field given the index.
	 *
	 * @param index the field index (1-relative)
	 * @param value a float value
	 */
	void setFloat(int index, float value);

	/**
	 * Sets four bytes containing the float value to a field given the name.
	 *
	 * @param name the field name
	 * @param value a float value
	 */
	void setFloat(String name, float value);

	/**
	 * Sets two bytes containing the short value to a field given the index.
	 *
	 * @param index the field index (1-relative)
	 * @param value a short value
	 */
	void setShort(int index, short value);

	/**
	 * Sets two bytes containing the short value to a field given the index.
	 *
	 * @param name the field name
	 * @param value a short value
	 */
	void setShort(String name, short value);

	/**
	 * Sets one byte value to a field given the index.
	 *
	 * @param index the field index (1-relative)
	 * @param value a byte value
	 */
	void setByte(int index, byte value);

	/**
	 * Sets one byte value to a field given the name.
	 *
	 * @param name the field name
	 * @param value a byte value
	 */
	void setByte(String name, byte value);

	/**
	 * Sets eight bytes containing the long value to the record given the field index.
	 *
	 * @param index the field index (1-relative)
	 * @param value a long value
	 */
	void setLong(int index, long value);

	/**
	 * Sets eight bytes containing the long value to the record given the field name.
	 *
	 * @param name the field name
	 * @param value a long value
	 */
	void setLong(String name, long value);

	/**
	 * Clears the content of the <code>TableRecord</code> object.
	 */
	void clear();

}
