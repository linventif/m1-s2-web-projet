package utc.miage.tp.workout.statistique;

public class MonthlyBarView {
  private final String label;
  private final double value;
  private final int height;
  private final boolean currentMonth;

  public MonthlyBarView(String label, double value, int height, boolean currentMonth) {
    this.label = label;
    this.value = value;
    this.height = height;
    this.currentMonth = currentMonth;
  }

  public String getLabel() {
    return label;
  }

  public double getValue() {
    return value;
  }

  public int getHeight() {
    return height;
  }

  public boolean isCurrentMonth() {
    return currentMonth;
  }
}
