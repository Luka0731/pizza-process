package ch.frox.pizzaprocess.main.java.domain.pizza;

import lombok.Getter;

@Getter
public enum PizzaSize {
  MEDIUM("Medium", 24),
  LARGE("Large", 36),
  EXTRA_LARGE("Extra Large", 50);

  private final String label;
  private final int diameterCm;

  PizzaSize(String label, int diameterCm) {
    this.label = label;
    this.diameterCm = diameterCm;
  }
}