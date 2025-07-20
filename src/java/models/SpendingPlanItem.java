// New model class: SpendingPlanItem.java
package models;

import java.math.BigDecimal;

public class SpendingPlanItem {
    private int itemID;
    private int planID;
    private String category;
    private BigDecimal plannedAmount;
    private BigDecimal actualAmount;
    private String description;

    // Constructors
    public SpendingPlanItem() {}

    // Getters and Setters
    public int getItemID() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getPlanID() {
        return planID;
    }

    public void setPlanID(int planID) {
        this.planID = planID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPlannedAmount() {
        return plannedAmount;
    }

    public void setPlannedAmount(BigDecimal plannedAmount) {
        this.plannedAmount = plannedAmount;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SpendingPlanItem{" +
                "itemID=" + itemID +
                ", planID=" + planID +
                ", category='" + category + '\'' +
                ", plannedAmount=" + plannedAmount +
                ", actualAmount=" + actualAmount +
                ", description='" + description + '\'' +
                '}';
    }
}