package xyz.nuyube.minecraft.sellxp;

final class ConfigKey {
  String name;
  Object value;
  public ConfigKey(String n, Object v) {
    name = n;
    value = v;
  }

  /**
   * Returns the value as a string
   * @return
   * The value, either converted to a string, or directly casted.
   * @throws NullPointerException
   * If the value is really null
   */
  public String asString() throws  NullPointerException {
    if(isNull()) {
      throw new NullPointerException();
    }
    if(value instanceof String) {
      return (String) value;
    }
    else {
      return value.toString();
    }
  }
  
  public boolean isNull() {
    return value == null;
  }
  /**
   * Returns the value as a double, trying to convert it if the value is really a string
   * @return
   * the value as a double 
   * @throws IllegalStateException
   * If the value is neither a string or double
   * @throws NumberFormatException 
   * If the value is a string and cannot be parsed as a double
   * @throws NullPointerException
   * If the value is really null
   */
  public double asDouble() throws IllegalStateException , NumberFormatException, NullPointerException {
    if(isNull()) {
      throw new NullPointerException();
    }
    if(value instanceof Double) { 
      return (double)value;
    }
    else if(value instanceof String) { 
      return Double.parseDouble(((String) value).trim());
    }
    else {
      throw new IllegalStateException();
    }
  }
  /**
   * Returns the value as an int, trying to convert it if the value is really a string
   * @return
   * the value as an int 
   * @throws IllegalStateException
   * If the value is neither a string or int
   * @throws NumberFormatException 
   * If the value is a string and cannot be parsed as an int
   * @throws NullPointerException
   * If the value is really null
   */
  public int asInt() throws IllegalStateException , NumberFormatException, NullPointerException {
    if(isNull()) {
      throw new NullPointerException();
    }
    if(value instanceof Integer) { 
      return (int)value;
    }
    else if(value instanceof String) { 
      return Integer.parseInt(((String) value).trim());
    }
    else {
      throw new IllegalStateException();
    }
  }
}