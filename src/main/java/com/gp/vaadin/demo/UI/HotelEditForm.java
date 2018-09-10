package com.gp.vaadin.demo.UI;

import com.gp.vaadin.demo.Entity.Category;
import com.gp.vaadin.demo.Entity.Hotel;
import com.gp.vaadin.demo.Services.CategoryService;
import com.gp.vaadin.demo.Services.HotelService;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.ValueContext;
import com.vaadin.ui.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

import static com.gp.vaadin.demo.Services.StaticFields.ENTER_NUMBER;

public class HotelEditForm extends FormLayout {

    private TextField name = new TextField("Name");
    private TextField address = new TextField("Address");
    private TextField rating = new TextField("Rating");
    private DateField operatesFrom = new DateField("Date");
    private NativeSelect<Category> category = new NativeSelect<>("Category");
    private TextArea description = new TextArea("Description");
    private TextField url = new TextField("URL");
    private PaymentForm paymentForm = new PaymentForm();
    private ViewForHotel ui;
    private HotelService hotelService = HotelService.getInstance();
    private CategoryService categoryService = CategoryService.getInstance();
    private Hotel hotel;
    private Binder<Hotel> binder = new Binder<>(Hotel.class);

    public HotelEditForm(ViewForHotel ui) {
        this.ui = ui;
        setMargin(true);
        setVisible(false);
        Button save = new Button("Save");
        Button close = new Button("Close");
        HorizontalLayout buttons = new HorizontalLayout(save, close);
        buttons.setSpacing(true);

        name.setDescription("Hotel name");
        name.setWidth(100, Unit.PERCENTAGE);
        address.setWidth(100, Unit.PERCENTAGE);
        address.setDescription("Address of Hotel");
        rating.setWidth(100, Unit.PERCENTAGE);
        rating.setDescription("Rating of Hotel");
        paymentForm.setWidth(100, Unit.PERCENTAGE);
        paymentForm.setDescription("Payment method");
        operatesFrom.setWidth(100, Unit.PERCENTAGE);
        operatesFrom.setDescription("Date of Operates");
        category.setWidth(100, Unit.PERCENTAGE);
        category.setDescription("Category of Hotel");
        description.setWidth(100, Unit.PERCENTAGE);
        description.setDescription("Description of Hotel");
        url.setWidth(100, Unit.PERCENTAGE);
        url.setDescription("URL of Hotel");

        addComponents(name, address, rating, operatesFrom, category, paymentForm, description, url, buttons);

        category.setItems(categoryService.getAll());
        prepareFields();

        save.addClickListener(e -> save());
        close.addClickListener(e -> setVisible(false));
    }

    private void prepareFields() {
        binder.forField(name).asRequired("Please enter a name").bind(Hotel::getName, Hotel::setName);
        binder.forField(address).asRequired("Please enter a address").bind(Hotel::getAddress, Hotel::setAddress);
        binder.forField(rating).asRequired("Please enter a rating").withValidator(e -> Integer.parseInt(e) >= 0 && Integer.parseInt(e) <= 5, ENTER_NUMBER).bind(this::getRating, this::setRating);
        binder.forField(operatesFrom).asRequired("Please choose a date").withValidator(HotelEditForm::validateDate).bind(HotelEditForm::getDate, this::setDate);
        binder.forField(category).asRequired("Please choose a category").withNullRepresentation(null).bind(Hotel::getCategory, Hotel::setCategory);
        binder.forField(description).bind(Hotel::getDescription, Hotel::setDescription);
        binder.forField(url).asRequired("Please enter a URL").bind(Hotel::getUrl, Hotel::setUrl);
        binder.forField(paymentForm).asRequired("Please choose a payment method").withValidator(paymentForm.getDefaultValidator()).bind(Hotel::getPayment, Hotel::setPayment);
    }

    public static LocalDate getDate(Hotel hotel) {
        if (hotel.getOperatesFrom() == null) return null;

        return Instant.ofEpochMilli(hotel.getOperatesFrom())
                .atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static ValidationResult validateDate(LocalDate localDate, ValueContext valueContext) {
        if (localDate.isBefore(LocalDate.now())) {
            return ValidationResult.ok();
        }
        return ValidationResult.error("Please enter a date in past time");
    }

    public String getRating(Hotel hotel) {
        if (hotel.getRating() == null) return "";
        return String.valueOf(hotel.getRating());
    }

    public void setRating(Hotel hotel, String s) {
        hotel.setRating(Integer.parseInt(s));
    }

    public void setDate(Hotel hotel, LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        int year = localDate.getYear();
        int month = localDate.getMonthValue() - 1;
        int day = localDate.getDayOfMonth();
        calendar.set(year, month, day, 0, 0);
        hotel.setOperatesFrom(calendar.getTimeInMillis());
    }

    private void save() {
        if (binder.isValid()) {
            try {
                binder.writeBean(hotel);
            } catch (ValidationException e) {
                Notification.show("Unable to save! " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
            Category category = categoryService.get(hotel.getCategory().getId());
            if (category == null) {
                Notification.show("Unable to save! Please review errors and fix them.",
                        Notification.Type.ERROR_MESSAGE);
                return;
            }

            hotelService.save(hotel);
            ui.updateList();
            setVisible(false);
        } else {
            List<ValidationResult> validationResults = binder.validate().getValidationErrors();
            if (!validationResults.isEmpty()) {
                Notification.show(validationResults.get(0).getErrorMessage(), Notification.Type.ERROR_MESSAGE);
            }
        }
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
        binder.readBean(this.hotel);
        category.setItems(categoryService.getAll());
        category.setItemCaptionGenerator((ItemCaptionGenerator<Category>) Category::getName);
        binder.readBean(this.hotel);
        setVisible(true);
    }
}