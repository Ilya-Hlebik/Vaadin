package com.gp.vaadin.demo.Entity;

import org.hibernate.annotations.NotFound;

import javax.persistence.*;

@Entity
@Table(name = "HOTEL")
@NamedQueries({
        @NamedQuery(name = "Hotel.getAll", query = "SELECT c from Hotel c"),
        @NamedQuery(name = "Hotel.filter", query = "SELECT e FROM Hotel AS e WHERE LOWER(e.name) LIKE :filterByName AND LOWER(e.address) LIKE :filterByAddress")
})
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Version
    @Column(name = "OPTLOCK", columnDefinition = "integer DEFAULT 0", nullable = false)
    private Integer version;

    @Column
    private String name;

    @Column
    private String address;

    @Column
    private Integer rating;

    @Column(name = "OPERATES_FROM")
    private Long operatesFrom;

    @NotFound
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID",
            referencedColumnName = "ID",
            foreignKey = @ForeignKey(name = "FK_HOTEL_CATEGORY"))
    private Category category;

    @Embedded
    private Payment payment;

    @Column
    private String description;

    @Column
    private String url;

    public Hotel() {
    }

    public Hotel(String name, String address, Integer rating, String description, String url) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.description = description;
        this.url = url;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Long getOperatesFrom() {
        return operatesFrom;
    }

    public void setOperatesFrom(Long operatesFrom) {
        this.operatesFrom = operatesFrom;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hotel hotel = (Hotel) o;

        if (id != null ? !id.equals(hotel.id) : hotel.id != null) return false;
        if (version != null ? !version.equals(hotel.version) : hotel.version != null) return false;
        if (name != null ? !name.equals(hotel.name) : hotel.name != null) return false;
        if (address != null ? !address.equals(hotel.address) : hotel.address != null) return false;
        if (rating != null ? !rating.equals(hotel.rating) : hotel.rating != null) return false;
        if (operatesFrom != null ? !operatesFrom.equals(hotel.operatesFrom) : hotel.operatesFrom != null) return false;
        if (category != null ? !category.equals(hotel.category) : hotel.category != null) return false;
        if (description != null ? !description.equals(hotel.description) : hotel.description != null) return false;
        return url != null ? url.equals(hotel.url) : hotel.url == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (rating != null ? rating.hashCode() : 0);
        result = 31 * result + (operatesFrom != null ? operatesFrom.hashCode() : 0);
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}