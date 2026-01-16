package de.ruu.jasper.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Invoice Data Model - wird als JSON an JasperReports Service gesendet
 */
public class InvoiceData {

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private LocalDate dueDate;
    private String customerName;
    private String customerAddress;
    private List<InvoiceItem> items = new ArrayList<>();
    private String notes;

    // Getter/Setter

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Convenience Methods

    public InvoiceData addItem(String description, int quantity, double unitPrice) {
        items.add(new InvoiceItem(description, quantity, unitPrice));
        return this;
    }

    public double getSubtotal() {
        return items.stream()
            .mapToDouble(InvoiceItem::getTotal)
            .sum();
    }

    public double getTax() {
        return getSubtotal() * 0.19; // 19% MwSt
    }

    public double getTotal() {
        return getSubtotal() + getTax();
    }

    // Builder Pattern

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final InvoiceData invoice = new InvoiceData();

        public Builder invoiceNumber(String invoiceNumber) {
            invoice.setInvoiceNumber(invoiceNumber);
            return this;
        }

        public Builder invoiceDate(LocalDate date) {
            invoice.setInvoiceDate(date);
            return this;
        }

        public Builder dueDate(LocalDate date) {
            invoice.setDueDate(date);
            return this;
        }

        public Builder customer(String name, String address) {
            invoice.setCustomerName(name);
            invoice.setCustomerAddress(address);
            return this;
        }

        public Builder addItem(String description, int quantity, double unitPrice) {
            invoice.addItem(description, quantity, unitPrice);
            return this;
        }

        public Builder notes(String notes) {
            invoice.setNotes(notes);
            return this;
        }

        public InvoiceData build() {
            return invoice;
        }
    }

    // Nested Class: Invoice Item

    public static class InvoiceItem {
        private String description;
        private int quantity;
        private double unitPrice;

        public InvoiceItem() {}

        public InvoiceItem(String description, int quantity, double unitPrice) {
            this.description = description;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

        public double getTotal() {
            return quantity * unitPrice;
        }
    }
}

