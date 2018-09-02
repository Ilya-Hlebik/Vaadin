package com.gp.vaadin.demo.UI;

import com.gp.vaadin.demo.Entity.Category;
import com.gp.vaadin.demo.Entity.Hotel;
import com.gp.vaadin.demo.Services.CategoryService;
import com.vaadin.data.ValidationResult;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Set;

import static com.gp.vaadin.demo.Services.StaticFields.*;

public class ViewForPopUp extends PopupView {

    private NativeSelect<String> nativeSelect = new NativeSelect<>();
    private TextField field = new TextField();
    private NativeSelect<Category> categoryNativeSelect = new NativeSelect<>();
    private DateField dateField = new DateField();
    private ViewForHotel viewForHotel;
    private Set<Hotel> hotels;


    public ViewForPopUp(ViewForHotel hotelUI) {
        viewForHotel = hotelUI;
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(true);
        Button update = new Button("Update");
        update.setWidth(100, Unit.PERCENTAGE);
        Button cancel = new Button("Cancel");
        cancel.setWidth(100, Unit.PERCENTAGE);
        nativeSelect.setEmptySelectionAllowed(true);
        nativeSelect.setEmptySelectionCaption(PLEASE_SELECT_FIELD);
        nativeSelect.setItems(NAME, ADDRESS, RATING, OPERATES_FROM, CATEGORY, DESCRIPTION, URL);
        field.setPlaceholder("Field value");
        categoryNativeSelect.setEmptySelectionAllowed(false);
        categoryNativeSelect.setVisible(false);
        dateField.setVisible(false);
        nativeSelect.setWidth(100, Unit.PERCENTAGE);
        categoryNativeSelect.setWidth(100, Unit.PERCENTAGE);
        update.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        horizontalLayout.addComponents(update, cancel);
        Label label = new Label(BULK_UPDATE, ContentMode.HTML);
        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(label, nativeSelect, field, categoryNativeSelect, dateField, horizontalLayout);
        setContent(createContent(null, layout));
        setHideOnMouseOut(false);

        nativeSelect.addSelectionListener((SingleSelectionListener<String>) singleSelectionEvent -> {
            boolean isPresent = singleSelectionEvent.getSelectedItem().isPresent();
            if (isPresent) {
                String itemName = singleSelectionEvent.getSelectedItem().get();
                if (CATEGORY.equals(itemName)) {
                    field.setVisible(false);
                    dateField.setVisible(false);
                    categoryNativeSelect.setVisible(true);

                } else if (OPERATES_FROM.equals(itemName)) {
                    field.setVisible(false);
                    categoryNativeSelect.setVisible(false);
                    dateField.setValue(null);
                    dateField.setVisible(true);

                } else {
                    field.setValue("");
                    field.setVisible(true);
                    dateField.setVisible(false);
                    categoryNativeSelect.setVisible(false);
                }
            }
        });
        cancel.addClickListener(e -> setPopupVisible(false));
        update.addClickListener(e -> update());
    }


    public void showPopUp(Set<Hotel> hotelList) {
        hotels = hotelList;
        nativeSelect.setSelectedItem(PLEASE_SELECT_FIELD);
        field.setValue("");
        dateField.setValue(null);
        categoryNativeSelect.setItems(CategoryService.getInstance().getAll());
        setPopupVisible(true);
    }

    private void update() {
        boolean isPresent = nativeSelect.getSelectedItem().isPresent();
        if (isPresent) {
            String fieldName = nativeSelect.getSelectedItem().get();
            if (PLEASE_SELECT_FIELD.equals(fieldName)) {
                throwError(PLEASE_SELECT_FIELD_FIRST);

            } else if (CATEGORY.equals(fieldName)) {
                if (categoryNativeSelect.getSelectedItem().isPresent()) {
                    Category category = categoryNativeSelect.getSelectedItem().get();
                    apply(fieldName.toLowerCase(), category);
                } else {
                    throwError("Please, choose a category");
                }

            } else if (OPERATES_FROM.equals(fieldName)) {
                LocalDate date = dateField.getValue();
                if (date == null) {
                    throwError("Please, select a date");
                    return;
                }
                ValidationResult operatesValidation = HotelEditForm.validateDate(date, null);
                boolean operatesIsValid = isValid(operatesValidation);
                if (operatesIsValid) {
                    long timeStamp = ViewForPopUp.getTime(dateField.getValue());
                    apply(OPERATES_FIELD_NAME, timeStamp);
                }

            } else if (RATING.equals(fieldName)) {
                ValidationResult ratingValidation = ViewForPopUp.validateRating(field.getValue());
                boolean ratingIsValid = isValid(ratingValidation);
                if (ratingIsValid) apply(fieldName.toLowerCase(), Integer.parseInt(field.getValue()));

            } else {
                if (!fieldName.equals(DESCRIPTION)) {
                    if (field.getValue() == null || field.getValue().equals("")) {
                        throwError("Description can't be empty");
                        return;
                    }
                }
                apply(fieldName.toLowerCase(), field.getValue());
            }
        } else {
            throwError(PLEASE_SELECT_FIELD_FIRST);
        }
    }

    private void apply(String name, Object hotelValue) {
        Class hotelClass = Hotel.class;
        hotels.forEach(hotel -> {
            try {
                Field field = hotelClass.getDeclaredField(name);
                field.setAccessible(true);
                field.set(hotel, hotelValue);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            setPopupVisible(false);
            viewForHotel.onUpdateHotels(hotels);
        });
    }

    private boolean isValid(ValidationResult validationResult) {
        if (validationResult.isError()) {
            throwError(validationResult.getErrorMessage());
            return false;
        }
        return true;
    }


    private static long getTime(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        int year = localDate.getYear();
        int month = localDate.getMonthValue() - 1;
        int day = localDate.getDayOfMonth();
        calendar.set(year, month, day, 0, 0);
        return calendar.getTimeInMillis();
    }

    private void throwError(String message) {
        Notification.show(message, Notification.Type.ERROR_MESSAGE);
    }

    private static ValidationResult validateRating(String s) {
        try {
            int rating = Integer.parseInt(s);

            if (rating > -1 && rating < 6) {
                return ValidationResult.ok();
            }
            return ValidationResult.error(ENTER_NUMBER);
        } catch (Exception e) {
            return ValidationResult.error("Number should be a integer number between 0 and 5");
        }
    }
}