package de.tuda.stg.reclipse.logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Simple class which includes all information about a specific reactive
 * variable. This information is taken for the visualisation of this variable in
 * the graph as a node.
 */
public class ReactiveVariable implements Serializable,
		Comparable<ReactiveVariable> {

	private static final long serialVersionUID = -179656828771932317L;

	/**
	 * The unique identifier of this variable.
	 */
	private UUID id;

	/**
	 * The type of this reactive variable.
	 *
	 * @see ReactiveVariableType
	 */
	private ReactiveVariableType reactiveVariableType;

	/**
	 * The point in time at which this information is valid.
	 */
	private int pointInTime;

	/**
	 * The type of the entry in the dependency graph history.
	 *
	 * @see DependencyGraphHistoryType
	 */
	private DependencyGraphHistoryType dependencyGraphHistoryType;

	/**
	 * A free text field where additional information can be stored. If the
	 * dependency graph history type is a
	 * {@link DependencyGraphHistoryType#NODE_ATTACHED}, then this field has to
	 * be filled with a String in the form "ID1-&gt;ID2".
	 */
	private String additionalInformation;

	/**
	 * The simple type of this variable - e.g. Integer
	 */
	private String typeSimple;

	/**
	 * The full type of this variable - e.g. java.lang.Integer
	 */
	private String typeFull;

	/**
	 * The name of this variable in the source code.
	 */
	private String name;

	/**
	 * A generic map which can be used for additional language-specific
	 * attribute-value-pairs.
	 */
	private Map<String, Object> additionalKeys = new HashMap<>();

	/**
	 * The current value of this variable as a String. Must not be null.
	 */
	private String valueString;
	
	private boolean exceptionOccured;

	/**
	 * IDs of variables to which this variable is connected.
	 */
	private final Set<UUID> connectedWith = new HashSet<>();

	public ReactiveVariable() {
		// empty constructor
	}

	public ReactiveVariable(final UUID theId,
			final ReactiveVariableType theReactiveVariableType,
			final int thePointInTime,
			final DependencyGraphHistoryType theHistoryType,
			final String theAdditionalInformation,
			final String theTypeSimple, final String theTypeFull,
			final String theName, final String theValueString) {
		id = theId;
		reactiveVariableType = theReactiveVariableType;
		pointInTime = thePointInTime;
		dependencyGraphHistoryType = theHistoryType;
		additionalInformation = theAdditionalInformation;
		typeSimple = theTypeSimple;
		typeFull = theTypeFull;
		name = theName;
		valueString = theValueString;
		exceptionOccured = false;
	}

	public ReactiveVariable(final ReactiveVariable reVar) {
		setId(reVar.getId());
		setReactiveVariableType(reVar.getReactiveVariableType());
		setPointInTime(reVar.getPointInTime());
		setDependencyGraphHistoryType(reVar.getDependencyGraphHistoryType());
		setAdditionalInformation(reVar.getAdditionalInformation());
		setTypeSimple(reVar.getTypeSimple());
		setTypeFull(reVar.getTypeFull());
		setName(reVar.getName());
		for (final String key : reVar.getAdditionalKeysKeySet()) {
			setAdditionalKeyValue(key, reVar.getAdditionalKeyValue(key));
		}
		setValueString(reVar.getValueString());
		setExceptionOccured(reVar.isExceptionOccured());
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID theId) {
		id = theId;
	}

	public ReactiveVariableType getReactiveVariableType() {
		return reactiveVariableType;
	}

	public void setReactiveVariableType(
			final ReactiveVariableType theReactiveVariableType) {
		reactiveVariableType = theReactiveVariableType;
	}

	public int getPointInTime() {
		return pointInTime;
	}

	public void setPointInTime(final int thePointInTime) {
		pointInTime = thePointInTime;
	}

	public DependencyGraphHistoryType getDependencyGraphHistoryType() {
		return dependencyGraphHistoryType;
	}

	public void setDependencyGraphHistoryType(
			final DependencyGraphHistoryType theDependencyGraphHistoryType) {
		dependencyGraphHistoryType = theDependencyGraphHistoryType;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(final String theAdditionalInformation) {
		additionalInformation = theAdditionalInformation;
	}

	public String getTypeSimple() {
		return typeSimple;
	}

	public void setTypeSimple(final String theTypeSimple) {
		typeSimple = theTypeSimple;
	}

	public String getTypeFull() {
		return typeFull;
	}

	public void setTypeFull(final String theTypeFull) {
		typeFull = theTypeFull;
	}

	public String getName() {
		return name;
	}

	public void setName(final String theName) {
		name = theName;
	}

	public Map<String, Object> getAdditionalKeys() {
		return additionalKeys;
	}

	public Set<String> getAdditionalKeysKeySet() {
		return additionalKeys.keySet();
	}

	public Object getAdditionalKeyValue(final String key) {
		return additionalKeys.get(key);
	}

	public void setAdditionalKeys(final Map<String, Object> theAdditionalKeys) {
		additionalKeys = theAdditionalKeys;
	}

	public void setAdditionalKeyValue(final String key, final Object s) {
		additionalKeys.put(key, s);
	}

	public String getValueString() {
		return valueString;
	}

	public void setValueString(final String s) {
		valueString = s;
	}

	public boolean isExceptionOccured() {
		return exceptionOccured;
	}

	public void setExceptionOccured(boolean exceptionOccured) {
		this.exceptionOccured = exceptionOccured;
	}

	public Set<UUID> getConnectedWith() {
		return connectedWith;
	}

	public boolean isConnectedWith(final UUID otherId) {
		return connectedWith.contains(otherId);
	}

	public void setConnectedWith(final UUID otherId) {
		connectedWith.add(otherId);
	}

	@Override
	public int compareTo(final ReactiveVariable v) {
		if (v == null) {
			return 1;
		}
		return id.compareTo(v.id);
	}

	@Override
	public String toString() {
		return id.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + pointInTime;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReactiveVariable other = (ReactiveVariable) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (pointInTime != other.pointInTime)
			return false;
		return true;
	}
}
