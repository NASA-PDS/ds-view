package gov.nasa.pds.search.core.registry.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.nasa.pds.registry.model.ExtrinsicObject;
import gov.nasa.pds.registry.model.RegistryObject;
import gov.nasa.pds.registry.model.Slot;
import gov.nasa.pds.search.core.constants.Constants;
import gov.nasa.pds.search.core.exception.SearchCoreFatalException;
import gov.nasa.pds.search.core.stats.SearchCoreStats;
import gov.nasa.pds.search.core.util.Debugger;

/**
 * Class leverages the Decorator Pattern by inheriting the functions of
 * {@link ExtrinsicObject} through the {@link ExtrinsicObjectDecorator} class.
 * Currently no methods are overridden, however, this provides that flexibility
 * that should be a part of extending a class.
 * 
 * @author jpadams
 *
 */
public class SearchCoreExtrinsic extends ExtrinsicObjectDecorator {
	private static final long serialVersionUID = 2780915859620067707L;
	
	private boolean validAssociationValues;
	private List<String> invalidAssociations;
	
	public SearchCoreExtrinsic(ExtrinsicObject extObject) {
		super(extObject);
		
		this.validAssociationValues = true;
		this.invalidAssociations = new ArrayList<String>();
	}
	
	/**
	 * Returns a list of Strings that pertain to a particular slot
	 * 
	 * @param slotName
	 * @return
	 * @throws SearchCoreFatalException
	 */
	public List<String> getSlotValues(String slotName) {		
		Slot slot = super.getSlot(slotName.trim());
		ExtrinsicRegistryAttribute ra;
		
		// This if-else handles whether the slotName is a slot,
		// attribute, or missing altogether.
		if (slot != null) {	// Slot exists
			if (slotIsAssociationReference(slotName)) {
				for (String slotValue : slot.getValues()) {
					if (!slotValueIsLidvid(slotValue)) {
						this.validAssociationValues = false;
					}
				}
				
				if (!this.validAssociationValues) {
					addInvalidAssociation(slotName);
				}
			}
			return slot.getValues();
		} else if ((ra = ExtrinsicRegistryAttribute.get(slotName)) != null) {
			return Arrays.asList(ra.getValueFromExtrinsic(super.decoratedExtrinsic));
		} else {
			SearchCoreStats.addMissingSlot(super.getLid(), slotName);
			return null;
		}
	}
	
	public String getLidvid() {
		List<String> versionValues = getSlotValues(Constants.VERSION_ID_SLOT);
		if (versionValues != null) {
			return super.getLid() + "::" + versionValues.get(0);
		} else {
			return super.getLid();
		}
	}
	
	public boolean slotIsAssociationReference(String slotName) {
		if (slotName.endsWith("_ref")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean slotValueIsLidvid(String slotValue) {
		if (slotValue.contains("::")) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean hasValidAssociationValues() {
		return this.validAssociationValues;
	}
	
	public void setValidAssociationValues(boolean validAssociationValues) {
		this.validAssociationValues = validAssociationValues;
	}
	
	public void addInvalidAssociation(String slotName) {
		if (!this.invalidAssociations.contains(slotName)) {
			this.invalidAssociations.add(slotName);
		}
	}
	
	/**
	 * Utility method to convert list of ExtrinsicObjects to SearchCoreExtrinsic objects
	 * 
	 * @param extObjList
	 * @return
	 */
	public static List<SearchCoreExtrinsic> asSearchCoreExtrinsics(List<ExtrinsicObject> extObjList) {
		List<SearchCoreExtrinsic> sceList = new ArrayList<SearchCoreExtrinsic>();
		for (ExtrinsicObject extObj : extObjList) {
			sceList.add(new SearchCoreExtrinsic(extObj));
		}
		return sceList;
	}
	
}
