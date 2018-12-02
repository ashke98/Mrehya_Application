package com.mrehya.Shopping;

import java.io.Serializable;

/**
 * Created by sdfsdfasf on 2/22/2018.
 */

public class Product implements Serializable{
    int id, categoryId , priority ,sale_price  ,sale  ,stock ,visit;
    String title , image, image1, image2,image3 ,code ,slug ,language, short_description ,description ,price, previewimage  ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getSale_price() {
        return sale_price;
    }

    public void setSale_price(int sale_price) {
        this.sale_price = sale_price;
    }

    public int getSale() {
        return sale;
    }

    public void setSale(int sale) {
        this.sale = sale;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getpreviewimage() {
        return previewimage;
    }

    public void setpreviewimage(String previewimage) {
        this.previewimage = previewimage;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getShort_description() {
        return short_description;
    }

    public void setShort_description(String short_description) {
        this.short_description = short_description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Product(int id, String price, int sale_price, int sale, String title, String image, String short_description) {
        this.id = id;
        this.price = price;
        this.sale_price = sale_price;
        this.sale = sale;
        this.title = title;
        this.image = image;
        this.short_description = short_description;
    }
    public Product(int id, String price, String title, String image, String previewimage, String short_description,int stock) {
        this.id = id;
        this.price = price;
        this.title = title;
        this.image = image;
        this.previewimage= previewimage;
        this.short_description= short_description;
        this.stock = stock;

    }

    public Product(String price, int sale_price, int sale, String title, String image, String short_description) {
        this.price = price;
        this.sale_price = sale_price;
        this.sale = sale;
        this.title = title;
        this.image = image;
        this.short_description = short_description;
    }

    public Product(String price, int sale_price, int sale, int stock, int visit, String title, String image, String image1, String image2, String image3, String code, String language, String description) {

        this.price = price;
        this.sale_price = sale_price;
        this.sale = sale;
        this.stock = stock;
        this.visit = visit;
        this.title = title;
        this.image = image;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.code = code;
        this.language = language;
        this.description = description;
    }
}
