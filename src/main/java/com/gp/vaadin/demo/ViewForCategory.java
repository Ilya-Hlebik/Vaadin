package com.gp.vaadin.demo;

import com.gp.vaadin.demo.Entity.Category;
import com.gp.vaadin.demo.Services.CategoryService;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import java.util.List;
import java.util.Set;


public class ViewForCategory extends VerticalLayout implements View {

    private HotelCategoryEditForm categoryEditForm = new HotelCategoryEditForm(this);
    final Button addCategory = new Button("Add category");
    final Button deleteCategory = new Button("Delete category");
    final Button editCategory = new Button("Edit category");
    final CategoryService categoryService = CategoryService.getInstance();
    final Grid<Category> listOfCategory = new Grid<>();

    public ViewForCategory(){
        addCategory.addClickListener(e -> {
            listOfCategory.deselectAll();
            categoryEditForm.setCategory(new Category());
        });

        deleteCategory.addClickListener(e -> {
            Set<Category> selectedItems = listOfCategory.asMultiSelect().getSelectedItems();
            if (selectedItems.isEmpty()) return;
            categoryService.delete(selectedItems);
            updateList();
        });

        editCategory.addClickListener(e -> {
            Set<Category> selectedItems = listOfCategory.asMultiSelect().getSelectedItems();
            if (selectedItems.isEmpty()) return;
            Category category = selectedItems.iterator().next();
            categoryEditForm.setCategory(category);
        });

        listOfCategory.addColumn(Category::getName).setCaption("Name");
        listOfCategory.setSelectionMode(Grid.SelectionMode.MULTI);

        listOfCategory.addSelectionListener(e -> {
            Set<Category> selectedCategories = e.getAllSelectedItems();
            if (selectedCategories != null && selectedCategories.size() == 1) {
                deleteCategory.setEnabled(true);
                editCategory.setEnabled(true);
            } else if (selectedCategories != null && selectedCategories.size() > 1) {
                deleteCategory.setEnabled(true);
                editCategory.setEnabled(false);
            } else {
                deleteCategory.setEnabled(false);
                editCategory.setEnabled(false);
                categoryEditForm.setVisible(false);
            }
        });
        setSpacing(true);
        setMargin(false);
        setWidth("100%");

       HorizontalLayout control = new HorizontalLayout(addCategory, deleteCategory, editCategory);
       HorizontalLayout content = new HorizontalLayout(listOfCategory, categoryEditForm);

        listOfCategory.setSizeFull();
        categoryEditForm.setVisible(false);
        categoryEditForm.setSizeFull();
        content.setMargin(false);
        content.setWidth("100%");
        deleteCategory.setEnabled(false);
        editCategory.setEnabled(false);
        addComponents(control, content);

        updateList();
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        updateList();
    }

    public void updateList() {
        List<Category> categorySet = categoryService.getAll();
        listOfCategory.setItems(categorySet);
    }
}