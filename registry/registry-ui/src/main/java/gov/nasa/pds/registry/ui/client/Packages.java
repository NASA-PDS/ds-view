// Copyright 2009, by the California Institute of Technology.
// ALL RIGHTS RESERVED. United States Government sponsorship acknowledged.
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
package gov.nasa.pds.registry.ui.client;

import gov.nasa.pds.registry.ui.client.Tab.TabInfo;
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackage;
import gov.nasa.pds.registry.ui.shared.ViewRegistryPackages;
import gov.nasa.pds.registry.ui.shared.ViewSlot;
import gov.nasa.pds.registry.ui.shared.InputContainer;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.PopupPanel;

import com.google.gwt.gen2.table.override.client.FlexTable;
import com.google.gwt.gen2.table.override.client.FlexTable.FlexCellFormatter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import com.google.gwt.gen2.table.client.CachedTableModel;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGridBulkRenderer;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.PagingOptions;
import com.google.gwt.gen2.table.client.PagingScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.TableDefinition;
import com.google.gwt.gen2.table.client.DefaultRowRenderer;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;

import com.google.gwt.gen2.table.event.client.RowCountChangeEvent;
import com.google.gwt.gen2.table.event.client.RowCountChangeHandler;
import com.google.gwt.gen2.table.event.client.RowSelectionEvent;
import com.google.gwt.gen2.table.event.client.RowSelectionHandler;
import com.google.gwt.gen2.table.event.client.TableEvent.Row;
import com.google.gwt.gen2.table.event.client.PageCountChangeEvent;
import com.google.gwt.gen2.table.event.client.PageCountChangeHandler;
import com.google.gwt.gen2.table.client.AbstractScrollTable.SortPolicy;
import com.google.gwt.gen2.table.client.SelectionGrid.SelectionPolicy;
import java.util.List;
import java.util.Set;

/**
 * A tab to browse the registered packages. 
 * 
 * @author hyunlee
 * 
 */
public class Packages extends Tab {
	
	public static TabInfo init() {
		return new TabInfo("Packages", "") {
			public Tab createInstance() {
				return new Packages();
			}
			
			public String getColor() {
				return "#14386D";
			}
			
			public String getName() {
				return "Packages";
			}
		};
	}
	
	private VerticalPanel panel = new VerticalPanel();	
	private VerticalPanel scrollPanel = new VerticalPanel();
	private FlexTable layout = new FlexTable();

	/**
	 * The dialog box that displays product details.
	 */
	protected final DialogBox productDetailsBox = new DialogBox();

	/**
	 * A panel used for layout within the dialogBox
	 */
	protected final VerticalPanel dialogVPanel = new VerticalPanel();

	protected final HTML loadingPlaceholder = new HTML("Loading packages...");

	/**
	 * The close button used by the dialogBox to close the dialog.
	 */
	protected Button closeButton;
	
	/**
     * The {@link PackageTableModel}.
     */
	private PackageTableModel tableModel = null;
	
	/**
	 * The {@link DefaultTableDefinition}.
	 */
	private DefaultTableDefinition<ViewRegistryPackage> tableDefinition = null;
	
	protected HTML recordCountContainer = new HTML("");
	
	/**
	 * The {@link PagingScrollTable}.
	 */
	protected PagingScrollTable<ViewRegistryPackage> pagingScrollTable = null;
	
	/**
	 * The {@link CachedTableModel} around the main table model.
	 */
	private CachedTableModel<ViewRegistryPackage> cachedTableModel = null;
	
	private Packages instance = null;
	
	/**
	 * 
	 * @return <code>this</code> instance
	 */
	public Packages get() {
		return instance;
	}
	
	/**
	 * @return the data table. This is the underlying grid for displaying data,
	 *         not just a data model.
	 */
	public FixedWidthGrid getDataTable() {
		return getPagingScrollTable().getDataTable();
	}
	
	/**
	 * @return the {@link PagingScrollTable}
	 */
	public PagingScrollTable<ViewRegistryPackage> getPagingScrollTable() {
		return this.pagingScrollTable;
	}
	
	public Packages() {
		panel.clear();
		instance = this;	
		panel.setSpacing(8);
		panel.setWidth("100%");
		
        initWidget(panel);     
        
        this.initLayout();
        
        // Initialize product detail popup
		this.initProductDetailPopup();

        this.initTable();
        
        this.initPaging();
        
        // HACK: A bug in PagingScrollTable causes the paging widget to not
        // update when new data from an RPC call comes in. This forces the
        // paging options to update with the correct num pages by triggering a
        // recount of the pages based on the row count.
        this.tableModel.addRowCountChangeHandler(new RowCountChangeHandler() {
            @Override
            public void onRowCountChange(RowCountChangeEvent event) {
                get().getPagingScrollTable().setPageSize(RegistryUI.PAGE_SIZE);
            }
        });
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

        // add layout to main panel
        panel.add(this.layout);

        // add enough rows for all of the required widgets
        // filter options
        this.layout.insertRow(0);
        // scroll table wrapper
        this.layout.insertRow(0);
        // paging options
        this.layout.insertRow(0);

        // add style name to scroll table container
        this.layout.getCellFormatter().addStyleName(1, 0, "scrollTableWrapper");

        // add the scroll panel
        this.layout.setWidget(1, 0, this.scrollPanel);

        // add title to scroll table
        this.scrollPanel.add(new HTML(
                "<div class=\"title\">Packages Registry</div>"));

        // add record count container
        this.layout.setWidget(3, 0, this.recordCountContainer);
    }
    
    /**
	 * Initialize scroll table and behaviors.
	 */
	protected void initTable() {
		// create table model that knows to make rest call for rows and caches
		// filter settings for calls
		this.tableModel = new PackageTableModel();

		// create model to contain the cached data
		this.cachedTableModel = new CachedTableModel<ViewRegistryPackage>(
				this.tableModel);

		// set cache for rows before start row
		this.cachedTableModel.setPreCachedRowCount(RegistryUI.PAGE_SIZE);

		// set cache for rows after end row
		this.cachedTableModel.setPostCachedRowCount(RegistryUI.PAGE_SIZE);

		// create a table definition, this defines columns, layout, row colors
		TableDefinition<ViewRegistryPackage> tableDef = createTableDefinition();

		// Create the scroll table
		this.pagingScrollTable = new PagingScrollTable<ViewRegistryPackage>(
				this.cachedTableModel, tableDef);

		// set the num rows to display per page
		this.pagingScrollTable.setPageSize(RegistryUI.PAGE_SIZE);

		// set content to display when there is no data
		this.pagingScrollTable.setEmptyTableWidget(new HTML(
				"<h2>There is no data to display</h2>"));

		// Setup the bulk renderer
		FixedWidthGridBulkRenderer<ViewRegistryPackage> bulkRenderer = new FixedWidthGridBulkRenderer<ViewRegistryPackage>(
				this.pagingScrollTable.getDataTable(), this.pagingScrollTable);
		this.pagingScrollTable.setBulkRenderer(bulkRenderer);

		// Setup the formatting
		this.pagingScrollTable.setCellPadding(0);
		this.pagingScrollTable.setCellSpacing(0);
		this.pagingScrollTable
				.setResizePolicy(ScrollTable.ResizePolicy.FILL_WIDTH);
		this.pagingScrollTable.setHeight(RegistryUI.TABLE_HEIGHT);

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
					System.out.println("Row selection event fired but no selected rows found.");
					return;
				}

				// since only one row can be selected, just get the first one
				int rowIndex = selected.iterator().next().getRowIndex();

				// get the product instance associated with that row
				ViewRegistryPackage product = get().getPagingScrollTable().getRowValue(
						rowIndex);

				// create grid for data, there are 7 fixed fields and fields for
				// each slot
				Grid detailTable = new Grid(7 + product.getSlots().size() + 5, 2);

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
				detailTable.setText(3, 0, "Version Name");
				detailTable.setText(3, 1, product.getVersionName());

				// guid
				detailTable.setText(4, 0, "GUID");
				detailTable.setText(4, 1, product.getGuid());

				// lid
				detailTable.setText(5, 0, "LID");
				detailTable.setText(5, 1, product.getLid());

				// home
				detailTable.setText(6, 0, "Home");
				detailTable.setText(6, 1, product.getHome());

				// slots
				int row = 7;
				
				List<ViewSlot> slots = product.getSlots();
				for (int i = 0; i < slots.size(); i++) {
					ViewSlot slot = slots.get(i);
					int curRow = i + row;
					
					detailTable.setText(curRow, 0, Products.getNice(slot.getName(),
							true, true));
					
					// check for long string....need to display differently (another popup???)
					String valuesString = Products.toSeparatedString(slot.getValues());
					if (valuesString.length()>200) 
						detailTable.setText(curRow, 1, valuesString.substring(0, 200) + "  ...");
					else
						detailTable.setText(curRow, 1, valuesString);					
					row = curRow;
				}
				
				// add styles to cells
				for (int i = 0; i < detailTable.getRowCount(); i++) {
					detailTable.getCellFormatter().setStyleName(i, 0,
							"detailLabel");
					detailTable.getCellFormatter().setStyleName(i, 1,
							"detailValue");
				}

				get().dialogVPanel.insert(detailTable, 0);

				// display, center and focus popup
				get().productDetailsBox.center();
				get().closeButton.setFocus(true);
			}
		};

		// add detail popup handler to rows
		this.pagingScrollTable.getDataTable().addRowSelectionHandler(handler);

		// add handler to update found element count
		this.pagingScrollTable
				.addPageCountChangeHandler(new PageCountChangeHandler() {
					@Override
					public void onPageCountChange(PageCountChangeEvent event) {
						int count = get().pagingScrollTable.getTableModel()
								.getRowCount();

						// display count in page
						// TODO: do number formatting and message formatting and
						// externalize
						get().recordCountContainer
								.setHTML("<div class=\"recordCount\">Num Records: "
										+ count + "</div>");
					}
				});

		// set selection policy
		SelectionGrid grid = get().getDataTable();
		grid.setSelectionPolicy(SelectionPolicy.ONE_ROW);

		// Add the scroll table to the layout
		this.scrollPanel.add(this.pagingScrollTable);
		this.scrollPanel.setWidth("100%");

		// add a formatter
		final FlexCellFormatter formatter = this.layout.getFlexCellFormatter();
		formatter.setWidth(1, 0, "100%");
		//formatter.setWidth(1,1,"100%"); // why???
		formatter.setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
	}
	
	/**
	 * Initialize the popup that will display package details.
	 * 
	 * Note that there are a variety of display options and behaviors available
	 * to this box. Only a portion of them are currently being leveraged.
	 */
	private void initProductDetailPopup() {
		// set title
		this.productDetailsBox.setText("Package Details");

		// make hide and show be animated
		this.productDetailsBox.setAnimationEnabled(true);

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
				get().productDetailsBox.hide();
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

		// add a vertical panel as master layout panel to the dialog box
		// contents
		this.productDetailsBox.setWidget(this.dialogVPanel);

		// set a style name associated with the dialog so that it can be styled
		// through external css
		this.productDetailsBox.addStyleName("detailBox");

		// add the close handler that de-selects the row that was used to
		// trigger the "show" of the dialog
		this.productDetailsBox.addCloseHandler(dialogCloseHandler);
	}

    /**
	 * Initialize the paging widget.
	 */
	public void initPaging() {
		PagingOptions pagingOptions = new PagingOptions(getPagingScrollTable());

		// add the paging widget to the last row of the layout
		this.layout.setWidget(2, 0, pagingOptions);
	}
	
	
	private TableDefinition<ViewRegistryPackage> createTableDefinition() {
		// Create the table definition
		this.tableDefinition = new DefaultTableDefinition<ViewRegistryPackage>();

		// Set the row renderer
		String[] rowColors = new String[] { "#FFFFCC", "#FFFFFF" };
		this.tableDefinition
				.setRowRenderer(new DefaultRowRenderer<ViewRegistryPackage>(rowColors));

		// service name
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Name") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getName();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// LID
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"LID") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getLid();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(150);
			columnDef.setColumnSortable(true);
			//columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}
		
		// System version name
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Version Name") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getVersionName();
				}

			};
			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			columnDef.setColumnSortable(true);
			columnDef.setColumnTruncatable(false);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// OBJECT TYPE
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Object Type") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getObjectType();
				}
			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		// STATUS
		{
			PackageColumnDefinition<String> columnDef = new PackageColumnDefinition<String>(
					"Status") {
				@Override
				public String getCellValue(ViewRegistryPackage rowValue) {
					return rowValue.getStatus();
				}

			};

			columnDef.setMinimumColumnWidth(50);
			columnDef.setPreferredColumnWidth(50);
			// columnDef.setMaximumColumnWidth(200);
			columnDef.setColumnSortable(true);
			this.tableDefinition.addColumnDefinition(columnDef);
		}

		return this.tableDefinition;
	}
	
	/**
	 * Action to take once the module has loaded.
	 */
	protected void onModuleLoaded() {
		// set page to first page, triggering call for data
		this.pagingScrollTable.gotoFirstPage();
	}

	public void onShow() {
	}
}