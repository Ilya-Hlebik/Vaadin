package com.gp.vaadin.demo.UI;


import com.gp.vaadin.demo.Entity.Hotel;
import com.gp.vaadin.demo.Services.CategoryService;
import com.gp.vaadin.demo.Services.HotelService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;

import java.util.List;
import java.util.Set;

public class ViewForHotel extends VerticalLayout implements View {

    final HotelService hotelService = HotelService.getInstance();
    final TextField filterByName = new TextField();
    final TextField filterByAddress = new TextField();
    final Button addHotel = new Button("Add hotel");
    final Button deleteHotel = new Button("Delete hotel");
    final Button editHotel = new Button("Edit hotel");

    final Grid<Hotel> hotelGrid = new Grid<>();
    final HotelEditForm hotelEditForm = new HotelEditForm(this);
    final CategoryService categoryService = CategoryService.getInstance();


    public ViewForHotel() {

        filterByName.setPlaceholder("filter by name");
        filterByName.setValueChangeMode(ValueChangeMode.LAZY);
        filterByAddress.setPlaceholder("filter by address");
        filterByAddress.setValueChangeMode(ValueChangeMode.LAZY);
        filterByName.addValueChangeListener(e -> updateList());
        filterByAddress.addValueChangeListener(e -> updateList());

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

        hotelGrid.addColumn(Hotel::getName).setCaption("Name");
        hotelGrid.addColumn(Hotel::getAddress).setCaption("Address");
        hotelGrid.addColumn(Hotel::getRating).setCaption("Rating");
        hotelGrid.addColumn(this::getCategory).setCaption("Category");
        hotelGrid.addColumn(HotelEditForm::getDate).setCaption("Operates From");
        hotelGrid.addColumn(Hotel::getDescription).setCaption("Description");
        Grid.Column<Hotel, String> htmlColumn = hotelGrid.addColumn(hotel ->
                        "<a href='" + hotel.getUrl() + "' target='_target'>hotel info</a>",
                new HtmlRenderer()).setCaption("Url");
        hotelGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        hotelGrid.addSelectionListener(e -> {

            Set<Hotel> selectedHotels = e.getAllSelectedItems();
            if (selectedHotels != null && selectedHotels.size() == 1) {
                deleteHotel.setEnabled(true);
                editHotel.setEnabled(true);
            } else if (selectedHotels != null && selectedHotels.size() > 1) {
                deleteHotel.setEnabled(true);
                editHotel.setEnabled(false);
            } else {
                deleteHotel.setEnabled(false);
                editHotel.setEnabled(false);
                hotelEditForm.setVisible(false);
            }
        });

        HorizontalLayout control = new HorizontalLayout(filterByName, filterByAddress, addHotel, deleteHotel, editHotel);
        HorizontalLayout content = new HorizontalLayout(hotelGrid, hotelEditForm);

        deleteHotel.setEnabled(false);
        editHotel.setEnabled(false);

        setSpacing(true);
        setMargin(false);
        setWidth("100%");
        hotelGrid.setSizeFull();
        hotelEditForm.setSizeFull();
        content.setMargin(false);
        content.setWidth("100%");
        content.setHeight(32, Unit.REM);
        content.setExpandRatio(hotelGrid, 229);
        content.setExpandRatio(hotelEditForm, 92);
        addComponents(control, content);


        updateList();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updateList();
    }


    private String getCategory(Hotel hotel) {
        return categoryService.getCategoryName(hotel.getCategory());
    }

    public void updateList() {
        List<Hotel> hotelList = hotelService.getAll(filterByName.getValue(), filterByAddress.getValue());
        hotelGrid.setItems(hotelList);
    }

}