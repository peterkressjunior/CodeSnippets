package org.json;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import de.kress.peter.Car;
import de.kress.peter.World;

public class JSONMarshaller {

	public static ArrayList<String> exploreSetters(Class<?> clazz)
			throws SecurityException {

		ArrayList<String> list = new ArrayList<String>();
		Method[] methods = clazz.getMethods();
		Method method;

		for (int i = 0; i < methods.length; i++) {
			method = methods[i];
			if (method.getName().length() > 4
					&& method.getName().startsWith("set")
					&& Character.isUpperCase(method.getName().charAt(3))) {
				list.add(Character.toLowerCase(method.getName().charAt(3))
						+ method.getName().substring(4));
			}
		}
		return list;
	}

	public static void setFields(JSONObject jsonObject, Object jsonModel)
			throws SecurityException, IllegalArgumentException,
			IllegalAccessException, JSONException, InstantiationException {
		if (jsonObject != null) {
			ArrayList<String> list = exploreSetters(jsonModel.getClass());
			Field[] fields = jsonModel.getClass().getFields();
			Field field;

			for (int i = 0; i < fields.length; i++) {
				field = fields[i];
				@SuppressWarnings("unused")
				Type type = field.getType();
				if (list.contains(field.getName())) {
					if (field.getType() == int.class) {
						setInt(jsonObject.optInt(field.getName()), field,
								jsonModel);
						continue;
					}
					if (field.getType() == String.class) {
						setString(jsonObject.optString(field.getName()), field,
								jsonModel);
						continue;
					}
					if (Object.class.isAssignableFrom(field.getType())) {
						Object model = (Object) field.getType().newInstance();
						setObject(model, field, jsonModel);
						setFields(jsonObject.optJSONObject(field.getName()),
								model);
						continue;
					}
					if (field.getType().isArray()) {
						@SuppressWarnings("unused")
						JSONArray jsonArray = jsonObject.optJSONArray(field
								.getName());

					}
				}
			}
		}
	}

	public static void main(String[] args) throws CloneNotSupportedException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Car myCar = null;
		
		try {
			String worldJSON = "{\"name\":\"NISSAN\",\"indicator\":0,\"topCar\":{\"name\":\"MICRA\",\"dors\":4},\"cars\":[{\"name\":\"MICRA\",\"dors\":4}, {\"name\":\"MICRA\",\"dors\":4}]}";
			World world = World.create(worldJSON); 
			
			System.out.println(world.getName());
			System.out.println(world.getIndicator());
			System.out.println(Car.serialize(world.getTopCar()));
			for (int i = 0; i < world.getCars().length; i++) {
				System.out.println(Car.serialize(world.getCars()[i]));
			}
			System.out.println(World.serialize(world));
		} catch (SecurityException e) {

			e.printStackTrace();
		}
	}

	// transform a jsonObject to an int
	public static int toInt(JSONObject jsonObject, String key) {
		return jsonObject.getInt(key);
	}

	// transform a jsonObject to a String
	public static String toString(JSONObject jsonObject, String key) {
		return jsonObject.optString(key);
	}

	// transform a jsonObject to a Boolean
	public static boolean toBoolean(JSONObject jsonObject, String key) {
		return jsonObject.optBoolean(key);
	}

	// transform a jsonObject to a Double
	public static double toDouble(JSONObject jsonObject, String key) {
		return jsonObject.optDouble(key);
	}

	// transform a jsonObject to an Object
	public static Object toObject(JSONObject jsonObject, String key) {
		return jsonObject.opt(key);
	}

	// transform a jsonObject to an Long
	public static long toLong(JSONObject jsonObject, String key) {
		return jsonObject.optLong(key);
	}

	// transform a jsonObject to an JSONObject
	public static JSONObject toJSONObject(JSONObject jsonObject, String key) {
		return jsonObject.optJSONObject(key);
	}

	// transform a jsonObject to an JSONArray
	public static JSONArray toJSONArray(JSONObject jsonObject, String key) {
		return jsonObject.optJSONArray(key);
	}

	public static void setInt(int intValue, Field field, Object object)
			throws IllegalArgumentException, IllegalAccessException {
		field.set(object, intValue);
	}

	public static void setString(String stringValue, Field field, Object object)
			throws IllegalArgumentException, IllegalAccessException {
		field.set(object, stringValue);
	}

	public static void setBoolean(boolean booleanValue, Field field,
			Object object) throws IllegalArgumentException,
			IllegalAccessException {
		field.set(object, booleanValue);
	}

	public static void setDouble(double doubleValue, Field field, Object object)
			throws IllegalArgumentException, IllegalAccessException {
		field.set(object, doubleValue);
	}

	public static void setObject(Object objectValue, Field field, Object object)
			throws IllegalArgumentException, IllegalAccessException {
		field.set(object, objectValue.getClass().cast(objectValue));
	}

	public static void setLong(long longValue, Field field, Object object)
			throws IllegalArgumentException, IllegalAccessException {
		field.set(object, longValue);
	}

	public static Object loadObject(Class<?> clazz, String jsonObject) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method[] methods = clazz.getMethods();
		Method create = null;
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals("create")) {
				create = methods[i];
				break;
			}
		}
		@SuppressWarnings("unused")
		Parameter[] parameters = create.getParameters();
		@SuppressWarnings("unused")
		int count = create.getParameterCount();
		Constructor<?>[] constructors = clazz.getConstructors();
		@SuppressWarnings("unused")
		Constructor<?> constructor = null;
		for (int i = 0; i < constructors.length; i++) {
			constructor = constructors[i];
		}
		return create.invoke(null, jsonObject);
	}
}

// this.map = new HashMap<String, Object>();
// if (map != null) {
// Iterator<Entry<String, Object>> i = map.entrySet().iterator();
