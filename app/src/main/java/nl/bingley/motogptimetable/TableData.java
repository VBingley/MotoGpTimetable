package nl.bingley.motogptimetable;

import java.util.ArrayList;
import java.util.Collection;

import nl.bingley.motogptimetable.model.RiderDetails;
import nl.bingley.motogptimetable.model.livetiming.Category;
import nl.bingley.motogptimetable.model.livetiming.ColumnType;
import nl.bingley.motogptimetable.model.livetiming.Rider;

public class TableData {

    private Category category = new Category();
    private Collection<Rider> riders = new ArrayList<>();
    private Collection<RiderDetails> riderDetailsList = new ArrayList<>();

    private ColumnType columnNumber = ColumnType.Number;
    private ColumnType columnName = ColumnType.ShortName;
    private ColumnType columnLapTime = ColumnType.BestLapTime;
    private ColumnType columnGap = ColumnType.LeadGap;

    private String error = null;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Collection<Rider> getRiders() {
        return riders;
    }

    public void setRiders(Collection<Rider> riders) {
        this.riders = riders;
    }

    public Collection<RiderDetails> getRiderDetailsList() {
        return riderDetailsList;
    }

    public void setRiderDetailsList(Collection<RiderDetails> riderDetailsList) {
        this.riderDetailsList = riderDetailsList;
    }

    public ColumnType getColumnName() {
        return columnName;
    }

    public ColumnType getColumnLapTime() {
        return columnLapTime;
    }

    public void setColumnLapTime(ColumnType columnLapTime) {
        this.columnLapTime = columnLapTime;
    }

    public ColumnType getColumnGap() {
        return columnGap;
    }

    public void setColumnGap(ColumnType columnGap) {
        this.columnGap = columnGap;
    }

    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public boolean isColumnNumberTypeNumber() {
        return columnNumber == ColumnType.Number;
    }

    public boolean isColumnNameTypeLong() {
        return columnName == ColumnType.LongName;
    }

    public boolean isColumnLapTimeTypeBest() {
        return columnLapTime == ColumnType.BestLapTime;
    }

    public boolean isColumnGapTypeLead() {
        return columnGap == ColumnType.LeadGap;
    }

    public void toggleColumnNumber() {
        if (isColumnNumberTypeNumber()) {
            columnNumber = ColumnType.Constructor;
        } else {
            columnNumber = ColumnType.Number;
        }
    }

    public void toggleColumnName() {
        if (isColumnNameTypeLong()) {
            columnName = ColumnType.ShortName;
        } else {
            columnName = ColumnType.LongName;
        }
    }

    public void toggleColumnLapTime() {
        if (columnLapTime == ColumnType.BestLapTime) {
            columnLapTime = ColumnType.LastLapTime;
        } else {
            columnLapTime = ColumnType.BestLapTime;
        }
    }

    public void toggleColumnGap() {
        if (columnGap == ColumnType.LeadGap) {
            columnGap = ColumnType.NextGap;
        } else {
            columnGap = ColumnType.LeadGap;
        }
    }
}
