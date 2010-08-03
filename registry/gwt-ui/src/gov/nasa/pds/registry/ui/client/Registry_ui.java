package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.shared.InputContainer;
import gov.nasa.pds.registry.ui.shared.ViewProduct;
import gov.nasa.pds.registry.ui.shared.ViewSlot;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable;
import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.gwt.gen2.table.client.DefaultRowRenderer;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.PagingOptions;
import com.google.gwt.gen2.table.client.PagingScrollTable;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.TableDefinition;
import com.google.gwt.gen2.table.client.AbstractScrollTable.SortPolicy;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import com.google.gwt.gen2.table.event.client.RowCountChangeEvent;
import com.google.gwt.gen2.table.event.client.RowCountChangeHandler;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.TableEvent.Row;
import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point for the registry front end application. Front end currently
 * supplies browse, filter, and sort capabilities around registered products.
 * 
 * @author jagander
 * 
 */
@SuppressWarnings("nls")
public class Registry_ui implements EntryPoint {

	/**
	 * Default number of records for each page
	 */
	public static final int PAGE_SIZE = 50;

	/**
	 * A mapping to <code>this</code> so that anonymous subclasses, handlers,
	 * may get access to the containing class.
	 */
	private static Registry_ui instance = null;

	/**
	 * 
	 * @return <code>this</code> instance
	 */
	public static Registry_ui get() {
		return instance;
	}

	/**
	 * The main layout panel. All view components should be children of this.
	 */
	private FlexTable layout = new FlexTable();

	/**
	 * The dialog box that displays product details.
	 */
	protected final DialogBox dialogBox = new DialogBox();

	/**
	 * A panel used for layout within the dialogBox
	 */
	protected final VerticalPanel dialogVPanel = new VerticalPanel();

	/**
	 * The close button used by the dialogBox to close the dialog.
	 */
	protected Button closeButton;

	/**
	 * @return the data table. This is the underlying grid for displaying data,
	 *         not just a data model.
	 */
	public FixedWidthGrid getDataTable() {
		return getScrollTable().getDataTable();
	}

	/**
	 * @return the footer table. In this case, it is a repeat of the header row
	 *         but is not sortable.
	 */
	public FixedWidthFlexTable getFooterTable() {
		return getScrollTable().getFooterTable();
	}

	/**
	 * @return the header table. This is the header row of the data table. It is
	 *         separate from the data table itself so that it does not scroll
	 *         with the data. It also supports the table sorting.
	 */
	public FixedWidthFlexTable getHeaderTable() {
		return getScrollTable().getHeaderTable();
	}

	/**
	 * @return the scroll table.
	 */
	// TODO: unclear why scrollTable is needed separately from pagingScrollTable
	// but is based on example and will be investigated for necessity
	public AbstractScrollTable getScrollTable() {
		return this.pagingScrollTable;
	}

	/**
	 * The {@link CachedTableModel} around the main table model.
	 */
	private CachedTableModel<ViewProduct> cachedTableModel = null;

	/**
	 * The {@link PagingScrollTable}.
	 */
	protected PagingScrollTable<ViewProduct> pagingScrollTable = null;

	/**
	 * The {@link ProductTableModel}.
	 */
	private ProductTableModel tableModel = null;

	/**
	 * The {@link DefaultTableDefinition}.
	 */
	private DefaultTableDefinition<ViewProduct> tableDefinition = null;

	/**
	 * @return the cached table model
	 */
	public CachedTableModel<ViewProduct> getCachedTableModel() {
		return this.cachedTableModel;
	}

	/**
	 * @return the {@link PagingScrollTable}
	 */
	public PagingScrollTable<ViewProduct> getPagingScrollTable() {
		return this.pagingScrollTable;
	}

	/**
	 * @return the table definition of columns
	 */
	public DefaultTableDefinition<ViewProduct> getTableDefinition() {
		return this.tableDefinition;
	}

	/**
	 * @return the table model
	 */
	public ProductTableModel getTableModel() {
		return this.tableModel;
	}

	/*
	 * public void insertDataRow(int beforeRow) {
	 * getCachedTableModel().insertRow(beforeRow); }
	 */

	protected FlexTable getLayout() {
		return this.layout;
	}

	/**
	 * This is the entry point method that assembles the view and kicks off the
	 * RPC.
	 */
	public void onModuleLoad() {
		// assign instance for exterior retrieval
		instance = this;

		// Initialize and add the main layout to the page
		this.initLayout();

		// Initialize detail popup
		this.initDetailPopup();

		// initialize filter and search options
		this.initFilterOptions();

		// initialize scroll table
		this.initTable();

		// initialize paging widget
		this.initPaging();

		// HACK: A bug in PagingScrollTable causes the paging widget to not
		// update when new data from an RPC call comes in. This forces the
		// paging options to update with the correct num pages by triggering a
		// recount of the pages based on the row count.
		this.tableModel.addRowCountChangeHandler(new RowCountChangeHandler() {

			@Override
			public void onRowCountChange(RowCountChangeEvent event) {
				get().getPagingScrollTable().setPageSize(PAGE_SIZE);
			}
		});

		// Do any required post processing
		onModuleLoaded();
	}

	/**
	 * Initialize the master layout including inserting expected rows and title.
	 */
	private void initLayout() {
		// set basic formatting options
		this.layout.setWidth("100%");
		this.layout.setCellPadding(0);
		this.layout.setCellSpacing(0);

		// add layout to root panel
		RootPanel.get("productsContainer").add(this.layout);

		// add enough rows for all of the required widgets
		// label
		this.layout.insertRow(0);
		// filter options
		this.layout.insertRow(0);
		// scroll table
		this.layout.insertRow(0);
		// paging options
		this.layout.insertRow(0);

		// add table label
		// NOTE: this is actually not part of the table, it just renders above
		this.layout.setWidget(1, 0, new HTML(
				"<div class=\"title\">Product Registry</div>"));
	}

	/**
	 * Initialize the popup that will display product details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initDetailPopup() {

		// set title
		this.dialogBox.setText("Product Details");

		// make hide and show be animated
		this.dialogBox.setAnimationEnabled(true);

		// create a close button
		this.closeButton = new Button("Close");

		// add the close button to the panel
		this.dialogVPanel.add(this.closeButton);

		// create a handler for the click of the close button to hide the detail
		// box
		ClickHandler closButtonHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// hide the detail box
				get().dialogBox.hide();
			}
		};

		// create a handler for the detail box hide that de-selects the row that
		// was selected when the dialog box was shown. This is separate from the
		// close handler as there is a click-outside test that also triggers a
		// hide of the dialog box
		CloseHandler<PopupPanel> dialogCloseHandler = new CloseHandler<PopupPanel>() {

			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				// clear selection and allow row to be clicked again
				get().getPagingScrollTable().getDataTable().deselectAllRows();

				// remove data from popup
				get().dialogVPanel.remove(0);
			}

		};

		// add the handler to the button
		this.closeButton.addClickHandler(closButtonHandler);

		// We can set the id of a widget by accessing its Element
		// this.closeButton.getElement().setId("closeButton");

		// add a vertical panel as master layout panel to the dialog box
		// contents
		this.dialogBox.setWidget(this.dialogVPanel);

		// set a style name associated with the dialog so that it can be styled
		// through external css
		this.dialogBox.addStyleName("detailBox");

		// enable hiding the dialog when clicking outside of its constraints
		this.dialogBox.setAutoHideEnabled(true);

		// add the close handler that de-selects the row that was used to
		// trigger
		// the "show" of the dialog
		this.dialogBox.addCloseHandler(dialogCloseHandler);
	}

	/**
	 * Initialize the filter and search options for the table data.
	 */
	public void initFilterOptions() {

		// create filter and search elements
		HorizontalPanel inputTable = new HorizontalPanel();
		// set alignment to bottom so that button is positioned correctly
		inputTable.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		// add input table to layout
		this.layout.setWidget(0, 0, inputTable);

		// create textual input for global unique identifier search
		final TextBox guidInput = new TextBox();
		guidInput.setName("guid");
		InputContainer guidInputWrap = new InputContainer("GUID", guidInput);
		inputTable.add(guidInputWrap);

		// create textual input for local id search
		final TextBox lidInput = new TextBox();
		lidInput.setName("lid");
		InputContainer lidInputWrap = new InputContainer("LID", lidInput);
		inputTable.add(lidInputWrap);

		// create textual input for name search
		final TextBox nameInput = new TextBox();
		nameInput.setName("name");
		InputContainer nameInputWrap = new InputContainer("Name", nameInput);
		inputTable.add(nameInputWrap);

		// create dropdown input for submitter
		final TextBox submitterInput = new TextBox();
		submitterInput.setName("submitter");
		InputContainer submitterInputWrap = new InputContainer("Submitter",
				submitterInput);
		inputTable.add(submitterInputWrap);

		// create dropdown input for object type
		// TODO: list should be exhaustive, get from enum or build process?
		final ListBox objectsInput = new ListBox(false);
		objectsInput.setName("objectType");
		objectsInput.addItem("Any Object Type", "-1");
		objectsInput.addItem("Target");
		objectsInput.addItem("Guest");
		InputContainer objectsInputWrap = new InputContainer("Object Type",
				objectsInput);
		inputTable.add(objectsInputWrap);

		// create dropdown input for status type
		final ListBox statusInput = new ListBox(false);
		// TODO: comes from enum ObjectStatus so iterate over values? or is
		// order more important?
		statusInput.setName("statusType");
		statusInput.addItem("Any Status", "-1");
		statusInput.addItem("SUBMITTED");
		statusInput.addItem("APPROVED");
		statusInput.addItem("DEPRECATED");
		statusInput.addItem("WITHDRAWN");
		InputContainer statusInputWrap = new InputContainer("Status",
				statusInput);
		inputTable.add(statusInputWrap);

		// create button for doing an update
		final Button updateButton = new Button("Update");
		InputContainer updateButtonWrap = new InputContainer(null, updateButton);
		inputTable.add(updateButtonWrap);

		// add handler to leverage the update button
		// TODO: determine if there is a form widget that's better to use here,
		// not sure this is reasonable as we're not doing a real submit, it's
		// triggering a javascript event that is integral to the behavior of the
		// scroll table
		updateButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// clear cache as data will be invalid after filter
				get().getCachedTableModel().clearCache();

				// get table model that holds filters
				ProductTableModel tablemodel = get().getTableModel();

				// clear old filters
				tablemodel.clearFilters();

				// get values from input

				// guid
				String guid = guidInput.getValue();
				if (!guid.equals("")) {
					tablemodel.addFilter("guid", guid);
				}

				// lid
				String lid = lidInput.getValue();
				if (!lid.equals("")) {
					tablemodel.addFilter("lid", lid);
				}

				// name
				String name = nameInput.getValue();
				if (!name.equals("")) {
					tablemodel.addFilter("name", name);
				}

				// submitter
				String submitter = submitterInput.getValue();
				if (!submitter.equals("")) {
					tablemodel.addFilter("submitter", submitter);
				}

				// object type
				String objectType = objectsInput.getValue(objectsInput
						.getSelectedIndex());
				// set object type filter if set to something specific
				if (!objectType.equals("-1")) {
					tablemodel.addFilter("objectType", objectType);
				}

				// status
				String status = statusInput.getValue(statusInput
						.getSelectedIndex());
				// set status if set to something specific
				if (!status.equals("-1")) {
					tablemodel.addFilter("status", status);
				}

				// HACK: row count of zero causes cache check to fail and table
				// not be updated
				get().getTableModel().setRowCount(1000);

				// go back to first page and force update
				get().getPagingScrollTable().gotoPage(0, true);

			}
		});
	}

	/**
	 * Initialize scroll table and behaviors.
	 */
	protected void initTable() {
		// create table model that knows to make rest call for rows and caches
		// filter settings for calls
		this.tableModel = new ProductTableModel();

		// create model to contain the cached data
		this.cachedTableModel = new CachedTableModel<ViewProduct>(
				this.tableModel);

		// set cache for rows before start row
		this.cachedTableModel.setPreCachedRowCount(50);

		// set cache for rows after end row
		this.cachedTableModel.setPostCachedRowCount(50);

		// create a table definition, this defines columns, layout, row colors
		TableDefinition<ViewProduct> tableDef = createTableDefinition();

		// Create the scroll table
		this.pagingScrollTable = new PagingScrollTable<ViewProduct>(
				this.cachedTableModel, tableDef);

		// set the num rows to display per page
		this.pagingScrollTable.setPageSize(PAGE_SIZE);

		// set content to display when there is no data
		this.pagingScrollTable.setEmptyTableWidget(new HTML(
				"There is no data to display"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<ViewProduct> bulkRenderer = new FixedWidthGridBulkRenderer<ViewProduct>(
				this.pagingScrollTable.getDataTable(), this.pagingScrollTable);
		this.pagingScrollTable.setBulkRenderer(bulkRenderer);

		// Setup the formatting
		this.pagingScrollTable.setCellPadding(0);
		this.pagingScrollTable.setCellSpacing(0);
		this.pagingScrollTable
				.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
		this.pagingScrollTable.setHeight("400px");

		// allow multiple cell resizing
		this.pagingScrollTable
				.setColumnResizePolicy(ScrollTable.ColumnResizePolicy.MULTI_CELL);

		// only allow a single column sort
		// TODO: implement multi-column sort? appears supported on the REST side
		this.pagingScrollTable.setSortPolicy(SortPolicy.SINGLE_CELL);

		// create row handler to fill in and display popup
		RowSelectionHandler handler = new RowSelectionHandler() {

			public void onRowSelection(RowSelectionEvent event) {

				// get selected rows
				Set<Row> selected = event.getSelectedRows();
				if (selected.size() == 0) {
					// TODO: out is for debugging purposes, fix for
					// erroneously fired events is pending
					System.out
							.println("Row selection event fired but no selected rows found.");
					return;
				}

				// since only one row can be selected, just get the first one
				int rowIndex = selected.iterator().next().getRowIndex();

				// get the product instance associated with that row
				ViewProduct product = get().getPagingScrollTable().getRowValue(
						rowIndex);
				// NOTE: if need to look up object specifically, use lid. This
				// will be necessary if the get products call gets less data,
				// reducing the response size and the cost of generating row
				// data.

				// create grid for data, there are 8 fixed fields and fields for
				// each slot
				Grid detailTable = new Grid(8 + product.getSlots().size(), 2);

				// set each field label and the values

				// name
				detailTable.setText(0, 0, "Name");
				detailTable.setText(0, 1, product.getName());

				// type
				detailTable.setText(1, 0, "Object Type");
				detailTable.setText(1, 1, product.getObjectType());

				// status
				detailTable.setText(2, 0, "Status");
				detailTable.setText(2, 1, product.getStatus());

				// version
				detailTable.setText(3, 0, "Version");
				detailTable.setText(3, 1, product.getVersion());

				// user version
				detailTable.setText(4, 0, "User Version");
				detailTable.setText(4, 1, product.getUserVersion());

				// guid
				detailTable.setText(5, 0, "GUID");
				detailTable.setText(5, 1, product.getGuid());

				// lid
				detailTable.setText(6, 0, "LID");
				detailTable.setText(6, 1, product.getLid());

				// home
				detailTable.setText(7, 0, "Home");
				detailTable.setText(7, 1, product.getHome());

				// slots
				List<ViewSlot> slots = product.getSlots();
				for (int i = 0; i < slots.size(); i++) {
					ViewSlot slot = slots.get(i);
					int curRow = i + 8;
					String valuesString = toSeparatedString(slot.getValues());

					detailTable.setText(curRow, 0, getNice(slot.getName(),
							true, true));
					detailTable.setText(curRow, 1, valuesString);
				}

				// add styles to cells
				for (int i = 0; i < detailTable.getRowCount(); i++) {
					detailTable.getCellFormatter().setStyleName(i, 0,
							"detailLabel");
					detailTable.getCellFormatter().setStyleName(i, 1,
							"detailValue");
				}

				// add new data as first element in vertical stack, which should
				// be the close button since last data table was removed
				get().dialogVPanel.insert(detailTable, 0);

				// display, center and focus popup
				get().dialogBox.center();
				get().closeButton.setFocus(true);
			}

		};

		// add detail popup handler to rows
		this.pagingScrollTable.getDataTable().addRowSelectionHandler(handler);

		// set selection policy
		SelectionGrid grid = get().getDataTable();
		grid.setSelectionPolicy(SelectionPolicy.ONE_ROW);

		// Add the scroll table to the layout
		this.layout.setWidget(2, 0, this.pagingScrollTable);

		// add a formatter
		final FlexCellFormatter formatter = this.layout.getFlexCellFormatter();
		formatter.setWidth(1, 1, "100%");
		formatter.setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
	}

	/**
	 * Initialize the paging widget.
	 */
	public void initPaging() {
		PagingOptions pagingOptions = new PagingOptions(getPagingScrollTable());

		// add the paging widget to the last row of the layout
		this.layout.setWidget(3, 0, pagingOptions);
	}

	/**
	 * Action to take once the module has loaded.
	 */
	protected void onModuleLoaded() {
		// set page to first page, triggering call for data
		this.pagingScrollTable.gotoFirstPage();
	}

	/**
	 * @return the {@link TableDefinition} with all ColumnDefinitions defined.
	 */
	private TableDefinition<ViewProduct> createTableDefinition() {
		// Create the table definition
		this.tableDefinition = new DefaultTableDefinition<ViewProduct>();

		// Set the row renderer
		String[] rowColors = new String[] { "#FFFFCC", "#FFFFFF" };
		this.tableDefinition
				.setRowRenderer(new DefaultRowRenderer<ViewProduct>(rowColors));
		// Name, LID, userVersion, ObjectType, Status

		// product name
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"Name") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getName();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// LID
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"LID") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getLid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// User Version
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"User Version") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getUserVersion();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(100);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// OBJECT TYPE
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"Object Type") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getObjectType();
				}
			};

			columnDef.setMinimumColumnWidth(100);
			columnDef.setPreferredColumnWidth(100);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// STATUS
		{
			ProductColumnDefinition<String> columnDef = new ProductColumnDefinition<String>(
					"Status") {
				@Override
				public String getCellValue(ViewProduct rowValue) {
					return rowValue.getStatus();
				}

			};

			columnDef.setMinimumColumnWidth(100);
			columnDef.setPreferredColumnWidth(100);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		return this.tableDefinition;
	}

	/**
	 * Get a display version of an underscore separated string. This is useful
	 * for the slot display names.
	 * 
	 * Note that this is copied from the pds-utils project, StrUtils. It was
	 * copied wholesale due to dependency issues in that class and the normal
	 * functioning of GWT client classes.
	 */
	public static String getNice(final String string, final Boolean capitalize,
			final Boolean replaceUnderscore) {
		String out = string.trim();
		if (replaceUnderscore == null || replaceUnderscore) {
			out = out.replaceAll("_", " ");
		}
		if (capitalize == null || capitalize) {
			out = toUpper(out);
		}
		return out;
	}

	/**
	 * Uppercase each word of a string.
	 * 
	 * Note that this is copied from the pds-utils project, StrUtils. It was
	 * copied wholesale due to dependency issues in that class and the normal
	 * functioning of GWT client classes.
	 */
	public static String toUpper(final String string) {
		String out = "";
		final String source = string.toLowerCase();
		String[] parts = source.split(" ");
		for (int i = 0; i < parts.length; i++) {
			final String part = parts[i];
			out += part.substring(0, 1).toUpperCase() + part.substring(1);
			if (i < parts.length - 1) {
				out += " ";
			}
		}
		return out;
	}

	/**
	 * Convert a list to a comma separated string.
	 * 
	 * Note that this is copied from the pds-utils project, StrUtils. It was
	 * copied wholesale due to dependency issues in that class and the normal
	 * functioning of GWT client classes.
	 */
	public static String toSeparatedString(final List<?> list) {
		String returnString = "";

		Iterator<?> it = list.iterator();
		while (it.hasNext()) {
			Object element = it.next();
			String val = null;
			val = element.toString();
			returnString += val;
			if (it.hasNext()) {
				returnString += ", ";
			}
		}
		return returnString;
	}

}
