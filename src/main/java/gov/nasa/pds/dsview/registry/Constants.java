// Copyright 2012-2014, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government Sponsorship acknowledged.
// Any commercial use must be negotiated with the Office of Technology Transfer
// at the California Institute of Technology.
//
// This software is subject to U. S. export control laws and regulations
// (22 C.F.R. 120-130 and 15 C.F.R. 730-774). To the extent that the software
// is subject to U.S. export control laws and regulations, the recipient has
// the responsibility to obtain export licenses or other export authority as
// may be required before exporting such information to foreign countries or
// providing access to foreign nationals.
//
// $Id$

package gov.nasa.pds.dsview.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that holds constants used in ds-view.
 *
 * @author hyunlee
 *
 */
public class Constants {

  // map for target object from pds3 label to the registry slot key
  public static final Map<String, String> targetPds3ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    targetPds3ToRegistry.put("TARGET_NAME", "target_name");
    targetPds3ToRegistry.put("PRIMARY_BODY_NAME", "target_primary_body_name");
    // targetPds3ToRegistry.put("ORBIT_DIRECTION", "orbit_direction");
    // targetPds3ToRegistry.put("ROTATION_DIRECTION", "rotation_direction");
    targetPds3ToRegistry.put("TARGET_TYPE", "target_type");
    targetPds3ToRegistry.put("TARGET_DESCRIPTION", "target_description");
    targetPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_text");
    targetPds3ToRegistry.put("RESOURCE_LINK", "resource_link");
  }

  // map for target object from pds3 label to the search key
  public static final Map<String, String> targetPds3ToSearch = new LinkedHashMap<String, String>();
  static {
    targetPds3ToSearch.put("TARGET_NAME", "target_name");
    targetPds3ToSearch.put("PRIMARY_BODY_NAME", "primary_body_name");
    targetPds3ToSearch.put("TARGET_TYPE", "target_type");
    targetPds3ToSearch.put("TARGET_DESCRIPTION", "target_description");
    targetPds3ToSearch.put("REFERENCE_DESCRIPTION", "external_reference_text");
    targetPds3ToSearch.put("SEARCH/ACCESS DATA", "resource_link");
  }

  public static final Map<String, String> msnPds3ToRegistry = new LinkedHashMap<String, String>();
  static {
    msnPds3ToRegistry.put("MISSION_NAME", "mission_name");
    msnPds3ToRegistry.put("MISSION_ALIAS", "alternate_id");
    msnPds3ToRegistry.put("MISSION_START_DATE", "mission_start_date");
    msnPds3ToRegistry.put("MISSION_STOP_DATE", "mission_stop_date");
    msnPds3ToRegistry.put("MISSION_DESCRIPTION", "mission_description");
    msnPds3ToRegistry.put("MISSION_OBJECTIVES_SUMMARY", "mission_objectives_summary");
    msnPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_text");
  }

  public static final Map<String, String> msnPds3ToSearch = new LinkedHashMap<String, String>();
  static {
    msnPds3ToSearch.put("MISSION_NAME", "investigation_name");
    msnPds3ToSearch.put("MISSION_ALIAS", "investigation_name_or_alias");
    msnPds3ToSearch.put("MISSION_START_DATE", "investigation_start_date");
    msnPds3ToSearch.put("MISSION_STOP_DATE", "investigation_stop_date");
    msnPds3ToSearch.put("MISSION_DESCRIPTION", "investigation_description");
    msnPds3ToSearch.put("MISSION_OBJECTIVES_SUMMARY", "investigation_objectives_summary");
    msnPds3ToSearch.put("REFERENCE_DESCRIPTION", "external_reference_text");
  }

  public static final Map<String, String> dsPds3ToRegistry = new LinkedHashMap<String, String>();
  static {
    dsPds3ToRegistry.put("DATA_SET_NAME", "data_set_name");
    dsPds3ToRegistry.put("DATA_SET_ID", "data_set_id");
    dsPds3ToRegistry.put("NSSDC_DATA_SET_ID", "data_set_nssdc_collection_id");
    dsPds3ToRegistry.put("DATA_SET_TERSE_DESCRIPTION", "data_set_terse_description");
    dsPds3ToRegistry.put("DATASET_DESCRIPTION", "data_set_description");
    dsPds3ToRegistry.put("DATA_SET_RELEASE_DATE", "data_set_release_date");
    dsPds3ToRegistry.put("START_TIME", "data_set_start_date_time");
    dsPds3ToRegistry.put("STOP_TIME", "data_set_stop_date_time");
    dsPds3ToRegistry.put("MISSION_NAME", "mission_name");
    dsPds3ToRegistry.put("MISSION_START_DATE", "mission_start_date");
    dsPds3ToRegistry.put("MISSION_STOP_DATE", "mission_stop_date");
    dsPds3ToRegistry.put("TARGET_NAME", "target_name");
    dsPds3ToRegistry.put("TARGET_TYPE", "target_type");
    dsPds3ToRegistry.put("INSTRUMENT_HOST_ID", "instrument_host_id");
    dsPds3ToRegistry.put("INSTRUMENT_NAME", "instrument_name");
    dsPds3ToRegistry.put("INSTRUMENT_ID", "instrument_id");
    dsPds3ToRegistry.put("INSTRUMENT_TYPE", "instrument_type");
    dsPds3ToRegistry.put("NODE_NAME", "node_name");
    dsPds3ToRegistry.put("ARCHIVE_STATUS", "data_set_archive_status");
    dsPds3ToRegistry.put("CONFIDENCE_LEVEL_NOTE", "data_set_confidence_level_note");
    dsPds3ToRegistry.put("CITATION_DESCRIPTION", "data_set_citation_text");
    dsPds3ToRegistry.put("ABSTRACT_TEXT", "data_set_abstract_description");
    dsPds3ToRegistry.put("PRODUCER_FULL_NAME", "data_set_producer_full_name"); // node_to_data_archivist
                                                                               // reference?????
                                                                               // person_name
    dsPds3ToRegistry.put("TELEPHONE_NUMBER", "person_telephone_number"); // node_to_data_archivist
                                                                         // person_telephone_number
    dsPds3ToRegistry.put("RESOURCES", "resources");
  }

  public static final Map<String, String> dsPds3ToSearch = new LinkedHashMap<String, String>();
  static {
    dsPds3ToSearch.put("DATA_SET_NAME", "data_set_name");
    dsPds3ToSearch.put("DATA_SET_ID", "data_set_id");
    dsPds3ToSearch.put("NSSDC_DATA_SET_ID", "nssdc_data_set_id");
    dsPds3ToSearch.put("DATA_SET_TERSE_DESCRIPTION", "data_set_terse_description");
    dsPds3ToSearch.put("DATA_SET_DESCRIPTION", "data_set_description");
    dsPds3ToSearch.put("DATA_SET_RELEASE_DATE", "data_set_release_date");
    dsPds3ToSearch.put("START_TIME", "start_time");
    dsPds3ToSearch.put("STOP_TIME", "stop_time");
    dsPds3ToSearch.put("MISSION_NAME", "investigation_name");
    dsPds3ToSearch.put("MISSION_START_DATE", "investigation_start_date");
    dsPds3ToSearch.put("MISSION_STOP_DATE", "investigation_stop_date");
    dsPds3ToSearch.put("TARGET_NAME", "target_name");
    dsPds3ToSearch.put("TARGET_TYPE", "target_type");
    dsPds3ToSearch.put("INSTRUMENT_HOST_ID", "instrument_host_id");
    dsPds3ToSearch.put("INSTRUMENT_NAME", "instrument_name");
    dsPds3ToSearch.put("INSTRUMENT_ID", "instrument_id");
    dsPds3ToSearch.put("INSTRUMENT_TYPE", "instrument_type");
    dsPds3ToSearch.put("NODE_NAME", "node_id");
    dsPds3ToSearch.put("ARCHIVE_STATUS", "archive_status");
    dsPds3ToSearch.put("CONFIDENCE_LEVEL_NOTE", "confidence_level_note");
    dsPds3ToSearch.put("CITATION_DESCRIPTION", "citation_description");
    dsPds3ToSearch.put("ABSTRACT_TEXT", "abstract_text");
    dsPds3ToSearch.put("PRODUCER_FULL_NAME", "full_name"); // node_to_data_archivist reference?????
                                                           // person_name
  }

  public static final Map<String, String> instPds3ToRegistry = new LinkedHashMap<String, String>();
  static {
    instPds3ToRegistry.put("INSTRUMENT_ID", "instrument_id");
    instPds3ToRegistry.put("INSTRUMENT_NAME", "instrument_name");
    instPds3ToRegistry.put("INSTRUMENT_TYPE", "instrument_type");
    instPds3ToRegistry.put("INSTRUMENT_HOST_ID", "instrument_host_id");
    instPds3ToRegistry.put("INSTRUMENT_DESC", "instrument_description");
    instPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_text");
  }

  public static final Map<String, String> instHostPds3ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    instHostPds3ToRegistry.put("INSTRUMENT_HOST_ID", "instrument_host_id");
    instHostPds3ToRegistry.put("INSTRUMENT_HOST_NAME", "instrument_host_name");
    instHostPds3ToRegistry.put("INSTRUMENT_HOST_TYPE", "instrument_host_type");
    instHostPds3ToRegistry.put("INSTRUMENT_HOST_DESC", "instrument_host_description");
    instHostPds3ToRegistry.put("REFERENCE_DESCRIPTION", "external_reference_text");
  }

  public static final Map<String, String> nodePds3ToRegistry = new LinkedHashMap<String, String>();
  static {
    nodePds3ToRegistry.put("NODE_ID", "alternate_id");
    nodePds3ToRegistry.put("NODE_NAME", "node_name");
    nodePds3ToRegistry.put("INSTITUTION_NAME", "node_institution_name");
    nodePds3ToRegistry.put("DESCRIPTION", "node_description");
  }

  public static final Map<String, String> personPds3ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    personPds3ToRegistry.put("PDS_USER_ID", "pds_user_id");
    personPds3ToRegistry.put("FULL_NAME", "person_name");
    personPds3ToRegistry.put("TELEPHONE_NUMBER", "person_telephone_number");
    personPds3ToRegistry.put("INSTITUTION_NAME", "person_institution_name");
    personPds3ToRegistry.put("NODE_NAME", "person_team_name");
    personPds3ToRegistry.put("ELECTRONIC_MAIL_ID", "person_electronic_mail_address");
  }

  public static final Map<String, String> volumePds3ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    volumePds3ToRegistry.put("VOLUME_ID", "volume_id");
    volumePds3ToRegistry.put("VOLUME_SET_ID", "volume_set_id");
    volumePds3ToRegistry.put("VOLUME_NAME", "volume_name");
    volumePds3ToRegistry.put("VOLUME_VER_ID", "volume_version_id");
    volumePds3ToRegistry.put("PUBLISHED_DATE", "volume_publication_date");
    volumePds3ToRegistry.put("VOLUME_DESC", "volume_description");
    // volumePds3ToRegistry.put("LABEL_REV_NOTE", "label_rev_note"
  }

  public static final Map<String, String> bundlePds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    bundlePds4ToRegistry.put("IDENTIFIER", "identifier");
    bundlePds4ToRegistry.put("NAME", "title");
    bundlePds4ToRegistry.put("TYPE", "bundle_type");
    bundlePds4ToRegistry.put("DESCRIPTION", "bundle_description");
  }

  public static final Map<String, String> bundlePds4ToSearch = new LinkedHashMap<String, String>();
  static {
    bundlePds4ToSearch.put("IDENTIFIER", "identifier");
    bundlePds4ToSearch.put("NAME", "title");
    bundlePds4ToSearch.put("TYPE", "bundle_type");
    bundlePds4ToSearch.put("DESCRIPTION", "description");
    bundlePds4ToSearch.put("SEARCH/ACCESS DATA", "resource_ref");
  }

  public static final Map<String, String> bundleCitationPds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    bundleCitationPds4ToRegistry.put("DIGITAL OBJECT IDENTIFIER (DOI)", "citation_doi");
    bundleCitationPds4ToRegistry.put("AUTHORS", "citation_author_list");
    bundleCitationPds4ToRegistry.put("EDITORS", "citation_editor_list");
    bundleCitationPds4ToRegistry.put("PUBLICATION YEAR", "citation_publication_year");
    bundleCitationPds4ToRegistry.put("DESCRIPTION", "citation_description");
  }

  public static final List<String> authorOrganizationFields = new ArrayList<String>();
  static {
    authorOrganizationFields.add("citation_author_organization_name");
  }

  public static final List<String> authorPersonFields = new ArrayList<String>();
  static {
    authorPersonFields.add("citation_author_person_given_name");
    authorPersonFields.add("citation_author_person_family_name");
    authorPersonFields.add("citation_author_person_affiliation_organization_name");
  }

  public static final List<String> editorOrganizationFields = new ArrayList<String>();
  static {
    editorOrganizationFields.add("citation_editor_organization_name");
  }

  public static final List<String> editorPersonFields = new ArrayList<String>();
  static {
    editorPersonFields.add("citation_editor_person_given_name");
    editorPersonFields.add("citation_editor_person_family_name");
    editorPersonFields.add("citation_editor_person_affiliation_organization_name");
  }

  public static final Map<String, String> bundleContextPds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    bundleContextPds4ToRegistry.put("START DATE TIME", "observation_start_date_time");
    bundleContextPds4ToRegistry.put("STOP DATE TIME", "observation_stop_date_time");
    bundleContextPds4ToRegistry.put("LOCAL MEAN SOLAR TIME", "observation_local_mean_solar_time");
    bundleContextPds4ToRegistry.put("LOCAL TRUE SOLAR TIME", "observation_local_true_solar_time");
    bundleContextPds4ToRegistry.put("SOLAR LONGITUDE", "observation_solar_longitude");
    bundleContextPds4ToRegistry.put("PRIMARY RESULT PURPOSE", "primary_result_purpose");
    bundleContextPds4ToRegistry.put("PRIMARY RESULT PROCESSING LEVEL",
        "primary_result_processing_level");
    bundleContextPds4ToRegistry.put("PRIMARY RESULT DESCRIPTION", "primary_result_description");
    bundleContextPds4ToRegistry.put("PRIMARY RESULT WAVELENGTH RANGE",
        "primary_result_wavelength_range");
    bundleContextPds4ToRegistry.put("PRIMARY RESULT DOMAIN", "primary_result_domain");
    bundleContextPds4ToRegistry.put("PRIMARY RESULT DISCIPLINE NAME",
        "primary_result_discipline_name");
    // bundleContextPds4ToRegistry.put("PRIMARY RESULT FACET1", "primary_result_facet1");
    // bundleContextPds4ToRegistry.put("PRIMARY RESULT SUBFACET1", "primary_result_subfacet1");
    // bundleContextPds4ToRegistry.put("PRIMARY RESULT FACET2", "primary_result_facet2");
    // bundleContextPds4ToRegistry.put("PRIMARY RESULT SUBFACET2", "primary_result_subfacet2");
    bundleContextPds4ToRegistry.put("INVESTIGATION", "investigation_name");
    bundleContextPds4ToRegistry.put("OBSERVING SYSTEM", "observing_system_name");
    bundleContextPds4ToRegistry.put("OBSERVING SYSTEM COMPONENT",
        "observing_system_component_name");
    bundleContextPds4ToRegistry.put("TARGET", "target_name");
    bundleContextPds4ToRegistry.put("REFERENCES", "external_reference_text");
  }

  public static final Map<String, String> collectionPds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    collectionPds4ToRegistry.put("IDENTIFIER", "identifier");
    collectionPds4ToRegistry.put("NAME", "title");
    collectionPds4ToRegistry.put("TYPE", "collection_type");
  }

  public static final Map<String, String> collectionPds4ToSearch =
      new LinkedHashMap<String, String>();
  static {
    collectionPds4ToSearch.put("IDENTIFIER", "identifier");
    collectionPds4ToSearch.put("NAME", "title");
    collectionPds4ToSearch.put("TYPE", "collection_type");
    collectionPds4ToSearch.put("DESCRIPTION", "description");
    collectionPds4ToSearch.put("SEARCH/ACCESS DATA", "resource_ref");
  }

  public static final Map<String, String> documentPds4ToSearch =
      new LinkedHashMap<String, String>();
  static {
    documentPds4ToSearch.put("IDENTIFIER", "identifier");
    documentPds4ToSearch.put("NAME", "title");
    documentPds4ToSearch.put("DESCRIPTION", "description");
    documentPds4ToSearch.put("SEARCH/ACCESS DATA", "resource_ref");
  }

  public static final Map<String, String> targetPds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    targetPds4ToRegistry.put("IDENTIFIER", "identifier");
    targetPds4ToRegistry.put("NAME", "target_name");
    targetPds4ToRegistry.put("TYPE", "target_type");
    targetPds4ToRegistry.put("DESCRIPTION", "target_description");
    targetPds4ToRegistry.put("REFERENCES", "external_reference_text");
  }

  public static final Map<String, String> msnPds4ToRegistry = new LinkedHashMap<String, String>();
  static {
    msnPds4ToRegistry.put("IDENTIFIER", "identifier");
    msnPds4ToRegistry.put("NAME", "investigation_name");
    msnPds4ToRegistry.put("TYPE", "investigation_type");
    msnPds4ToRegistry.put("DESCRIPTION", "investigation_description");
    msnPds4ToRegistry.put("START DATE", "investigation_start_date");
    msnPds4ToRegistry.put("STOP DATE", "investigation_stop_date");
    msnPds4ToRegistry.put("REFERENCES", "external_reference_text");
  }

  public static final Map<String, String> instCtxPds4ToSearch = new LinkedHashMap<String, String>();
  static {
    instCtxPds4ToSearch.put("IDENTIFIER", "identifier");
    instCtxPds4ToSearch.put("NAME", "instrument_name");
    instCtxPds4ToSearch.put("TYPE", "instrument_type");
    instCtxPds4ToSearch.put("DESCRIPTION", "instrument_description");
    instCtxPds4ToSearch.put("MODEL IDENTIFIER", "instrument_model_id");
    instCtxPds4ToSearch.put("NAIF INSTRUMENT IDENTIFIER", "instrument_naif_id");
    instCtxPds4ToSearch.put("SERIAL NUMBER", "instrument_serial_number");
    instCtxPds4ToSearch.put("REFERENCES", "external_reference_text");
  }

  public static final Map<String, String> instHostCtxPds4ToSearch =
      new LinkedHashMap<String, String>();
  static {
    instHostCtxPds4ToSearch.put("IDENTIFIER", "identifier");
    instHostCtxPds4ToSearch.put("NAME", "instrument_host_name");
    // instHostCtxPds4ToSearch.put("VERSION IDENTIFIER", "instrument_host_version_id");
    instHostCtxPds4ToSearch.put("TYPE", "instrument_host_type");
    instHostCtxPds4ToSearch.put("DESCRIPTION", "instrument_host_description");
    instHostCtxPds4ToSearch.put("NAIF INSTRUMENT IDENTIFIER", "instrument_host_naif_id");
    instHostCtxPds4ToSearch.put("SERIAL NUMBER", "instrument_host_serial_number");
    instHostCtxPds4ToSearch.put("REFERENCES", "external_reference_text");
  }

  public static final Map<String, String> resrcCtxPds4ToSearch =
      new LinkedHashMap<String, String>();
  static {
    resrcCtxPds4ToSearch.put("IDENTIFIER", "identifier");
    resrcCtxPds4ToSearch.put("NAME", "resource_name");
    resrcCtxPds4ToSearch.put("TYPE", "resource_type");
    resrcCtxPds4ToSearch.put("DESCRIPTION", "resource_description");
    resrcCtxPds4ToSearch.put("SEARCH/ACCESS DATA", "resources");
    // resrcCtxPds4ToSearch.put("NAIF INSTRUMENT IDENTIFIER", "instrument_host_naif_id");
    // resrcCtxPds4ToSearch.put("SERIAL NUMBER", "instrument_host_serial_number");
    // resrcCtxPds4ToSearch.put("REFERENCES", "external_reference_text");
  }

  public static final Map<String, String> observationalPds4ToSearch =
      new LinkedHashMap<String, String>();
  static {
    observationalPds4ToSearch.put("IDENTIFIER", "identifier");
    observationalPds4ToSearch.put("NAME", "title");
    observationalPds4ToSearch.put("TYPE", "data_class");
    observationalPds4ToSearch.put("FILE(S)", "file_name");
  }

  public static final Map<String, String> telescopePds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    telescopePds4ToRegistry.put("IDENTIFIER", "identifier");
    telescopePds4ToRegistry.put("NAME", "title");
    telescopePds4ToRegistry.put("DESCRIPTION", "description");
    telescopePds4ToRegistry.put("FACILITY", "facility_ref");
    telescopePds4ToRegistry.put("APERTURE", "telescope_aperture");
    telescopePds4ToRegistry.put("LONGITUDE", "telescope_longitude");
    telescopePds4ToRegistry.put("LATITUDE", "telescope_latitude");
    telescopePds4ToRegistry.put("ALTITUDE", "telescope_altitude");
    telescopePds4ToRegistry.put("COORIDINATE SOURCE", "telescope_coordinate_source");
    telescopePds4ToRegistry.put("INVESTIGATION(S)", "investigation_ref");
    telescopePds4ToRegistry.put("INSTRUMENT(S)", "instrument_ref");
  }

  public static final Map<String, String> facilityPds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    facilityPds4ToRegistry.put("IDENTIFIER", "identifier");
    facilityPds4ToRegistry.put("NAME", "title");
    facilityPds4ToRegistry.put("TYPE", "facility_type");
    facilityPds4ToRegistry.put("DESCRIPTION", "description");
    facilityPds4ToRegistry.put("ADDRESS", "facility_address");
    facilityPds4ToRegistry.put("COUNTRY", "facility_country");
    facilityPds4ToRegistry.put("INVESTIGATION(S)", "investigation_ref");
    facilityPds4ToRegistry.put("TELESCOPE(S)", "telescope_ref");
  }

  public static final Map<String, String> airbornePds4ToRegistry =
      new LinkedHashMap<String, String>();
  static {
    airbornePds4ToRegistry.put("IDENTIFIER", "identifier");
    airbornePds4ToRegistry.put("NAME", "title");
    airbornePds4ToRegistry.put("TYPE", "airborne_type");
    airbornePds4ToRegistry.put("DESCRIPTION", "description");
  }
}
