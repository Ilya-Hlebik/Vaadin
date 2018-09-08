package com.gp.vaadin.demo.UI;


import com.gp.vaadin.demo.Entity.Hotel;
import com.gp.vaadin.demo.Services.CategoryService;
import com.gp.vaadin.demo.Services.HotelService;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.SerializablePredicate;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;

import java.util.Set;

public class ViewForHotel extends VerticalLayout implements View {

    private final HotelService hotelService = HotelService.getInstance();
    private final TextField filterByName = new TextField();
    private final TextField filterByAddress = new TextField();
    private final Button addHotel = new Button("Add hotel");
    private final Button deleteHotel = new Button("Delete hotel");
    private final Button editHotel = new Button("Edit hotel");
    private final Button bulkUpdateBtn = new Button("Bulk Update");
    private final ViewForPopUp viewForPopUp = new ViewForPopUp(this);

    private final Grid<Hotel> hotelGrid = new Grid<>();
    private final HotelEditForm hotelEditForm = new HotelEditForm(this);
    private final CategoryService categoryService = CategoryService.getInstance();
    private ListDataProvider<Hotel> hotelListDataProvider;

    public ViewForHotel() {

        filterByName.setPlaceholder("filter by name");
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        filterByAddress.setPlaceholder("filter by address");
        filterByAddress.setValueChangeMode(ValueChangeMode.LAZY);
        filterByName.addValueChangeListener(e -> hotelFiler());
        filterByAddress.addValueChangeListener(e -> hotelFiler());

        addHotel.addClickListener(e -> {
            hotelGrid.deselectAll();
            hotelEditForm.setHotel(new Hotel());
        });

        deleteHotel.addClickListener(e -> {
            Set<Hotel> selectedItems = hotelGrid.getSelectedItems();
            if (selectedItems.isEmpty()) return;
            hotelService.delete(selectedItems);
            updateList();
        });

        editHotel.addClickListener(e -> {
            Set<Hotel> selectedItems = hotelGrid.asMultiSelect().getSelectedItems();
            if (selectedItems.isEmpty()) return;
            Hotel hotel = selectedItems.iterator().next();
            hotelEditForm.setHotel(hotel);
        });

        bulkUpdateBtn.addClickListener(e -> {
            viewForPopUp.showPopUp(hotelGrid.getSelectedItems());
        });

        hotelGrid.addColumn(Hotel::getName).setCaption("Name");
        hotelGrid.addColumn(Hotel::getAddress).setCaption("Address");
        hotelGrid.addColumn(Hotel::getRating).setCaption("Rating");
        hotelGrid.addColumn(this::getCategory).setCaption("Category");
        hotelGrid.addColumn(HotelEditForm::getDate).setCaption("Operates From");
        hotelGrid.addColumn(Hotel::getDescription).setCaption("Description");
        hotelGrid.addColumn(hotel ->
                        "<a href='" + hotel.getUrl() + "' target='_target'>hotel info</a>",
                new HtmlRenderer()).setCaption("Url");
        hotelGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        hotelGrid.addSelectionListener(e -> {

            Set<Hotel> selectedHotels = e.getAllSelectedItems();
            if (selectedHotels != null && selectedHotels.size() == 1) {
                deleteHotel.setEnabled(true);
                editHotel.setEnabled(true);
                bulkUpdateBtn.setEnabled(false);
            } else if (selectedHotels != null && selectedHotels.size() > 1) {
                deleteHotel.setEnabled(true);
                editHotel.setEnabled(false);
                bulkUpdateBtn.setEnabled(true);
            } else {
                deleteHotel.setEnabled(false);
                editHotel.setEnabled(false);
                bulkUpdateBtn.setEnabled(false);
                hotelEditForm.setVisible(false);
            }
        });

        HorizontalLayout control = new HorizontalLayout(filterByName, filterByAddress, addHotel, deleteHotel, editHotel, bulkUpdateBtn);
        HorizontalLayout content = new HorizontalLayout(hotelGrid, hotelEditForm);

        bulkUpdateBtn.setEnabled(false);
        deleteHotel.setEnabled(false);
        editHotel.setEnabled(false);
        viewForPopUp.setPopupVisible(false);


        content.setMargin(false);
        content.setSpacing(false);
        content.setWidth("100%");
        hotelGrid.setWidth("100%");
        hotelEditForm.setMargin(new MarginInfo(false, false, false, true));
        content.setExpandRatio(hotelGrid, 30);
        content.setExpandRatio(hotelEditForm, 20);

        addComponents(control, content, viewForPopUp);
        setComponentAlignment(viewForPopUp, Alignment.MIDDLE_CENTER);

        updateList();
    }

    public void onUpdateHotels(Set<Hotel> hotels) {
        hotelService.updateHotels(hotels);
        updateList();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updateList();
    }

    private boolean isPassed(String text, String filter) {
        return filter == null || filter.isEmpty() || text.toLowerCase().contains(filter.toLowerCase());
    }

    private String getCategory(Hotel hotel) {
        return categoryService.getCategoryName(hotel.getCategory());
    }

    public void updateList() {
        hotelListDataProvider = DataProvider.ofCollection(hotelService.getAll());
        hotelGrid.setDataProvider(hotelListDataProvider);
    }

    private void hotelFiler() {
        hotelListDataProvider.setFilter((SerializablePredicate<Hotel>) h -> {
            boolean name = isPassed(h.getName(), filterByName.getValue());
            boolean address = isPassed(h.getAddress(), filterByAddress.getValue());
            return (name && address);
        });
    }

}