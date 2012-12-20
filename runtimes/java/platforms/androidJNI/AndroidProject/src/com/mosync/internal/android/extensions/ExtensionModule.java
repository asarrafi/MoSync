package com.mosync.internal.android.extensions;

import java.util.HashMap;
import java.util.Map;


public class ExtensionModule {
	private Class clazz;
	private String name;
	private int id;
	private HashMap<Integer, FunctionInvocation> invokersById = new HashMap<Integer, FunctionInvocation>();
	private HashMap<String, FunctionInvocation> invokersByName = new HashMap<String, FunctionInvocation>();
	private Object module;
	private Map<String, TypeDescriptor> typedefs = new HashMap<String, TypeDescriptor>();
	private Map<String, StructType> structs = new HashMap<String, StructType>();
	private int hash;

	public ExtensionModule(int id, String name, String className, int hash)
			throws Throwable {
		this.id = id;
		this.name = name;
		clazz = Class.forName(className);
		module = clazz.newInstance();
		this.hash = hash;
	}

	public String getName() {
		return name;
	}

	public Object getModule() {
		return module;
	}

	public FunctionInvocation getInvoker(int ix) {
		return invokersById.get(ix);
	}

	public FunctionInvocation getFunction(String name) {
		return invokersByName.get(name);
	}

	public FunctionInvocation getFunction(int id) {
		return invokersById.get(id);
	}

	public int getId() {
		return id;
	}

	public void setInvokers(FunctionInvocation[] invokers) {
		for (int i = 0; i < invokers.length; i++) {
			FunctionInvocation invoker = invokers[i];
			invokersById.put(i, invoker);
			invokersByName.put(invoker.getName(), invoker);
		}
	}

	public void setTypedefs(Map<String, TypeDescriptor> typedefs) {
		this.typedefs = typedefs;
	}


	public void setStructs(Map<String, StructType> structs) {
		this.structs = structs;
	}

	public int getHash() {
		return hash;
	}

	public TypeDescriptor getTypeDescriptor(String name, int ptrDepth,
			boolean out) {
		if (ptrDepth > 4) {
			throw new IllegalArgumentException("Pointer depth > 4");
		}

		TypeDescriptor result = null;
		if ("char".equals(name) && ptrDepth == 1) {
			result = new StringType();
		} else if (ptrDepth > 0) {
			result = new PointerType(getTypeDescriptor(name, ptrDepth - 1, out));
		} else if ("int".equals(name)) {
			result = new IntType();
		} else if ("char".equals(name)) {
			result = new CharType();
		} else if ("double".equals(name)) {
			result = new DoubleType();
		} else if ("float".equals(name)) {
			result = new FloatType();
		} else {
			result = typedefs.get(name);
		}

		if (result == null) {
			result = structs.get(name);
		}

		if (result == null) {
			throw new IllegalArgumentException("Unknown type: " + name);
		}

		if (!result.isArray() && out) {
			result = new PointerType(result);
		}

		return result;
	}


}