package com.gp.vaadin.demo;

import com.gp.vaadin.demo.Entity.Category;
import com.gp.vaadin.demo.Services.CategoryService;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.ui.*;

public class HotelCategoryEditForm extends FormLayout {
    private Binder<Category> binder = new Binder<>(Category.class);
    private Button save = new Button("Save");
    private Button close = new Button("Close");
    private TextField name = new TextField("Category");
    private ViewForCategory viewForCategory;
    private Category category;
    private HorizontalLayout buttons = new HorizontalLayout(save, close);
    private CategoryService categoryService = CategoryService.getInstance();


    public HotelCategoryEditForm(ViewForCategory ui) {
        viewForCategory = ui;

        addComponents(name, buttons);
        setMargin(true);
        setVisible(false);

        name.setDescription("Category of Hotel");
        binder.forField(name).asRequired("Need name").bind(Category::getName, Category::setName);
        save.addClickListener(e -> save());
        close.addClickListener(e -> close());
    }

    private void close() {
        category = new Category();
        setVisible(false);
    }

    private void save() {
        if (binder.isValid()) {
            try {
                binder.writeBean(category);
            } catch (ValidationException e) {
                Notification.show("Unable to save! " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            }
            categoryService.save(category);
            setVisible(false);
            viewForCategory.updateList();
        } else {
            Notification.show("Please Enter a name of category  " , Notification.Type.ERROR_MESSAGE);
        }
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        binder.readBean(this.category);
        setVisible(true);
    }
}